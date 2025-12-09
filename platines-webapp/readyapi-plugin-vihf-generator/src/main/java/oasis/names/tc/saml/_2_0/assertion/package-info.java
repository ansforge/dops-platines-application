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
    namespace = "urn:oasis:names:tc:SAML:2.0:assertion",
    xmlns = {
      @javax.xml.bind.annotation.XmlNs(
          prefix = "saml2",
          namespaceURI = "urn:oasis:names:tc:SAML:2.0:assertion")
    },
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package oasis.names.tc.saml._2_0.assertion;
