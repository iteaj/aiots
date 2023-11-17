package com.iteaj.iboot.module.iot.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;

import com.iteaj.framework.spi.iot.consts.ApiConfigDirection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
     * 报文开始位置
     */
    private String position;

    /**
     * 报文结束位置
     */
    private Integer length;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 接口方向
     */
    private ApiConfigDirection direction;

}
