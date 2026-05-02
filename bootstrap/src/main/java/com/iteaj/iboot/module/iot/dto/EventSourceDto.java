package com.iteaj.iboot.module.iot.dto;

import com.iteaj.iboot.module.iot.entity.DeviceGroup;
import com.iteaj.iboot.module.iot.entity.EventSource;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class EventSourceDto extends EventSource {

    /**
     * 接口
     */
    private List<ModelApi> apis;

    /**
     * 组
     */
    private List<DeviceGroup> groups;

    /**
     * 产品id与协议的映射关系
     */
    private Map<Long, String> productToProtocolMap;
}
