/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.executor.soapui;

/**
 * Enumération représentant les balises présentent dans un projet SoapUI.
 *
 * @author aboittiaux
 */
public enum SoapUIXmlElements {

  /** Balise description. */
  DESCRIPTION("con:description"),
  /** Balise schematron. */
  SCHEMATRON_PROPERTY("schematronProperty"),
  /** Balise testSuite. */
  TEST_SUITE("con:testSuite"),
  /** Balise testCase. */
  TEST_CASE("con:testCase"),
  /** Balise testStep. */
  TEST_STEP("con:testStep"),
  /** Attribut name. */
  ATTRIBUTE_NAME("name"),
  /** Role projet. */
  ROLE_PROJET("con:mockService"),
  /** Role projet. */
  ROLE_PROJET_REST("con:restMockService"),
  /** Fichier associé au projet. */
  FILE("file"),
  /** Name property. */
  PROPERTIES("con:properties"),
  /** Name property. */
  PROPERTY("con:property"),
  /** Name property. */
  PROPERTY_NAME("con:name"),
  /** Value property. */
  PROPERTY_VALUE("con:value"),
  /** Path Attribute. */
  PATH_ATTRIBUTE("path");

  /** Récupération de la valeur de l'énumération. */
  private String label;

  /**
   * @param label
   */
  private SoapUIXmlElements(String label) {
    this.label = label;
  }

  /**
   * @return the label
   */
  public String getLabel() {
    return label;
  }
  ;
}
