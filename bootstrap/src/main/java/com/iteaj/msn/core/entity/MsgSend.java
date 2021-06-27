package com.iteaj.msn.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.framework.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author iteaj
 * @since 2020-07-07
 */
@Data
@TableName("t_msg_send")
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

    /**创建时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**更新时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;
}
