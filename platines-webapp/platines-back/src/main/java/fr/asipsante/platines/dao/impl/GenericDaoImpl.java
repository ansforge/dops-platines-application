/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IGenericDao;
import fr.asipsante.platines.entity.AbstractEntity;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

/**
 * This class provides the basic implementations needed to make Object-Relational operations on
 * databases, using the Criteria specification.
 *
 * @author apierre
 * @param <E> Entity's type this instance has to manage.
 * @param <K> The generic type.
 */
@Repository
public abstract class GenericDaoImpl<E, K extends Serializable> implements IGenericDao<E, K> {

  /** entity manager. */
  @PersistenceContext protected EntityManager entityManager;

  /** The class of the entity this instance has to manage. */
  protected Class<? extends E> clazz;

  /**
   * By defining this class as abstract, we prevent Spring from creating instance of this class if
   * not defined as abstract, getClass().getGenericSuperClass() would return Object. There would be
   * exception because Object class does not have constructor with parameters.
   */
  public GenericDaoImpl() {
    final Type t = getClass().getGenericSuperclass();
    final ParameterizedType pt = (ParameterizedType) t;
    clazz = (Class) pt.getActualTypeArguments()[0];
  }

  /** {@inheritDoc} */
  @Override
  @PostAuthorize(
      "hasPermission(returnObject,'READ') or hasAccess(returnObject) or isManaged(returnObject)")
  public E getById(K id) {
    return entityManager.find(clazz, id);
  }

  /** {@inheritDoc} */
  @Override
  public E save(E entity) {
    try {
      // Insert your entity by calling persist method
      entityManager.persist(entity);
      entityManager.flush();
      return entity;
    } catch (final EntityExistsException ignored) {
      return null;
    }
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("hasPermission(#entity, 'WRITE')")
  public void update(E entity) {
    entityManager.merge(entity);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("hasPermission(#entity, 'DELETE')")
  public void delete(E entity) {
    entityManager.remove(entityManager.find(clazz, ((AbstractEntity) entity).getId()));
  }

  /** {@inheritDoc} */
  @Override
  @PostFilter(
      "hasPermission(filterObject, 'READ') or hasAccess(filterObject) or isManaged(filterObject)")
  public List<E> getAll() {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<E> query = (CriteriaQuery<E>) builder.createQuery(clazz);
    query.select(query.from(clazz));
    return entityManager.createQuery(query).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  @PostAuthorize(
      "hasPermission(returnObject, 'READ') or hasAccess(returnObject) or isManaged(returnObject)")
  public E saveAndGet(E entity) {
    entityManager.merge(entity);
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<Long> query = builder.createQuery(Long.class);
    final Root<E> root = (Root<E>) query.from(clazz);
    query.select(builder.max(root.get("id")));
    final Serializable id = entityManager.createQuery(query).getSingleResult();
    return getById((K) id);
  }
}
