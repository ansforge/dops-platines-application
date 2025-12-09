/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.vihf;

import static fr.ans.platines.vihf.utils.VihfUtils.getDocumentFromString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.eviware.soapui.impl.wsdl.WsdlSubmitContext;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlDataSourceTestStep;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.project.Project;
import fr.ans.platines.vihf.readyapi.VIHFRequestFilter;
import fr.ans.platines.vihf.readyapi.X509ClientAuthCertificate;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@ExtendWith(MockitoExtension.class)
class VIHFRequestFilterTest {

  private static final String DN =
      "CN=CLIENT_WS_ROR,OU=318751275100020,O=AGENCE DES SYSTEMES D'INFORMATION PARTAG,ST=Paris"
          + " (75),C=FR";

  private static final Logger logger = LoggerFactory.getLogger(VIHFRequestFilterTest.class);

  @Mock private ModelItem context;

  @Spy private WsdlSubmitContext submitContext = new WsdlSubmitContext(context);

  @Mock private Request request;

  @Mock private X509ClientAuthCertificate clientCertificate;

  @Mock private Project project;

  @Mock private WsdlTestCase testCase;

  @Mock private ModelItem item;

  private String requestbody =
      """
			<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
			  <soap:Header/>
			  <soap:Body/>
			</soap:Envelope>
				""";

  private VIHFRequestFilter filter = new VIHFRequestFilter();

  @Test
  void testAddVihf() throws Exception {
    lenient().when(clientCertificate.getDN()).thenReturn(DN);
    lenient().when(submitContext.getModelItem()).thenReturn(project);
    lenient().when(project.getPropertyValue(anyString())).thenReturn("0");
    lenient().when(request.getParent()).thenReturn(item);
    lenient().when(item.getParent()).thenReturn(testCase);
    lenient()
        .when(testCase.getTestStepsOfType(WsdlDataSourceTestStep.class))
        .thenReturn((List<WsdlDataSourceTestStep>) Collections.EMPTY_LIST);
    lenient().when(submitContext.getProperty("requestContent")).thenReturn(requestbody);
    filter.setX509ClientAuthCert(clientCertificate);
    ;
    filter.filterRequest(submitContext, request);
    when(submitContext.getProperty("requestContent")).thenCallRealMethod();
    String soapMessage = (String) submitContext.getProperty("requestContent");
    Document xmlMessage = getDocumentFromString(soapMessage);
    NodeList nodes = xmlMessage.getChildNodes();
    Node envelope, header, security, assertion = null;

    for (int i = 0; i < nodes.getLength(); i++) {
      envelope = nodes.item(i);
      if (envelope.getNodeType() == Node.ELEMENT_NODE) {
        NodeList nl2 = envelope.getChildNodes();
        for (int i2 = 0; i2 < nl2.getLength(); i2++) {
          header = nl2.item(i2);
          if (header.getNodeName().equals("soap:Header")) {
            if (header.hasChildNodes()) {
              if (header.getFirstChild().getNodeName().equals("wsse:Security")) {
                security = header.getFirstChild();
                if (security.getFirstChild().getNodeName().equals("saml2:Assertion")) {
                  assertion = security.getFirstChild();
                }
              }
            }
          }
        }
      }
      assertTrue(assertion.getTextContent().contains(DN));
      // On doit trouver le DN dans l'assertion
    }
  }
}
