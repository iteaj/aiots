package com.iteaj.plugin.protocol.iec104.network;

public enum IEC104EventType {

    /**
     * 继电保护设备事件
     */
    SingleProtection,

    /**
     * 继电保护设备成组启动事件
     */
    ProtectionStartEvent,

    /**
     * 继电保护设备成组输入信息
     */
    ProtectionOutputCircuit
}
