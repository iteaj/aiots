package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.iboot.module.iot.consts.ConditionType;
import com.iteaj.iboot.module.iot.consts.LinkageLogical;
import com.iteaj.iboot.module.iot.consts.OperatorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Objects;

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
     * 序号
     */
    private Integer sort;

    /**
     * 操作设备名称
     */
    @TableField(exist = false)
    private String deviceName;

    /**
     * 条件类型
     */
    private ConditionType type;

    /**
     * 类型值(事件接口码, 设备上下线状态)
     */
    private String typeValue;

    /**
     * 指令
     */
    @TableField(exist = false)
    private String direct;

    /**
     * 条件值
     */
    private String value;

    /**
     * 运算符
     */
    private OperatorType operator;

    /**
     * 所属产品
     */
    private Long productId;

    /**
     * 产品代码
     */
    @TableField(exist = false)
    private String productCode;

    /**
     * 产品名称
     */
    @TableField(exist = false)
    private String productName;

    /**
     * 物模型接口
     */
    private Long linkedId;

    /**
     * 协议代码
     */
    @TableField(exist = false)
    private String protocolCode;

    /**
     * 物模型接口名称
     */
    @TableField(exist = false)
    private String linkedName;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 事件源详情
     */
    private Long eventSourceDetailId;

    public String getLogicalName() {
        return logical != null ? logical.getDesc() : "";
    }

    public String getOperatorName() {
        return operator != null ? operator.getDesc() : "";
    }

    public String[] getMergeValue() {
        return typeValue != null ? new String[] {typeValue} : new String[]{};
    }

    public String getDeviceName() {
        if(this.deviceName == null) {
            if(Objects.equals(this.uid, "-1")) {
                return "组全部设备";
            }
        }

        return this.deviceName;
    }
}
