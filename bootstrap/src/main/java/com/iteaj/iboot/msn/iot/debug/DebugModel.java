package com.iteaj.iboot.msn.iot.debug;

import lombok.Data;

@Data
public class DebugModel {

    /**
     * 记录id
     */
    private Long id;

    /**
     * 设备编号
     */
    private String deviceSn;

    /**
     * 客户端Sn
     */
    private String clientSn;
}
