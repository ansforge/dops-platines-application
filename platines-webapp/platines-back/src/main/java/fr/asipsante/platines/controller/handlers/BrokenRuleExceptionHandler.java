/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.controller.handlers;

import fr.asipsante.platines.exception.BrokenRuleException;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@RestController
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BrokenRuleExceptionHandler extends ResponseEntityExceptionHandler {
  public static Logger LOGGER = LoggerFactory.getLogger(BrokenRuleExceptionHandler.class);

  @ExceptionHandler(BrokenRuleException.class)
  public final ResponseEntity<Object> handleAllExceptions(
      BrokenRuleException ex, WebRequest request) {
    LOGGER.error("Invalid upload", ex);
    ExceptionResponse exceptionResponse =
        new ExceptionResponse(new Date(), ex.getMessage(), request.getDescription(false));
    return new ResponseEntity<>(exceptionResponse, HttpStatus.PRECONDITION_FAILED);
  }
}
