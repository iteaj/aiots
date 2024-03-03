package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.iboot.module.iot.consts.LinkageLogical;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

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
    private LinkageLogical logical;

    /**
     * 所属联动项
     */
    private Long linkageItemId;

    /**
     * 动作id(属于动作条件)
     */
    private Long linkageActionId;

    /**
     * 场景条件项
     */
    @TableField(exist = false)
    private List<LinkageConditionItem> items;
}
