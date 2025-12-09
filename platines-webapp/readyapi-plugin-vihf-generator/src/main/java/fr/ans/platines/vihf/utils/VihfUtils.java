/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.ans.platines.vihf.utils;

import fr.ans.platines.vihf.VihfBuilder;
import fr.ans.platines.vihf.exception.GenericVihfException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class VihfUtils {

  /** Logger definition. */
  private static final transient Logger logger =
      LoggerFactory.getLogger(VihfBuilder.class.getName());

  public static String toUtf8(String msg) {
    byte ptext[] = msg.getBytes();
    String value = "";
    try {
      value = new String(ptext, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      logger.info("Error when encoding string : {}", msg, e);
    }
    return value;
  }

  public static Document getDocumentFromString(String xmlString) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    try {
      builder = factory.newDocumentBuilder();
      Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
      return doc;
    } catch (Exception e) {
      logger.error("Error when converting string : {}", xmlString, e);
    }
    return null;
  }

  public static String getXMLStringFromDocument(Document doc) throws GenericVihfException {
    try {
      DOMSource domSource = new DOMSource(doc);
      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = tf.newTransformer();
      transformer.transform(domSource, result);
      return writer.toString();
    } catch (TransformerException e) {
      logger.error("Could not convert XML tree to String", e);
      throw new GenericVihfException(e.getMessage(), e.getCause());
    }
  }
}
