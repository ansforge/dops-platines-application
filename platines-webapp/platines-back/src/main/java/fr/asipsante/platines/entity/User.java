/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.listeners.UserListener;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Class User corresponding to the table "user" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "utilisateur")
@EntityListeners(UserListener.class)
public class User extends AbstractEntity implements Serializable {

  /** The constant SerialVersionUID. */
  private static final long serialVersionUID = -6419995385091742504L;

  /** User mail. */
  @Column(name = "mail", length = 50)
  private String mail;

  /** User password. */
  @Column(name = "password", length = 200)
  private String password;

  /** User name. */
  @Column(name = "nom", length = 50)
  private String name;

  /** User forename. */
  @Column(name = "prenom", length = 50)
  private String forename;

  /** User firm. */
  @Column(name = "entreprise", length = 50)
  private String firm;

  /** User creation date. */
  @Column(name = "dateCreation")
  private Date creationDate;

  /** User profile. */
  @ManyToOne
  @JoinColumn(name = "PROFILE_ID", nullable = false)
  private Profile profile;

  /** User creation date. */
  @Column(name = "date_last_try_authentication")
  private Date dateLastTryAuthentication;

  /** User creation date. */
  @Column(name = "number_of_failure")
  private Integer nbAuthentFailed;

  /** User themes. */
  @OneToMany(
      mappedBy = "user",
      cascade = {CascadeType.REMOVE},
      orphanRemoval = true)
  private Set<UserFamily> families;

  /** Constructors. */
  public User() {
    super();
  }

  public User(
      String mail,
      String password,
      String name,
      String forename,
      String firm,
      Profile profile,
      Set<UserFamily> families) {
    super();
    this.mail = mail;
    this.password = password;
    this.name = name;
    this.forename = forename;
    this.firm = firm;
    this.profile = profile;
    this.families = families;
  }

  /**
   * Adds user family.
   *
   * @param family, the new user family.
   */
  public void addFamily(Theme family) {

    final UserFamily userFamily = new UserFamily(this, family);
    families.add(userFamily);
    family.getUsers().add(userFamily);
  }

  /**
   * Removes user family.
   *
   * @param family, the user family to remove.
   */
  public void removeFamily(Theme family) {

    final UserFamily userFamily = new UserFamily(this, family);
    families.remove(userFamily);
    family.getUsers().remove(userFamily);
  }

  /**
   * Gets mail.
   *
   * @return user mail.
   */
  public String getMail() {
    return mail;
  }

  /**
   * Sets user mail.
   *
   * @param mail, the new mail.
   */
  public void setMail(String mail) {
    this.mail = mail;
  }

  /**
   * Gets user password.
   *
   * @return user password.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets user password.
   *
   * @param password, the new password.
   */
  public void setPassword(String password) {
    final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    this.password = passwordEncoder.encode(password);
  }

  /**
   * Gets user name.
   *
   * @return user name.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets user name.
   *
   * @param name, the new name.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets user forename.
   *
   * @return user forename.
   */
  public String getForename() {
    return forename;
  }

  /**
   * Sets user forename.
   *
   * @param forename, the new forename.
   */
  public void setForename(String forename) {
    this.forename = forename;
  }

  /**
   * Gets user firm.
   *
   * @return user firm.
   */
  public String getFirm() {
    return firm;
  }

  /**
   * Sets user firm.
   *
   * @param firm, the new user firm.
   */
  public void setFirm(String firm) {
    this.firm = firm;
  }

  /**
   * Gets user creation date.
   *
   * @return user creation date.
   */
  public Date getCreationDate() {
    return creationDate;
  }

  /**
   * Sets user creation date.
   *
   * @param creationDate, the new creation date.
   */
  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  /**
   * Gets user profile.
   *
   * @return the user profile.
   */
  public Profile getProfile() {
    return profile;
  }

  /**
   * Sets user profile.
   *
   * @param profile, the new user profile.
   */
  public void setProfile(Profile profile) {
    this.profile = profile;
  }

  /**
   * Gets user families.
   *
   * @return the user families.
   */
  public Set<UserFamily> getFamilies() {
    return families;
  }

  /**
   * Sets user families.
   *
   * @param families, the new user families.
   */
  public void setFamilies(Set<UserFamily> families) {
    this.families = families;
  }

  /**
   * @return the dateLastTryAuthentication
   */
  public Date getDateLastTryAuthentication() {
    return dateLastTryAuthentication;
  }

  /**
   * @param dateLastTryAuthentication the dateLastTryAuthentication to set
   */
  public void setDateLastTryAuthentication(Date dateLastTryAuthentication) {
    this.dateLastTryAuthentication = dateLastTryAuthentication;
  }

  /**
   * @return the nbAuthentFailed
   */
  public Integer getNbAuthentFailed() {
    return nbAuthentFailed;
  }

  /**
   * @param nbAuthentFailed the nbAuthentFailed to set
   */
  public void setNbAuthentFailed(Integer nbAuthentFailed) {
    this.nbAuthentFailed = nbAuthentFailed;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((firm == null) ? 0 : firm.hashCode());
    result = prime * result + ((forename == null) ? 0 : forename.hashCode());
    result = prime * result + ((mail == null) ? 0 : mail.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    result = prime * result + ((profile == null) ? 0 : profile.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    final User other = (User) obj;
    if (firm == null) {
      if (other.firm != null) return false;
    } else if (!firm.equals(other.firm)) return false;
    if (forename == null) {
      if (other.forename != null) return false;
    } else if (!forename.equals(other.forename)) return false;
    if (mail == null) {
      if (other.mail != null) return false;
    } else if (!mail.equals(other.mail)) return false;
    if (name == null) {
      if (other.name != null) return false;
    } else if (!name.equals(other.name)) return false;
    if (password == null) {
      if (other.password != null) return false;
    } else if (!password.equals(other.password)) return false;
    if (profile == null) {
      if (other.profile != null) return false;
    } else if (!profile.equals(other.profile)) return false;
    return true;
  }

  @Override
  public Sid getOwner() {
    return null;
  }
}
