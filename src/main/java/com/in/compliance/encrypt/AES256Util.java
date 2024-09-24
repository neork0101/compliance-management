package com.in.compliance.encrypt;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class AES256Util {

    private static final String AES = "AES";
    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 12;
    private static final int TAG_SIZE = 128;

    private static final SecretKey SECRET_KEY = generateKey();

    // Generate AES-256 key (you should securely store this in production)
    private static SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
            keyGenerator.init(KEY_SIZE);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new RuntimeException("Error generating AES key", e);
        }
    }

    // Encrypt data
    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(AES_GCM);
            byte[] iv = new byte[IV_SIZE];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(iv);

            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY, parameterSpec);

            byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            byte[] encryptedDataWithIv = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, encryptedDataWithIv, 0, iv.length);
            System.arraycopy(encryptedData, 0, encryptedDataWithIv, iv.length, encryptedData.length);

            return Base64.getEncoder().encodeToString(encryptedDataWithIv);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    // Decrypt data
    public static String decrypt(String encryptedData) {
        try {
            byte[] data = Base64.getDecoder().decode(encryptedData);
            byte[] iv = new byte[IV_SIZE];
            byte[] actualEncryptedData = new byte[data.length - IV_SIZE];

            System.arraycopy(data, 0, iv, 0, iv.length);
            System.arraycopy(data, iv.length, actualEncryptedData, 0, actualEncryptedData.length);

            Cipher cipher = Cipher.getInstance(AES_GCM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY, parameterSpec);

            byte[] decryptedData = cipher.doFinal(actualEncryptedData);
            return new String(decryptedData, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }
}
