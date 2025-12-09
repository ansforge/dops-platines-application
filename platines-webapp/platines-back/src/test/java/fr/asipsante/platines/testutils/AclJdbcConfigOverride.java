/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.testutils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;

/**
 * Cet utilitaire de test permet de modifier la configuration de spring-security pendant les tests
 * pour le rendre compatible avec h2.
 *
 * @author edegenetais
 */
public class AclJdbcConfigOverride {
  private JdbcMutableAclService mutableAclService;

  public AclJdbcConfigOverride(@Autowired JdbcMutableAclService mutableAclService) {
    this.mutableAclService = mutableAclService;
    this.mutableAclService.setClassIdentityQuery("select max(id) from acl_class");
    this.mutableAclService.setSidIdentityQuery("select max(id) from acl_sid");
  }
}
