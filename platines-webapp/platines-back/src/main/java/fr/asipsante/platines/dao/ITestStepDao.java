/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import fr.asipsante.platines.entity.TestStepResult;

/**
 * Interface provides methods required to link {@link TestStepResult} entity to a data source.
 *
 * @author apierre
 */
public interface ITestStepDao extends IGenericDao<TestStepResult, Long> {

  /**
   * Saves a test step without token.
   *
   * @param result, the test step
   */
  void persistWithoutToken(TestStepResult result);
}
