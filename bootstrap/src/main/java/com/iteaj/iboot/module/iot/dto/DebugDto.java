package com.iteaj.iboot.module.iot.dto;

import com.iteaj.framework.IVOption;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 接口调试参数
     */
    private List<DeviceCommandDto> apiCodeDebugMap = new ArrayList<>();

    public void addApiDebugParam(List<DeviceCommandDto> params) {
        if(params != null && !params.isEmpty()) {
            apiCodeDebugMap.addAll(params);
        }
    }
}
