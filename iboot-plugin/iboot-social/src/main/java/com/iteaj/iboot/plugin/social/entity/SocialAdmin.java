package com.iteaj.iboot.plugin.social.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 管理端用户（映射 sys_admin 表，仅用于社交平台登录自动注册）
 * @author iteaj
 */
@Data
@Accessors(chain = true)
@TableName("sys_admin")
public class SocialAdmin {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户账号 */
    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String account;

    /** 密码（MD5） */
    private String password;

    /** 用户昵称 */
    private String name;

    /** 用户头像 */
    private String avatar;

    /** 用户状态(enabled, disabled) */
    private String status;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
