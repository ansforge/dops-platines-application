/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl;

import fr.asipsante.platines.service.PlatinesTechDataService;
import java.util.Optional;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

/**
 * @author edegenetais
 */
@Service("platinesTechData")
public class PlatinesTechDataServiceImpl implements PlatinesTechDataService {

  private BuildProperties buildProperties = null;

  public PlatinesTechDataServiceImpl(Optional<BuildProperties> buildProperties) {
    if (buildProperties.isPresent()) {
      this.buildProperties = buildProperties.get();
    }
  }

  @Override
  public String getWebappVersion() {
    final String version;
    if (buildProperties == null) {
      version = "unknown";
    } else {
      final String buildVersion = buildProperties.getVersion();

      if (buildVersion != null && buildVersion.endsWith("-SNAPSHOT")) {
        version = buildVersion + "(" + buildProperties.getTime() + ")";
      } else {
        version = buildVersion;
      }
    }
    return version;
  }
}
