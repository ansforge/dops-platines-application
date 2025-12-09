/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.executor.soapui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import fr.asipsante.platines.executor.model.ProjectDetail;
import fr.asipsante.platines.executor.model.TestCaseDetail;
import fr.asipsante.platines.executor.model.TestSuiteDetail;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Order(20)
public class SoapUIProjectBuilderTest {

  @Test
  public void testGetProjectDetail() throws IOException {
    InputStream projectContent =
        Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream(
                "fr/asipsante/platines/executor/soapui/C_Notif_parametrable__conformite_au_schema.xml");
    SoapUIProjectBuilder builder = new SoapUIProjectBuilder();
    ProjectDetail p = builder.getProjectDetail(projectContent.readAllBytes());
    assertEquals(4, p.getProperties().size());
  }

  @Test
  public void testStepFilter() throws IOException {
    InputStream projectContent =
        Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream(
                "fr/asipsante/platines/executor/soapui/Nested-data-source-loop-project.xml");
    SoapUIProjectBuilder builder = new SoapUIProjectBuilder();
    ProjectDetail p = builder.getProjectDetail(projectContent.readAllBytes());
    assertFalse(p.getTestSuiteDetail().isEmpty(), "At least one TestSuite should be found.");
    final TestSuiteDetail testSuite = p.getTestSuiteDetail().get(0);
    assertFalse(testSuite.getListTestCase().isEmpty(), "At least one TestCase should be found.");
    final TestCaseDetail testCaseDetail = testSuite.getListTestCase().get(0);
    assertEquals(2, testCaseDetail.getListTestSteps().size());
  }
}
