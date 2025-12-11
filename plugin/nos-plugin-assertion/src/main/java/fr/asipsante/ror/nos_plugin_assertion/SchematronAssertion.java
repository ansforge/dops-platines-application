/*
 * Construction de la pop voulu pour transformer les fichiers de nomenclatures au format xml. 
 */
package fr.asipsante.ror.nos_plugin_assertion;

import java.io.File;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.transform.stream.StreamSource;

import org.apache.xmlbeans.XmlObject;
import org.oclc.purl.dsdl.svrl.SchematronOutputType;
import org.w3c.dom.Document;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.config.TestAssertionConfig;
import com.eviware.soapui.impl.wsdl.panels.assertions.AssertionCategoryMapping;
import com.eviware.soapui.impl.wsdl.teststeps.WsdlMessageAssertion;
import com.eviware.soapui.model.iface.MessageExchange;
import com.eviware.soapui.model.iface.SubmitContext;
import com.eviware.soapui.model.testsuite.Assertable;
import com.eviware.soapui.model.testsuite.AssertionException;
import com.eviware.soapui.model.testsuite.ResponseAssertion;
import com.eviware.soapui.plugins.auto.PluginTestAssertion;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.xml.XmlObjectConfigurationBuilder;
import com.eviware.soapui.support.xml.XmlObjectConfigurationReader;
import com.helger.schematron.ISchematronResource;
import com.helger.schematron.svrl.SVRLFailedAssert;
import com.helger.schematron.svrl.SVRLHelper;
import com.helger.schematron.svrl.SVRLReader;
import com.helger.schematron.xslt.SchematronResourceSCH;

@PluginTestAssertion(id = "Assertion Schematron", label = "Assertion Schematron", category = AssertionCategoryMapping.VALIDATE_RESPONSE_CONTENT_CATEGORY,
description = "Assertion qui valide la conformité d'une réponse xml à un fichier schematron")
public class SchematronAssertion extends WsdlMessageAssertion implements ResponseAssertion {
	private static final String SCHEMATRON_PROPERTY = "schematronProperty";
	
	private static final String ERROR_MESSAGE = "Le fichier ne se trouve pas dans le dossier des préférences,"
			+ " veuillez le copier dans ce dossier pour que le plugin puisse fonctionner correctement";
	/**
	 * Path du fichier schematron.
	 */
	private String schematronPath;
	private JFileChooser chooser;
	/**
	 * Assertions need to have a constructor that takes a TestAssertionConfig
	 * and the ModelItem to be asserted
	 */
	public SchematronAssertion(TestAssertionConfig assertionConfig, Assertable modelItem) {
		super(assertionConfig, modelItem, false, true, true, true);
		XmlObjectConfigurationReader reader = new XmlObjectConfigurationReader(getConfiguration());
		if(reader.readString(SCHEMATRON_PROPERTY, null) != null) {
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
		if(schematronPath != null) {
			chooser.setSelectedFile(new File(schematronPath));
		}
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Fichiers Schematron", "sch");
		chooser.setFileFilter(filter);
		chooser.setCurrentDirectory(new File(SoapUI.getSettings().getString("nosFolder", "Value du folder")));
		dialog.getContentPane().add(chooser);
		chooser.showOpenDialog(dialog);
		schematronPath = chooser.getSelectedFile().getAbsolutePath();
		String nosFolder = SoapUI.getSettings().getString("nosFolder", "Value du folder");
		nosFolder = nosFolder.replaceAll("\\s+","");
		schematronPath = schematronPath.replaceAll("\\s+","");
		File ff = new File(nosFolder);
		String f2 = ff.getAbsolutePath();
		f2 = f2.replaceAll("\\\\", "/");
		schematronPath = schematronPath.replaceAll("\\\\", "/");
		schematronPath = schematronPath.replace(f2+"/", "");
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
	protected String internalAssertResponse(MessageExchange messageExchange, SubmitContext submitContext)
			throws AssertionException {
		//Récupération de la valeur de la propriété user.dir
		String propertyUserDir = System.getProperty("user.dir");
		//Changement de la propriété user.dir pour qu'elle pointe sur le dossier des nomenclatures
		System.setProperty("user.dir", SoapUI.getSettings().getString("nosFolder", "Value du folder"));
		File schematronFile = new File(schematronPath);
		String xmlResponse = messageExchange.getResponse().getContentAsXml();
		ISchematronResource srcSchematron = SchematronResourceSCH.fromFile(schematronFile);
		if(!srcSchematron.isValidSchematron()) {
			throw new IllegalArgumentException("Invalide schematron: " + schematronFile.getAbsolutePath());
		}

		SchematronOutputType outputType = null;
		try {
			Document doc =srcSchematron.applySchematronValidation(new StreamSource(new StringReader(xmlResponse)));
			outputType = SVRLReader.readXML(doc);
		} catch (Exception e) {
			SoapUI.getErrorLog().fatal(e);
			throw new IllegalArgumentException("Invalide schematron: " + schematronFile.getAbsolutePath() + " " + e);
		}
		if(outputType != null) {
			int nbErrors = 0;
			List<SVRLFailedAssert> failedList = SVRLHelper.getAllFailedAssertions(outputType);
			if(failedList.size() != 0) {
				for (SVRLFailedAssert svrlFailedAssert : failedList) {
					SoapUI.log.error("L'assertion a échouée pour le test " + svrlFailedAssert.getTest() +
							" " + svrlFailedAssert.getText() + " a échoué à la position " + svrlFailedAssert.getLocation());
					SoapUI.getErrorLog().error("L'assertion a échouée pour le test " + svrlFailedAssert.getTest() +
							" " + svrlFailedAssert.getText() + " a échoué à la position " + svrlFailedAssert.getLocation());
				}
				nbErrors = failedList.size();
				
				throw new IllegalArgumentException(nbErrors + " erreurs voir le détail dans les logs pour le schematron : " + schematronFile.getName());
			}

		}
		System.setProperty("user.dir", propertyUserDir);
		return "Assertion valide";
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
