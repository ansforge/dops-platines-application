/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao;

import java.io.Serializable;
import java.util.List;

/**
 * The interface {@link IGenericDao} provides all the generic methods required to link an entity to
 * a data source. The provided methods allow performing the CRUD operations on the regarded entity.
 *
 * @author apierre
 * @param <E> Class of the entity to managed by this DAO.
 * @param <K> the generic type
 */
public interface IGenericDao<E, K extends Serializable> {

  /**
   * The method {@link #getById(Serializable)} loads and returns the entity whose identifier is
   * received as parameter.
   *
   * <p>This method returns <code>null</code> if no entity is found in the data source.
   *
   * @param id Identifier of the entity to load.
   * @return The entity corresponding to the identifier received as parameter. <code>null</code> if
   *     no entity is found corresponding to the identifier.
   */
  E getById(K id);

  /**
   * The method {@link #save(Object)} saves persist entity to the database.
   *
   * @param entity Entity to save in the database.
   */
  E save(E entity);

  /**
   * The method {@link #saveAndGet(Object)} saves persist entity to the database and returns it
   * after the operation has been successfully performed.
   *
   * <p>This method is useful when some modifications are done on the entity during its insertion
   * (auto-incremented primary key for example) and these modifications have to be loaded in the
   * returned object.
   *
   * @param entity Entity to save in the database.
   * @return Entity loaded from the data source once the operation has been successfully performed.
   */
  E saveAndGet(E entity);

  /**
   * The method {@link #update(Object)} updates the entity received as parameter from the data
   * source. The entity has to exist in the data source if you want the operation to success.
   *
   * @param entity Entity to update in the database.
   */
  void update(E entity);

  /**
   * The method {@link #delete(Object)} deletes the entity received as parameter from the data
   * source. The entity has to exist in the data source if you want the operation to success.
   *
   * @param entity Entity to delete from the data source.
   */
  void delete(E entity);

  /**
   * The method {@link #getAll()} loads and returns all entity found in data source.
   *
   * @return List of entity.
   */
  List<E> getAll();
}
