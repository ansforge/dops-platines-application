/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.ITestStepDao;
import fr.asipsante.platines.entity.TestStepResult;
import org.springframework.stereotype.Repository;

/**
 * @author apierre
 */
@Repository(value = "testStepDao")
public class TestStepDaoImpl extends GenericDaoImpl<TestStepResult, Long> implements ITestStepDao {

  @Override
  public void persistWithoutToken(TestStepResult result) {
    entityManager.persist(result);
  }
}
