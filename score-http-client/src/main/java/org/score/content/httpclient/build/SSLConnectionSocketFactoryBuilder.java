package org.score.content.httpclient.build;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Created with IntelliJ IDEA.
 * User: davidmih
 * Date: 6/27/14
 */
public class SSLConnectionSocketFactoryBuilder {
    public static final String TRUST_ALL_ROOTS_ERROR = "Could not use trustAllRoots=";
    public static final String SSL_CONNECTION_ERROR = "Could not create SSL connection. Invalid keystore or trustKeystore certificates.";
    public static final String BAD_KEYSTORE_ERROR = "The keystore provided in the 'keystore' input is corrupted OR the password (in the 'keystorePassword' input) is incorrect";
    public static final String INVALID_KEYSTORE_ERROR = "A keystore could not be found or it does not contain the needed certificate";
    public static final String BAD_TRUST_KEYSTORE_ERROR = "The trust keystore provided in the 'trustKeystore' input is corrupted OR the password (in the 'trustPassword' input) is incorrect";
    public static final String INVALID_TRUST_KEYSTORE_ERROR = "A trust keystore could not be found or it does not contain the needed certificate";
    private String trustAllRootsStr = "false";
    private String keystore;
    private String keystorePassword;
    private String trustKeystore;
    private String trustPassword;

    protected KeyStore createKeyStore(final URL url, final String password)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        if (url == null) {
            throw new IllegalArgumentException("Keystore url may not be null");
        }
        KeyStore keystore = KeyStore.getInstance("jks");
        InputStream is = null;
        try {
            is = url.openStream();
            keystore.load(is, password != null ? password.toCharArray() : null);
        } finally {
            if (is != null) is.close();
        }
        return keystore;
    }

    public SSLConnectionSocketFactory build() {
        boolean trustAllRoots = Boolean.parseBoolean(trustAllRootsStr);

        SSLContextBuilder sslContextBuilder = SSLContexts.custom();
        if (!trustAllRoots) {
            boolean useClientCert = !StringUtils.isEmpty(keystore);
            //validate SSL certificates sent by the server
            boolean useTrustCert = !StringUtils.isEmpty(trustKeystore);

            String javaKeystore = System.getProperty("java.home") + "/lib/security/cacerts";
            boolean storeExists = new File(javaKeystore).exists();

            if (!useClientCert && storeExists) {
                keystore = "file:" + javaKeystore;
                keystorePassword = (StringUtils.isEmpty(keystorePassword)) ? "changeit" : keystorePassword;
                useClientCert = true;
            } else if (useClientCert && !keystore.startsWith("http")) {
                keystore = "file:" + keystore;
            }

            if (!useTrustCert && storeExists) {
                trustKeystore = "file:" + javaKeystore;
                trustPassword = (StringUtils.isEmpty(trustPassword)) ? "changeit" : trustPassword;
                useTrustCert = true;
            } else if (useTrustCert && !trustKeystore.startsWith("http")) {
                trustKeystore = "file:" + trustKeystore;
            }
            createTrustKeystore(sslContextBuilder, useTrustCert);
            createKeystore(sslContextBuilder, useClientCert);
        } else {
            try {
                sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            } catch (Exception e) {
                throw new RuntimeException(TRUST_ALL_ROOTS_ERROR + trustAllRoots, e);
            }
        }

        sslContextBuilder.useSSL();
        sslContextBuilder.useTLS();

        //todo remove ALLOW_ALL_HOSTNAME_VERIFIER
        SSLConnectionSocketFactory sslsf;
        try {
            sslsf = new SSLConnectionSocketFactory(
                    sslContextBuilder.build(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Exception e) {
            throw new RuntimeException(SSL_CONNECTION_ERROR, e);
        }
        return sslsf;
    }

    protected void createKeystore(SSLContextBuilder sslContextBuilder, boolean useClientCert) {
        if (useClientCert) {
            KeyStore clientKeyStore;
            try {
                clientKeyStore = createKeyStore(new URL(keystore), keystorePassword);
                sslContextBuilder.loadKeyMaterial(clientKeyStore, keystorePassword.toCharArray());
            } catch (UnrecoverableKeyException | IOException ue) {
                throw new RuntimeException(BAD_KEYSTORE_ERROR, ue);
            } catch (GeneralSecurityException gse) {
                throw new RuntimeException(INVALID_KEYSTORE_ERROR, gse);
            }
        }
    }

    protected void createTrustKeystore(SSLContextBuilder sslContextBuilder, boolean useTrustCert) {
        if (useTrustCert) {
            KeyStore trustKeyStore;
            try {
                trustKeyStore = createKeyStore(new URL(trustKeystore), trustPassword);
                sslContextBuilder.loadTrustMaterial(trustKeyStore);
            } catch (IOException ioe) {
                throw new RuntimeException(BAD_TRUST_KEYSTORE_ERROR, ioe);
            } catch (GeneralSecurityException gse) {
                throw new RuntimeException(INVALID_TRUST_KEYSTORE_ERROR, gse);
            }
        }
    }

    public SSLConnectionSocketFactoryBuilder setTrustAllRoots(String trustAllRoots) {
        if (!StringUtils.isEmpty(trustAllRoots)) {
            this.trustAllRootsStr = trustAllRoots;
        }
        return this;
    }

    public SSLConnectionSocketFactoryBuilder setKeystore(String keystore) {
        this.keystore = keystore;
        return this;
    }

    public SSLConnectionSocketFactoryBuilder setKeystorePassword(String keystorePassword) {
        this.keystorePassword = keystorePassword;
        return this;
    }

    public SSLConnectionSocketFactoryBuilder setTrustKeystore(String trustKeystore) {
        this.trustKeystore = trustKeystore;
        return this;
    }

    public SSLConnectionSocketFactoryBuilder setTrustPassword(String trustPassword) {
        this.trustPassword = trustPassword;
        return this;
    }
}
