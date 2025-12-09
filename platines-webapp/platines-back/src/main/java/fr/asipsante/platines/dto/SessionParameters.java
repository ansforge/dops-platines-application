/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

import fr.asipsante.platines.entity.enums.Role;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @author aboittiaux
 */
@Getter
@Setter
public class SessionParameters {

  /** uuid de la session */
  private String uuidSession;

  /** application role */
  private Role applicationRole;

  /** authorized IP for MockServices */
  private String authorizedIP;

  /** mTLS Flag */
  private boolean mTls;

  /** commandline arguments. */
  private List<String> arguments;

  /** file artifact. */
  private String fileArtifact;

  /** keystore file. */
  private String keystoreFile;

  /** truststore file */
  private String truststoreFile;

  /**
   * Relaxed query chars : list of characters to accept in mock server query parameters without
   * encoding among RFC HTTP 1.1 forbidden characters. @See
   * https://tomcat.apache.org/tomcat-9.0-doc/config/http.html for the definition of tomcat HTTP
   * connector parameter `relaxedQueryChars`
   */
  private String relaxedQueryChars;

  /**
   * @param arguments, arguments
   * @param fileArtifact, file artifact
   * @param keystoreFile, keystore
   */
  public SessionParameters(List<String> arguments, String fileArtifact, String keystoreFile) {
    super();
    this.arguments = arguments;
    this.fileArtifact = fileArtifact;
    this.keystoreFile = keystoreFile;
  }

  public SessionParameters(String fileArtifact, String keystoreFile) {
    this(null, fileArtifact, keystoreFile);
  }

  /**
   * @return the arguments
   */
  public List<String> getArguments() {
    return arguments;
  }

  /**
   * @param arguments the arguments to set
   */
  public void setArguments(List<String> arguments) {
    this.arguments = arguments;
  }

  /**
   * @return the fileArtifact
   */
  public String getFileArtifact() {
    return fileArtifact;
  }

  /**
   * @param fileArtifact the fileArtifact to set
   */
  public void setFileArtifact(String fileArtifact) {
    this.fileArtifact = fileArtifact;
  }

  /**
   * @return the keystoreFile
   */
  public String getKeystoreFile() {
    return keystoreFile;
  }

  /**
   * @param keystoreFile the keystoreFile to set
   */
  public void setKeystoreFile(String keystoreFile) {
    this.keystoreFile = keystoreFile;
  }

  public String getTruststoreFile() {
    return truststoreFile;
  }

  public void setTruststoreFile(String truststoreFile) {
    this.truststoreFile = truststoreFile;
  }
}
