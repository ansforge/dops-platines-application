/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.TestCertificateDto;
import fr.asipsante.platines.entity.TestCertificate;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "testCertificateDtoMapper")
public class TestCertificateDtoMapper
    extends GenericDtoMapper<TestCertificate, TestCertificateDto> {

  /**
   * Converts a {@link TestCertificate} into a {@link TestCertificateDto}.
   *
   * @param testCertificate, the testCertificate to convert
   * @return a TestCertificateDto
   */
  public TestCertificateDto convertToTestCertificateDto(TestCertificate testCertificate) {
    final TestCertificateDto testCertificateDto = new TestCertificateDto();
    testCertificateDto.setId(testCertificate.getId());
    testCertificateDto.setFileName(testCertificate.getFileName());
    testCertificateDto.setDescription(testCertificate.getDescription());
    testCertificateDto.setDownloadable(testCertificate.isDownloadable());
    testCertificateDto.setState(testCertificate.getState());
    testCertificateDto.setValidityDate(testCertificate.getValidityDate());
    testCertificateDto.setType(testCertificate.getType());
    return testCertificateDto;
  }
}
