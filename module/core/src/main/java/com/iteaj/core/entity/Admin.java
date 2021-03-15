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

    public Sex getSex() {
        return sex;
    }

    public Admin setSex(Sex sex) {
        this.sex = sex;
        return this;
    }

    public Long getOrgId() {
        return orgId;
    }

    public Admin setOrgId(Long orgId) {
        this.orgId = orgId;
        return this;
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

    public Admin setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Admin setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getAvatar() {
        return avatar;
    }

    public Admin setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public Admin setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public String getAccount() {
        return account;
    }

    public Admin setAccount(String account) {
        this.account = account;
        return this;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public Admin setLoginIp(String loginIp) {
        this.loginIp = loginIp;
        return this;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public Admin setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Admin setPassword(String password) {
        this.password = password;
        return this;
    }

    public UseStatus getStatus() {
        return status;
    }

    public Admin setStatus(UseStatus status) {
        this.status = status;
        return this;
    }

    public boolean isSuperUser() {
        return this.getId() == 1l;
    }
}
