/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.ror.converter.enumeration;

/**
 * Enumération permettant d'avoir un aperçu sur la transformation des fichiers.
 * @author aboittiaux
 */
public enum NosStatus {
    /**
     * Statut du fichier quand il a été transformé avec succès.
     */
    TRANSFORM("Converti"),
    /**
     * Statut du fichier quand il n'a pas été transformé avec succès.
     */
    INVALID("Invalide"),
    /**
     * Statut du fichier quand il n'a pas été trouvé dans le fichier zip.
     */
    NULLPOINTER("Manquant");
    
    /**
     * Valeur de l'enum.
     */
    private String value;

    private NosStatus() {
    }

    
    private NosStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    
}
