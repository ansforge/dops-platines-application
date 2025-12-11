/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.platines.model;

import java.io.File;
import java.util.Properties;

/**
 * @author aboittiaux
 *
 */
public class DriverContext {

	/**
	 * Properties du test.
	 */
	private Properties properties;
	
	/**
	 * Répertoire où se trouver le fichier de test.
	 */
	private File projectDirectory;

	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * @return the projectDirectory
	 */
	public File getProjectDirectory() {
		return projectDirectory;
	}

	/**
	 * @param projectDirectory the projectDirectory to set
	 */
	public void setProjectDirectory(File projectDirectory) {
		this.projectDirectory = projectDirectory;
	}
	
	
	
}
