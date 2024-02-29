package com.iteaj.iboot.module.iot.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 联动条件
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_linkage_condition")
public class LinkageCondition extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 所属场景
     */
    private Long linkageId;

    /**
     * 逻辑
     */
    private String logical;

    /**
     * 动作id(说明属于动作条件)
     */
    private Long linkageActionId;


}
