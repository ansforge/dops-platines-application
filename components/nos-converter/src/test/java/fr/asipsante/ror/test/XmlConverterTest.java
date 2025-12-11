/*
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.ror.test;

import fr.asipsante.ror.converter.api.IXMLConverter;
import fr.asipsante.ror.converter.enumeration.NosStatus;
import fr.asipsante.ror.converter.impl.XMLConverter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author aboittiaux
 */
public class XmlConverterTest {
    private final IXMLConverter converter;
    private final File zipSourceNominal;
    private final File zipSourceFichierManquant;
    private final File zipSourceValidationFail;
    private final File targetFolder;
    private Map<String, String> resultConverter;
    private int nbTransformFile = 0;
    private int nbInvalidFile = 0;
    private int nbAbsentFile = 0;
    
    
    public XmlConverterTest() {
        converter = new XMLConverter();
        zipSourceNominal = new File(ClassLoader.getSystemResource("zip/Nominal.zip").getFile());
        zipSourceFichierManquant = new File(ClassLoader.getSystemResource("zip/FichierManquant.zip").getFile());
        zipSourceValidationFail = new File(ClassLoader.getSystemResource("zip/ValidationFail.zip").getFile());
        targetFolder = new File(ClassLoader.getSystemResource("zip/").getFile());
        resultConverter = new HashMap<>();
    }
    
    /**
     * Vérification que dans le cas nominal.
     * La méthode retourne une map de la taille voulue
     * Avec tout les fichiers transformés
     * Et que les valeur des clés de la map soit toute à transformées
     * @throws IOException 
     */
    @Test
    public void testNominal() throws IOException {
        resultConverter = converter.convert(zipSourceNominal, targetFolder);
        Assert.assertEquals(
                "Vérification que la map possède bien le nombre de fichiers attendus",
                16,
                resultConverter.size());
        for (Map.Entry<String, String> entry : resultConverter.entrySet()) {
            if(entry.getValue().equals(NosStatus.TRANSFORM.getValue())) {
                File fileXml = new File(ClassLoader.getSystemResource("zip/").getFile() + 
                        entry.getKey().replace(".tabs", ".xml"));
                Assert.assertTrue(
                        "Vérification que le fichier existe bien quand il a été transformé",
                        fileXml.exists());
            }
            
        }
        countFile();
        Assert.assertEquals(
                "Vérification que chaque fichier à bien le statut transformé",
                16,
                nbTransformFile);
      
        deleteFile();
    }
    
    @Test
    public void testFichierManquant() throws IOException {
        resultConverter = converter.convert(zipSourceFichierManquant, targetFolder);
        Assert.assertEquals(
                "Vérification que la map possède bien le nombre de fichiers attendus",
                13,
                resultConverter.size());
        for (Map.Entry<String, String> entry : resultConverter.entrySet()) {
            if(entry.getValue().equals(NosStatus.TRANSFORM.getValue())) {
                File fileXml = new File(ClassLoader.getSystemResource("zip/").getFile() + 
                        entry.getKey().replace(".tabs", ".xml"));
                Assert.assertTrue(
                        "Vérification que le fichier existe bien quand il a été transformé",
                        fileXml.exists());
            }
            
        }
        countFile();
        Assert.assertEquals("Vérification qu'il y a bien 11 fichiers transformés dans la map",
                12, nbTransformFile);
        Assert.assertEquals("Vérification qu'il y a bien 1 fichiers absent dans la map",
                1, nbAbsentFile);
        
        deleteFile();
    }
    
    @Test
    public void testFichierInvalid() throws IOException {
        resultConverter = converter.convert(zipSourceValidationFail, targetFolder);
        Assert.assertEquals(
                "Vérification que la map possède bien le nombre de fichiers attendus",
                16,
                resultConverter.size());
        for (Map.Entry<String, String> entry : resultConverter.entrySet()) {
            if(entry.getValue().equals(NosStatus.TRANSFORM.getValue())) {
                File fileXml = new File(ClassLoader.getSystemResource("zip/").getFile() +
                        entry.getKey().replace(".tabs", ".xml"));
                Assert.assertTrue(
                        "Vérification que le fichier existe bien quand il a été transformé",
                        fileXml.exists());
            }
            
        }
        countFile();
        Assert.assertEquals("Vérification qu'il y a bien 16 fichiers transformés dans la map",
                15, nbTransformFile);
        Assert.assertEquals("Vérification qu'il y a bien 0 fichiers absent dans la map",
                0, nbAbsentFile);
        Assert.assertEquals("Vérification qu'il y a bien 1 fichier invalide dans la map",
                1, nbInvalidFile);
        deleteFile();
    }
    
    private void deleteFile() {
        for(Map.Entry<String, String> entry : resultConverter.entrySet()) {
            String fileName = entry.getKey().replace(".tabs", ".xml");
            File f = new File(targetFolder + File.separator + fileName);
            f.delete();
            File tabsFIle = new File(targetFolder + File.separator + entry.getKey());
            tabsFIle.delete();
        }
    }
    
    private void countFile() {
        for(Map.Entry<String, String> entry : resultConverter.entrySet()) {
            if(entry.getValue().equals(NosStatus.TRANSFORM.getValue())) {
                nbTransformFile++;
            } else if (entry.getValue().equals(NosStatus.INVALID.getValue())) {
                nbInvalidFile++;
            } else if (entry.getValue().equals(NosStatus.NULLPOINTER.getValue())) {
                nbAbsentFile++;
            }
        }
    }
    
    private void verifyFileExiste() {
        for (Map.Entry<String, String> entry : resultConverter.entrySet()) {
            if(entry.getValue().equals(NosStatus.TRANSFORM.getValue())) {
                File fileXml = new File("src/test/resources/zip/" +
                        entry.getKey().replace(".tabs", ".xml"));
                fileXml.exists();
            }
            
        }
    }
}
