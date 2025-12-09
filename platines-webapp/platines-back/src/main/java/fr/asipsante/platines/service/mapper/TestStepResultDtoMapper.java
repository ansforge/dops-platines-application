/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.StepLogDto;
import fr.asipsante.platines.dto.TestStepResultDto;
import fr.asipsante.platines.entity.TestStepResult;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "testStepResultDtoMapper")
public class TestStepResultDtoMapper extends GenericDtoMapper<TestStepResult, TestStepResultDto> {

  /** ModelMapper. */
  @Autowired private ModelMapper modelMapper;

  /**
   * Converts a {@link TestStepResult} into a {@link StepLogDto}.
   *
   * @param step, the {@link TestStepResult} to convert
   * @return a {@link StepLogDto}
   */
  public StepLogDto convertToStepLogDto(TestStepResult step) {
    return modelMapper.map(step, StepLogDto.class);
  }
}
