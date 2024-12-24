package com.iteaj.plugin.protocol.plc.omron.network;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.SpecifiedCharacterMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.DataFormat;
import com.iteaj.plugin.protocol.plc.core.transfer.ReverseWordTransform;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.StringResources;

import java.nio.charset.StandardCharsets;

/**
 * 欧姆龙的HostLink的C-Mode实现形式，当前的类是通过以太网透传实现。地址支持携带站号信息，例如：s=2;D100<br />
 * The C-Mode implementation form of Omron’s HostLink, the current class is realized through Ethernet transparent transmission.
 * Address supports carrying station number information, for example: s=2;D100
 * <br /><br />
 * 暂时只支持的字数据的读写操作，不支持位的读写操作。另外本模式下，程序要在监视模式运行才能写数据，欧姆龙官方回复的。
 */
public class OmronHostLinkCModeOverTcp extends NetworkDeviceBase {

    public OmronHostLinkCModeOverTcp( )
    {
        this.setByteTransform(new ReverseWordTransform( ));
        this.WordLength               = 1;
        this.getByteTransform().setDataFormat(DataFormat.CDAB);
        this.getByteTransform().setIsStringReverse(true);
    }

    public OmronHostLinkCModeOverTcp( String ipAddress, int port )
    {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new SpecifiedCharacterMessage(AsciiControl.CR);
    }

    /**
     * PLC设备的站号信息<br />
     * PLC device station number information
     */
    public byte UnitNumber = 0;

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        byte station = this.UnitNumber;
        OperateResultExTwo<Integer, String> analysis = PlcHelper.ExtractParameter(address, "s", this.UnitNumber);
        if (analysis.IsSuccess) {
            station = analysis.Content1.byteValue();
            address = analysis.Content2;
        }

        // 解析地址
        OperateResultExOne<byte[]> command = BuildReadCommand( address, length, false );
        if (!command.IsSuccess) return command;

        // 核心交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( PackCommand( command.Content, station ) );
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

        // 数据有效性分析
        OperateResultExOne<byte[]> valid = ResponseValidAnalysis( read.Content, true );
        if (!valid.IsSuccess) return OperateResultExOne.CreateFailedResult( valid );

        // 读取到了正确的数据
        return OperateResultExOne.CreateSuccessResult( valid.Content );
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        byte station = this.UnitNumber;
        OperateResultExTwo<Integer, String> analysis = PlcHelper.ExtractParameter(address, "s", this.UnitNumber);
        if (analysis.IsSuccess) {
            station = analysis.Content1.byteValue();
            address = analysis.Content2;
        }

        // 获取指令
        OperateResultExOne<byte[]> command = BuildWriteWordCommand( address, value ); ;
        if (!command.IsSuccess) return command;

        // 核心数据交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer( PackCommand( command.Content, station ) );
        if (!read.IsSuccess) return read;

        // 数据有效性分析
        OperateResultExOne<byte[]> valid = ResponseValidAnalysis( read.Content, false );
        if (!valid.IsSuccess) return valid;

        // 成功
        return OperateResult.CreateSuccessResult( );
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return super.ReadBool(address, length);
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        return super.Write(address, value);
    }

    /**
     * 读取PLC的当前的型号信息
     * @return 型号
     */
    public OperateResultExOne<String> ReadPlcModel( ) {
        // 核心数据交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(PackCommand("MM".getBytes(StandardCharsets.US_ASCII), UnitNumber));
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 数据有效性分析
        int err = Integer.parseInt(new String(read.Content, 5, 2, StandardCharsets.US_ASCII), 16);
        if (err > 0) return new OperateResultExOne<String>(err, "Unknown Error");

        // 成功
        String model = new String(read.Content, 7, 2, StandardCharsets.US_ASCII);
        return GetModelText(model);
    }

    /**
     * 解析欧姆龙的数据地址，参考来源是Omron手册第188页，比如D100， E1.100<br />
     * Analyze Omron's data address, the reference source is page 188 of the Omron manual, such as D100, E1.100
     * @param address 数据地址
     * @param isBit 是否是位地址
     * @param isRead 是否读取
     * @return 解析后的结果地址对象
     */
    public static OperateResultExTwo<String, String> AnalysisAddress( String address, boolean isBit, boolean isRead ) {
        OperateResultExTwo<String, String> result = new OperateResultExTwo<String, String>();
        try {
            switch (address.charAt(0)) {
                case 'D':
                case 'd': {
                    // DM区数据
                    result.Content1 = isRead ? "RD" : "WD";
                    break;
                }
                case 'C':
                case 'c': {
                    // CIO区数据
                    result.Content1 = isRead ? "RR" : "WR";
                    break;
                }
                case 'H':
                case 'h': {
                    // HR区
                    result.Content1 = isRead ? "RH" : "WH";
                    break;
                }
                case 'A':
                case 'a': {
                    // AR区
                    result.Content1 = isRead ? "RJ" : "WJ";
                    break;
                }
                case 'E':
                case 'e': {
                    // E区，比较复杂，需要专门的计算
                    String[] splits = address.split("\\.");
                    int block = Integer.parseInt(splits[0].substring(1), 16);
                    result.Content1 = (isRead ? "RE" : "WE") + new String(SoftBasic.BuildAsciiBytesFrom((byte) block), StandardCharsets.US_ASCII);
                    break;
                }
                default:
                    throw new Exception(StringResources.Language.NotSupportedDataType());
            }

            if (address.charAt(0) == 'E' || address.charAt(0) == 'e') {
                String[] splits = address.split("\\.");
                if (isBit) {
                    // 位操作
                    //ushort addr = ushort.Parse( splits[1] );
                    //result.Content2 = new byte[3];
                    //result.Content2[0] = BitConverter.GetBytes( addr )[1];
                    //result.Content2[1] = BitConverter.GetBytes( addr )[0];

                    //if (splits.Length > 2)
                    //{
                    //	result.Content2[2] = byte.Parse( splits[2] );
                    //	if (result.Content2[2] > 15)
                    //	{
                    //		throw new Exception( StringResources.Language.OmronAddressMustBeZeroToFiveteen );
                    //	}
                    //}
                } else {
                    // 字操作
                    int addr = Integer.parseInt(splits[1]);
                    result.Content2 = String.format("%04d", addr);
                }
            } else {
                if (isBit) {
                    // 位操作
                    //string[] splits = address.Substring( 1 ).Split( new char[] { '.' }, StringSplitOptions.RemoveEmptyEntries );
                    //ushort addr = ushort.Parse( splits[0] );
                    //result.Content2 = new byte[3];
                    //result.Content2[0] = BitConverter.GetBytes( addr )[1];
                    //result.Content2[1] = BitConverter.GetBytes( addr )[0];

                    //if (splits.Length > 1)
                    //{
                    //	result.Content2[2] = byte.Parse( splits[1] );
                    //	if (result.Content2[2] > 15)
                    //	{
                    //		throw new Exception( StringResources.Language.OmronAddressMustBeZeroToFiveteen );
                    //	}
                    //}
                } else {
                    // 字操作
                    int addr = Integer.parseInt(address.substring(1));
                    result.Content2 = String.format("%04d", addr);
                }
            }
        } catch (Exception ex) {
            result.Message = ex.getMessage();
            return result;
        }

        result.IsSuccess = true;
        return result;
    }

    /**
     * 根据读取的地址，长度，是否位读取创建Fins协议的核心报文<br />
     * According to the read address, length, whether to read the core message that creates the Fins protocol
     * @param address 地址，具体格式请参照示例说明
     * @param length 读取的数据长度
     * @param isBit 是否使用位读取
     * @return 带有成功标识的Fins核心报文
     */
    public static OperateResultExOne<byte[]> BuildReadCommand( String address, short length, boolean isBit ) {
        OperateResultExTwo<String, String> analysis = AnalysisAddress(address, isBit, true);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        StringBuilder sb = new StringBuilder();
        sb.append(analysis.Content1);
        sb.append(analysis.Content2);
        sb.append(String.format("%04d", length));

        return OperateResultExOne.CreateSuccessResult(sb.toString().getBytes(StandardCharsets.US_ASCII));
    }

    /**
     * 根据读取的地址，长度，是否位读取创建Fins协议的核心报文<br />
     * According to the read address, length, whether to read the core message that creates the Fins protocol
     * @param address 地址，具体格式请参照示例说明
     * @param value 等待写入的数据
     * @return 带有成功标识的Fins核心报文
     */
    public static OperateResultExOne<byte[]> BuildWriteWordCommand( String address, byte[] value ) {
        OperateResultExTwo<String, String> analysis = AnalysisAddress(address, false, false);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        StringBuilder sb = new StringBuilder();
        sb.append(analysis.Content1);
        sb.append(analysis.Content2);
        sb.append( SoftBasic.ByteToHexString( value) );
//        for (int i = 0; i < value.length / 2; i++) {
//            sb.append(String.format("%04X", Utilities.getUShort(value, i * 2)));
//        }

        return OperateResultExOne.CreateSuccessResult(sb.toString().getBytes(StandardCharsets.US_ASCII));
    }


    /**
     * 验证欧姆龙的Fins-TCP返回的数据是否正确的数据，如果正确的话，并返回所有的数据内容
     * @param response 来自欧姆龙返回的数据内容
     * @param isRead 是否读取
     * @return 带有是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> ResponseValidAnalysis( byte[] response, boolean isRead ) {
        // 数据有效性分析
        if (response.length >= 11) {
            try {
                // 提取错误码
                int err = Integer.parseInt(new String(response, 5, 2, StandardCharsets.US_ASCII), 16);
                byte[] Content = null;

                if (response.length > 11)
                    Content = SoftBasic.HexStringToBytes(new String(response, 7, response.length - 11));

                if (err > 0) {
                    OperateResultExOne<byte[]> result = new OperateResultExOne<byte[]>();
                    result.ErrorCode = err;
                    result.Content = Content;
                    return result;
                } else {
                    return OperateResultExOne.CreateSuccessResult(Content);
                }
            } catch (Exception ex) {
                return new OperateResultExOne<byte[]>("ResponseValidAnalysis failed: " + ex.getMessage() + " Source: " + SoftBasic.ByteToHexString(response, ' '));
            }
        }

        return new OperateResultExOne<byte[]>(StringResources.Language.OmronReceiveDataError());
    }

    /**
     * 将普通的指令打包成完整的指令
     * @param cmd fins指令
     * @param unitNumber 站号信息
     * @return 完整的质量
     */
    public static byte[] PackCommand( byte[] cmd, byte unitNumber ) {
        byte[] buffer = new byte[7 + cmd.length];

        buffer[0] = (byte) '@';
        buffer[1] = SoftBasic.BuildAsciiBytesFrom(unitNumber)[0];
        buffer[2] = SoftBasic.BuildAsciiBytesFrom(unitNumber)[1];
        buffer[buffer.length - 2] = (byte) '*';
        buffer[buffer.length - 1] = 0x0D;
        System.arraycopy(cmd, 0, buffer, 3, cmd.length);
        // 计算FCS
        int tmp = buffer[0];
        for (int i = 1; i < buffer.length - 4; i++) {
            tmp = (tmp ^ buffer[i]);
        }
        buffer[buffer.length - 4] = SoftBasic.BuildAsciiBytesFrom((byte) tmp)[0];
        buffer[buffer.length - 3] = SoftBasic.BuildAsciiBytesFrom((byte) tmp)[1];
        String output = new String(buffer, StandardCharsets.US_ASCII);
        return buffer;
    }

    /**
     * 获取model的字符串描述信息
     * @param model 型号代码
     * @return 是否解析成功
     */
    public static OperateResultExOne<String> GetModelText( String model )
    {
        switch (model)
        {
            case "30": return OperateResultExOne.CreateSuccessResult( "CS/CJ" );
            case "01": return OperateResultExOne.CreateSuccessResult( "C250" );
            case "02": return OperateResultExOne.CreateSuccessResult( "C500" );
            case "03": return OperateResultExOne.CreateSuccessResult( "C120/C50" );
            case "09": return OperateResultExOne.CreateSuccessResult( "C250F" );
            case "0A": return OperateResultExOne.CreateSuccessResult( "C500F" );
            case "0B": return OperateResultExOne.CreateSuccessResult( "C120F" );
            case "0E": return OperateResultExOne.CreateSuccessResult( "C2000" );
            case "10": return OperateResultExOne.CreateSuccessResult( "C1000H" );
            case "11": return OperateResultExOne.CreateSuccessResult( "C2000H/CQM1/CPM1" );
            case "12": return OperateResultExOne.CreateSuccessResult( "C20H/C28H/C40H, C200H, C200HS, C200HX/HG/HE (-ZE)" );
            case "20": return OperateResultExOne.CreateSuccessResult( "CV500" );
            case "21": return OperateResultExOne.CreateSuccessResult( "CV1000" );
            case "22": return OperateResultExOne.CreateSuccessResult( "CV2000" );
            case "40": return OperateResultExOne.CreateSuccessResult( "CVM1-CPU01-E" );
            case "41": return OperateResultExOne.CreateSuccessResult( "CVM1-CPU11-E" );
            case "42": return OperateResultExOne.CreateSuccessResult( "CVM1-CPU21-E" );
            default: return new OperateResultExOne<String>( "Unknown model, model code:" + model );
        }
    }
}
