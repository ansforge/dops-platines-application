/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.ror.converter.enumeration;
/**
 * 
 * @author aboittiaux
 *
 */
public enum ValueSetAttribut {
    
    /**
     * Balise OID.
     */
    OID("id"),
    /**
     * Balise Libellé.
     */
    FILE_TYPE("typeFichier"),
    /**
     * Balise Libellé adapté.
     */
    FILENAME("displayName"),
    /**
     * Balise Code.
     */
    DESCRIPTION("description"),
    /**
     * Balise Date Màj.
     */
    FILE_URL("urlFichier"),
    /**
     * Balise Date Valid.
     */
    DATE_VALID("dateValid"),
    /**
     * Balise Date fin.
     */
    DATE_FIN("dateFin"),
    /**
     * Balise Nom fichier.
     */
    DATE_MAJ("dateMaj");
    /**
     * Valeur de l'enum.
     */
    private String value;

    private ValueSetAttribut() {
    }

    
    private ValueSetAttribut(String valeur) {
        this.value = valeur;
    }

    public String getValue() {
        return value;
    }
    
    
    

}
