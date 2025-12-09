/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.executor;

import com.smartbear.ready.cmd.runner.pro.SoapUIProTestCaseRunner;
import fr.asipsante.platines.model.DriverTestResult;
import java.io.File;

public class SoapUIProExecutor extends Executor {

  @Override
  public void init() {
    SoapUIProTestCaseRunner runner = new SoapUIProTestCaseRunner();
  }

  @Override
  public DriverTestResult execute(File project) {
    // TODO Auto-generated method stub
    return null;
  }
}
