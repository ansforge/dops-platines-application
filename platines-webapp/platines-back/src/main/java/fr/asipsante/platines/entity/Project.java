/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.entity.listeners.VisibilityListener;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.springframework.security.acls.model.Sid;

/**
 * Class Project corresponding to the table "projet" in the database.
 *
 * @author apierre
 */
@Entity
@Table(name = "projet")
@EntityListeners(VisibilityListener.class)
public class Project extends AbstractEntity implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 0L;

  /** The project name. */
  @Column(name = "nom", length = 50)
  private String name;

  /** The project description. */
  @Column(name = "description", columnDefinition = "TEXT")
  private String description;

  /** The project file name. */
  @Column(name = "nomFichier", length = 150)
  private String fileName;

  /** The project file. */
  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Column(name = "fichier", columnDefinition = "longblob")
  private byte[] file;

  /** The project visibility. */
  @Column(name = "visibilite")
  private boolean visibility;

  /** The project role. */
  @Enumerated(EnumType.STRING)
  @Column(name = "role", length = 25)
  private Role role;

  /** The versions. */
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "version_project",
      joinColumns = {@JoinColumn(name = "PROJECT_ID", nullable = true, updatable = true)},
      inverseJoinColumns = {@JoinColumn(name = "VERSION_ID", nullable = true, updatable = true)})
  private List<Version> versions;

  /** The related files. */
  @OneToMany(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "PROJECT_ID")
  private List<RelatedFiles> relatedFiles;

  /** The test certificates. */
  @ManyToOne
  @JoinColumn(name = "CERTIFICAT_ID")
  private TestCertificate testCertificate;

  /** The project properties. */
  @OneToMany(cascade = CascadeType.REMOVE)
  @JoinColumn(name = "PROJECT_ID")
  private List<Property> properties;

  /** upload date. */
  @Column(name = "date_upload")
  private Date dateUpload;

  /**
   * Gets the project name.
   *
   * @return the project name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the project name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the project description.
   *
   * @return the project description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the project description.
   *
   * @param description the description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the project versions.
   *
   * @return the project versions
   */
  public List<Version> getVersions() {
    return versions;
  }

  /**
   * Sets the project versions.
   *
   * @param versions the versions
   */
  public void setVersions(List<Version> versions) {
    this.versions = versions;
  }

  /**
   * Gets the test certificate.
   *
   * @return the test certificate
   */
  public TestCertificate getTestCertificate() {
    return testCertificate;
  }

  /**
   * Sets the test certificate.
   *
   * @param testCertificate the test certificate
   */
  public void setTestCertificate(TestCertificate testCertificate) {
    this.testCertificate = testCertificate;
  }

  /**
   * Gets the project file name.
   *
   * @return the file name
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Sets the project file name.
   *
   * @param fileName the file name
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Gets the project file.
   *
   * @return the project file
   */
  public byte[] getFile() {
    return file;
  }

  /**
   * Sets the project file.
   *
   * @param file the file
   */
  public void setFile(byte[] file) {
    this.file = file;
  }

  /**
   * Gets the project visibility.
   *
   * @return true, if is visible by user, false if is visible by admin
   */
  public boolean getVisibility() {
    return visibility;
  }

  /**
   * Sets the visibility.
   *
   * @param visibility the visibility
   */
  public void setVisibility(boolean visibility) {
    this.visibility = visibility;
  }

  /**
   * Gets the project role.
   *
   * @return the project role
   */
  public Role getRole() {
    return role;
  }

  /**
   * Sets the project role.
   *
   * @param role the role
   */
  public void setRole(Role role) {
    this.role = role;
  }

  /**
   * Gets the related files.
   *
   * @return the related files
   */
  public List<RelatedFiles> getRelatedFiles() {
    return relatedFiles;
  }

  /**
   * Sets the related files.
   *
   * @param relatedFiles the related files
   */
  public void setRelatedFiles(List<RelatedFiles> relatedFiles) {
    this.relatedFiles = relatedFiles;
  }

  /**
   * Gets the project properties.
   *
   * @return the properties
   */
  public List<Property> getProperties() {
    return properties;
  }

  /**
   * Sets the project properties.
   *
   * @param properties the properties
   */
  public void setProperties(List<Property> properties) {
    this.properties = properties;
  }

  /**
   * Gets date upload.
   *
   * @return the dateUpload
   */
  public Date getDateUpload() {
    return dateUpload;
  }

  /**
   * Sets date upload.
   *
   * @param dateUpload the dateUpload to set
   */
  public void setDateUpload(Date dateUpload) {
    this.dateUpload = dateUpload;
  }

  @Override
  public Sid getOwner() {
    return null;
  }
}
