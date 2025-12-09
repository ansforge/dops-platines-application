/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * @author aboittiaux
 */
@Entity
@Table(name = "r_operation_unexpected")
@PrimaryKeyJoinColumn(name = "id")
public class ROperationUnexpected extends RServerOperationHistory {

  /** uuid. */
  private static final long serialVersionUID = 523087600776516365L;

  /** Session. */
  @ManyToOne
  @JoinColumn(name = "SESSIONTST_ID")
  private TestSession testSession;

  /**
   * @return the testSession
   */
  public TestSession getTestSession() {
    return testSession;
  }

  /**
   * @param testSession the testSession to set
   */
  public void setTestSession(TestSession testSession) {
    this.testSession = testSession;
  }
}
