package com.iteaj.core.entity;

import com.iteaj.framework.BaseEntity;
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
public class MsgSend extends BaseEntity {


    /**
     * 消息内容
     */
    private String content;

    /**
     * 优先级 normal、high、special
     */
    private String priority;

    /**
     * 标题
     */
    private String title;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 发送者
     */
    private Integer userId;

    /**
     * 发送人昵称
     */
    private String userName;


}
