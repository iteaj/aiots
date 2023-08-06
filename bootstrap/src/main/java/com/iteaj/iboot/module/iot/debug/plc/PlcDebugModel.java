package com.iteaj.iboot.module.iot.debug.plc;

import com.iteaj.iboot.module.iot.debug.DebugModel;
import lombok.Data;

@Data
public class PlcDebugModel extends DebugModel {

    /**
     * plc ip地址
     */
    private String ip;

    /**
     * plc 端口
     */
    private Integer port;

    /**
     * 型号
     */
    private String model;

    /**
     * read、write、message
     */
    private String cmd;

    /**
     * 要写的值
     */
    private String value;

    /**
     * 操作地址
     */
    private String address;

    /**
     * 类型 int、float、double ...
     */
    private String type;

    /**
     * 2. 西门子
     * 3. 欧姆龙
     */
    private Integer plcType;
}
