/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.model.bulkupdate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author edegenetais
 */
class ValidationReportImpl implements ValidationReport {

  private List<String> errors = new ArrayList<>();

  @Override
  public List<String> errors() {
    return Collections.unmodifiableList(errors);
  }

  @Override
  public boolean isValid() {
    return errors.isEmpty();
  }

  public void addError(String message) {
    errors.add(message);
  }
}
