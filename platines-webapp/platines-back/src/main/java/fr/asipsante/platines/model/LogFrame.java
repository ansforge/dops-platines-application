/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.model;

import java.io.InputStream;

public class LogFrame {

  private int offset;

  private InputStream frame;

  public LogFrame() {
    super();
  }

  public LogFrame(int offset, InputStream frame) {
    super();
    this.offset = offset;
    this.frame = frame;
  }

  public InputStream getFrame() {
    return frame;
  }

  public void setFrame(InputStream frame) {
    this.frame = frame;
  }

  public int getOffset() {
    return offset;
  }

  public void setOffset(int offset) {
    this.offset = offset;
  }
}
