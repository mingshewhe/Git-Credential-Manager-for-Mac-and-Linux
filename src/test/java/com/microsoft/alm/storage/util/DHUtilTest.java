package com.microsoft.alm.storage.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Base64;

public class DHUtilTest {

    private final String plainText = "ewrolmofasfsda";
    @Test
    public void encryptAndDecrypt() throws Exception {
        DHKeyPair dhKeyPair = DHUtil.initKey();
        byte[] encryptData = DHUtil.encrypt(plainText.getBytes(), dhKeyPair.getPublicKey(), dhKeyPair.getPrivateKey());
        String decryptText = new String(DHUtil.decrypt(encryptData, dhKeyPair.getPublicKey(), dhKeyPair.getPrivateKey()));
        Assert.assertEquals(plainText, decryptText);
    }

    @Test
    public void encryptAndDecryptByPublicKey() throws Exception {
        // publicKey: MEcwLQYJKoZIhvcNAQMBMCACExZWAhV0cUBBckkhWWg0c0IIBYcCBRI0VniQAgIAgAMWAAITC9wdlrZZPAArIr15qi5PaXjI6w==
        // privateKey: MEcCAQAwLQYJKoZIhvcNAQMBMCACExZWAhV0cUBBckkhWWg0c0IIBYcCBRI0VniQAgIAgAQTAhEAvS9+c9BhV1wV28BI0IOotA==
        DHKeyPair clientDhKeyPair = DHUtil.initKey();
        Base64.Decoder decoder = Base64.getDecoder();
        DHKeyPair serverDhKeyPair = DHUtil.initKey(clientDhKeyPair.getPublicKey());
        byte[] encryptData = DHUtil.encrypt(plainText.getBytes(), clientDhKeyPair.getPublicKey(), serverDhKeyPair.getPrivateKey());
        byte[] decryptData = DHUtil.decrypt(encryptData, serverDhKeyPair.getPublicKey(), clientDhKeyPair.getPrivateKey());
        Assert.assertEquals(plainText, new String(decryptData));
    }
}