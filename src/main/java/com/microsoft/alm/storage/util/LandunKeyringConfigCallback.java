package com.microsoft.alm.storage.util;

import com.microsoft.alm.constant.Constants;
import name.neuhalfen.projects.crypto.bouncycastle.openpgp.keys.callbacks.KeyringConfigCallback;

import java.util.Base64;

import static com.microsoft.alm.constant.Constants.LANDUN_GIT_DH_PRIVATE_KEY;

public class LandunKeyringConfigCallback implements KeyringConfigCallback {

    @Override
    public char[] decryptionSecretKeyPassphraseForSecretKeyId(long keyId) {
        String vmSeqId = System.getenv(Constants.DEVOPS_VM_SEQ_ID);
        String publicKey = System.getenv(Constants.GIT_CREDENTIAL_PUBLIC_KEY + "_" + vmSeqId);
        String encryptPassphrase = System.getenv(Constants.GIT_CREDENTIAL_PASSPHRASE + "_" + vmSeqId);
        if (vmSeqId == null || publicKey == null || encryptPassphrase == null) {
            return null;
        }
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            return new String(
                    DHUtil.decrypt(
                            decoder.decode(encryptPassphrase),
                            decoder.decode(publicKey),
                            decoder.decode(LANDUN_GIT_DH_PRIVATE_KEY)
                    )
            ).toCharArray();
        } catch (Exception e) {
            return null;
        }
    }
}
