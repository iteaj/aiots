package com.iteaj.iboot.module.iot.dto;

import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.consts.DeviceTypeAlias;
import com.iteaj.iboot.module.iot.entity.Device;
import lombok.Data;

@Data
public class DeviceDto extends Device {

    /**
     * 设备类型
     */
    private DeviceType deviceType;

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

    /**
     * 设备类型名称
     * @return
     */
    public String getDeviceTypeName() {
        return this.deviceType != null ? this.deviceType.getDesc() : null;
    }
}
