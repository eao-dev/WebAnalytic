package com.webAnalytic.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash {

    private static String algorithm = "SHA-256";

    public static void setAlgorithm(String algorithm) {
        Hash.algorithm = algorithm;
    }

    public static byte[] doHash(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        return md.digest(data.getBytes());
    }

}
