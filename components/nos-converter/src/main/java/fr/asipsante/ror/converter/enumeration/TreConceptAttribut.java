/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.ror.converter.enumeration;
/**
 * 
 * @author aboittiaux
 *
 */
public enum TreConceptAttribut {

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
    LIBELLE_ADAPTE("displayName"),
    /**
     * Balise Code.
     */
    LIBELLE_COURT("shortDesignation"),
    /**
     * Balise Date Màj.
     */
    LIBELLE_LONG("longDesignation"),
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

    private TreConceptAttribut() {
    }

    
    private TreConceptAttribut(String valeur) {
        this.value = valeur;
    }

    public String getValue() {
        return value;
    }
    
}
