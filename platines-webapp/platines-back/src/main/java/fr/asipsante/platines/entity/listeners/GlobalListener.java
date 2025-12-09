/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity.listeners;

import fr.asipsante.platines.dao.IAclSecurityDao;
import fr.asipsante.platines.entity.AbstractEntity;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * @author apierre
 */
@Component
public class GlobalListener {

  /** acl dao. */
  private static IAclSecurityDao aclDao;

  /** constructeur. */
  public GlobalListener() {
    super();
  }

  @Autowired
  public void setAclDao(IAclSecurityDao aclDao) {
    GlobalListener.aclDao = aclDao;
  }

  /**
   * ajoute des permissions à l'entite persistée.
   *
   * @param entity, l'entité persistée
   */
  @PostPersist
  public void addPermission(AbstractEntity entity) {
    final Sid owner = new PrincipalSid(SecurityContextHolder.getContext().getAuthentication());
    aclDao.addPermissionAdmin(entity);
    aclDao.addAllPermission(entity, owner);
  }

  /**
   * supprime les permissions de l'entité supprimée.
   *
   * @param entity, l'entité supprimée
   */
  @PostRemove
  public void deletePermission(AbstractEntity entity) {
    aclDao.deletePermission(entity);
  }
}
