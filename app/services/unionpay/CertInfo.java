package services.unionpay;

import java.security.KeyStore;

/**
 * Created by nookio on 15/6/11.
 */
public class CertInfo {
    private String certPath;
    private String password;
    private String keyType;
    private KeyStore keyStore;

    public KeyStore getKeyStore() {
        return this.keyStore;
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public String getCertPath() {
        return this.certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKeyType() {
        return this.keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public CertInfo(String certPath, String password, String keyType) {
        this.certPath = certPath;
        this.password = password;
        this.keyType = keyType;
    }

    public CertInfo() {
    }
}
