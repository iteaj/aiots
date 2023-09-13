package com.iteaj.iboot.module.iot.dto;

import com.iteaj.iboot.module.iot.entity.DeviceModel;
import lombok.Data;

@Data
public class ProductDto extends DeviceModel {

    /**
     * 设备数量
     */
    private Integer deviceCount;
}
