/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */

package fr.asipsante.ror.converter.enumeration;

/**
 * Classe d'énumération représentant les balises retrouvées dans les fichiers tabs.
 */
public enum NosHeader {
    /**
     * Balise OID.
     */
    OID("<OID>"),
    /**
     * Balise Libellé.
     */
    LIBELLE("<Libellé>"),
    /**
     * Balise Libellé adapté.
     */
    LIBELLE_ADAPTE("<Libellé adapté>"),
    /**
     * Balise Code.
     */
    CODE("<Code>"),
    /**
     * Balise Date Màj.
     */
    DATE_MAJ("<Date MàJ>"),
    /**
     * Balise Date Valid.
     */
    DATE_VALID("<Date Valid>"),
    /**
     * Balise Date fin.
     */
    DATE_FIN("<Date fin>"),
    /**
     * Balise Nom fichier.
     */
    NOM_FICHIER("<Nom fichier>"),
    /**
     * Balise Type fichier.
     */
    TYPE_FICHIER("<Type fichier>"),
    /**
     * Balise Type fichier.
     */
    URL_FICHIER("<URL fichier>"),
    /**
     * Balise Description.
     */
    DESCRIPTION("<Description>");
    /**
     * Valeur de l'enum.
     */
    private String value;

    private NosHeader() {
    }

    
    private NosHeader(String valeur) {
        this.value = valeur;
    }

    public String getValue() {
        return value;
    }
    
    
    
}
