package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 物模型属性字典
 */
@Data
@Accessors(chain = true)
@TableName("iot_model_attr_dict")
public class ModelAttrDict extends BaseEntity {

    /**
     * 字典名称
     */
    @NotBlank(message = "字典名称必填")
    @TableField(condition = SqlCondition.LIKE)
    private String dictName;

    /**
     * 字典值
     */
    @NotBlank(message = "字典值必填")
    private String dictValue;

    /**
     * 模型属性id
     */
    @NotNull(message = "模型属性必填")
    @TableField(whereStrategy = FieldStrategy.IGNORED)
    private Long modelAttrId;
}
