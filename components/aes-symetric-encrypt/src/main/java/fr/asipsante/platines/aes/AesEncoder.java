/**
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.platines.aes;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Class to encrypt and decrypt a String.
 * @author aboittiaux
 *
 */
public class AesEncoder {

    /**
     * Constant GCM_IV_LENGTH.
     */
    private final static int GCM_IV_LENGTH = 12;

    /**
     * Constant GCM_TAG_LENGTH.
     */
    private final static int GCM_TAG_LENGTH = 16;

    /**
     * The secret key.
     */
    private SecretKey key;

    /**
     * Default constructor.
     */
    public AesEncoder() {
        String privateKey = "";
        if (System.getenv("AES_PRIVATE_KEY") != null) {
            privateKey = System.getenv("AES_PRIVATE_KEY");
        } else {
            privateKey = "XMzDdG4D03CK4Dr9";
        }
        key = new SecretKeySpec(privateKey.getBytes(), "AES");
    }

    /**
     * Method to crypt the password.
     * This method crypt a password with a private key
     * locate in system environment.
     * @param privateString
     * @return
     * @throws AesException
     */
    public String encrypt(String privateString) throws AesException {
        byte[] encrypted;
        String encoded = null;
        try {
            final byte[] iv = new byte[GCM_IV_LENGTH];
            (new SecureRandom()).nextBytes(iv);

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            final GCMParameterSpec ivSpec = new GCMParameterSpec(GCM_TAG_LENGTH * Byte.SIZE, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);

            final byte[] ciphertext = cipher.doFinal(privateString.getBytes("UTF8"));
            encrypted = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, encrypted, 0, iv.length);
            System.arraycopy(ciphertext, 0, encrypted, iv.length, ciphertext.length);
            encoded = Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new AesException(e);
        }

        return encoded;
    }

    /**
     * Method to decrypt the password.
     * This method decrypt an encoded password
     * with the method encrypt
     * @param encrypted
     * @return
     * @throws AesException
     */
    public String decrypt(String encrypted) throws AesException {
        final byte[] decoded = Base64.getDecoder().decode(encrypted);
        String result = null;
        try {
            final byte[] iv = Arrays.copyOfRange(decoded, 0, GCM_IV_LENGTH);
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            final GCMParameterSpec ivSpec = new GCMParameterSpec(GCM_TAG_LENGTH * Byte.SIZE, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
            final byte[] ciphertext = cipher.doFinal(decoded, GCM_IV_LENGTH, decoded.length - GCM_IV_LENGTH);
            result = new String(ciphertext, "UTF8");
        } catch (Exception e) {
            throw new AesException(e);
        }
        return result;
    }

}
