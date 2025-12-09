/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.entity.listeners.GlobalListener;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;

/**
 * Class Application corresponding to the table "application" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "application")
@EntityListeners(GlobalListener.class)
public class Application extends AbstractEntity implements Serializable {

  /** The constant serialVersionUID. */
  private static final long serialVersionUID = 0L;

  /** Application name. */
  @Column(name = "nom", length = 50)
  private String name;

  /** Application version. */
  @Column(name = "version", length = 50)
  private String version;

  /** Application url. */
  @Column(name = "url", length = 150)
  private String url;

  /** Application description. */
  @Column(name = "description", length = 50)
  private String description;

  /** Application role. */
  @Enumerated(EnumType.STRING)
  @Column(name = "role", length = 25)
  private Role role;

  /** Application owner. */
  @ManyToOne
  @JoinColumn(name = "USER_ID", nullable = false)
  private User user;

  /** Application chain of trust. */
  @ManyToOne
  @JoinColumn(name = "chainedeconfiance_id")
  private ChainOfTrust chainOfTrust;

  /**
   * Gets the application name.
   *
   * @return application name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the application name.
   *
   * @param name, the new application name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the application version.
   *
   * @return application version
   */
  public String getVersion() {
    return version;
  }

  /**
   * Sets the application version.
   *
   * @param version, the new application version to set
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Gets the application URL.
   *
   * @return application URL
   */
  public String getUrl() {
    return url;
  }

  /**
   * Sets the application URL.
   *
   * @param url, the new application URL to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * Gets the application description.
   *
   * @return application description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the application description.
   *
   * @param description, the new description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the application role.
   *
   * @return application role
   */
  public Role getRole() {
    return role;
  }

  /**
   * Sets the application role.
   *
   * @param role, the new application role to set
   */
  public void setRole(Role role) {
    this.role = role;
  }

  /**
   * Gets the application user.
   *
   * @return the application user
   */
  public User getUser() {
    return user;
  }

  /**
   * Sets the application user.
   *
   * @param user, the new application user to set
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * Gets the application chain of trust.
   *
   * @return application chain of trust
   */
  public ChainOfTrust getChainOfTrust() {
    return chainOfTrust;
  }

  /**
   * Sets the application chain of trust.
   *
   * @param chainOfTrust, the new application chain of trust to set
   */
  public void setChainOfTrust(ChainOfTrust chainOfTrust) {
    this.chainOfTrust = chainOfTrust;
  }

  @Override
  public Sid getOwner() {
    return new PrincipalSid(getUser().getMail());
  }
}
