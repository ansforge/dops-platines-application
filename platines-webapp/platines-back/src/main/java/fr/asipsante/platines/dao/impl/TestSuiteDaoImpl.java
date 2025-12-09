/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.ITestSuiteDao;
import fr.asipsante.platines.entity.TestSuiteResult;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

/**
 * @author apierre
 */
@Repository(value = "testSuiteDao")
public class TestSuiteDaoImpl extends GenericDaoImpl<TestSuiteResult, Long>
    implements ITestSuiteDao {

  @Override
  public TestSuiteResult persistAndGet(TestSuiteResult testSuiteResult) {
    entityManager.persist(testSuiteResult);
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Long> query = builder.createQuery(Long.class);
    final Root<TestSuiteResult> root = query.from(TestSuiteResult.class);
    query.select(builder.max(root.get("id")));
    final Long id = entityManager.createQuery(query).getSingleResult();
    return getById(id);
  }

  /** {@inheritDoc} */
  @Override
  public void updateTestSuite(TestSuiteResult suiteResult) {
    entityManager.merge(suiteResult);
  }

  /** {@inheritDoc} */
  @Override
  public List<TestSuiteResult> getTestSuiteByRproject(Long idRproject) {
    return (List<TestSuiteResult>)
        entityManager
            .createQuery("SELECT s FROM TestSuiteResult s where s.projectResult.id = :id")
            .setParameter("id", idRproject)
            .getResultList();
  }
}
