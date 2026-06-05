package com.iteaj.iboot.module.iot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CmdInvokeDto {

    /**
     * 指令码
     */
    private String code;

    /**
     * 指令名称
     */
    private String name;

    /**
     * 设备uid
     */
    private String uid;

    /**
     * 调用场景
     */
    private String scenario;
}
