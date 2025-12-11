/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.platines.model;

import java.util.Date;

public class DriverSessionResult {

	private Date dateExecution;
	
	private Long timeTaken;
	
	private String status;
	
	private String uuidSession;

	/**
	 * @return the dateExecution
	 */
	public Date getDateExecution() {
		return dateExecution;
	}

	/**
	 * @param dateExecution the dateExecution to set
	 */
	public void setDateExecution(Date dateExecution) {
		this.dateExecution = dateExecution;
	}

	/**
	 * @return the timeTaken
	 */
	public Long getTimeTaken() {
		return timeTaken;
	}

	/**
	 * @param timeTaken the timeTaken to set
	 */
	public void setTimeTaken(Long timeTaken) {
		this.timeTaken = timeTaken;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

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
	
	
	
	
}
