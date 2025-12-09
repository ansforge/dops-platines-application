/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.dto.FichierT;
import fr.asipsante.platines.dto.FileDownloaded;
import fr.asipsante.platines.dto.ProjectDto;
import fr.asipsante.platines.dto.ProjectListDto;
import fr.asipsante.platines.dto.RelatedFilesDto;
import fr.asipsante.platines.dto.ResourceBytesDto;
import fr.asipsante.platines.entity.Project;
import fr.asipsante.platines.entity.Property;
import fr.asipsante.platines.entity.RelatedFiles;
import fr.asipsante.platines.entity.SimulatedService;
import fr.asipsante.platines.entity.TestCertificate;
import fr.asipsante.platines.entity.Theme;
import fr.asipsante.platines.entity.Version;
import fr.asipsante.platines.executor.model.ProjectDetail;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * The Project library service. All methods associated to entities {@link Theme}, {@link
 * SimulatedService}, {@link Version}, {@link Project}, {@link TestCertificate}, {@link
 * RelatedFiles}, and {@link Property}.
 *
 * @author apierre
 */
public interface ProjectLibraryService {

  /**
   * Gets all projects.
   *
   * @return the projects list
   */
  List<ProjectListDto> getAllProjects();

  /**
   * Gets a projects list by a version.
   *
   * @param idVersion the id version
   * @return a project list
   */
  List<ProjectListDto> getProjectsByVersion(Long idVersion);

  /**
   * Deletes a project.
   *
   * @param idProject the id project
   */
  void deleteProject(Long idProject);

  /**
   * Creates a project.
   *
   * @param projectDto the project dto
   * @param relatedFiles the related files
   * @param projectFile the project file
   * @param fileType the file type
   * @throws IOException IO exception
   */
  void createProject(
      ProjectDto projectDto,
      MultipartFile[] relatedFiles,
      MultipartFile projectFile,
      List<FichierT> fileType)
      throws IOException;

  /**
   * Analyzes the project file.
   *
   * @param projectFile the project file
   * @return the project details
   */
  ProjectDetail analyze(byte[] projectFile);

  /**
   * Updates a project.
   *
   * @param projectDto the project dto
   * @param relatedFiles the related files
   * @param projectFile the project file
   * @param fileType the file type
   * @throws IOException IO exception
   */
  void updateProject(
      ProjectDto projectDto,
      MultipartFile[] relatedFiles,
      MultipartFile projectFile,
      List<FichierT> fileType)
      throws IOException;

  /**
   * Updates a project.
   *
   * @param project the project dto
   * @param relatedFiles the related files
   * @throws IOException IO exception
   */
  void updateProject(Project project) throws IOException;

  /**
   * Gets a project by its id.
   *
   * @param idProject the id project
   * @return the project
   */
  ProjectDto getProjectById(Long idProject);

  /**
   * Gets the files of the project as a zip.
   *
   * @param id the id
   * @return the files as zip
   * @throws IOException IO exception
   */
  FileDownloaded getFilesOfProject(Long id) throws IOException;

  /**
   * Gets the details of a project.
   *
   * @param idProject the id project
   * @return the project details
   */
  ProjectDetail getDetailOfProject(Long idProject);

  /**
   * Gets the related files for a project.
   *
   * @param id the id
   * @return a related files list
   */
  List<RelatedFilesDto> getRelatedFilesOfProject(Long id);

  /**
   * Gets a document by its id.
   *
   * @param idFile the id file
   * @return the document
   */
  RelatedFilesDto getDocumentById(Long idFile);

  /**
   * Gets the inherited resource documents of the project.
   *
   * @param id the id
   * @return a related files list
   */
  List<ResourceBytesDto> getProjectResourceDocuments(Long id);

  /**
   * Gets a document by its id.
   *
   * @param idResource the id resource
   * @return the document
   */
  ResourceBytesDto getProjectResourceDocumentById(Long idResource);

  /**
   * Récupérer le ou les projets liés à ce nom de fichier projet.
   *
   * @param projectFileName le nom du fichier projet
   * @param themeId identifiant du thème cible
   * @return
   */
  List<Project> getProjectsForFileNameInTheme(String projectFileName, Long themeId);
}
