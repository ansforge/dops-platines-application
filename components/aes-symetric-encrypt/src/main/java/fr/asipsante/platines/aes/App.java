/**
 * (c) Copyright 1998-2017, ASIP. All rights reserved.
 */
package fr.asipsante.platines.aes;

/**
 * Jar de cryptage mot de passe plateforme platines.
 *
 */
public class App {

    private App() {

    }

    /**
     * Method main to use the jar in command line.
     * 
     * @param args
     * @throws AesException
     */
    public static void main(String[] args) throws AesException {

        if (args.length > 0) {
            final String password = args[0];
            final AesEncoder aesEncoder = new AesEncoder();
            final String encryptedValue1 = aesEncoder.encrypt(password);
            if (encryptedValue1 != null) {
                System.out.println("Mot de passe chiffré : " + encryptedValue1);
            } else {
                System.out.println("Erreur lors du chiffrement du mot de passe");
            }
        } else {
            System.out.println("Erreur : pas de mot de passe ..");
        }

    }
}
