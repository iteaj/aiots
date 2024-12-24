package com.iteaj.plugin.protocol.plc.core.profinet.melsec.helper;

/**
 * MC协议的类型
 */
public enum McType {
    /**
     * 基于二进制的MC协议
     */
    McBinary,
    /**
     * 基于ASCII格式的MC协议
     */
    MCAscii,

    /**
     * R系列的二进制MC协议
     */
    McRBinary,
}
