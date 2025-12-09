/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dao.impl;

import fr.asipsante.platines.dao.ITestSessionDao;
import fr.asipsante.platines.entity.ProjectResult;
import fr.asipsante.platines.entity.ProjectResultProperty;
import fr.asipsante.platines.entity.TestSession;
import fr.asipsante.platines.entity.enums.SessionStatus;
import fr.asipsante.platines.exception.DatabaseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

/**
 * @author apierre
 */
@Repository(value = "testSessionDao")
public class TestSessionDaoImpl extends GenericDaoImpl<TestSession, Long>
    implements ITestSessionDao {

  /** status session. */
  private static final String SESSION_STATUS = "sessionStatus";

  @Override
  @PostFilter(
      "(hasPermission(filterObject, 'READ') and hasAccess(filterObject)) or"
          + " isManaged(filterObject)")
  public List<TestSession> getSessionsEnabled() {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<TestSession> query = builder.createQuery(TestSession.class);
    final Root<TestSession> root = query.from(TestSession.class);
    query
        .select(root)
        .where(builder.isNull(root.get("suppressionDate")))
        .orderBy(builder.desc(root.get("creationDate")));
    return entityManager.createQuery(query).getResultList();
  }

  @Override
  @PostFilter(
      "(hasPermission(filterObject, 'READ') and hasAccess(filterObject)) or"
          + " isManaged(filterObject)")
  public List<TestSession> getSessionsDisabled() {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<TestSession> query = builder.createQuery(TestSession.class);
    final Root<TestSession> root = query.from(TestSession.class);
    query
        .select(root)
        .where(builder.isNotNull(root.get("suppressionDate")))
        .orderBy(builder.desc(root.get("creationDate")));
    return entityManager.createQuery(query).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize("(hasPermission(#session, 'WRITE') and hasAccess(#session)) or isManaged(#session)")
  public void update(TestSession session) {
    entityManager.merge(session);
  }

  @Override
  @PreAuthorize(
      "(hasPermission(#session, 'DELETE') and hasAccess(#session)) or isManaged(#session)")
  public void deleteSession(TestSession session, Date date) {
    session.setSuppressionDate(date);
    update(session);
  }

  /** {@inheritDoc} */
  @Override
  @PreAuthorize(
      "(hasPermission(#session, 'DELETE') and hasAccess(#session)) or isManaged(#session)")
  public void delete(TestSession session) {
    entityManager.remove(entityManager.find(TestSession.class, session.getId()));
  }

  @Override
  @PreAuthorize(
      "(hasPermission(#session, 'CREATE') and hasPermission(#session, 'WRITE') and"
          + " hasAccess(#session)) or isManaged(#session)")
  public TestSession duplicateSession(TestSession session) {
    final TestSession sessionDup = new TestSession();
    sessionDup.setApplication(session.getApplication());
    sessionDup.setDescription(session.getDescription());
    sessionDup.setVersion(session.getVersion());
    sessionDup.setSessionDuration(session.getSessionDuration());
    if (session.getSessionType() != null) {
      sessionDup.setSessionType(session.getSessionType());
    }
    final List<ProjectResult> projects = new ArrayList<>();
    for (final ProjectResult projectResult : session.getProjectResults()) {
      final ProjectResult projectResult2 = new ProjectResult();
      projectResult2.setIdProject(projectResult.getIdProject());
      projectResult2.setName(projectResult.getName());
      projectResult2.setNumberOrder(projectResult.getNumberOrder());
      projectResult2.setProjectProperties(new ArrayList<>());
      for (final ProjectResultProperty projectResultProperty :
          projectResult.getProjectProperties()) {
        final ProjectResultProperty projectResultProperty2 = new ProjectResultProperty();
        projectResultProperty2.setDescription(projectResultProperty.getDescription());
        projectResultProperty2.setKey(projectResultProperty.getKey());
        projectResultProperty2.setValue(projectResultProperty.getValue());
        projectResultProperty2.setPropertyType(projectResultProperty.getPropertyType());
        projectResult2.getProjectProperties().add(projectResultProperty2);
      }
      projects.add(projectResult2);
    }
    sessionDup.setProjectResults(projects);

    return sessionDup;
  }

  @Override
  public TestSession getSessionByUuid(String uuid) {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<TestSession> query = builder.createQuery(TestSession.class);
    final Root<TestSession> root = query.from(TestSession.class);
    query.select(root).where(builder.equal(root.get("uuid"), uuid));
    TestSession session;
    try {
      session = entityManager.createQuery(query).getSingleResult();
    } catch (final NoResultException e) {
      throw new DatabaseException(e, "pas de session portant cet uuid");
    }
    return session;
  }

  @Override
  public byte[] getZipLogSession(Long idSession) {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<TestSession> query = builder.createQuery(TestSession.class);
    final Root<TestSession> root = query.from(TestSession.class);
    query.select(root).where(builder.equal(root.get("id"), idSession));
    byte[] logSession = null;
    TestSession session;
    try {
      session = entityManager.createQuery(query).getSingleResult();
      if (session.getLog() != null) {
        logSession = session.getLog();
      }
    } catch (final NoResultException e) {
      throw new DatabaseException(e, "pas de session portant cet id");
    }
    return logSession;
  }

  @Override
  public void updateSessionWithoutToken(TestSession testSession) {
    entityManager.merge(testSession);
  }

  @PostFilter(
      "(hasPermission(filterObject, 'READ') and hasAccess(filterObject)) or"
          + " isManaged(filterObject)")
  @Override
  public List<TestSession> getActiveSessions() {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<TestSession> query = builder.createQuery(TestSession.class);
    final Root<TestSession> root = query.from(TestSession.class);
    query
        .select(root)
        .where(
            builder.notEqual(root.get(SESSION_STATUS), SessionStatus.CANCELED),
            builder.notEqual(root.get(SESSION_STATUS), SessionStatus.ERROR),
            builder.notEqual(root.get(SESSION_STATUS), SessionStatus.FINISHED),
            builder.notEqual(root.get(SESSION_STATUS), SessionStatus.NONEXECUTE));
    return entityManager.createQuery(query).getResultList();
  }

  /** {@inheritDoc} */
  @Override
  @PostAuthorize(
      "(hasPermission(returnObject,'READ') and hasPermission(returnObject,'WRITE') and"
          + " hasAccess(returnObject)) or isManaged(returnObject)")
  public TestSession getById(Long id) {
    return entityManager.find(TestSession.class, id);
  }
}
