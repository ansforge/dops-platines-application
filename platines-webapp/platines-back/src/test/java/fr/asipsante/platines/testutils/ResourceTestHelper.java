/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.testutils;

import java.io.IOException;
import org.apache.commons.io.IOUtils;

/**
 * Utilitaire de test pour récupérer le contenu des ressources dans les tests.
 *
 * @author ericdegenetais
 */
public class ResourceTestHelper {

  private Class<?> tsClass;

  /**
   * @param testSuite référence de la suite de test.
   */
  public ResourceTestHelper(Object testSuite) {
    this.tsClass = testSuite.getClass();
  }

  public byte[] getLocalResourceContent(final String resourceName) throws IOException {
    byte[] content =
        IOUtils.resourceToByteArray(
            "/" + tsClass.getPackageName().replace('.', '/') + "/" + resourceName);
    return content;
  }
}
