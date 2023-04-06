package com.iteaj.iboot.msn.core.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iteaj.iboot.msn.core.enums.Sex;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.consts.UseStatus;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 系统管理员
 */
@Data
@TableName("sys_admin")
@Accessors(chain = true)
public class Admin extends BaseEntity {

    /*性别*/
    @Excel(name = "性别", orderNum = "60", dict = "core_sex")
    private String sex;

    /*所属部门*/
    @Excel(name = "部门", orderNum = "40", dict = "org_id")
    private Long orgId;

    /*用户昵称*/
    @TableField(condition = SqlCondition.LIKE)
    @Excel(name = "用户名", orderNum = "10")
    private String name;

    @Excel(name = "邮箱", orderNum = "50")
    private String email;

    @Excel(name = "手机号", orderNum = "30")
    @TableField(condition = SqlCondition.LIKE)
    private String phone;

    /*用户头像*/
    private String avatar;

    private String remark;

    /*用户账号, 不进行更新操作*/
    @TableField(updateStrategy = FieldStrategy.NEVER)
    @Excel(name = "用户账号", orderNum = "20")
    private String account;

    /*登录ip*/
    private String loginIp;

    /*登录时间*/
    private Date loginDate;

    /*密码, 不进行密码更新*/
    @JsonIgnore
    private String password;

    /*账号使用状态*/
    @Excel(name = "用户账号", orderNum = "70")
    private UseStatus status;


    @Excel(name = "创建时间", orderNum = "90", exportFormat = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private Date createTime;

    /**更新时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    public boolean isSuperUser() {
        return this.getId() == 1l;
    }
}

