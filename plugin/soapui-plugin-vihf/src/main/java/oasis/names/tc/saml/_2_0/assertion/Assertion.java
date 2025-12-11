//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.0 
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2019.04.24 à 02:26:39 PM CEST 
//

package oasis.names.tc.saml._2_0.assertion;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Classe Java pour anonymous complex type.
 * 
 * <p>
 * Le fragment de schéma suivant indique le contenu attendu figurant dans cette
 * classe.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}Issuer"/&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}Subject"/&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}AuthnStatement"/&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}AttributeStatement"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Version" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal"&gt;
 *             &lt;enumeration value="2.0"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="IssueInstant" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="YYYY-MM-ddTHH:MM:ssZ"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="ID" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="ABCS"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "issuer", "subject", "authnStatement", "attributeStatement" })
@XmlRootElement(name = "Assertion")
public class Assertion {

	@XmlElement(name = "Issuer", required = true)
	protected Issuer issuer;
	@XmlElement(name = "Subject", required = true)
	protected Subject subject;
	@XmlElement(name = "AuthnStatement", required = true)
	protected AuthnStatement authnStatement;
	@XmlElement(name = "AttributeStatement", required = true)
	protected AttributeStatement attributeStatement;
	@XmlAttribute(name = "ID", required = true)
	protected String id;
	@XmlAttribute(name = "IssueInstant", required = true)
	protected String issueInstant;
	@XmlAttribute(name = "Version", required = true)
	protected String version;

	/**
	 * Obtient la valeur de la propriété issuer.
	 * 
	 * @return possible object is {@link Issuer }
	 * 
	 */
	public Issuer getIssuer() {
		return issuer;
	}

	/**
	 * Définit la valeur de la propriété issuer.
	 * 
	 * @param value allowed object is {@link Issuer }
	 * 
	 */
	public void setIssuer(Issuer value) {
		this.issuer = value;
	}

	/**
	 * Obtient la valeur de la propriété subject.
	 * 
	 * @return possible object is {@link Subject }
	 * 
	 */
	public Subject getSubject() {
		return subject;
	}

	/**
	 * Définit la valeur de la propriété subject.
	 * 
	 * @param value allowed object is {@link Subject }
	 * 
	 */
	public void setSubject(Subject value) {
		this.subject = value;
	}

	/**
	 * Obtient la valeur de la propriété authnStatement.
	 * 
	 * @return possible object is {@link AuthnStatement }
	 * 
	 */
	public AuthnStatement getAuthnStatement() {
		return authnStatement;
	}

	/**
	 * Définit la valeur de la propriété authnStatement.
	 * 
	 * @param value allowed object is {@link AuthnStatement }
	 * 
	 */
	public void setAuthnStatement(AuthnStatement value) {
		this.authnStatement = value;
	}

	/**
	 * Obtient la valeur de la propriété attributeStatement.
	 * 
	 * @return possible object is {@link AttributeStatement }
	 * 
	 */
	public AttributeStatement getAttributeStatement() {
		return attributeStatement;
	}

	/**
	 * Définit la valeur de la propriété attributeStatement.
	 * 
	 * @param value allowed object is {@link AttributeStatement }
	 * 
	 */
	public void setAttributeStatement(AttributeStatement value) {
		this.attributeStatement = value;
	}

	/**
	 * Obtient la valeur de la propriété version.
	 * 
	 * @return possible object is {@link BigDecimal }
	 * 
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Définit la valeur de la propriété version.
	 * 
	 * @param value allowed object is {@link BigDecimal }
	 * 
	 */
	public void setVersion(String value) {
		this.version = value;
	}

	/**
	 * Obtient la valeur de la propriété issueInstant.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getIssueInstant() {
		return issueInstant;
	}

	/**
	 * Définit la valeur de la propriété issueInstant.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setIssueInstant(String value) {
		this.issueInstant = value;
	}

	/**
	 * Obtient la valeur de la propriété id.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getID() {
		return id;
	}

	/**
	 * Définit la valeur de la propriété id.
	 * 
	 * @param value allowed object is {@link String }
	 * 
	 */
	public void setID(String value) {
		this.id = value;
	}

}
