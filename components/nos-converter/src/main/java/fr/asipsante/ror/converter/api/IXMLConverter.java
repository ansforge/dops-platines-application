/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */

package fr.asipsante.ror.converter.api;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Interface de conversion de fichiers .tab en .xml.
 * Cette interface possède 4 méthodes pour la conversion de fichiers .tabs contenus dans un zipFile.
 * Les différentes méthodes utilisent une fichier zip contenant un dossier dans lequel se trouvent
 * les fichiers de nomenclatures au format .tabs ainsi que le fichier de la table des nomenclatures.
 * Par défaut l'encodage du fichier zip et des fichiers qu'il contient est celui du système windows : ISO-8859-1
 * Cependant il est possible de préciser l'encodage des différents fichiers en précisant le charset a utiliser.
 * De plus, les fichiers xml générés sont validés contre un fichier nos.xsd se trouvant dans le dossier xsd. 
 * Toutefois, il est possible de ne pas effectuer cette validation en passant
 * le boolean "false" à la méthode de conversion.
 * Une implémentation possible de ces différentes méthodes se trouve dans la classe
 * @see fr.asipsante.ror.converter.impl.XMLConverter
 */
public interface IXMLConverter {
    
    /**
     * Méthode de conversion de fichiers .tabs en .xml.
     * La méthode prend en argument un fichier zip ainsi
     * que le dossier dans lequel seront écrits les fichiers au format xml
     * La méthode utilisera le carset "ISO-8859-1" par défaut pour lire les fichiers.tabs et restituera les fichiers 
     * xml en UTF-8.
     * Par défaut la méthode validera le xml généré avec le fichier xsd, nos.xsd contenu dans la lib.
     * @param zipSource
     * @param targetFolder
     * @return Une map contenant le nom du fichier à transformer et l'état de sa transformation
     * @throws IOException
     * @see fr.asipsante.ror.converter.impl.XMLConverter#convert(java.io.File, java.io.File) 
     */
    Map<String, String> convert(File zipSource, File targetFolder) throws IOException;
    /**
     * Méthode de conversion de fichiers .tabs en .xml.
     * La méthode prend en argument un fichier zip ainsi
     * que le dossier dans lequel seront écrits les fichiers au format xml.
     * La méthode utilisera le carset "ISO-8859-1" par défaut pour lire les fichiers.tabs et restituera les fichiers 
     * xml en UTF-8.
     * Si le document doit être validé contre un fichier xsd avant validation
     * le paramètre validation doit être passé à true
     * @param zipSource
     * @param targetFolder
     * @param validation
     * @return Une map contenant le nom du fichier à transformer et l'état de sa transformation
     * @throws IOException
     * @see fr.asipsante.ror.converter.impl.XMLConverter#convert(java.io.File, java.io.File, boolean) 
     */
    Map<String, String> convert(File zipSource, File targetFolder, boolean validation) throws IOException;
        
    /**
     * Méthode de conversion de fichiers .tabs en .xml.
     * La méthode prend en argument un fichier zip ainsi
     * que le dossier dans lequel seront écrits les fichiers au format xml
     * La méthode utilisera le carset "ISO-8859-1" par défaut pour lire les fichiers.tabs et restituera les fichiers 
     * xml en UTF-8.
     * @param zipSource
     * @param targetFolder
     * @param charset
     * @return Une map contenant le nom du fichier à transformer et l'état de sa transformation
     * @throws IOException
     * @see fr.asipsante.ror.converter.impl.XMLConverter#convert(java.io.File, java.io.File, java.nio.charset.Charset) 
     */
    Map<String, String> convert(File zipSource, File targetFolder, Charset charset) throws IOException;
    
    /**
     * Méthode de conversion de fichiers .tabs en .xml.
     * La méthode prend en argument un fichier zip ainsi
     * que le dossier dans lequel seront écrits les fichiers au format xml
     * ainsi que le charset dans lequel sont écrits les fichiers, si ce n'est pas de l'ISO-8859-1
     * Si le document doit être validé contre un fichier xsd avant validation
     * le paramètre validation doit être passé à true
     * @param zipSource
     * @param targetFolder
     * @param charset
     * @param validation
     * @return Une map contenant le nom du fichier à transformer et l'état de sa transformation
     * @throws IOException
     * @see fr.asipsante.ror.converter.impl.XMLConverter#convert
     * (java.io.File, java.io.File, java.nio.charset.Charset, boolean) 
     */
    Map<String, String> convert(File zipSource, File targetFolder, Charset charset, boolean validation)
            throws IOException;
}
