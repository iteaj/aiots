package com.iteaj.plugin.protocol.iec104.network;

public enum IEC104TIType {
    /**
     * 单点信息(占一位两种状态)
     */
    SinglePoint,
    /**
     * 带状态改变的单点信息
     */
    StatusChanges,
    /**
     * 双点信息(占2位四种状态)
     */
    DoublePoint,
    /**
     * 步位置信息(多档 -64 and 63)
     */
    TransientState,
    /**
     * 比特串(32位)
     */
    BinaryState
}
