package com.iteaj.plugin.protocol.plc.core.security;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

public class RSAHelper {
    /**
     * 对原始字节的数据进行加密，不限制长度，因为RSA本身限制了117字节，所以此处进行数据切割加密。<br />
     * Encrypt the original byte data without limiting the length, because RSA itself limits 117 bytes, so the data is cut and encrypted here.
     * @param publicKey RSA公钥对密钥
     * @param data 等待加密的原始数据
     * @return 加密之后的结果信息
     */
    public static byte[] EncryptLargeDataByRSA(byte[] publicKey, byte[] data )
            throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException,
            InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);

        return EncryptLargeDataByRSA(keyFactory.generatePublic(keySpec), data);
    }

    /**
     * 对原始字节的数据进行加密，不限制长度，因为RSA本身限制了117字节，所以此处进行数据切割加密。<br />
     * Encrypt the original byte data without limiting the length, because RSA itself limits 117 bytes, so the data is cut and encrypted here.
     * @param publicKey RSA公钥对密钥
     * @param data 等待加密的原始数据
     * @return 加密之后的结果信息
     */
    public static byte[] EncryptLargeDataByRSA(PublicKey publicKey, byte[] data )
            throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException{
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        ArrayList<byte[]> splits = SoftBasic.ArraySplitByLength(data, 110);
        for (int i = 0; i < splits.size(); i++) {
            byte[] encrypt = cipher.doFinal(splits.get(i));
            ms.write(encrypt, 0, encrypt.length);
        }
        return ms.toByteArray();
    }

    /**
     * 对超过117字节限制的加密数据进行加密，因为RSA本身限制了117字节，所以此处进行数据切割解密。<br />
     * @param privateKey RSA私钥对象
     * @param data 等待解密的数据
     * @return 解密之后的结果数据
     */
    public static byte[] DecryptLargeDataByRSA(byte[] privateKey, byte[] data )
            throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException,
            InvalidKeySpecException {

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return DecryptLargeDataByRSA(keyFactory.generatePrivate(keySpec), data);
    }

    /**
     * 对超过117字节限制的加密数据进行加密，因为RSA本身限制了117字节，所以此处进行数据切割解密。<br />
     * @param privateKey RSA私钥对象
     * @param data 等待解密的数据
     * @return 解密之后的结果数据
     */
    public static byte[] DecryptLargeDataByRSA(PrivateKey privateKey, byte[] data )
            throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException{
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        ArrayList<byte[]> splits = SoftBasic.ArraySplitByLength(data, 128);
        for (int i = 0; i < splits.size(); i++) {
            byte[] decrypt = cipher.doFinal(splits.get(i));
            ms.write(decrypt, 0, decrypt.length);
        }
        return ms.toByteArray();
    }
}
