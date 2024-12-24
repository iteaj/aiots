package com.iteaj.plugin.protocol.plc.core.profinet.Keyence;

import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.SpecifiedCharacterMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.AsciiControl;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

import java.net.Socket;
import java.util.Date;

public class KeyenceNanoSerialOverTcp extends NetworkDeviceBase {

    /**
     * 实例化一个默认的对象<br />
     * Instantiate a default object
     */
    public KeyenceNanoSerialOverTcp( )
    {
        this.WordLength                            = 1;
        this.setByteTransform(new RegularByteTransform( ));
        this.getByteTransform().setIsStringReverse( true );
        this.LogMsgFormatBinary                    = false;
    }

    /**
     * 使用指定的ip地址和端口号来初始化对象<br />
     * Initialize the object with the specified IP address and port number
     * @param ipAddress Ip地址数据
     * @param port 端口号
     */
    public KeyenceNanoSerialOverTcp( String ipAddress, int port ) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    protected INetMessage GetNewNetMessage( ) {
        return new SpecifiedCharacterMessage(AsciiControl.CR, AsciiControl.LF);
    }

    protected OperateResult InitializationOnConnect(Socket socket ) {
        // 建立通讯连接{CR/r}
        OperateResultExOne<byte[]> result = ReadFromCoreServer(socket, KeyenceNanoHelper.GetConnectCmd(Station, UseStation), true, true);
        if (!result.IsSuccess) return result;

        return OperateResult.CreateSuccessResult();
    }

    protected OperateResult ExtraOnDisconnect( Socket socket ) {
        OperateResultExOne<byte[]> result = ReadFromCoreServer(socket, KeyenceNanoHelper.GetDisConnectCmd(Station, UseStation), true, true);
        if (!result.IsSuccess) return result;

        return OperateResult.CreateSuccessResult();
    }

    /**
     * 获取或设置当前的站号信息，在RS232连接模式下，设置为0，如果是RS485/RS422连接下，必须设置正确的站号<br />
     * Get or set the current station number information. In RS232 connection mode, set it to 0.
     * If it is RS485/RS422 connection, you must set the correct station number.
     */
    public byte Station = 0;

    /**
     * 获取或设置当前是否启用站号信息，当不启动站号时，在连接和断开的时候，将不使用站号报文。<br />
     * Get or set whether the station number information is currently enabled or not.
     * When the station number is not activated, the station number message will not be used when connecting and disconnecting.
     */
    public boolean UseStation = false;

    public OperateResultExOne<byte[]> Read( String address, short length )
    {
        return KeyenceNanoHelper.Read( this, address, length );
    }

    public OperateResult Write( String address, byte[] value )
    {
        return KeyenceNanoHelper.Write( this, address, value );
    }

    public OperateResultExOne<boolean[]> ReadBool( String address, short length )
    {
        return KeyenceNanoHelper.ReadBool( this, address, length );
    }

    public OperateResult Write( String address, boolean value )
    {
        return KeyenceNanoHelper.Write( this, address, value );
    }

    public OperateResult Write( String address, boolean[] value )
    {
        return KeyenceNanoHelper.Write( this, address, value );
    }


    public OperateResultExOne<KeyencePLCS> ReadPlcType( )
    {
        return KeyenceNanoHelper.ReadPlcType( this );
    }

    public OperateResultExOne<Integer> ReadPlcMode( )
    {
        return KeyenceNanoHelper.ReadPlcMode( this );
    }

    public OperateResult SetPlcDateTime( Date dateTime )
    {
        return KeyenceNanoHelper.SetPlcDateTime( this, dateTime );
    }

    public OperateResultExOne<String> ReadAddressAnnotation( String address )
    {
        return KeyenceNanoHelper.ReadAddressAnnotation( this, address );
    }

    public OperateResultExOne<byte[]> ReadExpansionMemory( byte unit, short address, short length )
    {
        return KeyenceNanoHelper.ReadExpansionMemory( this, unit, address, length );
    }

    public OperateResult WriteExpansionMemory( byte unit, short address, byte[] value )
    {
        return KeyenceNanoHelper.WriteExpansionMemory( this, unit, address, value );
    }

    public String toString( )
    {
        return "KeyenceNanoSerialOverTcp[" + getIpAddress() + ":" + getPort() + "]";
    }
}
