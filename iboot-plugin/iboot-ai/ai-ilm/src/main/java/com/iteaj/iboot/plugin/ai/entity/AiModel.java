package com.iteaj.iboot.plugin.ai.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * AI模型数据模型
 * @author aiots-dev
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ai_model")
public class AiModel extends BaseEntity {

    /** 模型名称 */
    @TableField(value = "name", condition = SqlCondition.LIKE)
    private String name;

    /** 模型类型 */
    @TableField("model_type")
    private String modelType;

    /** 模态 */
    @TableField("modality")
    private String modality;

    /** 任务类型 */
    @TableField("task_type")
    private String taskType;

    /** 模型文件路径 */
    @TableField("model_path")
    private String modelPath;

    /** 版本号 */
    @TableField("version")
    private String version;

    /** 状态 */
    @TableField("status")
    private String status;

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
