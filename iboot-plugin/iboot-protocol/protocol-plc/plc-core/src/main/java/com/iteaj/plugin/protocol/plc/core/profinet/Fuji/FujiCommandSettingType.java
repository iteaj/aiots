package com.iteaj.plugin.protocol.plc.core.profinet.Fuji;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.address.FujiCommandSettingTypeAddress;
import com.iteaj.plugin.protocol.plc.core.message.FujiCommandSettingTypeMessage;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.ByteTransformHelper;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.transfer.ReverseBytesTransform;
import com.iteaj.plugin.protocol.plc.core.types.BitConverter;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

/**
 * 基于Command-Setting-Type通信模式的协议实现，地址格式参数DEMO界面<br />
 * Protocol implementation based on Command-Setting-Type communication mode, address format parameter DEMO interface
 *
 * @apiNote 本类实例化对象之后，还需要设置 {@link FujiCommandSettingType#setDataSwap(boolean)} 属性，根据实际情况来设置。
 */
public class FujiCommandSettingType extends NetworkDeviceBase {
    // region Constructor

    /**
     * 实例化一个默认的对象
     */
    public FujiCommandSettingType( ) {
        this.setByteTransform(new ReverseBytesTransform());
        this.WordLength = 2;
    }

    /**
     * 使用指定的IP地址和端口号来实例化一个对象
     * @param ipAddress IP地址信息
     * @param port 端口号信息
     */
    public FujiCommandSettingType( String ipAddress, int port ) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    protected INetMessage GetNewNetMessage( ) {
        return new FujiCommandSettingTypeMessage( );
    }

    public OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response ) {
        return UnpackResponseContentHelper(send, response);
    }

    // endregion

    // region ReadWrite Override

    public OperateResultExOne<byte[]> Read( String address, short length ) {
        OperateResultExOne<byte[]> bulid = BuildReadCommand(address, length);
        if (!bulid.IsSuccess) return bulid;

        return ReadFromCoreServer(bulid.Content);
    }

    /**
     * 写入原始数据到PLC指定的地址里，返回是否写入成功
     * @param address PLC的地址信息
     * @param value 原始数据内容
     * @return 是否写入成功
     */
    public OperateResult Write(String address, byte[] value ) {
        OperateResultExOne<byte[]> bulid = BuildWriteCommand(address, value);
        if (!bulid.IsSuccess) return bulid;

        return ReadFromCoreServer(bulid.Content);
    }

    //endregion

    //region Read Write Byte

    /**
     * 读取指定地址的byte数据，地址格式 S100 <br />
     * Reads the byte data of the specified address, the address format S100
     * @param address 起始地址，格式为S100
     * @return 是否读取成功的结果对象
     */
    public OperateResultExOne<Byte> ReadByte( String address ) {
        return ByteTransformHelper.GetByteResultFromBytes(Read(address, (short) 1), this.getByteTransform());
    }

    /**
     *  向PLC中写入byte数据，返回值说明<br />
     *  Write byte data to the PLC, return value description
     * @param address 起始地址，格式为 S100
     * @param value byte数据
     * @return 是否写入成功的结果对象
     */
    public OperateResult Write(String address, byte value ) {
        return Write(address, new byte[]{value});
    }

		//endregion

		//region Public Propertes
    
    /**
     * 获取当前的对象是否进行数据交换操作，将根据PLC的实际值来设定。<br />
     * Get whether the current object performs data exchange operation or not, it will be set according to the actual value of the PLC.
     * @return 数据是否交换的功能
     */
    public boolean getDataSwap(){
        return this.dataSwap;
    }

    /**
     * 设置当前的对象是否进行数据交换操作，将根据PLC的实际值来设定。<br />
     * Set whether the current object performs data exchange operation or not, it will be set according to the actual value of the PLC.
     * @param value 设置值
     */
    public void setDataSwap(boolean value) {
        this.dataSwap = value;
        if (value)
            this.setByteTransform(new RegularByteTransform());
        else
            this.setByteTransform(new ReverseBytesTransform());
    }

    //endregion

    //region Object Override

    public String toString( ) {
        return "FujiCommandSettingType[" + getIpAddress() + ":" + getPort() + "]";
    }

    //endregion

    //region Private Member

    private boolean dataSwap = false;                                 // 是否发生了数据交换

    //endregion

    //region Static Helper

    /**
     * 构建读取的报文指令
     * @param address PLC的地址信息
     * @param length 读取的长度信息
     * @return 报文构建的结果对象
     */
    public static OperateResultExOne<byte[]> BuildReadCommand(String address, short length ) {
        OperateResultExOne<FujiCommandSettingTypeAddress> analysisAddress = FujiCommandSettingTypeAddress.ParseFrom(address, length);
        if (!analysisAddress.IsSuccess) return OperateResultExOne.CreateFailedResult(analysisAddress);

        byte[] buffer = new byte[9];
        buffer[0] = 0x00;
        buffer[1] = 0x00;
        buffer[2] = 0x00;
        buffer[3] = analysisAddress.Content.DataCode;
        buffer[4] = 0x04;
        buffer[5] = BitConverter.GetBytes(analysisAddress.Content.getAddressStart())[0];
        buffer[6] = BitConverter.GetBytes(analysisAddress.Content.getAddressStart())[1];
        buffer[7] = BitConverter.GetBytes(analysisAddress.Content.getLength())[0];
        buffer[8] = BitConverter.GetBytes(analysisAddress.Content.getLength())[1];

        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    /**
     * 构建写入原始报文数据的请求信息
     * @param address 地址数据
     * @param value 原始报文的数据
     * @return 原始的写入报文数据
     */
    public static OperateResultExOne<byte[]> BuildWriteCommand( String address, byte[] value ) {
        OperateResultExOne<FujiCommandSettingTypeAddress> analysisAddress = FujiCommandSettingTypeAddress.ParseFrom(address, 0);
        if (!analysisAddress.IsSuccess) return OperateResultExOne.CreateFailedResult(analysisAddress);

        byte[] buffer = new byte[9 + value.length];
        buffer[0] = 0x01;
        buffer[1] = 0x00;
        buffer[2] = 0x00;
        buffer[3] = analysisAddress.Content.DataCode;
        buffer[4] = (byte) (0x04 + value.length);
        buffer[5] = BitConverter.GetBytes(analysisAddress.Content.getAddressStart())[0];
        buffer[6] = BitConverter.GetBytes(analysisAddress.Content.getAddressStart())[1];
        buffer[7] = BitConverter.GetBytes(analysisAddress.Content.getLength())[0];
        buffer[8] = BitConverter.GetBytes(analysisAddress.Content.getLength())[0];
        Utilities.ByteArrayCopyTo(value, buffer, 9);

        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    /**
     * 根据错误信息获取相关数据信息
     * @param error 错误代号
     * @return 实际的错误描述信息
     */
    public static String GetErrorText( int error )
    {
        switch (error)
        {
            case 0x12: return "Write of data to the program area";
            case 0x20: return "Non-existing CMND code";
            case 0x21: return "Input data is not in the order of data corresponding to CMND";
            case 0x22: return "Operation only from the loader is effective. Operation from any other node is disabled";
            case 0x24: return "A non-existing module has been specified";
            case 0x32: return "An address out of the memory size has been specified";
            default: return StringResources.Language.UnknownError();
        }
    }

    /**
     * 根据PLC返回的数据，解析出实际的数据内容
     * @param send 发送给PLC的数据
     * @param response PLC返回的数据
     * @return 结果数据信息
     */
    public static OperateResultExOne<byte[]> UnpackResponseContentHelper( byte[] send, byte[] response ) {
        try {
            if (response[1] != 0x00) return new OperateResultExOne<byte[]>(GetErrorText(response[1]));
            if (response[0] == 0x01) return OperateResultExOne.CreateSuccessResult(new byte[0]);          // 写入操作直接返回成功

            if (response.length < 10)
                return new OperateResultExOne<byte[]>(StringResources.Language.ReceiveDataLengthTooShort() + "10, Source: " + SoftBasic.ByteToHexString(response, ' '));
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 10));
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("UnpackResponseContentHelper failed: " + ex.getMessage() + " Source: " + SoftBasic.ByteToHexString(response, ' '));
        }
    }

    //endregion
}
