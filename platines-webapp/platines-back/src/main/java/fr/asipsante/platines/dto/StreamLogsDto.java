/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * @author apierre
 */
public class StreamLogsDto {

  /** nombre de lignes. */
  private int nbLines;

  /** lignes. */
  private String[] lines;

  /**
   * @return the nbLines
   */
  public int getNbLines() {
    return nbLines;
  }

  /**
   * @param nbLines the nbLines to set
   */
  public void setNbLines(int nbLines) {
    this.nbLines = nbLines;
  }

  /**
   * @return the lines
   */
  public String[] getLines() {
    return lines;
  }

  /**
   * @param lines the lines to set
   */
  public void setLines(String[] lines) {
    this.lines = lines;
  }
}
