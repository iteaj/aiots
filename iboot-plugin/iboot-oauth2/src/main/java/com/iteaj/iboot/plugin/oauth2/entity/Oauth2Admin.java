package com.iteaj.iboot.plugin.oauth2.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.consts.UseStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class Oauth2Admin extends BaseEntity {
    /**
     * 性别
     */
    private String sex;

    /**
     * 所属部门
     */
    private Long orgId;

    /**
     * 所属岗位
     */
    private Long postId;

    /**
     * 用户昵称
     */
    @TableField(condition = SqlCondition.LIKE)
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    @TableField(condition = SqlCondition.LIKE)
    private String phone;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 备注
     */
    private String remark;

    /**
     * 用户账号
     */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String account;

    /**
     * 密码
     */
    @JSONField(serialize = false)
    private String password;

    /**
     * 用户状态(enabled, disabled)
     */
    private UseStatus status;

    /**
     * 角色列表
     */
    private List<String> roles;


}
