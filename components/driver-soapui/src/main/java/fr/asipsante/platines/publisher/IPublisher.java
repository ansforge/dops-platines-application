/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.platines.publisher;

import fr.asipsante.platines.model.DriverSessionResult;
import fr.asipsante.platines.model.DriverTestResult;
import fr.asipsante.platines.model.SessionLog;

public interface IPublisher {

	void publishProject(DriverTestResult resultat);
	void publishSession(DriverSessionResult driverSessionResult);
	void publishLogSession(SessionLog sessionLog);
}
