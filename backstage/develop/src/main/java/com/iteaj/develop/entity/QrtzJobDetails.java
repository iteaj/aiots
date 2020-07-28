package com.iteaj.develop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.iteaj.framework.BaseEntity;
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
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class QrtzJobDetails extends BaseEntity {


    private String description;

    private String isDurable;

    private String isNonconcurrent;

    private String isUpdateData;

    private String jobClassName;

    private String jobGroup;

    private String jobName;

    private String requestsRecovery;

    @TableId(value = "SCHED_NAME", type = IdType.AUTO)
    private String schedName;


}
