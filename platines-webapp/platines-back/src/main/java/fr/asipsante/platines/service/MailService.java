/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.mail.entity.Mail;

/**
 * The Mail service.
 *
 * @author apierre
 */
public interface MailService {

  /**
   * Sends a mail.
   *
   * @param mail, the mail to send
   */
  void sendEmail(Mail mail);
}
