package com.iteaj.iboot.module.iot.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.iboot.module.iot.consts.ConditionType;
import com.iteaj.iboot.module.iot.consts.LinkageLogical;
import com.iteaj.iboot.module.iot.consts.OperatorType;
import com.iteaj.iboot.module.iot.linkage.ConditionCalc;
import com.iteaj.iboot.module.iot.linkage.ConditionalsManager;
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
     * 操作设备(-1表示组所有设备)
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
    private String operator;

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
     * 连接id
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
     * 模型属性的数据类型
     */
    @TableField(exist = false)
    private String dataType;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 事件源详情
     */
    private Long eventSourceDetailId;

    public static final String[] DEVICE_ANY = {"-1", "各组任一此产品下的设备"};
    public static final String[] DEVICE_TRIGGER = {"-2", "当前触发设备"};
    public static final String[] DEVICE_ONLINE = {"-1", "设备上线", "status::online"};
    public static final String[] DEVICE_OFFLINE = {"-2", "设备离线", "status::offline"};

    public String getLogicalName() {
        return logical != null ? logical.getDesc() : "";
    }

    public String getLinkedName() {
        String linkedId = this.linkedId + "";
        if(DEVICE_ONLINE[0].equals(linkedId)) {
            return DEVICE_ONLINE[1];
        } else if(DEVICE_OFFLINE[0].equals(linkedId)) {
            return DEVICE_OFFLINE[1];
        }

        return linkedName;
    }

    /**
     * 运算符名称
     * @return
     */
    public String getOperatorName() {
        if(StrUtil.isNotBlank(this.getOperator())) {
            ConditionCalc conditionCalc = ConditionalsManager.get(this.getOperator());
            if(conditionCalc != null) {
                return conditionCalc.getName();
            }
        }

        return getOperator();
    }

    public String[] getMergeValue() {
        return typeValue != null ? new String[] {typeValue} : new String[]{};
    }

    public String getDeviceName() {
        if(this.deviceName == null) {
            if(Objects.equals(this.uid, DEVICE_ANY[0])) {
                return DEVICE_ANY[1];
            } else if(Objects.equals(this.uid, DEVICE_TRIGGER[0])) {
                return DEVICE_TRIGGER[1];
            }
        }

        return this.deviceName;
    }
}
