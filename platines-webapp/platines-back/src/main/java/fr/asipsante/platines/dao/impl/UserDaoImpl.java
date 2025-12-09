/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.IUserDao;
import fr.asipsante.platines.entity.User;
import fr.asipsante.platines.exception.DatabaseException;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

/**
 * @author apierre
 */
@Repository(value = "userRefactoringDao")
public class UserDaoImpl extends GenericDaoImpl<User, Long> implements IUserDao {

  @Override
  public User getUserByEmailAndPassword(String email, String password) {
    final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
    final Root<User> root = query.from(User.class);
    query.select(root).where(criteriaBuilder.equal(root.get("mail"), email));
    final List<User> users = entityManager.createQuery(query).getResultList();
    final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    User connectedUser = null;
    for (final User user : users) {
      if (passwordEncoder.matches(password, user.getPassword())) {
        connectedUser = user;
      }
    }

    return connectedUser;
  }

  @Override
  public User getUserByEmail(String email) {
    final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<User> query = criteriaBuilder.createQuery(User.class);
    final Root<User> root = query.from(User.class);
    query.select(root).where(criteriaBuilder.equal(root.get("mail"), email));
    User user = null;
    try {
      user = entityManager.createQuery(query).getSingleResult();
    } catch (final NoResultException e) {
      throw new DatabaseException(e, "Aucun utilisateur connu avec cet email");
    }
    return user;
  }

  /** {@inheritDoc} */
  @Override
  @PostFilter("hasPermission(filterObject, 'READ') or isManaged(filterObject)")
  public List<User> getAll() {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<User> query = (CriteriaQuery<User>) builder.createQuery(clazz);
    query.select(query.from(User.class));
    return entityManager.createQuery(query).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  @PostAuthorize("hasPermission(returnObject,'READ') or isManaged(returnObject)")
  public User getById(Long id) {
    return entityManager.find(User.class, id);
  }

  @Override
  public User safeFetchById(Long id) {
    return entityManager.find(User.class, id);
  }

  @Override
  public void safeUpdate(User user) {
    entityManager.merge(user);
  }

  @Override
  @PostFilter("isNotManaged(filterObject)")
  public List<User> safeFetchAllUnassignedUsers() {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<User> query = (CriteriaQuery<User>) builder.createQuery(clazz);
    query.select(query.from(User.class));
    return entityManager.createQuery(query).getResultList();
  }
}
