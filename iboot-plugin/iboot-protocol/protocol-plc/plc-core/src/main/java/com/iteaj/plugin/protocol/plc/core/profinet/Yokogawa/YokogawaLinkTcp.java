package com.iteaj.plugin.protocol.plc.core.profinet.Yokogawa;


import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.address.YokogawaLinkAddress;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.YokogawaLinkBinaryMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.DataFormat;
import com.iteaj.plugin.protocol.plc.core.transfer.ReverseWordTransform;
import com.iteaj.plugin.protocol.plc.core.types.PlcHelper;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExTwo;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * 横河PLC的二进制通信类，支持X,Y,I,E,M,T,C,L继电器类型的数据读写，支持D,B,F,R,V,Z,W,TN,CN寄存器类型的数据读写，还支持一些高级的信息读写接口，详细参考API文档。<br />
 * Yokogawa PLC's binary communication type, supports X, Y, I, E, M, T, C, L relay type data read and write,
 * supports D, B, F, R, V, Z, W, TN, CN registers Types of data reading and writing, and some advanced information reading and writing interfaces are also supported.
 * Please refer to the API documentation for details.
 */
public class YokogawaLinkTcp extends NetworkDeviceBase {
    /**
     * 实例化一个默认的对象<br />
     * Instantiate a default object
     */
    public YokogawaLinkTcp( )
    {
        ReverseWordTransform transform = new ReverseWordTransform();
        transform.setDataFormat(DataFormat.CDAB);
        setByteTransform(transform);
        CpuNumber     = 0x01;
    }

    /**
     * 指定IP地址和端口号来实例化一个对象<br />
     * Specify the IP address and port number to instantiate an object
     * @param ipAddress Ip地址
     * @param port 端口号
     */
    public YokogawaLinkTcp(String ipAddress, int port )
    {
        this();
        setIpAddress(ipAddress);
        setPort(port);
    }

    protected INetMessage GetNewNetMessage( ) {
		    return new YokogawaLinkBinaryMessage( );
    }

    /**
     * 获取或设置当前的CPU Number，默认值为1<br />
     * Get or set the current CPU Number, the default value is 1
     */
    public byte CpuNumber = 1;

    /**
     * 读取的线圈地址支持X,Y,I,E,M,T,C,L，寄存器地址支持D,B,F,R,V,Z,W,TN,CN，举例：D100；也可以携带CPU进行访问，举例：cpu=2;D100<br />
     * <b>[商业授权]</b> 如果想要读取特殊模块的数据，需要使用 <b>Special:</b> 开头标记，举例：Special:unit=0;slot=1;100<br />
     * The read coil address supports X, Y, I, E, M, T, C, L, and the register address supports D, B, F, R, V, Z, W, TN, CN, for example: D100;
     * it can also be carried CPU access, for example: cpu=2;D100. <br />
     * <b>[Authorization]</b> If you want to read the data of a special module, you need to use the <b>Special:</b> beginning tag, for example: Special:unit=0;slot=1;100
     * @param address PLC的地址信息 D, B, F, R, V, Z, W, TN, CN, for example: D100;
     * @param length 读取的地址长度
     * @return 成功读取的对象
     */
    public OperateResultExOne<byte[]> Read(String address, short length ) {
        OperateResultExOne<ArrayList<byte[]>> command;
        if (address.startsWith("Special:") || address.startsWith("special:")) {
            command = BuildReadSpecialModule(CpuNumber, address, length);
        } else {
            command = BuildReadCommand(CpuNumber, address, length, false);
        }
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        for (int i = 0; i < command.Content.size(); i++) {
            OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content.get(i));
            if (!read.IsSuccess) return read;

            OperateResultExOne<byte[]> check = CheckContent(read.Content);
            if (!check.IsSuccess) return check;

            try {
                content.write(check.Content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return OperateResultExOne.CreateSuccessResult(content.toByteArray());
    }

    /**
     * 写入的线圈地址支持Y,I,E,M,T,C,L，寄存器地址支持D,B,F,R,V,Z,W,TN,CN，举例：D100；也可以携带CPU进行访问，举例：cpu=2;D100<br />
     * 如果想要写入特殊模块的数据，需要使用 <b>Special:</b> 开头标记，举例：Special:unit=0;slot=1;100<br />
     * The read coil address supports Y, I, E, M, T, C, L, and the register address supports D, B, F, R, V, Z, W, TN, CN, for example: D100;
     * it can also be carried CPU access, for example: cpu=2;D100.
     * If you want to read the data of a special module, you need to use the <b>Special:</b> beginning tag, for example: Special:unit=0;slot=1;100
     * @param address PLC的地址信息
     * @param value 写入的原始字节数据
     * @return 是否写入成功的结果对象
     */
    public OperateResult Write(String address, byte[] value ) {
        OperateResultExOne<byte[]> command;
        if (address.startsWith("Special:") || address.startsWith("special:")) {
            command = BuildWriteSpecialModule(CpuNumber, address, value);
        } else {
            command = BuildWriteWordCommand(CpuNumber, address, value);
        }
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        return CheckContent(read.Content);
    }

    /**
     * 读取的线圈地址支持X,Y,I,E,M,T,C,L，举例：Y100；也可以携带CPU进行访问，举例：cpu=2;Y100<br />
     * The read coil address supports X, Y, I, E, M, T, C, L, for example: Y100; you can also carry the CPU for access, for example: cpu=2;Y100
     * @param address 读取的起始地址信息
     * @param length 读取的长度信息
     * @return 读取的结果对象
     */
    public OperateResultExOne<boolean[]> ReadBool( String address, short length )
    {
        OperateResultExOne<ArrayList<byte[]>> command = BuildReadCommand( CpuNumber, address, length, true );
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult( command );

        ByteArrayOutputStream content = new ByteArrayOutputStream( );
        for (int i = 0; i < command.Content.size(); i++)
        {
            OperateResultExOne<byte[]> read = ReadFromCoreServer( command.Content.get(i) );
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

            OperateResultExOne<byte[]> check = CheckContent( read.Content );
            if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult( check );

            try {
                content.write( check.Content );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] buffer = content.toByteArray();
        boolean[] array = new boolean[buffer.length];
        for (int i = 0; i < buffer.length; i ++){
            array[i] = buffer[i] != 0x00;
        }
        return OperateResultExOne.CreateSuccessResult( array );
    }

    /**
     * 写入的线圈地址支持Y,I,E,M,T,C,L，举例：Y100；也可以携带CPU进行访问，举例：cpu=2;Y100<br />
     * The write coil address supports Y, I, E, M, T, C, L, for example: Y100; you can also carry the CPU for access, for example: cpu=2;Y100
     * @param address 读取的线圈地址支持X,Y,I,E,M,T,C,L，举例：Y100；也可以携带CPU进行访问，举例：cpu=2;Y100
     * @param value 等待写入的值
     * @return 是否写入成功的结果对象
     */
    public OperateResult Write( String address, boolean[] value ) {
        OperateResultExOne<byte[]> command = BuildWriteBoolCommand(CpuNumber, address, value);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        return CheckContent(read.Content);
    }

    /**
     * <b>[商业授权]</b> 随机读取{@link Boolean}数组信息，主需要出传入{@link String}数组地址信息，就可以返回批量{@link Boolean}值<br />
     * <b>[Authorization]</b> Random read {@link Boolean} array information, the master needs to pass in the {@link String} array address information,
     * and then the batch can be returned to {@link Boolean} value
     * <br /><br />
     * 读取的线圈地址支持X,Y,I,E,M,T,C,L，举例：Y100；也可以携带CPU进行访问，举例：cpu=2;Y100<br />
     * The read coil address supports X, Y, I, E, M, T, C, L, for example: Y100; you can also carry the CPU for access, for example: cpu=2;Y100
     * @param address 批量地址信息
     * @return 带有成功标志的Bool数组信息
     */
    public OperateResultExOne<boolean[]> ReadRandomBool( String[] address ) {

        OperateResultExOne<ArrayList<byte[]>> command = BuildReadRandomCommand(CpuNumber, address, true);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ByteArrayOutputStream lists = new ByteArrayOutputStream();
        for (int i = 0; i < command.Content.size(); i++) {
            byte[] content = command.Content.get(i);

            OperateResultExOne<byte[]> read = ReadFromCoreServer(content);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            OperateResultExOne<byte[]> check = CheckContent(read.Content);
            if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

            try {
                lists.write(check.Content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] buffer = lists.toByteArray();
        boolean[] array = new boolean[buffer.length];
        for (int i = 0; i < buffer.length; i++) {
            array[i] = buffer[i] != 0x00;
        }
        return OperateResultExOne.CreateSuccessResult(array);
    }

    /**
     * <b>[商业授权]</b> 随机写入{@link Boolean}数组信息，主需要出传入{@link String}数组地址信息，以及对应的{@link Boolean}数组值<br />
     * <b>[Authorization]</b> Randomly write the {@link Boolean} array information, the main need to pass in the {@link String} array address information,
     * and the corresponding {@link Boolean} array value
     * <br /><br />
     * 写入的线圈地址支持Y,I,E,M,T,C,L，举例：Y100；也可以携带CPU进行访问，举例：cpu=2;Y100<br />
     * The write coil address supports Y, I, E, M, T, C, L, for example: Y100; you can also carry the CPU for access, for example: cpu=2;Y100
     * @param address 批量地址信息
     * @param value 批量的数据值信息
     * @return 是否写入成功
     */
    public OperateResult WriteRandomBool(String[] address, boolean[] value ) {
        if (address.length != value.length)
            return new OperateResult(StringResources.Language.TwoParametersLengthIsNotSame());

        OperateResultExOne<byte[]> command = BuildWriteRandomBoolCommand(CpuNumber, address, value);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        OperateResultExOne<byte[]> check = CheckContent(read.Content);
        if (!check.IsSuccess) return check;

        return OperateResult.CreateSuccessResult();
    }

    /**
     * <b>[商业授权]</b> 随机读取{@link Byte}数组信息，主需要出传入{@link String}数组地址信息，就可以返回批量{@link Byte}值<br />
     *  <b>[Authorization]</b> Random read {@link Byte} array information, the master needs to pass in the {@link String} array address information,
     *  and then the batch can be returned to {@link Byte} value
     * @param address 批量地址信息
     * @return 带有成功标志的Bool数组信息
     */
    public OperateResultExOne<byte[]> ReadRandom(String[] address ) {

        OperateResultExOne<ArrayList<byte[]>> command = BuildReadRandomCommand(CpuNumber, address, false);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ByteArrayOutputStream lists = new ByteArrayOutputStream();
        for (int i = 0; i < command.Content.size(); i++) {
            byte[] content = command.Content.get(i);

            OperateResultExOne<byte[]> read = ReadFromCoreServer(content);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            OperateResultExOne<byte[]> check = CheckContent(read.Content);
            if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

            try {
                lists.write(check.Content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return OperateResultExOne.CreateSuccessResult(lists.toByteArray());
    }

    /**
     * <b>[商业授权]</b> 随机读取{@link Short}数组信息，主需要出传入{@link String}数组地址信息，就可以返回批量{@link Short}值<br />
     * <b>[Authorization]</b> Random read {@link Short} array information, the master needs to pass in the {@link String} array address information,
     * and then the batch can be returned to {@link Short} value
     * @param address 批量地址信息
     * @return 带有成功标志的Bool数组信息
     */
    public OperateResultExOne<short[]> ReadRandomInt16( String[] address ) {
        OperateResultExOne<byte[]> read = ReadRandom(address);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(getByteTransform().TransInt16(read.Content, 0, address.length));
    }

    /**
     * <b>[商业授权]</b> 随机写入{@link Byte}数组信息，主需要出传入{@link String}数组地址信息，以及对应的{@link Byte}数组值<br />
     * <b>[Authorization]</b> Randomly write the {@link Byte} array information, the main need to pass in the {@link String} array address information,
     * and the corresponding {@link Byte} array value
     * @param address 批量地址信息
     * @param value 批量的数据值信息
     * @return 是否写入成功
     */
    public OperateResult WriteRandom(String[] address, byte[] value ) {

        if (address.length * 2 != value.length)
            return new OperateResult(StringResources.Language.TwoParametersLengthIsNotSame());

        OperateResultExOne<byte[]> command = BuildWriteRandomWordCommand(CpuNumber, address, value);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        OperateResultExOne<byte[]> check = CheckContent(read.Content);
        if (!check.IsSuccess) return check;

        return OperateResult.CreateSuccessResult();
    }

    /**
     * <b>[商业授权]</b> 随机写入{@link Short}数组信息，主需要出传入{@link String}数组地址信息，以及对应的{@link Short}数组值<br />
     * <b>[Authorization]</b> Randomly write the {@link Short} array information, the main need to pass in the {@link String} array address information,
     * and the corresponding {@link Short} array value
     * @param address 批量地址信息
     * @param value 批量的数据值信息
     * @return 是否写入成功
     */
    public OperateResult WriteRandom( String[] address, short[] value ) {
        return WriteRandom( address, getByteTransform().TransByte( value ) );
    }

    /**
     * <b>[商业授权]</b> 如果未执行程序，则开始执行程序<br />
     * <b>[Authorization]</b> Starts executing a program if it is not being executed
     * <br /><br />
     * This command will be ignored if it is executed while a program is being executed.<br />
     * Refer to the users manual for the individual modules for the response formats that are used at error times.
     * @return 是否启动成功
     */
    public OperateResult Start( ) {

        OperateResultExOne<byte[]> command = BuildStartCommand(CpuNumber);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        OperateResultExOne<byte[]> check = CheckContent(read.Content);
        if (!check.IsSuccess) return check;

        return OperateResult.CreateSuccessResult();
    }

    /**
     * <b>[商业授权]</b> 停止当前正在执行程序<br />
     * <b>[Authorization]</b> Stops the executing program.
     * <br /><br />
     * This command will be ignored if it is executed when no program is being executed.<br />
     * Refer to the users manual for the individual modules for the response formats that are used at error times.
     * @return 是否启动成功
     */
    public OperateResult Stop( ) {

        OperateResultExOne<byte[]> command = BuildStopCommand(CpuNumber);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        OperateResultExOne<byte[]> check = CheckContent(read.Content);
        if (!check.IsSuccess) return check;

        return OperateResult.CreateSuccessResult();
    }

    /**
     * <b>[商业授权]</b> 重置当前的模块，当前打开的连接被强制关闭。 模块中所做的设置也将被清除。然后当前对象需要重连PLC。<br />
     * <b>[Authorization]</b> When this command is executed via an Ethernet interface module or an Ethernet connection of an F3SP66, F3SP67,
     * F3SP71 or F3SP76 sequence CPU module, the connection which is currently open is forced to close.
     * The settings made in the modules are also cleared. Then the current object needs to reconnect to the PLC.
     * @return 是否重置成功
     */
    public OperateResult ModuleReset( ) {
        OperateResultExOne<byte[]> read = ReadFromCoreServer( new byte[] { 0x61, CpuNumber, 0x00, 0x00 }, false, true );         // 不需要返回
        if (!read.IsSuccess) return read;

        return OperateResult.CreateSuccessResult( );
    }

    /**
     * <b>[商业授权]</b> 读取当前PLC的程序状态，返回1：RUN；2：Stop；3：Debug；255：ROM writer<br />
     * <b>[Authorization]</b> Read the program status. return code 1:RUN; 2:Stop; 3:Debug; 255:ROM writer
     * @return 当前PLC的程序状态，返回1：RUN；2：Stop；3：Debug；255：ROM writer
     */
    public OperateResultExOne<Integer> ReadProgramStatus( ) {
        OperateResultExOne<byte[]> read = ReadFromCoreServer(new byte[]{0x62, CpuNumber, 0x00, 0x02, 0x00, 0x01});
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        OperateResultExOne<byte[]> check = CheckContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);
        ;

        return OperateResultExOne.CreateSuccessResult((int) check.Content[1]);
    }

    /**
     * <b>[商业授权]</b> 读取当前PLC的系统状态，系统的ID，CPU类型，程序大小信息<br />
     * <b>[Authorization]</b> Read current PLC system status, system ID, CPU type, program size information
     * @return 系统信息的结果对象
     */
    public OperateResultExOne<YokogawaSystemInfo> ReadSystemInfo( ) {
        OperateResultExOne<byte[]> read = ReadFromCoreServer(new byte[]{0x62, CpuNumber, 0x00, 0x02, 0x00, 0x02});
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        OperateResultExOne<byte[]> check = CheckContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        return YokogawaSystemInfo.Parse(check.Content);
    }

    /**
     * <b>[商业授权]</b> 读取当前PLC的时间信息，包含年月日时分秒<br />
     * b>[Authorization]</b> Read current PLC time information, including year, month, day, hour, minute, and second
     * @return PLC的当前的时间信息
     */
    public OperateResultExOne<Date> ReadDateTime( ) {
        OperateResultExOne<byte[]> read = ReadFromCoreServer(new byte[]{0x63, CpuNumber, 0x00, 0x00});
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        OperateResultExOne<byte[]> check = CheckContent(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        return OperateResultExOne.CreateSuccessResult(new Date(
                2000 + getByteTransform().TransUInt16(check.Content, 0),
                getByteTransform().TransUInt16(check.Content, 2),
                getByteTransform().TransUInt16(check.Content, 4),
                getByteTransform().TransUInt16(check.Content, 6),
                getByteTransform().TransUInt16(check.Content, 8),
                getByteTransform().TransUInt16(check.Content, 10)));
    }

    /**
     * <b>[商业授权]</b> 读取特殊模块的数据信息，需要指定模块单元号，模块站号，数据地址，长度信息。<br />
     * <b>[Authorization]</b> To read the data information of a special module, you need to specify the module unit number,
     * module slot number, data address, and length information.
     * @param moduleUnit 模块的单元号
     * @param moduleSlot 模块的站号
     * @param dataPosition 模块的数据地址
     * @param length 长度信息
     * @return 带有成功标识的byte[]，可以自行解析出所需要的各种类型的数据
     */
    public OperateResultExOne<byte[]> ReadSpecialModule( byte moduleUnit, byte moduleSlot, int dataPosition, short length ) {

        ByteArrayOutputStream content = new ByteArrayOutputStream();
        ArrayList<byte[]> commands = BuildReadSpecialModule(CpuNumber, moduleUnit, moduleSlot, dataPosition, length);

        for (int i = 0; i < commands.size(); i++) {
            OperateResultExOne<byte[]> read = ReadFromCoreServer(commands.get(i));
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            OperateResultExOne<byte[]> check = CheckContent(read.Content);
            if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

            try {
                content.write(check.Content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return OperateResultExOne.CreateSuccessResult(content.toByteArray());
    }

    /**
     * 检查当前的反馈内容，如果没有发生错误，就解析出实际的数据内容。<br />
     * Check the current feedback content, if there is no error, parse out the actual data content.
     * @param content 原始的数据内容
     * @return 解析之后的数据内容
     */
    public static OperateResultExOne<byte[]> CheckContent( byte[] content ) {
        if (content[1] != 0x00) return new OperateResultExOne<>(YokogawaLinkHelper.GetErrorMsg(content[1]));
        if (content.length > 4)
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(content, 4));
        else
            return OperateResultExOne.CreateSuccessResult(new byte[0]);
    }

    /**
     * 构建读取命令的原始报文信息
     * @param cpu Cpu Number
     * @param address 地址数据信息
     * @param length 数据长度信息
     * @param isBit 是否位访问
     * @return 实际的读取的报文信息
     */
    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand( byte cpu, String address, short length, boolean isBit ) {
        OperateResultExTwo<Integer, String> cpuExtra = PlcHelper.ExtractParameter(address, "cpu", cpu);
        if (cpuExtra.IsSuccess) {
            cpu = cpuExtra.Content1.byteValue();
            address = cpuExtra.Content2;
        }

        OperateResultExOne<YokogawaLinkAddress> analysis = YokogawaLinkAddress.ParseFrom(address, length);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        OperateResultExTwo<int[], int[]> splits;
        if (isBit) splits = PlcHelper.SplitReadLength(analysis.Content.getAddressStart(), length, (short) 256);
        else splits = PlcHelper.SplitReadLength(analysis.Content.getAddressStart(), length, (short) 64);

        ArrayList<byte[]> lists = new ArrayList<byte[]>();
        for (int i = 0; i < splits.Content1.length; i++) {
            analysis.Content.setAddressStart(splits.Content1[i]);

            byte[] buffer = new byte[12];
            buffer[0] = isBit ? (byte) 0x01 : (byte) 0x11;
            buffer[1] = cpu;
            buffer[2] = 0x00;
            buffer[3] = 0x08;
            System.arraycopy(analysis.Content.GetAddressBinaryContent(), 0, buffer, 4, 6);
            buffer[10] = Utilities.getBytes(splits.Content2[i])[1];
            buffer[11] = Utilities.getBytes(splits.Content2[i])[0];

            lists.add(buffer);
        }
        return OperateResultExOne.CreateSuccessResult(lists);
    }

    /**
     * 构建随机读取的原始报文的初始命令
     * @param cpu Cpu Number
     * @param address 实际的数据地址信息
     * @param isBit 是否是位读取
     * @return 实际的读取的报文信息
     */
    public static OperateResultExOne<ArrayList<byte[]>> BuildReadRandomCommand(byte cpu, String[] address, boolean isBit ) {
        ArrayList<String[]> splits = SoftBasic.ArraySplitByLength(String.class, address, 32);
        ArrayList<byte[]> lists = new ArrayList<byte[]>();

        for (int j = 0; j < splits.size(); j++) {
            String[] addressSplit = splits.get(j);

            byte[] buffer = new byte[6 + 6 * addressSplit.length];
            buffer[0] = isBit ? (byte) 0x04 : (byte) 0x14;
            buffer[1] = cpu;
            buffer[2] = Utilities.getBytes(buffer.length - 4)[1];
            buffer[3] = Utilities.getBytes(buffer.length - 4)[0];
            buffer[4] = Utilities.getBytes(addressSplit.length)[1];
            buffer[5] = Utilities.getBytes(addressSplit.length)[0];

            for (int i = 0; i < addressSplit.length; i++) {
                OperateResultExTwo<Integer, String> cpuExtra = PlcHelper.ExtractParameter(addressSplit[i], "cpu", cpu);
                if (cpuExtra.IsSuccess) {
                    cpu = cpuExtra.Content1.byteValue();
                    address[i] = cpuExtra.Content2;
                }
                buffer[1] = cpu;

                OperateResultExOne<YokogawaLinkAddress> analysis = YokogawaLinkAddress.ParseFrom(addressSplit[i], (short) 1);
                if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

                System.arraycopy(analysis.Content.GetAddressBinaryContent(), 0, buffer, 6 * i + 6, 6);
            }

            lists.add(buffer);
        }
        return OperateResultExOne.CreateSuccessResult(lists);
    }

    /**
     * 构建批量写入Bool数组的命令，需要指定CPU Number信息和设备地址信息
     * @param cpu Cpu Number
     * @param address 设备地址数据
     * @param value 实际的bool数组
     * @return 构建的写入指令
     */
    public static OperateResultExOne<byte[]> BuildWriteBoolCommand(byte cpu, String address, boolean[] value ) {
        OperateResultExTwo<Integer, String> cpuExtra = PlcHelper.ExtractParameter(address, "cpu", cpu);
        if (cpuExtra.IsSuccess) {
            cpu = cpuExtra.Content1.byteValue();
            address = cpuExtra.Content2;
        }

        OperateResultExOne<YokogawaLinkAddress> analysis = YokogawaLinkAddress.ParseFrom(address, (short) 0);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        byte[] buffer = new byte[12 + value.length];
        buffer[0] = 0x02;
        buffer[1] = cpu;
        buffer[2] = 0x00;
        buffer[3] = (byte) (0x08 + value.length);
        System.arraycopy(analysis.Content.GetAddressBinaryContent(), 0, buffer, 4, 6);
        buffer[10] = Utilities.getBytes(value.length)[1];
        buffer[11] = Utilities.getBytes(value.length)[0];
        for (int i = 0; i < value.length; i++) {
            buffer[12 + i] = value[i] ? (byte) 0x01 : (byte) 0x00;
        }
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    /**
     * 构建批量随机写入Bool数组的命令，需要指定CPU Number信息和设备地址信息
     * @param cpu Cpu Number
     * @param address 设备地址数据
     * @param value 实际的bool数组
     * @return 构建的写入指令
     */
    public static OperateResultExOne<byte[]> BuildWriteRandomBoolCommand( byte cpu, String[] address, boolean[] value ) {
        if (address.length != value.length)
            return new OperateResultExOne<byte[]>(StringResources.Language.TwoParametersLengthIsNotSame());

        byte[] buffer = new byte[6 + address.length * 8 - 1];
        buffer[0] = 0x05;
        buffer[1] = cpu;
        buffer[2] = Utilities.getBytes(buffer.length - 4)[1];
        buffer[3] = Utilities.getBytes(buffer.length - 4)[0];
        buffer[4] = Utilities.getBytes(address.length)[1];
        buffer[5] = Utilities.getBytes(address.length)[0];

        for (int i = 0; i < address.length; i++) {
            OperateResultExTwo<Integer, String> cpuExtra = PlcHelper.ExtractParameter(address[i], "cpu", cpu);
            if (cpuExtra.IsSuccess) {
                cpu = cpuExtra.Content1.byteValue();
                address[i] = cpuExtra.Content2;
            }
            buffer[1] = cpu;

            OperateResultExOne<YokogawaLinkAddress> analysis = YokogawaLinkAddress.ParseFrom(address[i], (short) 0);
            if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

            System.arraycopy(analysis.Content.GetAddressBinaryContent(), 0, buffer, 6 + 8 * i, 6);
            buffer[12 + 8 * i] = value[i] ? (byte) 0x01 : (byte) 0x00;
        }
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    /**
     * 构建字写入的命令报文信息，需要指定设备地址
     * @param cpu Cpu Number
     * @param address 地址
     * @param value 原始的数据值
     * @return 原始的报文命令
     */
    public static OperateResultExOne<byte[]> BuildWriteWordCommand( byte cpu, String address, byte[] value ) {
        OperateResultExTwo<Integer, String> cpuExtra = PlcHelper.ExtractParameter(address, "cpu", cpu);
        if (cpuExtra.IsSuccess) {
            cpu = cpuExtra.Content1.byteValue();
            address = cpuExtra.Content2;
        }

        OperateResultExOne<YokogawaLinkAddress> analysis = YokogawaLinkAddress.ParseFrom(address, (short) 0);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        byte[] buffer = new byte[12 + value.length];
        buffer[0] = 0x12;
        buffer[1] = cpu;
        buffer[2] = 0x00;
        buffer[3] = (byte) (0x08 + value.length);
        System.arraycopy(analysis.Content.GetAddressBinaryContent(), 0, buffer, 4, 6);
        buffer[10] = Utilities.getBytes(value.length / 2)[1];
        buffer[11] = Utilities.getBytes(value.length / 2)[0];
        System.arraycopy(value, 0, buffer, 12, value.length);
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    /**
     * 构建随机写入字的命令的报文
     * @param cpu Cpu Number
     * @param address 地址
     * @param value 原始的数据值
     * @return 原始的报文命令
     */
    public static OperateResultExOne<byte[]> BuildWriteRandomWordCommand( byte cpu, String[] address, byte[] value ) {
        if (address.length * 2 != value.length)
            return new OperateResultExOne<byte[]>(StringResources.Language.TwoParametersLengthIsNotSame());

        byte[] buffer = new byte[6 + address.length * 8];
        buffer[0] = 0x15;
        buffer[1] = cpu;
        buffer[2] = Utilities.getBytes(buffer.length - 4)[1];
        buffer[3] = Utilities.getBytes(buffer.length - 4)[0];
        buffer[4] = Utilities.getBytes(address.length)[1];
        buffer[5] = Utilities.getBytes(address.length)[0];

        for (int i = 0; i < address.length; i++) {
            OperateResultExTwo<Integer, String> cpuExtra = PlcHelper.ExtractParameter(address[i], "cpu", cpu);
            if (cpuExtra.IsSuccess) {
                cpu = cpuExtra.Content1.byteValue();
                address[i] = cpuExtra.Content2;
            }
            buffer[1] = cpu;

            OperateResultExOne<YokogawaLinkAddress> analysis = YokogawaLinkAddress.ParseFrom(address[i], (short) 0);
            if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

            System.arraycopy(analysis.Content.GetAddressBinaryContent(), 0, buffer, 6 + 8 * i, 6);
            buffer[12 + 8 * i] = value[i * 2 + 0];
            buffer[13 + 8 * i] = value[i * 2 + 1];
        }
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    /**
     * 构建启动PLC的命令报文
     * @param cpu Cpu Number
     * @return 原始的报文命令
     */
    public static OperateResultExOne<byte[]> BuildStartCommand( byte cpu ) {
        return OperateResultExOne.CreateSuccessResult(new byte[]{0x45, cpu, 0x00, 0x00});
    }

    /**
     * 构建停止PLC的命令报文
     * @param cpu Cpu Number
     * @return 原始的报文命令
     */
    public static OperateResultExOne<byte[]> BuildStopCommand( byte cpu ) {
        return OperateResultExOne.CreateSuccessResult(new byte[]{0x46, cpu, 0x00, 0x00});
    }

    /**
     * 构建读取特殊模块的命令报文
     * @param cpu Cpu Number
     * @param moduleUnit 模块单元号
     * @param moduleSlot 模块站号
     * @param dataPosition 数据位置
     * @param length 长度信息
     * @return 原始的报文命令
     */
    public static ArrayList<byte[]> BuildReadSpecialModule( byte cpu, byte moduleUnit, byte moduleSlot, int dataPosition, short length ) {
        ArrayList<byte[]> lists = new ArrayList<byte[]>();
        OperateResultExTwo<int[], int[]> splits = PlcHelper.SplitReadLength(dataPosition, length, (short) 64);
        for (int i = 0; i < splits.Content1.length; i++) {
            // length 的基本单位为64
            byte[] buffer = new byte[10];
            buffer[0] = 0x31;
            buffer[1] = cpu;
            buffer[2] = Utilities.getBytes(buffer.length - 4)[1];
            buffer[3] = Utilities.getBytes(buffer.length - 4)[0];
            buffer[4] = moduleUnit;
            buffer[5] = moduleSlot;
            buffer[6] = Utilities.getBytes(splits.Content1[i])[1];
            buffer[7] = Utilities.getBytes(splits.Content1[i])[0];
            buffer[8] = Utilities.getBytes(splits.Content2[i])[1];
            buffer[9] = Utilities.getBytes(splits.Content2[i])[0];

            lists.add(buffer);
        }
        return lists;
    }

    /**
     * 构建读取特殊模块的命令报文，需要传入高级地址，必须以 <b>Special:</b> 开头表示特殊模块地址，示例：Special:cpu=1;unit=0;slot=1;100<br />
     * To construct a command message to read a special module, the advanced address needs to be passed in.
     * It must start with <b>Special:</b> to indicate the address of the special module, for example: Special:cpu=1;unit=0;slot=1;100
     * @param cpu Cpu Number
     * @param address 高级的混合地址，除了Cpu可以不携带，例如：Special:unit=0;slot=1;100
     * @param length 长度信息
     * @return 原始的报文命令
     */
    public static OperateResultExOne<ArrayList<byte[]>> BuildReadSpecialModule( byte cpu, String address, short length ) {
        if (address.startsWith("Special:") || address.startsWith("special:")) {
            address = address.substring(8);
            OperateResultExTwo<Integer, String> cpuExtra = PlcHelper.ExtractParameter(address, "cpu", cpu);
            if (cpuExtra.IsSuccess) {
                cpu = cpuExtra.Content1.byteValue();
                address = cpuExtra.Content2;
            }

            OperateResultExTwo<Integer, String> unitExtra = PlcHelper.ExtractParameter(address, "unit");
            if (!unitExtra.IsSuccess) return OperateResultExOne.CreateFailedResult(unitExtra);

            byte unit = unitExtra.Content1.byteValue();
            address = unitExtra.Content2;

            OperateResultExTwo<Integer, String> slotExtra = PlcHelper.ExtractParameter(address, "slot");
            if (!slotExtra.IsSuccess) return OperateResultExOne.CreateFailedResult(slotExtra);

            byte slot = slotExtra.Content1.byteValue();
            address = slotExtra.Content2;

            try {
                return OperateResultExOne.CreateSuccessResult(BuildReadSpecialModule(cpu, unit, slot, Integer.parseInt(address), length));
            } catch (Exception ex) {
                return new OperateResultExOne<ArrayList<byte[]>>("Address format wrong: " + ex.getMessage());
            }
        } else {
            return new OperateResultExOne<ArrayList<byte[]>>("Special module address must start with Special:");
        }
    }

    /**
     * 构建读取特殊模块的命令报文
     * @param cpu Cpu Number
     * @param moduleUnit 模块单元号
     * @param moduleSlot 模块站号
     * @param dataPosition 数据位置
     * @param data 数据内容
     * @return 原始的报文命令
     */
    public static byte[] BuildWriteSpecialModule( byte cpu, byte moduleUnit, byte moduleSlot, int dataPosition, byte[] data ) {
        // length 的基本单位为64
        byte[] buffer = new byte[10 + data.length];
        buffer[0] = 0x32;
        buffer[1] = cpu;
        buffer[2] = Utilities.getBytes(buffer.length - 4)[1];
        buffer[3] = Utilities.getBytes(buffer.length - 4)[0];
        buffer[4] = moduleUnit;
        buffer[5] = moduleSlot;
        buffer[6] = Utilities.getBytes(dataPosition)[1];
        buffer[7] = Utilities.getBytes(dataPosition)[0];
        buffer[8] = Utilities.getBytes(data.length / 2)[1];
        buffer[9] = Utilities.getBytes(data.length / 2)[0];
        System.arraycopy(data, 0, buffer, 10, data.length);
        return buffer;
    }

    /**
     * 构建写入特殊模块的命令报文，需要传入高级地址，必须以 <b>Special:</b> 开头表示特殊模块地址，示例：Special:cpu=1;unit=0;slot=1;100<br />
     * To construct a command message to write a special module, the advanced address needs to be passed in.
     * It must start with <b>Special:</b> to indicate the address of the special module, for example: Special:cpu=1;unit=0;slot=1;100
     * @param cpu Cpu Number
     * @param address 高级的混合地址，除了Cpu可以不携带，例如：Special:unit=0;slot=1;100
     * @param data 写入的原始数据内容
     * @return 原始的报文命令
     */
    public static OperateResultExOne<byte[]> BuildWriteSpecialModule(byte cpu, String address, byte[] data ) {
        OperateResultExOne<ArrayList<byte[]>> analysis = BuildReadSpecialModule(cpu, address, (short) 0);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        byte[] buffer = new byte[10 + data.length];
        buffer[0] = 0x32;
        buffer[1] = analysis.Content.get(0)[1];
        buffer[2] = Utilities.getBytes(buffer.length - 4)[1];
        buffer[3] = Utilities.getBytes(buffer.length - 4)[0];
        buffer[4] = analysis.Content.get(0)[4];
        buffer[5] = analysis.Content.get(0)[5];
        buffer[6] = analysis.Content.get(0)[6];
        buffer[7] = analysis.Content.get(0)[7];
        buffer[8] = Utilities.getBytes(data.length / 2)[1];
        buffer[9] = Utilities.getBytes(data.length / 2)[0];
        System.arraycopy(data, 0, buffer, 10, data.length);
        return OperateResultExOne.CreateSuccessResult(buffer);
    }


    public String toString( ) {
        return "YokogawaLinkTcp[" + getIpAddress() + ":" + getPort() + "]";
    }
}
