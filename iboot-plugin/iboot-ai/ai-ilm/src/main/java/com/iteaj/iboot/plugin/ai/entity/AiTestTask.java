package com.iteaj.iboot.plugin.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.iteaj.framework.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * AI模型测试任务数据模型
 * @author aiots-dev
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ai_test_task")
public class AiTestTask extends BaseEntity {

    /** 任务名称 */
    @TableField(value = "task_name", condition = SqlCondition.LIKE)
    private String taskName;

    /** 训练任务ID */
    @TableField("train_task_id")
    private Long trainTaskId;

    /** 测试状态(pending/running/completed/failed) */
    @TableField("status")
    private String status;

    /** 准确率 */
    @TableField("accuracy")
    private Double accuracy;

    /** 精确率 */
    @TableField("precision_rate")
    private Double precisionRate;

    /** 召回率 */
    @TableField("recall")
    private Double recall;

    /** 测试报告 */
    @TableField("test_report")
    private String testReport;

    /** 开始时间 */
    @TableField("start_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @TableField("end_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 错误信息 */
    @TableField("error_msg")
    private String errorMsg;

    /** 备注 */
    @TableField("remark")
    private String remark;

    /** 创建时间 */
    @TableField("create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新时间 */
    @TableField("update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
