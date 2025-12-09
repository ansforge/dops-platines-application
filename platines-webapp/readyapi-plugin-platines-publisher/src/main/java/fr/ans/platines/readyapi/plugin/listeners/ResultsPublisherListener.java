/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.readyapi.plugin.listeners;

import com.eviware.soapui.model.support.TestRunListenerAdapter;
import com.eviware.soapui.model.testsuite.TestCaseRunContext;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.plugins.ListenerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ListenerConfiguration
public class ResultsPublisherListener extends TestRunListenerAdapter {

  private static final Logger logger = LoggerFactory.getLogger(ProjectRunnerListener.class);

  @Override
  public void afterRun(TestCaseRunner testRunner, TestCaseRunContext runContext) {
    logger.info("Test " + testRunner.getTestCase().getName() + " stopping...");
    // Alimenter le publisher
    // Voir si on peut publier les résultats au fil de l'eau...
  }
}
