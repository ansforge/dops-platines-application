/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.soapui.assertions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.config.TestAssertionConfig;
import com.eviware.soapui.model.iface.MessageExchange;
import com.eviware.soapui.model.iface.Response;
import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.model.testsuite.Assertable;
import com.eviware.soapui.model.testsuite.AssertionException;
import fr.ans.platines.readyapi.schematron.SchematronAssertion;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class SchematronAssertionTest extends SchematronAssertion {

  @Mock private MessageExchange message;
  @Mock private Response response;

  @TempDir private File soapuiHome;

  public SchematronAssertionTest(
      @Mock TestAssertionConfig assertionConfig, @Mock Assertable modelItem) {
    super(assertionConfig, modelItem);
  }

  @BeforeEach
  public void makeSettings() throws Exception {
    System.setProperty("soapui.home", soapuiHome.getAbsolutePath());
    Settings settings = SoapUI.getSettings();
    URL urlSch =
        Thread.currentThread().getContextClassLoader().getResource("validation_identifiants.sch");
    String nosFolder = new File(urlSch.toURI()).getParentFile().getAbsolutePath();
    settings.setString("nosFolder", nosFolder);
    String saveSettings = SoapUI.saveSettings();
    LoggerFactory.getLogger(SchematronAssertionTest.class)
        .debug("Save settings returned {}", saveSettings);
  }

  @Test
  public void testInternalAssertResponse() throws Exception {
    String schematronPath = "validation_identifiants.sch";
    super.setSchematronPath(schematronPath);
    InputStream responseStream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("reponse.xml");
    StringBuilder sb = new StringBuilder();
    for (int ch; (ch = responseStream.read()) != -1; ) {
      sb.append((char) ch);
    }
    lenient().when(message.getResponse()).thenReturn(response);
    lenient().when(message.getResponse().getContentAsXml()).thenReturn(sb.toString());
    AssertionException ex =
        assertThrows(AssertionException.class, () -> super.internalAssertResponse(message, null));
    assertEquals(3, ex.getErrorCount());
  }

  @Test
  public void testInternalAssertResponseNoFiredRules() throws Exception {
    String schematronPath = "validation_identifiants.sch";
    super.setSchematronPath(schematronPath);

    InputStream responseStream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("bad_response.xml");
    StringBuilder sb = new StringBuilder();
    for (int ch; (ch = responseStream.read()) != -1; ) {
      sb.append((char) ch);
    }
    lenient().when(message.getResponse()).thenReturn(response);
    lenient().when(message.getResponse().getContentAsXml()).thenReturn(sb.toString());
    AssertionException ex =
        assertThrows(AssertionException.class, () -> super.internalAssertResponse(message, null));
    assertEquals(1, ex.getErrorCount());
  }

  @Test
  public void testSupportForCurrentFunctionRulesOK() throws Exception {
    String schematronPath = "use_of_current.sch";
    super.setSchematronPath(schematronPath);

    InputStream responseStream =
        Thread.currentThread().getContextClassLoader().getResourceAsStream("reponse.xml");
    StringBuilder sb = new StringBuilder();
    for (int ch; (ch = responseStream.read()) != -1; ) {
      sb.append((char) ch);
    }
    lenient().when(message.getResponse()).thenReturn(response);
    lenient().when(message.getResponse().getContentAsXml()).thenReturn(sb.toString());
    assertEquals("Assertion Schematron valide", super.internalAssertResponse(message, null));
  }

  @Test
  public void testSupportForCurrentFunctionRulesDontApply() throws Exception {
    String schematronPath = "use_of_current.sch";
    super.setSchematronPath(schematronPath);

    InputStream responseStream =
        Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("current_no_rule_reponse.xml");
    StringBuilder sb = new StringBuilder();
    for (int ch; (ch = responseStream.read()) != -1; ) {
      sb.append((char) ch);
    }
    lenient().when(message.getResponse()).thenReturn(response);
    lenient().when(message.getResponse().getContentAsXml()).thenReturn(sb.toString());
    AssertionException ex =
        assertThrows(AssertionException.class, () -> super.internalAssertResponse(message, null));
    assertEquals(1, ex.getErrorCount());
  }
}
