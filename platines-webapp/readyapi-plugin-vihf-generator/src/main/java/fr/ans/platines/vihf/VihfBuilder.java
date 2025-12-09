/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.vihf;

import static fr.ans.platines.vihf.utils.VihfUtils.toUtf8;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import oasis.names.tc.saml._2_0.assertion.Assertion;
import oasis.names.tc.saml._2_0.assertion.Attribute;
import oasis.names.tc.saml._2_0.assertion.AttributeStatement;
import oasis.names.tc.saml._2_0.assertion.AttributeValue;
import oasis.names.tc.saml._2_0.assertion.AuthnContext;
import oasis.names.tc.saml._2_0.assertion.AuthnStatement;
import oasis.names.tc.saml._2_0.assertion.Issuer;
import oasis.names.tc.saml._2_0.assertion.Subject;
import org.hl7.v3.ProfilUtilisateur;
import org.hl7.v3.Role;
import org.hl7.v3.VIHFProfil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VihfBuilder {

  /** Logger definition. */
  private static final transient Logger LOGGER =
      LoggerFactory.getLogger(VihfBuilder.class.getName());

  private Profil profil;

  private oasis.names.tc.saml._2_0.assertion.ObjectFactory assertionFactory;
  private org.hl7.v3.ObjectFactory profilFactory;

  private String dateNow;

  private String dn;

  public VihfBuilder(Profil profil, String dn) {
    this.dn = dn;
    this.profil = profil;
    this.assertionFactory = new oasis.names.tc.saml._2_0.assertion.ObjectFactory();
    this.profilFactory = new org.hl7.v3.ObjectFactory();
    final Date currentTime = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
    dateNow = sdf.format(currentTime);
  }

  public String buildAssertion() {

    String tokenVIHF = "";
    try {
      JAXBContext context =
          JAXBContext.newInstance(
              oasis.names.tc.saml._2_0.assertion.ObjectFactory.class.getPackage().getName(),
              oasis.names.tc.saml._2_0.assertion.ObjectFactory.class.getClassLoader());
      Marshaller marshaller = context.createMarshaller();
      marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
      marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

      StringWriter sw = new StringWriter();

      marshaller.marshal(fetchAssertion(), sw);

      tokenVIHF = sw.toString();
    } catch (JAXBException e) {
      LOGGER.error("Error when building assertion", e);
    }

    return tokenVIHF;
  }

  private Subject fetchSubject() {
    Subject subject = assertionFactory.createSubject();
    subject.setNameID("18751275100020/26");
    return subject;
  }

  private Issuer fetchIssuer() {

    Issuer issuer = assertionFactory.createIssuer();
    issuer.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:X509SubjectName");
    issuer.setValue(dn);
    return issuer;
  }

  private AuthnContext fetchAuthnContext() {
    AuthnContext authnContext = assertionFactory.createAuthnContext();
    authnContext.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified");
    return authnContext;
  }

  private AuthnStatement fetchAuthnStatement() {
    AuthnStatement authnStatement = assertionFactory.createAuthnStatement();
    authnStatement.setAuthnInstant(dateNow);
    authnStatement.setAuthnContext(fetchAuthnContext());
    return authnStatement;
  }

  private ProfilUtilisateur fetchProfilUtilisateur() {
    ProfilUtilisateur profilUtilisateur = profilFactory.createProfilUtilisateur();
    profilUtilisateur.setCode(profil.getCode());
    profilUtilisateur.setCodeSystem("1.2.250.1.213.1.6.1.66");
    profilUtilisateur.setDisplayName(toUtf8(profil.getDisplayName()));
    profilUtilisateur.setType("CE");
    return profilUtilisateur;
  }

  private Role fetchRole() {
    Role role = profilFactory.createRole();
    role.setCode("AUTOMATE");
    role.setCodeSystem("1.2.250.1.213.1.1.4.6");
    role.setDisplayName("Automate");
    role.setType("CE");
    return role;
  }

  private VIHFProfil fetchVIHFProfil() {
    VIHFProfil vihfProfil = profilFactory.createVIHFProfil();
    vihfProfil.setCode("profil_referentiel");
    vihfProfil.setCodeSystem("1.2.250.1.213.1.1.4.312");
    vihfProfil.setDisplayName(toUtf8("Accès à un référentiel"));
    vihfProfil.setType("CE");
    return vihfProfil;
  }

  private Attribute fetchAttribute(Object attributeContent, String attributeName) {
    AttributeValue attributeValue = assertionFactory.createAttributeValue();
    attributeValue.getContent().add(attributeContent);
    Attribute attribute = assertionFactory.createAttribute();
    attribute.setName(attributeName);
    attribute.setAttributeValue(attributeValue);
    return attribute;
  }

  private AttributeStatement fetchAttributeStatement() {
    AttributeStatement attributeStatement = assertionFactory.createAttributeStatement();
    attributeStatement.getAttribute().add(fetchAttribute("2.0", "VIHF_Version"));
    attributeStatement.getAttribute().add(fetchAttribute("urn:ROR", "Ressource_URN"));
    attributeStatement
        .getAttribute()
        .add(fetchAttribute(fetchRole(), "urn:oasis:names:tc:xacml:2.0:subject:role"));
    attributeStatement
        .getAttribute()
        .add(fetchAttribute("Platines", "urn:oasis:names:tc:xspa:1.0:subject:subject-id"));
    attributeStatement
        .getAttribute()
        .add(fetchAttribute(fetchProfilUtilisateur(), "Profil_Utilisateur"));
    attributeStatement.getAttribute().add(fetchAttribute(fetchVIHFProfil(), "VIHF_Profil"));
    return attributeStatement;
  }

  private Assertion fetchAssertion() {
    Assertion assertion = assertionFactory.createAssertion();
    assertion.setIssuer(fetchIssuer());
    assertion.setIssueInstant(dateNow);
    assertion.setSubject(fetchSubject());
    assertion.setID(UUID.randomUUID().toString());
    assertion.setAttributeStatement(fetchAttributeStatement());
    assertion.setAuthnStatement(fetchAuthnStatement());
    assertion.setVersion(new BigDecimal("2.0"));
    return assertion;
  }
}
