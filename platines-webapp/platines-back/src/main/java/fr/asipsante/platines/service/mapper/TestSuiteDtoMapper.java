/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.TestSuiteDto;
import fr.asipsante.platines.entity.TestSuiteResult;
import java.util.ArrayList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "testSuiteDtoMapper")
public class TestSuiteDtoMapper extends GenericDtoMapper<TestSuiteResult, TestSuiteDto> {

  /** ModelMapper. */
  @Autowired private ModelMapper modelMapper;

  /** UserDtoMapper. */
  @Autowired
  @Qualifier("testCaseResultDtoMapper")
  private TestCaseResultDtoMapper testCaseResultDtoMapper;

  /**
   * Converts a {@link TestSuiteResult} into a {@link TestSuiteDto}.
   *
   * @param suite, the {@link TestSuiteResult} to convert
   * @return a {@link TestSuiteDto}
   */
  public TestSuiteDto convertToTestSuiteDto(TestSuiteResult suite) {
    final TestSuiteDto suiteDto = modelMapper.map(suite, TestSuiteDto.class);
    suiteDto.setTestCases(new ArrayList<>());
    if (!suite.getTestCases().isEmpty()) {
      suite
          .getTestCases()
          .forEach(
              testCase ->
                  suiteDto
                      .getTestCases()
                      .add(testCaseResultDtoMapper.convertToTestCaseDto(testCase)));
    }
    return suiteDto;
  }
}
