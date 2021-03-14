package com.iteaj.modules.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;

/**
 * create time: 2020/4/22
 *
 * @author iteaj
 * @since 1.0
 */
public class OperaLog extends BaseEntity {

    private String ip;
    private String url;
    private String msn;
    private String type;
    private Long userId;
    @TableField(condition = "%s >= #{%s}")
    private Long millis;
    private String userName;
    private String shortName;
    private String status;
    private String errMsg;
    private String title;

    public OperaLog() {

    }

    public OperaLog(String type, String userName, String shortName, String status) {
        this.type = type;
        this.userName = userName;
        this.shortName = shortName;
        this.status = status;
    }

    public String getIp() {
        return ip;
    }

    public OperaLog setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public OperaLog setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getType() {
        return type;
    }

    public OperaLog setType(String type) {
        this.type = type;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public OperaLog setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public OperaLog setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getShortName() {
        return shortName;
    }

    public OperaLog setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public OperaLog setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public OperaLog setErrMsg(String errMsg) {
        this.errMsg = errMsg;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public OperaLog setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMsn() {
        return msn;
    }

    public OperaLog setMsn(String msn) {
        this.msn = msn;
        return this;
    }

    public Long getMillis() {
        return millis;
    }

    public OperaLog setMillis(Long millis) {
        this.millis = millis;
        return this;
    }
}
