package com.iteaj.iboot.module.iot.dto;

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
     * 产品列表
     */
    private List<Long> productIds;

    /**
     * 产品名称列表
     */
    private List<String> productNames;
}
