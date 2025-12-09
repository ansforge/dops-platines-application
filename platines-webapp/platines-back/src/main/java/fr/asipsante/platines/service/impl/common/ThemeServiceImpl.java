/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import fr.asipsante.platines.dao.IThemeDao;
import fr.asipsante.platines.dto.FamilyTreeDto;
import fr.asipsante.platines.dto.ThemeDto;
import fr.asipsante.platines.dto.ThemeRolesDto;
import fr.asipsante.platines.entity.Theme;
import fr.asipsante.platines.entity.enums.Role;
import fr.asipsante.platines.service.ThemeService;
import fr.asipsante.platines.service.mapper.ThemeDtoMapper;
import fr.asipsante.platines.service.mapper.ThemeTreeDtoMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * The family service.
 *
 * @author apierre
 */
@Service
public class ThemeServiceImpl implements ThemeService {

  /** LOGGER. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ThemeServiceImpl.class);

  /** Theme DAO. */
  @Autowired private IThemeDao themeDao;

  @Autowired
  @Qualifier("familyDtoMapper")
  private ThemeDtoMapper familyDtoMapper;

  @Autowired
  @Qualifier("familyTreeDtoMapper")
  private ThemeTreeDtoMapper familyTreeDtoMapper;

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<ThemeDto> getAllFamilies() {
    final List<ThemeDto> familiesDto = new ArrayList<>();
    final List<Theme> families = themeDao.getAll();
    for (final Theme family : families) {
      familiesDto.add(familyDtoMapper.convertToDto(family));
    }
    return familiesDto;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<FamilyTreeDto> getAllFamiliesAsTree() {
    final List<FamilyTreeDto> familiesTreeDto = new ArrayList<>();
    final List<Theme> families = themeDao.getAll();
    for (final Theme family : families) {
      familiesTreeDto.add(familyTreeDtoMapper.convertToDto(family));
    }
    return familiesTreeDto;
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void createFamily(ThemeDto familyDto) {
    themeDao.save(familyDtoMapper.convertToEntity(familyDto));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public ThemeDto getFamilyById(Long id) {
    final Theme safeFetchById = themeDao.safeFetchById(id);
    if (safeFetchById == null) {
      throw new EntityNotFoundException("Aucun thème pour l'id " + id);
    }
    return familyDtoMapper.convertToDto(safeFetchById);
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void updateFamily(ThemeDto familyDto) {
    themeDao.update(familyDtoMapper.convertToEntity(familyDto));
  }

  /** {@inheritDoc} */
  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void deleteFamily(Long id) {
    themeDao.delete(themeDao.getById(id));
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public List<ThemeRolesDto> getThemeRoleCoverage() {
    Map<Theme, Set<Role>> coverageData = themeDao.getCoveredRoleMapping();
    return coverageData.entrySet().stream()
        .map(
            entry ->
                new ThemeRolesDto(familyDtoMapper.convertToDto(entry.getKey()), entry.getValue()))
        .collect(Collectors.toList());
  }
}
