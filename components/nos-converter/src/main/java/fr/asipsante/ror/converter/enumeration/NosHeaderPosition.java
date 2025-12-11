/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.ror.converter.enumeration;

/**
 *
 * @author aboittiaux
 */
public enum NosHeaderPosition {
    /**
     * OID position.
     */
    OID_POSITION(0),
    /**
     * FILE_TYPE position.
     */
    FILE_TYPE(1),
    /**
     * FILE_NAME position.
     */
    FILE_NAME(2),
    /**
     * DESCRIPTION position.
     */
    DESCRIPTION(3),
    /**
     * FILE_URL position.
     */
    FILE_URL(4),
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
     * Valeur de l'enum.
     */
    private int position;

    private NosHeaderPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
    
    
    
}
