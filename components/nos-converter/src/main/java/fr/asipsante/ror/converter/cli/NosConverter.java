/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */

package fr.asipsante.ror.converter.cli;

import fr.asipsante.ror.converter.enumeration.NosStatus;
import fr.asipsante.ror.converter.impl.XMLConverter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

/**
 * Cli permettant d'exécuter la transformation des fichiers .tabs en .xml
 * en parsant les paramètres donnés en ligne de commande.
 * On récupère les informations passées en paramètres de la ligne de commande
 * via l'objet Option.
 * Les options sont prédéfinies dans la méthode static configParameters.
 * L'objet CommandLineParser permet de récupérer les paramètres de la ligne de commande
 * La méthode de conversion utilisée est ensuite dépendante des paramètres récupérés.
 * Et on récupère dans les logs le nombre:
 * - de fichiers transformés
 * - de fichiers invalides
 * - de fichiers manquants dans le zip.
 */
public class NosConverter {

    /**
     * Le Logger de log4j.
     */
    private static final Logger LOGGER = Logger.getLogger(XMLConverter.class);
    /**
     * Parametre de validation.
     */
    private static String validation;
    /**
     * Objet représentant la ligne de commande.
     */
    private static CommandLine line;
    private NosConverter() {
        
    }
    
    /**
     * Définition des options de la cli.
     * -s pour le source .zip (obligatoire)
     * -t pour le target folder (obligatoire)
     * -c pour le charset à utiliser (optionnel)
     * -v pour spécifier si la validation contre un fichier xsd
     * est voulue ou non (optionnel)
     * @return 
     */
    private static Options configParameters() {
        final Option source = new Option("s", "source", true, "Chemin du fichier zip");
        final Option target = new Option("t", "target", true, "Répertoire cicle");
        final Option charset = new Option("c", "charset", true, "Charset du zip source");
        final Option validation = new Option("v", "validation", true, "Option pour valider le fichier xml généré"
                + " contre un fichier xsd");
        final Options options = new Options();
        options.addOption(source);
        options.addOption(target);
        options.addOption(charset);
        options.addOption(validation);
        return options;
    }
    
    /**
     * Main permettant de récupérer les paramètres passés en ligne de commande.
     * @param args 
     */
    public static void main(String[] args) {
        final Options options = configParameters();
        Map<String, String> fichiers = new HashMap<>();
        
        if("-h".equals(args[0]) || "--help".equals(args[0])) {
            //Helper pour afficher les options disponibles pour la cli
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("help", options);
        } else {
            //Parser des paramètres passés en ligne de commande
            final CommandLineParser parser = new DefaultParser();
            try {
                line = parser.parse(options, args);
                //Récupération du zip mentionné dans la ligne de commande
                final String pathZipFile = line.getOptionValue("s");
                //Récupération du paramètre de validation dans la ligne de commande
                validation = line.getOptionValue("v");
                //Récupération du folder renseigné dans le ligne de commande
                final String folderPath = line.getOptionValue("t");
                final XMLConverter converter = new XMLConverter();
                if(line.getOptionValue('c') != null && validation != null) {
                    fichiers = converter.convert(new File(pathZipFile), new File(folderPath),
                            Charset.forName(line.getOptionValue('c')), checkValidation());
                } else if (line.getOptionValue('c') == null && validation != null) {
                    fichiers = converter.convert(new File(pathZipFile), new File(folderPath), checkValidation());
                } else if (line.getOptionValue('c') != null && validation == null) {
                    fichiers = converter.convert(new File(pathZipFile), new File(folderPath),
                            Charset.forName(line.getOptionValue('c')));
                } else {
                    fichiers = converter.convert(new File(pathZipFile), new File(folderPath));
                }
                
            } catch (ParseException ex) {
                LOGGER.fatal(ex);
            } catch (IOException ex) {
                LOGGER.fatal(ex);
            } catch (IllegalArgumentException ex) {
                LOGGER.fatal(ex);
            }
            response(fichiers);

        }
        
    }
    
    private static void response(Map<String, String> resultConversion) {
        //Compteur des fichiers transformés
        int nbFichiersTransform = 0;
        //Compteur des fichiers non transformés
        int nbFichiersNonTransform = 0;
        //Compteur des fichiers absent;
        int nbFichiersAbsent = 0;
        for(Map.Entry<String, String> entry : resultConversion.entrySet()) {
            if (entry.getValue().equals(NosStatus.TRANSFORM.getValue())) {
                nbFichiersTransform++;
            } else if (entry.getValue().equals(NosStatus.INVALID.getValue())) {
                nbFichiersNonTransform++;
                //Montre dans les logs le fichier non transformé
                LOGGER.info("Le fichier : " + entry.getKey() + " n'a pas été transformé");
            } else {
                nbFichiersAbsent++;
                //Montre dans les logs le fichier absent du zip
                LOGGER.info("Le fichier : " + entry.getKey() + " n'est pas présent dans le zip");
            }
        }
        LOGGER.info(nbFichiersTransform + "/" + resultConversion.size() + "fichiers transformés");
        LOGGER.info(nbFichiersNonTransform + "/" + resultConversion.size() + "fichiers non-transformés");
        LOGGER.info(nbFichiersAbsent + "/" + resultConversion.size() + "fichiers absents");
    }
    
    private static boolean checkValidation() {
        boolean validationParameter = true;
        if(validation != null && "false".equals(validation.toLowerCase())) {
            validationParameter = false;
        }
        return validationParameter;
    }
}
