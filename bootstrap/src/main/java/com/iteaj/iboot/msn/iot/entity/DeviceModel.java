package com.iteaj.iboot.msn.iot.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 设备型号
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_device_model")
public class DeviceModel extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 设备类型
     */
    private Integer typeId;

    /**
     * 型号
     */
    @TableField(condition = SqlCondition.LIKE)
    private String model;

    /**
     * 型号说明
     */
    private String remark;


}
