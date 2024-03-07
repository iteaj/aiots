package com.iteaj.iboot.module.iot.entity;

import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.iboot.module.iot.consts.ConditionType;
import com.iteaj.iboot.module.iot.consts.LinkageLogical;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 场景条件项
 * </p>
 *
 * @author iteaj
 * @since 2024-02-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_linkage_condition_item")
public class LinkageConditionItem extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 所属条件
     */
    private Long linkageConditionId;

    /**
     * 逻辑
     */
    private LinkageLogical logical;

    /**
     * 操作设备(-1表示所有设备)
     */
    private String uid;

    /**
     * 条件类型
     */
    private ConditionType type;

    /**
     * 指令
     */
    private String direct;

    /**
     * 条件值
     */
    private String value;

    /**
     * 运算符
     */
    private String operator;

    /**
     * 所属产品
     */
    private Long productId;

    /**
     * 物模型接口
     */
    private Long modelApiId;

    /**
     * 协议指令配置
     */
    private String modelApiConfig;

    /**
     * 事件源详情
     */
    private Long eventSourceDetailId;

}
