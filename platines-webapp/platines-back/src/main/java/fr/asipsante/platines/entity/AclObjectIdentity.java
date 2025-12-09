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
 * Acl object identity.
 *
 * @author apierre
 */
@Entity
@Table(name = "acl_object_identity")
public class AclObjectIdentity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1866880816858891075L;

  /** the object id. */
  @Id
  @GeneratedValue
  @Column(name = "id")
  private Long id;

  /** the object class in acl. */
  @OneToOne
  @JoinColumn(name = "object_id_class")
  private AclClass objectClass;

  /** the object id in database. */
  @Column(name = "object_id_identity")
  private Long objectIdIdentity;

  /** the parent. */
  @Column(name = "parent_object")
  private Long parentObject;

  /** the owner. */
  @OneToOne
  @JoinColumn(name = "owner_sid")
  private AclSid ownerSid;

  /** inheriting. */
  @Column(name = "entries_inheriting")
  private boolean entriesInheriting;

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
   * @return the objectIdIdentity
   */
  public Long getObjectIdIdentity() {
    return objectIdIdentity;
  }

  /**
   * @param objectIdIdentity the objectIdIdentity to set
   */
  public void setObjectIdIdentity(Long objectIdIdentity) {
    this.objectIdIdentity = objectIdIdentity;
  }

  /**
   * @return the parentObject
   */
  public Long getParentObject() {
    return parentObject;
  }

  /**
   * @param parentObject the parentObject to set
   */
  public void setParentObject(Long parentObject) {
    this.parentObject = parentObject;
  }

  /**
   * @return the entriesInheriting
   */
  public boolean isEntriesInheriting() {
    return entriesInheriting;
  }

  /**
   * @param entriesInheriting the entriesInheriting to set
   */
  public void setEntriesInheriting(boolean entriesInheriting) {
    this.entriesInheriting = entriesInheriting;
  }

  /**
   * @return the objectClass
   */
  public AclClass getObjectClass() {
    return objectClass;
  }

  /**
   * @param objectClass the objectClass to set
   */
  public void setObjectClass(AclClass objectClass) {
    this.objectClass = objectClass;
  }

  /**
   * @return the ownerSid
   */
  public AclSid getOwnerSid() {
    return ownerSid;
  }

  /**
   * @param ownerSid the ownerSid to set
   */
  public void setOwnerSid(AclSid ownerSid) {
    this.ownerSid = ownerSid;
  }
}
