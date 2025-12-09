/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.TestCaseResultDto;
import fr.asipsante.platines.entity.TestCaseResult;
import java.util.ArrayList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "testCaseResultDtoMapper")
public class TestCaseResultDtoMapper extends GenericDtoMapper<TestCaseResult, TestCaseResultDto> {

  /** ModelMapper. */
  @Autowired private ModelMapper modelMapper;

  /** UserDtoMapper. */
  @Autowired
  @Qualifier("testStepResultDtoMapper")
  private TestStepResultDtoMapper testStepResultDtoMapper;

  @Autowired
  @Qualifier("rOperationExpectedDtoMapper")
  private ROperationExpectedDtoMapper rOperationExpectedDtoMapper;

  /**
   * Converts a {@link TestCaseResult} into a {@link TestCaseResultDto}.
   *
   * @param testCase, the {@link TestCaseResult} to convert
   * @return a {@link TestCaseResultDto}
   */
  public TestCaseResultDto convertToTestCaseDto(TestCaseResult testCase) {
    final TestCaseResultDto caseDto = modelMapper.map(testCase, TestCaseResultDto.class);
    caseDto.setTestSteps(new ArrayList<>());
    if (!testCase.getTestSteps().isEmpty()) {
      testCase
          .getTestSteps()
          .forEach(step -> caseDto.getTestSteps().add(testStepResultDtoMapper.convertToDto(step)));
    }
    if (!testCase.getCaseResultOperations().isEmpty()) {
      caseDto.setrOperationDto(new ArrayList<>());
      testCase
          .getCaseResultOperations()
          .forEach(
              caseResult ->
                  caseDto
                      .getrOperationDto()
                      .add(rOperationExpectedDtoMapper.convertToDto(caseResult)));
    }
    return caseDto;
  }
}
