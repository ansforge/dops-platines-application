/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.executor;

import fr.asipsante.platines.model.DriverContext;
import fr.asipsante.platines.model.DriverTestResult;
import fr.asipsante.platines.publisher.TestsResultsPublisher;
import java.io.File;
import java.util.List;
import java.util.Properties;

public abstract class Executor {

  protected static final String PROXY_HOST =
      System.getProperty("http.proxyHost", System.getenv("PROXY_HOST"));
  protected static final String PROXY_PORT =
      System.getProperty("http.proxyPort", System.getenv("PROXY_PORT"));
  protected static final String SESSION_DIRECTORY =
      System.getProperty("session.directory", System.getenv("SESSION_DIRECTORY"));
  protected static final String KEYSTORE_PASSWORD =
      System.getProperty("keystore.password", System.getenv("KEYSTORE_PASSWORD"));
  protected static final String JAVA_APP_DIR =
      System.getProperty("java.app.dir", System.getenv("JAVA_APP_DIR"));

  /** Context du driver. */
  protected DriverContext context;

  /**
   * Liste des publishers sur lesquels seront envoyés les résultats de l'exécution de chaque test.
   */
  protected List<TestsResultsPublisher> iPublishers;

  /** Properties du projet< */
  protected Properties properties;

  /** Résultat du test. */
  protected DriverTestResult driverTestResult;

  public Executor() {
    super();
  }

  public abstract void init();

  public abstract DriverTestResult execute(File project);

  public void publish(DriverTestResult driverTestResult) {
    for (TestsResultsPublisher publisher : iPublishers) {
      publisher.publishProject(driverTestResult);
    }
  }

  /**
   * Get iPublishers.
   *
   * @return
   */
  public List<TestsResultsPublisher> getiPublishers() {
    return iPublishers;
  }

  /**
   * Set iPublishers.
   *
   * @param iPublishers
   */
  public void setiPublishers(List<TestsResultsPublisher> iPublishers) {
    this.iPublishers = iPublishers;
  }

  /**
   * @return the properties
   */
  public Properties getProperties() {
    return properties;
  }

  /**
   * @param properties the properties to set
   */
  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  /**
   * @return the driverTestResult
   */
  public DriverTestResult getDriverTestResult() {
    return driverTestResult;
  }

  /**
   * @param driverTestResult the driverTestResult to set
   */
  public void setDriverTestResult(DriverTestResult driverTestResult) {
    this.driverTestResult = driverTestResult;
  }
}
