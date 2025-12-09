/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dao.IConnectionHistoryDao;
import fr.asipsante.platines.dao.IGenericDao;
import fr.asipsante.platines.dao.IUserDao;
import fr.asipsante.platines.entity.ConnectionHistory;
import fr.asipsante.platines.entity.Profile;
import fr.asipsante.platines.entity.User;
import fr.asipsante.platines.exception.DatabaseException;
import fr.asipsante.platines.exception.ServiceException;
import fr.asipsante.platines.mail.entity.Mail;
import fr.asipsante.platines.service.ConnectionService;
import fr.asipsante.platines.service.MailService;
import fr.asipsante.platines.service.PreferencesService;
import fr.asipsante.platines.service.authentication.JwtTokenProvider;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

/**
 * @author aboittiaux
 */
@Service(value = "connectionService")
public class ConnectionServiceImpl implements ConnectionService {

  /** LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionServiceImpl.class);

  /** SUBJECT_FORGOT_PASSWORD. */
  private static final String SUBJECT_FORGOT_PASSWORD = "Platines: Mot de passe oublié";

  /** EXPIRATION_TIME_RESET. */
  private static final long EXPIRATION_TIME_RESET = 600_000;

  /** AUTHENT_BLOCKED. */
  private static final long AUTHENT_BLOCKED = 30 * 60000L;

  /** LIMIT_TRY_AUTHENTICATION. */
  private static final Integer LIMIT_TRY_AUTHENTICATION = 5;

  /** BLOCKED_ACCOUNT_DURATION. */
  private static final String BLOCKED_ACCOUNT_DURATION = "blocked_account_duration";

  /** KEY_LIMIT_TRY_AUTHENTICATION. */
  private static final String KEY_LIMIT_TRY_AUTHENTICATION = "limit_try_authentication";

  /** Message for invalid password. */
  private static final String INVALID_PASSWORD = "Compte/mot de passe invalide";

  /** Service des preferences. */
  @Autowired private PreferencesService preferencesService;

  /** IMailService. */
  @Autowired private MailService mailService;

  /** JwtTokenProvider. */
  @Autowired private JwtTokenProvider jwtTokenProvider;

  /** IUserDao. */
  private IUserDao userDao;

  /** IConnectionHistoryDao. */
  private IConnectionHistoryDao connectionHistoryDao;

  /** Contructeur. */
  public ConnectionServiceImpl() {}

  /**
   * @param userDao userDao
   * @param connectionHistoryDao connectionHistoryDao
   */
  @Autowired
  public ConnectionServiceImpl(
      @Qualifier("userRefactoringDao") IGenericDao<User, Long> userDao,
      @Qualifier("connectionHistoryDao")
          IGenericDao<ConnectionHistory, Long> connectionHistoryDao) {
    super();
    this.userDao = (IUserDao) userDao;
    this.connectionHistoryDao = (IConnectionHistoryDao) connectionHistoryDao;
  }

  @Transactional
  @Override
  public String authenticate(String email, String password) {
    try {
      LOGGER.info("Authentication start");
      final User user = userDao.getUserByEmail(email);
      LOGGER.info("User found {}", user);
      String token;
      LOGGER.info("Token created");
      if (user != null) {
        Integer limitTryAuthentication;
        LOGGER.info("Limit try authentication created");
        final String limit =
            preferencesService.getPreferences().getProperty(KEY_LIMIT_TRY_AUTHENTICATION);
        LOGGER.info("Limit : {}", limit);
        if (limit != null) {
          LOGGER.info("Limit not null");
          limitTryAuthentication = Integer.parseInt(limit);
        } else {
          LOGGER.info("Limit null");
          limitTryAuthentication = LIMIT_TRY_AUTHENTICATION;
        }
        LOGGER.info("Limit try authentication initialized : {}", limitTryAuthentication);

        final String authentBlocked =
            preferencesService.getPreferences().getProperty(BLOCKED_ACCOUNT_DURATION);
        LOGGER.info("Authent blocked : {}", authentBlocked);
        long blockedAccountDuration;
        LOGGER.info("Blocked account duration created");
        if (authentBlocked != null) {
          LOGGER.info("Authent blocked not null");
          blockedAccountDuration = Long.parseLong(authentBlocked);
        } else {
          LOGGER.info("Authent blocked null");
          blockedAccountDuration = AUTHENT_BLOCKED;
        }
        LOGGER.info("Blocked account duration initialized : {}", blockedAccountDuration);

        final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        LOGGER.info("Password encoder created");
        if (passwordEncoder.matches(password, user.getPassword())) {
          token =
              authenticatePasswordMatch(
                  password, user, limitTryAuthentication, blockedAccountDuration);
        } else {
          token =
              authenticatePasswordMismatch(
                  password, user, limitTryAuthentication, blockedAccountDuration);
        }
      } else {
        LOGGER.info("Invalid password 3");
        token = INVALID_PASSWORD;
      }
      LOGGER.info("Authentication end");
      LOGGER.info("Token : {}", token);
      return token;
    } catch (DatabaseException e) {
      LOGGER.info("Authentication failure", e);
      throw new AccessDeniedException("Authentication failed", e);
    }
  }

  private String authenticatePasswordMatch(
      String password, User user, Integer limitTryAuthentication, long blockedAccountDuration) {
    String token;
    LOGGER.info("Password {} matches {}", password, user.getPassword());
    if (user.getNbAuthentFailed() < limitTryAuthentication) {
      LOGGER.info("Nb authent failed < limit try authentication");
      token = userExist(user);
      LOGGER.info("Token created : {}", token);
    } else if (user.getNbAuthentFailed() >= limitTryAuthentication
        && new Date()
            .after(
                new Date(user.getDateLastTryAuthentication().getTime() + blockedAccountDuration))) {
      LOGGER.info(
          "Nb authent failed >= limit try authentication and date after date last try"
              + " authentication + blocked account duration");
      token = userExist(user);
      LOGGER.info("Token created : {}", token);
    } else {
      LOGGER.info("Compte verrouille");
      throw new ServiceException("Compte verrouillé");
    }
    return token;
  }

  @NotNull
  private String authenticatePasswordMismatch(
      String password, User user, Integer limitTryAuthentication, long blockedAccountDuration) {
    String token;
    LOGGER.info("Password {} doesn't match {}", password, user.getPassword());
    if (user.getNbAuthentFailed() < limitTryAuthentication) {
      LOGGER.info("Nb authent failed < limit try authentication");
      user.setNbAuthentFailed(user.getNbAuthentFailed() + 1);
      LOGGER.info("Nb authent failed incremented");
      user.setDateLastTryAuthentication(new Date());
      LOGGER.info("Date last try authentication set");
      userDao.update(user);
      LOGGER.info("User updated");
    } else if (user.getNbAuthentFailed() >= limitTryAuthentication
        && new Date()
            .after(
                new Date(user.getDateLastTryAuthentication().getTime() + blockedAccountDuration))) {
      LOGGER.info(
          "Nb authent failed >= limit try authentication and date after date last try"
              + " authentication + blocked account duration");
      user.setNbAuthentFailed(1);
      LOGGER.info("Nb authent failed set to 1");
      user.setDateLastTryAuthentication(new Date());
      LOGGER.info("Date last try authentication set");
      userDao.update(user);
      LOGGER.info("User updated");
    } else {
      LOGGER.info("Invalid password 1");
      throw new ServiceException(INVALID_PASSWORD);
    }
    LOGGER.info("Invalid password 2");
    token = INVALID_PASSWORD;
    return token;
  }

  @Transactional
  @Override
  public String validateToken(String validateTokenRequest) {
    String token = null;
    if (jwtTokenProvider.validateToken(validateTokenRequest) != null) {
      final User user = userDao.getUserByEmail(jwtTokenProvider.getUsername(validateTokenRequest));

      if (user != null) {
        final List<Profile> profiles = new ArrayList<>();
        profiles.add(user.getProfile());
        token = jwtTokenProvider.createToken(user, profiles, null);
      }
    }
    return token;
  }

  @Value("${server.servlet.context-path}")
  private String contextPath;

  @Override
  @Transactional(readOnly = true)
  @Async
  public void forgotPassword(String eMailAddress) {
    try {
      final User user = userDao.getUserByEmail(eMailAddress);
      String token;
      if (user != null) {
        final List<Profile> profiles = new ArrayList<>();
        profiles.add(user.getProfile());
        token = jwtTokenProvider.createToken(user, profiles, EXPIRATION_TIME_RESET);
      } else {
        throw new ServiceException();
      }
      final Mail mail = new Mail();
      mail.setMailTo(user.getMail());
      mail.setMailSubject(SUBJECT_FORGOT_PASSWORD);
      mail.setTemplate("/velocity/mail/forgot_password.vm");
      final String url =
          System.getenv("EXTERNAL_PROTOCOL")
              + "://"
              + System.getenv("API_DOMAIN")
              + contextPath
              + "#/reset;id="
              + token;
      final Map<String, Object> variables = new HashMap<>();
      variables.put("url", url);
      variables.put("firstName", HtmlUtils.htmlEscape(user.getForename()));
      mail.setVariables(variables);
      mailService.sendEmail(mail);
    } catch (final DatabaseException e) {
      throw new ServiceException("Utilisateur inconnu avec ce mot de passe");
    }
  }

  @Override
  @Transactional
  public void resetPassword(String token, String password) {
    jwtTokenProvider.validateToken(token);
    final String userMail = jwtTokenProvider.getUsername(token);
    try {
      final User user = userDao.getUserByEmail(userMail);
      user.setPassword(password);
    } catch (final DatabaseException e) {
      throw new ServiceException(e.getErrorDescription());
    }
  }

  private String userExist(User user) {
    final List<Profile> profiles = new ArrayList<>();
    profiles.add(user.getProfile());
    final ConnectionHistory connectionHistory = new ConnectionHistory();
    connectionHistory.setMail(user.getMail());
    connectionHistory.setDateLogin(new Date());
    connectionHistoryDao.saveConnectionHistory(connectionHistory);
    user.setNbAuthentFailed(0);
    user.setDateLastTryAuthentication(new Date());
    String connectionMessage =
        "connection successful for user:"
            + " LastName="
            + user.getName()
            + " FirstName="
            + user.getForename()
            + " Firm="
            + user.getFirm()
            + " Profile="
            + user.getProfile().getLabel();
    LOGGER.info(connectionMessage);
    return jwtTokenProvider.createToken(user, profiles, null);
  }
}
