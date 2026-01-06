package com.iteaj.plugin.protocol.iec104.network;

public enum IEC104TEType {
    /**
     * 值范围在-1到1之间
     */
    normalized("归一化值"),
    /**
     * 值范围在-1到1之间
     */
    normalized_nd("归一化值(不带品质描述)"),

    /**
     * 范围在-32768 到 32767之间
     */
    scaled("标度值"),

    short_float("短浮点数")
    ;

    private String desc;

    IEC104TEType(String desc) {
        this.desc = desc;
    }
}
