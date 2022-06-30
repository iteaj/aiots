package com.iteaj.iboot.msn.iot.dto;

import lombok.Data;

@Data
public class CurrentDeviceDto {

    /**
     * 总设备数量
     */
    private long today;

    /**
     * 在线数量
     */
    private long online;
}
