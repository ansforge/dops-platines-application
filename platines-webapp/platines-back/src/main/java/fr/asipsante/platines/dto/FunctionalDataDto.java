/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.dto;

/**
 * @author apierre
 */
public class FunctionalDataDto {

  /** Content. */
  private String content;

  /** Default constructor. */
  public FunctionalDataDto() {
    super();
  }

  /**
   * Constructor with parameters.
   *
   * @param content, the functional data content
   */
  public FunctionalDataDto(String content) {
    super();
    this.content = content;
  }

  /**
   * @return the content
   */
  public String getContent() {
    return content;
  }

  /**
   * @param content the content to set
   */
  public void setContent(String content) {
    this.content = content;
  }
}
