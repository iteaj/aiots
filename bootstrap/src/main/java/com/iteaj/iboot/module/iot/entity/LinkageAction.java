package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.framework.BaseEntity;
import com.iteaj.iboot.module.iot.consts.ActionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Objects;

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
     * 设备uid(-1全部, -2当前设备)
     */
    private String uid;

    /**
     * 设备名称
     */
    @TableField(exist = false)
    private String deviceName;

    /**
     * 动作类型
     */
    private ActionType type;

    /**
     * 所属场景
     */
    private Long linkageId;

    /**
     * 动作id(告警id或物模型接口id)
     */
    private Long linkedId;

    /**
     * 动作名称
     */
    @TableField(exist = false)
    private String linkedName;

    /**
     * 所属产品
     */
    private Long productId;

    /**
     * 产品名称
     */
    @TableField(exist = false)
    private String productName;

    /**
     * 所属联动项
     */
    private Long linkageItemId;

    /**
     * 物模型接口码
     */
    private String modelApiCode;

    /**
     * 选中的动作名称
     */
    private String typeName;

    /**
     * 选中的动作值
     */
    private String typeValue;

    /**
     * 产品码
     */
    @TableField(exist = false)
    private String productCode;

    /**
     * 合并值
     * @return
     */
    public String[] getMergeValue() {
        return typeValue != null ? new String[] {typeValue} : new String[]{};
    }

    public String getDeviceName() {
        if(this.deviceName == null) {
            if(Objects.equals(this.uid, "-1")) {
                return "组全部设备";
            } else if(Objects.equals(this.uid, "-1")) {
                return "当前设备";
            }
        }

        return this.deviceName;
    }
}
