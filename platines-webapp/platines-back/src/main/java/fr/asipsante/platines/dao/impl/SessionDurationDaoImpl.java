/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.ISessionDurationDao;
import fr.asipsante.platines.entity.SessionDuration;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import org.springframework.stereotype.Repository;

/**
 * @author apierre
 */
@Repository(value = "sessionDurationDao")
public class SessionDurationDaoImpl extends GenericDaoImpl<SessionDuration, Long>
    implements ISessionDurationDao {

  @Override
  public List<SessionDuration> getAllDurations() {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<SessionDuration> query = builder.createQuery(SessionDuration.class);
    query.select(query.from(SessionDuration.class));
    return entityManager.createQuery(query).getResultList();
  }
}
