package com.iteaj.iboot.msn.iot.dto;

import com.iteaj.iboot.msn.iot.consts.DeviceTypeAlias;
import com.iteaj.iboot.msn.iot.entity.Device;
import lombok.Data;

@Data
public class DeviceDto extends Device {

    /**
     * 型号名称
     */
    private String modelName;

    /**
     * 设备别名
     */
    private DeviceTypeAlias alias;
}
