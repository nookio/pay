package services.unionpay;

import config.UnionpayConfig;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import play.Logger;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nookio on 15/8/4.
 */
public class CertUtil {

    private CertUtil(){}

    private static KeyStore keyStore = null;

    private static Logger.ALogger logger = Logger.of(CertUtil.class);

    private static Map<String, X509Certificate> certMap = new HashMap();

    private static X509Certificate validateCert = null;

    static {
        init();
    }

    public static void init() {
        initSignCert();
//        initEncryptCert();
        initValidateCertFromDir();
    }

    public static void initSignCert() {
        logger.info("加载签名证书开始");
        if(null != keyStore) {
            keyStore = null;
        }

        try {
            keyStore = getKeyInfo(UnionpayConfig.CERT_PATH, UnionpayConfig.PASSWORD, UnionpayConfig.KEY_TYPE);
        } catch (IOException var1) {
            logger.error("加载签名证书失败", var1);
        }

        logger.info("加载签名证书结束");
    }


    public static String getSignCertId() {
        try {
            Enumeration e = keyStore.aliases();
            String keyAlias = null;
            if(e.hasMoreElements()) {
                keyAlias = (String)e.nextElement();
            }

            X509Certificate cert = (X509Certificate)keyStore.getCertificate(keyAlias);
            return cert.getSerialNumber().toString();
        } catch (Exception var3) {
            logger.error("获取签名证书的序列号失败", var3);
            if(null == keyStore) {
                logger.error("keyStore实例化失败,当前为NULL");
            }

            return "";
        }
    }

    public static KeyStore getKeyInfo(String pfxkeyfile, String keypwd, String type) throws IOException {
        FileInputStream fis = null;

        Object nPassword;
        try {
            try {
                logger.info("KeyStore Loading Start...");
                KeyStore e = null;
                if("JKS".equals(type)) {
                    e = KeyStore.getInstance(type);
                } else if("PKCS12".equals(type)) {
                    Security.insertProviderAt(new BouncyCastleProvider(), 1);
                    Security.addProvider(new BouncyCastleProvider());
                    e = KeyStore.getInstance(type);
                }

                logger.info("传入的私钥证书路径为=>[" + pfxkeyfile + "],密码=[" + keypwd + "]");
                fis = new FileInputStream(pfxkeyfile);
                nPassword = null;
                char[] nPassword1 = null != keypwd && !"".equals(keypwd.trim())?keypwd.toCharArray():null;
                if(null != e) {
                    e.load(fis, nPassword1);
                }

                logger.info("KeyStore Loading End...");
                KeyStore var6 = e;
                return var6;
            } catch (Exception var10) {
                if(Security.getProvider("BC") == null) {
                    logger.info("BC Provider not installed.");
                    logger.error("读取私钥证书失败", var10);
                    if(var10 instanceof KeyStoreException && "PKCS12".equals(type)) {
                        Security.removeProvider("BC");
                    }
                }
            }
            nPassword = null;
        } finally {
            if(null != fis) {
                fis.close();
            }
        }
        return (KeyStore)nPassword;
    }

    public static void initValidateCertFromDir() {
        logger.info("从目录中加载验证签名证书开始.");
        certMap.clear();
        String filePath = UnionpayConfig.VALIDATE_CERT_DIR;
        if(null != filePath && !"".equals(filePath)) {
            CertificateFactory cf = null;
            FileInputStream in = null;

            try {
                cf = CertificateFactory.getInstance("X.509");
                File e = new File(filePath);
                File[] files = e.listFiles(new CertUtil.CerFilter());

                for(int i = 0; i < files.length; ++i) {
                    File file = files[i];
                    in = new FileInputStream(file.getAbsolutePath());
                    validateCert = (X509Certificate)cf.generateCertificate(in);
                    certMap.put(validateCert.getSerialNumber().toString(), validateCert);
                    logger.info("[" + file.getAbsolutePath() + "][serialNumber=" + validateCert.getSerialNumber().toString() + "]");
                }
            } catch (CertificateException var17) {
                logger.error("验证签名证书加载失败", var17);
            } catch (FileNotFoundException var18) {
                logger.error("验证签名证书加载失败,证书文件不存在", var18);
            } finally {
                if(null != in) {
                    try {
                        in.close();
                    } catch (IOException var16) {
                        logger.error(var16.toString());
                    }
                }

            }

            logger.info("从目录中加载验证签名证书结束.");
        } else {
            logger.error("验证签名证书路径配置为空.");
        }
    }

    public static PrivateKey getSignCertPrivateKey() {
        try {
            Enumeration e = keyStore.aliases();
            String keyAlias = null;
            if(e.hasMoreElements()) {
                keyAlias = (String)e.nextElement();
            }

            PrivateKey privateKey = (PrivateKey)keyStore.getKey(keyAlias, UnionpayConfig.PASSWORD.toCharArray());
            return privateKey;
        } catch (Exception e) {
            logger.error("获取签名证书的私钥失败", e);
            return null;
        }
    }

    public static PublicKey getValidateKey(String certId) {
        X509Certificate cf = null;
        if(certMap.containsKey(certId)) {
            cf = (X509Certificate)certMap.get(certId);
            return cf.getPublicKey();
        } else {
            initValidateCertFromDir();
            if(certMap.containsKey(certId)) {
                cf = (X509Certificate)certMap.get(certId);
                return cf.getPublicKey();
            } else {
                logger.error("没有certId=[" + certId + "]对应的验签证书文件,返回NULL.");
                return null;
            }
        }
    }

    static class CerFilter implements FilenameFilter {
        CerFilter() {
        }

        public boolean isCer(String name) {
            return name.toLowerCase().endsWith(".cer");
        }

        public boolean accept(File dir, String name) {
            return this.isCer(name);
        }
    }
}
