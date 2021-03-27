package com.iteaj.framework.spi.oauth2;
import com.alibaba.fastjson.JSONObject;

/**
 * create time: 2021/3/26
 *
 * @author iteaj
 * @since 1.0
 */
public class OAuth2User {
    /**
     * 用户第三方系统的唯一id。在调用方集成该组件时，可以用uuid + source唯一确定一个用户
     *  即openid
     */
    private String uuid;
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户昵称
     */
    private String nickname;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 用户网址
     */
    private String blog;
    /**
     * 所在公司
     */
    private String company;
    /**
     * 位置
     */
    private String location;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 用户备注（各平台中的用户个人介绍）
     */
    private String remark;

    /**
     * 用户来源
     */
    private String source;
    /**
     * 用户授权的token信息
     */
    private OAuth2Token token;
    /**
     * 第三方平台返回的原始用户信息
     */
    private JSONObject rawUserInfo;

    public String getUuid() {
        return uuid;
    }

    public OAuth2User setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public OAuth2User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getNickname() {
        return nickname;
    }

    public OAuth2User setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public OAuth2User setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public String getBlog() {
        return blog;
    }

    public OAuth2User setBlog(String blog) {
        this.blog = blog;
        return this;
    }

    public String getCompany() {
        return company;
    }

    public OAuth2User setCompany(String company) {
        this.company = company;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public OAuth2User setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public OAuth2User setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public OAuth2User setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public String getSource() {
        return source;
    }

    public OAuth2User setSource(String source) {
        this.source = source;
        return this;
    }

    public OAuth2Token getToken() {
        return token;
    }

    public OAuth2User setToken(OAuth2Token token) {
        this.token = token;
        return this;
    }

    public JSONObject getRawUserInfo() {
        return rawUserInfo;
    }

    public OAuth2User setRawUserInfo(JSONObject rawUserInfo) {
        this.rawUserInfo = rawUserInfo;
        return this;
    }
}
