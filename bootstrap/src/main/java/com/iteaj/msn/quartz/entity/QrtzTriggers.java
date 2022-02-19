package com.iteaj.msn.quartz.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.Entity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.JdbcType;
import org.quartz.JobDataMap;

/**
 * @author iteaj
 * @since 2020-07-26
 */
@Data
@Accessors(chain = true)
@TableName(value = "qrtz_triggers", resultMap="OriMap")
public class QrtzTriggers implements Entity<String> {

    @TableId
    private String jobName;
    private String calendarName;
    private String description;

    private Long endTime;

    private String jobGroup;

    private Integer misfireInstr;

    private Long nextFireTime;

    private Long prevFireTime;

    private Integer priority;

    private String schedName;

    private Long startTime;

    private String triggerGroup;

    private String triggerName;

    private String triggerState;

    private String triggerType;

    @TableField(exist = false)
    private String cron;

    /**
     * 执行方法字符串
     */
    @TableField(exist = false)
    private String execMethod;

    /**
     * 是否并发执行
     */
    private boolean concurrent;

    /**
     * 执行参数
     */
    @TableField(value = "job_data", exist = false)
    private JobDataMap params;

    @Override
    public String getId() {
        return jobName;
    }

    @Override
    public void setId(String id) {
        this.jobName = id;
    }
}
