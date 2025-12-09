/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

/**
 * The interface {@link IGenericDtoMapper} provides the generic methods required to map an entity to
 * its dto.
 *
 * @param <E> entity
 * @param <K> entityDto
 * @author cnader
 */
public interface IGenericDtoMapper<E, K> {

  /**
   * The method {@link #convertToEntity(Object)} returns an Entity object from its Dto.
   *
   * @param entityDto Dto on which to base conversion.
   * @return entity e
   */
  E convertToEntity(K entityDto);

  /**
   * The method {@link #convertToDto(Object)} convertToDto} returns a Dto object from its Entity.
   *
   * @param entity Entity on which to base conversion.
   * @return entityDto k
   */
  K convertToDto(E entity);
}
