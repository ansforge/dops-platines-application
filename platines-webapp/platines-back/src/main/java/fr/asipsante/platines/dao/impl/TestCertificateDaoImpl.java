/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.ITestCertificateDao;
import fr.asipsante.platines.entity.TestCertificate;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Repository;

/**
 * @author apierre
 */
@Repository(value = "testCertificateDao")
public class TestCertificateDaoImpl extends GenericDaoImpl<TestCertificate, Long>
    implements ITestCertificateDao {

  @Override
  @PostAuthorize(
      "hasPermission(returnObject, 'READ') or isManaged(returnObject) or hasAccess(returnObject)")
  public TestCertificate getById(Long id) {
    return entityManager.find(TestCertificate.class, id);
  }
}
