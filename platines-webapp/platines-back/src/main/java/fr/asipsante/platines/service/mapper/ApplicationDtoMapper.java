/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.ApplicationDto;
import fr.asipsante.platines.dto.ApplicationListDto;
import fr.asipsante.platines.entity.Application;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "applicationDtoMapper")
public class ApplicationDtoMapper extends GenericDtoMapper<Application, ApplicationDto> {

  /** ModelMapper. */
  @Autowired private ModelMapper modelMapper;

  /** UserDtoMapper. */
  @Autowired
  @Qualifier("userDtoMapper")
  private UserDtoMapper userDtoMapper;

  /** ChainOfTrustDtoMapper. */
  @Autowired
  @Qualifier("chainOfTrustDtoMapper")
  private ChainOfTrustDtoMapper chainOfTrustDtoMapper;

  /**
   * Converts an {@link ApplicationListDto} into an {@link Application}.
   *
   * @param applicationListDto, the applicationListDto to convert
   * @return an application
   */
  public Application convertToApplication(ApplicationListDto applicationListDto) {
    final Application application = modelMapper.map(applicationListDto, Application.class);
    application.setUser(userDtoMapper.convertToUser(applicationListDto.getUser()));
    return application;
  }

  /**
   * Converts an {@link ApplicationDto} into an {@link Application}.
   *
   * @param applicationDto, the applicationDto to convert
   * @return an application
   */
  public Application convertToApplication(ApplicationDto applicationDto) {
    final Application application = modelMapper.map(applicationDto, Application.class);
    if (applicationDto.getUser() != null) {
      application.setUser(userDtoMapper.convertToUser(applicationDto.getUser()));
    }
    if (applicationDto.getChainOfTrustDto() != null) {
      application.setChainOfTrust(
          chainOfTrustDtoMapper.convertToChainOfTrust(applicationDto.getChainOfTrustDto()));
    }
    return application;
  }

  /**
   * Converts an {@link Application} into an {@link ApplicationDto}.
   *
   * @param application, the application to convert
   * @return an applicationDto
   */
  public ApplicationDto convertToApplicationDto(Application application) {
    final ApplicationDto applicationDto = modelMapper.map(application, ApplicationDto.class);
    if (application.getChainOfTrust() != null) {
      applicationDto.setChainOfTrustDto(
          chainOfTrustDtoMapper.convertToChainOfTrustListDto(application.getChainOfTrust()));
    }
    if (application.getUser() != null) {
      applicationDto.setUser(userDtoMapper.convertToUserDto(application.getUser()));
    }
    return applicationDto;
  }

  /**
   * Converts an {@link Application} into a {@link ApplicationListDto}.
   *
   * @param application, the application to convert
   * @return an applicationListDto
   */
  public ApplicationListDto convertToApplicationListDto(Application application) {
    final ApplicationListDto applicationListDto =
        modelMapper.map(application, ApplicationListDto.class);
    if (application.getUser() != null) {
      applicationListDto.setUser(userDtoMapper.convertToUserDto(application.getUser()));
    }
    return applicationListDto;
  }
}
