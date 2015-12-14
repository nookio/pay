package services.unionpay;


import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import play.Logger;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by nookio on 15/6/11.
 */
public class SecureUtil {
    private static final String ALGORITHM_MD5 = "MD5";
    private static final String ALGORITHM_SHA1 = "SHA-1";
    private static final String BC_PROV_ALGORITHM_SHA1RSA = "SHA1withRSA";

    private  static Logger.ALogger logger = play.Logger.of(SecureUtil.class);



    public SecureUtil() {
    }

    public static byte[] md5(byte[] datas) {
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(datas);
            return md.digest();
        } catch (Exception var3) {
            var3.printStackTrace();
            logger.error("MD5计算失败", var3);
            return null;
        }
    }

    public static byte[] sha1(byte[] data) {
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            md.reset();
            md.update(data);
            return md.digest();
        } catch (Exception var3) {
            var3.printStackTrace();
            logger.error("SHA1计算失败", var3);
            return null;
        }
    }

    public static byte[] md5X16(String datas, String encoding) {
        byte[] bytes = md5(datas, encoding);
        StringBuilder md5StrBuff = new StringBuilder();

        for(int e = 0; e < bytes.length; ++e) {
            if(Integer.toHexString(255 & bytes[e]).length() == 1) {
                md5StrBuff.append("0").append(Integer.toHexString(255 & bytes[e]));
            } else {
                md5StrBuff.append(Integer.toHexString(255 & bytes[e]));
            }
        }

        try {
            return md5StrBuff.toString().getBytes(encoding);
        } catch (UnsupportedEncodingException var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static byte[] sha1X16(String data, String encoding) {
        byte[] bytes = sha1(data, encoding);
        StringBuilder sha1StrBuff = new StringBuilder();

        for(int e = 0; e < bytes.length; ++e) {
            if(Integer.toHexString(255 & bytes[e]).length() == 1) {
                sha1StrBuff.append("0").append(Integer.toHexString(255 & bytes[e]));
            } else {
                sha1StrBuff.append(Integer.toHexString(255 & bytes[e]));
            }
        }

        try {
            return sha1StrBuff.toString().getBytes(encoding);
        } catch (UnsupportedEncodingException var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public static byte[] md5(String datas, String encoding) {
        try {
            return md5(datas.getBytes(encoding));
        } catch (UnsupportedEncodingException var3) {
           logger.error("MD5计算失败", var3);
            return null;
        }
    }

    public static byte[] sha1(String datas, String encoding) {
        try {
            return sha1(datas.getBytes(encoding));
        } catch (UnsupportedEncodingException var3) {
           logger.error("SHA1计算失败", var3);
            return null;
        }
    }

    public static byte[] signBySoft(PrivateKey privateKey, byte[] data) throws Exception {
        Object result = null;
        Signature st = Signature.getInstance("SHA1withRSA");
        st.initSign(privateKey);
        st.update(data);
        byte[] result1 = st.sign();
        return result1;
    }

    public static boolean validateSignBySoft(PublicKey publicKey, byte[] signData, byte[] srcData) throws Exception {
        Signature st = Signature.getInstance("SHA1withRSA");
        st.initVerify(publicKey);
        st.update(srcData);
        return st.verify(signData);
    }

    public static byte[] inflater(byte[] inputByte) throws IOException {
        boolean compressedDataLength = false;
        Inflater compresser = new Inflater(false);
        compresser.setInput(inputByte, 0, inputByte.length);
        ByteArrayOutputStream o = new ByteArrayOutputStream(inputByte.length);
        byte[] result = new byte[1024];

        try {
            while(!compresser.finished()) {
                int compressedDataLength1 = compresser.inflate(result);
                if(compressedDataLength1 == 0) {
                    break;
                }

                o.write(result, 0, compressedDataLength1);
            }
        } catch (Exception var9) {
            System.err.println("Data format error!\n");
            var9.printStackTrace();
        } finally {
            o.close();
        }

        compresser.end();
        return o.toByteArray();
    }

    public static byte[] deflater(byte[] inputByte) throws IOException {
        boolean compressedDataLength = false;
        Deflater compresser = new Deflater();
        compresser.setInput(inputByte);
        compresser.finish();
        ByteArrayOutputStream o = new ByteArrayOutputStream(inputByte.length);
        byte[] result = new byte[1024];

        try {
            while(!compresser.finished()) {
                int compressedDataLength1 = compresser.deflate(result);
                o.write(result, 0, compressedDataLength1);
            }
        } finally {
            o.close();
        }

        compresser.end();
        return o.toByteArray();
    }

    public static String EncryptPin(String pin, String card, String encoding, PublicKey key) {
        byte[] pinBlock = pin2PinBlockWithCardNO(pin, card);
        Object data = null;

        try {
            byte[] data1 = encryptedPin(key, pinBlock);
            return new String(base64Encode(data1), encoding);
        } catch (Exception var7) {
            var7.printStackTrace();
            return "";
        }
    }

    public static String EncryptData(String dataString, String encoding, PublicKey key) {
        Object data = null;

        try {
            byte[] data1 = encryptedPin(key, dataString.getBytes(encoding));
            return new String(base64Encode(data1), encoding);
        } catch (Exception var5) {
            var5.printStackTrace();
            return "";
        }
    }

    public static String DecryptedData(String dataString, String encoding, PrivateKey key) {
        Object data = null;

        try {
            byte[] data1 = decryptedPin(key, dataString.getBytes(encoding));
            return new String(data1, encoding);
        } catch (Exception var5) {
            var5.printStackTrace();
            return "";
        }
    }

    public static byte[] base64Decode(byte[] inputByte) throws IOException {
        return Base64.decodeBase64(inputByte);
    }

    public static byte[] base64Encode(byte[] inputByte) throws IOException {
        return Base64.encodeBase64(inputByte);
    }

    public byte[] Str2Hex(String str) {
        char[] ch = str.toCharArray();
        byte[] b = new byte[ch.length / 2];

        int i;
        for(i = 0; i < ch.length && ch[i] != 0; ++i) {
            if(ch[i] >= 48 && ch[i] <= 57) {
                ch[i] = (char)(ch[i] - 48);
            } else if(ch[i] >= 65 && ch[i] <= 70) {
                ch[i] = (char)(ch[i] - 65 + 10);
            }
        }

        for(i = 0; i < b.length; ++i) {
            b[i] = (byte)((ch[2 * i] << 4 & 240) + (ch[2 * i + 1] & 15));
        }

        return b;
    }

    public static String Hex2Str(byte[] b) {
        StringBuffer d = new StringBuffer(b.length * 2);

        for(int i = 0; i < b.length; ++i) {
            char hi = Character.forDigit(b[i] >> 4 & 15, 16);
            char lo = Character.forDigit(b[i] & 15, 16);
            d.append(Character.toUpperCase(hi));
            d.append(Character.toUpperCase(lo));
        }

        return d.toString();
    }

    public static String ByteToHex(byte[] bytes) {
        StringBuffer sha1StrBuff = new StringBuffer();

        for(int i = 0; i < bytes.length; ++i) {
            if(Integer.toHexString(255 & bytes[i]).length() == 1) {
                sha1StrBuff.append("0").append(Integer.toHexString(255 & bytes[i]));
            } else {
                sha1StrBuff.append(Integer.toHexString(255 & bytes[i]));
            }
        }

        return sha1StrBuff.toString();
    }

    public static String Hex2Str(byte[] b, int len) {
        String str = "";
        char[] ch = new char[len * 2];

        for(int i = 0; i < len; ++i) {
            if((b[i] >> 4 & 15) < 10 && (b[i] >> 4 & 15) >= 0) {
                ch[i * 2] = (char)((b[i] >> 4 & 15) + 48);
            } else {
                ch[i * 2] = (char)((b[i] >> 4 & 15) + 65 - 10);
            }

            if((b[i] & 15) < 10 && (b[i] & 15) >= 0) {
                ch[i * 2 + 1] = (char)((b[i] & 15) + 48);
            } else {
                ch[i * 2 + 1] = (char)((b[i] & 15) + 65 - 10);
            }
        }

        str = new String(ch);
        return str;
    }

    public String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";

        for(int n = 0; n < b.length; ++n) {
            stmp = Integer.toHexString(b[n] & 255);
            if(stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }

            if(n < b.length - 1) {
                hs = hs + ":";
            }
        }

        return hs.toUpperCase();
    }

    public String genmac(byte[] inputByte, byte[] inputkey) throws Exception {
        try {
            Mac ex = Mac.getInstance("HmacMD5");
            SecretKeySpec key = new SecretKeySpec(inputkey, "DES");
            ex.init(key);
            byte[] macCode = ex.doFinal(inputByte);
            String strMac = this.byte2hex(macCode);
            return strMac;
        } catch (Exception var7) {
            var7.printStackTrace();
            throw var7;
        }
    }

    public boolean checkmac(byte[] inputByte, byte[] inputkey, String inputmac) throws Exception {
        try {
            Mac ex = Mac.getInstance("HmacMD5");
            SecretKeySpec key = new SecretKeySpec(inputkey, "DES");
            ex.init(key);
            byte[] macCode = ex.doFinal(inputByte);
            String strMacCode = this.byte2hex(macCode);
            return strMacCode.equals(inputmac);
        } catch (Exception var8) {
            throw var8;
        }
    }

    public static String fillString(String string, char filler, int totalLength, boolean atEnd) {
        byte[] tempbyte = string.getBytes();
        int currentLength = tempbyte.length;
        int delta = totalLength - currentLength;

        for(int i = 0; i < delta; ++i) {
            if(atEnd) {
                string = string + filler;
            } else {
                string = filler + string;
            }
        }

        return string;
    }

    public static byte[] encryptedPin(PublicKey publicKey, byte[] plainPin) throws Exception {
        try {
            Cipher e = CliperInstance.getInstance();
            e.init(1, publicKey);
            int blockSize = e.getBlockSize();
            int outputSize = e.getOutputSize(plainPin.length);
            int leavedSize = plainPin.length % blockSize;
            int blocksSize = leavedSize != 0?plainPin.length / blockSize + 1:plainPin.length / blockSize;
            byte[] raw = new byte[outputSize * blocksSize];

            for(int i = 0; plainPin.length - i * blockSize > 0; ++i) {
                if(plainPin.length - i * blockSize > blockSize) {
                    e.doFinal(plainPin, i * blockSize, blockSize, raw, i * outputSize);
                } else {
                    e.doFinal(plainPin, i * blockSize, plainPin.length - i * blockSize, raw, i * outputSize);
                }
            }

            return raw;
        } catch (Exception var9) {
            throw new Exception(var9.getMessage());
        }
    }

    public byte[] encryptedData(PublicKey publicKey, byte[] plainData) throws Exception {
        try {
            Cipher e = Cipher.getInstance("RSA/ECB/PKCS1Padding", new BouncyCastleProvider());
            e.init(1, publicKey);
            int blockSize = e.getBlockSize();
            int outputSize = e.getOutputSize(plainData.length);
            int leavedSize = plainData.length % blockSize;
            int blocksSize = leavedSize != 0?plainData.length / blockSize + 1:plainData.length / blockSize;
            byte[] raw = new byte[outputSize * blocksSize];

            for(int i = 0; plainData.length - i * blockSize > 0; ++i) {
                if(plainData.length - i * blockSize > blockSize) {
                    e.doFinal(plainData, i * blockSize, blockSize, raw, i * outputSize);
                } else {
                    e.doFinal(plainData, i * blockSize, plainData.length - i * blockSize, raw, i * outputSize);
                }
            }

            return raw;
        } catch (Exception var10) {
            throw new Exception(var10.getMessage());
        }
    }

    public static byte[] decryptedPin(PrivateKey privateKey, byte[] cryptPin) throws Exception {
        try {
            byte[] e = base64Decode(cryptPin);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", new BouncyCastleProvider());
            cipher.init(2, privateKey);
            int blockSize = cipher.getBlockSize();
            int outputSize = cipher.getOutputSize(e.length);
            int leavedSize = e.length % blockSize;
            int blocksSize = leavedSize != 0?e.length / blockSize + 1:e.length / blockSize;
            byte[] pinData = new byte[outputSize * blocksSize];

            for(int i = 0; e.length - i * blockSize > 0; ++i) {
                if(e.length - i * blockSize > blockSize) {
                    cipher.doFinal(e, i * blockSize, blockSize, pinData, i * outputSize);
                } else {
                    cipher.doFinal(e, i * blockSize, e.length - i * blockSize, pinData, i * outputSize);
                }
            }

            return pinData;
        } catch (Exception var10) {
            logger.error("解密失败", var10);
            return null;
        }
    }

    private static byte[] pin2PinBlock(String aPin) {
        int tTemp = 1;
        int tPinLen = aPin.length();
        byte[] tByte = new byte[8];

        try {
            tByte[0] = (byte)Integer.parseInt(Integer.toString(tPinLen), 10);
            int e;
            String a;
            if(tPinLen % 2 == 0) {
                for(e = 0; e < tPinLen; e += 2) {
                    a = aPin.substring(e, e + 2);
                    tByte[tTemp] = (byte)Integer.parseInt(a, 16);
                    if(e == tPinLen - 2 && tTemp < 7) {
                        for(int b = tTemp + 1; b < 8; ++b) {
                            tByte[b] = -1;
                        }
                    }

                    ++tTemp;
                }
            } else {
                for(e = 0; e < tPinLen - 1; e += 2) {
                    a = aPin.substring(e, e + 2);
                    tByte[tTemp] = (byte)Integer.parseInt(a, 16);
                    if(e == tPinLen - 3) {
                        String var9 = aPin.substring(tPinLen - 1) + "F";
                        tByte[tTemp + 1] = (byte)Integer.parseInt(var9, 16);
                        if(tTemp + 1 < 7) {
                            for(int x = tTemp + 2; x < 8; ++x) {
                                tByte[x] = -1;
                            }
                        }
                    }

                    ++tTemp;
                }
            }
        } catch (Exception var8) {
            ;
        }

        return tByte;
    }

    private static byte[] formatPan(String aPan) {
        int tPanLen = aPan.length();
        byte[] tByte = new byte[8];
        int temp = tPanLen - 13;

        try {
            tByte[0] = 0;
            tByte[1] = 0;

            for(int e = 2; e < 8; ++e) {
                String a = aPan.substring(temp, temp + 2);
                tByte[e] = (byte)Integer.parseInt(a, 16);
                temp += 2;
            }
        } catch (Exception var6) {
            ;
        }

        return tByte;
    }

    public static byte[] pin2PinBlockWithCardNO(String aPin, String aCardNO) {
        byte[] tPinByte = pin2PinBlock(aPin);
        if(aCardNO.length() == 11) {
            aCardNO = "00" + aCardNO;
        } else if(aCardNO.length() == 12) {
            aCardNO = "0" + aCardNO;
        }

        byte[] tPanByte = formatPan(aCardNO);
        byte[] tByte = new byte[8];

        for(int i = 0; i < 8; ++i) {
            tByte[i] = (byte)(tPinByte[i] ^ tPanByte[i]);
        }

        return tByte;
    }

    private static byte[] addPKCS1Padding(byte[] aBytesText, int aBlockSize) {
        if(aBytesText.length > aBlockSize - 3) {
            return null;
        } else {
            SecureRandom tRandom = new SecureRandom();
            byte[] tAfterPaddingBytes = new byte[aBlockSize];
            tRandom.nextBytes(tAfterPaddingBytes);
            tAfterPaddingBytes[0] = 0;
            tAfterPaddingBytes[1] = 2;

            int i;
            for(i = 2; i < aBlockSize - 1 - aBytesText.length; ++i) {
                if(tAfterPaddingBytes[i] == 0) {
                    tAfterPaddingBytes[i] = (byte)tRandom.nextInt();
                }
            }

            tAfterPaddingBytes[i] = 0;
            System.arraycopy(aBytesText, 0, tAfterPaddingBytes, i + 1, aBytesText.length);
            return tAfterPaddingBytes;
        }
    }

    public String assymEncrypt(String tPIN, String iPan, RSAPublicKey publicKey) {
        System.out.println("SampleHashMap::assymEncrypt([" + tPIN + "])");
        System.out.println("SampleHashMap::assymEncrypt(PIN =[" + tPIN + "])");

        try {
            short e = 1024;
            int tBlockSize = e / 8;
            Object tTemp = null;
            byte[] var15 = pin2PinBlockWithCardNO(tPIN, iPan);
            var15 = addPKCS1Padding(var15, tBlockSize);
            BigInteger tPlainText = new BigInteger(var15);
            BigInteger tCipherText = tPlainText.modPow(publicKey.getPublicExponent(), publicKey.getModulus());
            byte[] tCipherBytes = tCipherText.toByteArray();
            int tCipherLength = tCipherBytes.length;
            byte[] tEncryptPIN;
            if(tCipherLength > tBlockSize) {
                tEncryptPIN = new byte[tBlockSize];
                System.arraycopy(tCipherBytes, tCipherLength - tBlockSize, tEncryptPIN, 0, tBlockSize);
                tCipherBytes = tEncryptPIN;
            } else if(tCipherLength < tBlockSize) {
                tEncryptPIN = new byte[tBlockSize];

                for(int i = 0; i < tBlockSize - tCipherLength; ++i) {
                    tEncryptPIN[i] = 0;
                }

                System.arraycopy(tCipherBytes, 0, tEncryptPIN, tBlockSize - tCipherLength, tCipherLength);
                tCipherBytes = tEncryptPIN;
            }

            String var16 = new String(base64Encode(tCipherBytes));
            System.out.println("SampleHashMap::assymEncrypt(EncryptCardNo =[" + var16 + "])");
            return var16;
        } catch (Exception var13) {
            var13.printStackTrace(System.out);
            return tPIN;
        } catch (Error var14) {
            var14.printStackTrace(System.out);
            return tPIN;
        }
    }

    public static String trace(byte[] inBytes) {
        int j = 0;
        byte[] temp = new byte[76];
        bytesSet(temp, ' ');
        StringBuffer strc = new StringBuffer("");
        strc.append("----------------------------------------------------------------------------\n");

        for(int i = 0; i < inBytes.length; ++i) {
            if(j == 0) {
                System.arraycopy(String.format("%03d: ", new Object[]{Integer.valueOf(i)}).getBytes(), 0, temp, 0, 5);
                System.arraycopy(String.format(":%03d", new Object[]{Integer.valueOf(i + 15)}).getBytes(), 0, temp, 72, 4);
            }

            System.arraycopy(String.format("%02X ", new Object[]{Byte.valueOf(inBytes[i])}).getBytes(), 0, temp, j * 3 + 5 + (j > 7?1:0), 3);
            if(inBytes[i] == 0) {
                temp[j + 55 + (j > 7?1:0)] = 46;
            } else {
                temp[j + 55 + (j > 7?1:0)] = inBytes[i];
            }

            ++j;
            if(j == 16) {
                strc.append(new String(temp)).append("\n");
                bytesSet(temp, ' ');
                j = 0;
            }
        }

        if(j != 0) {
            strc.append(new String(temp)).append("\n");
            bytesSet(temp, ' ');
        }

        strc.append("----------------------------------------------------------------------------\n");
        return strc.toString();
    }

    private static void bytesSet(byte[] inBytes, char fill) {
        if(inBytes.length != 0) {
            for(int i = 0; i < inBytes.length; ++i) {
                inBytes[i] = (byte)fill;
            }

        }
    }
}
