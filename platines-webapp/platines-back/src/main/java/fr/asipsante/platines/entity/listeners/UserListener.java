/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity.listeners;

import fr.asipsante.platines.dao.IAclSecurityDao;
import fr.asipsante.platines.entity.AbstractEntity;
import fr.asipsante.platines.entity.User;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;
import org.springframework.stereotype.Component;

/**
 * @author apierre
 */
@Component
public class UserListener {

  /** acl dao. */
  private static IAclSecurityDao aclDao;

  /** constructeur. */
  public UserListener() {
    super();
  }

  @Autowired
  public void setAclDao(IAclSecurityDao aclDao) {
    UserListener.aclDao = aclDao;
  }

  /**
   * Add permission for the user just created.
   *
   * @param entity the user
   */
  @PostPersist
  public void addPermissionUser(AbstractEntity entity) {
    final Sid sidUser = new PrincipalSid(((User) entity).getMail());
    aclDao.addPermissionAdmin(entity);
    aclDao.addPermission(entity, sidUser, BasePermission.READ);
    aclDao.addPermission(entity, sidUser, BasePermission.WRITE);
  }

  /**
   * delete permission for the user.
   *
   * @param user the user to delete
   */
  @PostRemove
  public void deletePermissionUser(AbstractEntity user) {
    aclDao.deletePermission(user);
  }
}
