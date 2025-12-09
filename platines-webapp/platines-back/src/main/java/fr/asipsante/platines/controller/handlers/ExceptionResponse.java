/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller.handlers;

import java.util.Date;

public class ExceptionResponse {

  Date timesatmp;

  String message;

  String detail;

  public ExceptionResponse(Date timesatmp, String message, String detail) {
    super();
    this.timesatmp = timesatmp;
    this.message = message;
    this.detail = detail;
  }

  public Date getTimesatmp() {
    return timesatmp;
  }

  public void setTimesatmp(Date timesatmp) {
    this.timesatmp = timesatmp;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getDetail() {
    return detail;
  }

  public void setDetail(String detail) {
    this.detail = detail;
  }
}
