package com.iteaj.plugin.protocol.plc.core.security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;

/**
 * AES加密解密的密钥信息
 */
public class AesCryptography {
    public AesCryptography(String key){
        this.key = key;
        try{
            Key key1 = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            encryptCipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            encryptCipher.init(Cipher.ENCRYPT_MODE, key1);

            decryptCipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            decryptCipher.init(Cipher.DECRYPT_MODE, key1);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * 加密指定的数据信息
     * @param data 数据信息
     * @return 加密的结果
     */
    public byte[] Encrypt( byte[] data )
    {
        if (data == null) return null;
        try{
            return encryptCipher.doFinal( data );
        }
        catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 将指定的数据进行解密操作
     * @param data 等待解密的数据
     * @return 解密之后的数据信息
     * @throws BadPaddingException 异常
     * @throws IllegalBlockSizeException 异常
     */
    public byte[] Decrypt( byte[] data ) throws BadPaddingException, IllegalBlockSizeException {
        if (data == null) return null;
        return decryptCipher.doFinal( data );
    }

    /**
     * 获取密钥信息
     * @return 密钥
     */
    public String getKey() {
        return this.key;
    }

    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";//默认的加密算法
    private Cipher encryptCipher = null;
    private Cipher decryptCipher = null;
    private String key;
}
