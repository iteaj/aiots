package com.iteaj.msn.core.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 消息
 * </p>
 *
 * @author iteaj
 * @since 2021-09-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_msg_send")
public class MsgSend extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 标题
     */
    private String title;

    /**
     * 发送者
     */
    private Integer userId;

    /**
     * 发送人昵称
     */
    private String userName;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 消息内容
     */
    private String content;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
