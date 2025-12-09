/*
 * (c) Copyright 2017-2024, ANS. All rights reserved.
 */
package fr.asipsante.platines.entity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.model.Sid;

/**
 * @author aboittiaux
 */
@Entity
@Table(name = "donnees_fonctionnelles")
public class FunctionalData extends AbstractEntity implements Serializable {

  /** uuid. */
  private static final long serialVersionUID = 1L;

  /** Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(FunctionalData.class);

  /** properties. */
  @Transient private Properties props;

  /** xml properties. */
  @Lob
  @Basic(fetch = FetchType.LAZY)
  @Column(name = "xmlProp")
  private byte[] xmlProp;

  /** Default constructor. */
  public FunctionalData() {
    super();
  }

  /**
   * @return the props
   */
  public Properties getProps() {

    props = new Properties();
    try {
      if (xmlProp != null) {
        props.loadFromXML(new ByteArrayInputStream(xmlProp));
      }
    } catch (final IOException e) {
      LOGGER.error("Erreur lors du chargement des données fonctionnelles", e);
    }

    return props;
  }

  /**
   * @param props the props to set
   */
  public void setProps(Properties props) {
    this.props = props;
  }

  /**
   * @return the xmlProp
   */
  public byte[] getXmlProp() {
    return xmlProp;
  }

  /**
   * @param xmlProp the xmlProp to set
   */
  public void setXmlProp(byte[] xmlProp) {
    this.xmlProp = xmlProp;
  }

  @Override
  public Sid getOwner() {
    return null;
  }
}
