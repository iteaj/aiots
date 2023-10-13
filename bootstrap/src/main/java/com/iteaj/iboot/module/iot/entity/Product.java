package com.iteaj.iboot.module.iot.entity;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.consts.GatewayType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 产品
 * </p>
 *
 * @author iteaj
 * @since 2022-07-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_product")
public class Product extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 图标
     */
    private String logo;

    /**
     * 产品代码
     */
    private String code;

    /**
     * 设备类型
     */
    private DeviceType deviceType;

    /**
     * 产品类型
     */
    private Integer productTypeId;

    /**
     * 类型名称
     */
    @TableField(exist = false)
    private String typeName;

    /**
     * 产品名称
     */
    @TableField(condition = SqlCondition.LIKE)
    private String name;

    /**
     * 绑定的网关
     */
    private Long gatewayId;

    /**
     * 网关类型
     */
    @TableField(exist = false)
    private GatewayType gatewayType;

    /**
     * 网关名称
     */
    @TableField(exist = false)
    private String gatewayName;

    /**
     * 绑定的协议
     */
    private Long protocolId;

    /**
     * 协议名称
     */
    @TableField(exist = false)
    private String protocolName;

    /**
     * 产品说明
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 设备类型名称
     * @return
     */
    public String getDeviceTypeName() {
        return this.deviceType != null ? this.deviceType.getDesc() : null;
    }

    /**
     * 网关类型名称
     * @return
     */
    public String getGatewayTypeName() {
        return this.gatewayType != null ? this.gatewayType.getDesc() : null;
    }
}
