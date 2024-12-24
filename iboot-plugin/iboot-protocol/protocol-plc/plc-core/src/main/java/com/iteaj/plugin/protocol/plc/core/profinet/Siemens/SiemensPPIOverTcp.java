package com.iteaj.plugin.protocol.plc.core.profinet.Siemens;

import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.net.IReadWriteDevice;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.ReverseBytesTransform;
import com.iteaj.plugin.protocol.plc.core.types.MemoryStream;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.profinet.Siemens.Helper.SiemensPPIHelper;
import com.iteaj.plugin.protocol.plc.core.StringResources;

import java.net.Socket;
import java.util.Date;

public class SiemensPPIOverTcp extends NetworkDeviceBase {


    public SiemensPPIOverTcp() {
        this.WordLength = 2;
        this.setByteTransform(new ReverseBytesTransform());
        this.communicationLock = new Object();
    }

    /**
     * 使用指定的ip地址和端口号来实例化对象<br />
     * Instantiate the object with the specified IP address and port number
     * @param ipAddress Ip地址信息
     * @param port 端口号信息
     */
    public SiemensPPIOverTcp(String ipAddress, int port) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected OperateResultExOne<byte[]> ReceiveByMessage(Socket socket, int timeOut, INetMessage netMessage) {
        MemoryStream ms = new MemoryStream();
        Date startTime = new Date();        // 加入超时，防止无限循环接收

        while (true) {
            OperateResultExOne<byte[]> receive = super.ReceiveByMessage(socket, timeOut, netMessage);
            if (!receive.IsSuccess) return receive;

            ms.Write(receive.Content);          // 数据写入缓存，然后判断是否完整
            if (SiemensPPIHelper.CheckReceiveDataComplete(ms))
                return OperateResultExOne.CreateSuccessResult(ms.ToArray());

            // 一直不完整的话，引发超时
            if (getReceiveTimeOut() > 0 && (new Date().getTime() - startTime.getTime()) > getReceiveTimeOut())
                return new OperateResultExOne<>(StringResources.Language.ReceiveDataTimeout() + getReceiveTimeOut());
        }
    }

    /**
     * 获取站号信息
     * @return 站号数据
     */
    public byte getStation() {
        return station;
    }

    /**
     * 设置站号信息
     * @param station 新的站号的数据
     */
    public void setStation(byte station) {
        this.station = station;
    }

    /**
     * @see SiemensPPIHelper#Read(IReadWriteDevice, String, short, byte, Object)
     */
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return SiemensPPIHelper.Read(this, address, length, this.station, this.communicationLock);
    }

    /**
     * @see SiemensPPIHelper#ReadBool(IReadWriteDevice, String, short, byte, Object) 
     */
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return SiemensPPIHelper.ReadBool(this, address, length, this.station, this.communicationLock);
    }

    /**
     * @see SiemensPPIHelper#Write(IReadWriteDevice, String, byte[], byte, Object) 
     */
    public OperateResult Write(String address, byte[] value) {
        return SiemensPPIHelper.Write(this, address, value, this.station, this.communicationLock);
    }

    /**
     * @see SiemensPPIHelper#Write(IReadWriteDevice, String, boolean[], byte, Object) 
     */
    public OperateResult Write(String address, boolean[] value) {
        return SiemensPPIHelper.Write(this, address, value, this.station, this.communicationLock);
    }

    /**
     * @see SiemensS7Net#ReadByte(String) 
     */
    public OperateResultExOne<Byte> ReadByte(String address) {
        OperateResultExOne<byte[]> read = Read(address, (short) 1);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(read.Content[0]);
    }

    public OperateResult Write(String address, byte value) {
        return Write(address, new byte[]{value});
    }


    /**
     * @see SiemensPPIHelper#Start(IReadWriteDevice, String, byte, Object) 
     */
    public OperateResult Start(String parameter) {
        return SiemensPPIHelper.Start(this, parameter, this.station, this.communicationLock);
    }

    /**
     * @see SiemensPPIHelper#Stop(IReadWriteDevice, String, byte, Object)
     */
    public OperateResult Stop(String parameter) {
        return SiemensPPIHelper.Stop(this, parameter, this.station, this.communicationLock);
    }


    private byte station = 0x02;            // PLC的站号信息
    private Object communicationLock;       // 通讯锁

    public String toString() {
        return "SiemensPPIOverTcp[" + getIpAddress() + ":" + getPort() + "]";
    }
}
