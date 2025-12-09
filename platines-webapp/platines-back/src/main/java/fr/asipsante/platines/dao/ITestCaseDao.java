/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.TestCaseResult;
import java.util.List;

/**
 * Interface provides methods required to link {@link TestCaseResult} entity to a data source.
 *
 * @author apierre
 */
public interface ITestCaseDao extends IGenericDao<TestCaseResult, Long> {

  /**
   * Saves and gets a testCase result.
   *
   * @param testCaseResult, the testcase result to save.
   * @return the test case result
   */
  TestCaseResult persistAndGet(TestCaseResult testCaseResult);

  /**
   * Gets a test case result without token.
   *
   * @param id, the test case id
   * @return the test case
   */
  TestCaseResult getWithoutToken(Long id);

  /**
   * Gets all the test case for a session.
   *
   * @param uuidSession, the session uuid
   * @return all the test case
   */
  List<TestCaseResult> getTestCasesByUuidSession(String uuidSession);

  /**
   * Updates a test case.
   *
   * @param caseResult, the test case to update
   */
  void updateTestCase(TestCaseResult caseResult);

  /**
   * Gets all test case for a test suite.
   *
   * @param idTestSuite, the test suite id
   * @return all test case
   */
  List<TestCaseResult> getTestCasesByTestSuite(Long idTestSuite);
}
