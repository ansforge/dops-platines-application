/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller;

import fr.asipsante.platines.dto.CertificateDto;
import fr.asipsante.platines.dto.ChainOfTrustDto;
import fr.asipsante.platines.dto.ChainOfTrustListDto;
import fr.asipsante.platines.dto.ResponseDto;
import fr.asipsante.platines.dto.UserDto;
import fr.asipsante.platines.mail.entity.CertificateState;
import fr.asipsante.platines.service.ApplicationService;
import fr.asipsante.platines.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author apierre
 */
@RequestMapping("/secure/")
@RestController
public class ChainOfTrustController {

  /** The id for the user profile in database. */
  private static final Long USER_ID = 2L;

  /** The application service. */
  @Autowired private ApplicationService applicationService;

  /** The user service. */
  @Autowired private UserService userService;

  /**
   * Persists a new trust chain in database.
   *
   * @param chainOfTrustDto the new chain trust to persist
   * @return the id of the new chain of trust
   */
  @PostMapping(value = "chainOfTrust/add", produces = "application/json")
  public Long createChainOfTrust(@RequestBody ChainOfTrustDto chainOfTrustDto) {
    return applicationService.createChainOfTrust(chainOfTrustDto);
  }

  /**
   * Updates a chain of trust.
   *
   * @param chainOfTrustDto the trust chain with updates.
   */
  @PostMapping(value = "chainOfTrust/update", produces = "application/json")
  public void updateChainOfTrust(@RequestBody ChainOfTrustDto chainOfTrustDto) {
    applicationService.updateChainOfTrust(chainOfTrustDto);
  }

  /**
   * Deletes a chain of trust in database.
   *
   * @param idChainOfTrust id of the trust chain to suppress
   */
  @DeleteMapping(value = "chainOfTrust/delete/{idChainOfTrust}", produces = "application/json")
  public void deleteChainOfTrust(@PathVariable Long idChainOfTrust) {
    applicationService.deleteChainOfTrust(idChainOfTrust);
  }

  /**
   * Gets all the trust chains.
   *
   * @param token the user token
   * @return the list of the trust chains
   */
  @GetMapping(value = "chainOfTrust/getAll", produces = "application/json")
  public List<ChainOfTrustListDto> getAll(@RequestHeader(value = "Authorization") String token) {
    final UserDto userDto = userService.getUserByToken(token);
    if (userDto.getProfile().getId().equals(USER_ID)) {
      return applicationService.getChainOfTrustListDtoByUser(userDto.getId());
    } else {
      return applicationService.getChainOfTrustListDto();
    }
  }

  /**
   * Gets a trust chain by is id.
   *
   * @param idChain id of the trust chain to find
   * @return the trust chain
   */
  @GetMapping(value = "chainOfTrust/get/{idChain}", produces = "application/json")
  public ChainOfTrustDto getChainByid(@PathVariable Long idChain) {
    return applicationService.getChainOfTrustById(idChain);
  }

  /**
   * Persists a new certificate in database.
   *
   * @param certificateDto the new certificate to persist.
   * @return a response
   */
  @PostMapping(value = "chainOfTrust/certificate/add", produces = "application/json")
  public ResponseDto createCertificate(@RequestBody CertificateDto certificateDto) {
    final ResponseDto response = new ResponseDto();
    final CertificateState certificateState = applicationService.createCertificate(certificateDto);
    if (certificateState.getValidityDate() != null) {
      response.setStatus(true);
    }
    return response;
  }

  /**
   * Gets all the trust chains for a user.
   *
   * @param idUser the id of the user
   * @return the list of the trust chains
   */
  @GetMapping(value = "chainOfTrust/get/user/{idUser}", produces = "application/json")
  public List<ChainOfTrustListDto> getChainsByUser(@PathVariable Long idUser) {
    return applicationService.getChainOfTrustListDtoByUser(idUser);
  }

  /**
   * Updates a certificate.
   *
   * @param certificateDto the certificate with updates
   * @return a response
   */
  @PostMapping(value = "chainOfTrust/certificate/update", produces = "application/json")
  public ResponseDto updateCertificate(@RequestBody CertificateDto certificateDto) {
    final ResponseDto response = new ResponseDto();
    final CertificateState certificateState = applicationService.updateCertificate(certificateDto);
    if (certificateState.getValidityDate() != null) {
      response.setStatus(true);
    }
    return response;
  }

  /**
   * Deletes a certificate from database.
   *
   * @param idCertificate id of the certificate to delete
   */
  @DeleteMapping(
      value = "chainOfTrust/certificate/delete/{idCertificate}",
      produces = "application/json")
  public void deleteCertificate(@PathVariable Long idCertificate) {
    applicationService.deleteCertificate(idCertificate);
  }

  /**
   * Gets a certificate by is id.
   *
   * @param idCertificate id of the certificate to find
   * @return the certificate
   */
  @GetMapping(value = "chainOfTrust/certificate/get/{idCertificate}", produces = "application/json")
  public CertificateDto getCertificateById(@PathVariable Long idCertificate) {
    return applicationService.getCertificateById(idCertificate);
  }
}
