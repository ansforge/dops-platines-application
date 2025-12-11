/**
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package aes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import fr.asipsante.platines.aes.AesEncoder;
import fr.asipsante.platines.aes.AesException;

/**
 * @author aboittiaux
 *
 */
public class TestPasswordEncoder {

    private String passwordEncode;

    private String passwordDecode;

    private AesEncoder aesEncoder;

    @Before
    public void setPassword() {
        aesEncoder = new AesEncoder();
        passwordDecode = "Password2018!";
        passwordEncode = "C905C9nV6UzKmKF67i3L9B3ZD4INQDNXxq+TkyigFbjKThnKeYJ1YlU=";
    }

    @Test
    public void testDecode() throws AesException {
        String decode = aesEncoder.decrypt(passwordEncode);
        assertNotNull("Vérification que le mot de passe est bien décrypté", decode);
        assertEquals("Le message décrypté doit être égal à Password2018!", decode, passwordDecode);
    }

    @Test
    public void testEncrypt() throws AesException {
        String encode = aesEncoder.encrypt(passwordDecode);
        assertNotNull("Vérification que le mot de passe est bien crypté", encode);

    }

    @Test(expected = AesException.class)
    public void testAesException() throws AesException {
        aesEncoder.encrypt(null);
    }
}
