package com.iteaj.iboot.module.iot.collect.status;

import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import lombok.Data;

import java.util.Date;

/**
 * 设备实时状态
 */
@Data
public class RealtimeStatus {

    /**
     * 设备编号
     */
    private String deviceSn;

    /**
     * 所属协议
     */
    private String protocolCode;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 设备状态
     */
    private DeviceStatus status;
}
