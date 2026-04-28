package com.iteaj.iboot.plugin.ai.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * AI训练指标数据模型
 * @author aiots-dev
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ai_train_metric")
public class AiTrainMetric extends BaseEntity {

    /** 任务ID */
    @TableField("task_id")
    private Long taskId;

    /** 轮次 */
    @TableField("epoch")
    private Integer epoch;

    /** 训练损失 */
    @TableField("train_loss")
    private Double trainLoss;

    /** 验证损失 */
    @TableField("val_loss")
    private Double valLoss;

    /** 训练准确率 */
    @TableField("train_acc")
    private Double trainAcc;

    /** 验证准确率 */
    @TableField("val_acc")
    private Double valAcc;

    /** 学习率 */
    @TableField("learning_rate")
    private Double learningRate;

    /** 记录时间 */
    @TableField("record_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recordTime;
}
