package com.iteaj.msn.quartz.entity;

import com.iteaj.framework.Entity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
public class QrtzFiredTriggers implements Entity {


    private String entryId;

    private Long firedTime;

    private String instanceName;

    private String isNonconcurrent;

    private String jobGroup;

    private String jobName;

    private Integer priority;

    private String requestsRecovery;

    private String schedName;

    private Long schedTime;

    private String state;

    private String triggerGroup;

    private String triggerName;


    @Override
    public Serializable getId() {
        return null;
    }

    @Override
    public void setId(Serializable id) {

    }
}
