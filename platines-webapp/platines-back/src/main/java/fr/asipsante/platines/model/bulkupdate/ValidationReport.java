/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.model.bulkupdate;

import java.util.List;

/**
 * @author edegenetais
 */
public interface ValidationReport {

  public List<String> errors();

  public boolean isValid();
}
