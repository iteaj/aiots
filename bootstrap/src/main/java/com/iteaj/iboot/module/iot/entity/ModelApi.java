package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.util.List;

import com.iteaj.framework.mybatis.handler.JacksonTypeHandler;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 物模型接口
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_model_api")
public class ModelApi extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 接口类型
     */
    @NotNull(message = "接口类型必填")
    private ProtocolApiType type;

    /**
     * 是否为指令
     */
    private String direct;

    /**
     * 功能代码
     */
    @NotBlank(message = "接口代码必填")
    @TableField(condition = SqlCondition.LIKE)
    private String code;

    /**
     * 是否为协议接口
     */
    private Boolean protocol;

    /**
     * 所属产品
     */
    @NotNull(message = "所属产品必填")
    private Long productId;

    /**
     * 接口名称
     */
    @TableField(condition = SqlCondition.LIKE)
    private String name;

    /**
     * 接口状态
     */
    private String status;

    /**
     * 接口说明
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 指令参数
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private ObjectNode directParams;

    /**
     * 上行接口配置
     */
    @TableField(exist = false)
    private List<ModelApiConfig> upConfig;

    /**
     * 下行接口配置
     */
    @TableField(exist = false)
    private List<ModelApiConfig> downConfig;
}
