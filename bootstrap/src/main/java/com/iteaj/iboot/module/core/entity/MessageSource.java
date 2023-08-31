package com.iteaj.iboot.module.core.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.mybatis.handler.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 消息源
 *
 * @author iteaj
 * @since 2023-07-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_message_source")
public class MessageSource extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 消息类型(email,sms,...)
     */
    private String type;

    /**
     * 消息源名称
     */
    private String name;

    /**
     * 通道类型
     */
    private String channel;

    /**
     * 额外配置
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ObjectNode config;

    /**
     * 创建时间
     */
    private Date createTime;

    public MessageSource() { }

    public MessageSource(String type) {
        this.type = type;
    }
}
