/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity.listeners;

import fr.asipsante.platines.dao.IAclSecurityDao;
import fr.asipsante.platines.entity.AbstractEntity;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * @author apierre
 */
@Component
public class VisibilityListener {

  /** LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(VisibilityListener.class);

  /** authority name for a user. */
  private static final String AUTHORITY_USER = "ROLE_user";

  /** acl dao. */
  private static IAclSecurityDao aclDao;

  /** Constructeur. */
  public VisibilityListener() {
    super();
  }

  /** initialize the class, and inject it in spring context. */
  @Autowired
  public void setAclDao(IAclSecurityDao aclDao) {
    VisibilityListener.aclDao = aclDao;
  }

  /**
   * Ajoute les permission acl, après avoir enregistré en base. ajoute tous les droits pour l'admin.
   * ajoute tous les droits pour l'user propriétaire. ajoute le droit en lecture pour tous les
   * managers.
   *
   * @param entity l'entité concernée
   */
  @PostPersist
  public void addPermissionPostCreate(AbstractEntity entity) {
    final Sid sid = new PrincipalSid(SecurityContextHolder.getContext().getAuthentication());
    aclDao.addPermissionAdmin(entity);
    aclDao.addAllPermission(entity, sid);
    addPermissionVisible(entity);
  }

  private void addPermissionVisible(AbstractEntity entity) {
    final Sid sidRoleUser = new GrantedAuthoritySid(AUTHORITY_USER);

    Method m = ReflectionUtils.findMethod(entity.getClass(), "getVisibility");
    if (m != null) {
      try {
        if ((boolean) m.invoke(entity)) {
          if (!aclDao.doesACEExists(entity, sidRoleUser, BasePermission.READ)) {
            aclDao.addPermissionVisibleUser(entity, sidRoleUser);
          }
        } else {
          if (aclDao.doesACEExists(entity, sidRoleUser, BasePermission.READ)) {
            aclDao.deleteAccessControlEntry(entity, sidRoleUser, BasePermission.READ);
          }
        }
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
        LOGGER.error("Error when invoke getVisibility() Method", e);
      }
    }
  }

  /**
   * supprime les permissions associées à une entité.
   *
   * @param entity l'entité concernée
   */
  @PostRemove
  public void deletePermission(AbstractEntity entity) {
    aclDao.deletePermission(entity);
  }

  /**
   * vérifie que les users ont le bon droit en fonction de la visibilité de l'entité.
   *
   * @param entity l'entité concernée
   */
  @PostUpdate
  public void updatePermission(AbstractEntity entity) {
    addPermissionVisible(entity);
  }
}
