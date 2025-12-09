/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Acl entry.
 *
 * @author apierre
 */
@Entity
@Table(name = "acl_entry")
public class AclEntry implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5712883372207878694L;

  /** entry id. */
  @Id
  @GeneratedValue
  @Column(name = "id")
  private Long id;

  /** entry object. */
  @OneToOne
  @JoinColumn(name = "acl_object_identity")
  private AclObjectIdentity aclObjectIdentity;

  /** ace order. */
  @Column(name = "ace_order")
  private int aceOrder;

  /** sid. */
  @OneToOne
  @JoinColumn(name = "sid")
  private AclSid sid;

  /** mask. */
  @Column(name = "mask")
  private int mask;

  /** granting. */
  @Column(name = "granting")
  private boolean granting;

  /** success. */
  @Column(name = "audit_success")
  private boolean auditSuccess;

  /** failure. */
  @Column(name = "audit_failure")
  private boolean auditFailure;

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
   * @return the aceOrder
   */
  public int getAceOrder() {
    return aceOrder;
  }

  /**
   * @param aceOrder the aceOrder to set
   */
  public void setAceOrder(int aceOrder) {
    this.aceOrder = aceOrder;
  }

  /**
   * @return the mask
   */
  public int getMask() {
    return mask;
  }

  /**
   * @param mask the mask to set
   */
  public void setMask(int mask) {
    this.mask = mask;
  }

  /**
   * @return the granting
   */
  public boolean isGranting() {
    return granting;
  }

  /**
   * @param granting the granting to set
   */
  public void setGranting(boolean granting) {
    this.granting = granting;
  }

  /**
   * @return the auditSuccess
   */
  public boolean isAuditSuccess() {
    return auditSuccess;
  }

  /**
   * @param auditSuccess the auditSuccess to set
   */
  public void setAuditSuccess(boolean auditSuccess) {
    this.auditSuccess = auditSuccess;
  }

  /**
   * @return the auditFailure
   */
  public boolean isAuditFailure() {
    return auditFailure;
  }

  /**
   * @param auditFailure the auditFailure to set
   */
  public void setAuditFailure(boolean auditFailure) {
    this.auditFailure = auditFailure;
  }

  /**
   * @return the aclObjectIdentity
   */
  public AclObjectIdentity getAclObjectIdentity() {
    return aclObjectIdentity;
  }

  /**
   * @param aclObjectIdentity the aclObjectIdentity to set
   */
  public void setAclObjectIdentity(AclObjectIdentity aclObjectIdentity) {
    this.aclObjectIdentity = aclObjectIdentity;
  }

  /**
   * @return the sid
   */
  public AclSid getSid() {
    return sid;
  }

  /**
   * @param sid the sid to set
   */
  public void setSid(AclSid sid) {
    this.sid = sid;
  }
}
