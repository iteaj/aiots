package com.iteaj.iboot.plugin.gbs.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GbsViewChannelDto {

    /**
     * 通道名称
     */
    private String label;

    /**
     * 设备标识
     */
    private String value;

    public GbsViewChannelDto(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public GbsViewChannelDto(String label, String deviceId, String channelId) {
        this.label = label;
        this.value = deviceId + "::" + channelId;
    }
}
