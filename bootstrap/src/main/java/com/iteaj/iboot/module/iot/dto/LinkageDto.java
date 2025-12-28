package com.iteaj.iboot.module.iot.dto;

import com.alibaba.fastjson.JSONArray;
import com.iteaj.iboot.module.iot.consts.EventSourceType;
import com.iteaj.iboot.module.iot.entity.Linkage;
import lombok.Data;

import java.util.List;

@Data
public class LinkageDto extends Linkage {

    /**
     * 设备组名称
     */
    private String deviceGroupName;

    /**
     * 事件源的设备组id
     */
    private JSONArray sourceDeviceGroupIds;

    /**
     * 产品列表
     */
    private JSONArray productIds;

    /**
     * 产品名称列表
     */
    private List<String> productNames;

    /**
     * 事件源类型
     */
    private EventSourceType eventSourceType;
}
