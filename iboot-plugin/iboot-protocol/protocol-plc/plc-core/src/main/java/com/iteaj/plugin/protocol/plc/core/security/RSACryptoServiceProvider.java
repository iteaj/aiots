package com.iteaj.plugin.protocol.plc.core.security;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSACryptoServiceProvider {

    public RSACryptoServiceProvider(){
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 初始化密钥对生成器，密钥大小为96-1024位
        assert keyPairGen != null;
        keyPairGen.initialize(1024,new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();
        privateKey = (RSAPrivateKey) keyPair.getPrivate();
        publicKey = (RSAPublicKey) keyPair.getPublic();
        privatePEMKey = privateKey.getEncoded();
        publicPEMKey = publicKey.getEncoded();
    }

    public RSACryptoServiceProvider(byte[] privatePEMKey, byte[] publicPEMKey) throws InvalidKeySpecException, NoSuchAlgorithmException {
        this.privatePEMKey = privatePEMKey;
        this.publicPEMKey = publicPEMKey;

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        if (this.privatePEMKey != null) {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privatePEMKey);
            this.privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        }
        if (this.publicPEMKey != null) {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicPEMKey);
            this.publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
        }
    }

    /**
     * 将指定的数据解密，如果解密密码，抛出异常
     * @param data 数据信息
     * @return 解密之后的数据
     */
    public byte[] DecryptLargeData( byte[] data ) throws Exception {
        return RSAHelper.DecryptLargeDataByRSA(privateKey, data);
    }

    /**
     * 将指定的数据加密
     * @param data 指定的数据信息
     * @return 加密之后的数据
     */
    public byte[] EncryptLargeData(byte[]data){
        try{
            return RSAHelper.EncryptLargeDataByRSA(publicKey, data);
        }
        catch (Exception ex){
            return null;
        }
    }

    /**
     * 获取PEM格式的公钥
     * @return 公钥
     */
    public byte[] GetPEMPublicKey( )
    {
        return publicPEMKey;
    }

    /**
     * 获取PEM格式的私钥
     * @return 私钥
     */
    public byte[] GetPEMPrivateKey( )
    {
        return privatePEMKey;
    }

    private byte[] privatePEMKey = null;
    private RSAPrivateKey privateKey = null;
    private byte[] publicPEMKey = null;
    private RSAPublicKey publicKey = null;

}
