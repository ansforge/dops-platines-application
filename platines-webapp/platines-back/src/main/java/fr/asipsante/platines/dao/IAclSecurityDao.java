/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

/**
 * @author apierre
 */
public interface IAclSecurityDao {

  /**
   * Créé une permission pour une entité.
   *
   * @param entity, the entity to permit
   * @param recipient, l'utilisateur qui a les droits
   * @param permission, the permission to give
   */
  void addPermission(Object entity, Sid recipient, Permission permission);

  /**
   * Met des droits admin (lecture, ecriture, modification, suppression) sur une entité.
   *
   * @param entity, l'entité concernée.
   */
  void addPermissionAdmin(Object entity);

  /**
   * Met des droits gentionnaire (lecture, modification, suppression) sur une entité.
   *
   * @param entity, l'entité concernée.
   */
  void addPermissionManager(Object entity);

  /**
   * Ajoute les 4 permissions (lecture, écriture, modification, suppression) sur une entité pour un
   * utilisateur.
   *
   * @param entity, l'entité concernée
   * @param recipient, l'utilisateur qui obtient les droits
   */
  void addAllPermission(Object entity, Sid recipient);

  /**
   * Donne des droits en lecture à un utilisateur qui n'est pas propriétaire de l'objet.
   *
   * @param entity, l'entité concernée
   * @param sid, l'utilisateur à qui on ajoute le droit en lecture.
   */
  void addPermissionVisibleUser(Object entity, Sid sid);

  /**
   * Ajoute des ACE en base.
   *
   * @param element, l'entité
   * @param recipient, l'utilisateur
   * @param permission, la permission
   * @return true si c'est ok
   */
  boolean addAccessControlEntry(Object element, Sid recipient, Permission permission);

  /**
   * Vérifie si l'ACE existe déjà pour une entité.
   *
   * @param element, l'entité
   * @param recipient, l'utilisateur
   * @param permission, la permission
   * @return true si l'ACE existe
   */
  boolean doesACEExists(Object element, Sid recipient, Permission permission);

  /**
   * Supprime les permissions associées à une entité.
   *
   * @param element, l'entité concernée
   */
  void deletePermission(Object element);

  /**
   * Supprime un ACE en base.
   *
   * @param element, l'entité concernée
   * @param recipient, l'utilisateur
   * @param permission, la permission
   */
  void deleteAccessControlEntry(Object element, Sid recipient, Permission permission);
}
