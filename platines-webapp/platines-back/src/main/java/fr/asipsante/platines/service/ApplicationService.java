/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.dto.ApplicationDto;
import fr.asipsante.platines.dto.ApplicationListDto;
import fr.asipsante.platines.dto.CertificateDto;
import fr.asipsante.platines.dto.ChainOfTrustDto;
import fr.asipsante.platines.dto.ChainOfTrustListDto;
import fr.asipsante.platines.entity.Application;
import fr.asipsante.platines.entity.Certificate;
import fr.asipsante.platines.entity.ChainOfTrust;
import fr.asipsante.platines.mail.entity.CertificateState;
import java.util.List;

/**
 * Application service. Methods for entities {@link Application}, {@link ChainOfTrust}, {@link
 * Certificate}
 *
 * @author aboittiaux
 */
public interface ApplicationService {

  /**
   * Gets all the applications.
   *
   * @return the application list
   */
  List<ApplicationListDto> getApplications();

  /**
   * Gets all user applications.
   *
   * @param idUser the id user
   * @return the application list
   */
  List<ApplicationListDto> getApplicationsByUser(Long idUser);

  /**
   * Gets an application by its id.
   *
   * @param applicationId the application id
   * @return the application
   */
  ApplicationDto getById(Long applicationId);

  /**
   * Creates an application in database.
   *
   * @param applicationDto the application dto
   */
  void createApplication(ApplicationDto applicationDto);

  /**
   * Updates an application in database.
   *
   * @param applicationDto the application dto
   */
  void updateApplication(ApplicationDto applicationDto);

  /**
   * Deletes an application.
   *
   * @param idApplication the id application
   */
  void deleteApplication(Long idApplication);

  /**
   * Creates a trust chain.
   *
   * @param chainOfTrustDto the chain of trust dto
   * @return the id
   */
  Long createChainOfTrust(ChainOfTrustDto chainOfTrustDto);

  /**
   * Updates a trust chain.
   *
   * @param chainOfTrustDto the chain of trust dto
   */
  void updateChainOfTrust(ChainOfTrustDto chainOfTrustDto);

  /**
   * Deletes a trust chain.
   *
   * @param idChainOfTrust the id chain of trust
   */
  void deleteChainOfTrust(Long idChainOfTrust);

  /**
   * Gets all the chain of trust.
   *
   * @return a list of chain of trust
   */
  List<ChainOfTrustListDto> getChainOfTrustListDto();

  /**
   * Gets the chain of trust for an user.
   *
   * @param idUser the id user
   * @return a chain of trust list
   */
  List<ChainOfTrustListDto> getChainOfTrustListDtoByUser(Long idUser);

  /**
   * Gets a chain of trust by its id.
   *
   * @param idChain the id chain
   * @return the chain of trust
   */
  ChainOfTrustDto getChainOfTrustById(Long idChain);

  /**
   * Creates a certificate.
   *
   * @param certificateDto the certificate dto
   * @return the certificate state
   */
  CertificateState createCertificate(CertificateDto certificateDto);

  /**
   * Updates a certificate in database.
   *
   * @param certificateDto the certificate dto
   * @return the certificate state
   */
  CertificateState updateCertificate(CertificateDto certificateDto);

  /**
   * Deletes a certificate.
   *
   * @param idCertficate the id certficate
   */
  void deleteCertificate(Long idCertficate);

  /**
   * Gets a certificate by its id.
   *
   * @param idCertificate the id certificate
   * @return the certificate
   */
  CertificateDto getCertificateById(Long idCertificate);
}
