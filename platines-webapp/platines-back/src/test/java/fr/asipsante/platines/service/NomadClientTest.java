/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import fr.ans.platines.nomad.reader.LogStreamResponseProvider;
import fr.asipsante.platines.model.LogFrame;
import fr.asipsante.platines.service.impl.nomad.NomadClient;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@WireMockTest
@Order(100)
public class NomadClientTest {

  private NomadClient nomadClient;

  @Test
  public void streamLogsTest(WireMockRuntimeInfo wmRuntimeInfo) throws Exception {
    nomadClient = new NomadClient("localhost", wmRuntimeInfo.getHttpPort());
    nomadClient.getClient().getHttpClient().register(LogStreamResponseProvider.class);

    LogFrame frame =
        nomadClient.getLogsContent(
            "54aacf90-376f-852a-b0ed-d54a866c1f30", "default", "stderr", 0, "", "start");
    InputStream stream = frame.getFrame();
    String text = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
    assertEquals(55, countLines(text));
  }

  private static int countLines(String str) {
    String[] lines = str.split("\r\n|\r|\n");
    return lines.length;
  }
}
