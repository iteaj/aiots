package com.iteaj.iboot.msn.iot.debug;

import lombok.Data;

@Data
public class DebugResult {

    /**
     * 设备编号
     */
    private String deviceSn;

    /**
     * 响应时间(毫秒)
     */
    private long respTime;

    /**
     * 读的值
     * 写为null
     */
    private String value;

    /**
     * 请求的十六进制报文
     */
    private String reqMsg;

    /**
     * 响应的十六机制报文
     */
    private String respMsg;
}
