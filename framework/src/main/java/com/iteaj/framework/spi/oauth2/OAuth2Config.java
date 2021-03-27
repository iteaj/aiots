package com.iteaj.framework.spi.oauth2;

import java.io.Serializable;
import java.util.List;

/**
 * create time: 2021/3/25
 *
 * @author iteaj
 * @since 1.0
 */
public class OAuth2Config implements Serializable {
    /**
     * 客户端id：对应各平台的appKey
     */
    private String clientId;

    /**
     * 客户端Secret：对应各平台的appSecret
     */
    private String clientSecret;

    /**
     * 支付宝公钥：当选择支付宝登录时，该值可用
     * 对应“RSA2(SHA256)密钥”中的“支付宝公钥”
     */
    private String alipayPublicKey;

    /**
     * 是否需要申请unionid，目前只针对qq登录
     * 注：qq授权登录时，获取unionid需要单独发送邮件申请权限。如果个人开发者账号中申请了该权限，可以将该值置为true，在获取openId时就会同步获取unionId
     * 参考链接：http://wiki.connect.qq.com/unionid%E4%BB%8B%E7%BB%8D
     * <p>
     */
    private boolean unionId;

    /**
     * 企业微信，授权方的网页应用ID
     *
     */
    private String agentId;

    /**
     * 授权域, 详情请看(me.zhyd.oauth.enums.scope.AuthScope)
     */
    private List<String> scopes;

    public String getClientId() {
        return clientId;
    }

    public OAuth2Config setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public OAuth2Config setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public boolean isUnionId() {
        return unionId;
    }

    public OAuth2Config setUnionId(boolean unionId) {
        this.unionId = unionId;
        return this;
    }

    public String getAgentId() {
        return agentId;
    }

    public OAuth2Config setAgentId(String agentId) {
        this.agentId = agentId;
        return this;
    }

    public String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public OAuth2Config setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
        return this;
    }

    public List<String> getScopes() {
        return scopes;
    }

    public OAuth2Config setScopes(List<String> scopes) {
        this.scopes = scopes;
        return this;
    }

    @Override
    public String toString() {
        return "OAuth2Config{" +
                "clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", alipayPublicKey='" + alipayPublicKey + '\'' +
                ", unionId=" + unionId +
                ", agentId='" + agentId + '\'' +
                '}';
    }
}
