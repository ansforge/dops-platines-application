/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.dto.SessionParameters;
import fr.asipsante.platines.dto.TestSessionDto;

public interface SessionRunner {

  /** lance l'exécution. */
  void launch();

  /**
   * prépare le zip et les differents fichiers nécessaires pour l'éxécution.
   *
   * @return l'environnement
   */
  SessionParameters init();

  /** Stoppe une session (usage administrateur) et passe le statut à CANCELLED. */
  void stop(Long idSession);

  /**
   * @param session the Test Session
   */
  void setSessionTst(TestSessionDto session);
}
