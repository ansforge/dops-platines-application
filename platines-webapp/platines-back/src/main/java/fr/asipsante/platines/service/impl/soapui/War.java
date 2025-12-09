/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.soapui;

import fr.asipsante.platines.entity.RelatedFiles;
import fr.asipsante.platines.entity.Resource;
import fr.asipsante.platines.entity.enums.FileType;
import fr.asipsante.platines.exception.ServiceException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

/**
 * The type War.
 *
 * @author apierre
 */
public class War {
  /** The LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(War.class);

  /** The OS where the application is install. */
  private static final String OS = System.getProperty("os.name").toLowerCase();

  /** The name of the project file. */
  private static final String PROJECT_FILE_NAME = "[ProjectFileName]";

  /** The endpoint of mockservice. */
  private static final String MOCKSERVICE_ENDPOINT = "[mockServiceEndpoint]";

  /** The Buffer to write file. */
  private static final int BUFFER_SIZE = 10240;

  /** The permissions on write files. */
  private static final String PERMISSIONS_FILE = "rwxrwxrwx";

  /** The Buffer to write file. */
  private static final int DEFAULT_BUFFER_SIZE = 1024;

  /** The war. */
  private final File warFile;

  /** The directory of war. */
  private final File warDir;

  /** The directory WEB-INF. */
  private String webInfFolder;

  /** The directory ext. */
  private String extLibFolder;

  /** The directory soapui. */
  private String soapuiFolder;

  /** The projectFileName. */
  private final String projectFileName;

  private final List<RelatedFiles> relatedFiles;

  private final List<Resource> resources;

  /**
   * Constructor of War class.
   *
   * @param warFile war File
   * @param warDir war Directory
   * @param projectFileName project File Name
   * @param relatedFiles the related files
   * @param resources resources list
   */
  public War(
      File warFile,
      File warDir,
      String projectFileName,
      List<RelatedFiles> relatedFiles,
      List<Resource> resources) {
    this.warDir = warDir;
    this.warFile = warFile;
    this.projectFileName = projectFileName;
    this.relatedFiles = relatedFiles;
    this.resources = resources;
  }

  /** Create the folders and the necessary files for a mock. */
  public void create() {
    webInfFolder = createFolder(warDir + File.separator + "WEB-INF", PERMISSIONS_FILE);
    // Les bibliothèques "custom" sont déployées dans ext pour ne pas être écrasées
    // lors du téléchargement des "libs" standards.
    extLibFolder = createFolder(warDir + File.separator + "ext", PERMISSIONS_FILE);
    soapuiFolder = createFolder(webInfFolder + File.separator + "soapui", PERMISSIONS_FILE);
    LOGGER.debug(
        "warDir : {}, webInfFolder : {}, extLibFolder : {}, soapuiFolder : {}",
        warDir,
        webInfFolder,
        extLibFolder,
        soapuiFolder);
    final String listenersFolder =
        createFolder(webInfFolder + File.separator + "listeners", PERMISSIONS_FILE);
    try {
      InputStream resource = new ClassPathResource("platines-listeners.xml").getInputStream();

      copyInputStreamToFile(
          resource, new File(listenersFolder + File.separator + "platines-listeners.xml"));
      for (final RelatedFiles file : relatedFiles) {
        writeRelatedFiles(file);
      }
      for (final Resource file : resources) {
        writeResourcesFiles(file);
      }
      createWebXml();
      final File[] warFiles = warDir.listFiles();
      assert warFiles != null;
      for (final File file : warFiles) {
        if (file.isFile()) {
          copyProjectFile(file);
        }
      }
      final ArrayList<File> files = getAllFilesFrom(new File(webInfFolder));
      // On ajoute le fichier des bibliothèques Custom à la racine du War
      ArrayList<File> extFiles = getAllFilesFrom(new File(extLibFolder));
      files.addAll(extFiles);
      final File[] filez = files.toArray(new File[files.size()]);
      createJarArchive(warFile, warDir, filez);
    } catch (final IOException e) {
      LOGGER.error("Erreur lors de la copie des fichiers : ", e);
      throw new ServiceException(e, "Erreur lors de la création du war ");
    }
  }

  private void writeResourcesFiles(Resource resource) throws IOException {
    if (FileType.RESOURCE.equals(resource.getFileType())) {
      writeFile(warDir + File.separator + resource.getFileName(), resource.getFile());
    } else if (FileType.SINGLE_JAR.equals(resource.getFileType())) {
      writeFile(extLibFolder + File.separator + resource.getFileName(), resource.getFile());
    } else if (FileType.BUNDLE_JAR.equals(resource.getFileType())) {
      final byte[] buffer = new byte[1024];
      final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(resource.getFile()));
      ZipEntry zipEntry = zis.getNextEntry();
      while (zipEntry != null) {
        final String fileName = zipEntry.getName();
        final File newFile = new File(extLibFolder + File.separator + fileName);
        final FileOutputStream fos = new FileOutputStream(newFile, false);
        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }
        fos.close();
        zipEntry = zis.getNextEntry();
      }
      zis.closeEntry();
      zis.close();
    }
  }

  private void writeRelatedFiles(RelatedFiles relatedFiles) throws IOException {
    if (FileType.RESOURCE.equals(relatedFiles.getFileType())) {
      writeFile(warDir + File.separator + relatedFiles.getFileName(), relatedFiles.getFile());
    } else if (FileType.SINGLE_JAR.equals(relatedFiles.getFileType())) {
      writeFile(extLibFolder + File.separator + relatedFiles.getFileName(), relatedFiles.getFile());
    } else if (FileType.BUNDLE_JAR.equals(relatedFiles.getFileType())) {
      final byte[] buffer = new byte[1024];
      final ZipInputStream zis =
          new ZipInputStream(new ByteArrayInputStream(relatedFiles.getFile()));
      ZipEntry zipEntry = zis.getNextEntry();
      while (zipEntry != null) {
        final String fileName = zipEntry.getName();
        final File newFile = new File(extLibFolder + File.separator + fileName);
        final FileOutputStream fos = new FileOutputStream(newFile, false);
        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }
        fos.close();
        zipEntry = zis.getNextEntry();
      }
      zis.closeEntry();
      zis.close();
    }
  }

  private String createFolder(String folderName, String permissions) {
    final File folder = new File(folderName);

    if (!folder.exists()) {
      if (folder.mkdirs()) {
        if (OS.contains("win")) {
          LOGGER.info("Pas de permission à gérer sur les fichiers sous windows");
        } else {
          try {
            Files.setPosixFilePermissions(
                folder.toPath(), PosixFilePermissions.fromString(permissions));
          } catch (final IOException e) {
            LOGGER.error("Erreur lors de la création du répertoire de travail", e);
          }
        }
      } else {
        throw new ServiceException("Impossible to create directory : " + folderName);
      }
    }

    return folder.getAbsolutePath();
  }

  private void copyProjectFile(File f) {
    try (FileInputStream fis = new FileInputStream(f);
        OutputStream os = new FileOutputStream(soapuiFolder + File.separator + f.getName())) {

      final byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
      int length;
      while ((length = fis.read(buffer)) > 0) {
        os.write(buffer, 0, length);
      }
    } catch (final IOException exception) {
      LOGGER.error("Erreur d'entrée/sortie : ", exception);
      throw new ServiceException(exception, "Erreur lors de la création du projet soapui ");
    }
  }

  private ArrayList<File> getAllFilesFrom(File dir) {
    final ArrayList<File> result = new ArrayList<>();
    if (dir.isDirectory()) {
      result.addAll(Arrays.asList(Objects.requireNonNull(dir.listFiles())));
      final ArrayList<File> toAdd = new ArrayList<>();
      for (final File f : result) {
        if (f.isDirectory()) {
          toAdd.addAll(getAllFilesFrom(f));
        }
      }
      result.addAll(toAdd);
    }
    return result;
  }

  /**
   * Creates the archive jar.
   *
   * @param archiveFile archive File
   * @param root root
   * @param tobeJared file or files to be Jared
   */
  private void createJarArchive(File archiveFile, File root, File... tobeJared) {

    try (final FileOutputStream stream = new FileOutputStream(archiveFile);
        final JarOutputStream out = new JarOutputStream(stream, new Manifest())) {

      final byte[] buffer = new byte[BUFFER_SIZE];
      // Open archive file

      for (File file : tobeJared) {
        if (file == null || !file.exists()) {
          continue; // Just in case...
        }

        // Add archive entry
        String jarName = file.isDirectory() ? file.getAbsolutePath() + "/" : file.getAbsolutePath();
        jarName = jarName.replace(root.getAbsolutePath(), "").substring(1);
        jarName = jarName.replace(File.separatorChar, '/');
        final JarEntry jarAdd = new JarEntry(jarName);
        jarAdd.setTime(file.lastModified());
        out.putNextEntry(jarAdd);

        if (jarAdd.isDirectory()) {
          continue;
        }
        // Write file to archive
        final FileInputStream in = new FileInputStream(file);
        int len;
        while ((len = in.read(buffer)) > 0) {
          out.write(buffer, 0, len);
        }
        in.close();
        out.closeEntry();
      }
    } catch (final Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
      throw new ServiceException(ex, "Erreur lors de la création de l'archive ");
    }
  }

  /** Create web xml. */
  protected void createWebXml() {

    try (final BufferedWriter out =
        new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(new File(webInfFolder, "web.xml"))))) {

      final BufferedReader in =
          new BufferedReader(
              new InputStreamReader(
                  new ClassPathResource("mocks/WEB-INF/web.xml.tmpl").getInputStream()));
      String inputLine;
      final StringBuilder content = new StringBuilder();

      while ((inputLine = in.readLine()) != null) {
        content.append(inputLine).append("\n");
      }
      customizeWebXmlContent(content);

      out.write(content.toString());
      out.flush();
    } catch (final IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  /**
   * Create content.
   *
   * @param content the content
   */
  protected void customizeWebXmlContent(StringBuilder content) {
    content.replace(
        content.indexOf(PROJECT_FILE_NAME),
        content.indexOf(PROJECT_FILE_NAME) + PROJECT_FILE_NAME.length(),
        projectFileName);
    content.replace(
        content.indexOf(MOCKSERVICE_ENDPOINT),
        content.indexOf(MOCKSERVICE_ENDPOINT) + MOCKSERVICE_ENDPOINT.length(),
        "");
  }

  /**
   * Deletes all files, just files, in directory.
   *
   * @param dir directory
   */
  protected void clearDir(File dir) {
    for (final File file : Objects.requireNonNull(dir.listFiles())) {
      if (file.isFile()) {
        file.delete();
      }
    }
  }

  private void writeFile(String pathFile, byte[] input) {
    final File keystore = new File(pathFile);
    try (FileOutputStream fos = new FileOutputStream(keystore)) {
      fos.write(input);
    } catch (final IOException e) {
      throw new ServiceException(e, "Erreur lors de la création de la session");
    }
  }

  private void copyInputStreamToFile(InputStream inputStream, File file) throws IOException {

    // append = false
    try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
      int read;
      byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
      while ((read = inputStream.read(bytes)) != -1) {
        outputStream.write(bytes, 0, read);
      }
    }
  }
}
