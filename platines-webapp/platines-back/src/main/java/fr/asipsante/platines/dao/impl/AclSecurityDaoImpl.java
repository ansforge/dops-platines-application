/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IAclSecurityDao;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @author apierre
 */
@Repository(value = "aclSecurityDao")
public class AclSecurityDaoImpl implements IAclSecurityDao {

  /** Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(AclSecurityDaoImpl.class);

  /** name authority admin. */
  private static final String AUTHORITY_ADMIN = "ROLE_admin";

  /** name authority manager. */
  private static final String AUTHORITY_MANAGER = "ROLE_manager";

  /** Entity manager. */
  @PersistenceContext protected EntityManager entityManager;

  /** acl service. */
  @Autowired private JdbcMutableAclService mutableAclService;

  /** Constructeur. */
  public AclSecurityDaoImpl() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = false)
  public void addPermission(Object entity, Sid recipient, Permission permission) {
    MutableAcl acl;
    final ObjectIdentity oid = new ObjectIdentityImpl(entity);

    try {
      LOGGER.debug("mutable acl service : {}", mutableAclService);
      acl = (MutableAcl) mutableAclService.readAclById(oid);
    } catch (final NotFoundException nfe) {
      acl = mutableAclService.createAcl(oid);
    }

    acl.insertAce(acl.getEntries().size(), permission, recipient, true);
    mutableAclService.updateAcl(acl);

    LOGGER.debug("Added permission " + permission + " for Sid " + recipient + " contact " + entity);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public void addPermissionAdmin(Object entity) {

    final Sid sidAdmin = new GrantedAuthoritySid(AUTHORITY_ADMIN);
    MutableAcl acl;
    final ObjectIdentity oid = new ObjectIdentityImpl(entity);

    try {
      acl = (MutableAcl) mutableAclService.readAclById(oid);
    } catch (final NotFoundException nfe) {
      acl = mutableAclService.createAcl(oid);
    }

    acl.insertAce(acl.getEntries().size(), BasePermission.READ, sidAdmin, true);
    acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, sidAdmin, true);
    acl.insertAce(acl.getEntries().size(), BasePermission.CREATE, sidAdmin, true);
    acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, sidAdmin, true);
    mutableAclService.updateAcl(acl);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public void addPermissionManager(Object entity) {

    final Sid sidManager = new GrantedAuthoritySid(AUTHORITY_MANAGER);
    MutableAcl acl;
    final ObjectIdentity oid = new ObjectIdentityImpl(entity);

    try {
      acl = (MutableAcl) mutableAclService.readAclById(oid);
    } catch (final NotFoundException nfe) {
      acl = mutableAclService.createAcl(oid);
    }

    acl.insertAce(acl.getEntries().size(), BasePermission.READ, sidManager, true);
    acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, sidManager, true);
    acl.insertAce(acl.getEntries().size(), BasePermission.CREATE, sidManager, true);
    acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, sidManager, true);
    mutableAclService.updateAcl(acl);
  }

  /** {@inheritDoc} */
  @Override
  public void addAllPermission(Object entity, Sid sid) {
    MutableAcl acl;
    final ObjectIdentity oid = new ObjectIdentityImpl(entity);

    try {
      acl = (MutableAcl) mutableAclService.readAclById(oid);
    } catch (final NotFoundException nfe) {
      acl = mutableAclService.createAcl(oid);
    }

    acl.insertAce(acl.getEntries().size(), BasePermission.READ, sid, true);
    acl.insertAce(acl.getEntries().size(), BasePermission.WRITE, sid, true);
    acl.insertAce(acl.getEntries().size(), BasePermission.CREATE, sid, true);
    acl.insertAce(acl.getEntries().size(), BasePermission.DELETE, sid, true);
    mutableAclService.updateAcl(acl);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public boolean addAccessControlEntry(Object element, Sid recipient, Permission permission) {
    Assert.notNull(element, "AbstractSecuredEntity required");
    Assert.notNull(recipient, "recipient required");
    Assert.notNull(permission, "permission required");

    MutableAcl acl;
    final ObjectIdentity oid = new ObjectIdentityImpl(element);

    try {
      acl = (MutableAcl) mutableAclService.readAclById(oid);
    } catch (final NotFoundException nfe) {
      acl = mutableAclService.createAcl(oid);
    }

    if (doesACEExists(element, recipient, permission)) {
      LOGGER.debug(
          "ACE already exists, element:"
              + element
              + ", Sid:"
              + recipient
              + ", permission:"
              + permission);
    } else {
      acl.insertAce(acl.getEntries().size(), permission, recipient, true);
      mutableAclService.updateAcl(acl);
      LOGGER.debug(
          "Added permission " + permission + " for Sid " + recipient + " contact " + element);
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean doesACEExists(Object element, Sid recipient, Permission permission) {
    boolean result = false;
    final ObjectIdentity oid = new ObjectIdentityImpl(element);
    MutableAcl acl;
    try {
      acl = (MutableAcl) mutableAclService.readAclById(oid);
    } catch (final NotFoundException nfe) {
      acl = mutableAclService.createAcl(oid);
    }

    final List<AccessControlEntry> entries = acl.getEntries();
    for (final AccessControlEntry ace : entries) {
      if (ace.getSid().equals(recipient) && ace.getPermission().equals(permission)) {
        result = true;
      }
    }
    return result;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public void deletePermission(Object element) {
    // Delete the ACL information as well
    final ObjectIdentity oid = new ObjectIdentityImpl(element);
    mutableAclService.deleteAcl(oid, true);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public void deleteAccessControlEntry(Object element, Sid recipient, Permission permission) {
    final ObjectIdentity oid = new ObjectIdentityImpl(element);
    MutableAcl acl;
    try {
      acl = (MutableAcl) mutableAclService.readAclById(oid);
    } catch (final NotFoundException nfe) {
      acl = mutableAclService.createAcl(oid);
    }

    final List<AccessControlEntry> entries = acl.getEntries();
    for (int i = 0; i < entries.size(); i++) {
      if (entries.get(i).getSid().equals(recipient)
          && entries.get(i).getPermission().equals(permission)) {
        acl.deleteAce(i);
      }
    }

    mutableAclService.updateAcl(acl);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Deleted Permission:"
              + permission
              + " for recipient: "
              + recipient
              + ", for object: "
              + element);
    }
  }

  /** {@inheritDoc} */
  @Override
  public void addPermissionVisibleUser(Object entity, Sid sid) {
    MutableAcl acl;
    final ObjectIdentity oid = new ObjectIdentityImpl(entity);

    try {
      acl = (MutableAcl) mutableAclService.readAclById(oid);
    } catch (final NotFoundException nfe) {
      acl = mutableAclService.createAcl(oid);
    }

    acl.insertAce(acl.getEntries().size(), BasePermission.READ, sid, true);
    mutableAclService.updateAcl(acl);
  }

  /**
   * @return the mutableAclService
   */
  public MutableAclService getMutableAclService() {
    return mutableAclService;
  }

  /**
   * @param mutableAclService the mutableAclService to set
   */
  public void setMutableAclService(JdbcMutableAclService mutableAclService) {
    this.mutableAclService = mutableAclService;
  }
}
