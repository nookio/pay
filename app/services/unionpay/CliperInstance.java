package services.unionpay;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by nookio on 15/6/11.
 */
public class CliperInstance {
    private static ThreadLocal<Cipher> cipherTL = new ThreadLocal() {
        protected Cipher initialValue() {
            try {
                return Cipher.getInstance("RSA/ECB/PKCS1Padding", new BouncyCastleProvider());
            } catch (Exception var2) {
                return null;
            }
        }
    };

    public CliperInstance() {
    }

    public static Cipher getInstance() throws NoSuchAlgorithmException, NoSuchPaddingException {
        return (Cipher)cipherTL.get();
    }
}
