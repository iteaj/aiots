package com.iteaj.plugin.protocol.plc.omron.network;

import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkUdpDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.DataFormat;
import com.iteaj.plugin.protocol.plc.core.transfer.ReverseWordTransform;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

import java.util.Date;

public class OmronFinsUdp extends NetworkUdpDeviceBase implements IOmronFins {
    //region Constructor

    public OmronFinsUdp(String ipAddress, int port) {
        WordLength = 1;
        setIpAddress(ipAddress);
        setPort(port);
        ;
        ReverseWordTransform transform = new ReverseWordTransform();
        transform.setDataFormat(DataFormat.CDAB);
        transform.setIsStringReverse(true);
        setByteTransform(transform);
    }

    /// <inheritdoc cref="OmronFinsNet()"/>
    public OmronFinsUdp() {
        WordLength = 1;
        ReverseWordTransform transform = new ReverseWordTransform();
        transform.setDataFormat(DataFormat.CDAB);
        transform.setIsStringReverse(true);
        setByteTransform(transform);
    }

    //endregion

    //region IpAddress Override

    /**
     * 重写Ip地址的赋值的实现
     *
     * @param ipAddress IP地址
     */
    @Override
    public void setIpAddress(String ipAddress) {
        super.setIpAddress(ipAddress);
        DA1 = (byte) Integer.parseInt(ipAddress.substring(ipAddress.lastIndexOf(".") + 1));
    }

    //endregion

    //region Public Member

    /**
     * 信息控制字段，默认0x80
     * Information control field, default 0x80
     */
    public byte ICF = (byte) 0x80;

    /**
     * 系统使用的内部信息
     * Internal information used by the system
     */
    public byte RSV = 0x00;

    /**
     * 网络层信息，默认0x02，如果有八层消息，就设置为0x07
     * Network layer information, default is 0x02, if there are eight layers of messages, set to 0x07
     */
    public byte GCT = 0x02;

    /**
     * PLC的网络号地址，默认0x00
     * PLC network number address, default 0x00
     */
    public byte DNA = 0x00;


    /**
     * PLC的节点地址，这个值在配置了ip地址之后是默认赋值的，默认为Ip地址的最后一位<br />
     * PLC node address. This value is assigned by default after the IP address is configured. The default is the last bit of the IP address.
     */
    public byte DA1 = 0x13;

    /**
     * PLC的单元号地址，通常都为0<br />
     * PLC unit number address, usually 0
     */
    public byte DA2 = 0x00;

    /**
     * 上位机的网络号地址<br />
     * Network number and address of the computer
     */
    public byte SNA = 0x00;

    /**
     * 上位机的节点地址，假如你的电脑的Ip地址为192.168.0.13，那么这个值就是13<br />
     * The node address of the upper computer. If your computer's IP address is 192.168.0.13, then this value is 13
     */
    public byte SA1 = 0x0B;

    /**
     * 上位机的单元号地址<br />
     * Unit number and address of the computer
     */
    public byte SA2 = 0x00;

    /**
     * 设备的标识号<br />
     * Device identification number
     */
    public byte SID = 0x00;

    /**
     * 进行字读取的时候对于超长的情况按照本属性进行切割，默认500，如果不是CP1H及扩展模块的，可以设置为999，可以提高一倍的通信速度。<br />
     * When reading words, it is cut according to this attribute for the case of overlength. The default is 500.
     * If it is not for CP1H and expansion modules, it can be set to 999, which can double the communication speed.
     */
    public int ReadSplits = 500;

    public OmronPlcType getPlcType(){
        return this.plcType;
    }

    public void setPlcType( OmronPlcType plcType ){
        this.plcType = plcType;
    }


    //endregion

    // region Message Pack Extra

    protected byte[] PackCommandWithHeader( byte[] command ) {
        return PackCommand(command);
    }

    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response ) {
        return OmronFinsNetHelper.UdpResponseValidAnalysis(response);
    }

    // endregion

    //region Build Command

    private byte[] PackCommand(byte[] cmd) {
        byte[] buffer = new byte[10 + cmd.length];
        buffer[0] = ICF;
        buffer[1] = RSV;
        buffer[2] = GCT;
        buffer[3] = DNA;
        buffer[4] = DA1;
        buffer[5] = DA2;
        buffer[6] = SNA;
        buffer[7] = SA1;
        buffer[8] = SA2;
        buffer[9] = SID;
        System.arraycopy(cmd, 0, buffer, 10, cmd.length);

        return buffer;
    }

    //endregion

    //region Read Write Support

    public OperateResultExOne<byte[]> Read(String address, short length) {
        return OmronFinsNetHelper.Read(this, address, length, ReadSplits);
    }

    /**
     * 从欧姆龙PLC中读取多个地址的数据，返回读取结果，每个地址按照字为单位读取，地址格式为"D100","C100","W100","H100","A100"
     * @param address 从欧姆龙PLC中读取多个地址的数据，返回读取结果，每个地址按照字为单位读取，地址格式为"D100","C100","W100","H100","A100"
     * @return 带成功标志的结果数据对象
     */
    public OperateResultExOne<byte[]> Read( String[] address ) {
        return OmronFinsNetHelper.Read(this, address);
    }

    public OperateResult Write(String address, byte[] value) {
        return OmronFinsNetHelper.Write(this, address, value);
    }

    //endregion

    //region Read Write bool

    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return OmronFinsNetHelper.ReadBool(this, address, length, ReadSplits);
    }

    public OperateResult Write(String address, boolean[] values) {
        return OmronFinsNetHelper.Write(this, address, values);
    }


    public OperateResult Run( ) {
        return OmronFinsNetHelper.Run( this );
    }

    public OperateResult Stop( ) {
        return OmronFinsNetHelper.Stop( this );
    }

    public OperateResultExOne<OmronCpuUnitData> ReadCpuUnitData( ) {
        return OmronFinsNetHelper.ReadCpuUnitData( this );
    }

    public OperateResultExOne<OmronCpuUnitStatus> ReadCpuUnitStatus( ) {
        return OmronFinsNetHelper.ReadCpuUnitStatus( this );
    }

    public OperateResultExOne<Date> ReadCpuTime( ) {
        return OmronFinsNetHelper.ReadCpuTime( this );
    }
    //endregion

    //region Object Override

    private OmronPlcType plcType = OmronPlcType.CSCJ;

    public String toString() {
        return "OmronFinsUdp[" + getIpAddress() + ":" + getPort() + "]";
    }

    //endregion
}
