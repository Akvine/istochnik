package ru.akvine.istochnik.utils;

import lombok.experimental.UtilityClass;
import ru.akvine.istochnik.exceptions.CryptoException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public class CryptoUtils {
    public String hashSHA256(String input) {
        return hash(input, "SHA-256");
    }

    public String hashMD5(String input) {
        return hash(input, "MD5");
    }

    private String hash(String input, String algorithm) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException exception) {
            String errorMessage = String.format("Error while hash input. Message = [%s]", exception.getMessage());
            throw new CryptoException(errorMessage);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = String.format("%02x", b);
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
