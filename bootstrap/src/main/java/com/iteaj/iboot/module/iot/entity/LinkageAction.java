package com.iteaj.iboot.module.iot.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 联动动作
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_linkage_action")
public class LinkageAction extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 所属场景
     */
    private Long linkageId;

    /**
     * 物模型接口id
     */
    private Long modelApiId;

    /**
     * 物模型接口码
     */
    private String modelApiCode;

    /**
     * 物模型接口配置
     */
    private String modelApiConfig;


}
