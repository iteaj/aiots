package com.iteaj.iboot.msn.iot.dto;

import com.iteaj.iboot.msn.iot.entity.CollectData;
import lombok.Data;

@Data
public class CollectDataDto extends CollectData {

    /**
     * 设备类型
     */
    private Long deviceTypeId;

    /**
     * 设备型号
     */
    private Long modelId;

    /**
     * 设备编号
     */
    private String deviceSn;

    /**
     * 设备名称
     */
    private String deviceName;
}
