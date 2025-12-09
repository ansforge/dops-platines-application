/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.readyapi.schematron;

import com.eviware.soapui.config.TestAssertionConfig;
import com.eviware.soapui.impl.wsdl.panels.assertions.AssertionCategoryMapping;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlMessageAssertion;
import com.eviware.soapui.model.iface.MessageExchange;
import com.eviware.soapui.model.iface.SubmitContext;
import com.eviware.soapui.model.testsuite.Assertable;
import com.eviware.soapui.model.testsuite.AssertionError;
import com.eviware.soapui.model.testsuite.AssertionException;
import com.eviware.soapui.model.testsuite.ResponseAssertion;
import com.eviware.soapui.plugins.auto.PluginTestAssertion;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.xml.XmlObjectConfigurationBuilder;
import com.eviware.soapui.support.xml.XmlObjectConfigurationReader;
import com.helger.commons.io.resource.FileSystemResource;
import com.helger.schematron.sch.SchematronResourceSCH;
import com.helger.schematron.svrl.SVRLFailedAssert;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.jaxb.SchematronOutputType;
import com.smartbear.ready.core.ApplicationEnvironment;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.transform.stream.StreamSource;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginTestAssertion(
    id = "Assertion Schematron",
    label = "Assertion Schematron",
    category = AssertionCategoryMapping.VALIDATE_RESPONSE_CONTENT_CATEGORY,
    description = "Assertion qui valide la conformité d'une réponse xml via un script schematron")
public class SchematronAssertion extends WsdlMessageAssertion implements ResponseAssertion {

  private static final Logger LOGGER = LoggerFactory.getLogger(SchematronAssertion.class);

  private static final String SCHEMATRON_PROPERTY = "schematronProperty";

  private static final String ERROR_MESSAGE =
      "Le fichier ne se trouve pas dans le dossier des préférences, veuillez le copier dans ce"
          + " dossier pour que le plugin puisse fonctionner correctement";

  /** Path du fichier schematron. */
  private String schematronPath;

  private JFileChooser chooser;

  /**
   * Assertions need to have a constructor that takes a TestAssertionConfig and the ModelItem to be
   * asserted
   */
  public SchematronAssertion(TestAssertionConfig assertionConfig, Assertable modelItem) {
    super(assertionConfig, modelItem, false, true, true, true);
    XmlObjectConfigurationReader reader = new XmlObjectConfigurationReader(getConfiguration());
    if (reader.readString(SCHEMATRON_PROPERTY, null) != null) {
      schematronPath = reader.readString(SCHEMATRON_PROPERTY, null);
    }
  }

  @Override
  public boolean configure() {

    JDialog dialog = new JDialog(new JFrame());
    dialog.setSize(500, 500);
    chooser = new JFileChooser();
    chooser.setDialogTitle("Fichier Schematron");
    chooser.setDialogType(JFileChooser.SAVE_DIALOG);
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    if (schematronPath != null) {
      chooser.setSelectedFile(new File(schematronPath));
    }
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Fichiers Schematron", "sch");
    chooser.setFileFilter(filter);
    chooser.setCurrentDirectory(
        new File(ApplicationEnvironment.getSettings().getString("nosFolder", "Value du folder")));
    dialog.getContentPane().add(chooser);
    chooser.showOpenDialog(dialog);
    schematronPath = chooser.getSelectedFile().getAbsolutePath();
    String nosFolder =
        ApplicationEnvironment.getSettings().getString("nosFolder", "Value du folder");
    nosFolder = nosFolder.replaceAll("\\s+", "");
    schematronPath = schematronPath.replaceAll("\\s+", "");
    File ff = new File(nosFolder);
    String f2 = ff.getAbsolutePath();
    f2 = f2.replaceAll("\\\\", "/");
    schematronPath = schematronPath.replaceAll("\\\\", "/");
    schematronPath = schematronPath.replace(f2 + "/", "");
    if (!schematronPath.equals(chooser.getSelectedFile().getName())) {
      UISupport.showInfoMessage(ERROR_MESSAGE);
    }
    setConfiguration(createConfiguration());

    return true;
  }

  private XmlObject createConfiguration() {
    XmlObjectConfigurationBuilder builder = new XmlObjectConfigurationBuilder();
    return builder.add(SCHEMATRON_PROPERTY, schematronPath).finish();
  }

  @Override
  protected String internalAssertResponse(
      MessageExchange messageExchange, SubmitContext submitContext) throws AssertionException {
    // Récupération de la valeur de la propriété user.dir
    String propertyUserDir = System.getProperty("user.dir");
    try {
      final String nosFolder =
          ApplicationEnvironment.getSettings().getString("nosFolder", "Value du folder");
      // Changement de la propriété user.dir pour qu'elle pointe sur le dossier des
      // nomenclatures
      System.setProperty("user.dir", nosFolder);
      File schematronFile = new File(new File(nosFolder), new File(schematronPath).getName());
      String xmlResponse = messageExchange.getResponse().getContentAsXml();

      LOGGER.info("Application du schematron " + schematronPath);

      SchematronResourceSCH srcSchematron =
          new SchematronResourceSCH(new FileSystemResource(schematronFile));

      SchematronOutputType outputType = null;
      try {
        outputType =
            srcSchematron.applySchematronValidationToSVRL(
                new StreamSource(new StringReader(xmlResponse)));
      } catch (Exception e) {
        LOGGER.error(
            "Erreur technique lors du contrôle  : {}", schematronFile.getAbsolutePath(), e);
        throw new IllegalArgumentException(
            "Erreur technique lors du contrôle  : " + schematronFile.getAbsolutePath() + " " + e);
      }

      if (outputType != null) {
        List<Object> output = outputType.getActivePatternAndFiredRuleAndFailedAssert();
        int nbFiredRules =
            output.stream()
                .filter(o -> o.getClass().getSimpleName().equals("FiredRule"))
                .collect(Collectors.toList())
                .size();
        if (nbFiredRules == 0) {
          LOGGER.error("Aucune règle n'a pu être exécutée dans ce pattern");
          throw new AssertionException(
              new AssertionError("Aucune règle n'a pu être exécutée dans ce pattern"));
        }
        List<SVRLFailedAssert> failedList = SVRLHelper.getAllFailedAssertions(outputType);
        if (failedList.size() != 0) {
          List<AssertionError> errors = new ArrayList<>();
          for (SVRLFailedAssert svrlFailedAssert : failedList) {
            LOGGER.error(
                "L'assertion a échouée pour le test "
                    + svrlFailedAssert.getTest()
                    + " "
                    + svrlFailedAssert.getText()
                    + " a échoué à la position "
                    + svrlFailedAssert.getLocation());
            errors.add(new AssertionError(svrlFailedAssert.getText()));
          }
          throw new AssertionException(errors.toArray(new AssertionError[errors.size()]));
        }
      }
    } catch (AssertionException | IllegalArgumentException e) {
      // Passthrough for expected exceptions
      throw e;
    } catch (RuntimeException | Error e) {
      // Make sure unexpected exceptions will trigger a message
      LOGGER.error("Error while trying to apply Schematron", e);
      throw e;
    } finally {
      System.setProperty("user.dir", propertyUserDir);
    }
    return "Assertion Schematron valide";
  }

  public String getSchematronPath() {
    return schematronPath;
  }

  public void setSchematronPath(String schematronPath) {
    this.schematronPath = schematronPath;
  }

  public static String getSchematronProperty() {
    return SCHEMATRON_PROPERTY;
  }
}
