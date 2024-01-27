package com.iteaj.iboot.module.iot.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DebugDto {

    /**
     * 设备信息
     */
    private DeviceDto device;

    /**
     * 产品信息
     */
    private ProductDto product;
}
