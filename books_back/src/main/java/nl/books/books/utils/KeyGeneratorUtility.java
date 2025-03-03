package nl.books.books.utils;import java.security.KeyPair;import java.security.KeyPairGenerator;import java.security.NoSuchAlgorithmException;public class KeyGeneratorUtility {    /**     * Generates an RSA key pair with a key size of 2048 bits.     *     * @return a {@link KeyPair} containing the RSA public and private keys.     * @throws IllegalStateException if the key pair generation fails.     */    public static KeyPair generateRsaKey() {        try {            // Create an RSA KeyPairGenerator instance            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");            // Initialize with a key size of 2048 bits (secure for most use cases)            keyPairGenerator.initialize(2048);            // Generate and return the KeyPair            return keyPairGenerator.generateKeyPair();        } catch (NoSuchAlgorithmException e) {            throw new IllegalStateException("RSA algorithm is not supported by the runtime environment", e);        } catch (Exception e) {            throw new IllegalStateException("An error occurred while generating RSA key pair", e);        }    }}