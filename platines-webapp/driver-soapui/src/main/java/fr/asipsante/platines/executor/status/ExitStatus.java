/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.executor.status;

/**
 * @author apierre
 */
public enum ExitStatus {
  ZIP_ERROR(-1),
  SESSION_DIRECTORY_ERROR(-2);

  private int value;

  private ExitStatus(int value) {
    this.value = value;
  }

  /**
   * @return the value
   */
  public int getValue() {
    return value;
  }
}
