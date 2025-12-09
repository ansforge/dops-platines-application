/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.info.BuildProperties;

/**
 * @author edegenetais
 */
public class PlatinesTechDataServiceTest {
  @Test
  public void testNoBuildInfoCase() {
    String actual = new PlatinesTechDataServiceImpl(Optional.empty()).getWebappVersion();
    Assertions.assertEquals("unknown", actual);
  }

  @Test
  public void mustAddBuildTimeToSnapshotVersion() {
    BuildProperties buildInfo = Mockito.mock(BuildProperties.class);
    Mockito.when(buildInfo.getVersion()).thenReturn("2.1.0-SNAPSHOT");
    Mockito.when(buildInfo.getTime()).thenReturn(Instant.parse("2023-08-17T11:10:47.411Z"));
    String actual = new PlatinesTechDataServiceImpl(Optional.of(buildInfo)).getWebappVersion();
    Assertions.assertEquals("2.1.0-SNAPSHOT(2023-08-17T11:10:47.411Z)", actual);
  }

  @Test
  public void noBuildTimeForReleaseVersion() {
    BuildProperties buildInfo = Mockito.mock(BuildProperties.class);
    Mockito.when(buildInfo.getVersion()).thenReturn("2.1.0");
    Mockito.when(buildInfo.getTime()).thenReturn(Instant.parse("2023-08-17T11:10:57.411Z"));
    String actual = new PlatinesTechDataServiceImpl(Optional.of(buildInfo)).getWebappVersion();
    Assertions.assertEquals("2.1.0", actual);
  }
}
