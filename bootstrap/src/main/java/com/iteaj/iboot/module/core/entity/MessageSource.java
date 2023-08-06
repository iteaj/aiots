package com.iteaj.iboot.module.core.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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

    private String signature;

    private String accessKeyId;

    private String accessKeySecret;
    /**
     * 发件人邮件地址
     */
    private String fromAddress;

    /**
     * smtp服务器地址
     */
    private String smtpServer;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间
     */
    private Date createTime;


}
