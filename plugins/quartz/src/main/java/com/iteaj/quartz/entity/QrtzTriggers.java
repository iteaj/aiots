package com.iteaj.quartz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;

import java.io.Serializable;
import java.sql.Blob;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author iteaj
 * @since 2020-07-26
 */
@Data
@Accessors(chain = true)
@TableName("qrtz_triggers")
public class QrtzTriggers implements Serializable {


    private String calendarName;

    private String description;

    private Long endTime;

    private String jobGroup;

    private String jobName;

    private Integer misfireInstr;

    private Long nextFireTime;

    private Long prevFireTime;

    private Integer priority;

    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    private Long startTime;

    private String triggerGroup;

    private String triggerName;

    private String triggerState;

    private String triggerType;

    private String cronExpression;
}
