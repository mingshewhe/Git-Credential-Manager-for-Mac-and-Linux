// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.alm.storage;

import com.microsoft.alm.secret.Credential;
import org.junit.Assert;
import org.junit.Test;

public class GpgPassCredentialStoreTest {
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
        Assert.assertTrue(credentialStore.delete(key));
    }
}
