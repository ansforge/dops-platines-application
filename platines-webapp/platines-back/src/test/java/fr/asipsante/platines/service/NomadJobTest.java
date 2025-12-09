/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.junit.Assert.assertEquals;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import fr.ans.platines.nomad.reader.LogStreamResponseProvider;
import fr.asipsante.platines.dto.SessionParameters;
import fr.asipsante.platines.entity.JobStatus;
import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.exception.ServiceException;
import fr.asipsante.platines.model.LogFrame;
import fr.asipsante.platines.service.impl.nomad.NomadClient;
import fr.asipsante.platines.testutils.ConstantesCommunesTests;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * @author aboittiaux
 */
@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration({"/app-config.xml"})
@WireMockTest
@TestExecutionListeners({
  DependencyInjectionTestExecutionListener.class,
  DirtiesContextTestExecutionListener.class,
  TransactionalTestExecutionListener.class,
  DbUnitTestExecutionListener.class,
  WithSecurityContextTestExecutionListener.class
})
@DatabaseSetup(value = ConstantesCommunesTests.REF_JDD_BASE, type = DatabaseOperation.CLEAN_INSERT)
@DatabaseSetup(
    value = "/fr/asipsante/platines/service/session_tst.xml",
    type = DatabaseOperation.REFRESH)
@Order(1)
public class NomadJobTest {

  @Autowired
  @Qualifier("sessionService")
  private SessionService sessionService;

  @Autowired private JobManager manager;

  @Autowired private NomadClient nomad;

  @BeforeAll
  public static void init(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
    /*
     * TODO : remplacer cette méthode par un service Spring en dépendance des tests wiremock
     * pour se débnarrasser de la dépendance à l'ordre des tests.
     */
    // wireMock rules in resource/mappings/scenarios
    System.setProperty("nomad.ip.http", "localhost");
    System.setProperty("nomad.api.port", Integer.toString(wmRuntimeInfo.getHttpPort()));
    System.setProperty("consul.port", Integer.toString(wmRuntimeInfo.getHttpPort()));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void deploySuccess(WireMockRuntimeInfo wmRuntimeInfo) {
    sessionService.initExecutionSession((long) 1);
    SessionParameters sessionParameters = new SessionParameters("test.war.zip", "keystore.p12");
    sessionParameters.setApplicationRole(Role.CLIENT);
    sessionParameters.setMTls(false);
    sessionParameters.setAuthorizedIP("ip");
    sessionParameters.setUuidSession("d8226912-7139-4f1d-a2b2-a0ac9d8eecb4");
    manager.submitJob(sessionParameters);
    assertEquals("Le job est déployé avec succès ", JobStatus.RUNNING, manager.getJobStatus());
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void deployedBlocked(WireMockRuntimeInfo wmRuntimeInfo) {
    stubFor(
        post("/v1/jobs")
            .willReturn(
                aResponse()
                    .withBody(
                        "{\"EvalID\":"
                            + " \"7525163d-4938-1621-2b00-a9eb8c2350a678\",\"EvalCreateIndex\":"
                            + " 0,\"JobModifyIndex\": 109,\"Warnings\": \"\",\"Index\":"
                            + " 0,\"LastContact\": 0,\"KnownLeader\": false}\"")));

    SessionParameters sessionParams = new SessionParameters("test.war.zip", "keystore.p12");
    sessionParams.setApplicationRole(Role.CLIENT);
    sessionParams.setAuthorizedIP("ip");
    sessionParams.setMTls(false);
    sessionParams.setUuidSession("d8226912-7139-4f1d-a2b2-a0ac9d8eecb4");
    manager.submitJob(sessionParams);
    assertEquals("Le job est déployé avec succès ", JobStatus.RUNNING, manager.getJobStatus());
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void deployedFailed(WireMockRuntimeInfo wmRuntimeInfo) {
    Assertions.assertThrows(
        ServiceException.class,
        () -> {
          SessionParameters sessionParams = new SessionParameters("test.war.zip", "keystore.p12");
          sessionParams.setApplicationRole(Role.CLIENT);
          sessionParams.setUuidSession("evaluation_failed");
          sessionParams.setAuthorizedIP("ip");
          sessionParams.setMTls(false);
          manager.submitJob(sessionParams);
        });
  }

  @Test
  public void streamConnectionLogsTest() throws Exception {
    nomad.getClient().getHttpClient().register(LogStreamResponseProvider.class);
    SessionParameters sessionParameters = new SessionParameters("test.war.zip", "keystore.p12");
    sessionParameters.setApplicationRole(Role.CLIENT);
    sessionParameters.setMTls(false);
    sessionParameters.setAuthorizedIP("ip");
    sessionParameters.setUuidSession("d8226912-7139-4f1d-a2b2-a0ac9d8eecb4");
    manager.submitJob(sessionParameters);
    Map<String, LogFrame> logs = manager.streamConnectionLogs(StreamType.STDERR, false);
    LogFrame frame = logs.get("7a7a46ab-4bb5-fa6f-61fd-e5537ee480cd");
    String content = new String(frame.getFrame().readAllBytes());
    assertEquals(140, content.length());
    // Plus de changement dans les logs pour les prochains appels
    Map<String, LogFrame> logs2 = manager.streamConnectionLogs(StreamType.STDERR, false);
    assertEquals(0, logs2.get("7a7a46ab-4bb5-fa6f-61fd-e5537ee480cd").getOffset());

    Map<String, LogFrame> logs3 = manager.streamConnectionLogs(StreamType.STDERR, false);
    assertEquals(0, logs3.get("7a7a46ab-4bb5-fa6f-61fd-e5537ee480cd").getOffset());
  }
}
