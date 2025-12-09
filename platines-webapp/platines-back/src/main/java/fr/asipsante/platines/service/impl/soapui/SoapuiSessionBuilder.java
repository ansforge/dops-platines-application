/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.soapui;

import com.eviware.soapui.config.PropertyConfig;
import com.eviware.soapui.config.SoapuiProjectDocumentConfig;
import fr.asipsante.platines.dao.IProjectDao;
import fr.asipsante.platines.dao.IProjectResultDao;
import fr.asipsante.platines.dao.IResourceDao;
import fr.asipsante.platines.dao.ITestCaseDao;
import fr.asipsante.platines.dao.ITestSuiteDao;
import fr.asipsante.platines.dto.CertificateDto;
import fr.asipsante.platines.dto.ProjectResultDto;
import fr.asipsante.platines.dto.ProjectResultPropertyDto;
import fr.asipsante.platines.dto.SessionParameters;
import fr.asipsante.platines.dto.TestSessionDto;
import fr.asipsante.platines.entity.Project;
import fr.asipsante.platines.entity.ProjectResult;
import fr.asipsante.platines.entity.RelatedFiles;
import fr.asipsante.platines.entity.Resource;
import fr.asipsante.platines.entity.TestCaseResult;
import fr.asipsante.platines.entity.TestSuiteResult;
import fr.asipsante.platines.entity.enums.ResultStatus;
import fr.asipsante.platines.exception.ServiceException;
import fr.asipsante.platines.executor.ProjectBuilder;
import fr.asipsante.platines.executor.model.ProjectDetail;
import fr.asipsante.platines.executor.model.TestCaseDetail;
import fr.asipsante.platines.executor.model.TestSuiteDetail;
import fr.asipsante.platines.service.SessionBuilder;
import fr.asipsante.platines.service.impl.common.SessionRunnerImpl;
import fr.asipsante.platines.utils.ZipDirectory;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

/**
 * @author aboittiaux
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SoapuiSessionBuilder implements SessionBuilder {

  /** The Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(SoapuiSessionBuilder.class);

  /** The plugins common pattern. */
  private static final String PLUGIN_PATTERN = "readyapi-plugin";

  /** The OS where the application is install. */
  private static final String OS = System.getProperty("os.name").toLowerCase();

  /** The permissions on write files. */
  private static final String PERMISSIONS_FILE = "rwxrwxrwx";

  /** Dao des resources. */
  @Autowired
  @Qualifier("resourceDao")
  private IResourceDao resourceDao;

  /** Dao des projets. */
  @Autowired
  @Qualifier("projectDao")
  private IProjectDao projectDao;

  /** IProjectResultDao. */
  @Autowired
  @Qualifier("projectResultDao")
  private IProjectResultDao iProjectResultDao;

  /** ITestSuiteDao. */
  @Autowired
  @Qualifier("testSuiteDao")
  private ITestSuiteDao iTestSuiteDao;

  /** ITestCaseDao. */
  @Autowired
  @Qualifier("testCaseDao")
  private ITestCaseDao iTestCaseDao;

  @Autowired private ProjectBuilder projectBuilder;

  /** Chemin du répertoire ready. */
  @Value("${workspace}")
  private String pathReadyFolder;

  /** Chemin du répertoire de session. */
  private String pathSessionFolder;

  /** Chemin du répertoire des projets. */
  private String pathProjetsFolder;

  /** Chemin du répertoire des plugins soapui. */
  private String pathSoapuiPlugins;

  /** Projet. */
  private Project mockProject;

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public SessionParameters createClientSession(TestSessionDto session) {
    SessionParameters params;
    try {
      init(session.getUuid());

      final long[] associationsIds = {
        session.getVersion().getId(),
        session.getVersion().getService().getId(),
        session.getVersion().getService().getTheme().getId()
      };

      final String pathReady = createFolder(pathSessionFolder + File.separator + ".readyapi");
      createFolder(pathReady + File.separator + "logs");
      // Chemin du répertoire soapui.
      String pathSoapui = createFolder(pathSessionFolder + File.separator + ".soapui");
      // Chemin du répertoire des nomenclatures.
      String pathNomenclaturesFolder =
          createFolder(pathSessionFolder + File.separator + "nomenclatures");

      // Liste des arguments à passer au driver.
      List<String> args = new ArrayList<>();
      if (!session.getProjectResults().isEmpty()) {

        // itération sur les projets pour récupérer la liste des fichiers associés
        // nécessaires à l'execution des tests
        for (final ProjectResultDto rProjet : session.getProjectResults()) {
          final String pathRProjet =
              createFolder(pathProjetsFolder + File.separator + rProjet.getId());
          final Project projet = getProjectById(rProjet.getIdProject());
          final String pathProjetXml = pathRProjet + File.separator + projet.getFileName();
          if (!rProjet.getProjectProperties().isEmpty()) {
            // ajouter les properties dans le projet
            try {
              SoapuiProjectDocumentConfig soapUIProject =
                  SoapuiProjectDocumentConfig.Factory.parse(
                      new ByteArrayInputStream(projet.getFile()));
              mergePropertiesValues(rProjet, soapUIProject);
              soapUIProject.save(new File(pathProjetXml));
            } catch (XmlException e) {
              LOGGER.error("impossible de construire le fichier soapui", e);
              throw new ServiceException(e);
            }
          }
          if (projet.getTestCertificate() != null) {
            final String keystoreFolder = createFolder(pathRProjet + File.separator + "keystore");
            final String pathkeystore =
                keystoreFolder + File.separator + projet.getTestCertificate().getFileName();
            writeFile(pathkeystore, projet.getTestCertificate().getFile());
          }
          for (final RelatedFiles fichierAssocie : projet.getRelatedFiles()) {
            String pathFichierAssocies;
            // si schematron va dans dossier nomenclatures
            if (fichierAssocie.getFileName().endsWith("sch")) {
              pathFichierAssocies =
                  pathNomenclaturesFolder + File.separator + fichierAssocie.getFileName();
            } else {
              pathFichierAssocies = pathRProjet + File.separator + fichierAssocie.getFileName();
            }
            writeFile(pathFichierAssocies, fichierAssocie.getFile());
          }

          for (final Resource resource : resourceDao.getResourcesByAssociations(associationsIds)) {
            String pathFichierAssocies;
            // si schematron va dans dossier nomenclatures
            if (resource.getFileName().endsWith("sch")) {
              pathFichierAssocies =
                  pathNomenclaturesFolder + File.separator + resource.getFileName();
            } else {
              pathFichierAssocies = pathRProjet + File.separator + resource.getFileName();
            }
            writeFile(pathFichierAssocies, resource.getFile());
          }
          final File sessionFile = new File(pathSessionFolder);
          args.add(
              pathProjetXml.replace(
                  sessionFile.getParentFile().getAbsolutePath() + File.separator, ""));
        }
      } else {
        throw new ServiceException("La session " + session.getUuid() + "ne possède pas de projet");
      }

      writeFile(
          pathSessionFolder + File.separator + "soapui-settings.xml",
          IOUtils.toByteArray(
              Objects.requireNonNull(
                  SessionRunnerImpl.class
                      .getClassLoader()
                      .getResourceAsStream("soapui-settings.xml"))));

      // récupération des nomenclatures
      final Resource resourceNomenclature = resourceDao.getLastNomenclature(associationsIds);
      if (resourceNomenclature != null) {
        unzipNomenclature(resourceNomenclature, pathNomenclaturesFolder);
      }

      // Ajout des plugins soapui au dossier plugin
      pathSoapuiPlugins = createFolder(pathSoapui + File.separator + "plugins");
      addPlugins();

      // génération du zip
      final File fileSession = new File(pathSessionFolder);
      final ZipDirectory zipDirectory = new ZipDirectory();
      final boolean zipExist =
          zipDirectory.zipFiles(fileSession.getAbsolutePath(), pathSessionFolder + ".zip");
      if (zipExist) {
        deleteFolder(fileSession);
      }
      params = new SessionParameters(args, pathSessionFolder + ".zip", null);
      params.setApplicationRole(session.getApplication().getRole());
    } catch (final IOException e) {
      throw new ServiceException(
          e, "Erreur lors de la création de la session cliente " + session.getUuid());
    }
    return params;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public SessionParameters createServeurSession(TestSessionDto session) {
    SessionParameters params;
    try {
      init(session.getUuid());

      final long[] associationsIds = {
        session.getVersion().getId(),
        session.getVersion().getService().getId(),
        session.getVersion().getService().getTheme().getId()
      };

      ProjectResultDto projectResult;
      if (!session.getProjectResults().isEmpty()) {
        projectResult = session.getProjectResults().get(0);
      } else {
        throw new ServiceException("La session " + session.getUuid() + "ne possède pas de projet");
      }

      final String pathRProjet =
          createFolder(pathProjetsFolder + File.separator + projectResult.getId());
      mockProject = getProjectById(projectResult.getIdProject());

      final ProjectDetail projectDetail = projectBuilder.getProjectDetail(mockProject.getFile());
      createArborescence(projectResult.getId(), projectDetail);

      // Chemin du keystore.
      String keystoreArtifact = "";
      if (mockProject.getTestCertificate() != null) {
        keystoreArtifact = pathReadyFolder + File.separator + session.getUuid() + ".p12";
        writeFile(keystoreArtifact, mockProject.getTestCertificate().getFile());
      }
      // Truststore
      String truststoreArtifact = "";
      if (session.getChainOfTrustDto() != null) {
        truststoreArtifact =
            pathReadyFolder + File.separator + session.getUuid() + "-truststore.jks";
        final OutputStream certificateOutput = new FileOutputStream(truststoreArtifact);
        final KeyStore ks;
        ks = KeyStore.getInstance("JKS");
        char[] pwdArray = "changeit".toCharArray();
        ks.load(null, pwdArray);

        List<CertificateDto> ac = session.getChainOfTrustDto().getCertificate();
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        for (CertificateDto cert : ac) {
          int i = 1;
          try (InputStream input = new ByteArrayInputStream(cert.getPem().getBytes())) {
            ks.setCertificateEntry(Integer.toString(i), cf.generateCertificate(input));
            i++;
          }
        }

        ks.store(certificateOutput, pwdArray);
      }
      final InputStream strm = new ByteArrayInputStream(mockProject.getFile());

      SoapuiProjectDocumentConfig projectDoc = null;
      try {
        projectDoc = SoapuiProjectDocumentConfig.Factory.parse(strm);
      } catch (XmlException e) {
        LOGGER.error("erreur lors du parsing du descripteur SoapUI", e);
      } catch (IOException e) {
        LOGGER.error("impossible de lire le descripteur SoapUI", e);
      }

      final String pathProjetXml = pathRProjet + File.separator + mockProject.getFileName();
      mergePropertiesValues(projectResult, projectDoc);
      projectDoc.save(new File(pathProjetXml));
      final String warFile = pathReadyFolder + File.separator + session.getUuid() + ".war.zip";
      createMock(pathRProjet, mockProject.getFileName(), warFile, associationsIds);
      final File fileSession = new File(pathSessionFolder);
      deleteFolder(fileSession);
      params = new SessionParameters(warFile, keystoreArtifact);
      params.setTruststoreFile(truststoreArtifact);
      params.setApplicationRole(session.getApplication().getRole());
      String relaxedQueryChars =
          projectDoc.getSoapuiProject().getProperties().getPropertyList().stream()
              .filter(p -> "mock.relaxed.chars".equals(p.getName()))
              .map(p -> p.getValue())
              .findFirst()
              .orElse("");
      params.setRelaxedQueryChars(relaxedQueryChars);

    } catch (final IOException e) {
      throw new ServiceException(
          e, "Erreur lors de la création de la session serveur " + session.getUuid());
    } catch (final Exception e) {
      throw new ServiceException(e, "Erreur lors de la création du projet soapui ");
    }
    return params;
  }

  /**
   * creation des repertoires.
   *
   * @param uuidSession uuidSession
   * @throws IOException IOException
   */
  private void init(String uuidSession) throws IOException {

    pathReadyFolder = createFolder(pathReadyFolder + File.separator + "ready");
    pathSessionFolder = createFolder(pathReadyFolder + File.separator + uuidSession);
    pathProjetsFolder = createFolder(pathSessionFolder + File.separator + "projets");
  }

  private String createFolder(String folderName) throws IOException {
    final File folder = new File(folderName);
    if (!folder.exists()) {
      if (folder.mkdirs()) {
        if (OS.contains("win")) {
          LOGGER.info("Pas de permission à gérer sur les fichiers sous windows");
        } else {
          Files.setPosixFilePermissions(
              folder.toPath(),
              PosixFilePermissions.fromString(SoapuiSessionBuilder.PERMISSIONS_FILE));
        }
      } else {
        throw new ServiceException("Impossible to create folder : " + folderName);
      }
    }

    return folder.getAbsolutePath();
  }

  private void writeFile(String pathFile, byte[] input) {
    final File keystore = new File(pathFile);
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(keystore, false);
      fos.write(input);
    } catch (final IOException e) {
      throw new ServiceException(e, "Erreur lors de la création de la session");
    } finally {
      IOUtils.closeQuietly(fos);
    }
  }

  /**
   * Supprime un dossier.
   *
   * @param folder, le dossier à supprimer
   */
  private void deleteFolder(File folder) {
    final File[] files = folder.listFiles();
    if (files != null) {
      for (final File f : files) {
        if (f.isDirectory()) {
          deleteFolder(f);
        } else {
          f.delete();
        }
      }
    }
    folder.delete();
  }

  private void unzipNomenclature(Resource nomenclature, String pathNosFolder) throws IOException {
    File destDir = new File(pathNosFolder);
    byte[] buffer = new byte[1024];
    ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(nomenclature.getFile()));
    ZipEntry zipEntry;
    try {
      zipEntry = zis.getNextEntry();
    } catch (IOException e) {
      throw new ZipException("Failed to extract zip entry ");
    }
    while (zipEntry != null) {
      File newFile = newFile(destDir, zipEntry);

      if (zipEntry.isDirectory()) {
        if (!newFile.isDirectory() && !newFile.mkdirs()) {
          throw new ZipException("Failed to create directory ");
        }
      } else {
        // fix for Windows-created archives
        File parent = newFile.getParentFile();
        if (!parent.isDirectory() && !parent.mkdirs()) {
          throw new ZipException("Failed to create directory " + parent);
        }

        // write file content
        FileOutputStream fos = new FileOutputStream(newFile);
        int len;
        while ((len = zis.read(buffer)) > 0) {
          fos.write(buffer, 0, len);
        }
        fos.close();
      }
      zipEntry = zis.getNextEntry();
    }

    zis.closeEntry();
    zis.close();
  }

  private File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
    File destFile = new File(destinationDir, zipEntry.getName());

    String destDirPath = destinationDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
      throw new ZipException("Entry is outside of the target dir: " + zipEntry.getName());
    }

    return destFile;
  }

  private void addPlugins() throws IOException {
    final ClassLoader cl = Thread.currentThread().getContextClassLoader();
    final Enumeration<URL> libUrl = cl.getResources("META-INF/MANIFEST.MF");
    final JarManifestURLAccumulator jarListBuilder = new JarManifestURLAccumulator();
    libUrl.asIterator().forEachRemaining(jarListBuilder);

    for (URL jarUrl : jarListBuilder) {
      String urlPath = jarUrl.getPath();
      String jarName = urlPath.substring(urlPath.lastIndexOf('/') + 1);
      if (jarName.startsWith(PLUGIN_PATTERN)) {
        try (InputStream in = jarUrl.openStream();
            OutputStream out =
                new FileOutputStream(new File(pathSoapuiPlugins + File.separator + jarName)); ) {
          FileCopyUtils.copy(in, out);
        }
      }
    }
  }

  private void mergePropertiesValues(
      ProjectResultDto rProjet, SoapuiProjectDocumentConfig soapuiProject) {
    final List<ProjectResultPropertyDto> proprieteSessions = rProjet.getProjectProperties();
    List<PropertyConfig> properties =
        soapuiProject.getSoapuiProject().getProperties().getPropertyList();
    for (final ProjectResultPropertyDto proprieteSession : proprieteSessions) {
      properties.replaceAll(
          p ->
              p.getName().equals(proprieteSession.getKey())
                  ? convertProperty(proprieteSession)
                  : p);
    }
  }

  private PropertyConfig convertProperty(ProjectResultPropertyDto property) {
    PropertyConfig propertyConfig = PropertyConfig.Factory.newInstance();
    propertyConfig.setName(property.getKey());
    propertyConfig.setValue(property.getValue());
    return propertyConfig;
  }

  private void createMock(
      String pathRProjet, String projectFileName, String warFile, long[] associationsIds) {
    final War war =
        new War(
            new File(warFile),
            new File(pathRProjet),
            projectFileName,
            mockProject.getRelatedFiles(),
            resourceDao.getResourcesByAssociations(associationsIds));
    war.create();
  }

  /**
   * Gets project by its id.
   *
   * @param idProject, the project id
   * @return the project
   */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public Project getProjectById(Long idProject) {
    return projectDao.getById(idProject);
  }

  private ProjectResult getProjectResult(Long idRProject) {
    return iProjectResultDao.getByIdWithoutToken(idRProject);
  }

  private void createArborescence(Long idProjectResult, ProjectDetail projectDetail) {
    final ProjectResult projectResult = new ProjectResult();
    projectResult.setId(idProjectResult);
    for (final TestSuiteDetail testSuiteDetail : projectDetail.getTestSuiteDetail()) {
      TestSuiteResult testSuiteResult = new TestSuiteResult();
      testSuiteResult.setDescription(testSuiteDetail.getDescription());
      testSuiteResult.setName(testSuiteDetail.getName());
      testSuiteResult.setResultStatus(ResultStatus.NONEXECUTE);
      testSuiteResult.setProjectResult(getProjectResult(idProjectResult));
      testSuiteResult = iTestSuiteDao.persistAndGet(testSuiteResult);
      for (final TestCaseDetail testCaseDetail : testSuiteDetail.getListTestCase()) {
        final TestCaseResult caseResult = new TestCaseResult();
        caseResult.setName(testCaseDetail.getName());
        caseResult.setResponseName(testCaseDetail.getResponseName());
        caseResult.setDescription(testCaseDetail.getDescription());
        caseResult.setResultStatus(ResultStatus.NONEXECUTE);
        caseResult.setTestSuite(testSuiteResult);
        iTestCaseDao.persistAndGet(caseResult);
      }
    }
  }
}
