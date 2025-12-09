/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.TestSuiteResult;
import java.util.List;

/**
 * Interface provides methods required to link {@link TestSuiteResult} entity to a data source.
 *
 * @author apierre
 */
public interface ITestSuiteDao extends IGenericDao<TestSuiteResult, Long> {

  /**
   * Saves and gets a test suite.
   *
   * @param testSuiteResult, the test suite to save
   * @return the test suite
   */
  TestSuiteResult persistAndGet(TestSuiteResult testSuiteResult);

  /**
   * Updates a test suite.
   *
   * @param suiteResult, the test suite to update
   */
  void updateTestSuite(TestSuiteResult suiteResult);

  /**
   * Gets all the test suite for a project.
   *
   * @param idRproject, the project id
   * @return all the test suite
   */
  List<TestSuiteResult> getTestSuiteByRproject(Long idRproject);
}
