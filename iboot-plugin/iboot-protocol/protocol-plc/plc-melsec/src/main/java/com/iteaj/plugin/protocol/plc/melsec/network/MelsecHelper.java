package com.iteaj.plugin.protocol.plc.melsec.network;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExTwo;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.nio.charset.StandardCharsets;

/**
 * 所有三菱通讯类的通用辅助工具类，包含了一些通用的静态方法，可以使用本类来获取一些原始的报文信息。详细的操作参见例子<br />
 * All general auxiliary tool classes of Mitsubishi communication class include some general static methods.
 * You can use this class to get some primitive message information. See the example for detailed operation
 */
public class MelsecHelper {

    /**
     * 解析A1E协议数据地址<br />
     * Parse A1E protocol data address
     *
     * @param address 数据地址
     * @return 解析值
     */
    public static OperateResultExTwo<MelsecA1EDataType, Integer> McA1EAnalysisAddress(String address) {
        OperateResultExTwo<MelsecA1EDataType, Integer> result = new OperateResultExTwo<MelsecA1EDataType, Integer>();
        try {
            switch (address.charAt(0)) {
                case 'T':
                case 't': {
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        result.Content1 = MelsecA1EDataType.TS;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.TS.getFromBase());
                    } else if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        result.Content1 = MelsecA1EDataType.TC;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.TC.getFromBase());
                    } else if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        result.Content1 = MelsecA1EDataType.TN;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.TN.getFromBase());
                    } else {
                        throw new Exception(StringResources.Language.NotSupportedDataType());
                    }
                    break;
                }
                case 'C':
                case 'c': {
                    if (address.charAt(1) == 'S' || address.charAt(1) == 's') {
                        result.Content1 = MelsecA1EDataType.CS;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.CS.getFromBase());
                    } else if (address.charAt(1) == 'C' || address.charAt(1) == 'c') {
                        result.Content1 = MelsecA1EDataType.CC;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.CC.getFromBase());
                    } else if (address.charAt(1) == 'N' || address.charAt(1) == 'n') {
                        result.Content1 = MelsecA1EDataType.CN;
                        result.Content2 = Integer.parseInt(address.substring(2), MelsecA1EDataType.CN.getFromBase());
                    } else {
                        throw new Exception(StringResources.Language.NotSupportedDataType());
                    }
                    break;
                }
                case 'X':
                case 'x': {
                    result.Content1 = MelsecA1EDataType.X;
                    address = address.substring(1);
                    if (address.startsWith("0"))
                        result.Content2 = Integer.parseInt(address, 8);
                    else
                        result.Content2 = Integer.parseInt(address, MelsecA1EDataType.X.getFromBase());
                    break;
                }
                case 'Y':
                case 'y': {
                    result.Content1 = MelsecA1EDataType.Y;
                    address = address.substring(1);
                    if (address.startsWith("0"))
                        result.Content2 = Integer.parseInt(address, 8);
                    else
                        result.Content2 = Integer.parseInt(address, MelsecA1EDataType.Y.getFromBase());
                    break;
                }
                case 'M':
                case 'm': {
                    result.Content1 = MelsecA1EDataType.M;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.M.getFromBase());
                    break;
                }
                case 'S':
                case 's': {
                    result.Content1 = MelsecA1EDataType.S;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.S.getFromBase());
                    break;
                }
                case 'F':
                case 'f': {
                    result.Content1 = MelsecA1EDataType.F;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.F.getFromBase());
                    break;
                }
                case 'B':
                case 'b': {
                    result.Content1 = MelsecA1EDataType.B;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.B.getFromBase());
                    break;
                }
                case 'D':
                case 'd': {
                    result.Content1 = MelsecA1EDataType.D;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.D.getFromBase());
                    break;
                }
                case 'R':
                case 'r': {
                    result.Content1 = MelsecA1EDataType.R;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.R.getFromBase());
                    break;
                }
                case 'W':
                case 'w': {
                    result.Content1 = MelsecA1EDataType.W;
                    result.Content2 = Integer.parseInt(address.substring(1), MelsecA1EDataType.W.getFromBase());
                    break;
                }
                default:
                    throw new Exception(StringResources.Language.NotSupportedDataType());
            }
        } catch (Exception ex) {
            result.Message = ex.getMessage();
            return result;
        }

        result.IsSuccess = true;
        return result;
    }

    /**
     * 从字节构建一个ASCII格式的地址字节
     *
     * @param value 字节信息
     * @return ASCII格式的地址
     */
    public static byte[] BuildBytesFromData(byte value) {
        return Utilities.getBytes(String.format("%02x", value), "ASCII");
    }


    /**
     * 从short数据构建一个ASCII格式地址字节
     *
     * @param value short值
     * @return ASCII格式的地址
     */
    public static byte[] BuildBytesFromData(short value) {
        return Utilities.getBytes(String.format("%04x", value), "ASCII");
    }

    /**
     * 从int数据构建一个ASCII格式地址字节
     *
     * @param value int值
     * @return ASCII格式的地址
     */
    public static byte[] BuildBytesFromData(int value) {
        return Utilities.getBytes(String.format("%04x", value), "ASCII");
    }


    /**
     * 从三菱的地址中构建MC协议的6字节的ASCII格式的地址
     *
     * @param address 三菱地址
     * @param type    三菱的数据类型
     * @return 6字节的ASCII格式的地址
     */
    public static byte[] BuildBytesFromAddress(int address, MelsecMcDataType type) {
        return Utilities.getBytes(String.format(type.getFromBase() == 10 ? "%06d" : "%06x", address), "ASCII");
    }


    /**
     * 从字节数组构建一个ASCII格式的地址字节
     *
     * @param value 字节信息
     * @return ASCII格式的地址
     */
    public static byte[] BuildBytesFromData(byte[] value) {
        byte[] buffer = new byte[value.length * 2];
        for (int i = 0; i < value.length; i++) {
            byte[] data = BuildBytesFromData(value[i]);
            buffer[2 * i + 0] = data[0];
            buffer[2 * i + 1] = data[1];
        }
        return buffer;
    }


    /**
     * 将0，1，0，1的字节数组压缩成三菱格式的字节数组来表示开关量的
     *
     * @param value 原始的数据字节
     * @return 压缩过后的数据字节
     */
    public static byte[] TransBoolArrayToByteData(byte[] value) {
        int length = value.length % 2 == 0 ? value.length / 2 : (value.length / 2) + 1;
        byte[] buffer = new byte[length];

        for (int i = 0; i < length; i++) {
            if (value[i * 2 + 0] != 0x00) buffer[i] += 0x10;
            if ((i * 2 + 1) < value.length) {
                if (value[i * 2 + 1] != 0x00) buffer[i] += 0x01;
            }
        }

        return buffer;
    }

    /**
     * 将bool的组压缩成三菱格式的字节数组来表示开关量的
     *
     * @param value 原始的数据字节
     * @return 压缩过后的数据字节
     */
    public static byte[] TransBoolArrayToByteData(boolean[] value) {
        int length = (value.length + 1) / 2;
        byte[] buffer = new byte[length];

        for (int i = 0; i < length; i++) {
            if (value[i * 2 + 0]) buffer[i] += 0x10;
            if ((i * 2 + 1) < value.length) {
                if (value[i * 2 + 1]) buffer[i] += 0x01;
            }
        }

        return buffer;
    }


    /**
     * 计算Fx协议指令的和校验信息
     *
     * @param data 字节数据
     * @return 校验之后的数据
     */
    public static byte[] FxCalculateCRC(byte[] data) {
        return FxCalculateCRC(data, 1, 2);
    }

    public static byte[] FxCalculateCRC(byte[] data, int start, int tail ) {
        int sum = 0;
        for (int i = start; i < data.length - tail; i++) {
            sum += data[i] & 0xff;
        }
        return SoftBasic.BuildAsciiBytesFrom((byte) sum);
    }

    /**
     * 检查指定的和校验是否是正确的
     *
     * @param data 字节数据
     * @return 是否成功
     */
    public static boolean CheckCRC(byte[] data) {
        byte[] crc = FxCalculateCRC(data);
        if (crc[0] != data[data.length - 2]) return false;
        if (crc[1] != data[data.length - 1]) return false;
        return true;
    }



    public static byte[] TransByteArrayToAsciiByteArray(byte[] value) {
        if (value == null) return new byte[0];

        byte[] buffer = new byte[value.length * 2];
        for (int i = 0; i < value.length / 2; i++) {
            byte[] data = SoftBasic.BuildAsciiBytesFrom(Utilities.getShort(value, i * 2));
            System.arraycopy(data, 0, buffer, 4 * i, data.length);
        }
        return buffer;
    }

    public static byte[] TransAsciiByteArrayToByteArray( byte[] value )
    {
        byte[] Content = new byte[value.length / 2];
        for (int i = 0; i < Content.length / 2; i++)
        {
            int tmp = Integer.parseInt( new String( value, i * 4 , 4, StandardCharsets.US_ASCII ), 16 );
            System.arraycopy(Utilities.getBytes(tmp), 0, Content, i * 2, 2);
        }
        return Content;
    }
    /**
     * 根据三菱的错误码去查找对象描述信息
     *
     * @param code 错误码
     * @return 描述信息
     */
    public static String GetErrorDescription(int code) {
        switch (code) {
            case 0x0002:
                return StringResources.Language.MelsecError02();
            case 0x0051:
                return StringResources.Language.MelsecError51();
            case 0x0052:
                return StringResources.Language.MelsecError52();
            case 0x0054:
                return StringResources.Language.MelsecError54();
            case 0x0055:
                return StringResources.Language.MelsecError55();
            case 0x0056:
                return StringResources.Language.MelsecError56();
            case 0x0058:
                return StringResources.Language.MelsecError58();
            case 0x0059:
                return StringResources.Language.MelsecError59();
            case 0xC04D:
                return StringResources.Language.MelsecErrorC04D();
            case 0xC050:
                return StringResources.Language.MelsecErrorC050();
            case 0xC051:
            case 0xC052:
            case 0xC053:
            case 0xC054:
                return StringResources.Language.MelsecErrorC051_54();
            case 0xC055:
                return StringResources.Language.MelsecErrorC055();
            case 0xC056:
                return StringResources.Language.MelsecErrorC056();
            case 0xC057:
                return StringResources.Language.MelsecErrorC057();
            case 0xC058:
                return StringResources.Language.MelsecErrorC058();
            case 0xC059:
                return StringResources.Language.MelsecErrorC059();
            case 0xC05A:
            case 0xC05B:
                return StringResources.Language.MelsecErrorC05A_B();
            case 0xC05C:
                return StringResources.Language.MelsecErrorC05C();
            case 0xC05D:
                return StringResources.Language.MelsecErrorC05D();
            case 0xC05E:
                return StringResources.Language.MelsecErrorC05E();
            case 0xC05F:
                return StringResources.Language.MelsecErrorC05F();
            case 0xC060:
                return StringResources.Language.MelsecErrorC060();
            case 0xC061:
                return StringResources.Language.MelsecErrorC061();
            case 0xC062:
                return StringResources.Language.MelsecErrorC062();
            case 0xC070:
                return StringResources.Language.MelsecErrorC070();
            case 0xC072:
                return StringResources.Language.MelsecErrorC072();
            case 0xC074:
                return StringResources.Language.MelsecErrorC074();
            default:
                return StringResources.Language.MelsecPleaseReferToManualDocument();
        }
    }
}
