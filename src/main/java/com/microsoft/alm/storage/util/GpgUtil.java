package com.microsoft.alm.storage.util;

import name.neuhalfen.projects.crypto.bouncycastle.openpgp.BouncyGPG;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.InMemoryKeyring;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.keyrings.KeyringConfigs;
import org.bouncycastle.util.io.Streams;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static com.microsoft.alm.constant.Constants.*;

public class GpgUtil {
    static {
        BouncyGPG.registerProvider();
    }

    /**
     * gpg解密
     *
     * @param encryptedData 加密的输入流
     * @return 解密后的输入流, 使用者需要自己关闭流
     * @throws Exception
     */
    public static InputStream decrypt(InputStream encryptedData) throws Exception {
        InMemoryKeyring memoryKeyring = KeyringConfigs.forGpgExportedKeys(new LandunKeyringConfigCallback());
        memoryKeyring.addPublicKey(LANDUN_GIT_GPG_PUBLIC_KEY.getBytes(StandardCharsets.UTF_8));
        memoryKeyring.addSecretKey(LANDUN_GIT_GPG_PRIVATE_KEY.getBytes(StandardCharsets.UTF_8));
        return BouncyGPG
                .decryptAndVerifyStream()
                .withConfig(memoryKeyring)
                .andIgnoreSignatures()
                .fromEncryptedInputStream(encryptedData);
    }

    /**
     * gpg加密
     *
     * @param plainInputStream 明文输入流
     * @param destination      加密后输出目的地
     */
    public static void encrypt(InputStream plainInputStream, OutputStream destination) throws Exception {
        InMemoryKeyring memoryKeyring = KeyringConfigs.forGpgExportedKeys(new LandunKeyringConfigCallback());
        memoryKeyring.addPublicKey(LANDUN_GIT_GPG_PUBLIC_KEY.getBytes(StandardCharsets.UTF_8));
        memoryKeyring.addSecretKey(LANDUN_GIT_GPG_PRIVATE_KEY.getBytes(StandardCharsets.UTF_8));
        try (
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(destination, 16384);
                final OutputStream outputStream = BouncyGPG
                        .encryptToStream()
                        .withConfig(memoryKeyring)
                        .withStrongAlgorithms()
                        .toRecipient(LANDUN_GIT_GPG_RECIPIENT_ID)
                        .andDoNotSign()
                        .armorAsciiOutput()
                        .andWriteTo(bufferedOutputStream);
        ) {
            Streams.pipeAll(plainInputStream, outputStream);
        }
    }
}
