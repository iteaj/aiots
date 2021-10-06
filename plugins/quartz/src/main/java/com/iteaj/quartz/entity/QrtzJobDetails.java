package com.iteaj.quartz.entity;

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
public class QrtzJobDetails implements Entity {


    private String description;

    private String isDurable;

    private String isNonconcurrent;

    private String isUpdateData;

    private String jobClassName;

    private String jobGroup;

    private String jobName;

    private String requestsRecovery;

    private String schedName;

    @Override
    public Serializable getId() {
        return null;
    }

    @Override
    public void setId(Serializable id) {

    }
}
