// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.alm.storage;

import com.microsoft.alm.helpers.IOHelper;
import com.microsoft.alm.secret.Credential;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class PlaintextBackedCredentialStoreTest {

    final String xmlString ="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
            "<credential>\n" +
            "    <key>git:https://server.example.com</key>\n" +
            "    <value>\n" +
            "        <Password>swordfish</Password>\n" +
            "        <Username>j.travolta</Username>\n" +
            "    </value>\n" +
            "</credential>\n";
    final String key = "git:https://server.example.com";
    final String username = "j.travolta";
    final String password = "swordfish";


    @Test
    @Ignore
    public void testToXml() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            PlaintextBackedCredentialStore credentialStore = new PlaintextBackedCredentialStore();
            Credential credential = new Credential(username, password);
            credentialStore.toXml(bos, key, credential);
            Assert.assertEquals(xmlString, bos.toString());
        } finally {
            IOHelper.closeQuietly(bos);
        }
    }

    @Test
    public void testFromXml() {
        ByteArrayInputStream bais = null;
        try
        {
            bais = new ByteArrayInputStream(xmlString.getBytes());

            PlaintextBackedCredentialStore credentialStore = new PlaintextBackedCredentialStore();
            Credential credential = credentialStore.fromXml(key, bais);

            Assert.assertNotNull(credential);
            Assert.assertEquals(username, credential.Username);
            Assert.assertEquals(password, credential.Password);
        }
        finally
        {
            IOHelper.closeQuietly(bais);
        }
    }

    @Test
    public void testAddGetDelete() {
        PlaintextBackedCredentialStore credentialStore = new PlaintextBackedCredentialStore();
        Credential credential = new Credential(username, password);
        Assert.assertTrue(credentialStore.add(key, credential));

        Credential actualCredential = credentialStore.get(key);
        Assert.assertNotNull(actualCredential);
        Assert.assertEquals(actualCredential.Username, credential.Username);
        Assert.assertEquals(actualCredential.Password, credential.Password);
        Assert.assertTrue(credentialStore.delete(key));
    }
}
