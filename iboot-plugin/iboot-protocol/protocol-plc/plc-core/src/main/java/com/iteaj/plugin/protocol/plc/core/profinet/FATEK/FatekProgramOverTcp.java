package com.iteaj.plugin.protocol.plc.core.profinet.FATEK;

import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.SpecifiedCharacterMessage;
import com.iteaj.plugin.protocol.plc.core.net.IReadWriteDevice;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.types.AsciiControl;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

/**
 * 台湾永宏公司的编程口协议，此处是基于tcp的实现，地址信息请查阅api文档信息，地址可以携带站号信息，例如 s=2;D100<br />
 * The programming port protocol of Taiwan Yonghong company, here is the implementation based on TCP,
 * please refer to the API information for the address information, The address can carry station number information, such as s=2;D100
 * <br /><br />
 * 支持位访问：M,X,Y,S,T(触点),C(触点)，字访问：RT(当前值),RC(当前值)，D，R；具体参照API文档
 */
public class FatekProgramOverTcp extends NetworkDeviceBase {
    // region Constructor

    /**
     * 实例化默认的构造方法<br />
     * Instantiate the default constructor
     */
    public FatekProgramOverTcp( ) {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
    }

    /**
     * 使用指定的ip地址和端口来实例化一个对象<br />
     * Instantiate an object with the specified IP address and port
     * @param ipAddress 设备的Ip地址
     * @param port 设备的端口号
     */
    public FatekProgramOverTcp( String ipAddress, int port )
    {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.setSleepTime(20);
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new SpecifiedCharacterMessage(AsciiControl.ETX);
    }

    // endregion

    // region Public Member

    public byte getStation() {
        return Station;
    }

    public void setStation(byte station) {
        Station = station;
    }

    /**
     * PLC的站号信息，需要和实际的设置值一致，默认为1<br />
     * he station number information of the PLC needs to be consistent with the actual setting value. The default is 1.
     */
    private byte Station = 0;

    // endregion

    // region Read Write Support

    /**
     * @see FatekProgramHelper#Read(IReadWriteDevice, byte, String, short)
     */
    public OperateResultExOne<byte[]> Read(String address, short length ) {
        return FatekProgramHelper.Read(this, getStation(), address, length);
    }

    /**
     * @see FatekProgramHelper#Write(IReadWriteDevice, byte, String, byte[]) 
     */
    public OperateResult Write(String address, byte[] value ) {
        return FatekProgramHelper.Write(this, getStation(), address, value);
    }

    // endregion

    // region Bool Read Write

    /**
     * @see FatekProgramHelper#ReadBool(IReadWriteDevice, byte, String, short) 
     */
    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        return FatekProgramHelper.ReadBool(this, getStation(), address, length);
    }

    /**
     * @see FatekProgramHelper#Write(IReadWriteDevice, byte, String, boolean[]) 
     */
    public OperateResult Write( String address, boolean[] value ) {
        return FatekProgramHelper.Write(this, getStation(), address, value);
    }

    /**
     * @see FatekProgramHelper#Run(IReadWriteDevice, byte)
     */
    public OperateResult Run( ){
        return FatekProgramHelper.Run(this, this.getStation());
    }

    /**
     * @see FatekProgramHelper#Stop(IReadWriteDevice, byte) 
     */
    public OperateResult Stop( ){
        return FatekProgramHelper.Stop(this, this.getStation());
    }

    /**
     * @see FatekProgramHelper#ReadStatus(IReadWriteDevice, byte)
     */
    public OperateResultExOne<boolean[]> ReadStatus( ){
        return FatekProgramHelper.ReadStatus(this, this.getStation());
    }

    // endregion

    // region Object Override

    public String toString( ) {
        return "FatekProgramOverTcp[" + getIpAddress() + ":" + getPort() + "]";
    }

    // endregion

    // region Private Member

    private byte station = 0x01;                 // PLC的站号信息

    // endregion


}
