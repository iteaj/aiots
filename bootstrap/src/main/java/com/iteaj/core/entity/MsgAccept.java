package com.iteaj.core.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.framework.BaseEntity;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 *
 * </p>
 *
 * @author iteaj
 * @since 2020-07-07
 */
@Data
@ApiModel
@TableName("t_msg_accept")
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

    /**创建时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /**更新时间*/
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

}
