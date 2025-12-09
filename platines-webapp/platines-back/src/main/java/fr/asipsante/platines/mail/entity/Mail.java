/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.mail.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author apierre
 */
public class Mail {
  /** Mail to. */
  private String mailTo;

  /** Mail cc. */
  private String mailCc;

  /** Mail Bcc. */
  private String mailBcc;

  /** Mail subject. */
  private String mailSubject;

  /** Mail content. */
  private String mailContent;

  /** Mail template. */
  private String template;

  /** Mail content type. */
  private String contentType;

  /** Attachments. */
  private List<Object> attachments;

  /** Model. */
  private Map<String, Object> variables;

  /** constructor. */
  public Mail() {
    contentType = "text/plain";
  }

  /**
   * Gets the content type.
   *
   * @return the content type.
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Sets the content type.
   *
   * @param contentType content Type
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /**
   * @return mailBcc
   */
  public String getMailBcc() {
    return mailBcc;
  }

  /**
   * @param mailBcc mail Bcc
   */
  public void setMailBcc(String mailBcc) {
    this.mailBcc = mailBcc;
  }

  /**
   * @return mailCc
   */
  public String getMailCc() {
    return mailCc;
  }

  /**
   * @param mailCc mail Cc
   */
  public void setMailCc(String mailCc) {
    this.mailCc = mailCc;
  }

  /**
   * @return mail subject
   */
  public String getMailSubject() {
    return mailSubject;
  }

  /**
   * @param mailSubject mail Subject
   */
  public void setMailSubject(String mailSubject) {
    this.mailSubject = mailSubject;
  }

  /**
   * @return mail to
   */
  public String getMailTo() {
    return mailTo;
  }

  /**
   * @param mailTo mail to
   */
  public void setMailTo(String mailTo) {
    this.mailTo = mailTo;
  }

  /**
   * @return send date
   */
  public Date getMailSendDate() {
    return new Date();
  }

  /**
   * @return mail content
   */
  public String getMailContent() {
    return mailContent;
  }

  /**
   * @param mailContent mail Content
   */
  public void setMailContent(String mailContent) {
    this.mailContent = mailContent;
  }

  /**
   * @return attachments
   */
  public List<Object> getAttachments() {
    return attachments;
  }

  /**
   * @param attachments attachments
   */
  public void setAttachments(List<Object> attachments) {
    this.attachments = attachments;
  }

  /**
   * @return variables
   */
  public Map<String, Object> getVariables() {
    return variables;
  }

  /**
   * @param variables variables
   */
  public void setVariables(Map<String, Object> variables) {
    this.variables = variables;
  }

  /**
   * @return the template
   */
  public String getTemplate() {
    return template;
  }

  /**
   * @param template the template to set
   */
  public void setTemplate(String template) {
    this.template = template;
  }
}
