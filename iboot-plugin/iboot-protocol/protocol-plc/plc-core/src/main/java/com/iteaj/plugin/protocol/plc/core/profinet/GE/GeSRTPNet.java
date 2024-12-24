package com.iteaj.plugin.protocol.plc.core.profinet.GE;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.basic.SoftIncrementCount;
import com.iteaj.plugin.protocol.plc.core.address.GeSRTPAddress;
import com.iteaj.plugin.protocol.plc.core.message.GeSRTPMessage;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;

import java.net.Socket;
import java.util.Date;

public class GeSRTPNet extends NetworkDeviceBase {
    /**
     * 实例化一个默认的对象<br />
     * Instantiate a default object
     */
    public GeSRTPNet( ) {
        setByteTransform(new RegularByteTransform());
        WordLength = 2;
    }

    /**
     * 指定IP地址来实例化一个对象，端口默认 18245
     * @param ipAddress IP地址信息
     */
    public GeSRTPNet(String ipAddress ) {
        setByteTransform(new RegularByteTransform());
        WordLength = 2;
        setIpAddress(ipAddress);
        setPort(18245);
    }

    /**
     * 指定IP地址和端口号来实例化一个对象<br />
     * Specify the IP address and port number to instantiate an object
     * @param ipAddress Ip地址
     * @param port 端口号
     */
    public GeSRTPNet( String ipAddress, int port ) {
        setByteTransform(new RegularByteTransform());
        WordLength = 2;
        setIpAddress(ipAddress);
        setPort(port);
    }

    protected INetMessage GetNewNetMessage( ) {
        return new GeSRTPMessage( );
    }

    protected OperateResult InitializationOnConnect(Socket socket ) {
        OperateResultExOne<byte[]> read = ReadFromCoreServer(socket, new byte[56], true, true);
        if (!read.IsSuccess) return read;

        return OperateResult.CreateSuccessResult();
    }

    /**
     * 批量读取字节数组信息，需要指定地址和长度，返回原始的字节数组，支持 I,Q,M,T,SA,SB,SC,S,G 的位和字节读写，支持 AI,AQ,R 的字读写操作，地址示例：R1,M1<br />
     * Batch read byte array information, need to specify the address and length, return the original byte array.
     * Support I, Q, M, T, SA, SB, SC, S, G bit and byte read and write, support AI, AQ, R word read and write operations, address examples: R1, M1
     * <br/><br />
     * 其中读取R，AI，AQ寄存器的原始字节时，传入的长度参数为字节长度。长度为10，返回10个字节数组信息，如果返回长度不满6个字节的，一律返回6个字节的数据
     * @param address 数据地址
     * @param length 数据长度
     * @return 带有成功标识的byte[]数组
     */
    public OperateResultExOne<byte[]> Read(String address, short length ) {
        OperateResultExOne<byte[]> build = GeHelper.BuildReadCommand(incrementCount.GetCurrentValue(), address, length, false);
        if (!build.IsSuccess) return build;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(build.Content);
        if (!read.IsSuccess) return read;

        return GeHelper.ExtraResponseContent(read.Content);
    }

    public OperateResult Write( String address, byte[] value ) {
        OperateResultExOne<byte[]> build = GeHelper.BuildWriteCommand(incrementCount.GetCurrentValue(), address, value);
        if (!build.IsSuccess) return build;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(build.Content);
        if (!read.IsSuccess) return read;

        return GeHelper.ExtraResponseContent(read.Content);
    }

    /**
     * 根据指定的地址来读取一个字节的数据，按照字节为单位，例如 M1 字节，就是指 M1-M8 位组成的字节，M2 字节就是 M9-M16 组成的字节。不支持对 AI,AQ,R 寄存器的字节读取<br />
     * A byte of data is read according to the specified address, and the unit is byte. For example, M1 byte refers to a byte composed of M1-M8 bits,
     * and M2 byte is a byte composed of M9-M16. Does not support byte reading of AI, AQ, R registers
     * @param address 地址信息
     * @return 带有成功标识的 {@link Byte} 数据
     */
    public OperateResultExOne<Byte> ReadByte( String address ) {
        OperateResultExOne<GeSRTPAddress> analysis = GeSRTPAddress.ParseFrom(address, (short) 1, true);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        if (analysis.Content.getDataCode() == 0x0A ||
                analysis.Content.getDataCode() == 0x0C ||
                analysis.Content.getDataCode() == 0x08) {
            return new OperateResultExOne<Byte>(StringResources.Language.GeSRTPNotSupportByteReadWrite());
        }

        OperateResultExOne<byte[]> read = Read(address, (short) 1);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(read.Content[0]);
    }

    /**
     * 向PLC中写入byte数据，返回是否写入成功<br />
     * Write byte data to PLC and return whether the writing is successful
     * @param address 起始地址，格式为I100，M100，Q100，DB20.100 -> Starting address, formatted as I100,mM100,Q100,DB20.100
     * @param value byte数据 -> Byte data
     * @return 是否写入成功的结果对象 -> Whether to write a successful result object
     */
    public OperateResult Write( String address, byte value ) {
        OperateResultExOne<GeSRTPAddress> analysis = GeSRTPAddress.ParseFrom(address, (short) 1, true);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        if (analysis.Content.getDataCode() == 0x0A ||
                analysis.Content.getDataCode() == 0x0C ||
                analysis.Content.getDataCode() == 0x08) {
            return new OperateResultExOne<Byte>(StringResources.Language.GeSRTPNotSupportByteReadWrite());
        }

        return Write(address, new byte[]{value});
    }

    /**
     * 按照位为单位，批量从指定的地址里读取 bool 数组数据，不支持 AI，AQ，R 地址类型，地址比如从1开始，例如 I1,Q1,M1,T1,SA1,SB1,SC1,S1,G1<br />
     * R address types are not supported. For example, the address starts from 1, such as I1, Q1, M1, T1, SA1, SB1, SC1, S1, G1
     * In units of bits, read bool array data from the specified address in batches. AI, AQ,
     * @param address PLC的地址信息，例如 M1, G1
     * @param length 读取的长度信息，按照位为单位
     * @return 包含是否读取成功的bool数组
     */
    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        OperateResultExOne<GeSRTPAddress> analysis = GeSRTPAddress.ParseFrom(address, length, true);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        OperateResultExOne<byte[]> build = GeHelper.BuildReadCommand(incrementCount.GetCurrentValue(), analysis.Content);
        if (!build.IsSuccess) return OperateResultExOne.CreateFailedResult(build);

        OperateResultExOne<byte[]> read = ReadFromCoreServer(build.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        OperateResultExOne<byte[]> extra = GeHelper.ExtraResponseContent(read.Content);
        if (!extra.IsSuccess) return OperateResultExOne.CreateFailedResult(extra);

        return OperateResultExOne.CreateSuccessResult(
                SoftBasic.BoolArraySelectMiddle(
                        SoftBasic.ByteToBoolArray(extra.Content), analysis.Content.getAddressStart() % 8, length));
    }

    /**
     * 按照位为单位，批量写入 bool 数组到指定的地址里，不支持 AI，AQ，R 地址类型，地址比如从1开始，例如 I1,Q1,M1,T1,SA1,SB1,SC1,S1,G1<br />
     * In units of bits, write bool arrays in batches to the specified addresses. AI, AQ, and R address types are not supported. For example,
     * the address starts from 1, such as I1, Q1, M1, T1, SA1, SB1, SC1, S1, G1
     * @param address PLC的地址信息，例如 M1, G1
     * @param value bool 数组
     * @return 是否写入成功的结果对象
     */
    public OperateResult Write( String address, boolean[] value ) {
        OperateResultExOne<byte[]> build = GeHelper.BuildWriteCommand(incrementCount.GetCurrentValue(), address, value);
        if (!build.IsSuccess) return build;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(build.Content);
        if (!read.IsSuccess) return read;

        return GeHelper.ExtraResponseContent(read.Content);
    }

    /**
     * <b>[商业授权]</b> 读取PLC当前的时间，这个时间可能是不包含时区的，需要自己转换成本地的时间。<br />
     * <b>[Authorization]</b> Read the current time of the PLC, this time may not include the time zone, you need to convert the local time yourself.
     * @return 包含是否成功的时间信息
     */
    public OperateResultExOne<Date> ReadPLCTime( ) {
        OperateResultExOne<byte[]> build = GeHelper.BuildReadCoreCommand(incrementCount.GetCurrentValue(), (byte) 0x25, new byte[]{0x00, 0x00, 0x00, 0x02, 0x00});
        if (!build.IsSuccess) return OperateResultExOne.CreateFailedResult(build);

        OperateResultExOne<byte[]> read = ReadFromCoreServer(build.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        OperateResultExOne<byte[]> extra = GeHelper.ExtraResponseContent(read.Content);
        if (!extra.IsSuccess) return OperateResultExOne.CreateFailedResult(extra);

        return GeHelper.ExtraDateTime(extra.Content);
    }

    /**
     * <b>[商业授权]</b> 读取PLC当前的程序的名称<br />
     * <b>[Authorization]</b> Read the name of the current program of the PLC
     * @return 包含是否成的程序名称信息
     */
    public OperateResultExOne<String> ReadProgramName( )
    {
        OperateResultExOne<byte[]> build = GeHelper.BuildReadCoreCommand( incrementCount.GetCurrentValue( ), (byte) 0x01, new byte[] { 0x00, 0x00, 0x00, 0x02, 0x00 } );
        if (!build.IsSuccess) return OperateResultExOne.CreateFailedResult( build );

        OperateResultExOne<byte[]> read = ReadFromCoreServer( build.Content );
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

        OperateResultExOne<byte[]> extra = GeHelper.ExtraResponseContent( read.Content );
        if (!extra.IsSuccess) return OperateResultExOne.CreateFailedResult( extra );

        return GeHelper.ExtraProgramName( extra.Content );
    }

    private SoftIncrementCount incrementCount = new SoftIncrementCount( 65535 );

    public String toString( ) {
        return "GeSRTPNet[" + getIpAddress() + ":" + getPort() + "]";
    }

}
