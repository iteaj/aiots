package com.iteaj.plugin.protocol.plc.core.profinet.Omron;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.SpecifiedCharacterMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.DataFormat;
import com.iteaj.plugin.protocol.plc.core.transfer.ReverseWordTransform;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class OmronHostLinkOverTcp extends NetworkDeviceBase {

    public OmronHostLinkOverTcp( )
    {
        this.setByteTransform(new ReverseWordTransform( ));
        this.WordLength               = 1;
        this.getByteTransform().setDataFormat(DataFormat.CDAB);
        //this.getByteTransform().setIsStringReverse(true);
    }

    public OmronHostLinkOverTcp( String ipAddress, int port )
    {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new SpecifiedCharacterMessage(AsciiControl.CR);
    }

    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response )
    {
        return ResponseValidAnalysis( send, response );
    }

    /// <summary>
    /// Specifies whether or not there are network relays. Set “80” (ASCII: 38,30)
    /// when sending an FINS command to a CPU Unit on a network.Set “00” (ASCII: 30,30)
    /// when sending to a CPU Unit connected directly to the host computer.
    /// </summary>
    public byte ICF = 0x00;

    /// <inheritdoc cref="OmronFinsNet.DA2"/>
    public byte DA2 = 0x00;

    /// <inheritdoc cref="OmronFinsNet.SA2"/>
    public byte SA2 = 0x00;

    /// <inheritdoc cref="OmronFinsNet.SID"/>
    public byte SID  = 0x00;

    /**
     * The response wait time sets the time from when the CPU Unit receives a command block until it starts
     * to return a response.It can be set from 0 to F in hexadecimal, in units of 10 ms.
     * If F(15) is set, the response will begin to be returned 150 ms (15 × 10 ms) after the command block was received.
     */
    public byte ResponseWaitTime = 0x30;

    /**
     * PLC设备的站号信息<br />
     * PLC device station number information
     */
    public byte UnitNumber = 0;

    public OmronPlcType getPlcType(){
        return this.plcType;
    }

    public void setPlcType( OmronPlcType plcType ){
        this.plcType = plcType;
    }

    /**
     * 进行字读取的时候对于超长的情况按照本属性进行切割，默认260。<br />
     * When reading words, it is cut according to this attribute for the case of overlength. The default is 260.
     */
    public int ReadSplits = 260;

    public OperateResultExOne<byte[]> Read( String address, short length ) {
        byte station = this.UnitNumber;
        OperateResultExTwo<Integer, String> analysis = PlcHelper.ExtractParameter(address, "s", this.UnitNumber);
        if (analysis.IsSuccess) {
            station = analysis.Content1.byteValue();
            address = analysis.Content2;
        }

        // 解析地址
        OperateResultExOne<byte[][]> command = OmronFinsNetHelper.BuildReadCommand(this.plcType, address, length, false, ReadSplits); //ReadSplits
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Byte> contentArray = new ArrayList<>();
        for (int i = 0; i < command.Content.length; i++) {
            // 核心交互
            OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand(station, command.Content[i]));
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 读取到了正确的数据
            Utilities.ArrayListAddArray(contentArray, read.Content);
        }

        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(contentArray));
    }

    /**
     * 从欧姆龙PLC中读取多个地址的数据，返回读取结果，每个地址按照字为单位读取，地址格式为"D100","C100","W100","H100","A100"
     * @param address 从欧姆龙PLC中读取多个地址的数据，返回读取结果，每个地址按照字为单位读取，地址格式为"D100","C100","W100","H100","A100"
     * @return 带成功标志的结果数据对象
     */
    public OperateResultExOne<byte[]> Read( String[] address ) {
        byte station = this.UnitNumber;
        if (address != null && address.length > 0) {
            OperateResultExTwo<Integer, String> analysis = PlcHelper.ExtractParameter(address[0], "s", this.UnitNumber);
            if (analysis.IsSuccess) {
                station = analysis.Content1.byteValue();
                address[0] = analysis.Content2;
            }
        }

        // 解析地址
        OperateResultExOne<ArrayList<byte[]>> command = OmronFinsNetHelper.BuildReadCommand(address, this.plcType);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Byte> contentArray = new ArrayList<>();
        for (int i = 0; i < command.Content.size(); i++) {
            // 核心交互
            OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand(station, command.Content.get(i)));
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 读取到了正确的数据
            Utilities.ArrayListAddArray(contentArray, read.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(contentArray));
    }

    public OperateResult Write(String address, byte[] value )
    {
        byte station = this.UnitNumber;
        OperateResultExTwo<Integer,String> analysis = PlcHelper.ExtractParameter(address, "s", this.UnitNumber);
        if(analysis.IsSuccess){
            station = analysis.Content1.byteValue();
            address = analysis.Content2;
        }

        // 获取指令
        OperateResultExOne<byte[]> command = OmronFinsNetHelper.BuildWriteWordCommand(this.plcType, address, value, false ); ;
        if (!command.IsSuccess) return command;

        // 核心数据交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( PackCommand( station, command.Content ) );
        if (!read.IsSuccess) return read;

        // 成功
        return OperateResult.CreateSuccessResult( );
    }

    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        byte station = this.UnitNumber;
        OperateResultExTwo<Integer, String> analysis = PlcHelper.ExtractParameter(address, "s", this.UnitNumber);
        if (analysis.IsSuccess) {
            station = analysis.Content1.byteValue();
            address = analysis.Content2;
        }

        // 获取指令
        OperateResultExOne<byte[][]> command = OmronFinsNetHelper.BuildReadCommand(this.plcType, address, length, true, ReadSplits);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Boolean> contentArray = new ArrayList<Boolean>();
        for (int i = 0; i < command.Content.length; i++) {
            // 核心交互
            OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand(station, command.Content[i]));
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 返回正确的数据信息
            if (read.Content.length == 0) return new OperateResultExOne<boolean[]>("Data is empty.");
            for (int j = 0; j < read.Content.length; j++) {
                contentArray.add(read.Content[j] != 0x00);
            }
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.getBools(contentArray));
    }

    public OperateResult Write( String address, boolean[] values ) {
        byte station = this.UnitNumber;
        OperateResultExTwo<Integer, String> analysis = PlcHelper.ExtractParameter(address, "s", this.UnitNumber);
        if (analysis.IsSuccess) {
            station = analysis.Content1.byteValue();
            address = analysis.Content2;
        }

        // 获取指令
        OperateResultExOne<byte[]> command = OmronFinsNetHelper.BuildWriteWordCommand( this.plcType, address, TransBoolsArray(values), true);
        ;
        if (!command.IsSuccess) return command;

        // 核心数据交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand(station, command.Content));
        if (!read.IsSuccess) return read;

        // 成功
        return OperateResult.CreateSuccessResult();
    }


    private OmronPlcType plcType = OmronPlcType.CSCJ;
    public String toString( ) {
		    return "OmronHostLinkOverTcp[" + getIpAddress() + ":" + getPort() + "]";
    }

    /**
     * 将普通的指令打包成完整的指令
     * @param station PLC的站号信息
     * @param cmd fins指令
     * @return 完整的质量
     */
    private byte[] PackCommand( byte station, byte[] cmd ) {
        cmd = SoftBasic.BytesToAsciiBytes(cmd);

        byte[] buffer = new byte[18 + cmd.length];
        buffer[ 0] = (byte) '@';
        buffer[ 1] = SoftBasic.BuildAsciiBytesFrom(station)[0];
        buffer[ 2] = SoftBasic.BuildAsciiBytesFrom(station)[1];
        buffer[ 3] = (byte) 'F';
        buffer[ 4] = (byte) 'A';
        buffer[ 5] = ResponseWaitTime;
        buffer[ 6] = SoftBasic.BuildAsciiBytesFrom(this.ICF)[0];
        buffer[ 7] = SoftBasic.BuildAsciiBytesFrom(this.ICF)[1];
        buffer[ 8] = SoftBasic.BuildAsciiBytesFrom(this.DA2)[0];
        buffer[ 9] = SoftBasic.BuildAsciiBytesFrom(this.DA2)[1];
        buffer[10] = SoftBasic.BuildAsciiBytesFrom(this.SA2)[0];
        buffer[11] = SoftBasic.BuildAsciiBytesFrom(this.SA2)[1];
        buffer[12] = SoftBasic.BuildAsciiBytesFrom(this.SID)[0];
        buffer[13] = SoftBasic.BuildAsciiBytesFrom(this.SID)[1];
        buffer[buffer.length - 2] = (byte) '*';
        buffer[buffer.length - 1] = 0x0D;
        System.arraycopy(cmd, 0, buffer, 14, cmd.length);
        // 计算FCS
        int tmp = buffer[0];
        for (int i = 1; i < buffer.length - 4; i++) {
            tmp ^= buffer[i];
        }
        buffer[buffer.length - 4] = SoftBasic.BuildAsciiBytesFrom((byte) tmp)[0];
        buffer[buffer.length - 3] = SoftBasic.BuildAsciiBytesFrom((byte) tmp)[1];
        return buffer;
    }



    /**
     * 验证欧姆龙的Fins-TCP返回的数据是否正确的数据，如果正确的话，并返回所有的数据内容
     * @param send 发送的报文信息
     * @param response 来自欧姆龙返回的数据内容
     * @return 带有是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> ResponseValidAnalysis(byte[] send, byte[] response ) {
        // 数据有效性分析
        // @00FA00400000000102000040*\cr
        if (response.length >= 27) {
            String commandSend = new String(send, 14, 4, StandardCharsets.US_ASCII);
            String commandReceive = new String(response, 15, 4, StandardCharsets.US_ASCII);
            if (!commandReceive.equals(commandSend))
                return new OperateResultExOne<byte[]>("Send Command [" + commandSend + "] not the same as receive command [" + commandReceive + "]");
            // 提取错误码
            int err = 0;
            try {
                err = Integer.parseInt(new String(response, 19, 4, StandardCharsets.US_ASCII), 16);
            }
            catch (Exception ex){
                return new OperateResultExOne<>( "Get error code failed: " + ex.getMessage() + "\r\nSource Data: " + SoftBasic.GetAsciiStringRender( response ) );
            }
            byte[] content = new byte[0];
            if (response.length > 27)
                content = SoftBasic.HexStringToBytes(new String(response, 23, response.length - 27, StandardCharsets.US_ASCII));

            if (err > 0) {
                OperateResultExOne<byte[]> result = new OperateResultExOne<byte[]>();
                result.ErrorCode = err;
                result.Content = content;
                result.Message = GetErrorText(err);
            } else {
                if (new String(response, 15, 4, StandardCharsets.US_ASCII).equals("0104")) {
                    byte[] buffer = content.length > 0 ? new byte[content.length * 2 / 3] : new byte[0];
                    for (int i = 0; i < content.length / 3; i++) {
                        buffer[i * 2 + 0] = content[i * 3 + 1];
                        buffer[i * 2 + 1] = content[i * 3 + 2];
                    }
                    content = buffer;
                }
                return OperateResultExOne.CreateSuccessResult(content);
            }
        }

        return new OperateResultExOne<byte[]>(StringResources.Language.OmronReceiveDataError() + " Source Data: " + SoftBasic.ByteToHexString(response, ' '));
    }

    /**
     * 根据错误信息获取当前的文本描述信息
     * @param error 错误代号
     * @return 文本消息
     */
    public static String GetErrorText( int error ) {
        switch (error) {
            case 0x0001:
                return "Service was canceled.";
            case 0x0101:
                return "Local node is not participating in the network.";
            case 0x0102:
                return "Token does not arrive.";
            case 0x0103:
                return "Send was not possible during the specified number of retries.";
            case 0x0104:
                return "Cannot send because maximum number of event frames exceeded.";
            case 0x0105:
                return "Node address setting error occurred.";
            case 0x0106:
                return "The same node address has been set twice in the same network.";
            case 0x0201:
                return "The destination node is not in the network.";
            case 0x0202:
                return "There is no Unit with the specified unit address.";
            case 0x0203:
                return "The third node does not exist.";
            case 0x0204:
                return "The destination node is busy.";
            case 0x0205:
                return "The message was destroyed by noise";
            case 0x0301:
                return "An error occurred in the communications controller.";
            case 0x0302:
                return "A CPU error occurred in the destination CPU Unit.";
            case 0x0303:
                return "A response was not returned because an error occurred in the Board.";
            case 0x0304:
                return "The unit number was set incorrectly";
            case 0x0401:
                return "The Unit/Board does not support the specified command code.";
            case 0x0402:
                return "The command cannot be executed because the model or version is incorrect";
            case 0x0501:
                return "The destination network or node address is not set in the routing tables.";
            case 0x0502:
                return "Relaying is not possible because there are no routing tables";
            case 0x0503:
                return "There is an error in the routing tables.";
            case 0x0504:
                return "An attempt was made to send to a network that was over 3 networks away";
            // Command format error
            case 0x1001:
                return "The command is longer than the maximum permissible length.";
            case 0x1002:
                return "The command is shorter than the minimum permissible length.";
            case 0x1003:
                return "The designated number of elements differs from the number of write data items.";
            case 0x1004:
                return "An incorrect format was used.";
            case 0x1005:
                return "Either the relay table in the local node or the local network table in the relay node is incorrect.";
            // Parameter error
            case 0x1101:
                return "The specified word does not exist in the memory area or there is no EM Area.";
            case 0x1102:
                return "The access size specification is incorrect or an odd word address is specified.";
            case 0x1103:
                return "The start address in command process is beyond the accessible area";
            case 0x1104:
                return "The end address in command process is beyond the accessible area.";
            case 0x1106:
                return "FFFF hex was not specified.";
            case 0x1109:
                return "A large–small relationship in the elements in the command data is incorrect.";
            case 0x110B:
                return "The response format is longer than the maximum permissible length.";
            case 0x110C:
                return "There is an error in one of the parameter settings.";
            // Read Not Possible
            case 0x2002:
                return "The program area is protected.";
            case 0x2003:
                return "A table has not been registered.";
            case 0x2004:
                return "The search data does not exist.";
            case 0x2005:
                return "A non-existing program number has been specified.";
            case 0x2006:
                return "The file does not exist at the specified file device.";
            case 0x2007:
                return "A data being compared is not the same.";
            // Write not possible
            case 0x2101:
                return "The specified area is read-only.";
            case 0x2102:
                return "The program area is protected.";
            case 0x2103:
                return "The file cannot be created because the limit has been exceeded.";
            case 0x2105:
                return "A non-existing program number has been specified.";
            case 0x2106:
                return "The file does not exist at the specified file device.";
            case 0x2107:
                return "A file with the same name already exists in the specified file device.";
            case 0x2108:
                return "The change cannot be made because doing so would create a problem.";
            // Not executable in current mode
            case 0x2201:
            case 0x2202:
            case 0x2208:
                return "The mode is incorrect.";
            case 0x2203:
                return "The PLC is in PROGRAM mode.";
            case 0x2204:
                return "The PLC is in DEBUG mode.";
            case 0x2205:
                return "The PLC is in MONITOR mode.";
            case 0x2206:
                return "The PLC is in RUN mode.";
            case 0x2207:
                return "The specified node is not the polling node.";
            //  No such device
            case 0x2301:
                return "The specified memory does not exist as a file device.";
            case 0x2302:
                return "There is no file memory.";
            case 0x2303:
                return "There is no clock.";
            case 0x2401:
                return "The data link tables have not been registered or they contain an error.";
            default:
                return StringResources.Language.UnknownError();
        }
    }

    public static byte[] TransBoolsArray(boolean[] values){
        byte[] buffer = new byte[values.length];
        for (int i=0;i<buffer.length;i++) {
            buffer[i] = values[i] ? ((byte) 0x01) : ((byte) 0x00);
        }
        return  buffer;
    }
}
