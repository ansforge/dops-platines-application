/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.asipsante.platines.dto.BulkUpdateReport;
import fr.asipsante.platines.dto.FichierT;
import fr.asipsante.platines.dto.FileDownloaded;
import fr.asipsante.platines.dto.PemTestCertificate;
import fr.asipsante.platines.dto.ProjectDto;
import fr.asipsante.platines.dto.ProjectListDto;
import fr.asipsante.platines.dto.RelatedFilesDto;
import fr.asipsante.platines.dto.ResourceBytesDto;
import fr.asipsante.platines.dto.UserDto;
import fr.asipsante.platines.dto.VersionDto;
import fr.asipsante.platines.exception.ServiceException;
import fr.asipsante.platines.executor.model.ProjectDetail;
import fr.asipsante.platines.model.bulkupdate.BulkProjectUpdateArchive;
import fr.asipsante.platines.service.BulkUpdateService;
import fr.asipsante.platines.service.ProjectLibraryService;
import fr.asipsante.platines.service.TestCertificateService;
import fr.asipsante.platines.service.UserService;
import fr.asipsante.platines.service.VersionService;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author apierre
 */
@RequestMapping("/secure/")
@RestController
public class ProjectController {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

  /** The project library service. */
  @Autowired ProjectLibraryService projectService;

  /** The version service. */
  @Autowired VersionService versionService;

  /** The user Service. */
  @Autowired private UserService userService;

  @Autowired private TestCertificateService testCertificateService;

  @Autowired private BulkUpdateService bulkUpdateService;

  /**
   * Gets all the projects.
   *
   * @return the projects list
   */
  @GetMapping(value = "project/getAll", produces = "application/json")
  public List<ProjectListDto> getAllProjects() {
    return projectService.getAllProjects();
  }

  /**
   * Gets all the projects associated to a version.
   *
   * @param idVersion id of the version
   * @return the list of the projects
   */
  @GetMapping(value = "project/getByVersion/{idVersion}", produces = "application/json")
  public List<ProjectListDto> getProjectsByVersion(@PathVariable Long idVersion) {
    return projectService.getProjectsByVersion(idVersion);
  }

  /**
   * Deletes a project from database.
   *
   * @param id id of the project to delete
   */
  @PreAuthorize("hasRole('admin')  or hasRole('manager')")
  @DeleteMapping(value = "project/delete/{id}", produces = "application/json")
  public void delete(@PathVariable Long id) {
    projectService.deleteProject(id);
  }

  /**
   * Persists a new project.
   *
   * @param projectFile the project file associated to the project to persist
   * @param relatedFiles the files associated to the project to persist
   * @param project the new project to persist
   * @param mapFile the map file
   */
  @PreAuthorize("hasRole('admin')  or hasRole('manager')")
  @PostMapping(value = "project/add", headers = "Content-Type= multipart/form-data")
  public void create(
      @RequestParam("projectFile") MultipartFile projectFile,
      @RequestParam(value = "relatedFiles", required = false) MultipartFile[] relatedFiles,
      @RequestParam("project") String project,
      @RequestParam(value = "mapFile", required = false) String mapFile) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      // FIXME : ci-dessous, ne devrait pas être nécessaire, le Json projectDTO devrait être valide
      // Donc revoir le front et retirer ceci (cf #58764)
      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      final List<FichierT> fichierTs =
          mapper.readValue(mapFile, new TypeReference<List<FichierT>>() {});
      final ProjectDto dto = mapper.readValue(project, ProjectDto.class);
      projectService.createProject(dto, relatedFiles, projectFile, fichierTs);
    } catch (final IOException e) {
      throw new ServiceException(e);
    }
  }

  /**
   * Updates a project.
   *
   * @param projectFile the project file
   * @param relatedFiles the related files
   * @param project the project to update
   * @param mapFile the map file
   */
  @PreAuthorize("hasRole('admin')  or hasRole('manager')")
  @PostMapping(value = "project/update", headers = "Content-Type= multipart/form-data")
  public void update(
      @RequestParam(value = "projectFile", required = false) MultipartFile projectFile,
      @RequestParam(value = "relatedFiles", required = false) MultipartFile[] relatedFiles,
      @RequestParam("project") String project,
      @RequestParam("mapFile") String mapFile) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      final List<FichierT> fichierTs =
          mapper.readValue(mapFile, new TypeReference<List<FichierT>>() {});
      final ProjectDto dto = mapper.readValue(project, ProjectDto.class);
      projectService.updateProject(dto, relatedFiles, projectFile, fichierTs);
    } catch (final IOException e) {
      throw new ServiceException(e);
    }
  }

  /**
   * Gets a project by its id.
   *
   * @param idProject id of the project to find
   * @return the project
   */
  @GetMapping(value = "project/get/{idProject}", produces = "application/json")
  public ProjectDto getProjectById(@PathVariable Long idProject) {
    return projectService.getProjectById(idProject);
  }

  /**
   * Analyze the project file.
   *
   * @param file the project file
   * @return the details of the project file
   * @throws IOException IO Exception
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @PostMapping(headers = "Content-Type= multipart/form-data", value = "project/analyze")
  public ProjectDetail analyze(@RequestParam("file") MultipartFile file) throws IOException {
    return projectService.analyze(file.getBytes());
  }

  /**
   * Gets the file of a project.
   *
   * @param idProjet the project id
   * @return the project file
   * @throws IOException IO Exception
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @GetMapping(produces = "application/json", value = "project/download/{idProjet}")
  public FileDownloaded getProjectFiles(@PathVariable Long idProjet) throws IOException {
    return projectService.getFilesOfProject(idProjet);
  }

  /**
   * Gets the certificate of a project.
   *
   * @param idCertificate the certificate id
   * @return the certificate
   */
  @GetMapping(produces = "application/json", value = "project/certificate/download/{idCertificate}")
  public PemTestCertificate getCertificatePublicPartFromProject(@PathVariable Long idCertificate) {
    return testCertificateService.getPublicCertificate(idCertificate);
  }

  /**
   * Gets the details of a project.
   *
   * @param idProject the project id
   * @return the project details
   */
  @GetMapping(produces = "application/json", value = "project/detail/{idProject}")
  public ProjectDetail getDetailOfProject(@PathVariable Long idProject) {
    return projectService.getDetailOfProject(idProject);
  }

  /**
   * Gets the versions compatible with a project.
   *
   * @param idProject the id project
   * @param token the user token
   * @return list of the compatible versions
   */
  @GetMapping(produces = "application/json", value = "project/versions/{idProject}")
  public List<VersionDto> getCompatibleVersions(
      @PathVariable Long idProject, @RequestHeader(value = "Authorization") String token) {
    List<VersionDto> versionsDto;
    final UserDto userDto = userService.getUserByToken(token);
    if (userDto.getProfile().getId().equals(1L)) {
      versionsDto = versionService.getVersionByProject(idProject);
    } else {
      versionsDto = versionService.getVersionByUserByProject(idProject);
    }
    return versionsDto;
  }

  /**
   * Gets the related files of the project.
   *
   * @param idProject the project id
   * @return the related files
   */
  @GetMapping(value = "project/files/{idProject}", produces = "application/json")
  public List<RelatedFilesDto> getFilesOfProject(@PathVariable Long idProject) {
    return projectService.getRelatedFilesOfProject(idProject);
  }

  /**
   * Gets a document by its id.
   *
   * @param idFile the document id
   * @param response the servlet response
   * @return the document
   */
  @GetMapping(value = "project/file/load/{idFile}", produces = "application/json")
  public byte[] getDocumentById(@PathVariable Long idFile, HttpServletResponse response) {
    final RelatedFilesDto relatedFile = projectService.getDocumentById(idFile);

    if ("text/document".equals(relatedFile.getMimeType())) {
      response.setContentType("application/octet-stream");
    } else {
      response.setContentType(relatedFile.getMimeType());
    }
    response.addHeader("Content-Disposition", "attachment; filename=" + relatedFile.getFileName());
    response.setContentLength(relatedFile.getFile().length);

    return relatedFile.getFile();
  }

  /**
   * Gets the inherited resource documents of the project.
   *
   * @param idProject the project id
   * @return the resources
   */
  @GetMapping(value = "project/resourceDocuments/{idProject}", produces = "application/json")
  public List<ResourceBytesDto> getProjectResourceDocuments(@PathVariable Long idProject) {
    return projectService.getProjectResourceDocuments(idProject);
  }

  /**
   * Gets a document by its id.
   *
   * @param idResource the document id
   * @param response the servlet response
   * @return the document
   */
  @GetMapping(value = "project/resourceDocument/load/{idResource}", produces = "application/json")
  public byte[] getProjectResourceDocumentById(
      @PathVariable Long idResource, HttpServletResponse response) {
    final ResourceBytesDto resourceDocument =
        projectService.getProjectResourceDocumentById(idResource);

    if ("text/document".equals(resourceDocument.getMimeType())) {
      response.setContentType("application/octet-stream");
    } else {
      response.setContentType(resourceDocument.getMimeType());
    }
    response.addHeader(
        "Content-Disposition", "attachment; filename=" + resourceDocument.getFileName());
    response.setContentLength(resourceDocument.getFile().length);

    return resourceDocument.getFile();
  }

  @PostMapping(
      value = "project/update/bulk",
      produces = MediaType.APPLICATION_JSON,
      consumes = "application/zip")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  public BulkUpdateReport uploadBulkProjectUpdate(
      @RequestBody byte[] archiveContent,
      @QueryParam("themeId") Long themeId,
      @QueryParam("dryRun") Boolean dryRun)
      throws IOException {

    BulkProjectUpdateArchive archive = new BulkProjectUpdateArchive(archiveContent);
    boolean effectiveUpdate = (dryRun == null || dryRun.equals(Boolean.FALSE));
    return bulkUpdateService.performUpdate(archive, themeId, effectiveUpdate);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public void handle(HttpMessageNotReadableException e) {
    LOGGER.warn("Returning HTTP 400 Bad Request", e);
  }

  @ExceptionHandler(ClientAbortException.class)
  public void handleLockException(ClientAbortException exception, HttpServletRequest request) {
    final String message = "ClientAbortException generated by request {} {} from remote address {}";
    LOGGER.info(message, request.getMethod(), request.getRequestURL(), request.getRemoteAddr());
  }
}
