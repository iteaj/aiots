package com.iteaj.msn.core.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iteaj.msn.core.enums.Sex;
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
@TableName("t_admin")
@Accessors(chain = true)
public class Admin extends BaseEntity {

    /*性别*/
    private Sex sex;
    /*所属部门*/
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
    @JsonIgnore
    private String password;
    /*账号使用状态*/
    private UseStatus status;

    /**创建时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**更新时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    public boolean isSuperUser() {
        return this.getId() == 1l;
    }
}

