package com.iteaj.framework.spi.oauth2;

/**
 * create time: 2021/3/25
 *
 * @author iteaj
 * @since 1.0
 */
public class OAuth2Token {
    private String accessToken;
    private int expireIn;
    private String refreshToken;
    private int refreshTokenExpireIn;
    private String uid;
    private String openId;
    private String accessCode;
    private String unionId;

    /**
     * 小米附带属性
     */
    private String macAlgorithm;
    private String macKey;

    /**
     * 企业微信附带属性
     *
     * @since 1.10.0
     */
    private String code;

    public String getAccessToken() {
        return accessToken;
    }

    public OAuth2Token setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public int getExpireIn() {
        return expireIn;
    }

    public OAuth2Token setExpireIn(int expireIn) {
        this.expireIn = expireIn;
        return this;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public OAuth2Token setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    public int getRefreshTokenExpireIn() {
        return refreshTokenExpireIn;
    }

    public OAuth2Token setRefreshTokenExpireIn(int refreshTokenExpireIn) {
        this.refreshTokenExpireIn = refreshTokenExpireIn;
        return this;
    }

    public String getUid() {
        return uid;
    }

    public OAuth2Token setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getOpenId() {
        return openId;
    }

    public OAuth2Token setOpenId(String openId) {
        this.openId = openId;
        return this;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public OAuth2Token setAccessCode(String accessCode) {
        this.accessCode = accessCode;
        return this;
    }

    public String getUnionId() {
        return unionId;
    }

    public OAuth2Token setUnionId(String unionId) {
        this.unionId = unionId;
        return this;
    }

    public String getMacAlgorithm() {
        return macAlgorithm;
    }

    public OAuth2Token setMacAlgorithm(String macAlgorithm) {
        this.macAlgorithm = macAlgorithm;
        return this;
    }

    public String getMacKey() {
        return macKey;
    }

    public OAuth2Token setMacKey(String macKey) {
        this.macKey = macKey;
        return this;
    }

    public String getCode() {
        return code;
    }

    public OAuth2Token setCode(String code) {
        this.code = code;
        return this;
    }
}
