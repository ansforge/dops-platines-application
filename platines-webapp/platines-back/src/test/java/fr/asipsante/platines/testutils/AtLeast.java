/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.testutils;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * @author edegenetais
 */
public class AtLeast extends BaseMatcher<Long> {

  private int limit;

  public static AtLeast atLeast(final int par) {
    return new AtLeast(par);
  }

  public AtLeast(final int limitValue) {
    limit = limitValue;
  }

  @Override
  public boolean matches(Object o) {
    return Long.parseLong(o.toString()) >= limit;
  }

  @Override
  public void describeTo(Description d) {
    d.appendText("Expected >= " + limit);
  }
}
