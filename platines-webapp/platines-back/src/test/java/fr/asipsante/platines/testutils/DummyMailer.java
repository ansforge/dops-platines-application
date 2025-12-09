/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.testutils;

import javax.mail.internet.MimeMessage;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author edegenetais
 */
public class DummyMailer extends JavaMailSenderImpl {

  @Override
  protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages)
      throws MailException {
    LoggerFactory.getLogger(DummyMailer.class).info("Fake mail doSend()");
  }
}
