/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.platines.model;

import java.io.File;

public class SessionLog {

	private String uuidSession;
	
	private File sessionLog;
	
	/**
	 * @return the uuidSession
	 */
	public String getUuidSession() {
		return uuidSession;
	}
	/**
	 * @param uuidSession the uuidSession to set
	 */
	public void setUuidSession(String uuidSession) {
		this.uuidSession = uuidSession;
	}
	/**
	 * @return the sessionLog
	 */
	public File getSessionLog() {
		return sessionLog;
	}
	/**
	 * @param sessionLog the sessionLog to set
	 */
	public void setSessionLog(File sessionLog) {
		this.sessionLog = sessionLog;
	}
	
	
}
