package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
    private String type;

    /**
     * 功能代码
     */
    private String code;

    /**
     * 所属产品
     */
    private Long productId;

    /**
     * 接口名称
     */
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
