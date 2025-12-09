/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author apierre
 */
public class ZipDirectory {

  /** logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ZipDirectory.class);

  /** byte length. */
  private static final int BYTE_LENGTH = 1024;

  /**
   * Zip a folder.
   *
   * @param srcFolder, folder to zip
   * @param destZipFile, destination
   * @return true if ok.
   */
  public boolean zipFiles(String srcFolder, String destZipFile) {
    boolean result = false;
    try {
      zipFolder(srcFolder, destZipFile);
      if (System.getProperty("os.name").toLowerCase().contains("win")) {
        LOGGER.info("Pas de permission à gérer sur les fichiers sous windows");
      } else {
        Files.setPosixFilePermissions(
            Paths.get(destZipFile), PosixFilePermissions.fromString("rwxrwxrwx"));
      }
      result = true;
    } catch (final Exception e) {
      LOGGER.error("Erreur lors de la création du répertoire de travail", e);
    }
    return result;
  }

  /**
   * Zip the folder.
   *
   * @param srcFolder src Folder
   * @param destZipFile dest Zip File
   * @throws Exception Exception
   */
  private void zipFolder(String srcFolder, String destZipFile) throws Exception {
    ZipOutputStream zip;
    FileOutputStream fileWriter;
    /*
     * create the output stream to zip file result
     */
    fileWriter = new FileOutputStream(destZipFile);
    zip = new ZipOutputStream(fileWriter);
    /*
     * add the folder to the zip
     */
    addFolderToZip("", srcFolder, zip);
    /*
     * close the zip objects
     */
    zip.flush();
    zip.close();
    fileWriter.close();
  }

  /**
   * recursively add files to the zip files.
   *
   * @param path path
   * @param srcFile srcFile
   * @param zip zip
   * @param flag flag
   * @throws Exception Exception
   */
  private void addFileToZip(String path, String srcFile, ZipOutputStream zip, boolean flag)
      throws Exception {
    /*
     * create the file object for inputs
     */
    final File folder = new File(srcFile);

    /*
     * if the folder is empty add empty folder to the Zip file
     */
    if (flag) {
      zip.putNextEntry(new ZipEntry(path + "/" + folder.getName() + "/"));
    } else {
      /*
       * if the current name is directory, recursively traverse it to get the files
       */
      if (folder.isDirectory()) {
        /*
         * if folder is not empty
         */
        addFolderToZip(path, srcFile, zip);
      } else {
        /*
         * write the file to the output
         */
        final byte[] buf = new byte[BYTE_LENGTH];
        int len;
        final FileInputStream in = new FileInputStream(srcFile);
        zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
        while ((len = in.read(buf)) > 0) {
          /*
           * Write the Result
           */
          zip.write(buf, 0, len);
        }
        in.close();
      }
    }
  }

  /**
   * add folder to the zip file.
   *
   * @param path path
   * @param srcFolder srcFolder
   * @param zip zip
   * @throws Exception Exception
   */
  private void addFolderToZip(String path, String srcFolder, ZipOutputStream zip) throws Exception {
    final File folder = new File(srcFolder);

    /*
     * check the empty folder
     */
    if (Objects.requireNonNull(folder.list()).length == 0) {
      addFileToZip(path, srcFolder, zip, true);
    } else {
      /*
       * list the files in the folder
       */
      for (final String fileName : Objects.requireNonNull(folder.list())) {
        if ("".equals(path)) {
          addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip, false);
        } else {
          addFileToZip(path + "/" + folder.getName(), srcFolder + "/" + fileName, zip, false);
        }
      }
    }
  }
}
