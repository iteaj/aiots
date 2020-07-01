package com.iteaj.core.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.core.enums.Sex;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.consts.UseStatus;

import java.util.Date;

/**
 * 系统管理员
 */
@TableName
public class Admin extends BaseEntity {

    /*性别*/
    private Sex sex;
    /*组织机构id*/
    private Long orgId;
    /*用户昵称*/
    @TableField(condition = SqlCondition.LIKE)
    private String name;
    private String email;
    @TableField(condition = SqlCondition.LIKE)
    private String phone;
    /*用户头像*/
    private String avatar;
    private String remark;
    /*用户账号, 不进行更新操作*/
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String account;
    /*登录ip*/
    private String loginIp;
    /*登录时间*/
    private Date loginDate;
    /*密码, 不进行密码更新*/
    private String password;
    /*账号使用状态*/
    private UseStatus status;

    public Long getOrgId() {
        return orgId;
    }

    public Admin setOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public Admin setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRemark() {
        return remark;
    }

    public Admin setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public UseStatus getStatus() {
        return status;
    }

    public void setStatus(UseStatus status) {
        this.status = status;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public boolean isSuperUser() {
        return this.getId() == 1l;
    }
}
