package com.iteaj.plugin.protocol.plc.melsec.network;

import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

/**
 * 三菱计算机链接协议的网口版本，适用FX3U系列，FX3G，FX3S等等系列，通常在PLC侧连接的是485的接线口<br />
 * Network port version of Mitsubishi Computer Link Protocol, suitable for FX3U series, FX3G, FX3S, etc., usually the 485 connection port is connected on the PLC side<br /><br />
 * 关于在PLC侧的配置信息，协议：专用协议  传送控制步骤：格式一  站号设置：0
 */
public class MelsecFxLinksOverTcp extends NetworkDeviceBase {

		//region Constructor

    /**
     * 实例化默认的对象<br />
     * Instantiate the default object
     */
    public MelsecFxLinksOverTcp( ) {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.setSleepTime(20);
    }

    /**
     * 指定ip地址和端口号来实例化默认的对象<br />
     * Specify the IP address and port number to instantiate the default object
     * @param ipAddress Ip地址信息
     * @param port 端口号
     */
    public MelsecFxLinksOverTcp( String ipAddress, int port ) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    /// <inheritdoc/>
    public byte[] PackCommandWithHeader( byte[] command ) {
        return MelsecFxLinksHelper.PackCommandWithHeader( this, command );
    }

		//endregion

		//region Public Member

    public byte getStation(){
        return this.station;
    }

    public void setStation(byte value){
        this.station = value;
    }

    public byte getWaittingTime() {
        return watiingTime;
    }

    public void setWaittingTime(byte value){
        if (value > 0x0F)
        {
            watiingTime = 0x0F;
        }
        else
        {
            watiingTime = value;
        }
    }

    public boolean getSumCheck(){
        return this.sumCheck;
    }

    public void setSumCheck(boolean value){
        this.sumCheck = value;
    }


    public int getFormat(){
        return this.format;
    }

    public void setFormat(int value){
        this.format = value;
    }

		//endregion

		//region Read Write Support

    public OperateResultExOne<byte[]> Read(String address, short length ) {
        return MelsecFxLinksHelper.Read( this, address, length );
    }

    public OperateResult Write(String address, byte[] value ) {
        return MelsecFxLinksHelper.Write( this, address, value );
    }

		//endregion

		//region Bool Read Write

    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        return MelsecFxLinksHelper.ReadBool( this, address, length );
    }

    public OperateResult Write( String address, boolean[] value ){
        return MelsecFxLinksHelper.Write( this, address, value );
    }

		//endregion

    //region Start Stop

    public OperateResult StartPLC( String parameter ) {
        return MelsecFxLinksHelper.StartPLC( this, parameter );
    }

    public OperateResult StopPLC( String parameter ) {
        return MelsecFxLinksHelper.StopPLC( this, parameter );
    }

    public OperateResultExOne<String> ReadPlcType( String parameter  ) {
        return MelsecFxLinksHelper.ReadPlcType( this, parameter );
    }

		//endregion


		//region Private Member

    private byte station = 0x00;                 // PLC的站号信息
    private byte watiingTime = 0x00;             // 报文的等待时间，设置为0-15
    private boolean sumCheck = true;                // 是否启用和校验
    private int format = 1;
		//endregion

		//region Object Override

    public String toString( ) {
        return "MelsecFxLinksOverTcp[" + getIpAddress() + ":" + getPort() + "]";
    }

		//endregion
}
