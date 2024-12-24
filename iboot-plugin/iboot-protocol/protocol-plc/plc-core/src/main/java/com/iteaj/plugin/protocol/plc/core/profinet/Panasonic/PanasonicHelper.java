package com.iteaj.plugin.protocol.plc.core.profinet.Panasonic;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.types.Encoding;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExTwo;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.util.ArrayList;

/**
 * 松下PLC的辅助类，提供了基本的辅助方法，用于解析地址，计算校验和，创建报文
 * The auxiliary class of Panasonic PLC provides basic auxiliary methods for parsing addresses, calculating checksums, and creating messages
 */
public class PanasonicHelper {

    public static boolean CheckBoolOnWordAddress(String address) {
        return address.matches("^(s=[0-9]+;)?(D|LD)[0-9]+\\.[0-9]+$");
    }

    private static String CalculateCrc(StringBuilder sb) {
        byte tmp = 0;
        tmp = (byte) sb.charAt(0);
        for (int i = 1; i < sb.length(); i++) {
            tmp ^= (byte) sb.charAt(i);
        }
        return SoftBasic.ByteToHexString(new byte[]{tmp});
    }

    /**
     * 位地址转换方法，101等同于10.1等同于10*16+1=161
     * Bit address conversion method, 101 is equivalent to 10.1 is equivalent to 10 * 16 + 1 = 161
     *
     * @param address 地址信息
     * @return 实际的位地址信息
     */
    public static int CalculateComplexAddress(String address) {
        int add = 0;
        if (!address.contains(".")) {
            if (address.length() == 1)
                add = Integer.parseInt(address, 16);
            else
                add = Integer.parseInt(address.substring(0, address.length() - 1)) * 16 + Integer.parseInt(address.substring(address.length() - 1), 16);
        } else {
            add = Integer.parseInt(address.substring(0, address.indexOf("."))) * 16;
            String bit = address.substring(address.indexOf(".") + 1);
            if (bit.contains("A") || bit.contains("B") || bit.contains("C") || bit.contains("D") || bit.contains("E") || bit.contains("F")) {
                add += Integer.parseInt(bit, 16);
            } else {
                add += Integer.parseInt(bit);
            }
        }
        return add;
    }

    /**
     * 解析数据地址，解析出地址类型，起始地址
     * Parse the data address, resolve the address type, start address
     *
     * @param address 数据地址
     * @return 解析出地址类型，起始地址
     */
    public static OperateResultExTwo<String, Integer> AnalysisAddress(String address) {
        OperateResultExTwo<String, Integer> result = new OperateResultExTwo<String, Integer>();
        try {
            result.Content2 = 0;
            if (address.startsWith("IX") || address.startsWith("ix")) {
                result.Content1 = "IX";
                result.Content2 = Integer.parseInt(address.substring(2));
            } else if (address.startsWith("IY") || address.startsWith("iy")) {
                result.Content1 = "IY";
                result.Content2 = Integer.parseInt(address.substring(2));
            } else if (address.startsWith("ID") || address.startsWith("id")) {
                result.Content1 = "ID";
                result.Content2 = Integer.parseInt(address.substring(2));
            } else if (address.startsWith("SR") || address.startsWith("sr")) {
                result.Content1 = "SR";
                result.Content2 = CalculateComplexAddress(address.substring(2));
            } else if (address.startsWith("LD") || address.startsWith("ld")) {
                result.Content1 = "LD";
                result.Content2 = Integer.parseInt(address.substring(2));
            } else if (address.charAt(0) == 'X' || address.charAt(0) == 'x') {
                result.Content1 = "X";
                result.Content2 = CalculateComplexAddress(address.substring(1));
            } else if (address.charAt(0) == 'Y' || address.charAt(0) == 'y') {
                result.Content1 = "Y";
                result.Content2 = CalculateComplexAddress(address.substring(1));
            } else if (address.charAt(0) == 'R' || address.charAt(0) == 'r') {
                result.Content1 = "R";
                result.Content2 = CalculateComplexAddress(address.substring(1));
            } else if (address.charAt(0) == 'T' || address.charAt(0) == 't') {
                result.Content1 = "T";
                result.Content2 = Integer.parseInt(address.substring(1));
            } else if (address.charAt(0) == 'C' || address.charAt(0) == 'c') {
                result.Content1 = "C";
                result.Content2 = Integer.parseInt(address.substring(1));
            } else if (address.charAt(0) == 'L' || address.charAt(0) == 'l') {
                result.Content1 = "L";
                result.Content2 = CalculateComplexAddress(address.substring(1));
            } else if (address.charAt(0) == 'D' || address.charAt(0) == 'd') {
                result.Content1 = "D";
                result.Content2 = Integer.parseInt(address.substring(1));
            } else if (address.charAt(0) == 'F' || address.charAt(0) == 'f') {
                result.Content1 = "F";
                result.Content2 = Integer.parseInt(address.substring(1));
            } else if (address.charAt(0) == 'S' || address.charAt(0) == 's') {
                result.Content1 = "S";
                result.Content2 = Integer.parseInt(address.substring(1));
            } else if (address.charAt(0) == 'K' || address.charAt(0) == 'k') {
                result.Content1 = "K";
                result.Content2 = Integer.parseInt(address.substring(1));
            } else {
                throw new Exception(StringResources.Language.NotSupportedDataType());
            }
        } catch (Exception ex) {
            result.Message = ex.getMessage();
            return result;
        }

        result.IsSuccess = true;
        return result;
    }

    public static String GetStationString(int station) {
        if (station >= 0) return String.format("%02X", station);
        else return String.format("%02X", (byte) station);
    }

    /**
     * 创建读取离散触点的报文指令
     * Create message instructions for reading discrete contacts
     *
     * @param station 站号信息
     * @param address 地址信息
     * @return 包含是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> BuildReadOneCoil(int station, String address) {
        // 参数检查
        if (address == null) return new OperateResultExOne<byte[]>("address is not allowed null");
        if (address.length() < 1 || address.length() > 8) return new OperateResultExOne<byte[]>("length must be 1-8");

        StringBuilder sb = new StringBuilder("%");
        sb.append(GetStationString(station));
        sb.append("#RCS");

        // 解析地址
        OperateResultExTwo<String, Integer> analysis = AnalysisAddress(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        sb.append(analysis.Content1);
        if (analysis.Content1.equals("X") || analysis.Content1.equals("Y") || analysis.Content1.equals("R") || analysis.Content1.equals("L")) {
            sb.append(String.format("%03d", analysis.Content2 / 16));
            sb.append(String.format("%01x", analysis.Content2 % 16));
        } else if (analysis.Content1.equals("T") || analysis.Content1.equals("C")) {
            sb.append("0");
            sb.append(String.format("%03d", analysis.Content2));
        } else {
            return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
        }

        sb.append(CalculateCrc(sb));
        sb.append((char) 0x0D);

        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(sb.toString(), "ascii"));
    }

    /**
     * 创建写入离散触点的报文指令
     * Create message instructions to write discrete contacts
     *
     * @param station 站号信息
     * @param address 地址信息
     * @param value   bool值数组
     * @return 包含是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> BuildWriteOneCoil(int station, String address, boolean value) {
        // 参数检查
        StringBuilder sb = new StringBuilder("%");
        sb.append(GetStationString(station));
        sb.append("#WCS");

        // 解析地址
        OperateResultExTwo<String, Integer> analysis = AnalysisAddress(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        sb.append(analysis.Content1);
        if (analysis.Content1.equals("X") || analysis.Content1.equals("Y") || analysis.Content1.equals("R") || analysis.Content1.equals("L")) {
            sb.append(String.format("%03d", analysis.Content2 / 16));
            sb.append(String.format("%01x", analysis.Content2 % 16));
        } else if (analysis.Content1.equals("T") || analysis.Content1.equals("C")) {
            sb.append("0");
            sb.append(String.format("%03d", analysis.Content2));
        } else {
            return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
        }

        sb.append(value ? '1' : '0');

        sb.append(CalculateCrc(sb));
        sb.append((char) 0x0D);

        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(sb.toString(), "ascii"));
    }

    /**
     * 将松下的命令打包成带有%开头，CRC校验，CR结尾的完整的命令报文。如果参数 <c>useExpandedHeader</c> 设置为 <c>Ture</c>，则命令头使用 &lt; 开头
     *
     * @param station           站号信息
     * @param cmd               松下的命令。例如 RCSR100F
     * @param useExpandedHeader 设置是否使用扩展的命令头消息
     * @return 原始的字节数组的命令
     */
    public static OperateResultExOne<byte[]> PackPanasonicCommand(int station, String cmd, boolean useExpandedHeader) {
        StringBuilder sb = new StringBuilder(useExpandedHeader ? "<" : "%");
        sb.append(String.format("%02X", station));
        sb.append(cmd);                           // 追加命令，例如 RCSR100F, RCP2R100F
        sb.append(CalculateCrc(sb));
        sb.append((char) 0x0D);
        return OperateResultExOne.CreateSuccessResult(Encoding.ASCII.GetBytes(sb.toString()));
    }

    /**
     * 创建批量读取触点的报文指令
     * Create message instructions for batch reading contacts
     *
     * @param station 站号信息
     * @param address 地址信息
     * @param length  数据长度
     * @param isBit   是否位读取功能
     * @return 包含是否成功的结果对象
     */
    public static OperateResultExOne<ArrayList<byte[]>> BuildReadCommand(int station, String address, int length, boolean isBit) {
        // 参数检查
        if (address == null)
            return new OperateResultExOne<ArrayList<byte[]>>(StringResources.Language.PanasonicAddressParameterCannotBeNull());

        // 解析地址
        OperateResultExTwo<String, Integer> analysis = AnalysisAddress(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        ArrayList<byte[]> list = new ArrayList<>();
        if (isBit) {
            length += analysis.Content2 % 16;
            analysis.Content2 -= analysis.Content2 % 16;
            int[] splits = SoftBasic.SplitIntegerToArray(length, 25 * 16);

            for (int i = 0; i < splits.length; i++) {
                int len = splits[i];

                StringBuilder sb = new StringBuilder("#");
                if (analysis.Content1.equals("X") || analysis.Content1.equals("Y") || analysis.Content1.equals("R") || analysis.Content1.equals("L")) {
                    sb.append("RCC");
                    sb.append(analysis.Content1);

                    int wordStart = analysis.Content2 / 16;
                    int wordFinish = (analysis.Content2 + len - 1) / 16;
                    sb.append(String.format("%04d", wordStart));
                    sb.append(String.format("%04d", wordFinish));

                    analysis.Content2 += len;
                } else
                    return new OperateResultExOne<>("Bit read only support X,Y,R,L");

                list.add(PackPanasonicCommand(station, sb.toString(), false).Content);
            }
            return OperateResultExOne.CreateSuccessResult(list);
        } else {
            int[] splits = SoftBasic.SplitIntegerToArray(length, 500);
            for (int i = 0; i < splits.length; i++) {
                int len = splits[i];

                StringBuilder sb = new StringBuilder("#");
                if (analysis.Content1.equals("X") || analysis.Content1.equals("Y") || analysis.Content1.equals("R") || analysis.Content1.equals("L")) {
                    sb.append("RCC");
                    sb.append(analysis.Content1);

                    int wordStart = analysis.Content2 / 16;
                    int wordFinish = (analysis.Content2 + (len - 1) * 16) / 16;
                    sb.append(String.format("%04d", wordStart));
                    sb.append(String.format("%04d", wordFinish));

                    analysis.Content2 += len * 16;
                } else if (analysis.Content1.equals("D") || analysis.Content1.equals("LD") || analysis.Content1.equals("F")) {
                    sb.append("RD");
                    sb.append(analysis.Content1.substring(0, 1));

                    sb.append(String.format("%05d", analysis.Content2));
                    sb.append(String.format("%05d", (analysis.Content2 + len - 1)));

                    analysis.Content2 += len;
                } else if (analysis.Content1.equals("IX") || analysis.Content1.equals("IY") || analysis.Content1.equals("ID")) {
                    sb.append("RD");
                    sb.append(analysis.Content1);
                    sb.append("000000000");

                    analysis.Content2 += len;
                } else if (analysis.Content1.equals("C") || analysis.Content1.equals("T")) {
                    sb.append("RS");
                    sb.append(String.format("%04d", analysis.Content2));
                    sb.append(String.format("%04d", (analysis.Content2 + len - 1)));

                    analysis.Content2 += len;
                } else if (analysis.Content1.equals("K") || analysis.Content1.equals("S")) {
                    // 经过值及目标值
                    sb.append("R");
                    sb.append(analysis.Content1);
                    sb.append(String.format("%04d", analysis.Content2));
                    sb.append(String.format("%04d", (analysis.Content2 + len - 1)));

                    analysis.Content2 += len;
                } else {
                    return new OperateResultExOne<>(StringResources.Language.NotSupportedDataType());
                }

                list.add(PackPanasonicCommand(station, sb.toString(), len > 27).Content);
            }
        }
        return OperateResultExOne.CreateSuccessResult( list );
    }



    public static OperateResultExOne<byte[]> BuildWriteCommand( int station, String address, byte[] values ){
        return BuildWriteCommand(station, address, values, (short) -1);
    }

    /**
     * 创建批量读取触点的报文指令
     * Create message instructions for batch reading contacts
     * @param station 设备站号
     * @param address 地址信息
     * @param values 数据值
     * @param length 数据长度
     * @return 包含是否成功的结果对象
     */
    public static OperateResultExOne<byte[]> BuildWriteCommand( int station, String address, byte[] values, short length )
    {
        // 参数检查
        if (address == null) return new OperateResultExOne<byte[]>( StringResources.Language.PanasonicAddressParameterCannotBeNull() );

        // 解析地址
        OperateResultExTwo<String, Integer> analysis = AnalysisAddress( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        // 确保偶数长度
        values = SoftBasic.ArrayExpandToLengthEven( values );
        if (length == -1) length = (short)(values.length / 2);

        StringBuilder sb = new StringBuilder( "%" );
        sb.append( GetStationString( station ) );
        sb.append( "#" );

        if (analysis.Content1.equals("X") || analysis.Content1.equals("Y") || analysis.Content1.equals("R") || analysis.Content1.equals("L"))
        {
            sb.append( "WCC" );
            sb.append( analysis.Content1 );
            sb.append( String.format( "%04d", analysis.Content2 ) );
            sb.append( String.format( "%04d", (analysis.Content2 + length - 1) ) );
        }
        else if (analysis.Content1.equals("D") || analysis.Content1.equals("LD") || analysis.Content1.equals("F"))
        {
            sb.append( "WD" );
            sb.append( analysis.Content1.substring(0, 1) );
            sb.append( String.format( "%05d", analysis.Content2 ) );
            sb.append( String.format( "%05d", (analysis.Content2 + length - 1) ) );
        }
        else if (analysis.Content1.equals("IX") || analysis.Content1.equals("IY") || analysis.Content1.equals("ID"))
        {
            sb.append( "WD" );
            sb.append( analysis.Content1 );
            sb.append( String.format( "%09d", analysis.Content2 ) );
            sb.append( String.format( "%09d", (analysis.Content2 + length - 1) ) );
        }
        else if (analysis.Content1.equals("C") || analysis.Content1.equals("T"))
        {
            sb.append( "WS" );
            sb.append( String.format( "%04d", analysis.Content2 ) );
            sb.append( String.format( "%04d", (analysis.Content2 + length - 1) ) );
        }

        sb.append( SoftBasic.ByteToHexString( values ) );

        sb.append( CalculateCrc( sb ) );
        sb.append( (char) 0x0D );

        return OperateResultExOne.CreateSuccessResult( Utilities.getBytes( sb.toString( ), "ascii" ) );
    }

    /**
     * 检查从PLC反馈的数据，并返回正确的数据内容
     * Check the data feedback from the PLC and return the correct data content
     * @param response 反馈信号
     * @return 是否成功的结果信息
     */
    public static OperateResultExOne<byte[]> ExtraActualData( byte[] response )
    {
        if (response.length < 9) return new OperateResultExOne<byte[]>( StringResources.Language.PanasonicReceiveLengthMustLargerThan9() );

        if (response[3] == '$')
        {
            byte[] data = new byte[response.length - 9];
            if (data.length > 0)
            {
                System.arraycopy( response, 6, data, 0, data.length );
                data = SoftBasic.HexStringToBytes( Utilities.getString( data, "US-ASCII" ) );
            }
            return OperateResultExOne.CreateSuccessResult( data );
        }
        else if (response[3] == '!')
        {
            int err = Integer.parseInt( Utilities.getString( response, 4, 2, "US-ASCII" ) );
            return new OperateResultExOne<byte[]>( err, GetErrorDescription( err ) );
        }
        else
        {
            return new OperateResultExOne<byte[]>( StringResources.Language.UnknownError() );
        }
    }

    /**
     * 检查从PLC反馈的数据，并返回正确的数据内容
     * Check the data feedback from the PLC and return the correct data content
     * @param response 反馈信号
     * @return 是否成功的结果信息
     */
    public static OperateResultExOne<Boolean> ExtraActualBool( byte[] response )
    {
        if (response.length < 9) return new OperateResultExOne<Boolean>( StringResources.Language.PanasonicReceiveLengthMustLargerThan9() );

        if (response[3] == '$')
        {
            return OperateResultExOne.CreateSuccessResult( response[6] == 0x31 );
        }
        else if (response[3] == '!')
        {
            int err = Integer.parseInt( Utilities.getString( response, 4, 2, "US-ASCII" ) );
            return new OperateResultExOne<Boolean>( err, GetErrorDescription( err ) );
        }
        else
        {
            return new OperateResultExOne<Boolean>( StringResources.Language.UnknownError() );
        }
    }

    /**
     * 根据错误码获取到错误描述
     * Get the error description text according to the error code
     * @param err 错误代码
     * @return 字符信息
     */
    public static String GetErrorDescription( int err )
    {
        switch (err)
        {
            case 20: return StringResources.Language.PanasonicMewStatus20();
            case 21: return StringResources.Language.PanasonicMewStatus21();
            case 22: return StringResources.Language.PanasonicMewStatus22();
            case 23: return StringResources.Language.PanasonicMewStatus23();
            case 24: return StringResources.Language.PanasonicMewStatus24();
            case 25: return StringResources.Language.PanasonicMewStatus25();
            case 26: return StringResources.Language.PanasonicMewStatus26();
            case 27: return StringResources.Language.PanasonicMewStatus27();
            case 28: return StringResources.Language.PanasonicMewStatus28();
            case 29: return StringResources.Language.PanasonicMewStatus29();
            case 30: return StringResources.Language.PanasonicMewStatus30();
            case 40: return StringResources.Language.PanasonicMewStatus40();
            case 41: return StringResources.Language.PanasonicMewStatus41();
            case 42: return StringResources.Language.PanasonicMewStatus42();
            case 43: return StringResources.Language.PanasonicMewStatus43();
            case 50: return StringResources.Language.PanasonicMewStatus50();
            case 51: return StringResources.Language.PanasonicMewStatus51();
            case 52: return StringResources.Language.PanasonicMewStatus52();
            case 53: return StringResources.Language.PanasonicMewStatus53();
            case 60: return StringResources.Language.PanasonicMewStatus60();
            case 61: return StringResources.Language.PanasonicMewStatus61();
            case 62: return StringResources.Language.PanasonicMewStatus62();
            case 63: return StringResources.Language.PanasonicMewStatus63();
            case 65: return StringResources.Language.PanasonicMewStatus65();
            case 66: return StringResources.Language.PanasonicMewStatus66();
            case 67: return StringResources.Language.PanasonicMewStatus67();
            default: return StringResources.Language.UnknownError();
        }
    }

    /**
     * 根据MC的错误码去查找对象描述信息
     * @param code 错误码
     * @return 描述信息
     */
    public static String GetMcErrorDescription( int code )
    {
        switch (code)
        {
            case 0x4031: return StringResources.Language.PanasonicMc4031();
            case 0xC051: return StringResources.Language.PanasonicMcC051();
            case 0xC056: return StringResources.Language.PanasonicMcC056();
            case 0xC059: return StringResources.Language.PanasonicMcC059();
            case 0xC05B: return StringResources.Language.PanasonicMcC05B();
            case 0xC05C: return StringResources.Language.PanasonicMcC05C();
            case 0xC05F: return StringResources.Language.PanasonicMcC05F();
            case 0xC060: return StringResources.Language.PanasonicMcC060();
            case 0xC061: return StringResources.Language.PanasonicMcC061();
            default: return StringResources.Language.MelsecPleaseReferToManualDocument();
        }
    }

}

