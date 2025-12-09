/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dao.IProjectDao;
import fr.asipsante.platines.dao.IPropertyDao;
import fr.asipsante.platines.dao.IRelatedFiles;
import fr.asipsante.platines.dao.IResourceDao;
import fr.asipsante.platines.dao.ISimulatedServiceDao;
import fr.asipsante.platines.dao.ITestCertificateDao;
import fr.asipsante.platines.dao.IVersionDao;
import fr.asipsante.platines.dto.FichierT;
import fr.asipsante.platines.dto.FileDownloaded;
import fr.asipsante.platines.dto.ProjectDto;
import fr.asipsante.platines.dto.ProjectListDto;
import fr.asipsante.platines.dto.PropertyDto;
import fr.asipsante.platines.dto.RelatedFilesDto;
import fr.asipsante.platines.dto.ResourceBytesDto;
import fr.asipsante.platines.entity.Project;
import fr.asipsante.platines.entity.Property;
import fr.asipsante.platines.entity.RelatedFiles;
import fr.asipsante.platines.entity.Resource;
import fr.asipsante.platines.entity.Version;
import fr.asipsante.platines.entity.enums.FileType;
import fr.asipsante.platines.executor.ProjectBuilder;
import fr.asipsante.platines.executor.model.ProjectDetail;
import fr.asipsante.platines.service.ProjectLibraryService;
import fr.asipsante.platines.service.mapper.ProjectDtoMapper;
import fr.asipsante.platines.service.mapper.PropertyDtoMapper;
import fr.asipsante.platines.service.mapper.RelatedFilesDtoMapper;
import fr.asipsante.platines.service.mapper.ResourceBytesDtoMapper;
import fr.asipsante.platines.service.mapper.SimulatedServiceDtoMapper;
import fr.asipsante.platines.service.mapper.TestCertificateDtoMapper;
import fr.asipsante.platines.service.mapper.ThemeDtoMapper;
import fr.asipsante.platines.service.mapper.VersionDtoMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * The project library service.
 *
 * @author apierre
 */
@Service(value = "ProjectLibraryService")
public class ProjectLibraryServiceImpl implements ProjectLibraryService {

  /** LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectLibraryServiceImpl.class);

  /** Test certificate DAO. */
  @Autowired
  @Qualifier("testCertificateDao")
  private ITestCertificateDao testCertificateDao;

  /** ISimulatedServiceDao. */
  @Autowired
  @Qualifier("simulatedServiceDao")
  private ISimulatedServiceDao simulatedServiceDao;

  /** IProjectDao. */
  @Autowired
  @Qualifier("projectDao")
  private IProjectDao projectDao;

  /** IVersionDao. */
  @Autowired
  @Qualifier("versionDao")
  private IVersionDao versionDao;

  /** IResourceDao. */
  @Autowired
  @Qualifier("resourceDao")
  private IResourceDao resourceDao;

  /** Entity - DTO converters. */
  @Autowired
  @Qualifier("testCertificateDtoMapper")
  private TestCertificateDtoMapper testCertificateDtoMapper;

  @Autowired
  @Qualifier("familyDtoMapper")
  private ThemeDtoMapper familyDtoMapper;

  @Autowired
  @Qualifier("simulatedServiceDtoMapper")
  private SimulatedServiceDtoMapper simulatedServiceDtoMapper;

  @Autowired
  @Qualifier("projectDtoMapper")
  private ProjectDtoMapper projectDtoMapper;

  @Autowired
  @Qualifier("versionDtoMapper")
  private VersionDtoMapper versionDtoMapper;

  @Autowired
  @Qualifier("propertyDtoMapper")
  private PropertyDtoMapper propertyDtoMapper;

  @Autowired
  @Qualifier("relatedFilesDtoMapper")
  private RelatedFilesDtoMapper relatedFilesDtoMapper;

  @Autowired
  @Qualifier("resourceBytesDtoMapper")
  private ResourceBytesDtoMapper resourceBytesDtoMapper;

  /** IRelatedFiles. */
  @Autowired
  @Qualifier("relatedFilesDao")
  private IRelatedFiles relatedFilesDao;

  /** IPropertyDao. */
  @Autowired
  @Qualifier("propertyDao")
  private IPropertyDao propertyDao;

  /** DateConverter. */
  @Autowired private DateConverter dateConverter;

  @Autowired private ProjectBuilder parser;

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<ProjectListDto> getAllProjects() {
    final List<Project> projects = projectDao.getAll();
    final List<ProjectListDto> projectListDtos = new ArrayList<>();
    projects.forEach(
        project -> projectListDtos.add(projectDtoMapper.converToProjectListDto(project)));
    return projectListDtos;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<ProjectListDto> getProjectsByVersion(Long idVersion) {
    final List<Project> projects = projectDao.getProjectsByVersion(idVersion);
    final List<ProjectListDto> projectListDtos = new ArrayList<>();
    projects.forEach(
        project -> projectListDtos.add(projectDtoMapper.converToProjectListDto(project)));
    return projectListDtos;
  }

  /** {@inheritDoc} */
  @Override
  public ProjectDetail analyze(byte[] projectFile) {
    return parser.getProjectDetail(projectFile);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteProject(Long idProject) {
    final Project project = projectDao.getById(idProject);
    projectDao.delete(project);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public void createProject(
      ProjectDto projectDto,
      MultipartFile[] relatedFiles,
      MultipartFile projectFile,
      List<FichierT> fileType)
      throws IOException {
    Project project = new Project();
    final List<PropertyDto> properties = projectDto.getProperties();
    project.setDescription(projectDto.getDescription());
    project.setName(projectDto.getName());
    project.setRole(projectDto.getRole());
    project.setDateUpload(dateConverter.convertToUTC(new Date()));
    if (projectDto.getTestCertificate() != null) {
      project.setTestCertificate(
          testCertificateDtoMapper.convertToEntity(projectDto.getTestCertificate()));
    }
    project.setVisibility(projectDto.isVisibility());
    project.setFile(projectFile.getBytes());
    project.setFileName(projectFile.getOriginalFilename());
    final Version version = versionDao.getById(projectDto.getVersions().iterator().next().getId());
    project.setVersions(new ArrayList<>());
    project.getVersions().add(version);

    project = projectDao.saveAndGet(project);

    project.setProperties(new ArrayList<>());
    for (final PropertyDto prop : properties) {
      final Property property = propertyDao.saveAndGet(propertyDtoMapper.convertToEntity(prop));
      project.getProperties().add(property);
      projectDao.update(project);
    }
    project.setRelatedFiles(new ArrayList<>());
    if (relatedFiles != null)
      for (final MultipartFile multipartFile : relatedFiles) {
        RelatedFiles relatedFile = new RelatedFiles();
        relatedFile.setFileName(multipartFile.getOriginalFilename());
        relatedFile.setFile(multipartFile.getBytes());
        relatedFile.setDateUpload(dateConverter.convertToUTC(new Date()));

        relatedFile.setFileType(
            FileType.valueOf(
                fileType.stream()
                    .filter(f -> f.getFileName().equals(multipartFile.getOriginalFilename()))
                    .iterator()
                    .next()
                    .getFileType()));

        relatedFile = relatedFilesDao.saveAndGet(relatedFile);
        project.getRelatedFiles().add(relatedFile);
        projectDao.update(project);
      }
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public void updateProject(
      ProjectDto projectDto,
      MultipartFile[] relatedFiles,
      MultipartFile projectFile,
      List<FichierT> fileType)
      throws IOException {
    Project project = projectDao.getById(projectDto.getId());
    project.setDescription(projectDto.getDescription());
    project.setName(projectDto.getName());
    project.setRole(projectDto.getRole());
    project.setDateUpload(dateConverter.convertToUTC(new Date()));
    if (projectDto.getTestCertificate() != null) {
      project.setTestCertificate(
          testCertificateDtoMapper.convertToEntity(projectDto.getTestCertificate()));
    } else {
      project.setTestCertificate(null);
    }
    project.setVisibility(projectDto.isVisibility());
    if (projectFile != null) {
      project.setFile(projectFile.getBytes());
      project.setFileName(projectFile.getOriginalFilename());
    }

    if (relatedFiles != null) {
      for (final MultipartFile multipartFiles : relatedFiles) {
        final Predicate<RelatedFiles> pred =
            f -> f.getFileName().equals(multipartFiles.getOriginalFilename());
        if (project.getRelatedFiles().stream().noneMatch(pred)) {
          RelatedFiles relatedFile = new RelatedFiles();
          relatedFile.setFileName(multipartFiles.getOriginalFilename());
          relatedFile.setFile(multipartFiles.getBytes());
          relatedFile.setDateUpload(dateConverter.convertToUTC(new Date()));
          final Predicate<FichierT> pred2 =
              f -> f.getFileName().equals(multipartFiles.getOriginalFilename());
          Optional<FichierT> originalFileName = fileType.stream().filter(pred2).findFirst();
          if (originalFileName.isPresent() && fileType.stream().filter(pred2).count() == 1) {
            final FichierT fichierT2 = originalFileName.get();
            relatedFile.setFileType(FileType.valueOf(fichierT2.getFileType()));
          }
          relatedFile = relatedFilesDao.saveAndGet(relatedFile);
          project.getRelatedFiles().add(relatedFile);
          relatedFilesDao.save(relatedFile);
          projectDao.update(project);
        } else {
          final RelatedFiles file =
              project.getRelatedFiles().stream().filter(pred).findFirst().get();
          file.setFile(multipartFiles.getBytes());
          file.setDateUpload(dateConverter.convertToUTC(new Date()));
          file.setFileName(multipartFiles.getOriginalFilename());
          relatedFilesDao.update(file);
        }
      }
    }

    final List<RelatedFiles> relatedFilesList = project.getRelatedFiles();
    for (final RelatedFiles file : relatedFilesList) {
      if (fileType.stream().noneMatch(f -> f.getFileName().equals(file.getFileName()))) {
        relatedFilesDao.delete(file);
      } else {
        file.setFileType(
            FileType.valueOf(
                fileType.stream()
                    .filter(f -> f.getFileName().equals(file.getFileName()))
                    .findFirst()
                    .get()
                    .getFileType()));
      }
    }

    for (final Property propriety : project.getProperties()) {
      final Predicate<PropertyDto> pred = p -> p.getKey().equals(propriety.getKey());
      if (projectDto.getProperties().stream().noneMatch(pred)) {
        propertyDao.delete(propriety);
      }
    }

    for (final PropertyDto propertyDto : projectDto.getProperties()) {
      final Predicate<Property> pred = p -> p.getKey().equals(propertyDto.getKey());
      if (project.getProperties().stream().noneMatch(pred)) {
        propertyDao.saveAndGet(propertyDtoMapper.convertToEntity(propertyDto));
      } else {
        final Property property = project.getProperties().stream().filter(pred).findFirst().get();
        property.setDescription(propertyDto.getDescription());
        property.setKey(propertyDto.getKey());
        property.setValue(propertyDto.getValue());
        property.setPropertyType(propertyDto.getPropertyType());
        propertyDao.update(property);
      }
    }
  }

  @Override
  @Transactional
  public void updateProject(Project project) throws IOException {
    projectDao.update(project);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public ProjectDto getProjectById(Long idProject) {
    final Project project = projectDao.getById(idProject);
    final ProjectDto dto = new ProjectDto();
    dto.setDescription(project.getDescription());
    dto.setId(project.getId());
    dto.setFileName(project.getFileName());
    dto.setName(project.getName());
    dto.setRole(project.getRole());
    if (project.getTestCertificate() != null) {
      dto.setTestCertificate(testCertificateDtoMapper.convertToDto(project.getTestCertificate()));
    }
    dto.setVisibility(project.getVisibility());
    dto.setVersions(new ArrayList<>());
    for (final Version version : project.getVersions()) {
      dto.getVersions().add(versionDtoMapper.convertToVersionDto(version));
    }
    dto.setRelatedFiles(new ArrayList<>());
    for (final RelatedFiles relatedFiles : project.getRelatedFiles()) {
      final RelatedFilesDto relatedFilesDto = new RelatedFilesDto();
      relatedFilesDto.setId(relatedFiles.getId());
      relatedFilesDto.setFileName(relatedFiles.getFileName());
      relatedFilesDto.setFileType(relatedFiles.getFileType());
      relatedFilesDto.setDateUpload(relatedFiles.getDateUpload());
      dto.getRelatedFiles().add(relatedFilesDto);
    }
    dto.setProperties(new ArrayList<>());
    for (final Property prop : project.getProperties()) {
      dto.getProperties().add(propertyDtoMapper.convertToDto(prop));
    }

    return dto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional
  public FileDownloaded getFilesOfProject(Long id) throws IOException {
    final Project projet = projectDao.getById(id);
    String zipPath;
    final String projetFileName = projet.getFileName();
    final byte[] projetFile = projet.getFile();

    final Map<String, byte[]> files = new HashMap<>();

    for (final RelatedFiles relatedFiles : projet.getRelatedFiles()) {
      files.put(relatedFiles.getFileName(), relatedFiles.getFile());
    }

    final File temporyFile = File.createTempFile("temp", "dir");
    zipPath = temporyFile.getPath() + projetFileName + ".zip";
    final ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(zipPath));
    final ZipEntry projetEntry = new ZipEntry(projetFileName);
    zip.putNextEntry(projetEntry);
    zip.write(projetFile, 0, projetFile.length);
    zip.closeEntry();
    ZipEntry schEntry;

    for (final Map.Entry<String, byte[]> entry : files.entrySet()) {
      schEntry = new ZipEntry(entry.getKey());
      zip.putNextEntry(schEntry);
      zip.write(entry.getValue(), 0, entry.getValue().length);
      zip.closeEntry();
    }

    zip.close();

    final byte[] zipByte = Files.readAllBytes(Paths.get(zipPath));
    final File zipFile = new File(zipPath);

    final FileDownloaded zipDto = new FileDownloaded();
    zipDto.setFile(zipByte);
    zipDto.setFileName(projet.getName() + ".zip");
    zipFile.delete();
    temporyFile.delete();
    return zipDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public ProjectDetail getDetailOfProject(Long idProject) {
    LOGGER.debug("Starting to get detail of project at {}", new Date());
    final Project project = projectDao.getById(idProject);
    LOGGER.debug("Got the detail of project at {}", new Date());
    final Set<PropertyDto> properties = new HashSet<>();
    for (final Property property : project.getProperties()) {
      final PropertyDto propertyDto = propertyDtoMapper.convertToDto(property);
      properties.add(propertyDto);
    }
    LOGGER.debug("Getting project file at {}", new Date());
    byte[] projectFile = project.getFile();
    LOGGER.debug("Got the project file at {}", new Date());
    final ProjectDetail projectDetail = parser.getProjectDetail(projectFile);
    LOGGER.debug("Parsed the project file at {}", new Date());
    projectDetail.setProperties(properties);
    return projectDetail;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public List<RelatedFilesDto> getRelatedFilesOfProject(Long id) {
    final List<RelatedFilesDto> filesDto = new ArrayList<>();
    final Project project = projectDao.getById(id);
    if (!project.getRelatedFiles().isEmpty()) {
      final List<RelatedFiles> files = project.getRelatedFiles();
      files.forEach(
          file -> {
            if (file.getFileType().equals(FileType.DOCUMENT)) {
              final RelatedFilesDto fileDto = new RelatedFilesDto();
              fileDto.setId(file.getId());
              fileDto.setFileName(file.getFileName());
              filesDto.add(fileDto);
            }
          });
    }
    return filesDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public RelatedFilesDto getDocumentById(Long idFile) {
    RelatedFilesDto file = null;
    try {
      file = relatedFilesDtoMapper.convertToRelatedFileDto(relatedFilesDao.getDocumentById(idFile));
      if (file.getFile() != null) {
        final MagicMatch match = Magic.getMagicMatch(file.getFile());
        final String mimeType = match.getMimeType();
        file.setMimeType(mimeType);
        LOGGER.debug("mime type du fichier : {}", mimeType);
      }
    } catch (final MagicMatchNotFoundException e) {
      file.setMimeType("text/document");
    } catch (MagicParseException | MagicException e) {
      LOGGER.error("problème lors de la récupération du mimeType du fichier", e);
    }
    return file;
  }

  /** {@inheritDoc} */
  @Override
  public List<ResourceBytesDto> getProjectResourceDocuments(Long id) {
    final List<ResourceBytesDto> resourcesDto = new ArrayList<>();
    final Project project = projectDao.getById(id);
    if (!project.getVersions().isEmpty()) {
      final List<Resource> resourcesDao =
          Stream.of(
                  resourceDao.getResourcesByAssociation(project.getVersions().get(0).getId()),
                  resourceDao.getResourcesByAssociation(
                      project.getVersions().get(0).getService().getId()),
                  resourceDao.getResourcesByAssociation(
                      project.getVersions().get(0).getService().getTheme().getId()))
              .flatMap(Collection::stream)
              .collect(Collectors.toList());
      resourcesDao.forEach(
          resource -> {
            if (resource.getFileType().equals(FileType.DOCUMENT)) {
              final ResourceBytesDto fileDto = new ResourceBytesDto();
              fileDto.setId(resource.getId());
              fileDto.setFileName(resource.getFileName());
              resourcesDto.add(fileDto);
            }
          });
    }
    return resourcesDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(readOnly = true)
  public ResourceBytesDto getProjectResourceDocumentById(Long idResource) {
    ResourceBytesDto resourceDoc = null;
    try {
      resourceDoc = resourceBytesDtoMapper.convertToDto(resourceDao.getById(idResource));
      if (resourceDoc.getFile() != null) {
        final MagicMatch match = Magic.getMagicMatch(resourceDoc.getFile());
        final String mimeType = match.getMimeType();
        resourceDoc.setMimeType(mimeType);
        LOGGER.debug("mime type du fichier : {}", mimeType);
      }
    } catch (final MagicMatchNotFoundException e) {
      resourceDoc.setMimeType("text/document");
    } catch (MagicParseException | MagicException e) {
      LOGGER.error("problème lors de la récupération du mimeType du fichier", e);
    }
    return resourceDoc;
  }

  @Override
  public List<Project> getProjectsForFileNameInTheme(String projectFileName, Long themeId) {
    final List<Project> projectByFileName = projectDao.getProjectByFileName(projectFileName);
    return projectByFileName.stream()
        .filter(
            project ->
                project.getVersions().stream()
                    .anyMatch(version -> version.getService().getTheme().getId().equals(themeId)))
        .collect(Collectors.toList());
  }
}
