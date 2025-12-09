/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.config;

import fr.asipsante.platines.service.impl.nomad.NomadClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "job.manager", havingValue = "nomad", matchIfMissing = true)
public class NomadConfig {

  @Bean
  public NomadClient nomadClient() {
    String host = System.getenv("NOMAD_IP_http");
    if (host == null) {
      host = System.getProperty("nomad.ip.http", "localhost");
    }
    String port = System.getenv("NOMAD_API_PORT");
    if (port == null) {
      port = System.getProperty("nomad.api.port", "4646");
    }
    return new NomadClient(host, Integer.parseInt(port));
  }
}
