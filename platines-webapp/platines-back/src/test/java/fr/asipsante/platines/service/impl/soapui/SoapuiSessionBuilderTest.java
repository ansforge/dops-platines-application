/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.soapui;

import static org.junit.Assert.assertEquals;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import fr.asipsante.platines.dto.ApplicationDto;
import fr.asipsante.platines.dto.ProjectResultDto;
import fr.asipsante.platines.dto.SessionParameters;
import fr.asipsante.platines.dto.SimulatedServiceDto;
import fr.asipsante.platines.dto.TestSessionDto;
import fr.asipsante.platines.dto.ThemeDto;
import fr.asipsante.platines.dto.VersionDto;
import fr.asipsante.platines.entity.Project;
import fr.asipsante.platines.entity.RelatedFiles;
import fr.asipsante.platines.entity.enums.FileType;
import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.service.SessionBuilder;
import fr.asipsante.platines.testutils.ConstantesCommunesTests;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration({"/app-config.xml"})
@TestExecutionListeners({
  DependencyInjectionTestExecutionListener.class,
  DirtiesContextTestExecutionListener.class,
  TransactionalTestExecutionListener.class,
  DbUnitTestExecutionListener.class,
  WithSecurityContextTestExecutionListener.class,
  MockitoTestExecutionListener.class,
  ResetMocksTestExecutionListener.class
})
@SpyBean(SoapuiSessionBuilder.class)
@DatabaseSetup(value = ConstantesCommunesTests.REF_JDD_BASE, type = DatabaseOperation.CLEAN_INSERT)
@DatabaseSetup(value = "../../data-session.xml", type = DatabaseOperation.REFRESH)
@Order(4)
public class SoapuiSessionBuilderTest {

  /** The LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(SoapuiSessionBuilderTest.class);

  @Autowired private SessionBuilder sessionBuilder;

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void testCreateServeurSession() throws Exception {
    logMemory();
    System.setProperty("webapps.folder", ".");
    Project project = new Project();
    project.setId(1l);
    project.setFile(
        this.getClass()
            .getResourceAsStream("../../TestPropertiesProject-readyapi-project.xml")
            .readAllBytes());
    project.setFileName("TestPropertiesProject-readyapi-project.xml");
    byte[] jarContent =
        Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("lib/soapui-plugin-vihf-1.6-RELEASE.jar")
            .readAllBytes();
    RelatedFiles jarFile = new RelatedFiles();
    jarFile.setFileName("soapui-plugin-vihf-1.6-RELEASE.jar");
    jarFile.setFile(jarContent);
    jarFile.setFileType(FileType.SINGLE_JAR);
    jarFile.setId(1L);
    ArrayList<RelatedFiles> jars = new ArrayList<>();
    jars.add(jarFile);
    project.setRelatedFiles(jars);
    Mockito.doReturn(project).when(sessionBuilder).getProjectById(Mockito.anyLong());
    TestSessionDto session = new TestSessionDto();
    List<ProjectResultDto> results = new ArrayList<>();
    ProjectResultDto result = new ProjectResultDto();
    result.setProjectProperties(Collections.emptyList());
    result.setIdProject(1l);
    result.setId(1l);
    results.add(result);
    VersionDto version = new VersionDto();
    SimulatedServiceDto service = new SimulatedServiceDto();
    ThemeDto family = new ThemeDto();
    ApplicationDto application = new ApplicationDto();
    application.setId(1l);
    application.setDescription("desc");
    application.setName("name");
    application.setRole(Role.SERVER);
    application.setUrl("http://url");
    application.setVersion("v1");
    family.setId(1l);
    service.setTheme(family);
    service.setId(1l);
    version.setId(3l);
    version.setService(service);
    session.setId(1l);
    String sessionuuid = UUID.randomUUID().toString();
    session.setUuid(sessionuuid);
    session.setVersion(version);
    session.setProjectResults(results);
    session.setApplication(application);
    SessionParameters sessionParams = sessionBuilder.createServeurSession(session);
    logMemory();
    Path p = Paths.get(sessionParams.getFileArtifact());
    assertEquals(sessionuuid + ".war.zip", p.getFileName().toString());
    Files.delete(p);
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void testCreateClientSession() throws Exception {
    logMemory();
    System.setProperty("webapps.folder", ".");
    Project project = new Project();
    project.setId(1l);
    project.setFile(
        this.getClass()
            .getResourceAsStream("../../Nested-data-source-loop-project.xml")
            .readAllBytes());
    project.setFileName("Nested-data-source-loop-project.xml");
    project.setRelatedFiles(Collections.emptyList());
    Mockito.doReturn(project).when(sessionBuilder).getProjectById(Mockito.anyLong());
    TestSessionDto session = new TestSessionDto();
    List<ProjectResultDto> results = new ArrayList<>();
    ProjectResultDto result = new ProjectResultDto();
    result.setProjectProperties(Collections.emptyList());
    result.setIdProject(1l);
    result.setId(1l);
    results.add(result);
    VersionDto version = new VersionDto();
    SimulatedServiceDto service = new SimulatedServiceDto();
    ThemeDto family = new ThemeDto();
    ApplicationDto application = new ApplicationDto();
    application.setId(1l);
    application.setDescription("desc");
    application.setName("name");
    application.setRole(Role.SERVER);
    application.setUrl("http://url");
    application.setVersion("v1");
    family.setId(1l);
    service.setTheme(family);
    service.setId(1l);
    version.setId(3l);
    version.setService(service);
    session.setId(1l);
    String sessionuuid = UUID.randomUUID().toString();
    session.setUuid(sessionuuid);
    session.setVersion(version);
    session.setProjectResults(results);
    session.setApplication(application);
    SessionParameters sessionParams = sessionBuilder.createClientSession(session);
    logMemory();
    Path p = Paths.get(sessionParams.getFileArtifact());
    assertEquals(sessionuuid + ".zip", p.getFileName().toString());
    Files.delete(p);
  }

  @ParameterizedTest
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  @CsvSource(
      value = {
        "VIHF plugin:.*PLUGIN.*VIHF.*\\.JAR",
        "Schematron plugin:.*PLUGIN.*SCHEMATRON.*\\.JAR"
      },
      delimiter = ':')
  public void testClientSessionFindPlugin(final String nomPlugin, final String nameRegex)
      throws Exception {
    logMemory();
    System.setProperty("webapps.folder", ".");
    Project project = new Project();
    project.setId(1l);
    project.setFile(
        this.getClass()
            .getResourceAsStream("../../Nested-data-source-loop-project.xml")
            .readAllBytes());
    project.setFileName("Nested-data-source-loop-project.xml");
    project.setRelatedFiles(Collections.emptyList());
    Mockito.doReturn(project).when(sessionBuilder).getProjectById(Mockito.anyLong());
    TestSessionDto session = new TestSessionDto();
    List<ProjectResultDto> results = new ArrayList<>();
    ProjectResultDto result = new ProjectResultDto();
    result.setProjectProperties(Collections.emptyList());
    result.setIdProject(1l);
    result.setId(1l);
    results.add(result);
    VersionDto version = new VersionDto();
    SimulatedServiceDto service = new SimulatedServiceDto();
    ThemeDto family = new ThemeDto();
    ApplicationDto application = new ApplicationDto();
    application.setId(1l);
    application.setDescription("desc");
    application.setName("name");
    application.setRole(Role.SERVER);
    application.setUrl("http://url");
    application.setVersion("v1");
    family.setId(1l);
    service.setTheme(family);
    service.setId(1l);
    version.setId(3l);
    version.setService(service);
    session.setId(1l);
    String sessionuuid = UUID.randomUUID().toString();
    session.setUuid(sessionuuid);
    session.setVersion(version);
    session.setProjectResults(results);
    session.setApplication(application);
    SessionParameters sessionParams = sessionBuilder.createClientSession(session);
    logMemory();
    Path p = Paths.get(sessionParams.getFileArtifact());
    assertFoundInArchive(p, nameRegex, nomPlugin);
  }

  private void assertFoundInArchive(
      Path archivePath, final String nameRegex, final String searchTargetName) throws IOException {
    try (FileInputStream zfis = new FileInputStream(archivePath.toFile());
        ZipInputStream zis = new ZipInputStream(zfis); ) {
      boolean found = false;
      for (ZipEntry ze = zis.getNextEntry(); !found && ze != null; ze = zis.getNextEntry()) {
        if (!ze.isDirectory()
            && new File(ze.getName()).getName().toUpperCase().matches(nameRegex)) {
          found = true;
        }
      }
      Assertions.assertTrue(found, searchTargetName + " not found.");
    } finally {
      Files.delete(archivePath);
    }
  }

  private void logMemory() {
    LOGGER.info("Max Memory: {} Mb", Runtime.getRuntime().maxMemory() / 1048576);
    LOGGER.info("Total Memory: {} Mb", Runtime.getRuntime().totalMemory() / 1048576);
    LOGGER.info("Free Memory: {} Mb", Runtime.getRuntime().freeMemory() / 1048576);
  }
}
