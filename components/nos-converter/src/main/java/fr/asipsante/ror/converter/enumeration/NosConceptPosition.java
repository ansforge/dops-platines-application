/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.ror.converter.enumeration;
/**
 * Position des éléments concept.
 * @author aboittiaux
 *
 */
public enum NosConceptPosition {

    /**
     * OID position.
     */
    OID_POSITION(0),
    /**
     * CODE position.
     */
    CODE(1),
    /**
     * LIBELLE position.
     */
    LIBELLE(2),
    /**
     * LBELLE_COURT position.
     */
    LBELLE_COURT(3),
    /**
     * LIBELLE_LONG position.
     */
    LIBELLE_LONG(4),
    /**
     * VALID_DATE position.
     */
    VALID_DATE(5),
    /**
     * END_DATE position.
     */
    END_DATE(6),
    /**
     * MAJ_DATE position.
     */
    MAJ_DATE(7);
    /**
     * Valeur de l'énum.
     */
    private int value;

    /**
     * @param value
     */
    private NosConceptPosition(int value) {
        this.value = value;
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }
    
    
    
    
}
