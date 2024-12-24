package com.iteaj.plugin.protocol.plc.core.profinet.YASKAWA;

import com.iteaj.plugin.protocol.plc.core.basic.SoftIncrementCount;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.MemobusMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkUdpDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.DataFormat;
import com.iteaj.plugin.protocol.plc.core.transfer.ReverseWordTransform;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.profinet.YASKAWA.Helper.IMemobus;
import com.iteaj.plugin.protocol.plc.core.profinet.YASKAWA.Helper.MemobusHelper;

public class MemobusUdpNet  extends NetworkUdpDeviceBase implements IMemobus {

    // region Constructor

    /**
     * 实例化一个Memobus-Tcp协议的客户端对象<br />
     * Instantiate a client object of the Memobus-Tcp protocol
     */
    public MemobusUdpNet( ) {
        this.softIncrementCount = new SoftIncrementCount(255);
        this.WordLength = 1;
        this.setByteTransform(new ReverseWordTransform(DataFormat.CDAB));
    }

    /**
     * 指定服务器地址，端口号，客户端自己的站号来初始化<br />
     * Specify the server address, port number, and client's own station number to initialize
     * @param ipAddress 服务器的Ip地址
     * @param port 服务器的端口号
     */
    public MemobusUdpNet( String ipAddress, int port ) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    protected INetMessage GetNewNetMessage( ){
        return new MemobusMessage( );
    }

    public byte[] PackCommandWithHeader( byte[] command ) {
        return MemobusHelper.PackCommandWithHeader(command, this.softIncrementCount.GetCurrentValue());
    }

    public OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response ) {
        return MemobusHelper.UnpackResponseContent(send, response);
    }

    // endregion

    // region Public Properties

    public byte getCpuTo(){
        return this.cpuTo;
    }

    public void setCpuTo( byte value ){
        this.cpuTo =value;
    }

    public byte getCpuFrom(){
        return this.cpuFrom;
    }

    public void setCpuFrom(byte value){
        this.cpuFrom = value;
    }

    // endregion

    // region Read Write

    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        return MemobusHelper.ReadBool( this, address, length );
    }

    public OperateResult Write(String address, boolean value ) {
        return MemobusHelper.Write( this, address, value );
    }

    public OperateResult Write( String address, boolean[] value ) {
        return MemobusHelper.Write( this, address, value );
    }

    public OperateResultExOne<byte[]> Read( String address, short length ) {
        return MemobusHelper.Read( this, address, length );
    }

    public OperateResult Write( String address, byte[] value ) {
        return MemobusHelper.Write( this, address, value );
    }

    public OperateResult Write( String address, short value ) {
        if (address.matches("^[0-9]+$"))
            return MemobusHelper.Write(this, address, value);
        else
            return super.Write(address, value);
    }

    // endregion

    // region Random Read Write

    /**
     * {@link MemobusHelper#ReadRandom(IMemobus, String[])}
     */
    public OperateResultExOne<byte[]> ReadRandom( String[] address ) {
        return MemobusHelper.ReadRandom( this, address );
    }

    /**
     * {@link MemobusHelper#ReadRandom(IMemobus, short[])}
     */
    public OperateResultExOne<byte[]> ReadRandom( short[] address ) {
        return MemobusHelper.ReadRandom( this, address );
    }

    /**
     * {@link MemobusHelper#WriteRandom(IMemobus, int[], byte[])}
     */
    public OperateResult WriteRandom(int[] address, byte[] value ) {
        return MemobusHelper.WriteRandom( this, address, value );
    }

    // endregion

    // region Private Member

    private byte cpuTo = 0x02;
    private byte cpuFrom = 0x01;
    private SoftIncrementCount softIncrementCount;              // 自增消息的对象

    // endregion

    // region Object Override

    public String toString( ) {
        return "MemobusTcpNet[" + getIpAddress() + ":" + getPort() + "]";
    }
    // endregion
}
