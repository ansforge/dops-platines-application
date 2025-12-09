/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.executor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.settings.SSLSettings;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import fr.asipsante.platines.model.DriverContext;
import fr.asipsante.platines.model.DriverTestCaseResult;
import fr.asipsante.platines.model.DriverTestResult;
import fr.asipsante.platines.model.DriverTestSuiteResult;
import fr.asipsante.platines.publisher.TestsResultsPublisher;
import fr.asipsante.platines.utils.Unzip;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@WireMockTest(httpPort = 9898)
class ExecutorTest {

  static {
    System.setProperty("keystore.password", "password");
  }

  private Properties props = new Properties();

  @BeforeEach
  public void init(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
    String sessionDirectory =
        Thread.currentThread().getContextClassLoader().getResource(".").getPath()
            + "978161fc-7a2b-4b36-89a9-f12efed029a9";
    System.setProperty("session.directory", sessionDirectory);
    System.setProperty("java.app.dir", ".");
    Unzip unzip = new Unzip();
    File archiveFile = new File(sessionDirectory + ".zip");
    unzip.unzip(archiveFile, archiveFile.getParent());

    // wireMock rules in resource/mappings/scenarios
    props.setProperty(
        "endpoint", "http://localhost:" + Integer.toString(wmRuntimeInfo.getHttpPort()));
    props.setProperty("user.home", sessionDirectory);
  }

  @Test
  void testClientSession() throws Exception {
    List<TestsResultsPublisher> publishers = new ArrayList<>();
    String sessionDirectory =
        Thread.currentThread().getContextClassLoader().getResource(".").getPath()
            + "978161fc-7a2b-4b36-89a9-f12efed029a9";
    System.setProperty("session.directory", sessionDirectory);
    System.setProperty("java.app.dir", ".");
    Unzip unzip = new Unzip();
    File archiveFile = new File(sessionDirectory + ".zip");
    unzip.unzip(archiveFile, archiveFile.getParent());
    File projectFolder = new File(sessionDirectory + "/projets/1");
    DriverContext context = new DriverContext();
    context.setProjectDirectory(projectFolder.getParentFile());
    SoapuiExecutor executor = new SoapuiExecutor(context, publishers);
    assertDoesNotThrow(() -> executor.init());
    SoapUI.getSettings()
        .setString(SSLSettings.KEYSTORE, sessionDirectory + "/projets/1/keystore/test.p12");
    SoapUI.getSettings().reloadSettings();
    assertEquals(2, executor.getManager().getInstalledPlugins().size());
    DriverTestResult results =
        executor.execute(
            new File(sessionDirectory + "/projets/1/Nested-data-source-loop-project.xml"));
    List<DriverTestSuiteResult> suiteresults = results.getSuiteResults();
    List<DriverTestCaseResult> testResults = suiteresults.get(0).getCaseResults();
    assertEquals(1, testResults.size());
    assertEquals("SUCCESS", testResults.get(0).getStepResults().get(0).getStatus());
    assertEquals(0, testResults.get(0).getStepResults().get(0).getErrors().size());
  }

  @Test
  void testInitSoapUI() throws Exception {
    List<TestsResultsPublisher> publishers = new ArrayList<>();
    String sessionDirectory =
        Thread.currentThread().getContextClassLoader().getResource(".").getPath()
            + "13f28c59-deb5-4a3e-b264-ba4b2dbe7087";
    System.setProperty("session.directory", sessionDirectory);
    System.setProperty("java.app.dir", ".");
    Unzip unzip = new Unzip();
    File archiveFile = new File(sessionDirectory + ".zip");
    unzip.unzip(archiveFile, archiveFile.getParent());
    File projectFolder = new File(sessionDirectory + "/projets/1");
    DriverContext context = new DriverContext();
    context.setProjectDirectory(projectFolder.getParentFile());
    SoapuiExecutor executor = new SoapuiExecutor(context, publishers);
    assertDoesNotThrow(() -> executor.init());
    assertEquals(2, executor.getManager().getInstalledPlugins().size());
  }

  private Properties getProperties(File directoryProjet) throws Exception {
    Properties properties = new Properties();
    for (File file : directoryProjet.listFiles()) {
      if (file.getName().equals("project.properties")) {
        FileInputStream in = null;
        in = new FileInputStream(file);
        properties.load(in);
      }
    }

    return properties;
  }

  @Test
  void testPath() throws Exception {
    File file = new File("C:\\ans-repos\\Platines\\platines-webapp\\driver-soapui\\testfile.txt");
    System.out.println(file.getAbsolutePath());
    System.out.println(file.getParent());
  }
}
