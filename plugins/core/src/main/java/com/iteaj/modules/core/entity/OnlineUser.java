package com.iteaj.modules.core.entity;

import com.iteaj.modules.core.enums.AppType;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.spi.OnlineSession;

import java.util.Date;

/**
 * <p>
 * 在线用户
 * </p>
 *
 * @author iteaj
 * @since 2020-06-20
 */
public class OnlineUser extends BaseEntity implements OnlineSession {

    private String account;

    /**
     * 会话编号
     */
    private String sessionId;

    /**
     * 登录时使用的浏览器
     */
    private String browse;

    /**
     * 超时时长, 从访问到离开的时间， 单位分钟
     */
    private Long expireTime;

    /**
     * 访问的本地位置
     */
    private String location;

    /**
     * 访问ip
     */
    private String accessIp;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 使用的操作系统
     */
    private String os;

    /**
     * 应用类型
     */
    private AppType appType;

    /**
     * 用户状态(On. 在线, Off. 离线)
     */
    private String status;

    /**
     * 用户昵称
     */
    private String userNick;

    public OnlineUser(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getBrowse() {
        return browse;
    }

    public OnlineUser setBrowse(String browse) {
        this.browse = browse;
        return this;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public OnlineUser setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
        return this;
    }

    public String getLocation() {
        return location;
    }

    public OnlineUser setLocation(String location) {
        this.location = location;
        return this;
    }

    public String getAccessIp() {
        return accessIp;
    }

    public OnlineUser setAccessIp(String accessIp) {
        this.accessIp = accessIp;
        return this;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public OnlineUser setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
        return this;
    }

    public String getOs() {
        return os;
    }

    public OnlineUser setOs(String os) {
        this.os = os;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public OnlineUser setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getUserNick() {
        return userNick;
    }

    public OnlineUser setUserNick(String userNick) {
        this.userNick = userNick;
        return this;
    }

    @Override
    public OnlineUser setCreateTime(Date createTime) {
        return (OnlineUser) super.setCreateTime(createTime);
    }

    @Override
    public String toString() {
        return "OnlineUser{" +
        "browse=" + browse +
        ", expireTime=" + expireTime +
        ", location=" + location +
        ", loginIp=" + accessIp +
        ", loginTime=" + loginTime +
        ", os=" + os +
        ", status=" + status +
        ", userNick=" + userNick +
        "}";
    }

    @Override
    public String getAccount() {
        return this.account;
    }

    public OnlineUser setAccount(String account) {
        this.account = account;
        return this;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    public OnlineUser setSessionId(String sessionId) {
        this.sessionId = sessionId;
        return this;
    }

    @Override
    public long getTimeout() {
        return getExpireTime();
    }

    @Override
    public Date getStartTimestamp() {
        return getCreateTime();
    }

    @Override
    public Date getLastAccessTime() {
        return getUpdateTime();
    }

    public AppType getAppType() {
        return appType;
    }

    public OnlineUser setAppType(AppType appType) {
        this.appType = appType;
        return this;
    }
}
