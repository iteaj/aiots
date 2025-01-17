package com.iteaj.plugin.protocol.plc.core.profinet.XINJE;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.basic.SoftIncrementCount;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.ModbusTcpMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.DataFormat;
import com.iteaj.plugin.protocol.plc.core.transfer.ReverseWordTransform;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.modbus.ModbusInfo;

import java.util.ArrayList;

/**
 * 信捷内部的TCP信息，该协议是信捷基于modbus协议扩展而来，支持更多的地址类型，以及更广泛的地址范围。具体参考Demo界面信息。<br />
 * The TCP information inside Xinjie, which is extended by Xinjie based on the modbus protocol, supports more address types and a wider range of addresses. For details, refer to the Demo UI.
 */
public class XinJEInternalNet extends NetworkDeviceBase {

    /**
     * 实例化一个XINJE-Tcp协议的客户端对象<br />
     * Instantiate a client object of the Modbus-Tcp protocol
     */
    public XinJEInternalNet( ) {
        this.softIncrementCount = new SoftIncrementCount(65535);
        this.WordLength = 1;
        this.setByteTransform(new ReverseWordTransform());
        this.getByteTransform().setDataFormat(DataFormat.CDAB);
    }

    /**
     * 指定服务器地址，端口号，客户端自己的站号来初始化<br />
     * Specify the server address, port number, and client's own station number to initialize
     * @param ipAddress 服务器的Ip地址
     * @param port 服务器的端口号
     * @param station 客户端自身的站号
     */
    public XinJEInternalNet( String ipAddress, int port, byte station ) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
        this.Station = station;
    }

    protected INetMessage GetNewNetMessage( ) {
        return new ModbusTcpMessage( );
    }

    private SoftIncrementCount softIncrementCount;              // 自增消息的对象

    /**
     * 获取或者重新修改服务器的默认站号信息，当然，你可以再读写的时候动态指定，参见备注<br />
     * Get or modify the default station number information of the server. Of course, you can specify it dynamically when reading and writing, see note
     */
    public byte Station = 0x01;

    /**
     * 获取字节变换规则<br />
     * Gets the byte conversion rule
     * @return 字节变换规则信息
     */
    public DataFormat getDataFormat(){
        return this.getByteTransform().getDataFormat();
    }

    /**
     * 设置字节变换规则<br />
     * Set byte conversion rules
     * @param value 字节变换规则
     */
    public void setDataFormat( DataFormat  value ){
        this.getByteTransform().setDataFormat(value);
    }

    /**
     * 字符串数据是否按照字来反转，默认为False<br />
     * Whether the string data is reversed according to words. The default is False.
     * @return 字符串按照2个字节的排列进行颠倒，根据实际情况进行设置
     */
    public boolean getIsStringReverse(){
        return this.getByteTransform().getIsStringReverse();
    }

    /**
     * 字符串数据是否按照字来反转，默认为False<br />
     * Whether the string data is reversed according to words. The default is False.
     * @param value 字符串按照2个字节的排列进行颠倒，根据实际情况进行设置
     */
    public void  setIsStringReverse( boolean value ){
        this.getByteTransform().setIsStringReverse(value);
    }

    /**
     * 获取协议自增的消息号，你可以自定义消息号的规则，详细参见{@link XinJEInternalNet}说明，也可以查找{@link SoftIncrementCount}说明。<br />
     * To obtain the message number of the protocol autoincrement, you can customize the rules of the message number, see the {@link XinJEInternalNet} description for details, or you can find the {@link SoftIncrementCount} description.
     * @return 消息号ID
     */
    public SoftIncrementCount MessageId (){
        return softIncrementCount;
    }

    public byte[] PackCommandWithHeader( byte[] command ) {
        return ModbusInfo.PackCommandToTcp(command, (short) softIncrementCount.GetCurrentValue());
    }

    public OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response ) {
        return ModbusInfo.ExtractActualData(ModbusInfo.ExplodeTcpCommandToCore(response));
    }

    public OperateResultExOne<byte[]> Read( String address, short length ) {
        OperateResultExOne<ArrayList<byte[]>> command = XinJEHelper.BuildReadCommand(Station, address, length, false);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        return ReadFromCoreServer(command.Content);
    }

    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        OperateResultExOne<ArrayList<byte[]>> command = XinJEHelper.BuildReadCommand(Station, address, length, true);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray(read.Content), 0, length));
    }

    public OperateResult Write(String address, byte[] value ) {
        OperateResultExOne<byte[]> command = XinJEHelper.BuildWriteWordCommand(Station, address, value);
        if (!command.IsSuccess) return command;

        return ReadFromCoreServer(command.Content);
    }

    public OperateResult Write(String address, boolean[] value ) {
        OperateResultExOne<byte[]> command = XinJEHelper.BuildWriteBoolCommand(Station, address, value);
        if (!command.IsSuccess) return command;

        return ReadFromCoreServer(command.Content);
    }

    public String toString( ) {
        return "XinJEInternalNet[" + getIpAddress() + ":" + getPort() + "]";
    }
}
