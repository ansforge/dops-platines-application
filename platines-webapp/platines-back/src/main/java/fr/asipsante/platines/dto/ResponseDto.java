/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * The type Response dto.
 *
 * @author aboittiaux
 */
public class ResponseDto {

  /** The status. */
  private boolean status;

  /** The error. */
  private String error;

  /** The msg. */
  private String msg;

  /** The token. */
  private String token;

  /** Constructeur. */
  public ResponseDto() {
    super();
  }

  /**
   * Instantiates a new Response dto.
   *
   * @param status the status
   * @param error the error
   * @param msg the msg
   */
  public ResponseDto(boolean status, String error, String msg) {
    super();
    this.status = status;
    this.error = error;
    this.msg = msg;
  }

  /**
   * Is status boolean.
   *
   * @return the status
   */
  public boolean isStatus() {
    return status;
  }

  /**
   * Sets status.
   *
   * @param status the status to set
   */
  public void setStatus(boolean status) {
    this.status = status;
  }

  /**
   * Gets error.
   *
   * @return the error
   */
  public String getError() {
    return error;
  }

  /**
   * Sets error.
   *
   * @param error the error to set
   */
  public void setError(String error) {
    this.error = error;
  }

  /**
   * Gets msg.
   *
   * @return the msg
   */
  public String getMsg() {
    return msg;
  }

  /**
   * Sets msg.
   *
   * @param msg the msg to set
   */
  public void setMsg(String msg) {
    this.msg = msg;
  }

  /**
   * Gets token.
   *
   * @return the token
   */
  public String getToken() {
    return token;
  }

  /**
   * Sets token.
   *
   * @param token the token to set
   */
  public void setToken(String token) {
    this.token = token;
  }
}
