package com.iteaj.quartz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2020-07-26
 */
@Data
@Accessors(chain = true)
@TableName("qrtz_simple_triggers")
public class QrtzSimpleTriggers {


    private Long repeatCount;

    private Long repeatInterval;

    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    private Long timesTriggered;

    private String triggerGroup;

    private String triggerName;


}
