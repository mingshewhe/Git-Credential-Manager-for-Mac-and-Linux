// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.alm.storage.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class DHUtil {
    private final static String KEY_ALGORITHM = "DH";
    private final static String KEY_PROVIDER = "BC";
    private final static String SECRECT_ALGORITHM = "DES";
    private final static BigInteger p = new BigInteger("16560215747140417249215968347342080587", 16);
    private final static BigInteger g = new BigInteger("1234567890", 16);

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static DHKeyPair initKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM, KEY_PROVIDER);
        DHParameterSpec serverParam = new DHParameterSpec(p, g, 128);
        keyPairGenerator.initialize(serverParam, new SecureRandom());
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return new DHKeyPair(keyPair.getPublic().getEncoded(), keyPair.getPrivate().getEncoded());
    }

    public static DHKeyPair initKey(byte[] partyAPublicKey) throws Exception {
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(partyAPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        DHPublicKey publicKey = (DHPublicKey) keyFactory.generatePublic(x509KeySpec);

        DHParameterSpec dhParameterSpec = publicKey.getParams();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM, KEY_PROVIDER);
        keyPairGenerator.initialize(dhParameterSpec);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return new DHKeyPair(keyPair.getPublic().getEncoded(), keyPair.getPrivate().getEncoded());
    }

    public static byte[] encrypt(byte[] data, byte[] partAPublicKey, byte[] partBPrivateKey) throws Exception {
        byte[] key = getSecretKey(partAPublicKey, partBPrivateKey);
        SecretKeySpec secretKey = new SecretKeySpec(key, SECRECT_ALGORITHM);
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, byte[] partBPublicKey, byte[] partAPrivateKey) throws Exception {
        byte[] key = getSecretKey(partBPublicKey, partAPrivateKey);
        SecretKeySpec secretKey = new SecretKeySpec(key, SECRECT_ALGORITHM);
        Cipher cipher = Cipher.getInstance(secretKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    private static byte[] getSecretKey(byte[] publicKey, byte[] privateKey) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
        PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
        KeyAgreement keyAgree = KeyAgreement.getInstance(KEY_ALGORITHM, KEY_PROVIDER);
        keyAgree.init(priKey);
        keyAgree.doPhase(pubKey, true);
        SecretKey secretKey = keyAgree.generateSecret(SECRECT_ALGORITHM);
        return secretKey.getEncoded();
    }
}
