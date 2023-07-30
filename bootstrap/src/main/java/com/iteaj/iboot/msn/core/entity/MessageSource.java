package com.iteaj.iboot.msn.core.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 消息源
 * </p>
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

    private String signature;

    private String accessKeyId;

    private String accessKeySecret;

    /**
     * 创建时间
     */
    private Date createTime;


}
