/*
 *  (c) Copyright 1998-2016, ASIP. All rights reserved.
 */
package fr.asipsante.ror.test;

import fr.asipsante.ror.converter.enumeration.NosStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author aboittiaux
 */
public class NosStatusTest {
    
    private final String transform;
    private final String notTransform;
    private final String notPresent;
    
    public NosStatusTest() {
        notTransform = NosStatus.INVALID.getValue();
        transform = NosStatus.TRANSFORM.getValue();
        notPresent = NosStatus.NULLPOINTER.getValue();
    }
    
    @Test
    public void testValueNosStatus() {
        Assert.assertEquals(
                "La valeur de l'enum NON_TRANSFORM correspond bien à Non converti",
                "Invalide", notTransform);
        Assert.assertEquals(
                "La valeur de l'enum TRANSFORM correspond bien à Converti", "Converti", transform);
        Assert.assertEquals(
                "La valeur de l'enum NULLPOINTER correspond bien à Absent", "Manquant", notPresent);
    }
}
