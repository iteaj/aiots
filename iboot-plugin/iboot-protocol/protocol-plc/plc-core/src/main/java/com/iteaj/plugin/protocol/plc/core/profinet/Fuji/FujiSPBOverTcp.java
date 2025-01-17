package com.iteaj.plugin.protocol.plc.core.profinet.Fuji;

import com.iteaj.plugin.protocol.plc.core.message.FujiSPBMessage;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

/**
 * 富士PLC的SPB协议，详细的地址信息见api文档说明，地址可以携带站号信息，例如：s=2;D100，PLC侧需要配置无BCC计算，包含0D0A结束码<br />
 * Fuji PLC's SPB protocol. For detailed address information, see the api documentation,
 * The address can carry station number information, for example: s=2;D100, PLC side needs to be configured with no BCC calculation, including 0D0A end code
 */
public class FujiSPBOverTcp extends NetworkDeviceBase {
    /**
     * 使用默认的构造方法实例化对象<br />
     * Instantiate the object using the default constructor
     */
    public FujiSPBOverTcp() {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.setSleepTime(20);
    }

    /**
     * 使用指定的ip地址和端口来实例化一个对象<br />
     * Instantiate an object with the specified IP address and port
     *
     * @param ipAddress 设备的Ip地址
     * @param port      设备的端口号
     */
    public FujiSPBOverTcp(String ipAddress, int port) {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.setSleepTime(20);
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    protected INetMessage GetNewNetMessage() {
        return new FujiSPBMessage();
    }

    /**
     * 获取当前的站号信息
     *
     * @return 站号信息
     */
    public byte getStation() {
        return station;
    }

    /**
     * 设置当前的站号信息
     *
     * @param station 站号信息
     */
    public void setStation(byte station) {
        this.station = station;
    }


    public OperateResultExOne<byte[]> Read(String address, short length) {
        return FujiSPBHelper.Read(this, this.station, address, length);
    }

    public OperateResult Write(String address, byte[] value) {
        return FujiSPBHelper.Write(this, this.station, address, value);
    }

    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return FujiSPBHelper.ReadBool(this, this.station, address, length);
    }

    public OperateResult Write(String address, boolean value) {
        return FujiSPBHelper.Write(this, this.station, address, value);
    }

    private byte station = 0x01;                 // PLC的站号信息

    public String toString() {
        return "FujiSPBOverTcp[" + getIpAddress() + ":" + getPort() + "]";
    }
}
