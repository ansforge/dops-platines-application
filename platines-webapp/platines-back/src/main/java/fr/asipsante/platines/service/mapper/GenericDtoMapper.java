/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.mapper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The type Generic dto mapper.
 *
 * @param <E> the type parameter
 * @param <K> the type parameter
 * @author cnader
 */
@Service(value = "genericDtoMapper")
public abstract class GenericDtoMapper<E, K> {

  /** ModelMapper. */
  @Autowired private ModelMapper modelMapper;

  /** The class of the entity this instance has to manage. */
  protected Class<? extends E> entityClass;

  /** The class of the entityDto this instance has to manage. */
  protected Class<? extends K> dtoClass;

  /**
   * By defining this class as abstract, we prevent Spring from creating instance of this class if
   * not defined as abstract, getClass().getGenericSuperClass() would return Object. There would be
   * exception because Object class does not have constructor with parameters.
   */
  public GenericDtoMapper() {
    final Type t = getClass().getGenericSuperclass();
    final ParameterizedType pt = (ParameterizedType) t;
    entityClass = (Class) pt.getActualTypeArguments()[0];
    dtoClass = (Class) pt.getActualTypeArguments()[1];
  }

  /**
   * @param entityDto, the entityDto to convert
   * @return an entity
   */
  public E convertToEntity(K entityDto) {
    return modelMapper.map(entityDto, entityClass);
  }

  /**
   * @param entity, the entity to convert
   * @return an entityDto
   */
  public K convertToDto(E entity) {
    return modelMapper.map(entity, dtoClass);
  }
}
