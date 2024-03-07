package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.validate.AllGroup;
import com.iteaj.iboot.module.iot.consts.AttrOrigin;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 物模型属性
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_model_attr")
public class ModelAttr extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 所属产品
     */
    @NotNull(message = "所属产品必填")
    private Long productId;

    /**
     * 属性代码
     */
    @TableField(condition = SqlCondition.LIKE)
    @NotBlank(message = "属性字段必填")
    private String field;

    /**
     * 属性名称
     */
    @TableField(condition = SqlCondition.LIKE)
    @NotBlank(message = "属性名称必填")
    private String name;

    /**
     * 属性类型
     */
    @NotBlank(message = "属性类型必填")
    private String type;

    /**
     * 实际类型
     */
    private String realType;

    /**
     * 是否枚举
     */
    private Boolean enumerate;

    /**
     * 单位
     */
    private String unit;

    /**
     * 精度
     */
    private Integer accuracy;

    /**
     * 描述
     */
    private String remark;

    /**
     * 属性来源
     */
    @NotNull(message = "属性来源必填")
    private AttrOrigin origin;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 属性字典列表
     */
    @TableField(exist = false)
    private List<ModelAttrDict> dicts;
}
