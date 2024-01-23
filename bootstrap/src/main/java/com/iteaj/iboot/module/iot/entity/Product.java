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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @NotBlank(message = "产品代码必填")
    private String code;

    /**
     * 设备类型
     */
    @NotNull(message = "设备类型必填")
    private DeviceType deviceType;

    /**
     * 产品类型
     */
    @NotNull(message = "产品类型必填")
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
    @NotBlank(message = "产品名称必填")
    private String name;

    /**
     * 绑定的网关
     */
    @NotNull(message = "接入网关必填")
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
    @NotNull(message = "所属协议必填")
    private Long protocolId;

    /**
     * 协议名称
     */
    @TableField(exist = false)
    private String protocolName;

    /**
     * 协议代码
     */
    @TableField(exist = false)
    private String protocolCode;

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
