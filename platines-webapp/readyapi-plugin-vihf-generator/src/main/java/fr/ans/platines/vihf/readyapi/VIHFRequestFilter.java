/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.vihf.readyapi;

import static fr.ans.platines.vihf.utils.VihfUtils.getDocumentFromString;
import static fr.ans.platines.vihf.utils.VihfUtils.getXMLStringFromDocument;

import com.eviware.soapui.impl.wsdl.submit.filters.AbstractRequestFilter;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlDataSourceTestStep;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.iface.SubmitContext;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.support.ModelSupport;
import com.eviware.soapui.plugins.auto.PluginRequestFilter;
import fr.ans.platines.vihf.Profil;
import fr.ans.platines.vihf.VihfBuilder;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@PluginRequestFilter(protocol = "http")
public class VIHFRequestFilter extends AbstractRequestFilter {

  /** Logger definition. */
  private static final Logger logger = LoggerFactory.getLogger(VIHFRequestFilter.class);

  private String propertyName = "Profil_VIHF";

  private X509ClientAuthCertificate x509ClientAuthCert = new X509ClientAuthCertificate();

  public void setX509ClientAuthCert(X509ClientAuthCertificate x509ClientAuthCert) {
    this.x509ClientAuthCert = x509ClientAuthCert;
  }

  @Override
  public void filterRequest(SubmitContext context, Request request) {

    Project project = ModelSupport.getModelItemProject(context.getModelItem());
    WsdlTestCase testCase = (WsdlTestCase) request.getParent().getParent();
    String dataSourceProfilValue = "";
    String projectProfilValue = "";
    String testCaseProfilValue = "";
    // On récupère la valeur du profil au niveau projet si elle est renseignée
    projectProfilValue =
        project.getPropertyValue(propertyName) == null
            ? ""
            : project.getPropertyValue(propertyName);
    // On récupère la valeur du profil au niveau cas de test si renseignée
    testCaseProfilValue =
        testCase.getPropertyValue(propertyName) == null
            ? ""
            : testCase.getPropertyValue(propertyName);
    // On cherche une datasource du même nom (Profil_VIHF) et dans cette datasource une propriété du
    // même nom
    List<WsdlDataSourceTestStep> datasources =
        testCase.getTestStepsOfType(WsdlDataSourceTestStep.class);

    for (WsdlDataSourceTestStep wsdlDataSourceTestStep : datasources) {
      if (propertyName.equals(wsdlDataSourceTestStep.getName())) {
        dataSourceProfilValue = wsdlDataSourceTestStep.getPropertyValue(propertyName);
      }
    }
    VihfBuilder builder;

    String dn = x509ClientAuthCert.getDN();
    String profilValue = "";
    // On charge depuis la datasource en premier, puis celui du test, puis celui du projet
    if (dataSourceProfilValue.matches("^([0-3]){1}$")) {
      logger.info("Application du profil VIHF datasource.");
      profilValue = dataSourceProfilValue;
    } else if (testCaseProfilValue.matches("^([0-3]){1}$")) {
      logger.info("Application du profil VIHF cas de test.");
      profilValue = testCaseProfilValue;
    } else if (projectProfilValue.matches("^([0-3]){1}$")) {
      logger.info("Application du profil VIHF projet.");
      profilValue = projectProfilValue;
    } else {
      // do nothing
      logger.info("Aucun profil VIHF n'a été défini pour ce test");
    }
    if (profilValue.isEmpty()) {
      logger.info("Valeur de profil sélectionnée vide: rien à faire.");
    } else {
      Profil profil = new Profil(profilValue);
      builder = new VihfBuilder(profil, dn);
      String requestBody = (String) context.getProperty("requestContent");
      Document requestBodyDoc = getDocumentFromString(requestBody);
      String vihfToken = builder.buildAssertion();
      Document tokenVIHFDoc = getDocumentFromString(vihfToken);

      XPath xpath = XPathFactory.newInstance().newXPath();
      Node requestBodyHeaderNode = null;
      try {
        requestBodyHeaderNode =
            (Node)
                ((NodeList)
                        xpath.evaluate(
                            "//*[local-name()='Header']", requestBodyDoc, XPathConstants.NODESET))
                    .item(0);
      } catch (XPathExpressionException e) {
        logger.error("Error when injecting vihf token", e);
      }

      Node assertion = requestBodyDoc.importNode(tokenVIHFDoc.getDocumentElement(), true);

      Node security = null;
      security =
          requestBodyDoc.createElementNS(
              "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
              "Security");
      security.setPrefix("wsse");
      security.appendChild(assertion);

      requestBodyHeaderNode.appendChild(security);

      requestBodyDoc.getDocumentElement().normalize();

      String outRequest = getXMLStringFromDocument(requestBodyDoc);
      context.setProperty("requestContent", outRequest);
    }
  }
}
