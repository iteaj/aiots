package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import com.iteaj.framework.spi.iot.consts.ApiConfigDirection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 物模型接口配置
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_model_api_config")
public class ModelApiConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 接口码
     */
    private String apiCode;

    /**
     * 物模型属性字段
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String attrField;

    /**
     * 所属产品
     */
    private Long productId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 配置值
     */
    private String value;

    /**
     * 模型属性id
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long modelAttrId;

    /**
     * 协议属性字段
     */
    private String protocolAttrField;

    /**
     * 协议属性名称
     */
    private String protocolAttrName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 接口方向
     */
    private ApiConfigDirection direction;

    /**
     * 属性字典列表
     */
    @TableField(exist = false)
    private List<ModelAttrDict> dicts;

}
