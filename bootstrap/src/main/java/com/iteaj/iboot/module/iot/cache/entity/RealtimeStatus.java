package com.iteaj.iboot.module.iot.cache.entity;

import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

/**
 * 设备实时状态
 */
@Data
public class RealtimeStatus {

    /**
     * 设备所属uid
     */
    private Long uid;

    /**
     * 设备编号
     * @see com.sun.istack.internal.NotNull
     */
    private String deviceSn;

    /**
     * 父设备编号
     */
    private String parentDeviceSn;

    /**
     * 设备类型
     */
    private DeviceType type;

    /**
     * 所属组
     */
    private Long groupId;

    /**
     * 所属协议
     */
    private String protocolCode;

    /**
     * 所属产品
     */
    private String productCode;

    /**
     * 状态更新时间
     * @see #status
     */
    private Date updateTime;

    /**
     * 设备状态
     */
    private DeviceStatus status;

    public String buildDeviceKey() {
        if(this.getType() == DeviceType.Child) {
            return parentDeviceSn + "_" + deviceSn;
        } else {
            return deviceSn;
        }
    }

    public static String buildDeviceKey(String deviceSn, String parentDeviceSn) {
        return parentDeviceSn + "_" + deviceSn;
    }

    public boolean equals(Object o) {
        RealtimeStatus other = (RealtimeStatus) o;
        return Objects.equals(this.getProtocolCode(), other.getProtocolCode())
                && Objects.equals(this.buildDeviceKey(), other.buildDeviceKey());
    }
}
