/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.ITestCaseDao;
import fr.asipsante.platines.entity.TestCaseResult;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

/**
 * @author apierre
 */
@Repository(value = "testCaseDao")
public class TestCaseDaoImpl extends GenericDaoImpl<TestCaseResult, Long> implements ITestCaseDao {

  @Override
  public TestCaseResult persistAndGet(TestCaseResult testCaseResult) {
    entityManager.persist(testCaseResult);
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Long> query = builder.createQuery(Long.class);
    final Root<TestCaseResult> root = query.from(TestCaseResult.class);
    query.select(builder.max(root.get("id")));
    final Long id = entityManager.createQuery(query).getSingleResult();
    return getById(id);
  }

  /** {@inheritDoc} */
  @Override
  public TestCaseResult getWithoutToken(Long id) {
    return entityManager.find(TestCaseResult.class, id);
  }

  /** {@inheritDoc} */
  @Override
  public List<TestCaseResult> getTestCasesByUuidSession(String uuidSession) {
    final List<TestCaseResult> caseResults =
        entityManager
            .createQuery(
                "SELECT c FROM TestCaseResult c WHERE c.testSuite.projectResult.testSession.uuid ="
                    + " :uuid")
            .setParameter("uuid", uuidSession)
            .getResultList();
    return caseResults;
  }

  @Override
  public void updateTestCase(TestCaseResult caseResult) {
    entityManager.merge(caseResult);
  }

  /** {@inheritDoc} */
  @Override
  public List<TestCaseResult> getTestCasesByTestSuite(Long idTestSuite) {
    final List<TestCaseResult> caseResults =
        entityManager
            .createQuery("SELECT c FROM TestCaseResult c WHERE c.testSuite.id = :id")
            .setParameter("id", idTestSuite)
            .getResultList();
    return caseResults;
  }
}
