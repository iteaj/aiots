package com.iteaj.msn.core.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * create time: 2020/4/22
 *
 * @author iteaj
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@TableName("sys_access_log")
public class AccessLog extends BaseEntity {

    /**
     * 请求ip
     */
    private String ip;
    /**
     * 请求地址
     */
    private String url;
    /**
     * 所属模块
     */
    private String msn;
    /**
     * 功能类型
     */
    private String type;
    /**
     * 访问用户
     */
    private Long userId;
    /**
     * 使用时间
     */
    @TableField(condition = "%s >= #{%s}")
    private Long millis;
    /**
     * 用户名
     */
    @TableField(condition = SqlCondition.LIKE)
    private String userName;

    /**
     * 状态
     */
    private Boolean status;
    /**
     * 错误消息
     */
    private String errMsg;

    /**
     * 功能
     */
    private String title;

    /**
     * 执行时间
     */
    private Date createTime;

    public AccessLog() { }

    public AccessLog(String type, String userName) {
        this.type = type;
        this.userName = userName;
    }

}
