/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Acl sid.
 *
 * @author apierre
 */
@Entity
@Table(name = "acl_sid")
public class AclSid implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3803546893218883909L;

  /** the sid id. */
  @Id
  @GeneratedValue
  @Column(name = "id")
  private Long id;

  /** the sid name. */
  @Column(name = "sid")
  private String sid;

  /** principal. */
  @Column(name = "principal")
  private boolean principal;

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the sid
   */
  public String getSid() {
    return sid;
  }

  /**
   * @param sid the sid to set
   */
  public void setSid(String sid) {
    this.sid = sid;
  }

  /**
   * @return the principal
   */
  public boolean isPrincipal() {
    return principal;
  }

  /**
   * @param principal the principal to set
   */
  public void setPrincipal(boolean principal) {
    this.principal = principal;
  }
}
