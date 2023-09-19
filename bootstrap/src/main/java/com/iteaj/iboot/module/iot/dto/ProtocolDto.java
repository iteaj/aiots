package com.iteaj.iboot.module.iot.dto;

import com.iteaj.iboot.module.iot.entity.Protocol;
import lombok.Data;

@Data
public class ProtocolDto extends Protocol {

    /**
     * 绑定的设备数量
     */
    private Long deviceCount;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品代码
     */
    private String productCode;
}
