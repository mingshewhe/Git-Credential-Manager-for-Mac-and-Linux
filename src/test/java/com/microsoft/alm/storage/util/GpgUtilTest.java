// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.alm.storage.util;

import com.microsoft.alm.helpers.IOHelper;
import org.bouncycastle.util.io.Streams;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class GpgUtilTest {

    private final String plainText = "ewrolmofasfsda";

    @Test
    public void encryptAndDecrypt() throws Exception {
        ByteArrayOutputStream destination = null;
        ByteArrayInputStream plainInputStream = null;
        try {
            destination = new ByteArrayOutputStream();
            plainInputStream = new ByteArrayInputStream(plainText.getBytes());

            GpgUtil.encrypt(plainInputStream, destination);
        } finally {
            IOHelper.closeQuietly(plainInputStream);
            IOHelper.closeQuietly(destination);
        }

        ByteArrayInputStream encryptInputStream = null;
        InputStream decryptInputStream = null;
        ByteArrayOutputStream plainBA = null;
        try {
            encryptInputStream = new ByteArrayInputStream(destination.toByteArray());
            decryptInputStream = GpgUtil.decrypt(encryptInputStream);
            plainBA = new ByteArrayOutputStream();
            Streams.pipeAll(decryptInputStream, plainBA);
            encryptInputStream.close();
            Assert.assertEquals(plainText, plainBA.toString());
        } finally {
            IOHelper.closeQuietly(plainBA);
            IOHelper.closeQuietly(decryptInputStream);
            IOHelper.closeQuietly(encryptInputStream);
        }
    }
}
