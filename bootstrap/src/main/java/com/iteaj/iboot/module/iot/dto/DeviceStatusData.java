package com.iteaj.iboot.module.iot.dto;

import com.iteaj.iboot.module.iot.consts.DeviceStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceStatusData {

    private Long id;

    private String uid;

    /**
     * 设备编号
     */
    private String deviceSn;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备状态
     */
    private DeviceStatus status;

    /**
     * 产品码
     */
    private String productCode;

    /**
     * 所属产品
     */
    private String productName;
}
