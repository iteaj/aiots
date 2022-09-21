package com.iteaj.iboot.msn.iot.dto;

import com.iteaj.iboot.msn.iot.entity.CollectTask;
import com.iteaj.iboot.msn.iot.entity.Device;
import com.iteaj.iboot.msn.iot.entity.Signal;
import lombok.Data;

import java.util.List;

@Data
public class CollectTaskDto extends CollectTask {

    /**
     * 采集的设备名称
     */
    private String deviceName;

    /**
     * 设备的所属型号
     */
    private String modelName;

    /**
     * 所属设备类型
     */
    private Long typeId;

    /**
     * 所属设备型号
     */
    private Long modelId;

    /**
     * 设备
     */
    private Device device;

    /**
     * 点位列表
     */
    private List<Signal> signals;
}
