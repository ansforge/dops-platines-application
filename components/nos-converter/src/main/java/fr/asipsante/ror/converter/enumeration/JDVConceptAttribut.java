/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.ror.converter.enumeration;
/**
 * Enum représentant les attributs de l'élément Concept pour une jdv.
 * @author aboittiaux
 *
 */
public enum JDVConceptAttribut {

    /**
     * Balise OID.
     */
    OID("codeSystem"),
    /**
     * Balise Libellé.
     */
    CODE("code"),
    /**
     * Balise Libellé adapté.
     */
    LIBELLE("displayName");
    /**
     * Valeur de l'énum.
     */
    private String value;

    /**
     * @param value
     */
    private JDVConceptAttribut(String value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
    
    
}
