/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dao.IProjectDao;
import fr.asipsante.platines.dao.ITestSessionDao;
import fr.asipsante.platines.dto.SessionParameters;
import fr.asipsante.platines.dto.TestSessionDto;
import fr.asipsante.platines.entity.JobStatus;
import fr.asipsante.platines.entity.TestSession;
import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.entity.enums.SessionStatus;
import fr.asipsante.platines.exception.JobException;
import fr.asipsante.platines.exception.ServiceException;
import fr.asipsante.platines.exception.ServiceNomadException;
import fr.asipsante.platines.executor.ProjectBuilder;
import fr.asipsante.platines.mail.entity.Mail;
import fr.asipsante.platines.service.JobManager;
import fr.asipsante.platines.service.MailService;
import fr.asipsante.platines.service.PreferencesService;
import fr.asipsante.platines.service.SessionBuilder;
import fr.asipsante.platines.service.SessionRunner;
import fr.asipsante.platines.service.impl.listeners.MockSessionListener;
import fr.asipsante.platines.service.impl.listeners.RunnerSessionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

/**
 * The session controller.
 *
 * @author apierre
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SessionRunnerImpl implements SessionRunner {
  /** The Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(SessionRunnerImpl.class);

  /** The mock domain */
  @Value("${mockservices.domain}")
  private String MOCK_SERVICE_DOMAIN;

  /** The subject of mail when server's session start. */
  private static final String SUBJECT_MAIL_START = "Platines: Email d’ouverture du mockservice";

  /** The subject of mail when session cancelled. */
  private static final String SUBJECT_MAIL_CANCELLED = "Platines: Email d'annulation de session";

  /** The variable to convert seconds to minutes. */
  private static final long CONVERT_MIN = 60;

  /** The subject of mail when server's session is in error. */
  private static final String SUBJECT_MAIL_ERROR = "Incident Mock-service";

  /** The First name of mail recipient. */
  private static final String FIRSTNAME = "firstName";

  /** ProjectDao. */
  @Autowired
  @Qualifier("projectDao")
  private IProjectDao projectDao;

  /** TestSessionDao. */
  @Autowired
  @Qualifier("testSessionDao")
  private ITestSessionDao sessionDao;

  /** ServicePreference. */
  @Autowired private PreferencesService preferenceService;

  /** MailService. */
  @Autowired private MailService mailService;

  /** SessionStateManager. */
  @Autowired private SessionStateManager sessionStateManager;

  @Autowired private ProjectBuilder projectBuilder;

  /** TestSessionDto. */
  private TestSessionDto sessionTst;

  /** SessionEnvironment. */
  private SessionParameters sessionParameters;

  /**
   * prépare le zip et les differents fichiers nécessaires pour l'éxécution.
   *
   * @return les paramètres de session
   */
  @Override
  public SessionParameters init() {
    sessionStateManager.updateSessionTestStatus(sessionTst.getUuid(), SessionStatus.CONSTRUCT);
    // send a websocket
    sessionStateManager.updateAndPublishSessionStatus(sessionTst.getId(), SessionStatus.CONSTRUCT);
    // soapui manager
    SessionBuilder sessionBuilder = getPrototypeBeanSessionBuilder();
    try {
      if ((Role.SERVER).equals(sessionTst.getApplication().getRole())) {
        sessionParameters = sessionBuilder.createClientSession(sessionTst);
      } else {
        sessionParameters = sessionBuilder.createServeurSession(sessionTst);
      }
      if (sessionParameters == null) {
        sessionStateManager.updateSessionTestStatus(sessionTst.getUuid(), SessionStatus.ERROR);
        sessionStateManager.updateAndPublishSessionStatus(sessionTst.getId(), SessionStatus.ERROR);
      }
    } catch (final ServiceException e) {
      sessionStateManager.updateSessionTestStatus(sessionTst.getUuid(), SessionStatus.ERROR);
      sessionStateManager.updateAndPublishSessionStatus(sessionTst.getId(), SessionStatus.ERROR);
    }

    return sessionParameters;
  }

  /** lance l'exécution. */
  @Override
  public void launch() {
    sessionStateManager.updateSessionTestStatus(sessionTst.getUuid(), SessionStatus.DEPLOYMENT);
    sessionStateManager.updateAndPublishSessionStatus(sessionTst.getId(), SessionStatus.DEPLOYMENT);
    final JobManager jobManager = getPrototypeJobManager();
    sessionParameters.setUuidSession(sessionTst.getUuid());
    try {
      JobStatus jobStatus;
      if ((Role.CLIENT).equals(sessionTst.getApplication().getRole())) {
        // Session serveur
        sessionParameters.setMTls(sessionTst.getApplication().getChainOfTrustDto() != null);
        sessionParameters.setAuthorizedIP(sessionTst.getApplication().getUrl());
        jobManager.submitJob(sessionParameters);
        jobStatus = jobManager.getJobStatus();
        if (jobStatus.equals(JobStatus.RUNNING)) {
          sessionStateManager.updateSessionTestExecutionDate(
              sessionTst.getUuid(), SessionStatus.PENDING);
          sessionStateManager.updateAndPublishSessionStatus(
              sessionTst.getId(), SessionStatus.PENDING);
          String urlMock =
              "https://" + sessionTst.getUuid() + "." + MOCK_SERVICE_DOMAIN + "/mockservice";
          Mail mail =
              buildMail(
                  "/velocity/mail/mock_provisionning.vm",
                  getMailVariables(urlMock),
                  SUBJECT_MAIL_START,
                  sessionTst.getApplication().getUser().getMail());
          mailService.sendEmail(mail);
          MockSessionListener mockListener = getPrototypeBeanMockListener();
          mockListener.setSession(sessionTst);
          mockListener.setUrl(urlMock);
          mockListener.setJobManager(jobManager);
          mockListener.start();

        } else if (JobStatus.CANCELLED.equals(jobStatus)
            || JobStatus.PENDING.equals(jobStatus)
            || JobStatus.QUEUED.equals(jobStatus)) {
          sendEmails();
          jobManager.stopJob();
        } else {
          sendErrorMail();
          sessionStateManager.updateSessionTestStatus(sessionTst.getUuid(), SessionStatus.ERROR);
          sessionStateManager.updateAndPublishSessionStatus(
              sessionTst.getId(), SessionStatus.ERROR);
          jobManager.stopJob();
        }
      } else {
        jobManager.submitJob(sessionParameters);
        jobStatus = jobManager.getJobStatus();
        if (jobStatus.equals(JobStatus.RUNNING)) {
          if (SessionStatus.DEPLOYMENT.equals(
              sessionDao.getSessionByUuid(sessionTst.getUuid()).getSessionStatus())) {
            sessionStateManager.updateSessionTestExecutionDate(
                sessionTst.getUuid(), SessionStatus.STARTING);
            sessionStateManager.updateAndPublishSessionStatus(
                sessionTst.getId(), SessionStatus.STARTING);
          }
          final RunnerSessionListener runnerSessionListener =
              getPrototypeBeanRunnerSessionListener();
          runnerSessionListener.setSessionDto(sessionTst);
          runnerSessionListener.setJobManager(jobManager);
          runnerSessionListener.start();
        } else if (JobStatus.CANCELLED.equals(jobStatus)
            || JobStatus.PENDING.equals(jobStatus)
            || JobStatus.QUEUED.equals(jobStatus)) {
          sessionStateManager.updateSessionTestStatus(sessionTst.getUuid(), SessionStatus.CANCELED);
          sessionStateManager.updateAndPublishSessionStatus(
              sessionTst.getId(), SessionStatus.CANCELED);
        } else {
          sessionStateManager.updateSessionTestExecutionDate(
              sessionTst.getUuid(), SessionStatus.ERROR);
          sessionStateManager.updateAndPublishSessionStatus(
              sessionTst.getId(), SessionStatus.ERROR);
        }
      }
      deleteArtifact();

    } catch (ServiceException | ServiceNomadException | JobException e) {
      sessionStateManager.updateSessionTestStatus(sessionTst.getUuid(), SessionStatus.ERROR);
      sessionStateManager.updateAndPublishSessionStatus(sessionTst.getId(), SessionStatus.ERROR);
      sendErrorMail();
      LOGGER.error("Erreur lors du déploiement de la session {}", sessionTst.getUuid(), e);
      deleteArtifact();
    }
  }

  @Override
  @Transactional
  public void stop(Long idSession) {
    LOGGER.debug("Arret de la session : {}", idSession);
    final JobManager jobManager = getPrototypeJobManager();
    final TestSession session = sessionDao.getById(idSession);
    session.setSessionStatus(SessionStatus.CANCELED);
    final Map<String, Object> model = new HashMap<>();
    model.put("firstName", HtmlUtils.htmlEscape(session.getApplication().getUser().getForename()));
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss z");
    model.put("creationDate", HtmlUtils.htmlEscape(dateFormat.format(session.getCreationDate())));
    sessionDao.update(session);
    //        sendMail("/velocity/mail/managed_session_user.vm", model, SUBJECT_MAIL_CANCELLED,
    //                session.getApplication().getUser().getMail());
    jobManager.stopJob(session.getUuid());
  }

  /**
   * @param session the Test Session
   */
  public void setSessionTst(TestSessionDto session) {
    this.sessionTst = session;
  }

  /**
   * @return the clientSession
   */
  public SessionParameters getClientSessionParameters() {
    return sessionParameters;
  }

  /**
   * @param sessionParameters the clientSession to set
   */
  public void setClientSessionParameters(SessionParameters sessionParameters) {
    this.sessionParameters = sessionParameters;
  }

  /**
   * Méthode override par spring.
   *
   * @return waiting thread
   */
  @Lookup
  public MockSessionListener getPrototypeBeanMockListener() {
    // spring will override this method
    return null;
  }

  /**
   * Méthode override par spring.
   *
   * @return WaitingThreadClient
   */
  @Lookup
  public RunnerSessionListener getPrototypeBeanRunnerSessionListener() {
    // spring will override this method
    return null;
  }

  /**
   * Méthode override par spring.
   *
   * @return soapuiPackagerManager
   */
  @Lookup
  public SessionBuilder getPrototypeBeanSessionBuilder() {
    // spring will override this method
    return null;
  }

  /**
   * Méthode override par spring.
   *
   * @return JobManager
   */
  @Lookup
  public JobManager getPrototypeJobManager() {
    // spring will override this method
    return null;
  }

  private void sendEmails() {
    final Map<String, Object> variables = new HashMap<>();
    variables.put(
        FIRSTNAME, HtmlUtils.htmlEscape(sessionTst.getApplication().getUser().getForename()));
    variables.put(
        "userCompleteName",
        HtmlUtils.htmlEscape(
            sessionTst.getApplication().getUser().getForename()
                + " "
                + sessionTst.getApplication().getUser().getName()));
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy à HH:mm:ss z");
    variables.put(
        "creationDate", HtmlUtils.htmlEscape(dateFormat.format(sessionTst.getCreationDate())));
    variables.put("userEmailAdresse", sessionTst.getApplication().getUser().getMail());
    String provisionningTimeout =
        preferenceService.getPreferences().getProperty("provisionning_timeout");
    if (provisionningTimeout == null) {
      provisionningTimeout = "60";
    }
    variables.put("provisionning_timeout", provisionningTimeout);
    final String recipientsList =
        preferenceService.getPreferences().getProperty("list_email_notification");
    if (recipientsList != null) {
      final String[] recipients = recipientsList.split(";");
      for (final String rcpt : recipients) {
        Mail mail =
            buildMail(
                "/velocity/mail/cancelled_session_admin.vm",
                variables,
                SUBJECT_MAIL_CANCELLED,
                rcpt);
        mailService.sendEmail(mail);
      }
    }
    Mail mail =
        buildMail(
            "/velocity/mail/cancelled_session_user.vm",
            variables,
            SUBJECT_MAIL_CANCELLED,
            sessionTst.getApplication().getUser().getMail());
    mailService.sendEmail(mail);
    sessionStateManager.updateSessionTestStatus(sessionTst.getUuid(), SessionStatus.CANCELED);
    sessionStateManager.updateAndPublishSessionStatus(sessionTst.getId(), SessionStatus.CANCELED);
  }

  private void deleteArtifact() {
    File deleteFile = new File(sessionParameters.getFileArtifact());
    boolean fileDelete = deleteFile(deleteFile);
    if (!fileDelete) {
      LOGGER.error("Erreur pour supprimer le fichier {}", sessionParameters.getFileArtifact());
    }
    if (sessionParameters.getKeystoreFile() != null) {
      deleteFile = new File(sessionParameters.getKeystoreFile());
      fileDelete = deleteFile(deleteFile);
      if (!fileDelete) {
        LOGGER.error("Erreur pour supprimer le fichier {}", sessionParameters.getKeystoreFile());
      }
    }
  }

  private boolean deleteFile(File file) {
    boolean fileDelete = false;
    if (file.exists()) {
      try {
        Files.delete(file.toPath());
        fileDelete = true;
      } catch (IOException e) {
        LOGGER.error("Erreur de supression du fichier {}", file.getAbsolutePath(), e);
        fileDelete = false;
      }
    }
    return fileDelete;
  }

  private void sendErrorMail() {
    final String recipientsList =
        preferenceService.getPreferences().getProperty("list_email_notification");
    if (recipientsList != null) {
      final String[] recipients = recipientsList.split(";");
      for (final String rcpt : recipients) {
        final Mail mail = new Mail();
        mail.setMailTo(rcpt);
        mail.setMailSubject(SUBJECT_MAIL_ERROR);
        mail.setTemplate("/velocity/mail/error_session_server.vm");
        final Map<String, Object> variables = new HashMap<>();
        variables.put(
            FIRSTNAME,
            HtmlUtils.htmlEscape(
                sessionTst.getApplication().getUser().getForename()
                    + " "
                    + sessionTst.getApplication().getUser().getName()));
        final LocalDateTime now = LocalDateTime.now();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm:ss");
        final String formatDateTime = now.format(formatter);
        variables.put("date", HtmlUtils.htmlEscape(formatDateTime));
        variables.put("uuidSession", sessionTst.getUuid());
        mail.setVariables(variables);
        mailService.sendEmail(mail);
      }
    }
  }

  private Mail buildMail(
      String velocityTemplate, Map<String, Object> variables, String subject, String rcpt) {
    final Mail mail = new Mail();
    mail.setMailTo(rcpt);
    mail.setMailSubject(subject);
    mail.setTemplate(velocityTemplate);
    mail.setVariables(variables);
    return mail;
  }

  private Map<String, Object> getMailVariables(String urlMock) {

    final long hour = sessionTst.getSessionDuration().getDuration() / CONVERT_MIN;
    String hourText;
    if (hour > 1) {
      hourText = hour + " heures";
    } else {
      hourText = hour + " heure";
    }

    final List<String> contexts =
        projectBuilder.getMockContextPath(
            projectDao.getById(sessionTst.getProjectResults().get(0).getIdProject()).getFile());
    String urlText;
    if (contexts.size() > 1) {
      urlText = "aux urls suivantes :";
    } else {
      urlText = HtmlUtils.htmlEscape("à l'url suivante :");
    }

    final List<String> urls = new ArrayList<>();
    for (String url : contexts) {
      url = urlMock + url;
      urls.add(url);
    }
    final Map<String, Object> model = new HashMap<>();
    model.put(FIRSTNAME, HtmlUtils.htmlEscape(sessionTst.getApplication().getUser().getForename()));
    model.put("hours", hour);
    model.put("urls", urls);
    model.put("hourText", hourText);
    model.put("urlText", urlText);
    model.put("uuid", sessionTst.getUuid());
    return model;
  }

  /**
   * Supprime un dossier.
   *
   * @param folder, le dossier à supprimer
   */
  public void deleteFolder(File folder) {
    final File[] files = folder.listFiles();
    if (files != null) {
      for (final File f : files) {
        if (f.isDirectory()) {
          deleteFolder(f);
        } else {
          try {
            Files.delete(f.toPath());
          } catch (IOException e) {
            LOGGER.error("Erreur de supression du fichier {}", f.getAbsolutePath(), e);
          }
        }
      }
    }
    try {
      Files.delete(folder.toPath());
    } catch (IOException e) {
      LOGGER.error("Erreur de supression du répertoire {}", folder.getAbsolutePath(), e);
    }
  }

  // TODO envoyer un mail en cas d'annulation des session
  private void sendMail(
      String velocityTemplate, Map<String, Object> variables, String subject, String email) {
    final Mail mail = new Mail();
    mail.setMailTo(email);
    mail.setMailSubject(subject);
    mail.setTemplate(velocityTemplate);
    mail.setVariables(variables);
    mailService.sendEmail(mail);
  }
}
