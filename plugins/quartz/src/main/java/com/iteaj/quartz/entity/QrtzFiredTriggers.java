package com.iteaj.quartz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class QrtzFiredTriggers extends BaseEntity {


    private String entryId;

    private Long firedTime;

    private String instanceName;

    private String isNonconcurrent;

    private String jobGroup;

    private String jobName;

    private Integer priority;

    private String requestsRecovery;

    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;

    private Long schedTime;

    private String state;

    private String triggerGroup;

    private String triggerName;


}
