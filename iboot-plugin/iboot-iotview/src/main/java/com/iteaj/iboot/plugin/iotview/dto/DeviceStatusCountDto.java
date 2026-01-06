package com.iteaj.iboot.plugin.iotview.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceStatusCountDto {

    /**
     * 项值
     */
    private int value;

    /**
     * 总值
     */
    private int total;

    /**
     * 类别名称
     */
    private String name;
}
