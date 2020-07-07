package com.iteaj.core.entity;

import com.iteaj.framework.BaseEntity;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author iteaj
 * @since 2020-07-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MsgAccept extends BaseEntity {

    /**
     * 消息是否已读
     */
    private Boolean haveRead;

    /**
     * 消息发送id
     */
    private Long msgId;

    /**
     * 已读时间
     */
    private Date readTime;

    /**
     * 消息接收者用户
     */
    private Integer userId;


}
