package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.util.List;

import com.iteaj.iboot.module.core.enums.Status;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 场景联动
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_linkage")
public class Linkage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 场景名称
     */
    private String name;

    /**
     * 场景状态
     */
    private FuncStatus status;

    private Date createTime;

    /**
     * 所属事件源
     */
    private Long eventSourceId;

    /**
     * 事件源名称
     */
    @TableField(exist = false)
    private String eventSourceName;

    /**
     * 事件源状态
     */
    @TableField(exist = false)
    private FuncStatus eventSourceStatus;

    /**
     * 场景联动项
     */
    @TableField(exist = false)
    private List<LinkageItem> items;

    public String getStatusName() {
        return this.getStatus() != null ? this.status.getDesc() : null;
    }

    public String getEventSourceStatusName() {
        return this.getEventSourceStatus() != null ? this.eventSourceStatus.getDesc() : null;
    }
}
