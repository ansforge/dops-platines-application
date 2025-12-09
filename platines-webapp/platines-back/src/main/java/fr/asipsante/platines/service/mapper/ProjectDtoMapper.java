/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.ProjectDto;
import fr.asipsante.platines.dto.ProjectListDto;
import fr.asipsante.platines.dto.ProjectSessionDto;
import fr.asipsante.platines.entity.Project;
import java.util.ArrayList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "projectDtoMapper")
public class ProjectDtoMapper extends GenericDtoMapper<Project, ProjectDto> {

  /** ModelMapper. */
  @Autowired private ModelMapper modelMapper;

  /** UserDtoMapper. */
  @Autowired
  @Qualifier("testCertificateDtoMapper")
  private TestCertificateDtoMapper testCertificateDtoMapper;

  @Autowired
  @Qualifier("versionDtoMapper")
  private VersionDtoMapper versionDtoMapper;

  @Autowired
  @Qualifier("relatedFilesDtoMapper")
  private RelatedFilesDtoMapper relatedFilesDtoMapper;

  @Autowired
  @Qualifier("propertyDtoMapper")
  private PropertyDtoMapper propertyDtoMapper;

  /**
   * Converts a {@link Project} into a {@link ProjectListDto}.
   *
   * @param project, the {@link Project} to convert
   * @return a {@link ProjectListDto}
   */
  public ProjectListDto converToProjectListDto(Project project) {
    final ProjectListDto projectListDto = modelMapper.map(project, ProjectListDto.class);
    projectListDto.setVersions(new ArrayList<>());
    if (project.getTestCertificate() != null) {
      projectListDto.setTestCertificate(
          testCertificateDtoMapper.convertToTestCertificateDto(project.getTestCertificate()));
    }
    if (!project.getVersions().isEmpty()) {
      project
          .getVersions()
          .forEach(v -> projectListDto.getVersions().add(versionDtoMapper.convertToVersionDto(v)));
    }
    return projectListDto;
  }

  /**
   * Converts a {@link ProjectDto} into a {@link Project}.
   *
   * @param projectDto, the {@link ProjectDto} to convert
   * @return a {@link Project}
   */
  public Project convertToProject(ProjectDto projectDto) {
    final Project project = modelMapper.map(projectDto, Project.class);
    if (projectDto.getTestCertificate() != null) {
      project.setTestCertificate(
          testCertificateDtoMapper.convertToEntity(projectDto.getTestCertificate()));
    }

    if (projectDto.getRelatedFiles() != null) {
      projectDto
          .getRelatedFiles()
          .forEach(
              relatedFile ->
                  project
                      .getRelatedFiles()
                      .add(relatedFilesDtoMapper.convertToEntity(relatedFile)));
    }

    if (projectDto.getProperties() != null) {
      projectDto
          .getProperties()
          .forEach(
              property -> project.getProperties().add(propertyDtoMapper.convertToEntity(property)));
    }

    return project;
  }

  /**
   * Converts a {@link Project} into a {@link ProjectDto}.
   *
   * @param project, the {@link Project} to convert
   * @return a {@link ProjectDto}
   */
  public ProjectDto convertToProjectDto(Project project) {
    final ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);
    projectDto.setProperties(new ArrayList<>());
    projectDto.setRelatedFiles(new ArrayList<>());
    if (project.getTestCertificate() != null) {
      projectDto.setTestCertificate(
          testCertificateDtoMapper.convertToTestCertificateDto(project.getTestCertificate()));
    }

    if (project.getRelatedFiles() != null) {
      project
          .getRelatedFiles()
          .forEach(
              relatedFile ->
                  projectDto
                      .getRelatedFiles()
                      .add(relatedFilesDtoMapper.convertToRelatedFileDto(relatedFile)));
    }

    if (project.getProperties() != null) {
      project
          .getProperties()
          .forEach(
              property -> projectDto.getProperties().add(propertyDtoMapper.convertToDto(property)));
    }

    return projectDto;
  }

  /**
   * Converts a {@link Project} into a {@link ProjectSessionDto}.
   *
   * @param project, the {@link Project} to convert
   * @return a {@link ProjectSessionDto}
   */
  public ProjectSessionDto convertToProjectSessionDto(Project project) {
    final ProjectSessionDto projectDto = modelMapper.map(project, ProjectSessionDto.class);
    projectDto.setVersions(new ArrayList<>());
    projectDto.setProperties(new ArrayList<>());
    if (!project.getProperties().isEmpty()) {
      project
          .getProperties()
          .forEach(p -> projectDto.getProperties().add(propertyDtoMapper.convertToDto(p)));
    }
    if (!project.getVersions().isEmpty()) {
      project
          .getVersions()
          .forEach(v -> projectDto.getVersions().add(versionDtoMapper.convertToVersionDto(v)));
    }
    return projectDto;
  }
}
