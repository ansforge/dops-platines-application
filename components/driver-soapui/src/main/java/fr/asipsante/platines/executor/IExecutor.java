/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.platines.executor;

import java.io.File;

import fr.asipsante.platines.model.DriverTestResult;

public interface IExecutor {

	void init();
	DriverTestResult execute(File project);
	void publish(DriverTestResult driverTestResult);
}
