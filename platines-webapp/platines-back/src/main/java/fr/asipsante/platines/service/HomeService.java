/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.service;

import fr.asipsante.platines.dto.FunctionalDataDto;

/**
 * The Home service.
 *
 * @author apierre
 */
public interface HomeService {

  /**
   * Updates the properties for the application.
   *
   * @param content, the content of the home message
   * @return the properties
   */
  FunctionalDataDto updateProperties(String content);

  /**
   * Gets the applicaiton properties.
   *
   * @return the application properties
   */
  FunctionalDataDto getFunctionalData();
}
