/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.publisher;

import fr.asipsante.platines.model.DriverSessionResult;
import fr.asipsante.platines.model.DriverTestResult;
import fr.asipsante.platines.model.SessionLog;

public interface TestsResultsPublisher {

  void publishProject(DriverTestResult resultat);

  void publishSession(DriverSessionResult driverSessionResult);

  void publishLogSession(SessionLog sessionLog);
}
