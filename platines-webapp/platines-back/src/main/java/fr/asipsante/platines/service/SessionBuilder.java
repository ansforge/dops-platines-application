/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.dto.SessionParameters;
import fr.asipsante.platines.dto.TestSessionDto;
import fr.asipsante.platines.entity.Project;

/**
 * PackagerManager.
 *
 * @author aboittiaux
 */
public interface SessionBuilder {

  Project getProjectById(Long idProject);

  /**
   * Création d'une session cliente. Il doit en résulter un artifact qui sera téléchargé par le
   * jobmanager.
   *
   * @param session, la session à créér
   * @return l'environnement de la session
   */
  SessionParameters createClientSession(TestSessionDto session);

  /**
   * Création d'une session serveur. Il doit en résulter un artifact qui sera téléchargé par le
   * jobmanager.
   *
   * @param session, la session à créér
   * @return l'environnement de la session
   */
  SessionParameters createServeurSession(TestSessionDto session);
}
