/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * @author aboittiaux
 */
public class CertificateRequestDto {

  /** The id chaine. */
  private Long idChaine;

  /** The id chaine. */
  private Long id;

  /** The pem. */
  private String pem;

  /** Constructeur. */
  public CertificateRequestDto() {
    super();
  }

  /**
   * @return the idChaine
   */
  public Long getIdChaine() {
    return idChaine;
  }

  /**
   * @param idChaine the idChaine to set
   */
  public void setIdChaine(Long idChaine) {
    this.idChaine = idChaine;
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
   * @return the pem
   */
  public String getPem() {
    return pem;
  }

  /**
   * @param pem the pem to set
   */
  public void setPem(String pem) {
    this.pem = pem;
  }
}
