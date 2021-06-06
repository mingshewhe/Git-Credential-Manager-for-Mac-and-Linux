package com.microsoft.alm.storage;

import com.microsoft.alm.secret.Credential;
import org.junit.Assert;
import org.junit.Test;

public class GpgPassCredentialStoreTest {
    final String xmlString =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
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
    public void testAddGetDelete() {
        GpgPassCredentialStore credentialStore = new GpgPassCredentialStore();
        Credential credential = new Credential(username, password);
        Assert.assertTrue(credentialStore.add(key, credential));

        Credential actualCredential = credentialStore.get(key);
        Assert.assertNotNull(actualCredential);
        Assert.assertEquals(actualCredential.Username, credential.Username);
        Assert.assertEquals(actualCredential.Password, credential.Password);
        //Assert.assertTrue(credentialStore.delete(key));
    }
}