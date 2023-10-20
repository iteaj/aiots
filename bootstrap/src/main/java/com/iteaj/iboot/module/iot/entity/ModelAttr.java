package com.iteaj.iboot.module.iot.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
    private Long productId;

    /**
     * 属性代码
     */
    private String field;

    /**
     * 属性名称
     */
    private String name;

    /**
     * 属性类型
     */
    private String type;

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


}
