package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.util.List;

import com.iteaj.iboot.module.iot.consts.EventSourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 事件源
 * </p>
 *
 * @author iteaj
 * @since 2024-02-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_event_source")
public class EventSource extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 事件类型
     */
    private EventSourceType type;

    /**
     * 采集周期
     */
    private String cron;

    /**
     * 事件名称
     */
    private String name;

    /**
     * 有没有被动事件
     */
    private Boolean hasPassive;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 所属设备组
     */
    private Long deviceGroupId;

    /**
     * 模型接口列表
     */
    @TableField(exist = false)
    private List<String> modelApiIds;
}
