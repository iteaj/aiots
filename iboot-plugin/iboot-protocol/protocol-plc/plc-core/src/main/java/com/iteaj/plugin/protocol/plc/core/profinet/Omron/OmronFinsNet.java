package com.iteaj.plugin.protocol.plc.core.profinet.Omron;

import com.iteaj.plugin.protocol.plc.core.message.FinsMessage;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.DataFormat;
import com.iteaj.plugin.protocol.plc.core.transfer.ReverseWordTransform;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.net.Socket;
import java.util.Date;

/**
 * 欧姆龙PLC通讯类，采用Fins-Tcp通信协议实现，支持的地址信息参见api文档信息。<br />
 * Profinet.Omron PLC communication class is implemented using Fins-Tcp communication protocol. For the supported address information, please refer to the api document information.
 */
public class OmronFinsNet extends NetworkDeviceBase implements IOmronFins {
    /**
     * 实例化一个欧姆龙Fins帧协议的通讯对象<br />
     * Instantiate a communication object of Profinet.Omron PLC Fins frame protocol
     */
    public OmronFinsNet() {
        WordLength = 1;
        ReverseWordTransform transform = new ReverseWordTransform();
        transform.setDataFormat(DataFormat.CDAB);
        transform.setIsStringReverse(true);
        setByteTransform(transform);
    }

    /**
     * 指定ip地址和端口号来实例化一个欧姆龙PLC Fins帧协议的通讯对象<br />
     * Specify the IP address and port number to instantiate a communication object of the Profinet.Omron PLC Fins frame protocol
     * @param ipAddress PLCd的Ip地址
     * @param port PLC的端口
     */
    public OmronFinsNet(String ipAddress, int port) {
        this();
        setIpAddress(ipAddress);
        setPort(port);;
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new FinsMessage( );
    }

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
     * 上位机的节点地址，默认是0x01，当连接PLC之后，将由PLC来设定当前的值。<br />
     * The node address of the host computer is 0x01 by default. After connecting to the PLC, the PLC will set the current value.
     */
    public byte SA1 = 0x01;

    /**
     * 重写Ip地址的赋值的实现
     * @param ipAddress IP地址
     */
    @Override
    public void setIpAddress(String ipAddress) {
        super.setIpAddress(ipAddress);
        DA1 = (byte) Integer.parseInt( ipAddress.substring( ipAddress.lastIndexOf( "." ) + 1 ) );
    }

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

    /**
     * 获取多字节数据的反转类型，适用于int,float,double,long类型的数据
     * @return 变换规则
     */
    public DataFormat getDataFormat() {
        return getByteTransform().getDataFormat();
    }

    /**
     * 字符串数据是否发生反转
     *
     * @return bool值
     */
    public boolean isStringReverse() {
        return getByteTransform().getIsStringReverse();
    }

    /**
     * 设置多字节数据的反转类型，适用于int,float,double,long类型的数据
     *
     * @param dataFormat 数据类型
     */
    public void setDataFormat(DataFormat dataFormat) {
        getByteTransform().setDataFormat(dataFormat);
    }

    /**
     * 设置字符串数据是否反转
     *
     * @param stringReverse bool值
     */
    public void setStringReverse(boolean stringReverse) {
        getByteTransform().setIsStringReverse(stringReverse);
    }

    /**
     * 将普通的指令打包成完整的指令
     * @param cmd 指令
     * @return 字节
     */
    private byte[] PackCommand(byte[] cmd) {
        byte[] buffer = new byte[26 + cmd.length];
        System.arraycopy(handSingle, 0, buffer, 0, 4);

        byte[] tmp = Utilities.getBytes(buffer.length - 8);
        Utilities.bytesReverse(tmp);    // 翻转数组

        System.arraycopy(tmp, 0, buffer, 4, tmp.length);
        buffer[11] = 0x02;

        buffer[16] = ICF;
        buffer[17] = RSV;
        buffer[18] = GCT;
        buffer[19] = DNA;
        buffer[20] = DA1;
        buffer[21] = DA2;
        buffer[22] = SNA;
        buffer[23] = SA1;
        buffer[24] = SA2;
        buffer[25] = SID;
        System.arraycopy(cmd, 0, buffer, 26, cmd.length);

        return buffer;
    }


    @Override
    protected byte[] PackCommandWithHeader(byte[] command) {
        return PackCommand(command);
    }

    @Override
    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        return OmronFinsNetHelper.ResponseValidAnalysis(response);
    }

    protected OperateResult InitializationOnConnect(Socket socket) {
        // handSingle就是握手信号字节
        OperateResultExOne<byte[]> read = ReadFromCoreServer(socket, handSingle, true, false);
        if (!read.IsSuccess) return read;

        // 检查返回的状态
        byte[] buffer = new byte[4];
        buffer[0] = read.Content[15];
        buffer[1] = read.Content[14];
        buffer[2] = read.Content[13];
        buffer[3] = read.Content[12];
        int status = Utilities.getInt(buffer, 0);
        if (status != 0) return new OperateResult( status, OmronFinsNetHelper.GetStatusDescription( status ) );

        // 提取PLC的节点地址
        if (read.Content.length >= 20) SA1 = read.Content[19];
        if (read.Content.length >= 24) DA1 = read.Content[23];

        return OperateResult.CreateSuccessResult();
    }

    /**
     * 从欧姆龙PLC中读取想要的数据，返回读取结果，读取长度的单位为字，地址格式为"D100","C100","W100","H100","A100"<br />
     * Read the desired data from the Profinet.Omron PLC and return the read result. The unit of the read length is word. The address format is "D100", "C100", "W100", "H100", "A100"
     * @param address 读取地址，格式为"D100","C100","W100","H100","A100"
     * @param length 读取的数据长度，字最大值960，位最大值7168
     * @return 带成功标志的结果数据对象
     */
    @Override
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

    /**
     * 向PLC写入数据，数据格式为原始的字节类型，地址格式为"D100","C100","W100","H100","A100"<br />
     * Write data to PLC, the data format is the original byte type, and the address format is "D100", "C100", "W100", "H100", "A100"
     * @param address 起始地址
     * @param value 原始数据
     * @return 结果
     */
    @Override
    public OperateResult Write(String address, byte[] value) {
        return OmronFinsNetHelper.Write(this, address, value);
    }

    /**
     * 从欧姆龙PLC中批量读取位软元件，地址格式为"D100.0","C100.0","W100.0","H100.0","A100.0"<br />
     * Read bit devices in batches from Profinet.Omron PLC with address format "D100.0", "C100.0", "W100.0", "H100.0", "A100.0"
     * @param address 读取地址，格式为"D100","C100","W100","H100","A100"
     * @param length 读取的长度
     * @return 带成功标志的结果数据对象
     */
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return OmronFinsNetHelper.ReadBool(this, address, length, ReadSplits);
    }

    /**
     * 向PLC中位软元件写入bool数组，返回是否写入成功，比如你写入D100,values[0]对应D100.0，地址格式为"D100.0","C100.0","W100.0","H100.0","A100.0"<br />
     * Write the bool array to the PLC's median device and return whether the write was successful. For example, if you write D100, values [0] corresponds to D100.0
     * and the address format is "D100.0", "C100.0", "W100. 0 "," H100.0 "," A100.0 "
     * @param address 要写入的数据地址
     * @param values 要写入的实际数据，可以指定任意的长度
     * @return 返回写入结果
     */
    public OperateResult Write(String address, boolean[] values) {
        return OmronFinsNetHelper.Write(this,address,values);
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


    // 握手信号
    // 46494E530000000C0000000000000000000000D6
    private final byte[] handSingle = new byte[]
            {
                    0x46, 0x49, 0x4E, 0x53, // FINS
                    0x00, 0x00, 0x00, 0x0C, // 后面的命令长度
                    0x00, 0x00, 0x00, 0x00, // 命令码
                    0x00, 0x00, 0x00, 0x00, // 错误码
                    0x00, 0x00, 0x00, 0x00  // 节点号, 为0的话，自动获取
            };
    private OmronPlcType plcType = OmronPlcType.CSCJ;

    /**
     * 返回表示当前对象的字符串
     *
     * @return 字符串
     */
    @Override
    public String toString() {
        return "OmronFinsNet[" + getIpAddress() + ":" + getPort() + "]";
    }
}
