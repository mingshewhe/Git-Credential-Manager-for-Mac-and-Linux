// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.alm.storage.util;

import com.microsoft.alm.helpers.IOHelper;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.InMemoryKeyring;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfigs;
import org.bouncycastle.util.io.Streams;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static com.microsoft.alm.constant.Constants.*;

public class GpgUtil {
    static {
        BouncyGPG.registerProvider();
    }

    public static InputStream decrypt(InputStream encryptedData) throws Exception {
        InMemoryKeyring memoryKeyring = KeyringConfigs.forGpgExportedKeys(new LandunKeyringConfigCallback());
        memoryKeyring.addPublicKey(LANDUN_GIT_GPG_PUBLIC_KEY.getBytes());
        memoryKeyring.addSecretKey(LANDUN_GIT_GPG_PRIVATE_KEY.getBytes());
        return BouncyGPG
                .decryptAndVerifyStream()
                .withConfig(memoryKeyring)
                .andIgnoreSignatures()
                .fromEncryptedInputStream(encryptedData);
    }

    public static void encrypt(InputStream plainInputStream, OutputStream destination) throws Exception {
        InMemoryKeyring memoryKeyring = KeyringConfigs.forGpgExportedKeys(new LandunKeyringConfigCallback());
        memoryKeyring.addPublicKey(LANDUN_GIT_GPG_PUBLIC_KEY.getBytes());
        memoryKeyring.addSecretKey(LANDUN_GIT_GPG_PRIVATE_KEY.getBytes());
        BufferedOutputStream bufferedOutputStream = null;
        OutputStream outputStream = null;
        try {
            bufferedOutputStream = new BufferedOutputStream(destination, 16384);
            outputStream = BouncyGPG
                    .encryptToStream()
                    .withConfig(memoryKeyring)
                    .withStrongAlgorithms()
                    .toRecipient(LANDUN_GIT_GPG_RECIPIENT_ID)
                    .andDoNotSign()
                    .armorAsciiOutput()
                    .andWriteTo(bufferedOutputStream);
            Streams.pipeAll(plainInputStream, outputStream);
        } finally {
            IOHelper.closeQuietly(outputStream);
            IOHelper.closeQuietly(bufferedOutputStream);
        }
    }
}
