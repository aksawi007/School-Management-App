package org.sma.platform.common.datamodel.amqp;

public class SSLContext {
    private String keyStore;
    private String keyStorePassword;
    private String trustStore;
    private String trustStorePassword;
    private String tlsVersion = "TLSv1.2";
    private String keyStoreType;
    private String trustStoreType;
    private boolean externalizedCertificate;

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(String trustStore) {
        this.trustStore = trustStore;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }

    public String getTlsVersion() {
        return tlsVersion;
    }

    public void setTlsVersion(String tlsVersion) {
        this.tlsVersion = tlsVersion;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public String getTrustStoreType() {
        return trustStoreType;
    }

    public void setTrustStoreType(String trustStoreType) {
        this.trustStoreType = trustStoreType;
    }

    public boolean isExternalizedCertificate() {
        return externalizedCertificate;
    }

    public void setExternalizedCertificate(boolean externalizedCertificate) {
        this.externalizedCertificate = externalizedCertificate;
    }
}
