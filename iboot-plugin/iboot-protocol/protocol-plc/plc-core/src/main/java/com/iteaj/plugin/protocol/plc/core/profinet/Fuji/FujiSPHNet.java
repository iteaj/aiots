package com.iteaj.plugin.protocol.plc.core.profinet.Fuji;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.address.FujiSPHAddress;
import com.iteaj.plugin.protocol.plc.core.message.FujiSPHMessage;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.util.ArrayList;

public class FujiSPHNet extends NetworkDeviceBase {
    /**
     * 实例化一个默认的对象<br />
     * Instantiate a default object
     */
    public FujiSPHNet( ) {
        setByteTransform(new RegularByteTransform());
        WordLength = 1;
        setPort(18245);
    }

    /**
     * 指定IP地址和端口号来实例化一个对象<br />
     * Specify the IP address and port number to instantiate an object
     * @param ipAddress Ip地址
     * @param port 端口号
     */
    public FujiSPHNet( String ipAddress, int port ) {
        setByteTransform(new RegularByteTransform());
        WordLength = 1;
        setIpAddress(ipAddress);
        setPort(port);
    }

    /// <inheritdoc/>
    protected INetMessage GetNewNetMessage( )  {
        return new FujiSPHMessage( );
    }

    /**
     * 获取连接的CPU的站号，对于 CPU0-CPU7来说是CPU的站号，分为对应 0xFE-0xF7，对于P/PE link, FL-net是模块站号，分别对应0xF6-0xEF<br />
     * Get the cpu station:  CPU0 to CPU7: SX bus station No. of destination CPU (FEh to F7h); P/PE link, FL-net: SX bus station No. of destination module (F6H to EFH)
     * @return 站号信息
     */
    public byte getConnectionID() {
        return ConnectionID;
    }

    /**
     * 设置连接的CPU站号，对于 CPU0-CPU7来说是CPU的站号，分为对应 0xFE-0xF7，对于P/PE link, FL-net是模块站号，分别对应0xF6-0xEF<br />
     * Set the cpu station:  CPU0 to CPU7: SX bus station No. of destination CPU (FEh to F7h); P/PE link, FL-net: SX bus station No. of destination module (F6H to EFH)
     * @param connectionID 连接ID信息
     */
    public void setConnectionID(byte connectionID) {
        ConnectionID = connectionID;
    }

    private byte ConnectionID = (byte)0xFE;


    private OperateResultExOne<byte[]> ReadFujiSPHAddress(FujiSPHAddress address, short length ) {
        OperateResultExOne<ArrayList<byte[]>> command = BuildReadCommand(ConnectionID, address, length);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Byte> array = new ArrayList<>();
        for (int i = 0; i < command.Content.size(); i++) {
            OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content.get(i));
            if (!read.IsSuccess) return read;

            OperateResultExOne<byte[]> extra = ExtractActualData(read.Content);
            if (!extra.IsSuccess) return extra;

            Utilities.ArrayListAddArray(array, extra.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(array));
    }

    /**
     * 批量读取PLC的地址数据，长度单位为字。地址支持M1.1000，M3.1000，M10.1000，返回读取的原始字节数组。<br />
     * Read PLC address data in batches, the length unit is words. The address supports M1.1000, M3.1000, M10.1000,
     * and returns the original byte array read.
     * @param address PLC的地址，支持M1.1000，M3.1000，M10.1000
     * @param length 读取的长度信息，按照字为单位
     * @return 包含byte[]的原始字节数据内容
     */
    public OperateResultExOne<byte[]> Read( String address, short length ) {
        OperateResultExOne<FujiSPHAddress> analysis = FujiSPHAddress.ParseFrom(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        return ReadFujiSPHAddress(analysis.Content, length);
    }

    /**
     * 批量写入字节数组到PLC的地址里，地址支持M1.1000，M3.1000，M10.1000，返回是否写入成功。<br />
     * Batch write byte array to PLC address, the address supports M1.1000, M3.1000, M10.1000,
     * and return whether the writing is successful.
     * @param address PLC的地址，支持M1.1000，M3.1000，M10.1000
     * @param value 写入的原始字节数据
     * @return 是否写入成功
     */
    public OperateResult Write(String address, byte[] value ) {
        OperateResultExOne<byte[]> command = BuildWriteCommand(ConnectionID, address, value);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        OperateResultExOne<byte[]> extra = ExtractActualData(read.Content);
        if (!extra.IsSuccess) return extra;

        return OperateResult.CreateSuccessResult();
    }

    /**
     * 批量读取位数据的方法，需要传入位地址，读取的位长度，地址示例：M1.100.5，M3.1000.12，M10.1000.0<br />
     * To read the bit data in batches, you need to pass in the bit address, the length of the read bit, address examples: M1.100.5, M3.1000.12, M10.1000.0
     * @param address PLC的地址，示例：M1.100.5，M3.1000.12，M10.1000.0
     * @param length 读取的bool长度信息
     * @return 包含bool[]的结果对象
     */
    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        OperateResultExOne<FujiSPHAddress> analysis = FujiSPHAddress.ParseFrom(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        int bitCount = analysis.Content.getBitIndex() + length;
        int wordLength = bitCount % 16 == 0 ? bitCount / 16 : bitCount / 16 + 1;

        OperateResultExOne<byte[]> read = ReadFujiSPHAddress(analysis.Content, (short) wordLength);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(
                SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray(read.Content), analysis.Content.getBitIndex(), length));
    }

    /**
     * 批量写入位数据的方法，需要传入位地址，等待写入的boo[]数据，地址示例：M1.100.5，M3.1000.12，M10.1000.0<br />
     * To write bit data in batches, you need to pass in the bit address and wait for the boo[] data to be written. Examples of addresses: M1.100.5, M3.1000.12, M10.1000.0
     * <br />
     * <br />
     * [警告] 由于协议没有提供位写入的命令，所有通过字写入间接实现，先读取字数据，修改中间的位，然后写入字数据，所以本质上不是安全的，确保相关的地址只有上位机可以写入。<br />
     * [Warning] Since the protocol does not provide commands for bit writing, all are implemented indirectly through word writing. First read the word data,
     * modify the bits in the middle, and then write the word data, so it is inherently not safe. Make sure that the relevant address is only The host computer can write.
     * @param address PLC的地址，示例：M1.100.5，M3.1000.12，M10.1000.0
     * @param value 等待写入的bool数组
     * @return 是否写入成功的结果对象
     */
    public OperateResult Write( String address, boolean[] value ) {
        OperateResultExOne<FujiSPHAddress> analysis = FujiSPHAddress.ParseFrom(address);
        if (!analysis.IsSuccess) return analysis;

        int bitCount = analysis.Content.getBitIndex() + value.length;
        int wordLength = bitCount % 16 == 0 ? bitCount / 16 : bitCount / 16 + 1;

        OperateResultExOne<byte[]> read = ReadFujiSPHAddress(analysis.Content, (short) wordLength);
        if (!read.IsSuccess) return read;

        // 修改其中的某几个位，然后一起批量写入操作
        boolean[] writeBoolArray = SoftBasic.ByteToBoolArray(read.Content);
        System.arraycopy(value, 0, writeBoolArray, analysis.Content.getBitIndex(), value.length);

        OperateResultExOne<byte[]> command = BuildWriteCommand(ConnectionID, address, SoftBasic.BoolArrayToByte(writeBoolArray));
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> write = ReadFromCoreServer(command.Content);
        if (!write.IsSuccess) return write;

        OperateResultExOne<byte[]> extra = ExtractActualData(write.Content);
        if (!extra.IsSuccess) return extra;

        return OperateResult.CreateSuccessResult();
    }

    /**
     * This command is used to start all the CPUs that exist in a configuration in a batch.
     * Each CPU is cold-started or warm-started,depending on its condition. If a CPU is already started up,
     * or if the key switch is set at "RUN" position, the CPU does not perform processing for startup,
     * which, however, does not result in an error, and a response is returned normally
     * @return 是否启动成功
     */
    public OperateResult CpuBatchStart( ) {
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand(ConnectionID, (byte) 0x04, (byte) 0x00, null));
        if (!read.IsSuccess) return read;

        return ExtractActualData(read.Content);
    }

    /**
     * This command is used to initialize and start all the CPUs that exist in a configuration in a batch. Each CPU is cold-started.
     * If a CPU is already started up, or if the key switch is set at "RUN" position, the CPU does not perform processing for initialization
     * and startup, which, however, does not result in an error, and a response is returned normally.
     * @return 是否启动成功
     */
    public OperateResult CpuBatchInitializeAndStart( ) {
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand(ConnectionID, (byte) 0x04, (byte) 0x01, null));
        if (!read.IsSuccess) return read;

        return ExtractActualData(read.Content);
    }

    /**
     * This command is used to stop all the CPUs that exist in a configuration in a batch.
     * If a CPU is already stopped, or if the key switch is set at "RUN" position, the CPU does not perform processing for stop, which,
     * however, does not result in an error, and a response is returned normally.
     * @return 是否停止成功
     */
    public OperateResult CpuBatchStop( ) {
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand(ConnectionID, (byte) 0x04, (byte) 0x02, null));
        if (!read.IsSuccess) return read;

        return ExtractActualData(read.Content);
    }

    /**
     * This command is used to stop all the CPUs that exist in a configuration in a batch.
     * If a CPU is already stopped, or if the key switch is set at "RUN" position, the CPU does not perform processing for stop, which,
     * however, does not result in an error, and a response is returned normally.
     * @return 是否复位成功
     */
    public OperateResult CpuBatchReset( ) {
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand(ConnectionID, (byte) 0x04, (byte) 0x03, null));
        if (!read.IsSuccess) return read;

        return ExtractActualData(read.Content);
    }

    /**
     * This command is used to start an arbitrary CPU existing in a configuration by specifying it. The CPU may be cold-started or
     * warm-started, depending on its condition. An error occurs if the CPU is already started. A target CPU is specified by a connection
     * mode and connection ID.
     * @return 是否启动成功
     */
    public OperateResult CpuIndividualStart( ) {
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand(ConnectionID, (byte) 0x04, (byte) 0x04, null));
        if (!read.IsSuccess) return read;

        return ExtractActualData(read.Content);
    }

    /**
     * This command is used to initialize and start an arbitrary CPU existing in a configuration by specifying it. The CPU is cold-started.
     * An error occurs if the CPU is already started or if the key switch is set at "RUN" or "STOP" position. A target CPU is specified by
     * a connection mode and connection ID.
     * @return 是否启动成功
     */
    public OperateResult CpuIndividualInitializeAndStart( ) {
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand(ConnectionID, (byte) 0x04, (byte) 0x05, null));
        if (!read.IsSuccess) return read;

        return ExtractActualData(read.Content);
    }

    /**
     * This command is used to stop an arbitrary CPU existing in a configuration by specifying it. An error occurs if the CPU is already
     * stopped or if the key switch is set at "RUN" or "STOP" position. A target CPU is specified by a connection mode and connection ID.
     * @return 是否停止成功
     */
    public OperateResult CpuIndividualStop( ) {
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand(ConnectionID, (byte) 0x04, (byte) 0x06, null));
        if (!read.IsSuccess) return read;

        return ExtractActualData(read.Content);
    }

    /**
     * This command is used to reset an arbitrary CPU existing in a configuration by specifying it. An error occurs if the key switch is
     * set at "RUN" or "STOP" position. A target CPU is specified by a connection mode and connection ID.
     * @return 是否复位成功
     */
    public OperateResult CpuIndividualReset( ) {
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand(ConnectionID, (byte) 0x04, (byte) 0x07, null));
        if (!read.IsSuccess) return read;

        return ExtractActualData(read.Content);
    }

    public String toString( ) {
        return "FujiSPHNet[" + getIpAddress() + ":" + getPort() + "]";
    }

    //region Static Helper

    /**
     * 根据错误代号获取详细的错误描述信息
     * @param code 错误码
     * @return 错误的描述文本
     */
    public static String GetErrorDescription( byte code ) {
        switch (code) {
            case 0x10:
                return "Command cannot be executed because an error occurred in the CPU.";
            case 0x11:
                return "Command cannot be executed because the CPU is running.";
            case 0x12:
                return "Command cannot be executed due to the key switch condition of the CPU.";
            case 0x20:
                return "CPU received undefined command or mode.";
            case 0x22:
                return "Setting error was found in command header part.";
            case 0x23:
                return "Transmission is interlocked by a command from another device.";
            case 0x28:
                return "Requested command cannot be executed because another command is now being executed.";
            case 0x2B:
                return "Requested command cannot be executed because the loader is now performing another processing( including program change).";
            case 0x2F:
                return "Requested command cannot be executed because the system is now being initialized.";
            case 0x40:
                return "Invalid data type or number was specified.";
            case 0x41:
                return "Specified data cannot be found.";
            case 0x44:
                return "Specified address exceeds the valid range.";
            case 0x45:
                return "Address + the number of read/write words exceed the valid range.";
            case (byte) 0xA0:
                return "No module exists at specified destination station No.";
            case (byte) 0xA2:
                return "No response data is returned from the destination module.";
            case (byte) 0xA4:
                return "Command cannot be communicated because an error occurred in the SX bus.";
            case (byte) 0xA5:
                return "Command cannot be communicated because NAK occurred while sending data via the SX bus.";
            default:
                return StringResources.Language.UnknownError();
        }
    }

    private static byte[] PackCommand( byte connectionId, byte command, byte mode, byte[] data ) {
        if (data == null) data = new byte[0];
        byte[] buffer = new byte[20 + data.length];
        buffer[0] = (byte) 0xFB;
        buffer[1] = (byte) 0x80;
        buffer[2] = (byte) 0x80;
        buffer[3] = 0x00;
        buffer[4] = (byte) 0xFF;
        buffer[5] = 0x7B;
        buffer[6] = connectionId;  // connection id
        buffer[7] = 0x00;
        buffer[8] = 0x11;
        buffer[9] = 0x00;
        buffer[10] = 0x00;
        buffer[11] = 0x00;
        buffer[12] = 0x00;
        buffer[13] = 0x00;
        buffer[14] = command;       // command
        buffer[15] = mode;          // mode
        buffer[16] = 0x00;
        buffer[17] = 0x01;
        buffer[18] = Utilities.getBytes(data.length)[0];  // length
        buffer[19] = Utilities.getBytes(data.length)[1];
        if (data.length > 0)
            System.arraycopy(data, 0, buffer, 20, data.length);
        return buffer;
    }

    /**
     * 构建读取数据的命令报文
     * @param connectionId 连接ID
     * @param address 读取的PLC的地址
     * @param length 读取的长度信息，按照字为单位
     * @return 构建成功的读取报文命令
     */
    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand( byte connectionId, String address, short length ) {
        OperateResultExOne<FujiSPHAddress> analysis = FujiSPHAddress.ParseFrom(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        return BuildReadCommand(connectionId, analysis.Content, length);
    }

    /**
     * 构建读取数据的命令报文
     * @param connectionId 连接ID
     * @param address 读取的PLC的地址
     * @param length 读取的长度信息，按照字为单位
     * @return 构建成功的读取报文命令
     */
    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand( byte connectionId, FujiSPHAddress address, short length ) {
        ArrayList<byte[]> array = new ArrayList<>();
        int[] splits = SoftBasic.SplitIntegerToArray(length, 240);
        for (int i = 0; i < splits.length; i++) {
            byte[] buffer = new byte[6];
            buffer[0] = address.getTypeCode();
            buffer[1] = Utilities.getBytes(address.getAddressStart())[0];
            buffer[2] = Utilities.getBytes(address.getAddressStart())[1];
            buffer[3] = Utilities.getBytes(address.getAddressStart())[2];
            buffer[4] = Utilities.getBytes(splits[i])[0];
            buffer[5] = Utilities.getBytes(splits[i])[1];


            array.add(PackCommand(connectionId, (byte) 0x00, (byte) 0x00, buffer));
            address.setAddressStart(address.getAddressStart() + splits[i]);
        }
        return OperateResultExOne.CreateSuccessResult(array);
    }

    /**
     * 构建写入数据的命令报文
     * @param connectionId 连接ID
     * @param address 写入的PLC的地址
     * @param data 原始数据内容
     * @return 报文信息
     */
    public static OperateResultExOne<byte[]> BuildWriteCommand( byte connectionId, String address, byte[] data ) {
        OperateResultExOne<FujiSPHAddress> analysis = FujiSPHAddress.ParseFrom(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);
        int length = data.length / 2;

        byte[] buffer = new byte[6 + data.length];
        buffer[0] = analysis.Content.getTypeCode();
        buffer[1] = Utilities.getBytes(analysis.Content.getAddressStart())[0];
        buffer[2] = Utilities.getBytes(analysis.Content.getAddressStart())[1];
        buffer[3] = Utilities.getBytes(analysis.Content.getAddressStart())[2];
        buffer[4] = Utilities.getBytes(length)[0];
        buffer[5] = Utilities.getBytes(length)[1];
        System.arraycopy(data, 0, buffer, 6, data.length);
        return OperateResultExOne.CreateSuccessResult(PackCommand(connectionId, (byte) 0x01, (byte) 0x00, buffer));
    }

    /**
     * 从PLC返回的报文里解析出实际的数据内容，如果发送了错误，则返回失败信息
     * @param response PLC返回的报文信息
     * @return 是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> ExtractActualData(byte[] response ) {
        try {
            if (response[4] != 0x00)
                return new OperateResultExOne<byte[]>(response[4], GetErrorDescription(response[4]));
            if (response.length > 26)
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 26));
            return OperateResultExOne.CreateSuccessResult(new byte[0]);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage() + " Source: " + SoftBasic.ByteToHexString(response, ' '));
        }
    }

    //endregion
}
