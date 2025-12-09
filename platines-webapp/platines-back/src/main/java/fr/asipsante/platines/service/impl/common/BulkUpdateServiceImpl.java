/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dto.BulkUpdateReport;
import fr.asipsante.platines.dto.BulkUpdateReport.ReportLine;
import fr.asipsante.platines.dto.BulkUpdateReport.UpdateStatus;
import fr.asipsante.platines.dto.RelatedFilesDto;
import fr.asipsante.platines.entity.Project;
import fr.asipsante.platines.exception.InvalidUploadException;
import fr.asipsante.platines.executor.ProjectBuilder;
import fr.asipsante.platines.executor.model.ProjectDetail;
import fr.asipsante.platines.model.bulkupdate.BulkProjectUpdateArchive;
import fr.asipsante.platines.service.BulkUpdateService;
import fr.asipsante.platines.service.ProjectLibraryService;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service dédié à la gestion des updates en masse. Implémentation.
 *
 * @author edegenetais
 */
@Service
public class BulkUpdateServiceImpl implements BulkUpdateService {
  private static final Logger LOGGER = LoggerFactory.getLogger(BulkUpdateServiceImpl.class);
  private static final SimpleDateFormat DATE_HEURE_FR_SIMPLE =
      new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");

  @Autowired private ProjectLibraryService projectLibraryService;

  @Autowired private DateConverter dateConverter;

  @Autowired private ProjectBuilder projectAnalyzer;

  @Override
  @Transactional
  public BulkUpdateReport performUpdate(
      BulkProjectUpdateArchive zipData, Long themeId, boolean effectiveUpdate) throws IOException {
    if (themeId == null) {
      throw new InvalidUploadException(List.of(), "themeId is mandatory");
    }
    final List<ReportLine> reportLines = new ArrayList<>();
    Iterator<String> projectFileNames = zipData.projectNameIterator();
    while (projectFileNames.hasNext()) {
      final String projectFileName = projectFileNames.next();
      List<Project> projectsForName =
          projectLibraryService.getProjectsForFileNameInTheme(projectFileName, themeId);
      if (projectsForName.isEmpty()) {
        String rejectionMessage = "Fichier projet inconnu";
        reportLines.add(
            new ReportLine(
                projectFileName, null, null, null, UpdateStatus.REJECTED, rejectionMessage));

        LOGGER.info(
            "Ignored update from bulk archive for unknown project file {}", projectFileName);
      } else {
        Collections.sort(
            projectsForName,
            (Project p1, Project p2) -> -p1.getDateUpload().compareTo(p2.getDateUpload()));
        Project project = projectsForName.get(0);
        byte[] projectFileContent =
            zipData.getFileEntryData(
                project
                    .getFileName()); // TODO : ajouter la case insensitivity => voir les différents
        // niveaux
        List<RelatedFilesDto> updatedFiles = zipData.getRelatedFiles(project.getFileName());
        final ReportLine projectUpdateResult =
            tryProjectUpdateCore(project, projectFileContent, effectiveUpdate, updatedFiles);
        final ReportLine finalLine =
            switch (projectUpdateResult.status()) {
              case OK, WARNING -> toFinalProjectUpdateReportline(
                  projectsForName, projectUpdateResult, projectFileName);
              case REJECTED -> projectUpdateResult;
              default -> throw new AssertionError(
                  "Unsupported update status " + projectUpdateResult.status().name());
            };
        reportLines.add(finalLine);
      }
    }
    return new BulkUpdateReport(UUID.randomUUID().toString(), reportLines, themeId);
  }

  private ReportLine toFinalProjectUpdateReportline(
      List<Project> projectsForName,
      final ReportLine projectUpdateResult,
      final String projectFileName) {
    ReportLine finalLine;
    if (projectsForName.size() > 1) {
      Set<String> projectDescriptions =
          projectsForName.stream()
              .map(
                  p ->
                      p.getName()
                          + "(dernière màj="
                          + DATE_HEURE_FR_SIMPLE.format(p.getDateUpload())
                          + ")\n")
              .collect(Collectors.toSet());
      finalLine =
          new ReportLine(
              projectUpdateResult.filename(),
              projectUpdateResult.previousName(),
              projectUpdateResult.newName(),
              projectUpdateResult.lastUpdateDate(),
              UpdateStatus.WARNING,
              "Plusieurs projets pour " + projectFileName + ":\n " + projectDescriptions);

    } else {
      finalLine = projectUpdateResult;
    }
    return finalLine;
  }

  private ReportLine tryProjectUpdateCore(
      Project project,
      byte[] projectFileContent,
      boolean effectiveUpdate,
      List<RelatedFilesDto> updateRelatedFiles)
      throws IOException {
    ReportLine line;
    List<String> propertyKeys =
        project.getProperties().stream().map(prop -> prop.getKey()).collect(Collectors.toList());
    ProjectDetail testProjectContent = projectAnalyzer.getProjectDetail(projectFileContent);

    boolean prepertiesAlreadyExist =
        testProjectContent.getProperties().stream()
            .allMatch(prop -> propertyKeys.contains(prop.getKey()));

    Set<String> relatedFileNames =
        project.getRelatedFiles().stream()
            .map(file -> file.getFileName())
            .collect(Collectors.toSet());
    final Set<String> newFileSet =
        updateRelatedFiles.stream()
            .map(file -> file.getFileName())
            .filter(name -> !relatedFileNames.contains(name))
            .collect(Collectors.toSet());

    if (prepertiesAlreadyExist && newFileSet.isEmpty()) {
      if (effectiveUpdate) {
        project.setFile(projectFileContent);
        final Date updateDate = dateConverter.convertToUTC(new Date());
        project.setDateUpload(updateDate);
        Map<String, byte[]> updateMap =
            updateRelatedFiles.stream()
                .collect(Collectors.toMap(RelatedFilesDto::getFileName, file -> file.getFile()));
        project
            .getRelatedFiles()
            .forEach(
                file -> {
                  if (updateMap.containsKey(file.getFileName())) {
                    file.setDateUpload(updateDate);
                    file.setFile(updateMap.get(file.getFileName()));
                  }
                });
        projectLibraryService.updateProject(project);
      }
      line =
          new ReportLine(
              project.getFileName(),
              project.getName(),
              testProjectContent.getName(),
              project.getDateUpload(),
              UpdateStatus.OK,
              "");
    } else {

      StringBuilder rejectionMessage = new StringBuilder();
      if (!prepertiesAlreadyExist) {
        rejectionMessage
            .append("Mise à jour rejetée pour le fichier ")
            .append(project.getFileName())
            .append(" parce qu'elle ajoute des paramètres.")
            .append("/n");
      }
      if (!newFileSet.isEmpty()) {
        rejectionMessage
            .append("Mise à jour rejetée pour le fichier ")
            .append(project.getRelatedFiles())
            .append(" parce qu'elle ajoute les fichiers ")
            .append(newFileSet)
            .append(".\n");
      }
      line =
          new ReportLine(
              project.getFileName(),
              project.getName(),
              testProjectContent.getName(),
              project.getDateUpload(),
              UpdateStatus.REJECTED,
              rejectionMessage.toString());
      LOGGER.info(rejectionMessage.toString());
    }
    return line;
  }
}
