/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;

/**
 * Class ConnectionHistory corresponding to the table "historique_de_connexion" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "historique_de_connexion")
public class ConnectionHistory extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 0L;

  /** The user mail use as login. */
  @Column(name = "mail", length = 50)
  private String mail;

  /** The date login. */
  @Column(name = "dateLogin")
  private Date dateLogin;

  /**
   * Gets the mail.
   *
   * @return the mail
   */
  public String getMail() {
    return mail;
  }

  /**
   * Sets the mail.
   *
   * @param mail, the new mail
   */
  public void setMail(String mail) {
    this.mail = mail;
  }

  /**
   * Gets the date login.
   *
   * @return the date login
   */
  public Date getDateLogin() {
    return dateLogin;
  }

  /**
   * Sets the date login.
   *
   * @param dateLogin, the new date login
   */
  public void setDateLogin(Date dateLogin) {
    this.dateLogin = dateLogin;
  }

  @Override
  public Sid getOwner() {
    return new PrincipalSid(mail);
  }
}
