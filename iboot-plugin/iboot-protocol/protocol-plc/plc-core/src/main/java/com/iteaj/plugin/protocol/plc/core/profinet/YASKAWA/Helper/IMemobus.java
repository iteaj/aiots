package com.iteaj.plugin.protocol.plc.core.profinet.YASKAWA.Helper;

import com.iteaj.plugin.protocol.plc.core.net.IReadWriteDevice;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDoubleBase;
import com.iteaj.plugin.protocol.plc.core.transfer.IByteTransform;

/**
 * Memobus协议的接口信息
 */
public interface IMemobus extends IReadWriteDevice {

    /**
     * {@link NetworkDoubleBase#getByteTransform()}
     */
    IByteTransform getByteTransform();

    /**
     * 获取发送目标的CPU的编号信息，默认为 2<br />
     * Get the CPU number information of the sending destination, the default is 2
     * @return CpuTo 的值
     */
    byte getCpuTo();

    /**
     * 设置发送目标的CPU的编号信息，默认为 2<br />
     * Set the CPU number information of the sending destination, the default is 2
     * @param value CpuTo 的值
     */
    void setCpuTo( byte value );

    /**
     * 获取发送源的CPU的编号信息，默认为 1<br />
     * Get the number information of the sending source CPU, the default is 1
     * @return CpuFrom 的值
     */
    byte getCpuFrom();

    /**
     * 设置发送源的CPU的编号信息，默认为 1<br />
     * Set the number information of the sending source CPU, the default is 1
     * @param value CpuFrom 的值
     */
    void setCpuFrom(byte value);
}
