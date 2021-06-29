package com.iteaj.msn.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * create time: 2020/4/22
 *
 * @author iteaj
 * @since 1.0
 */
@Data
@Accessors(chain = true)
@TableName("t_access_log")
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
    private String userName;
    private String shortName;

    private String status;
    /**
     * 错误消息
     */
    private String errMsg;

    private String title;

    public AccessLog() { }

    public AccessLog(String type, String userName, String shortName, String status) {
        this.type = type;
        this.userName = userName;
        this.shortName = shortName;
        this.status = status;
    }

}
