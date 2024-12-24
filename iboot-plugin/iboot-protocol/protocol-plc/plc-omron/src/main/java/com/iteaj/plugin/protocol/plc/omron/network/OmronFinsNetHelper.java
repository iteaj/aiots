package com.iteaj.plugin.protocol.plc.omron.network;


import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.net.IReadWriteDevice;
import com.iteaj.plugin.protocol.plc.core.net.ReadWriteNetHelper;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.util.ArrayList;
import java.util.Date;

/**
 * Profinet.Omron PLC的FINS协议相关的辅助类，主要是一些地址解析，读写的指令生成。<br />
 * The auxiliary classes related to the FINS protocol of Profinet.Omron PLC are mainly some address resolution and the generation of read and write instructions.
 */
public class OmronFinsNetHelper {

    // region Static Method Helper

    /**
     * 同时读取多个地址的命令报文信息
     * @param address 多个地址
     * @param plcType PLC的类型
     * @return 命令报文
     */
    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand(String[] address, OmronPlcType plcType )
    {
        ArrayList<byte[]> cmds = new ArrayList<byte[]>( );
        ArrayList<String[]> splits = SoftBasic.ArraySplitByLength( address, 89 );
        for (int i = 0; i < splits.size(); i++)
        {
            String[] adds = splits.get(i);
            byte[] _PLCCommand = new byte[2 + 4 * adds.length];

            _PLCCommand[0] = 0x01;    // 批量读取存储区数据
            _PLCCommand[1] = 0x04;

            for (int j = 0; j < adds.length; j++)
            {
                OperateResultExOne<OmronFinsAddress> analysis = OmronFinsAddress.ParseFrom( adds[j], (short) 1, plcType );
                if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

                _PLCCommand[2 + 4 * j] = analysis.Content.getWordCode();
                _PLCCommand[3 + 4 * j] = (byte)(analysis.Content.getAddressStart() / 16 / 256);
                _PLCCommand[4 + 4 * j] = (byte)(analysis.Content.getAddressStart() / 16 % 256);
                _PLCCommand[5 + 4 * j] = (byte)(analysis.Content.getAddressStart() % 16);

            }

            cmds.add( _PLCCommand );
        }
        return OperateResultExOne.CreateSuccessResult( cmds );
    }

    public static byte[] BuildReadCommand( OmronFinsAddress address, short length, boolean isBit ) {
        byte[] _PLCCommand = new byte[8];
        _PLCCommand[0] = 0x01;    // 读取存储区数据
        _PLCCommand[1] = 0x01;
        if (isBit)
            _PLCCommand[2] = address.getBitCode();
        else
            _PLCCommand[2] = address.getWordCode();
        _PLCCommand[3] = (byte) (address.getAddressStart() / 16 / 256);
        _PLCCommand[4] = (byte) (address.getAddressStart() / 16 % 256);
        _PLCCommand[5] = (byte) (address.getAddressStart() % 16);
        _PLCCommand[6] = (byte) (length / 256);                           // 长度
        _PLCCommand[7] = (byte) (length % 256);

        return _PLCCommand;
    }

    /**
     * 根据读取的地址，长度，是否位读取创建Fins协议的核心报文<br />
     * According to the read address, length, whether to read the core message that creates the Fins protocol
     * @param plcType PLC的类型
     * @param address 地址，具体格式请参照示例说明
     * @param length 读取的数据长度
     * @param isBit 是否使用位读取
     * @return 带有成功标识的Fins核心报文
     */
    public static OperateResultExOne<byte[][]> BuildReadCommand(OmronPlcType plcType, String address, short length, boolean isBit, int splitLength ) {
        OperateResultExOne<OmronFinsAddress> analysis = OmronFinsAddress.ParseFrom(address, length, plcType);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        ArrayList<byte[]> cmds = new ArrayList<byte[]>();
        int[] lengths = SoftBasic.SplitIntegerToArray(length, isBit ? 1998 : splitLength);
        for (int i = 0; i < lengths.length; i++) {
            cmds.add(BuildReadCommand(analysis.Content, (short) lengths[i], isBit));
            // 起始地址偏移
            analysis.Content.setAddressStart(analysis.Content.getAddressStart() + (isBit ? lengths[i] : lengths[i] * 16));
        }

        return OperateResultExOne.CreateSuccessResult(Utilities.ToArray(cmds));
    }

    /**
     * 根据写入的地址，数据，是否位写入生成Fins协议的核心报文<br />
     * According to the written address, data, whether the bit is written to generate the core message of the Fins protocol
     * @param plcType PLC的类型
     * @param address 地址内容，具体格式请参照示例说明
     * @param value 实际的数据
     * @param isBit 是否位数据
     * @return 带有成功标识的Fins核心报文
     */
    public static OperateResultExOne<byte[]> BuildWriteWordCommand(OmronPlcType plcType,  String address, byte[] value, boolean isBit ) {
        OperateResultExOne<OmronFinsAddress> analysis = OmronFinsAddress.ParseFrom(address, (short) 0, plcType);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        byte[] _PLCCommand = new byte[8 + value.length];
        _PLCCommand[0] = 0x01;
        _PLCCommand[1] = 0x02;

        if (isBit)
            _PLCCommand[2] = analysis.Content.getBitCode();
        else
            _PLCCommand[2] = analysis.Content.getWordCode();

        _PLCCommand[3] = (byte) (analysis.Content.getAddressStart() / 16 / 256);
        _PLCCommand[4] = (byte) (analysis.Content.getAddressStart() / 16 % 256);
        _PLCCommand[5] = (byte) (analysis.Content.getAddressStart() % 16);
        if (isBit) {
            _PLCCommand[6] = (byte) (value.length / 256);
            _PLCCommand[7] = (byte) (value.length % 256);
        } else {
            _PLCCommand[6] = (byte) (value.length / 2 / 256);
            _PLCCommand[7] = (byte) (value.length / 2 % 256);
        }

        System.arraycopy(value, 0, _PLCCommand, 8, value.length);

        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    /**
     * 验证欧姆龙的Fins-TCP返回的数据是否正确的数据，如果正确的话，并返回所有的数据内容<br />
     * Verify that the data returned by Profinet.Omron's Fins-TCP is correct data, if correct, and return all data content
     * @param response 来自欧姆龙返回的数据内容
     * @return 带有是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> ResponseValidAnalysis( byte[] response ) {
        if (response.length >= 16) {
            // 提取错误码 -> Extracting error Codes
            byte[] buffer = new byte[4];
            buffer[0] = response[15];
            buffer[1] = response[14];
            buffer[2] = response[13];
            buffer[3] = response[12];

            int err = Utilities.getInt(buffer, 0);
            if (err > 0) return new OperateResultExOne<byte[]>(err, GetStatusDescription(err));

            byte[] result = new byte[response.length - 16];
            System.arraycopy(response, 16, result, 0, result.length);
            return UdpResponseValidAnalysis(result);
        }

        return new OperateResultExOne<byte[]>(StringResources.Language.OmronReceiveDataError());
    }

    /**
     * 验证欧姆龙的Fins-Udp返回的数据是否正确的数据，如果正确的话，并返回所有的数据内容<br />
     * Verify that the data returned by Profinet.Omron's Fins-Udp is correct data, if correct, and return all data content
     * @param response 来自欧姆龙返回的数据内容
     * @return 带有是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> UdpResponseValidAnalysis( byte[] response ) {
        if (response.length >= 14) {
            int err = (response[12] & 0xff) * 256 + (response[13] & 0xff);
            // if (err > 0) return new OperateResult<byte[]>( err, StringResources.Language.OmronReceiveDataError );
            // 先判断是否发生了 Network relay error ，否则视为网络正常，忽略CPU错误状态
            if (Extension.GetBoolByIndex(response[12], 7 )) {
                int mainCode = response[12] & 0x7F;
                int subCode = response[13] & 0x3F;
                return new OperateResultExOne<byte[]>(err, GetEndCodeDescription(mainCode, subCode));
            }

            if ((response[10] == 0x01 & response[11] == 0x01) ||
                    (response[10] == 0x01 & response[11] == 0x04) ||
                    (response[10] == 0x02 & response[11] == 0x01) ||
                    (response[10] == 0x03 & response[11] == 0x06) ||
                    (response[10] == 0x05 & response[11] == 0x01) ||
                    (response[10] == 0x05 & response[11] == 0x02) ||
                    (response[10] == 0x06 & response[11] == 0x01) ||
                    (response[10] == 0x06 & response[11] == 0x20) ||
                    (response[10] == 0x07 & response[11] == 0x01) ||
                    (response[10] == 0x09 & response[11] == 0x20) ||
                    (response[10] == 0x21 & response[11] == 0x02) ||
                    (response[10] == 0x22 & response[11] == 0x02)) {
                // C0 00 02 00 00 00 00 01 00 00 01 01 21 08
                // Read 操作
                try {
                    byte[] content = new byte[response.length - 14];
                    if (content.length > 0) System.arraycopy(response, 14, content, 0, content.length);

                    OperateResultExOne<byte[]> success = OperateResultExOne.CreateSuccessResult(content);
                    if (content.length == 0) success.IsSuccess = false;
                    success.ErrorCode = err;
                    success.Message = GetStatusDescription(err) + " Received:" + SoftBasic.ByteToHexString(response, ' ');

                    if (response[10] == 0x01 & response[11] == 0x04)          // 多个数据块读取的情况，还需要二次解析数据
                    {
                        byte[] buffer = content.length > 0 ? new byte[content.length * 2 / 3] : new byte[0];
                        for (int i = 0; i < content.length / 3; i++) {
                            buffer[i * 2 + 0] = content[i * 3 + 1];
                            buffer[i * 2 + 1] = content[i * 3 + 2];
                        }
                        success.Content = buffer;
                    }
                    return success;
                } catch (Exception ex) {
                    return new OperateResultExOne<byte[]>("UdpResponseValidAnalysis failed: " + ex.getMessage() + "\r\nContent: " + SoftBasic.ByteToHexString(response, ' '));
                }
            } else {
                // Write Data
                OperateResultExOne<byte[]> success = OperateResultExOne.CreateSuccessResult(new byte[0]);
                success.ErrorCode = err;
                success.Message = GetStatusDescription(err) + " Received:" + SoftBasic.ByteToHexString(response, ' ');
                return success;
            }
        }

        return new OperateResultExOne<byte[]>(StringResources.Language.OmronReceiveDataError());
    }

    /**
     * 根据欧姆龙返回的错误码，获取错误信息的字符串描述文本<br />
     * According to the error code returned by Profinet.Omron, get the string description text of the error message
     * @param err 错误码
     * @return 文本描述
     */
    public static String GetStatusDescription( int err ) {
        switch (err) {
            case 0:
                return StringResources.Language.OmronStatus0();
            case 1:
                return StringResources.Language.OmronStatus1();
            case 2:
                return StringResources.Language.OmronStatus2();
            case 3:
                return StringResources.Language.OmronStatus3();
            case 20:
                return StringResources.Language.OmronStatus20();
            case 21:
                return StringResources.Language.OmronStatus21();
            case 22:
                return StringResources.Language.OmronStatus22();
            case 23:
                return StringResources.Language.OmronStatus23();
            case 24:
                return StringResources.Language.OmronStatus24();
            case 25:
                return StringResources.Language.OmronStatus25();
            default:
                return StringResources.Language.UnknownError();
        }
    }

    private static String GetEndCodeDescription( int mainCode, int subCode ) {
        if (mainCode == 0) {
            if (subCode == 0) return "Normal completion";
            if (subCode == 1) return "Data link status: Service was canceled";
        } else if (mainCode == 0x01) {
            if (subCode == 1) return "Local node is not participating in the network.";
            if (subCode == 2) return "Token does not arrive. [Set the local node to within the maximum node address.]";
            if (subCode == 3) return "Send was not possible during the specified number of retries.";
            if (subCode == 4) return "Cannot send because maximum number of event frames exceeded.";
            if (subCode == 5) return "Node address setting error occurred";
            if (subCode == 6) return "The same node address has been set twice in the same network";
        } else if (mainCode == 0x02) {
            if (subCode == 1) return "The destination node is not in the network";
            if (subCode == 2) return "There is no Unit with the specified unit address.";
            if (subCode == 3) return "The third node does not exist";
            if (subCode == 4) return "The destination node is busy";
            if (subCode == 5) return "The message was destroyed by noise.";
        } else if (mainCode == 0x03) {
            if (subCode == 1) return "An error occurred in the communications controller";
            if (subCode == 2) return "A CPU error occurred in the destination CPU Unit.";
            if (subCode == 3) return "A response was not returned because an error occurred in the Board";
            if (subCode == 4) return "The unit number was set incorrectly";
        } else if (mainCode == 0x04) {
            if (subCode == 1) return "The Unit/Board does not support the specified command code";
            if (subCode == 2) return "The command cannot be executed because the model or version is incorrect";
        } else if (mainCode == 0x05) {
            if (subCode == 1) return "The destination network or node address is not set in the routing tables";
            if (subCode == 2) return "Relaying is not possible because there are no routing tables";
            if (subCode == 3) return "There is an error in the routing tables";
            if (subCode == 4) return "An attempt was made to send to a network that was over 3 networks away";
        } else if (mainCode == 0x10) {
            if (subCode == 1) return "The command is longer than the maximum permissible length";
            if (subCode == 2) return "The command is shorter than the minimum permissible length";
            if (subCode == 3) return "The designated number of elements differs from the number of write data items";
            if (subCode == 4) return "An incorrect format was used";
        } else if (mainCode == 0x11) {
            if (subCode == 1) return "The specified word does not exist in the memory area or there is no EM Area";
            if (subCode == 2) return "The access size specification is incorrect or an odd word address is specified";
            if (subCode == 3) return "The start address in command process is beyond the accessible area";
            if (subCode == 4) return "The end address in command process is beyond the accessible area";
        } else if (mainCode == 0x20) {
            if (subCode == 2) return "The program area is protected";
            if (subCode == 4) return "The search data does not exist.";
            if (subCode == 5) return "A non-existing program number has been specified";
        } else if (mainCode == 0x21) {
            if (subCode == 1) return "The specified area is read-only.";
            if (subCode == 2) return "The program area is protected.";
        }
        return StringResources.Language.UnknownError();
    }

    // endregion

    // region ReadWriteHelper

    /**
     * 从欧姆龙PLC中读取想要的数据，返回读取结果，读取长度的单位为字，地址格式为"D100","C100","W100","H100","A100"<br />
     * Read the desired data from the Profinet.Omron PLC and return the read result. The unit of the read length is word. The address format is "D100", "C100", "W100", "H100", "A100"
     * @param omron PLC设备的连接对象
     * @param address 读取地址，格式为"D100","C100","W100","H100","A100"
     * @param length 读取的数据长度
     * @param splits 分割信息
     * @return 带成功标志的结果数据对象
     */
    public static OperateResultExOne<byte[]> Read(IOmronFins omron, String address, short length, int splits ) {
        // 获取指令
        OperateResultExOne<byte[][]> command = OmronFinsNetHelper.BuildReadCommand( omron.getPlcType(), address, length, false, splits);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Byte> contentArray = new ArrayList<>();
        for (int i = 0; i < command.Content.length; i++) {
            // 核心数据交互
            OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(command.Content[i]);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 读取到了正确的数据
            Utilities.ArrayListAddArray(contentArray, read.Content);
        }

        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(contentArray));
    }

    /**
     * 从欧姆龙PLC中读取多个地址的数据，返回读取结果，每个地址按照字为单位读取，地址格式为"D100","C100","W100","H100","A100"
     * @param omron PLC设备的连接对象
     * @param address 从欧姆龙PLC中读取多个地址的数据，返回读取结果，每个地址按照字为单位读取，地址格式为"D100","C100","W100","H100","A100"
     * @return 带成功标志的结果数据对象
     */
    public static OperateResultExOne<byte[]> Read( IOmronFins omron, String[] address )
    {
        // 获取指令
        OperateResultExOne<ArrayList<byte[]>> command = OmronFinsNetHelper.BuildReadCommand( address, omron.getPlcType() );
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult( command );

        ArrayList<Byte> contentArray = new ArrayList<>();
        for (int i = 0; i < command.Content.size(); i++) {
            // 核心数据交互
            OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(command.Content.get(i));
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 读取到了正确的数据
            Utilities.ArrayListAddArray(contentArray, read.Content);
        }

        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(contentArray));
    }

    /**
     * 向PLC写入数据，数据格式为原始的字节类型，地址格式为"D100","C100","W100","H100","A100"<br />
     * Write data to PLC, the data format is the original byte type, and the address format is "D100", "C100", "W100", "H100", "A100"
     * @param omron PLC设备的连接对象
     * @param address 初始地址
     * @param value 原始的字节数据
     * @return 是否成功的结果对象
     */
    public static OperateResult Write(IOmronFins omron, String address, byte[] value ) {
        // 获取指令
        OperateResultExOne<byte[]> command = OmronFinsNetHelper.BuildWriteWordCommand( omron.getPlcType(), address, value, false);
        if (!command.IsSuccess) return command;

        // 核心数据交互
        OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 成功
        return OperateResult.CreateSuccessResult();
    }

    /**
     * 从欧姆龙PLC中批量读取位软元件，地址格式为"D100.0","C100.0","W100.0","H100.0","A100.0"<br />
     * Read bit devices in batches from Profinet.Omron PLC with address format "D100.0", "C100.0", "W100.0", "H100.0", "A100.0"
     * @param omron PLC设备的连接对象
     * @param address 读取地址，格式为"D100","C100","W100","H100","A100"
     * @param length 读取的长度
     * @param splits 分割信息
     * @return 带成功标志的结果数据对象
     */
    public static OperateResultExOne<boolean[]> ReadBool( IOmronFins omron, String address, short length, int splits ) {
        OperateResultExOne<OmronFinsAddress> analysis = OmronFinsAddress.ParseFrom(address, length, omron.getPlcType());
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        int len = 0;
        ArrayList<Boolean> contentArray = new ArrayList<>();
        while (len < length) {
            byte[] cmd = OmronFinsNetHelper.BuildReadCommand(analysis.Content, (short) (length - len), true);

            // 核心数据交互
            OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(cmd);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            if (read.Content.length == 0) return new OperateResultExOne<boolean[]>(read.ErrorCode, read.Message);

            // 返回正确的数据信息
            boolean[] array = Utilities.getBoolArray(read.Content);
            Utilities.ArrayListAddArray(contentArray, array);

            len += array.length;
            analysis.Content.setAddressStart(analysis.Content.getAddressStart() + array.length);
        }

        return OperateResultExOne.CreateSuccessResult(Utilities.ToBoolArray(contentArray));
    }

    /**
     * 向PLC中位软元件写入bool数组，返回是否写入成功，比如你写入D100,values[0]对应D100.0，地址格式为"D100.0","C100.0","W100.0","H100.0","A100.0"<br />
     * Write the bool array to the PLC's median device and return whether the write was successful. For example, if you write D100, values [0] corresponds to D100.0
     * @param omron PLC设备的连接对象
     * @param address 要写入的数据地址
     * @param values 要写入的实际数据，可以指定任意的长度
     * @return 返回写入结果
     */
    public static OperateResult Write( IOmronFins omron, String address, boolean[] values ) {

        if (omron.getPlcType() == OmronPlcType.CV && (address.startsWith( "CIO" ) || address.startsWith( "C" ))) {
            return ReadWriteNetHelper.WriteBoolWithWord(omron, address, values, 16, true, "");
        }

        byte[] array = new byte[values.length];
        for (int i = 0; i < array.length; i++) {
            array[i] = values[i] ? (byte) 0x01 : (byte) 0x00;
        }
        // 获取指令
        OperateResultExOne<byte[]> command = OmronFinsNetHelper.BuildWriteWordCommand( omron.getPlcType(), address, array, true);
        if (!command.IsSuccess) return command;

        // 核心数据交互
        OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 写入成功
        return OperateResult.CreateSuccessResult();
    }
    private static OperateResultExOne<Date> CreatePlcTime(byte[] buffer ) {
        try {
            String hex = SoftBasic.ByteToHexString(buffer);
            Date current = new Date();

            int year = Convert.ToInt32(String.valueOf((current.getYear() + 1900)).substring(0, 2) + hex.substring(0, 2));
            return OperateResultExOne.CreateSuccessResult(Utilities.getDateFrom(
                    year, Convert.ToInt32(hex.substring(2, 4)), Convert.ToInt32(hex.substring(4, 6)),
                    Convert.ToInt32(hex.substring(6, 8)), Convert.ToInt32(hex.substring(8, 10)), Convert.ToInt32(hex.substring(10, 12))));
        } catch (Exception ex) {
            return new OperateResultExOne<Date>("Prase Time failed: " + ex.getMessage() + Environment.NewLine + "Source: " + SoftBasic.ByteToHexString(buffer, ' '));
        }
    }

    /**
     * {@link IOmronFins#Run()}
     */
    public static OperateResult Run( IReadWriteDevice omron ) {
        return omron.ReadFromCoreServer( new byte[] { 0x04, 0x01, (byte) 0xFF, (byte) 0xFF, 0x04 } );
    }

    /**
     * {@link IOmronFins#Stop()}
     */
    public static OperateResult Stop( IReadWriteDevice omron ){
        return omron.ReadFromCoreServer( new byte[] { 0x04, 0x02,  (byte)0xFF,  (byte)0xFF } );
    }

    /**
     * {@link IOmronFins#ReadCpuUnitData()}
     */
    public static OperateResultExOne<OmronCpuUnitData> ReadCpuUnitData( IReadWriteDevice omron ) {

        OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(new byte[]{0x05, 0x01, 0x00});
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(new OmronCpuUnitData(read.Content));
    }

    /**
     * {@link IOmronFins#ReadCpuUnitStatus()}
     */
    public static OperateResultExOne<OmronCpuUnitStatus> ReadCpuUnitStatus( IReadWriteDevice omron ) {

        OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(new byte[]{0x06, 0x01});
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(new OmronCpuUnitStatus(read.Content));
    }

    /**
     * {@link IOmronFins#ReadCpuTime()}
     */
    public static OperateResultExOne<Date> ReadCpuTime( IReadWriteDevice omron ) {
        OperateResultExOne<byte[]> read = omron.ReadFromCoreServer(new byte[]{0x07, 0x01});
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return CreatePlcTime(read.Content);
    }



    // endregion

}
