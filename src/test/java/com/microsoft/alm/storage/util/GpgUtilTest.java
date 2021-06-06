package com.microsoft.alm.storage.util;

import org.bouncycastle.util.io.Streams;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class GpgUtilTest {

    private final String plainText = "ewrolmofasfsda";

    @Test
    public void encryptAndDecrypt() throws Exception {
        try (
                ByteArrayOutputStream destination = new ByteArrayOutputStream();
                ByteArrayInputStream plainInputStream = new ByteArrayInputStream(plainText.getBytes());
                ByteArrayOutputStream plainBA = new ByteArrayOutputStream()
                )
        {
            GpgUtil.encrypt(plainInputStream, destination);
            ByteArrayInputStream encryptInputStream = new ByteArrayInputStream(destination.toByteArray());
            Streams.pipeAll(GpgUtil.decrypt(encryptInputStream), plainBA);
            encryptInputStream.close();
            Assert.assertEquals(plainText, plainBA.toString());
        }
    }
}