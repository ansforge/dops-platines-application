/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import fr.asipsante.platines.dto.ChainOfTrustDto;
import fr.asipsante.platines.dto.ChainOfTrustListDto;
import fr.asipsante.platines.entity.ChainOfTrust;
import java.util.ArrayList;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author cnader
 */
@Service(value = "chainOfTrustDtoMapper")
public class ChainOfTrustDtoMapper extends GenericDtoMapper<ChainOfTrust, ChainOfTrustDto> {

  /** ModelMapper. */
  @Autowired private ModelMapper modelMapper;

  /** FamilyDtoMapper. */
  @Autowired
  @Qualifier("userDtoMapper")
  private UserDtoMapper userDtoMapper;

  /**
   * Converts a {@link ChainOfTrust} into a {@link ChainOfTrustDto}.
   *
   * @param chainOfTrust, the chainOfTrust to convert
   * @return a chainOfTrustDto
   */
  public ChainOfTrustDto convertToChainOfTrustDto(ChainOfTrust chainOfTrust) {
    final ChainOfTrustDto chainOfTrustDto = modelMapper.map(chainOfTrust, ChainOfTrustDto.class);
    if (chainOfTrust.getUser() != null) {
      chainOfTrustDto.setUser(userDtoMapper.convertToUserDto(chainOfTrust.getUser()));
    }
    chainOfTrustDto.setCertificate(new ArrayList<>());
    return chainOfTrustDto;
  }

  /**
   * Converts a {@link ChainOfTrustDto} into a {@link ChainOfTrust}.
   *
   * @param chainOfTrustDto, the chainOfTrustDto to convert
   * @return a chainOfTrust
   */
  public ChainOfTrust convertToChainOfTrust(ChainOfTrustDto chainOfTrustDto) {
    final ChainOfTrust chainOfTrust = modelMapper.map(chainOfTrustDto, ChainOfTrust.class);
    if (chainOfTrustDto.getUser() != null) {
      chainOfTrust.setUser(userDtoMapper.convertToUser(chainOfTrustDto.getUser()));
    }
    return chainOfTrust;
  }

  /**
   * Converts a {@link ChainOfTrustDto} into a {@link ChainOfTrust}.
   *
   * @param chainOfTrustDto, the chainOfTrustDto to convert
   * @return a chainOfTrust
   */
  public ChainOfTrust convertToChainOfTrust(ChainOfTrustListDto chainOfTrustDto) {
    final ChainOfTrust chainOfTrust = modelMapper.map(chainOfTrustDto, ChainOfTrust.class);
    if (chainOfTrustDto.getUser() != null) {
      chainOfTrust.setUser(userDtoMapper.convertToUser(chainOfTrustDto.getUser()));
    }
    return chainOfTrust;
  }

  /**
   * Converts a {@link ChainOfTrust} into a {@link ChainOfTrustListDto}.
   *
   * @param chainOfTrust, the chainOfTrust to convert
   * @return a chainOfTrustListDto
   */
  public ChainOfTrustListDto convertToChainOfTrustListDto(ChainOfTrust chainOfTrust) {
    final ChainOfTrustListDto chainOfTrustListDto =
        modelMapper.map(chainOfTrust, ChainOfTrustListDto.class);
    if (chainOfTrust.getUser() != null) {
      chainOfTrustListDto.setUser(userDtoMapper.convertToUserDto(chainOfTrust.getUser()));
    }
    return chainOfTrustListDto;
  }
}
