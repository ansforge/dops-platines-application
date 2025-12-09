/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.exception;

/**
 * Exception générique utilisée pour signaler la violation d'un règle de validité. Cette exception
 * donnera lieu à un code 400 + avec une reason string qui contient le message de cette exception.
 *
 * @see {@link fr.asipsante.platines.controller.handlers.BrokenRuleExceptionHandler}
 * @author edegenetais
 */
public class BrokenRuleException extends ApiException {

  /**
   * @param errorCode code catégorié (comme exigé par la superclasse {@link ApiException}).
   * @param message message décrivant la règle violéee, remontée au client (front).
   */
  public BrokenRuleException(String errorCode, String message) {
    super(errorCode, message);
  }

  /**
   * Variant permettant de remonter une exception remontée par un code de validation tiers.
   *
   * @param errorCode code catégorié (comme exigé par la superclasse {@link ApiException}).
   */
  public BrokenRuleException(String errorCode, String message, Throwable cause) {
    super(cause, errorCode, message);
  }
}
