package com.iteaj.iboot.module.iot.dto;

import com.iteaj.iboot.module.iot.consts.DeviceTypeAlias;
import com.iteaj.iboot.module.iot.entity.Device;
import lombok.Data;

@Data
public class DeviceDto extends Device {

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品类型名称
     */
    private String productTypeName;

    /**
     * 设备别名
     */
    private DeviceTypeAlias alias;
}
