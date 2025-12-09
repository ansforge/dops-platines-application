/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

/**
 * Enum of the job status.
 *
 * @author apierre
 */
public enum JobStatus {

  /** Status unknow. */
  UNKNOW,

  /** Status queued. */
  QUEUED,

  /** Status pending. */
  PENDING,

  /** Status running. */
  RUNNING,

  /** Status complete. */
  COMPLETE,

  /** Status cancelled. */
  CANCELLED,

  /** Status dead. */
  DEAD;
}
