package com.iteaj.iboot.module.iot.dto;

import com.iteaj.iboot.module.iot.consts.DeviceTypeAlias;
import com.iteaj.iboot.module.iot.entity.Device;
import lombok.Data;

@Data
public class DeviceDto extends Device {

    /**
     * 型号名称
     */
    private String modelName;

    /**
     * 设备类型名称
     */
    private String deviceTypeName;

    /**
     * 设备别名
     */
    private DeviceTypeAlias alias;
}
