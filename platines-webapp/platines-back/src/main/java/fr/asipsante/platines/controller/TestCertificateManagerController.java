/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import fr.asipsante.platines.dto.TestCertificateDto;
import fr.asipsante.platines.service.TestCertificateService;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author apierre
 */
@RequestMapping("/secure")
@RestController
public class TestCertificateManagerController {

  /** The test certificate service. */
  @Autowired TestCertificateService testCertificateService;

  private static final Logger LOGGER =
      org.slf4j.LoggerFactory.getLogger(TestCertificateManagerController.class);

  /** The gson builder. */
  final Gson builder =
      new GsonBuilder()
          .registerTypeAdapter(
              Date.class,
              (JsonDeserializer<Date>)
                  (jsonElement, type, context) -> new Date(jsonElement.getAsLong()))
          .create();

  /**
   * Gets all the test certificates registered in the database.
   *
   * @return all the test certificates.
   */
  @PreAuthorize("hasRole('admin') or hasRole('manager')")
  @GetMapping(value = "/testCertificate/getAll", produces = "application/json")
  public List<TestCertificateDto> getTestCertificates() {
    return testCertificateService.getAllTestCertificates();
  }

  /**
   * Gets a test certificate by its id.
   *
   * @param id the test certificate id.
   * @return the test certificate corresponding to the id.
   */
  @PreAuthorize("hasRole('admin')")
  @GetMapping(value = "/testCertificate/get/{id}", produces = "application/json")
  public TestCertificateDto getById(@PathVariable Long id) {
    return testCertificateService.getTestCertificateById(id);
  }

  /**
   * Delete a test certificate identified by is id.
   *
   * @param id the id to select the test certificate for deletion.
   */
  @PreAuthorize("hasRole('admin')")
  @DeleteMapping(value = "/testCertificate/delete/{id}", produces = "application/json")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    try {
      testCertificateService.deleteTestCertificateById(id);
      return ResponseEntity.accepted().build();
    } catch (DataIntegrityViolationException e) {
      LOGGER.debug("Data integrity violation: ", e);
      return ResponseEntity.badRequest().build();
    } catch (Exception e) {
      LOGGER.error("Error while deleting test certificate: ", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Analyze the certificate.
   *
   * @param file the certificate to analyze
   * @param password the password to read the certificate
   * @return the certificate end validity date
   */
  @PreAuthorize("hasRole('admin')")
  @PostMapping(value = "/testCertificate/analyze", headers = "Content-Type= multipart/form-data")
  public Date analyzeCertificate(
      @RequestParam("file") MultipartFile file, @RequestParam("password") String password) {
    return testCertificateService.getCertificateDateEndValidity(file, password);
  }

  /**
   * Save the new testCertificate in the database.
   *
   * @param file the certificate file
   * @param password the password to read the certificate
   * @param request the request containing all the certificate information to be persisted in
   *     database
   */
  @PreAuthorize("hasRole('admin')")
  @PostMapping(value = "/testCertificate/create", headers = "Content-Type= multipart/form-data")
  public void create(
      @RequestParam("file") MultipartFile file,
      @RequestParam("password") String password,
      @RequestParam String request) {

    final TestCertificateDto certificateDto = builder.fromJson(request, TestCertificateDto.class);
    final byte[] certificateFile = testCertificateService.changeCertificatePassword(file, password);
    certificateDto.setType(testCertificateService.getCertificateType(file, password));
    certificateDto.setFile(certificateFile);
    certificateDto.setFileName(file.getOriginalFilename());
    testCertificateService.saveTestCertificate(certificateDto);
  }

  /**
   * Update a testCertificate.
   *
   * @param file the new certificate file
   * @param password the password to read the certificate
   * @param request the request containing all the certificate informations to be updated in
   *     database
   */
  @PreAuthorize("hasRole('admin')")
  @PostMapping(value = "/testCertificate/edit", headers = "Content-Type= multipart/form-data")
  public void edit(
      @RequestParam(required = false, value = "file") MultipartFile file,
      @RequestParam(required = false, value = "password") String password,
      @RequestParam String request) {
    final TestCertificateDto certificateDto = builder.fromJson(request, TestCertificateDto.class);
    if (file != null) {
      final byte[] certificateFile =
          testCertificateService.changeCertificatePassword(file, password);
      certificateDto.setFile(certificateFile);
      certificateDto.setFileName(file.getOriginalFilename());
    }
    testCertificateService.updateTestCertificate(certificateDto);
  }
}
