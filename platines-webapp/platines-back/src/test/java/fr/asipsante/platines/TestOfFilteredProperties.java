/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test filtered properties when feasible.
 *
 * @author edegenetais
 */
public class TestOfFilteredProperties {
  private static final String EXPECTED_ARTIFACT_REPOSITORY_REGEX = "^asip-(snapshots|releases)$";

  @Test
  public void platinesArtifactRepositoryIsInSupportedRange() throws IOException {
    try (InputStream cfgIn = getClass().getResourceAsStream("/test-of-filtered.properties")) {
      Properties cfgProp = new Properties(1);
      cfgProp.load(cfgIn);
      String cfgValue = cfgProp.getProperty("repository.value");
      Assertions.assertTrue(
          cfgValue.matches(EXPECTED_ARTIFACT_REPOSITORY_REGEX),
          "Property value "
              + cfgValue
              + " does not match the expected regex '"
              + EXPECTED_ARTIFACT_REPOSITORY_REGEX
              + "'");
    }
  }
}
