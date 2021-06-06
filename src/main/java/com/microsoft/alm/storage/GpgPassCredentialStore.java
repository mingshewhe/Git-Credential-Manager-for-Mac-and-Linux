package com.microsoft.alm.storage;

import com.microsoft.alm.helpers.IOHelper;
import com.microsoft.alm.secret.Credential;
import com.microsoft.alm.storage.util.FilePathUtil;
import com.microsoft.alm.storage.util.GpgUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class GpgPassCredentialStore extends PlaintextBackedCredentialStore {
    private static final Logger logger = LoggerFactory.getLogger(GpgPassCredentialStore.class);
    protected String STORE_ROOT = ".password-store";
    private static final String credentialFileName = "git-credential-devops.gpg";

    @Override
    protected void toXml(OutputStream destination, String key, Credential secret) {
        ByteArrayOutputStream xmlOutputStream = null;
        ByteArrayInputStream xmlInputStream = null;
        try {
            xmlOutputStream = new ByteArrayOutputStream();
            super.toXml(xmlOutputStream, key, secret);
            xmlInputStream = new ByteArrayInputStream(xmlOutputStream.toByteArray());
            GpgUtil.encrypt(xmlInputStream, destination);
        } catch (Exception e) {
            logger.info("Warning: unable to serialize secret. Is the content corrupted?", e);
        } finally {
            IOHelper.closeQuietly(xmlInputStream);
            IOHelper.closeQuietly(xmlOutputStream);
        }
    }

    @Override
    protected Credential fromXml(String key, InputStream source) {
        try (InputStream plainInputStream = GpgUtil.decrypt(source)) {
            return super.fromXml(key, plainInputStream);
        } catch (Exception e) {
            logger.info("Warning: unable to deserialize credentialFile. Is the file corrupted?", e);
        }
        return null;
    }

    @Override
    protected File getCredentialFile(String key) {
        final File parentFolder = new File(FilePathUtil.determineParentFolder(), STORE_ROOT);
        final File programFolder = new File(parentFolder, FilePathUtil.keyToPathName(key));
        return new File(programFolder, credentialFileName);
    }

    @Override
    public boolean isSecure() {
        return true;
    }
}
