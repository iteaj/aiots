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
 * AI模型参数数据模型
 * @author aiots-dev
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ai_model_param")
public class AiModelParam extends BaseEntity {

    /** 模型ID */
    @TableField("model_id")
    private Long modelId;

    /** 参数名称 */
    @TableField("param_name")
    private String paramName;

    /** 参数键 */
    @TableField("param_key")
    private String paramKey;

    /** 参数值 */
    @TableField("param_value")
    private String paramValue;

    /** 备注 */
    @TableField("remark")
    private String remark;

    /** 排序 */
    @TableField("sort")
    private Integer sort;

    /** 创建时间 */
    @TableField("create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 更新时间 */
    @TableField("update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
