package com.loon.bridge.core.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AES工具类.
 * 
 * @author nbflow
 */
public final class AESUtil {

    /**
     * 私有构造方法.
     */
    private AESUtil() {

    }

    /** * The logger. */
    private static Logger logger = LoggerFactory.getLogger(AESUtil.class);

    /** * The Constant KEY_ALGORITHM. */
    private static final String KEY_ALGORITHM = "AES";

    // 秘钥长度为32byte，不够32byte自动加0补全，超出32byte截取前32byte
    /** * The Constant keyLen. */
    private static final int KEY_LENGTH = 32;

    /**
     * 加密.
     * 
     * @param data the data
     * @param key the key
     * @return the byte[]
     * @throws Exception the exception
     */
    public static byte[] encrypt(byte[] data, String key) throws Exception {
        Key ks = new SecretKeySpec(genKey(key), KEY_ALGORITHM);

        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, ks);

        return cipher.doFinal(data);
    }

    /**
     * 解密.
     * 
     * @param data the data
     * @param key the key
     * @return the byte[]
     * @throws Exception the exception
     */
    public static byte[] decrypt(byte[] data, String key) throws Exception {

        Key ks = new SecretKeySpec(genKey(key), KEY_ALGORITHM);

        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, ks);

        return cipher.doFinal(data);
    }

    /**
     * 2进制转为16进制.
     * 
     * @param buf the buf
     * @return the String
     */
    public static String parseByte2HexStr(byte[] buf) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 16进制转为2进制.
     * 
     * @param hexStr the hexStr
     * @return the byte[]
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * AES加密.
     * 
     * @param srcFilePath 初始文件路径
     * @param destFilePath 加密后文件保存路径
     * @param keyString 秘钥
     */
    public static void encryptFileByAes(String srcFilePath, String destFilePath, String keyString) {

        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;

        try {
            Key ks = new SecretKeySpec(genKey(keyString), "AES");
            // SecretKeyFactory skf = SecretKeyFactory.getInstance("AES");
            // SecretKey key = skf.generateSecret(ks);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, ks);
            bos = new BufferedOutputStream(new FileOutputStream(destFilePath));
            bis = new BufferedInputStream(new CipherInputStream(new FileInputStream(srcFilePath), cipher));
            byte[] buff = new byte[1024];
            int len;
            while ((len = bis.read(buff)) > 0) {
                bos.write(buff, 0, len);
            }
            bos.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    /**
     * AES解密.
     * 
     * @param srcFilePath 初始文件路径
     * @param destFilePath 解密后文件保存路径
     * @param keyString 秘钥
     */
    public static void decryptFileByAes(String srcFilePath, String destFilePath, String keyString) {

        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;

        try {
            Key ks = new SecretKeySpec(genKey(keyString), "AES");
            // SecretKeyFactory skf = SecretKeyFactory.getInstance("AES");
            // SecretKey key = skf.generateSecret(ks);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, ks);
            bis = new BufferedInputStream(new FileInputStream(srcFilePath));
            bos = new BufferedOutputStream(new CipherOutputStream(new FileOutputStream(destFilePath), cipher));
            byte[] buff = new byte[1024];
            int len;
            while ((len = bis.read(buff)) > 0) {
                bos.write(buff, 0, len);
            }
            bos.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    /**
     * 秘钥长度匹配.
     * 
     * @param keyString 输入秘钥
     * @return 补全为32byte后返回
     */
    public static byte[] genKey(String keyString) {
        byte[] bytes = keyString.getBytes();
        int len = bytes.length;

        if (len == KEY_LENGTH) {
            return bytes;
        } else if (len > KEY_LENGTH) {
            return Arrays.copyOf(bytes, KEY_LENGTH);
        } else {
            byte[] ans = new byte[KEY_LENGTH];
            System.arraycopy(bytes, 0, ans, 0, len);
            for (int i = len; i < KEY_LENGTH; i++) {
                ans[i] = '0';
            }
            return ans;
        }
    }

    /**
     * AES加密.
     * 
     * @param inputStream 文件输入流
     *
     * @param keyString 秘钥
     * @return InputStream 加密过后的输入流;
     */
    public static InputStream encryptStreamByAes(InputStream inputStream, String keyString) {

        BufferedInputStream bis = null;

        try {
            Key ks = new SecretKeySpec(genKey(keyString), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, ks);
            bis = new BufferedInputStream(new CipherInputStream(inputStream, cipher));
            return bis;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * AES解密.
     * 
     * @param inputStream 加密的输入流
     * 
     * @param keyString 秘钥
     * 
     * @return InputStream 解密的输入流;
     */
    public static InputStream decryptStreamByAes(InputStream inputStream, String keyString) {

        BufferedInputStream bis = null;

        try {
            Key ks = new SecretKeySpec(genKey(keyString), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, ks);
            bis = new BufferedInputStream(new CipherInputStream(inputStream, cipher));
            return bis;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return bis;
    }

    public static void main(String[] args) {
        
        // AESUtil.decryptFileByAes("/home/fffff/9-10", "/home/fffff/contacts.zip", "ZhixinUC");
        
        /*try {
            byte[] data = AESUtil.decrypt(FileUtil.getBytes(new File("C:/Users/Administrator/92734_eab.dat")), "uid");
            data = AESUtil.decrypt(data, "ZhixinUC");
            InputStream input = new ByteArrayInputStream(data);
            FileUtil.saveFile(input, "D:/mao.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}
