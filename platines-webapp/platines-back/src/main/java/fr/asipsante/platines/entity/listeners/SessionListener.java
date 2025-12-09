/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity.listeners;

import fr.asipsante.platines.dao.IAclSecurityDao;
import fr.asipsante.platines.entity.AbstractEntity;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author apierre
 */
@Component
public class SessionListener {

  /** acl dao. */
  private static IAclSecurityDao aclDao;

  /** constructeur. */
  public SessionListener() {
    super();
  }

  @Autowired
  public void setAclDao(IAclSecurityDao aclDao) {
    SessionListener.aclDao = aclDao;
  }

  /**
   * Ajout des permissions pour une session. Ajout pour l'admin et pour le propriétaire de
   * l'application.
   *
   * @param entity, l'entité à enregistrer
   */
  @PostPersist
  public void addPermissionSession(AbstractEntity entity) {
    aclDao.addPermissionAdmin(entity);
    if (entity.getOwner() != null) {
      aclDao.addAllPermission(entity, entity.getOwner());
    }
  }

  /**
   * suppression des permissions de la session.
   *
   * @param entity, l'entité supprimée
   */
  @PostRemove
  public void deletePermissionSession(AbstractEntity entity) {
    aclDao.deletePermission(entity);
  }
}
