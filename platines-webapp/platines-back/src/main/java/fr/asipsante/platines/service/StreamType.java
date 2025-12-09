/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

public enum StreamType {
  STDOUT("stdout"),
  STDERR("stderr");

  public final String label;

  private StreamType(String label) {
    this.label = label;
  }
}
