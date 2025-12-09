/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
/**
 * Les annotations de packages sont faites à la main pour être sur d'inclure le paramètre
 * elementFormDefault qui est mal géré par le générateur jaxb2-maven-plugin. L'anomalie suivante :
 * https://github.com/eclipse-ee4j/jaxb-ri/issues/823 semble avoir régressé ou ne jamais avoir été
 * corrigée.
 */
@javax.xml.bind.annotation.XmlSchema(
    namespace = "urn:hl7-org:v3",
    xmlns = {
      @javax.xml.bind.annotation.XmlNs(prefix = "hl7", namespaceURI = "urn:hl7-org:v3"),
      @javax.xml.bind.annotation.XmlNs(
          prefix = "xsi",
          namespaceURI = "http://wwww.w3.org/2001/XMLSchema-instance")
    },
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package org.hl7.v3;
