package com.microsoft.alm.storage.util;

import java.util.Arrays;

public class DHKeyPair {
    private byte[] publicKey;
    private byte[] privateKey;

    public DHKeyPair(byte[] publicKey, byte[] privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DHKeyPair dhKeyPair = (DHKeyPair) o;

        if (!Arrays.equals(publicKey, dhKeyPair.publicKey)) return false;
        return Arrays.equals(privateKey, dhKeyPair.privateKey);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(publicKey);
        result = 31 * result + Arrays.hashCode(privateKey);
        return result;
    }
}
