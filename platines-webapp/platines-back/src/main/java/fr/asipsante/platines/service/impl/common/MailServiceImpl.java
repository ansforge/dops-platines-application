/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.mail.entity.Mail;
import fr.asipsante.platines.service.MailService;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author aboittiaux
 */
@Service(value = "mailService")
public class MailServiceImpl implements MailService {

  @Value("${spring.mail.alias}")
  private String from;

  @Value("${spring.mail.personal-name}")
  private String personalName;

  /** LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);

  /** MAIL_FROM. */
  @Value("${spring.mail.alias:noreply.platines@esante.gouv.fr}")
  private String MAIL_FROM;

  /** JavaMailSender. */
  @Autowired JavaMailSender mailSender;

  /** VelocityEngine. */
  @Autowired VelocityEngine velocityEngine;

  /** {@inheritDoc} */
  @Override
  @Async
  public void sendEmail(Mail mail) {
    final MimeMessage mimeMessage = mailSender.createMimeMessage();
    try {
      mimeMessage.setHeader("Content-Type", "text/plain; charset=UTF-8");
      mimeMessage.setSubject(mail.getMailSubject(), "utf-8");
      final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
      InternetAddress fromAddress = new InternetAddress(from, personalName);
      mimeMessageHelper.setFrom(fromAddress);
      InternetAddress toAddress = new InternetAddress(mail.getMailTo(), mail.getMailTo());
      mimeMessageHelper.setTo(toAddress);
      mail.setMailContent(generateContentFromTemplate(mail.getVariables(), mail.getTemplate()));
      mimeMessageHelper.setText(mail.getMailContent(), true);
      mailSender.send(mimeMessage);
    } catch (final MessagingException e) {
      LOGGER.error("Erreur lors de l'envoi de mail ", e);
    } catch (UnsupportedEncodingException e) {
      LOGGER.error("Erreur lors de l'encodage de mail ", e);
    }
  }

  /**
   * Gets the content from a template.
   *
   * @param model, the model
   * @param template, the template
   * @return the content
   */
  public String generateContentFromTemplate(Map<String, Object> variables, String template) {
    final StringWriter stringWriter = new StringWriter();
    velocityEngine.mergeTemplate(template, "UTF-8", new VelocityContext(variables), stringWriter);
    return stringWriter.toString();
  }
}
