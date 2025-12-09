/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.Role;
import java.util.List;

/**
 * @author aboittiaux
 */
public class ProjectDto {

  /** The project id. */
  private Long id;

  /** The project name. */
  private String name;

  /** The project description. */
  private String description;

  /** The project file name. */
  private String fileName;

  /** The project file. */
  private byte[] file;

  /** The project visibility. */
  private boolean visibility;

  /** The project role. */
  private Role role;

  /** The project versions. */
  private List<VersionDto> versions;

  /** The project related Files. */
  private List<RelatedFilesDto> relatedFiles;

  /** The test certificate. */
  private TestCertificateDto testCertificate;

  /** The project properties. */
  private List<PropertyDto> properties;

  /** Constructor. */
  public ProjectDto() {
    super();
  }

  /**
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * @param fileName the fileName to set
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * @return the file
   */
  public byte[] getFile() {
    return file;
  }

  /**
   * @param file the file to set
   */
  public void setFile(byte[] file) {
    this.file = file;
  }

  /**
   * @return the visibility
   */
  public boolean isVisibility() {
    return visibility;
  }

  /**
   * @param visibility the visibility to set
   */
  public void setVisibility(boolean visibility) {
    this.visibility = visibility;
  }

  /**
   * @return the role
   */
  public Role getRole() {
    return role;
  }

  /**
   * @param role the role to set
   */
  public void setRole(Role role) {
    this.role = role;
  }

  /**
   * @return the versions
   */
  public List<VersionDto> getVersions() {
    return versions;
  }

  /**
   * @param versions the versions to set
   */
  public void setVersions(List<VersionDto> versions) {
    this.versions = versions;
  }

  /**
   * @return the relatedFiles
   */
  public List<RelatedFilesDto> getRelatedFiles() {
    return relatedFiles;
  }

  /**
   * @param relatedFiles the relatedFiles to set
   */
  public void setRelatedFiles(List<RelatedFilesDto> relatedFiles) {
    this.relatedFiles = relatedFiles;
  }

  /**
   * @return the testCertificate
   */
  public TestCertificateDto getTestCertificate() {
    return testCertificate;
  }

  /**
   * @param testCertificate the testCertificate to set
   */
  public void setTestCertificate(TestCertificateDto testCertificate) {
    this.testCertificate = testCertificate;
  }

  /**
   * @return the properties
   */
  public List<PropertyDto> getProperties() {
    return properties;
  }

  /**
   * @param properties the properties to set
   */
  public void setProperties(List<PropertyDto> properties) {
    this.properties = properties;
  }
}
