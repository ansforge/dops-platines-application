/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.dto.PemTestCertificate;
import fr.asipsante.platines.dto.TestCertificateDto;
import java.util.Date;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface TestCertificateService {

  /**
   * Gets all test certificates register in the database.
   *
   * @return all the tests certificates DTO.
   */
  List<TestCertificateDto> getAllTestCertificates();

  /**
   * Gets a test Certificate by is id.
   *
   * @param id the id
   * @return the test certificate corresponding to the id
   */
  TestCertificateDto getTestCertificateById(Long id);

  /**
   * Deletes a test Certificate by is id.
   *
   * @param id the id
   */
  void deleteTestCertificateById(Long id);

  /**
   * Persists a test certificate.
   *
   * @param testCertificateDto the test certificate dto
   */
  void saveTestCertificate(TestCertificateDto testCertificateDto);

  /**
   * Updates a test certificate.
   *
   * @param testCertificateDto the test certificate dto
   */
  void updateTestCertificate(TestCertificateDto testCertificateDto);

  /**
   * Gets the end validity date for a certificate p12.
   *
   * @param certificateFile the certificate file
   * @param password the password
   * @return the certificate end validity date.
   */
  Date getCertificateDateEndValidity(MultipartFile certificateFile, String password);

  /**
   * Changes the password in a certificate and return the certificate with the new key.
   *
   * @param certificateFile the certificate file
   * @param password the password
   * @return the certificate with the new password.
   */
  byte[] changeCertificatePassword(MultipartFile certificateFile, String password);

  /**
   * Gets a project certificate.
   *
   * @param idCertficate the id certficate
   * @return the certificate
   */
  PemTestCertificate getPublicCertificate(Long idCertficate);

  /**
   * Gets the certificate type.
   *
   * @param certificateFile the certificate file
   * @param password the password
   * @return the certificate type
   */
  String getCertificateType(MultipartFile certificateFile, String password);
}
