/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.model.bulkupdate;

import fr.asipsante.platines.dto.RelatedFilesDto;
import fr.asipsante.platines.exception.InvalidUploadException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author edegenetais
 */
public class BulkProjectUpdateArchive {
  private static final Logger LOGGER = LoggerFactory.getLogger(BulkProjectUpdateArchive.class);

  private static final Pattern LEGAL_ENTRY_NAME_REGEX =
      Pattern.compile("^[\\p{IsLatin}0-9\\/._\\-' ]+$");
  private static final String INVALID_PROJECT_BULK_UPDATE_ARCHIVE_ERROR_CODE =
      "Archive de mise à jour de projets en masse invalide.";
  private static final Charset[] SUPPORTED_ENTRY_NAME_CHARSETS = {
    Charset.forName("UTF-8"),
    Charset.forName("iso-8859-1"),
    Charset.forName("windows-1252"),
    Charset.forName("Cp850"),
    Charset.forName("iso-8859-15")
  };

  private byte[] zipData;
  private List<String> projectNames = new ArrayList<>();
  private List<String> dirNames = new ArrayList<>();
  private Charset nameCharset;

  public BulkProjectUpdateArchive(byte[] zipData) {
    this.zipData = zipData;

    ValidationReportImpl report = new ValidationReportImpl();

    tryArchiveListing(new DecodingState(0, List.of()));

    LOGGER.debug("Analyse complète. encodage ={}", this.nameCharset);

    addErrorIfNoProject(projectNames, report);

    addErrorIfNonXmlRootFile(projectNames, report);

    addErrorIfSubdirectories(dirNames, report);

    addErrorIfDirectoryMatchesNoProject(projectNames, dirNames, report);

    addErrorsForCaseInsensitiveDuplicates(projectNames, report, "fichier");

    addErrorsForCaseInsensitiveDuplicates(dirNames, report, "repertoire");

    if (!report.isValid()) {
      throw new InvalidUploadException(
          report.errors(), INVALID_PROJECT_BULK_UPDATE_ARCHIVE_ERROR_CODE);
    }
  }

  private void tryArchiveListing(DecodingState decodingState)
      throws BulkArchiveException, InvalidUploadException {
    /*
     * TODO : we should derecursivate archive encoding trial-and-error to curb the world (and this code)'s uglyness level.
     */
    this.nameCharset = SUPPORTED_ENTRY_NAME_CHARSETS[decodingState.charsetIndex()];
    try (ZipInputStream zipIs =
        new ZipInputStream(new ByteArrayInputStream(this.zipData), this.nameCharset)) {
      ZipEntry currentEntry = fetchEntryOrTryNextEntryEncoding(zipIs, decodingState);
      /*
       * NB : si ce premier appel a déclenché le redémarrage de l'énumération sur un autre encodage,
       * currentEntry sera null à ce stade pour signaler que l'énumétraton a déjà été terminée avec un autre encodage.
       */
      while (currentEntry != null) {
        if (currentEntry.isDirectory()) {
          dirNames.add(currentEntry.getName());
        } else {
          final String fileEntryName = currentEntry.getName();
          final int indexOfLastSlash = fileEntryName.lastIndexOf('/');
          if (indexOfLastSlash < 0) {
            projectNames.add(fileEntryName);
          }
        }
        /*
         * NB : Si l'appel ci-dessous déclenche le redémarrage de l'énumération sur un autre encodage,
         * currentEntry sera null au retour pour signaler que l'énumération a déjà été terminée avec un autre encodage.
         */
        currentEntry = fetchEntryOrTryNextEntryEncoding(zipIs, decodingState);
      }
    } catch (IOException e) {
      throw new InvalidUploadException(e, "Archive de mise à jour en masse corrompue.");
    }
  }

  private static record DecodingState(int charsetIndex, List<String> decodingErrors) {}
  ;

  /**
   * Appel lors du constructeur, doit être appliqué à l'énumération de 100% des {@link ZipEntry}
   * pour détecter à coup sur l'encodage des noms d'entrées. NB : s'il faut changer d'encodage,
   * cette méthode appelle récursivement {@link #tryArchiveListing(byte[], int) }, donc à son retour
   * elle renvoie <code>null</code> pour signifier que l'archive a déjà été énumérée.
   *
   * @param zipIs le stream de décompression courant.
   * @param charsetIndex l'index du charset courant.
   * @return la nouvelle entrée si son extraction a réussi, <code>null</code> si un autre encodage a
   *     permis l'énumération dans un appel récursif.
   * @throws IOException
   * @throws BulkArchiveException
   * @throws InvalidUploadException
   */
  private ZipEntry fetchEntryOrTryNextEntryEncoding(
      final ZipInputStream zipIs, DecodingState decodingState)
      throws IOException, BulkArchiveException, InvalidUploadException {
    ZipEntry currentEntry;
    try {
      currentEntry = zipIs.getNextEntry();
      if (currentEntry != null
          && !LEGAL_ENTRY_NAME_REGEX.matcher(currentEntry.getName()).matches()) {
        LOGGER.debug(
            "{} encoding refused due to forbidden characters in {}",
            this.nameCharset,
            currentEntry.getName());
        tryNextEncoding(
            decodingState,
            "Décodé avec le charset "
                + this.nameCharset
                + " ce nom contient des caractères interdits :"
                + currentEntry.getName());
        currentEntry =
            null; // Si on arrive à ce point, une énumération a réussi, fin de parcours d'archive.
      }
    } catch (IllegalArgumentException exc) {
      /*
       * This is rather ugly, however no API was found to check the archive name encoding,
       * so we need to process through trial-and-error.
       * TODO : we should derecursivate this.
       */
      LOGGER.debug("Failed to list archive using charset {}", this.nameCharset, exc);
      tryNextEncoding(
          decodingState,
          "Erreur de décodage des noms de fichiers avec l'encodage " + this.nameCharset);
      currentEntry =
          null; // Si on arrive à ce point, une énumération a réussi, fin de parcours d'archive.
    }
    return currentEntry;
  }

  private void tryNextEncoding(
      DecodingState decodingState, final String decodingFailureMessageString)
      throws InvalidUploadException, BulkArchiveException {
    int nextIndex = decodingState.charsetIndex() + 1;
    if (nextIndex < SUPPORTED_ENTRY_NAME_CHARSETS.length) {
      this.dirNames.clear();
      this.projectNames.clear();
      List<String> newErrors = new ArrayList<>(decodingState.decodingErrors());
      newErrors.add(decodingFailureMessageString);
      tryArchiveListing(new DecodingState(nextIndex, Collections.unmodifiableList(newErrors)));
    } else {
      throw new InvalidUploadException(
          decodingState.decodingErrors(), "Décodage des noms de fichiers impossible");
    }
  }

  public Iterator<String> projectNameIterator() {
    return projectNames.iterator();
  }

  public byte[] getFileEntryData(String entryName) {
    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData), nameCharset)) {
      byte[] content = null;
      ZipEntry entry = zis.getNextEntry();
      while (content == null && entry != null) {
        if (entry.getName().equals(entryName)) {
          if (entry.isDirectory()) {
            throw new IllegalArgumentException(entryName + " is not a project name.");
          }
          content = zis.readAllBytes();
        }
        entry = zis.getNextEntry();
      }
      if (content == null) {
        throw new IllegalArgumentException(entryName + " is not an entry for this archive.");
      }
      return content;
    } catch (IOException e) {
      throw new BulkArchiveException("Failed to get entry content", e);
    }
  }

  public List<RelatedFilesDto> getRelatedFiles(String projectFileName) {
    String entryDirname = projectFileName.substring(0, projectFileName.lastIndexOf('.')) + '/';
    List<RelatedFilesDto> relatedFiles = new ArrayList<>();
    try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipData), nameCharset)) {
      ZipEntry entry = zis.getNextEntry();
      while (entry != null) {
        if (!entry.isDirectory() && entry.getName().startsWith(entryDirname)) {
          String fileName = entry.getName().substring(entryDirname.length());
          byte[] fileContent = zis.readAllBytes();
          final RelatedFilesDto relatedFilesDto = new RelatedFilesDto();
          relatedFilesDto.setFileName(fileName);
          relatedFilesDto.setFile(fileContent);
          relatedFiles.add(relatedFilesDto);
        }
        entry = zis.getNextEntry();
      }
    } catch (IOException ex) {
      throw new BulkArchiveException("Failed to get entry content", ex);
    }
    return relatedFiles;
  }

  private void addErrorsForCaseInsensitiveDuplicates(
      List<String> nameList, ValidationReportImpl report, final String entryTypeName) {
    Map<String, List<String>> caseInsensitiveFileNameMap = new HashMap<>(nameList.size());
    nameList.forEach(
        name -> {
          final String caseInsensitiveClassName = name.toUpperCase();
          if (!caseInsensitiveFileNameMap.containsKey(caseInsensitiveClassName)) {
            caseInsensitiveFileNameMap.put(caseInsensitiveClassName, new ArrayList<>(2));
          }
          caseInsensitiveFileNameMap.get(caseInsensitiveClassName).add(name);
        });
    caseInsensitiveFileNameMap.values().stream()
        .filter(list -> list.size() > 1)
        .forEach(
            list -> report.addError("Collision de noms de " + entryTypeName + " projet : " + list));
  }

  private void addErrorIfDirectoryMatchesNoProject(
      List<String> projectNames, List<String> dirNames, ValidationReportImpl report) {

    dirNames.stream()
        .filter(new AcceptedDirnameMatcher(projectNames))
        .forEach(name -> report.addError("Ce répertoire ne correspond à aucun projet : " + name));
  }

  private void addErrorIfSubdirectories(List<String> dirNames, ValidationReportImpl report) {
    dirNames.stream()
        .filter(name -> name.split("/").length > 1)
        .forEach(name -> report.addError("Sous-répertoire interdit : " + name));
  }

  private void addErrorIfNonXmlRootFile(List<String> projectName, ValidationReportImpl report) {
    List<String> badProjectNames =
        projectName.stream()
            .filter(name -> !name.toLowerCase().endsWith(".xml"))
            .collect(Collectors.toList());
    if (!badProjectNames.isEmpty()) {
      badProjectNames.forEach(
          name ->
              report.addError("La racine contient un fichier qui n'est pas un projet : " + name));
    }
  }

  private void addErrorIfNoProject(List<String> projectName, ValidationReportImpl report) {
    if (projectName.stream().noneMatch(name -> name.toLowerCase().endsWith(".xml"))) {
      report.addError("La racine de l'archive doit contenir au moins un projet.");
    }
  }
}
