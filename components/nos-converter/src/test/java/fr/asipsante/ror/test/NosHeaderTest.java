/*
 *  (c) Copyright 1998-2016, ASIP. All rights reserved.
 */
package fr.asipsante.ror.test;

import fr.asipsante.ror.converter.enumeration.NosHeader;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author aboittiaux
 */
public class NosHeaderTest {
    private final String libelle;
    private final String code;
    private final String dateMaj;
    private final String libelleAdapte;
    private final String nomFichier;
    private final String oid;
    private final String typeFichier;
    public NosHeaderTest() {
        libelle = NosHeader.LIBELLE.getValue();
        code = NosHeader.CODE.getValue();
        dateMaj = NosHeader.DATE_MAJ.getValue();
        libelleAdapte = NosHeader.LIBELLE_ADAPTE.getValue();
        nomFichier = NosHeader.NOM_FICHIER.getValue();
        oid = NosHeader.OID.getValue();
        typeFichier = NosHeader.TYPE_FICHIER.getValue();
    }
    
    @Test
    public void testRetourEnum() {
        Assert.assertEquals(
                "La valeur de l'enum LIBELLE correspond bien à <Libellé>", "<Libellé>", libelle);
        Assert.assertEquals(
                "La valeur de l'enum CODE correspond bien à <Code>", "<Code>", code);
        Assert.assertEquals(
                "La valeur de l'enum DATE_MAJ correspond bien à <Date MàJ>", "<Date MàJ>", dateMaj);
        Assert.assertEquals(
                "La valeur de l'enum LIBELLE_ADAPTE correspond bien à <Libellé adapté>",
                "<Libellé adapté>", libelleAdapte);
        Assert.assertEquals(
                "La valeur de l'enum NOM_FICHIER correspond bien à <Nom fichier>", "<Nom fichier>", nomFichier);
        Assert.assertEquals(
                "La valeur de l'enum OID correspond bien à <OID>", "<OID>", oid);
        Assert.assertEquals(
                "La valeur de l'enum TYPE_FICHIER correspond bien à <Type fichier>", "<Type fichier>", typeFichier);
        
        
        
        
    }
}
