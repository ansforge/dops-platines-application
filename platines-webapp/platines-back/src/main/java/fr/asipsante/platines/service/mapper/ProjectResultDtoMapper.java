/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.ProjectResultDetailDto;
import fr.asipsante.platines.dto.ProjectResultDto;
import fr.asipsante.platines.entity.ProjectResult;
import fr.asipsante.platines.entity.enums.ResultStatus;
import java.util.ArrayList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "projectResultDtoMapper")
public class ProjectResultDtoMapper extends GenericDtoMapper<ProjectResult, ProjectResultDto> {

  /** ModelMapper. */
  @Autowired private ModelMapper modelMapper;

  /** UserDtoMapper. */
  @Autowired
  @Qualifier("projectResultPropertyDtoMapper")
  private ProjectResultPropertyDtoMapper projectResultPropertyDtoMapper;

  /** UserDtoMapper. */
  @Autowired
  @Qualifier("testSuiteDtoMapper")
  private TestSuiteDtoMapper testSuiteDtoMapper;

  /**
   * converts a {@link ProjectResult} into a {@link ProjectResultDto}.
   *
   * @param projectResult, the {@link ProjectResult} to convert
   * @return a {@link ProjectResultDto}
   */
  public ProjectResultDto convertToProjectResultDto(ProjectResult projectResult) {
    final ProjectResultDto projectResultDto =
        modelMapper.map(projectResult, ProjectResultDto.class);
    projectResultDto.setProjectProperties(new ArrayList<>());
    if (projectResult.getProjectProperties() != null) {
      projectResult
          .getProjectProperties()
          .forEach(
              property ->
                  projectResultDto
                      .getProjectProperties()
                      .add(projectResultPropertyDtoMapper.convertToDto(property)));
    }
    return projectResultDto;
  }

  /**
   * Converts a {@link ProjectResultDto} into a {@link ProjectResult}.
   *
   * @param projectRDto, the {@link ProjectResultDto} to convert
   * @return a {@link ProjectResult}
   */
  public ProjectResult convertToProjectResult(ProjectResultDto projectRDto) {
    final ProjectResult project = modelMapper.map(projectRDto, ProjectResult.class);
    project.setProjectProperties(new ArrayList<>());
    if (!projectRDto.getProjectProperties().isEmpty()) {
      projectRDto
          .getProjectProperties()
          .forEach(
              prop ->
                  project
                      .getProjectProperties()
                      .add(projectResultPropertyDtoMapper.convertToEntity(prop)));
    }
    if (projectRDto.getResultStatus() == null) {
      project.setResultStatus(ResultStatus.NONEXECUTE);
    }
    return project;
  }

  /**
   * Converts a {@link ProjectResult} into a {@link ProjectResultDetailDto}.
   *
   * @param project, the {@link ProjectResult} to convert
   * @return a {@link ProjectResultDetailDto}
   */
  public ProjectResultDetailDto convertToProjectResultDetailDto(ProjectResult project) {
    final ProjectResultDetailDto projectDto =
        modelMapper.map(project, ProjectResultDetailDto.class);
    projectDto.setProjectProperties(new ArrayList<>());
    projectDto.setTestSuites(new ArrayList<>());
    if (!project.getProjectProperties().isEmpty()) {
      project
          .getProjectProperties()
          .forEach(
              prop ->
                  projectDto
                      .getProjectProperties()
                      .add(projectResultPropertyDtoMapper.convertToDto(prop)));
    }
    if (!project.getTestSuites().isEmpty()) {
      project
          .getTestSuites()
          .forEach(
              suite ->
                  projectDto.getTestSuites().add(testSuiteDtoMapper.convertToTestSuiteDto(suite)));
    }
    return projectDto;
  }
}
