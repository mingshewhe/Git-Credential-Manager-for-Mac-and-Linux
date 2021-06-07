// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See License.txt in the project root.

package com.microsoft.alm.storage;

import com.microsoft.alm.helpers.IOHelper;
import com.microsoft.alm.helpers.XmlHelper;
import com.microsoft.alm.secret.Credential;
import com.microsoft.alm.storage.util.FilePathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

import static com.microsoft.alm.helpers.LoggingHelper.logError;

public class PlaintextBackedCredentialStore implements SecretStore<Credential> {

    private static final Logger logger = LoggerFactory.getLogger(PlaintextBackedCredentialStore.class);
    private static final String STORE_ROOT = ".store";
    private static final String credentialFileName = "git-credential-landun.xml";


    @Override
    public Credential get(String key) {
        File credentialFile = getCredentialFile(key);
        if (!credentialFile.exists()) {
            return null;
        }
        InputStream is = null;
        try {
            is = new FileInputStream(credentialFile);
            return fromXml(key, is);
        } catch (FileNotFoundException e) {
            logger.info("credentialFile {} did not exist", credentialFile.getAbsolutePath());
        } catch (Exception e) {
            logger.info("credentialFile {} read error", credentialFile.getAbsolutePath());
        } finally {
            IOHelper.closeQuietly(is);
        }
        return null;
    }

    @Override
    public boolean delete(String key) {
        File credentialFile = getCredentialFile(key);
        return credentialFile.delete();
    }

    @Override
    public boolean add(String key, Credential secret) {
        File credentialFile = getCredentialFile(key);
        File parentFile = credentialFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }

        FileOutputStream fos = null;
        try  {
            fos = new FileOutputStream(credentialFile);
            toXml(fos, key, secret);
            return true;
        } catch (FileNotFoundException e) {
            logger.info("credentialFile {} did not exist", credentialFile.getAbsolutePath());
        } catch (Exception e) {
            logger.info("credentialFile {} write error", credentialFile.getAbsolutePath());
        } finally {
            IOHelper.closeQuietly(fos);
        }
        return false;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    protected void toXml(final OutputStream destination, String key, Credential secret) {
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = dbf.newDocumentBuilder();
            final Document document = builder.newDocument();

            final Element credentialNode = document.createElement("credential");
            final Element keyNode = document.createElement("key");
            final Text keyValue = document.createTextNode(key);
            keyNode.appendChild(keyValue);
            credentialNode.appendChild(keyNode);
            final Element valueNode = secret.toXml(document);
            credentialNode.appendChild(valueNode);
            document.appendChild(credentialNode);

            final TransformerFactory tf = TransformerFactory.newInstance();
            final Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(new DOMSource(document), new StreamResult(destination));
        } catch (final Exception e) {
            logError(logger, "Warning: unable to serialize secret. Is the content corrupted?", e);
        }
    }

    protected Credential fromXml(String key, final InputStream source) {
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = dbf.newDocumentBuilder();
            final Document document = builder.parse(source);
            final Element credentialNode = document.getDocumentElement();

            String credentialKey = null;
            Credential value = null;
            final NodeList keyOrValueList = credentialNode.getChildNodes();
            for (int kov = 0; kov < keyOrValueList.getLength(); kov++) {
                final Node keyOrValueNode = keyOrValueList.item(kov);
                if (keyOrValueNode.getNodeType() != Node.ELEMENT_NODE) continue;

                final String keyOrValueName = keyOrValueNode.getNodeName();
                if ("key".equals(keyOrValueName)) {
                    credentialKey = XmlHelper.getText(keyOrValueNode);
                } else if ("value".equals(keyOrValueName)) {
                    value = Credential.fromXml(keyOrValueNode);
                }
            }
            if (credentialKey.equals(key)) {
                return value;
            }
        } catch (final Exception e) {
            logError(logger, "Warning: unable to deserialize credentialFile. Is the file corrupted?", e);
        }
        return null;
    }

    protected File getCredentialFile(String key) {
        final File parentFolder = new File(FilePathUtil.determineParentFolder(), STORE_ROOT);
        final File programFolder = new File(parentFolder, FilePathUtil.keyToPathName(key));
        return new File(programFolder, credentialFileName);
    }
}
