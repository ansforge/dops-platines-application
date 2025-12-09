/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import fr.asipsante.platines.dto.BulkUpdateReport.UpdateStatus;
import fr.asipsante.platines.dto.FileDownloaded;
import fr.asipsante.platines.testutils.AtLeast;
import fr.asipsante.platines.testutils.ConstantesCommunesTests;
import fr.asipsante.platines.testutils.ResourceTestHelper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author edegenetais
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners({
  DependencyInjectionTestExecutionListener.class,
  DirtiesContextTestExecutionListener.class,
  TransactionalTestExecutionListener.class,
  DbUnitTestExecutionListener.class,
  WithSecurityContextTestExecutionListener.class
})
@ContextConfiguration(locations = {"/app-config.xml"})
@DatabaseSetup(value = ConstantesCommunesTests.REF_JDD_BASE, type = DatabaseOperation.CLEAN_INSERT)
@DatabaseSetup(value = "donnees-projet.xml", type = DatabaseOperation.REFRESH)
@Order(13)
public class ProjectControllerTest {
  private static final String TEST_ATTACHMENT_FILE_MAP =
      "[{\"fileName\": \"test_attachment.txt\", \"fileType\": \"DOCUMENT\"}]";

  private static final String ROR_THEME_ID = "11";
  private static final String THEME_ID_PLATINES = "12";
  private static final long DEFAULT_TESTEE_PROJECT_ID = 10L;

  /** Injecter l'instance de WebApplicationContext. */
  @Autowired private WebApplicationContext webApplicationContext;

  /** Helper popur récupérer le contenu de ressources de test. */
  private ResourceTestHelper rscHelper = new ResourceTestHelper(this);

  /** Sauvegarder l'objet mockMvc. */
  private MockMvc mockMvc;

  /** Initialiser et builde le mockmvc. */
  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void needFilesInRootZipLevel() throws Exception {
    byte[] content = rscHelper.getLocalResourceContent("empty.zip");
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(content)
                .queryParam("themeId", ROR_THEME_ID))
        .andDo(print())
        .andExpect(status().is(400));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void iso8859_1InvalidShouldThrow400() throws Exception {
    byte[] content = rscHelper.getLocalResourceContent("maj_iso8859-1_invalid_structure.zip");
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(content)
                .queryParam("themeId", ROR_THEME_ID))
        .andDo(print())
        .andExpect(status().is(400));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void iso8859_1ValidShouldBeOK() throws Exception {
    byte[] content = rscHelper.getLocalResourceContent("maj_iso8859-1_valid_structure.zip");
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(content)
                .queryParam("themeId", ROR_THEME_ID))
        .andDo(print())
        .andExpect(status().is(202));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void oneXmlFileRootLevelOk() throws Exception {
    byte[] content = rscHelper.getLocalResourceContent("oneproject.zip");
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(content)
                .queryParam("themeId", ROR_THEME_ID))
        .andDo(print())
        .andExpect(status().is(202));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void themeIdQueryParmIsMandatory() throws Exception {
    byte[] content = rscHelper.getLocalResourceContent("oneproject.zip");
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(content))
        .andDo(print())
        .andExpect(status().is(400));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void oneNonProjectFileRootLevelKO() throws Exception {
    byte[] content = rscHelper.getLocalResourceContent("oneNOTproject.zip");
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(content)
                .queryParam("themeId", ROR_THEME_ID))
        .andDo(print())
        .andExpect(status().is(400));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void oneProjectFileRootLevelWithDocInDirOK() throws Exception {
    byte[] content = rscHelper.getLocalResourceContent("oneProjectWithDoc.zip");
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(content)
                .queryParam("themeId", ROR_THEME_ID))
        .andDo(print())
        .andExpect(status().is(202));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void multiDepthDirectoryForbidden() throws Exception {
    byte[] content = rscHelper.getLocalResourceContent("oneProjectWithMultiLevel.zip");
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(content)
                .queryParam("themeId", ROR_THEME_ID))
        .andDo(print())
        .andExpect(status().is(400));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void extraDirectoryForbidden() throws Exception {
    byte[] content = rscHelper.getLocalResourceContent("extradirectoryForbidden.zip");
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(content)
                .queryParam("themeId", ROR_THEME_ID))
        .andDo(print())
        .andExpect(status().is(400));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void caseInsensitiveDuplicateFilesForbidden() throws Exception {
    byte[] content =
        rscHelper.getLocalResourceContent("caseInsensitiveDuplicatesForbbidden_file.zip");
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(content)
                .queryParam("themeId", ROR_THEME_ID))
        .andDo(print())
        .andExpect(status().is(400));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void caseInsensitiveDuplicateDirForbidden() throws Exception {
    byte[] content =
        rscHelper.getLocalResourceContent("caseInsensitiveDuplicatesForbbidden_dir.zip");
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(content)
                .queryParam("themeId", ROR_THEME_ID))
        .andDo(print())
        .andExpect(status().is(400));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void testGetAll() throws Exception {
    mockMvc
        .perform(get("/secure/project/getAll"))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.length()")
                .value(
                    AtLeast.atLeast(
                        4))); // les trois projets du JDD projet + le projet id=1 du JDD de base
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void testProjectFileUpdateInProject() throws Exception {

    String projectString =
        mockMvc
            .perform(get("/secure/project/get/" + DEFAULT_TESTEE_PROJECT_ID))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.fileName")
                    .value("<null>")) // Sanity test : rien avant
            .andReturn()
            .getResponse()
            .getContentAsString();

    final String fileName = "project1.readyAPI.xml";
    byte[] projectFile = rscHelper.getLocalResourceContent(fileName);
    MockMultipartFile projectFileMP =
        new MockMultipartFile(
            "projectFile", fileName, MediaType.APPLICATION_XML_VALUE, projectFile);

    mockMvc
        .perform(
            multipart("/secure/project/update")
                .file(projectFileMP)
                .part(
                    new MockPart("project", projectString.getBytes()),
                    new MockPart("mapFile", "[]".getBytes())))
        .andExpect(status().is2xxSuccessful());

    byte[] actualProjectFile = retrieveFileFromProject(DEFAULT_TESTEE_PROJECT_ID, fileName);

    Assertions.assertArrayEquals(
        projectFile, actualProjectFile, "Project file content not updated");
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void testBulkUpdateOfProjectFileInOne() throws Exception {

    // 1 - conditionnement : injection du fichier initial
    String projectString =
        mockMvc
            .perform(get("/secure/project/get/" + DEFAULT_TESTEE_PROJECT_ID))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final String fileName = "project1.readyAPI.xml";
    byte[] projectFile = rscHelper.getLocalResourceContent(fileName);
    MockMultipartFile projectFileMP =
        new MockMultipartFile(
            "projectFile",
            "project1_up.readyAPI.xml",
            MediaType.APPLICATION_XML_VALUE,
            projectFile);

    mockMvc
        .perform(
            multipart("/secure/project/update")
                .file(projectFileMP)
                .part(
                    new MockPart("project", projectString.getBytes()),
                    new MockPart("mapFile", "[]".getBytes())))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

    // 2 - envoi de l'archive pour mise à jour
    final String archiveName = "miseAjourFichierProjet1.zip";
    byte[] archive = rscHelper.getLocalResourceContent(archiveName);
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(archive)
                .queryParam("themeId", ROR_THEME_ID))
        .andDo(print())
        .andExpect(status().is(202));

    // 3 - Vérification
    byte[] expectedProjectFile =
        retrieveFromZipArchive(
            rscHelper.getLocalResourceContent(archiveName), "project1_up.readyAPI.xml");
    byte[] actualProjectFile =
        retrieveFileFromProject(DEFAULT_TESTEE_PROJECT_ID, "project1_up.readyAPI.xml");
    Assertions.assertArrayEquals(expectedProjectFile, actualProjectFile);
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void wrongThemeDoesNotUpdate() throws Exception {
    final String chosenThemeFilter =
        THEME_ID_PLATINES; // Ce n'est pas le theme du projet 1 => pas d'update

    // 1 - conditionnement : injection du fichier initial
    String projectString =
        mockMvc
            .perform(get("/secure/project/get/" + DEFAULT_TESTEE_PROJECT_ID))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final String fileName = "project1.readyAPI.xml";
    byte[] projectFile = rscHelper.getLocalResourceContent(fileName);
    MockMultipartFile projectFileMP =
        new MockMultipartFile(
            "projectFile",
            "project1_up.readyAPI.xml",
            MediaType.APPLICATION_XML_VALUE,
            projectFile);

    mockMvc
        .perform(
            multipart("/secure/project/update")
                .file(projectFileMP)
                .part(
                    new MockPart("project", projectString.getBytes()),
                    new MockPart("mapFile", "[]".getBytes())))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

    // 2 - envoi de l'archive pour mise à jour
    final String archiveName = "miseAjourFichierProjet1.zip";
    byte[] archive = rscHelper.getLocalResourceContent(archiveName);
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(archive)
                .queryParam("themeId", chosenThemeFilter))
        .andDo(print())
        .andExpect(status().is(202));

    // 3 - Vérification : le contenu du fichier projet doit rester identique au contenu initial
    // injecté en 1
    byte[] actualProjectFile =
        retrieveFileFromProject(DEFAULT_TESTEE_PROJECT_ID, "project1_up.readyAPI.xml");
    Assertions.assertArrayEquals(projectFile, actualProjectFile);
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  @DatabaseSetup(value = "donnees-projet-select-on-date.xml", type = DatabaseOperation.UPDATE)
  public void ifTwoInThemeChooseTheLastUpdated() throws Exception {

    // 1 - attempt bulk update
    final String archiveName = "miseAjourFichierProjet1.zip";
    byte[] archive = rscHelper.getLocalResourceContent(archiveName);
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(archive)
                .queryParam("themeId", THEME_ID_PLATINES))
        .andDo(print())
        .andExpect(status().is(202));

    // 2 checks
    byte[] expectedProjectFile =
        retrieveFromZipArchive(
            rscHelper.getLocalResourceContent(archiveName), "project1_up.readyAPI.xml");
    byte[] actualProjectFile = retrieveFileFromProject(3L, "project1_up.readyAPI.xml");
    Assertions.assertArrayEquals(
        expectedProjectFile, actualProjectFile, "Project 3 should have been updated, but was not.");

    mockMvc
        .perform(get("/secure/project/download/" + 2L))
        .andDo(print())
        .andExpect(status().is5xxServerError()); // Without file, the file download should fail
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  @DatabaseSetup(
      value = "donnees-projet-different-properties.xml",
      type = DatabaseOperation.REFRESH)
  public void newPropertyDoesNotUpdate() throws Exception {

    // 1 - conditionnement : injection du fichier initial
    String projectString =
        mockMvc
            .perform(get("/secure/project/get/" + DEFAULT_TESTEE_PROJECT_ID))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final String fileName = "project1.readyAPI.xml";
    byte[] projectFile = rscHelper.getLocalResourceContent(fileName);
    MockMultipartFile projectFileMP =
        new MockMultipartFile(
            "projectFile",
            "project1_up.readyAPI.xml",
            MediaType.APPLICATION_XML_VALUE,
            projectFile);

    mockMvc
        .perform(
            multipart("/secure/project/update")
                .file(projectFileMP)
                .part(
                    new MockPart("project", projectString.getBytes()),
                    new MockPart("mapFile", "[]".getBytes())))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

    // 2 - envoi de l'archive pour mise à jour
    final String archiveName = "miseAjourFichierProjet1.zip";
    byte[] archive = rscHelper.getLocalResourceContent(archiveName);
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(archive)
                .queryParam("themeId", ROR_THEME_ID))
        .andDo(print())
        .andExpect(status().is(202));

    // 3 - Vérification : le contenu du fichier projet doit rester identique au contenu initial
    // injecté en 1
    byte[] actualProjectFile =
        retrieveFileFromProject(DEFAULT_TESTEE_PROJECT_ID, "project1_up.readyAPI.xml");
    Assertions.assertArrayEquals(projectFile, actualProjectFile);
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void reportShowsUpdatedOKIfOK() throws Exception {

    // 1 - conditionnement : injection du fichier initial
    String projectString =
        mockMvc
            .perform(get("/secure/project/get/" + DEFAULT_TESTEE_PROJECT_ID))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final String fileName = "project1.readyAPI.xml";
    byte[] projectFile = rscHelper.getLocalResourceContent(fileName);
    MockMultipartFile projectFileMP =
        new MockMultipartFile(
            "projectFile",
            "project1_up.readyAPI.xml",
            MediaType.APPLICATION_XML_VALUE,
            projectFile);

    mockMvc
        .perform(
            multipart("/secure/project/update")
                .file(projectFileMP)
                .part(
                    new MockPart("project", projectString.getBytes()),
                    new MockPart("mapFile", "[]".getBytes())))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

    // 2 - envoi de l'archive pour mise à jour
    final String archiveName = "miseAjourFichierProjet1.zip";
    byte[] archive = rscHelper.getLocalResourceContent(archiveName);
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(archive)
                .queryParam("themeId", ROR_THEME_ID))
        .andDo(print())
        .andExpect(status().is(202))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.reportLines[0].status")
                .value(UpdateStatus.OK.name()));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void reportShowsREJECTEDIfNotFound() throws Exception {
    final String themeId = THEME_ID_PLATINES; // Empêchera de trouver le projet

    // 1 - conditionnement : injection du fichier initial
    String projectString =
        mockMvc
            .perform(get("/secure/project/get/" + DEFAULT_TESTEE_PROJECT_ID))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final String fileName = "project1.readyAPI.xml";
    byte[] projectFile = rscHelper.getLocalResourceContent(fileName);
    MockMultipartFile projectFileMP =
        new MockMultipartFile(
            "projectFile",
            "project1_up.readyAPI.xml",
            MediaType.APPLICATION_XML_VALUE,
            projectFile);

    mockMvc
        .perform(
            multipart("/secure/project/update")
                .file(projectFileMP)
                .part(
                    new MockPart("project", projectString.getBytes()),
                    new MockPart("mapFile", "[]".getBytes())))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

    // 2 - envoi de l'archive pour mise à jour
    final String archiveName = "miseAjourFichierProjet1.zip";
    byte[] archive = rscHelper.getLocalResourceContent(archiveName);
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(archive)
                .queryParam("themeId", themeId))
        .andDo(print())
        .andExpect(status().is(202))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.reportLines[0].status")
                .value(UpdateStatus.REJECTED.name()));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  @DatabaseSetup(value = "donnees-projet-select-on-date.xml", type = DatabaseOperation.UPDATE)
  public void reportShowsWARNINGIfSelectedByDate() throws Exception {
    final String themeId = THEME_ID_PLATINES; // Empêchera de trouver le projet

    // 1 - conditionnement : injection du fichier initial
    String projectString =
        mockMvc
            .perform(get("/secure/project/get/" + DEFAULT_TESTEE_PROJECT_ID))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final String fileName = "project1.readyAPI.xml";
    byte[] projectFile = rscHelper.getLocalResourceContent(fileName);
    MockMultipartFile projectFileMP =
        new MockMultipartFile(
            "projectFile",
            "project1_up.readyAPI.xml",
            MediaType.APPLICATION_XML_VALUE,
            projectFile);

    mockMvc
        .perform(
            multipart("/secure/project/update")
                .file(projectFileMP)
                .part(
                    new MockPart("project", projectString.getBytes()),
                    new MockPart("mapFile", "[]".getBytes())))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

    // 2 - envoi de l'archive pour mise à jour
    final String archiveName = "miseAjourFichierProjet1.zip";
    byte[] archive = rscHelper.getLocalResourceContent(archiveName);
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(archive)
                .queryParam("themeId", themeId))
        .andDo(print())
        .andExpect(status().is(202))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.reportLines[0].status")
                .value(UpdateStatus.WARNING.name()));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void okDryRunShowsReportOK() throws Exception {

    // 1 - conditionnement : injection du fichier initial
    String projectString =
        mockMvc
            .perform(get("/secure/project/get/" + DEFAULT_TESTEE_PROJECT_ID))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final String fileName = "project1.readyAPI.xml";
    byte[] projectFile = rscHelper.getLocalResourceContent(fileName);
    MockMultipartFile projectFileMP =
        new MockMultipartFile(
            "projectFile",
            "project1_up.readyAPI.xml",
            MediaType.APPLICATION_XML_VALUE,
            projectFile);

    mockMvc
        .perform(
            multipart("/secure/project/update")
                .file(projectFileMP)
                .part(
                    new MockPart("project", projectString.getBytes()),
                    new MockPart("mapFile", "[]".getBytes())))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

    // 2 - envoi de l'archive pour mise à jour
    final String archiveName = "miseAjourFichierProjet1.zip";
    byte[] archive = rscHelper.getLocalResourceContent(archiveName);
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(archive)
                .queryParam("themeId", ROR_THEME_ID)
                .queryParam("dryRun", "true"))
        .andDo(print())
        .andExpect(status().is(202))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.reportLines[0].status")
                .value(UpdateStatus.OK.name()));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void okDryRunShowsNoDbChange() throws Exception {

    // 1 - conditionnement : injection du fichier initial
    String projectString =
        mockMvc
            .perform(get("/secure/project/get/" + DEFAULT_TESTEE_PROJECT_ID))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final String fileName = "project1.readyAPI.xml";
    byte[] projectFile = rscHelper.getLocalResourceContent(fileName);
    MockMultipartFile projectFileMP =
        new MockMultipartFile(
            "projectFile",
            "project1_up.readyAPI.xml",
            MediaType.APPLICATION_XML_VALUE,
            projectFile);

    mockMvc
        .perform(
            multipart("/secure/project/update")
                .file(projectFileMP)
                .part(
                    new MockPart("project", projectString.getBytes()),
                    new MockPart("mapFile", "[]".getBytes())))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

    // 2 - envoi de l'archive pour mise à jour
    final String archiveName = "miseAjourFichierProjet1.zip";
    byte[] archive = rscHelper.getLocalResourceContent(archiveName);
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(archive)
                .queryParam("themeId", ROR_THEME_ID)
                .queryParam("dryRun", "true"))
        .andDo(print())
        .andExpect(status().is(202));

    // 3 Check that db didn't change
    byte[] actualProjectFile =
        retrieveFileFromProject(DEFAULT_TESTEE_PROJECT_ID, "project1_up.readyAPI.xml");
    Assertions.assertArrayEquals(projectFile, actualProjectFile);
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  @DatabaseSetup(value = "donnees-projet-select-on-date.xml", type = DatabaseOperation.UPDATE)
  public void dryRunWARNINGIfSelectedByDate() throws Exception {
    final String themeId = THEME_ID_PLATINES; // Empêchera de trouver le projet

    // 1 - conditionnement : injection du fichier initial
    String projectString =
        mockMvc
            .perform(get("/secure/project/get/" + DEFAULT_TESTEE_PROJECT_ID))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final String fileName = "project1.readyAPI.xml";
    byte[] projectFile = rscHelper.getLocalResourceContent(fileName);
    MockMultipartFile projectFileMP =
        new MockMultipartFile(
            "projectFile",
            "project1_up.readyAPI.xml",
            MediaType.APPLICATION_XML_VALUE,
            projectFile);

    mockMvc
        .perform(
            multipart("/secure/project/update")
                .file(projectFileMP)
                .part(
                    new MockPart("project", projectString.getBytes()),
                    new MockPart("mapFile", "[]".getBytes())))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

    // 2 - envoi de l'archive pour mise à jour
    final String archiveName = "miseAjourFichierProjet1.zip";
    byte[] archive = rscHelper.getLocalResourceContent(archiveName);
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(archive)
                .queryParam("themeId", themeId)
                .queryParam("dryRun", "true"))
        .andDo(print())
        .andExpect(status().is(202))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.reportLines[0].status")
                .value(UpdateStatus.WARNING.name()));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void dryRunREJECTEDIfNotFound() throws Exception {
    final String themeId = THEME_ID_PLATINES; // Empêchera de trouver le projet

    // 1 - conditionnement : injection du fichier initial
    String projectString =
        mockMvc
            .perform(get("/secure/project/get/" + DEFAULT_TESTEE_PROJECT_ID))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final String fileName = "project1.readyAPI.xml";
    byte[] projectFile = rscHelper.getLocalResourceContent(fileName);
    MockMultipartFile projectFileMP =
        new MockMultipartFile(
            "projectFile",
            "project1_up.readyAPI.xml",
            MediaType.APPLICATION_XML_VALUE,
            projectFile);

    mockMvc
        .perform(
            multipart("/secure/project/update")
                .file(projectFileMP)
                .part(
                    new MockPart("project", projectString.getBytes()),
                    new MockPart("mapFile", "[]".getBytes())))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

    // 2 - envoi de l'archive pour mise à jour
    final String archiveName = "miseAjourFichierProjet1.zip";
    byte[] archive = rscHelper.getLocalResourceContent(archiveName);
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(archive)
                .queryParam("themeId", themeId)
                .queryParam("dryRun", "true"))
        .andDo(print())
        .andExpect(status().is(202))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.reportLines[0].status")
                .value(UpdateStatus.REJECTED.name()));
  }

  @Test
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void reportShowsREJECTEDIfAssociatedFileNotFound() throws Exception {
    final String themeId = ROR_THEME_ID;

    // 1 - conditionnement : injection du fichier projet initial
    String projectString =
        mockMvc
            .perform(get("/secure/project/get/" + DEFAULT_TESTEE_PROJECT_ID))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final String fileName = "project1.readyAPI.xml";
    byte[] projectFile = rscHelper.getLocalResourceContent(fileName);
    MockMultipartFile projectFileMP =
        new MockMultipartFile(
            "projectFile",
            "project1_up.readyAPI.xml",
            MediaType.APPLICATION_XML_VALUE,
            projectFile);

    mockMvc
        .perform(
            multipart("/secure/project/update")
                .file(projectFileMP)
                .part(
                    new MockPart("project", projectString.getBytes()),
                    new MockPart("mapFile", "[]".getBytes())))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

    // 2 - envoi de l'archive pour mise à jour
    final String archiveName = "miseAjourFichierProjet1_et_fichier.zip";
    byte[] archive = rscHelper.getLocalResourceContent(archiveName);
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(archive)
                .queryParam("themeId", themeId))
        .andDo(print())
        .andExpect(status().is(202))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.reportLines[0].status")
                .value(UpdateStatus.REJECTED.name()));
  }

  @Test
  @DatabaseSetup(value = "donnees-projet-related-file.xml", type = DatabaseOperation.REFRESH)
  @DatabaseTearDown(value = "donnees-projet-related-file.xml", type = DatabaseOperation.DELETE)
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void reportShowsOKIfAssociatedFileFound() throws Exception {
    final String themeId = ROR_THEME_ID;

    // 1 - conditionnement : injection du fichier projet initial
    String projectString =
        mockMvc
            .perform(get("/secure/project/get/" + DEFAULT_TESTEE_PROJECT_ID))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final String fileName = "project1.readyAPI.xml";
    byte[] projectFile = rscHelper.getLocalResourceContent(fileName);
    MockMultipartFile projectFileMP =
        new MockMultipartFile(
            "projectFile",
            "project1_up.readyAPI.xml",
            MediaType.APPLICATION_XML_VALUE,
            projectFile);
    MockMultipartFile relatedFile =
        new MockMultipartFile(
            "relatedFiles",
            "test_attachment.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "InitalValue".getBytes());

    mockMvc
        .perform(
            multipart("/secure/project/update")
                .file(projectFileMP)
                .file(relatedFile)
                .part(
                    new MockPart("project", projectString.getBytes()),
                    new MockPart("mapFile", TEST_ATTACHMENT_FILE_MAP.getBytes())))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

    // 2 - envoi de l'archive pour mise à jour
    final String archiveName = "miseAjourFichierProjet1_et_fichier.zip";
    byte[] archive = rscHelper.getLocalResourceContent(archiveName);
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(archive)
                .queryParam("themeId", themeId))
        .andDo(print())
        .andExpect(status().is(202))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.reportLines[0].status")
                .value(UpdateStatus.OK.name()));
  }

  @Test
  @DatabaseSetup(value = "donnees-projet-related-file.xml", type = DatabaseOperation.REFRESH)
  @DatabaseTearDown(value = "donnees-projet-related-file.xml", type = DatabaseOperation.DELETE)
  @WithUserDetails(value = "admin@admin", userDetailsServiceBeanName = "myUserDetails")
  public void updateAssociatedFileIfFound() throws Exception {
    final String themeId = ROR_THEME_ID;

    // 1 - conditionnement : injection du fichier projet initial
    String projectString =
        mockMvc
            .perform(get("/secure/project/get/" + DEFAULT_TESTEE_PROJECT_ID))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();

    final String fileName = "project1.readyAPI.xml";
    byte[] projectFile = rscHelper.getLocalResourceContent(fileName);
    MockMultipartFile projectFileMP =
        new MockMultipartFile(
            "projectFile",
            "project1_up.readyAPI.xml",
            MediaType.APPLICATION_XML_VALUE,
            projectFile);
    MockMultipartFile relatedFile =
        new MockMultipartFile(
            "relatedFiles",
            "test_attachment.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "InitalValue".getBytes());

    mockMvc
        .perform(
            multipart("/secure/project/update")
                .file(projectFileMP)
                .file(relatedFile)
                .part(
                    new MockPart("project", projectString.getBytes()),
                    new MockPart("mapFile", TEST_ATTACHMENT_FILE_MAP.getBytes())))
        .andDo(print())
        .andExpect(status().is2xxSuccessful());

    // 2 - envoi de l'archive pour mise à jour
    final String archiveName = "miseAjourFichierProjet1_et_fichier.zip";
    byte[] archive = rscHelper.getLocalResourceContent(archiveName);
    mockMvc
        .perform(
            post("/secure/project/update/bulk")
                .accept(MediaType.APPLICATION_JSON)
                .contentType("application/zip")
                .content(archive)
                .queryParam("themeId", themeId))
        .andDo(print())
        .andExpect(status().is(202));

    // 3 Check that db did change
    final byte[] fileData =
        retrieveFileFromProject(DEFAULT_TESTEE_PROJECT_ID, "test_attachment.txt");
    String actualProjectFile = fileData == null ? null : new String(fileData, "UTF-8");
    Assertions.assertEquals("Fichier attaché de test\n", actualProjectFile);
  }

  private byte[] retrieveFileFromProject(final long projectId, final String fileName)
      throws Exception {
    String projectFileMapString =
        mockMvc
            .perform(get("/secure/project/download/" + projectId))
            .andDo(print())
            .andExpect(status().is2xxSuccessful())
            .andReturn()
            .getResponse()
            .getContentAsString();
    FileDownloaded projectFileMap =
        new ObjectMapper().readValue(projectFileMapString, FileDownloaded.class);
    byte[] projectFileZip = projectFileMap.getFile();
    return retrieveFromZipArchive(projectFileZip, fileName);
  }

  private byte[] retrieveFromZipArchive(byte[] projectFileZip, final String fileName)
      throws IOException {
    byte[] actualProjectFile = null;
    try (ZipInputStream zipIs = new ZipInputStream(new ByteArrayInputStream(projectFileZip))) {

      for (ZipEntry e = zipIs.getNextEntry(); e != null; e = zipIs.getNextEntry()) {
        if (e.getName().equals(fileName)) {
          actualProjectFile = zipIs.readAllBytes();
        }
      }
    }
    return actualProjectFile;
  }
}
