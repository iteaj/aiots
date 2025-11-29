package com.iteaj.iboot.module.iot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceStatusDto {


    /**
     * 在线数量
     */
    private int onlineNum;

    /**
     * 离线数量
     */
    private int offlineNum;

    /**
     * 产品名称
     */
    private String productName;

    public static DeviceStatusDto buildZero(String productName) {
        DeviceStatusDto deviceStatusDto = new DeviceStatusDto();
        deviceStatusDto.setOnlineNum(0);
        deviceStatusDto.setOfflineNum(0);
        deviceStatusDto.setProductName(productName);
        return deviceStatusDto;
    }
}
