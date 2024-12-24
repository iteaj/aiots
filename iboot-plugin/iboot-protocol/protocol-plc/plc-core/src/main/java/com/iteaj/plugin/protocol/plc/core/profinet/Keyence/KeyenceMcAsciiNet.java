package com.iteaj.plugin.protocol.plc.core.profinet.Keyence;

import com.iteaj.plugin.protocol.plc.core.address.McAddressData;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.profinet.melsec.helper.McHelper;
import com.iteaj.plugin.protocol.plc.core.profinet.melsec.MelsecMcAsciiNet;

/**
 * 基恩士PLC的数据通信类
 */
public class KeyenceMcAsciiNet extends MelsecMcAsciiNet {

    /**
     * 实例化基恩士的Qna兼容3E帧协议的通讯对象
     */
    public KeyenceMcAsciiNet() {
        super();
    }

    /**
     * 实例化一个基恩士的Qna兼容3E帧协议的通讯对象
     * @param ipAddress PLC的Ip地址
     * @param port PLC的端口
     */
    public KeyenceMcAsciiNet(String ipAddress, int port) {
        super(ipAddress, port);
    }

    public OperateResultExOne<McAddressData> McAnalysisAddress(String address, short length, boolean isBit) {
        return McAddressData.ParseKeyenceFrom(address, length, isBit);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        if (KeyenceMcNet.CheckKeyenceBoolAddress( address ))
            return McHelper.ReadBool( this, address, length, false );
        return super.ReadBool(address, length);
    }

    @Override
    public OperateResult Write(String address, boolean[] values) {
        if (KeyenceMcNet.CheckKeyenceBoolAddress(address))
            return McHelper.Write(this, address, values, false);
        return super.Write(address, values);
    }

    /**
     * 获取当前对象的字符串标识形式
     * @return 字符串信息
     */
    @Override
    public String toString() {
        return String.format("KeyenceMcAsciiNet[%s:%d]", getIpAddress(), getPort());
    }
}
