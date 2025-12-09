/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.model;

/**
 * @author apierre
 */
public class SessionLog {

  /***
   * session uuid.
   */
  private String uuidSession;

  /** session log. */
  private byte[] sessionLog;

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
  public byte[] getSessionLog() {
    return sessionLog;
  }

  /**
   * @param sessionLog the sessionLog to set
   */
  public void setSessionLog(byte[] sessionLog) {
    this.sessionLog = sessionLog;
  }
}
