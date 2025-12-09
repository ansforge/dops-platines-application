/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service.impl.common;

import java.util.Date;
import org.springframework.stereotype.Component;

/**
 * @author apierre
 */
@Component
public class DateConverter {

  /**
   * Convertit une date en date au format UTC.
   *
   * @param date, la date à convertir
   * @return la date au format UTC
   */
  public Date convertToUTC(Date date) {
    //        final DateTimeZone zone = DateTimeZone.getDefault();
    //        final long utc = zone.convertLocalToUTC(date.getTime(), false);
    //        return new Date(utc);

    // TODO: Date conversion doesn't seem necessary? This method in particular seems to "convert"
    // the time to UTC twice in a row
    // which results in the time being off by 2 hours. I've replaced the code by a simple return
    // statement but it should most likely
    // be removed entirely.
    return date;
  }
}
