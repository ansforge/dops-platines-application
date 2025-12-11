package fr.plugin.soapui;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.eviware.soapui.impl.wsdl.submit.filters.AbstractRequestFilter;
import com.eviware.soapui.impl.wsdl.testcase.WsdlTestCase;
import com.eviware.soapui.model.iface.Request;
import com.eviware.soapui.model.iface.SubmitContext;
import com.eviware.soapui.model.project.Project;
import com.eviware.soapui.model.support.ModelSupport;
import com.eviware.soapui.plugins.auto.PluginRequestFilter;

@PluginRequestFilter(protocol = "http")
public class VIHFRequestFilter extends AbstractRequestFilter {

	/**
	 * Logger definition.
	 */
	private transient static final Logger LOGGER = Logger.getLogger(VIHFRequestFilter.class.getName());
	
	private String propertyName = "Profil_VIHF";
	
	private Profil profil;
	
	private Vihf vihf = new Vihf();

	@Override
	public void filterRequest(SubmitContext context, Request request) {

		Project project = ModelSupport.getModelItemProject(context.getModelItem());
		WsdlTestCase testCase = (WsdlTestCase) request.getParent().getParent();

		Profil projectProfil = new Profil(project.getPropertyValue(propertyName));
		Profil testCaseProfil = new Profil(testCase.getPropertyValue(propertyName));

		if (testCaseProfil.isValid()) {
			profil = new Profil(testCaseProfil.getValue());
		} else if (projectProfil.isValid()) {
			profil = new Profil(projectProfil.getValue());
		} else {
			profil = new Profil();
			LOGGER.log(Level.INFO, "Aucun profile VIHF n'a été défini pour ce test");
		}

		if (profil.isValid()) {

			String requestBody = (String) context.getProperty("requestContent");
			Document requestBodyDoc = convertStringToXMLDocument(requestBody);

			vihf.setProfil(profil);
			String tokenVIHF = vihf.generateVIHF();
			Document tokenVIHFDoc = convertStringToXMLDocument(tokenVIHF);

			XPath xpath = XPathFactory.newInstance().newXPath();
			Node requestBodyHeaderNode = null;
			try {
				requestBodyHeaderNode = (Node) ((NodeList) xpath.evaluate("//*[local-name()='Header']", requestBodyDoc,
						XPathConstants.NODESET)).item(0);
			} catch (XPathExpressionException e) {
				LOGGER.log(Level.INFO, e.toString());
			}
			
			Node assertion = requestBodyDoc.importNode(tokenVIHFDoc.getDocumentElement(), true); 
			
			Node security = null;
	        security = requestBodyDoc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
	        security.setPrefix("wsse");
	        security.appendChild(assertion);
	        
			requestBodyHeaderNode.appendChild(security);
			
			requestBodyDoc.getDocumentElement().normalize();
			
			String outRequest = convertXMLDocumentToString(requestBodyDoc);

			context.setProperty("requestContent", outRequest);
		}
	}
	
	public String convertXMLDocumentToString(Document requestBodyDoc) {

		DOMSource domSource = new DOMSource(requestBodyDoc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tf.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.transform(domSource, result);
		} catch (TransformerException e) {
			LOGGER.log(Level.INFO, e.toString());
		}

		return writer.toString();
	}

	public Document convertStringToXMLDocument(String xmlString) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
			return doc;
		} catch (Exception e) {
			LOGGER.log(Level.INFO, e.toString());
		}
		return null;
	}

}