package com.iteaj.iboot.msn.iot.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SignalOfModelDto {

    private String id;

    /**
     * 点位名称
     */
    private String name;

    /**
     * 点位地址
     */
    private String address;

    /**
     * 点位字段名称
     */
    private String fieldName;
}
