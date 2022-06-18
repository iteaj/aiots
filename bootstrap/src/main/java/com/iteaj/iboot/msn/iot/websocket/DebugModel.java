package com.iteaj.iboot.msn.iot.websocket;

import lombok.Data;

@Data
public class DebugModel {

    /**
     * 设备编号
     */
    private String deviceSn;

    /**
     * 客户端Sn
     */
    private String clientSn;
}
