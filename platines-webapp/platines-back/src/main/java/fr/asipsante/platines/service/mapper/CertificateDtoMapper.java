/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.CertificateDto;
import fr.asipsante.platines.entity.Certificate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "certificateDtoMapper")
public class CertificateDtoMapper extends GenericDtoMapper<Certificate, CertificateDto> {

  /** ModelMapper. */
  @Autowired private ModelMapper modelMapper;

  /** chainOfTrustDtoMapper. */
  @Autowired
  @Qualifier("chainOfTrustDtoMapper")
  private ChainOfTrustDtoMapper chainOfTrustDtoMapper;

  /**
   * Converts a {@link CertificateDto} into a {@link Certificate}.
   *
   * @param certificateDto, the certificateDto to convert
   * @return a certificate
   */
  public Certificate convertToCertificate(CertificateDto certificateDto) {
    final Certificate cert = modelMapper.map(certificateDto, Certificate.class);
    if (certificateDto.getChainOfTrust() != null) {
      cert.setChainOfTrust(
          chainOfTrustDtoMapper.convertToChainOfTrust(certificateDto.getChainOfTrust()));
    }
    return cert;
  }
}
