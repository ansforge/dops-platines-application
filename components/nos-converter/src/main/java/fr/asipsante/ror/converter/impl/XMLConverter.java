/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */

package fr.asipsante.ror.converter.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import fr.asipsante.ror.converter.api.IXMLConverter;
import fr.asipsante.ror.converter.enumeration.JDVConceptAttribut;
import fr.asipsante.ror.converter.enumeration.NosConceptPosition;
import fr.asipsante.ror.converter.enumeration.NosHeader;
import fr.asipsante.ror.converter.enumeration.NosHeaderPosition;
import fr.asipsante.ror.converter.enumeration.NosStatus;
import fr.asipsante.ror.converter.enumeration.TreConceptAttribut;
import fr.asipsante.ror.converter.enumeration.ValueSetAttribut;


/**
 * Implémentation de l'interface IXMLConverter.
 * Cette classe permet de récupérer le fichier zip contenant les fichiers à transformer.
 * Le fichier de nomenclature PUB_NomenclatureTable.tabs est 
 * recherché dans le fichier.
 * Puis il est analysé afin de récupérer la liste des fichiers à transformer.
 * Il s'agit des fichiers de type TRE et JDV.
 * Puis pour chaque fichier une arborescence en fichier xml est créée
 * en récupérant les informations contenues dans le fichier.
 * L'arborescence est ensuite validée par le fichier nos.xsd
 * qui va valider la structure du fichier xml.
 * Le fichier est ensuite écrit à l'endroit souhaité si la validation est ok.
 */
public class XMLConverter implements IXMLConverter {

    /**
     * Les entêtes utiles au CSVParser pour récupérer les données dans le fichier de nomenclature.
     */
    private static final String[] HEADER_PUB = {
            NosHeader.OID.getValue(), NosHeader.TYPE_FICHIER.getValue(),
            NosHeader.NOM_FICHIER.getValue(), NosHeader.DESCRIPTION.getValue(),
            NosHeader.URL_FICHIER.getValue(), NosHeader.DATE_VALID.getValue(), NosHeader.DATE_FIN.getValue(),
            NosHeader.DATE_MAJ.getValue()};
    /**
     * Le Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(XMLConverter.class);
    /**
     * Séparateur par défaut.
     */
    private static final String SEPERATOR = ";";
    /**
     * Nombre d'attibuts dans une JDV.
     */
    private static final int NOMBRE_MIN_ATTRIBUTE_JDV = 3;
    /**
     * Nombre d'attributs dans une TRE.
     */
    private static final int NOMBRE_MIN_ATTRIBUTE_TRE = 8;
    /**
     * Fichier correspondant à la table des tables de nomenclature.
     */
    private static final String FILE_NOMENCLATURE = "PUB_NomenclatureTable.tabs";
    /**
     * Fichier de type TRE.
     */
    private static final String TYPE_FILE_TRE = "TRE";
    /**
     * Fichier de type JDV.
     */
    private static final String TYPE_FILE_JDV = "JDV";
    /**
     * Fichier de type ASS.
     */
    private static final String TYPE_FILE_ASS = "ASS";    
    /**
     * Chemin du fichier de validation xsd.
     */
    private static final String TRE_XSD = "xsd/tre.xsd";
    /**
     * Chemin du fichier de validation xsd.
     */
    private static final String JDV_XSD = "xsd/jdv.xsd";
    /**
     * Chemin du fichier de validation xsd.
     */
    private static final String ASS_XSD = "xsd/ass.xsd";
    /**
     * Nom de l'élément root du fichier xml.
     */
    private static final String NAME_ROOT_ELEMENT = "RetrieveValueSetResponse";
    /**
     * Nom de l'élément valueSet.
     */
    private static final String NAME_VALUESET_ELEMENT = "ValueSet";
    /**
     * Nom de l'élément conceptList.
     */
    private static final String NAME_CONCEPTLIST_ELEMENT = "ConceptList";
    /**
     * Nom de l'élément concept.
     */
    private static final String NAME_CONCEPT_ELEMENT = "Concept";
    /**
     * Extension du fichier initial.
     */
    private static final String EXTENSION_FILE_TABS = ".tabs";
    /**
     * Fichier du fichier final.
     */
    private static final String EXTENSION_FILE_XML = ".xml";
    /**
     * Elément concept.
     */
    private Element conceptXML = null;

    /**
     * Input représentant le fichier de nomenclature.
     */
    private InputStream inputNomenclature = null;
    /**
     * Factory pour construire l'objet DocumentBuilder.
     */
    private DocumentBuilderFactory domFactory = null;
    /**
     * Objet DocumentBuilder permettant de créer l'objet Document.
     */
    private DocumentBuilder domBuilder = null;
    /**
     * Liste des fichiers dans le zip.
     */
    private List<ZipEntry> listeZipEntry = new ArrayList<>();
    /**
     * Liste des noms des fichiers à transformer.
     */
    private List<String> filesName = new ArrayList<>();
    /**
     * Partie du document correspondant au resultSet.
     */
    private List<String> resultSet = new ArrayList<>();
    /**
     * Partie du document correspondant à la conceptList.
     */
    private List<String> conceptList = new ArrayList<>();
    /**
     * SchemaFactory.
     */
    private SchemaFactory schemaFactory = null;
    /**
     * Schema.
     */
    private Schema schema = null;
    /**
     * TabElement.
     */
    private Element tabElement = null;

    /**
     * La validateur.
     */
    private Validator validator = null;

    /**
     * Map représentant les fichiers avec un statut de transformation.
     */
    private Map<String, String> result = new HashMap<>();

    /**
     * Elément root du document xml.
     */
    private Element rootElement = null; 

    /**
     * Nom du fichier à transformer.
     */
    private String fileName = "";
    /**
     * Nom du repertoire où se trouvent les nomenclatures.
     */
    private String zipDirectoryName = "";
    /**
     * Document xml.
     */
    private  Document xmlDocument = null;
    /**
     * Construction du DocumentBuilder à l'instanciation de l'implémentation.
     */
    public XMLConverter() {
        try {
            domFactory = DocumentBuilderFactory.newInstance();
            domBuilder = domFactory.newDocumentBuilder();

        } catch (FactoryConfigurationError exp) {
            LOGGER.fatal(exp);
        } catch (ParserConfigurationException ex) {
            LOGGER.fatal(ex);
        } 
    }

    /**
     * Méthode permettant de transformer les fichier du zip en fichiers xml.
     * La méthode appelle la méthode convert en lui settant le charset par défaut et une validation xsd.
     * @param zipSource
     * @param targetFolder
     * @return Une map contenant le nom du fichier à transformer et l'état de sa transformation
     * @throws java.io.IOException
     */
    @Override
    public Map<String, String> convert(File zipSource, File targetFolder) throws IOException {
        return convert(zipSource, targetFolder, Charset.forName("ISO-8859-1"), true);
    }

    /**
     * Méthode permettant de transformer les fichier du zip en fichiers xml.
     * La méthode appelle la méthode convert en settant le charset par défaut mais en laissant l'utilisateur
     * valider les fichiers xml contre un fichier xsd ou non.
     * @param fileZip
     * @param folder
     * @param validation
     * @return Une map contenant le nom du fichier à transformer et l'état de sa transformation
     * @throws java.io.IOException
     */
    @Override
    public Map<String, String> convert(File fileZip, File folder, boolean validation) throws IOException {
        return convert(fileZip, folder, Charset.forName("ISO-8859-1"), validation);
    }

    /**
     * Méthode permettant de transformer les fichier du zip en fichiers xml.
     * La méthode appelle la méthode convert avec une validation des fichiers xml générés contre un fichier xsd 
     * (nos.xsd) mais en laissant l'utilisateur indiquer le charset correspondant à l'encodage de ses fichiers.
     * @param zipSource
     * @param targetFolder
     * @param charset 
     * @return Une map contenant le nom du fichier à transformer et l'état de sa transformation
     * @throws java.io.IOException
     */
    @Override
    public Map<String, String> convert(File zipSource, File targetFolder, Charset charset) throws IOException {
        return convert(zipSource, targetFolder, charset, true);
    }
    /**
     * Méthode permettant de transformer les fichier du zip en fichiers xml.
     * Et qui prend le charset en paramètre si celui-ci n'est pas de l'ISO-8859-1
     * Le fichier zip d'entrée doit être constitué d'un répertoire
     * dans lequel seront répertoriés tout les fichiers .tabs.
     * Le fichier de table PUB_NomenclatureTable.tabs doit aussi se trouver dans ce répertoire
     * afin de récupérer la liste des fichiers à transformer.
     * Ici tout les fichier de type TRE et JDV sont transformés au format xml et sont ensuite
     * écrits dans le répertoire choisi par l'utilisateur
     * @param fileZip
     * @param folder
     * @param charset 
     * @param validation
     * @return Une map contenant le nom du fichier à transformer et l'état de sa transformation
     * @throws java.io.IOException
     */
    @Override
    public Map<String, String> convert(File fileZip, File folder, Charset charset, boolean validation)
            throws IOException {
        //Récupération du fichier zip avec comme charset celui d'un poste windows
        final ZipFile zipFile = new ZipFile(fileZip, charset);
        getListZipEntry(zipFile, folder);
        //Définition du formatage du CSV
        final CSVFormat format = CSVFormat.newFormat(';').withHeader(HEADER_PUB);
        //Lecture du fichier de nomenclature et récupération des données
        final BufferedReader br = new BufferedReader(new InputStreamReader(
                inputNomenclature, charset));
        final CSVParser parser = new CSVParser(br, format);
        final List<CSVRecord> csvRecords = parser.getRecords();
        //Alimentation de la liste avec les noms des fichiers à transformer
        for (CSVRecord csvRecord : csvRecords) {
            final String fileType = csvRecord.get(NosHeader.TYPE_FICHIER.getValue());
            if (fileType.equals(TYPE_FILE_TRE) || fileType.equals(TYPE_FILE_JDV) || fileType.equals(TYPE_FILE_ASS)) {
                final String file = csvRecord.get(NosHeader.NOM_FICHIER.getValue());
                filesName.add(file);
            }
        }
        //Appel à la méthode de transformation pour tout les fichiers
        
        for (int i = 0; i < listeZipEntry.size(); i++) {
            final ZipEntry entry2 = listeZipEntry.get(i);
            for (int j = 0; j < filesName.size(); j++) {
                //Récupération du nom du fichier
                fileName = filesName.get(j);
                if (entry2.getName().equals(zipDirectoryName + fileName)) {
                    //Récupération du flux à transformer
                    final InputStream input2 = zipFile.getInputStream(entry2);
                    
                    final String xmlPath = folder.getAbsolutePath() + File.separator +
                            fileName.replace(EXTENSION_FILE_TABS, "") + EXTENSION_FILE_XML;
                    generateXmlFile(input2, xmlPath, validation);
                    fileName ="";
                }
            }
        }
        for(int x=0; x<filesName.size(); x++){
            if(!result.containsKey(filesName.get(x))) {
                result.put(filesName.get(x), NosStatus.NULLPOINTER.getValue());
            }
        }

        return result;
    }

    private void defineResultSetAndConceptList(InputStream fileToTransform) throws IOException {

        //Lecture de la première ligne du fichier pour récupérer les entêtes
        final BufferedReader br = new BufferedReader(new InputStreamReader(fileToTransform));
        final String line1 = br.readLine();
        resultSet.add(line1);
        //Lecture du fichier jusqu'au premier "<"
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            if (line.startsWith("<")) {
                conceptList.add(line);
                break;
            }
            resultSet.add(line);
        }
        //Lecture de la fin du fichier
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            conceptList.add(line);
        }
    }

    private List<ZipEntry> getListZipEntry(ZipFile zipFile, File target) throws IOException {
        //Récupération des entrées du fichier zip
        final Enumeration<? extends ZipEntry> ent = zipFile.entries();
        //Alimentation de la liste de zip entries
        while (ent.hasMoreElements()) {
            final ZipEntry entry = ent.nextElement();
            //Récupération du répertoire où sont stockés les fichiers de nomenclature.
            if (entry.isDirectory()) {
                zipDirectoryName = entry.getName();
            }
            if (entry.getName().matches(zipDirectoryName + FILE_NOMENCLATURE)) {
                //Récupération du fichier de nomenclature
                inputNomenclature = zipFile.getInputStream(entry);
            }
            
            if(entry.getName().endsWith("tabs")) {
            	File tabFile = new File(entry.getName());
            	FileOutputStream fos = new FileOutputStream(new File(target.getAbsolutePath() + File.separator + tabFile.getName()));
            	byte[] buffer = new byte[1024];
    			int len;
    			InputStream in = zipFile.getInputStream(entry);
    			while ((len = in.read(buffer)) > 0) {
    				fos.write(buffer, 0, len);
    			}
    			in.close();
    			fos.close();
            }

            
            listeZipEntry.add(entry);
        }
        return listeZipEntry;
    }

    private void generateXmlFile(InputStream fileToTransform, String out, boolean validation)
            throws IOException {
        xmlDocument = domBuilder.newDocument();
        //Document présentant le structure du fichier xml
        defineResultSetAndConceptList(fileToTransform);
        //Création de l'élément root du document xml
        rootElement = xmlDocument.createElement(NAME_ROOT_ELEMENT);
        xmlDocument.appendChild(rootElement);
        //Création de l'élément valueSet du document
        final Element valueSetElement = xmlDocument.createElement(NAME_VALUESET_ELEMENT);
        rootElement.appendChild(valueSetElement);
        //Création de l'élément conceptList du document
        final Element conceptListElement = xmlDocument.createElement(NAME_CONCEPTLIST_ELEMENT);
        valueSetElement.appendChild(conceptListElement);
        //Tableau représentant les valeurs du resultSet
        final String[] valeursValueSet = resultSet.get(1).split(SEPERATOR, -1);
        //Alimentation des attributs du valueSet
        valueSetElement.setAttribute(ValueSetAttribut.OID.getValue(),
                valeursValueSet[NosHeaderPosition.OID_POSITION.getPosition()]);
        valueSetElement.setAttribute(ValueSetAttribut.FILE_TYPE.getValue(),
                valeursValueSet[NosHeaderPosition.FILE_TYPE.getPosition()]);
        valueSetElement.setAttribute(ValueSetAttribut.FILENAME.getValue(),
                valeursValueSet[NosHeaderPosition.FILE_NAME.getPosition()]);
        valueSetElement.setAttribute(ValueSetAttribut.DESCRIPTION.getValue(),
                valeursValueSet[NosHeaderPosition.DESCRIPTION.getPosition()]);
        valueSetElement.setAttribute(ValueSetAttribut.FILE_URL.getValue(),
                valeursValueSet[NosHeaderPosition.FILE_URL.getPosition()]);
        valueSetElement.setAttribute(ValueSetAttribut.DATE_VALID.getValue(),
                valeursValueSet[NosHeaderPosition.VALID_DATE.getPosition()]);
        valueSetElement.setAttribute(ValueSetAttribut.DATE_FIN.getValue(),
                valeursValueSet[NosHeaderPosition.END_DATE.getPosition()]);
        valueSetElement.setAttribute(ValueSetAttribut.DATE_MAJ.getValue(),
                valeursValueSet[NosHeaderPosition.MAJ_DATE.getPosition()]);

        for (int i = 1; i < conceptList.size(); i++) {
            //Création de l'élément concept
            conceptXML = xmlDocument.createElement(NAME_CONCEPT_ELEMENT);
            conceptListElement.appendChild(conceptXML);
            final String[] conceptAttribut = conceptList.get(i).split(";", -1);
            if(valueSetElement.getAttribute(ValueSetAttribut.FILE_TYPE.getValue()).equals(TYPE_FILE_JDV)
                    && conceptAttribut.length == NOMBRE_MIN_ATTRIBUTE_JDV) {
                conceptXML.setAttribute(JDVConceptAttribut.OID.getValue(),
                        conceptAttribut[NosConceptPosition.OID_POSITION.getValue()]);
                conceptXML.setAttribute(JDVConceptAttribut.CODE.getValue(),
                        conceptAttribut[NosConceptPosition.CODE.getValue()]);
                conceptXML.setAttribute(JDVConceptAttribut.LIBELLE.getValue(),
                        conceptAttribut[NosConceptPosition.LIBELLE.getValue()]);
                
            } else if (valueSetElement.getAttribute(ValueSetAttribut.FILE_TYPE.getValue()).equals(TYPE_FILE_TRE)
                    && conceptAttribut.length == NOMBRE_MIN_ATTRIBUTE_TRE) {
                conceptXML.setAttribute(TreConceptAttribut.OID.getValue(),
                        conceptAttribut[NosConceptPosition.OID_POSITION.getValue()]);
                conceptXML.setAttribute(TreConceptAttribut.CODE.getValue(),
                        conceptAttribut[NosConceptPosition.CODE.getValue()]);
                conceptXML.setAttribute(TreConceptAttribut.LIBELLE_ADAPTE.getValue(),
                        conceptAttribut[NosConceptPosition.LIBELLE.getValue()]);
                conceptXML.setAttribute(TreConceptAttribut.LIBELLE_COURT.getValue(),
                        conceptAttribut[NosConceptPosition.LBELLE_COURT.getValue()]);
                conceptXML.setAttribute(TreConceptAttribut.LIBELLE_LONG.getValue(),
                        conceptAttribut[NosConceptPosition.LIBELLE_LONG.getValue()]);
                conceptXML.setAttribute(TreConceptAttribut.DATE_VALID.getValue(),
                        conceptAttribut[NosConceptPosition.VALID_DATE.getValue()]);
                conceptXML.setAttribute(TreConceptAttribut.DATE_FIN.getValue(),
                        conceptAttribut[NosConceptPosition.END_DATE.getValue()]);
                conceptXML.setAttribute(TreConceptAttribut.DATE_MAJ.getValue(),
                        conceptAttribut[NosConceptPosition.MAJ_DATE.getValue()]);
            } else if (valueSetElement.getAttribute(ValueSetAttribut.FILE_TYPE.getValue()).equals(TYPE_FILE_ASS)) {
                xmlDocument.renameNode(conceptListElement, conceptListElement.getNamespaceURI(), "MappedConceptList");
                xmlDocument.renameNode(conceptXML, conceptXML.getNamespaceURI(), "MappedConcept");
                traitementASS(conceptAttribut);
            }
        }

        try {
            schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Source source = null;
            StreamSource streamSource = null;
            // FileInputStream fos = null;
            if(valueSetElement.getAttribute(ValueSetAttribut.FILE_TYPE.getValue()).equals(TYPE_FILE_JDV)) {
            	streamSource = new StreamSource(
                        Thread.currentThread().getContextClassLoader().getResourceAsStream(JDV_XSD));
                source = streamSource;
            } else if (valueSetElement.getAttribute(ValueSetAttribut.FILE_TYPE.getValue()).equals(TYPE_FILE_TRE)) {
            	streamSource = new StreamSource(
                        Thread.currentThread().getContextClassLoader().getResourceAsStream(TRE_XSD));
                source = streamSource;
            } else {
            	streamSource = new StreamSource(
                        Thread.currentThread().getContextClassLoader().getResourceAsStream(ASS_XSD));
                source = streamSource;
            }
            
            schema = schemaFactory.newSchema(source);
            //schema = factory.newSchema(new File(XMLConverter.class.getClassLoader().getResource(FILE_XSD).toURI()));
            validator = schema.newValidator();
            final DOMSource src = new DOMSource(xmlDocument);
            //Appel à la méthode de validation
            validate(src, out, validation);
            streamSource.getInputStream().close();
            
            result.put(fileName, NosStatus.TRANSFORM.getValue());
        } catch (TransformerException ex) {
            LOGGER.fatal(ex + " le fichier " + fileName +" n'est pas valide");
        } catch (SAXException ex) {
            LOGGER.fatal(ex + " le fichier " + fileName +" n'est pas valide");
            result.put(fileName, NosStatus.INVALID.getValue());
        } 
        xmlDocument.setXmlStandalone(true);
        resultSet = new ArrayList<>();
        conceptList = new ArrayList<>();

    }

    private void traitementASS(String[] data) {

        final String header = conceptList.get(0);
        final String[] tab = header.split(";", -1);
        String n = null;
        for(int x=0; x<tab.length; x++){
            n = tab[x];
            n = n.replaceAll("<", "");
            n = n.replaceAll(">", "");
            tab[x] = n;
        }
        int i = 0;
        for (String string : data) {
            if (!string.isEmpty()) {
                tabElement = xmlDocument.createElement(NAME_CONCEPT_ELEMENT);
                conceptXML.appendChild(tabElement);
                tabElement.setAttribute("code", string);
                tabElement.setAttribute("codeSystem", tab[i]);
            }
            
            i++;
        }
    }


    private void validate(DOMSource docSource, String out, boolean validation) 
            throws TransformerException, SAXException, IOException {

        //Validation du fichier xml
        if(validation) {
            validator.validate(docSource);
        }
        //Alimentation des attributs de l'élément root
        rootElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        rootElement.setAttribute("xsi:schemaLocation", "urn:ihe:iti:svs:2008 SVS.xsd");
        rootElement.setAttribute("xmlns", "urn:ihe:iti:svs:2008");
        //Transformation en xml
        final TransformerFactory tranFactory = TransformerFactory.newInstance();
        final Transformer aTransformer;
        aTransformer = tranFactory.newTransformer();
        //Paramétrage de l'indentation
        aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
        aTransformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
        aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        //Ecriture du fichier à l'endroit souhaité
        final FileOutputStream fos = new FileOutputStream(out);
        
        final StreamResult xmlFile = new StreamResult(fos);
        aTransformer.transform(docSource, xmlFile);
        xmlFile.getOutputStream().close();
    }



}
