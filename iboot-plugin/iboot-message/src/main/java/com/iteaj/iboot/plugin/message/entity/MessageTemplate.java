package com.iteaj.iboot.plugin.message.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 消息模板
 * </p>
 *
 * @author iteaj
 * @since 2024-07-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_message_template")
public class MessageTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 短信模板id
     */
    private String templateId;

    /**
     * 模板标题
     */
    private String templateTitle;

    /**
     * 消息模板名称
     */
    private String templateName;

    /**
     * 接收人列表
     */
    private String accepts;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 备注
     */
    private String remark;

    private Date createTime;


}
