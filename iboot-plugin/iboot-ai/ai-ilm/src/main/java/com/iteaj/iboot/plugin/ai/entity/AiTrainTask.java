package com.iteaj.iboot.plugin.ai.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.mybatis.handler.FastjsonTypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * AI训练任务数据模型
 * @author aiots-dev
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ai_train_task")
public class AiTrainTask extends BaseEntity {

    /** 任务名称 */
    @TableField(value = "task_name", condition = SqlCondition.LIKE)
    private String taskName;

    /** 预训练模型ID */
    @TableField("model_id")
    private Long modelId;

    /** 数据集配置 */
    @TableField("dataset_id")
    private String datasetId;

    @TableField(value = "config", typeHandler = FastjsonTypeHandler.class)
    private JSONObject config;

    /** 任务状态(pending/running/paused/completed/failed/cancelled) */
    @TableField("status")
    private String status;

    /** 训练进度 */
    @TableField("progress")
    private Double progress;

    /** 当前轮次 */
    @TableField("current_epoch")
    private Integer currentEpoch;

    /** 总轮次 */
    @TableField("total_epochs")
    private Integer totalEpochs;

    /** 输入维度 */
    @TableField("input_dim")
    private Integer inputDim;

    /** 神经元失活概率 */
    @TableField("dropout")
    private Double dropout;

    /** 学习率 */
    @TableField("lr")
    private Double lr;

    /** 批尺寸 */
    @TableField("batch_size")
    private Integer batchSize;

    /** 类别数 */
    @TableField("num_classes")
    private Integer numClasses;

    /** 训练后的模型路径 */
    @TableField("trained_model_path")
    private String trainedModelPath;

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
