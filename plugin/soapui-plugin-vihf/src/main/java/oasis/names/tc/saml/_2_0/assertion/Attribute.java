//
// Ce fichier a été généré par l'implémentation de référence JavaTM Architecture for XML Binding (JAXB), v2.3.0 
// Voir <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Toute modification apportée à ce fichier sera perdue lors de la recompilation du schéma source. 
// Généré le : 2019.04.24 à 02:26:39 PM CEST 
//


package oasis.names.tc.saml._2_0.assertion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java pour anonymous complex type.
 * 
 * <p>Le fragment de schéma suivant indique le contenu attendu figurant dans cette classe.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{urn:oasis:names:tc:SAML:2.0:assertion}AttributeValue"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="Name" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="Profil_Utilisateur"/&gt;
 *             &lt;enumeration value="Ressource_URN"/&gt;
 *             &lt;enumeration value="VIHF_Profil"/&gt;
 *             &lt;enumeration value="VIHF_Version"/&gt;
 *             &lt;enumeration value="urn:oasis:names:tc:xacml:2.0:subject:role"/&gt;
 *             &lt;enumeration value="urn:oasis:names:tc:xspa:1.0:subject:subject-id"/&gt;
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
@XmlType(name = "", propOrder = {
    "attributeValue"
})
@XmlRootElement(name = "Attribute")
public class Attribute {

    @XmlElement(name = "AttributeValue", required = true)
    protected AttributeValue attributeValue;
    @XmlAttribute(name = "Name", required = true)
    protected String name;

    /**
     * Obtient la valeur de la propriété attributeValue.
     * 
     * @return
     *     possible object is
     *     {@link AttributeValue }
     *     
     */
    public AttributeValue getAttributeValue() {
        return attributeValue;
    }

    /**
     * Définit la valeur de la propriété attributeValue.
     * 
     * @param value
     *     allowed object is
     *     {@link AttributeValue }
     *     
     */
    public void setAttributeValue(AttributeValue value) {
        this.attributeValue = value;
    }

    /**
     * Obtient la valeur de la propriété name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Définit la valeur de la propriété name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
