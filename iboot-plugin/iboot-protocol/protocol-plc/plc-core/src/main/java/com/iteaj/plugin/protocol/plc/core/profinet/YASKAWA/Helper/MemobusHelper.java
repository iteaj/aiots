package com.iteaj.plugin.protocol.plc.core.profinet.YASKAWA.Helper;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

/**
 * Memobus的辅助类对象
 */
public class MemobusHelper {

    // region Method Helper

    public static byte[] PackCommandWithHeader( byte[] command, long id ) {
        byte[] buffer = new byte[12 + command.length];        // 添加12字节的218报头
        buffer[0] = 0x11;                                     // 0x11:Memobus指令  0x12:通用信息  0x19:Memobus响应
        buffer[1] = (byte) id;
        buffer[2] = 0x00;                                     // 设定发送目标的通道编号(共享存储器的通道编号) 通过MP系列以外的设备存取时为00H
        buffer[3] = 0x00;                                     // 发送源的通道编号(共享存储器的通道编号)。通过MP系列以外的设备存取时为00H。
        buffer[6] = BitConverter.GetBytes(buffer.length)[0];
        buffer[7] = BitConverter.GetBytes(buffer.length)[1];
        Utilities.ByteArrayCopyTo(command, buffer, 12);
        return buffer;
    }

    public static String GetErrorText( byte err ) {
        switch (err) {
            case 0x01:
                return StringResources.Language.Memobus01();
            case 0x02:
                return StringResources.Language.Memobus02();
            case 0x03:
                return StringResources.Language.Memobus03();
            case 0x40:
                return StringResources.Language.Memobus40();
            case 0x41:
                return StringResources.Language.Memobus41();
            case 0x42:
                return StringResources.Language.Memobus42();
            default:
                return StringResources.Language.UnknownError();
        }
    }

    public static OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response ) {
        try {
            if (send.length > 15 && response.length > 15) {
                if (send[15] + 0x80 == response[15] && response.length >= 18) {
                    return new OperateResultExOne<byte[]>(response[17], GetErrorText(response[17]) + " Source: " + SoftBasic.ByteToHexString(response, ' '));
                }
                if (send[15] != response[15]) {
                    return new OperateResultExOne<byte[]>(response[15], "Send SFC not same as back SFC:" + SoftBasic.ByteToHexString(response, ' '));
                }
            }

            return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 12));
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("UnpackResponseContent failed: " + ex.getMessage() + "  Source: " + SoftBasic.ByteToHexString(response, ' '));
        }
    }

    private static void SetByteHead( byte[] buffer, byte mfc, byte sfc, byte cpuTo, byte cpuFrom ) {
        buffer[0] = BitConverter.GetBytes(buffer.length - 2)[0];
        buffer[1] = BitConverter.GetBytes(buffer.length - 2)[1];
        buffer[2] = mfc;
        buffer[3] = sfc;
        buffer[4] = (byte) ((cpuTo << 4) + cpuFrom);
    }

    public static byte GetAddressDataType( String address ) {
        byte dataType = 0x4D;
        if (address.charAt(0) == 'M' || address.charAt(0) == 'm') dataType = 0x4D;
        else if (address.charAt(0) == 'G' || address.charAt(0) == 'g') dataType = 0x47;
        else if (address.charAt(0) == 'I' || address.charAt(0) == 'i') dataType = 0x49;
        else if (address.charAt(0) == 'O' || address.charAt(0) == 'o') dataType = 0x4F;
        else if (address.charAt(0) == 'S' || address.charAt(0) == 's') dataType = 0x53;
        return dataType;
    }

    private static int CalculateBoolIndex( String address ) {
        if (address.charAt(1) == 'B' || address.charAt(1) == 'b') address = address.substring(2);
        else address = address.substring(1);

        int indexPoint = address.indexOf('.');
        if (indexPoint > 0) {
            return Convert.ToInt32(address.substring(0, indexPoint)) * 16 + PlcHelper.CalculateBitStartIndex(address.substring(indexPoint + 1));
        } else
            return Convert.ToInt32(address.substring(0, address.length() - 1)) * 16 + PlcHelper.CalculateBitStartIndex(address.substring(address.length() - 1));
    }

    /**
     * 构建读取的命令报文，支持功能码 01,02,03,04,09,0A
     * @param mfc 主功能码
     * @param sfc 子功能码
     * @param cpuTo 目标的CPU编号
     * @param cpuFrom 发送源CPU编号
     * @param address 起始地址
     * @param length 读取地址长度
     * @return 结果报文信息
     */
    public static OperateResultExOne<byte[]> BuildReadCommand( byte mfc, byte sfc, byte cpuTo, byte cpuFrom, String address, short length ) {
        if (Extension.StartsWithAndNumber(address, new String[]{"M", "G", "I", "O", "S"})) {
            byte dataType = GetAddressDataType(address);

            if (address.charAt(1) == 'B' || address.charAt(1) == 'b' || address.indexOf('.') > 0) {
                // 批量读取位的情况
                int add = CalculateBoolIndex(address);
                byte[] buffer = new byte[16];
                SetByteHead(buffer, (byte) 0x43, (byte) 0x41, cpuTo, cpuFrom);
                buffer[6] = dataType;
                Utilities.ByteArrayCopyTo(BitConverter.GetBytes(add), buffer, 8);
                Utilities.ByteArrayCopyTo(BitConverter.GetBytes(length), buffer, 12);
                return OperateResultExOne.CreateSuccessResult(buffer);
            } else {
                // 批量读取字的情况
                byte[] buffer = new byte[14];
                SetByteHead(buffer, (byte) 0x43, (byte) 0x49, cpuTo, cpuFrom);
                buffer[6] = dataType;
                Utilities.ByteArrayCopyTo(BitConverter.GetBytes(Convert.ToInt32(address.substring(1))), buffer, 8);
                Utilities.ByteArrayCopyTo(BitConverter.GetBytes(length), buffer, 12);
                return OperateResultExOne.CreateSuccessResult(buffer);
            }
        } else {
            int add = 0;
            try {
                add = Integer.parseInt(address);
            } catch (Exception ex) {
                return new OperateResultExOne<byte[]>("Address[" + address + "] wrong, not supported");
            }
            if (add > 65535)
                return new OperateResultExOne<byte[]>("Address[" + address + "] wrong, can not larger than 65535");

            if (sfc == 0x01 || sfc == 0x02 || sfc == 0x03 || sfc == 0x04) {
                byte[] buffer = new byte[9];
                SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);

                buffer[5] = BitConverter.GetBytes(add)[1];
                buffer[6] = BitConverter.GetBytes(add)[0];
                buffer[7] = BitConverter.GetBytes(length)[1];
                buffer[8] = BitConverter.GetBytes(length)[0];
                return OperateResultExOne.CreateSuccessResult(buffer);
            } else if (sfc == 0x09 || sfc == 0x0A) {
                byte[] buffer = new byte[10];
                SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);

                buffer[6] = BitConverter.GetBytes(add)[0];
                buffer[7] = BitConverter.GetBytes(add)[1];
                buffer[8] = BitConverter.GetBytes(length)[0];
                buffer[9] = BitConverter.GetBytes(length)[1];
                return OperateResultExOne.CreateSuccessResult(buffer);
            } else
                return new OperateResultExOne<byte[]>("SFC:" + sfc + " " + StringResources.Language.NotSupportedFunction());
        }
    }

    public static OperateResultExOne<byte[]> BuildReadRandomCommand( byte mfc, byte sfc, byte cpuTo, byte cpuFrom, short[] address ) {
        byte[] buffer = new byte[8 + address.length * 2];
        SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);

        buffer[6] = BitConverter.GetBytes(address.length)[0];
        buffer[7] = BitConverter.GetBytes(address.length)[1];
        for (int i = 0; i < address.length; i++) {
            buffer[8 + i * 2 + 0] = BitConverter.GetBytes(address[i])[0];
            buffer[8 + i * 2 + 1] = BitConverter.GetBytes(address[i])[1];
        }
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildReadRandomCommand( byte cpuTo, byte cpuFrom, String[] address ) {
        byte[] buffer = new byte[8 + address.length * 6];
        SetByteHead(buffer, (byte) 0x43, (byte) 0x4D, cpuTo, cpuFrom);

        buffer[6] = BitConverter.GetBytes(address.length)[0];
        buffer[7] = BitConverter.GetBytes(address.length)[1];
        for (int i = 0; i < address.length; i++) {
            byte dataType = GetAddressDataType(address[i]);

            buffer[8 + i * 6 + 0] = dataType;
            buffer[8 + i * 6 + 1] = 0x02;
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes(Convert.ToInt32(address[i].substring(1))), buffer, 8 + i * 6 + 2);
        }
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    /**
     * 构建写入单一的线圈的状态变更的报文
     * @param mfc 主功能码
     * @param sfc 子功能码
     * @param cpuTo 目标的CPU编号
     * @param cpuFrom 发送源CPU编号
     * @param address 起始地址
     * @param value 写入的通断值信息
     * @return 写入的报文
     */
    public static OperateResultExOne<byte[]> BuildWriteCommand( byte mfc, byte sfc, byte cpuTo, byte cpuFrom, short address, boolean value ) {
        byte[] buffer = new byte[9];
        SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);
        buffer[5] = BitConverter.GetBytes(address)[1];
        buffer[6] = BitConverter.GetBytes(address)[0];
        buffer[7] = (byte) (value ? 0xFF : 0x00);
        buffer[8] = 0x00;
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand( byte mfc, byte sfc, byte cpuTo, byte cpuFrom, String address, boolean[] value ) {
        if (Extension.StartsWithAndNumber(address, new String[]{"M", "G", "I", "O", "S"})) {
            byte dataType = GetAddressDataType(address);

            if (address.charAt(1) == 'B' || address.charAt(1) == 'b' || address.indexOf('.') > 0) {
                // 批量写入位的情况
                int add = CalculateBoolIndex(address);
                byte[] buffer = new byte[16 + (value.length + 7) / 8];
                SetByteHead(buffer, (byte) 0x43, (byte) 0x4F, cpuTo, cpuFrom);
                buffer[6] = dataType;
                Utilities.ByteArrayCopyTo(BitConverter.GetBytes(add), buffer, 8);
                Utilities.ByteArrayCopyTo(BitConverter.GetBytes(value.length), buffer, 12);
                Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(value), buffer, 16);
                return OperateResultExOne.CreateSuccessResult(buffer);
            } else {
                return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
            }
        } else {
            int add = 0;
            try {
                add = Integer.parseInt(address);
            } catch (Exception ex) {
                return new OperateResultExOne<byte[]>("Address[" + address + "] wrong, not supported");
            }
            if (add > 65535)
                return new OperateResultExOne<byte[]>("Address[" + address + "] wrong, can not larger than 65535");


            byte[] data = SoftBasic.BoolArrayToByte(value);
            byte[] buffer = new byte[9 + data.length];
            SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);
            buffer[5] = BitConverter.GetBytes(add)[1];
            buffer[6] = BitConverter.GetBytes(add)[0];
            buffer[7] = BitConverter.GetBytes(value.length)[1];
            buffer[8] = BitConverter.GetBytes(value.length)[0];
            Utilities.ByteArrayCopyTo(data, buffer, 9);
            return OperateResultExOne.CreateSuccessResult(buffer);
        }
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand( byte mfc, byte sfc, byte cpuTo, byte cpuFrom, int address, short value ) {
        byte[] buffer = new byte[9];
        SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);
        buffer[5] = BitConverter.GetBytes(address)[1];
        buffer[6] = BitConverter.GetBytes(address)[0];
        buffer[7] = BitConverter.GetBytes(value)[1];
        buffer[8] = BitConverter.GetBytes(value)[0];
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    public static OperateResultExOne<byte[]> BuildWriteCommand( byte mfc, byte sfc, byte cpuTo, byte cpuFrom, String address, byte[] value ) {
        if (Extension.StartsWithAndNumber(address, new String[]{"M", "G", "I", "O", "S"})) {
            byte dataType = GetAddressDataType(address);

            byte[] buffer = new byte[14 + value.length];
            SetByteHead(buffer, (byte) 0x43, (byte) 0x4B, cpuTo, cpuFrom);
            buffer[6] = dataType;
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes(Convert.ToInt32(address.substring(1))), buffer, 8);
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes(value.length / 2), buffer, 12);
            Utilities.ByteArrayCopyTo(SoftBasic.BytesReverseByWord(value), buffer, 14);
            return OperateResultExOne.CreateSuccessResult(buffer);
        } else {
            int add = 0;
            try {
                add = Integer.parseInt(address);
            } catch (Exception ex) {
                return new OperateResultExOne<byte[]>("Address[" + address + "] wrong, not supported");
            }
            if (add > 65535)
                return new OperateResultExOne<byte[]>("Address[" + address + "] wrong, can not larger than 65535");

            if (sfc == 0x0B) {
                byte[] buffer = new byte[10 + value.length];
                SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);
                buffer[6] = BitConverter.GetBytes(add)[0];
                buffer[7] = BitConverter.GetBytes(add)[1];
                buffer[8] = BitConverter.GetBytes(value.length / 2)[0];
                buffer[9] = BitConverter.GetBytes(value.length / 2)[1];
                Utilities.ByteArrayCopyTo(SoftBasic.BytesReverseByWord(value), buffer, 10);
                return OperateResultExOne.CreateSuccessResult(buffer);
            } else if (sfc == 0x10) {
                byte[] buffer = new byte[9 + value.length];
                SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);
                buffer[5] = BitConverter.GetBytes(add)[1];
                buffer[6] = BitConverter.GetBytes(add)[0];
                buffer[7] = BitConverter.GetBytes(value.length / 2)[1];
                buffer[8] = BitConverter.GetBytes(value.length / 2)[0];
                Utilities.ByteArrayCopyTo(value, buffer, 9);
                return OperateResultExOne.CreateSuccessResult(buffer);
            } else
                return new OperateResultExOne<byte[]>("SFC:" + sfc + " " + StringResources.Language.NotSupportedFunction());
        }
    }

    public static OperateResultExOne<byte[]> BuildWriteRandomCommand( byte mfc, byte sfc, byte cpuTo, byte cpuFrom, int[] address, byte[] value ) {
        if (value.length != address.length * 2)
            return new OperateResultExOne<byte[]>("value.Length must be twice as much as address.Length");

        byte[] buffer = new byte[8 + address.length * 4];
        SetByteHead(buffer, mfc, sfc, cpuTo, cpuFrom);

        buffer[6] = BitConverter.GetBytes(address.length)[0];
        buffer[7] = BitConverter.GetBytes(address.length)[1];
        for (int i = 0; i < address.length; i++) {
            buffer[8 + i * 4 + 0] = BitConverter.GetBytes(address[i])[0];
            buffer[8 + i * 4 + 1] = BitConverter.GetBytes(address[i])[1];
            buffer[8 + i * 4 + 2] = value[i * 2 + 1];                             // 这里的数据直接在赋值的时候就给颠倒了
            buffer[8 + i * 4 + 3] = value[i * 2 + 0];
        }
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    // endregion

    // region Read Write

    /**
     * 默认使用功能码01，读取线圈操作，如果需要指定读取输入线圈，地址需要携带额外的参数，例如 x=2;100<br />
     * The function code 01 is used by default to read the coil operation. If you need to specify the read input coil, the address needs to carry additional parameters, such as x=2;100
     * @param memobus 通信对象
     * @param address 地址
     * @param length 长度
     * @return 读取的结果
     */
    public static OperateResultExOne<boolean[]> ReadBool( IMemobus memobus, String address, short length ) {
        byte mfc = 0x20;
        OperateResultExTwo<Integer, String> mfc_extra = PlcHelper.ExtractParameter(address, "mfc");
        if (mfc_extra.IsSuccess) {
            mfc = mfc_extra.Content1.byteValue();
            address = mfc_extra.Content2;
        }
        byte sfc = 0x01;
        OperateResultExTwo<Integer, String> sfc_extra = PlcHelper.ExtractParameter(address, "x");
        if (sfc_extra.IsSuccess) {
            sfc = sfc_extra.Content1.byteValue();
            address = sfc_extra.Content2;
        }

        OperateResultExOne<byte[]> command = BuildReadCommand(mfc, sfc, memobus.getCpuTo(), memobus.getCpuFrom(), address, length);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        if (read.Content[3] == 0x41)
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectBegin(SoftBasic.ByteToBoolArray(SoftBasic.BytesArrayRemoveBegin(read.Content, 8)), length));
        else
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectBegin(SoftBasic.ByteToBoolArray(SoftBasic.BytesArrayRemoveBegin(read.Content, 5)), length));
    }

    /**
     * 单一线圈的状态变更，使用的主功能码为0x20, 子功能码为0x05<br />
     * The status of a single coil is changed, the main function code used is 0x20, and the sub function code is 0x05
     * @param memobus 通信对象
     * @param address 地址
     * @param value 写入的值
     * @return 是否写入成功
     */
    public static OperateResult Write(IMemobus memobus, String address, boolean value ) {
        if (Extension.StartsWithAndNumber(address, new String[]{"M", "G", "I", "O", "S"}))
            return Write(memobus, address, new boolean[]{value});

        byte mfc = 0x20;
        OperateResultExTwo<Integer, String> mfc_extra = PlcHelper.ExtractParameter(address, "mfc");
        if (mfc_extra.IsSuccess) {
            mfc = mfc_extra.Content1.byteValue();
            address = mfc_extra.Content2;
        }
        byte sfc = 0x05;
        OperateResultExTwo<Integer, String> sfc_extra = PlcHelper.ExtractParameter(address, "x");
        if (sfc_extra.IsSuccess) {
            sfc = sfc_extra.Content1.byteValue();
            address = sfc_extra.Content2;
        }

        OperateResultExOne<byte[]> command = BuildWriteCommand(mfc, sfc, memobus.getCpuTo(), memobus.getCpuFrom(), (short) Integer.parseInt(address), value);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResult.CreateSuccessResult();
    }


    /**
     * 多个线圈的状态更改，默认使用的是 0x0f 子功能码。<br />
     * The status of multiple coils is changed, and the sub-function code 0x0f is used by default.
     * @param memobus 通信对象
     * @param address 地址
     * @param value 写入的值
     * @return 是否写入成功s
     */
    public static OperateResult Write( IMemobus memobus, String address, boolean[] value ) {
        byte mfc = 0x20;
        OperateResultExTwo<Integer, String> mfc_extra = PlcHelper.ExtractParameter(address, "mfc");
        if (mfc_extra.IsSuccess) {
            mfc = mfc_extra.Content1.byteValue();
            address = mfc_extra.Content2;
        }
        byte sfc = 0x0F;
        OperateResultExTwo<Integer, String> sfc_extra = PlcHelper.ExtractParameter(address, "x");
        if (sfc_extra.IsSuccess) {
            sfc = sfc_extra.Content1.byteValue();
            address = sfc_extra.Content2;
        }

        OperateResultExOne<byte[]> command = BuildWriteCommand(mfc, sfc, memobus.getCpuTo(), memobus.getCpuFrom(), address, value);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResult.CreateSuccessResult();
    }

    private static OperateResultExOne<byte[]> ExtraContent( String address, byte[] content ) {
        if (content[2] == 0x20) {
            if (content[3] == 0x03 || content[3] == 0x04)
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(content, 5));
            else if (content[3] == 0x09 || content[3] == 0x0A)
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesReverseByWord(SoftBasic.BytesArrayRemoveBegin(content, 8)));
            else
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(content, 5));
        } else if (content[2] == 0x43) {
            if (content[3] == 0x49 || content[3] == 0x4D)
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesReverseByWord(SoftBasic.BytesArrayRemoveBegin(content, 10)));
            else
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(content, 8));
        } else {
            return new OperateResultExOne<byte[]>("[" + address + "], mfc[" + content[2] + "] is not supported");
        }
    }

    /**
     * 地址默认使用功能码03，如果需要指定其他的功能码地址，需要手动指定功能码，例如：x=4;100, x=9;100, x=10;100, 当然也可以写成 x=0x0A;100<br />
     * The address uses function code 03 by default. If you need to specify other function code addresses, you need to manually specify the function code, for example: x=4;100, x=9;100, x=10;100, of course, it can also be written as x=0x0A; 100
     * @param memobus 通信对象
     * @param address 地址
     * @param length 长度
     * @return 读取成功的结果对象
     */
    public static OperateResultExOne<byte[]> Read( IMemobus memobus, String address, short length ) {
        byte mfc = 0x20;
        OperateResultExTwo<Integer, String> mfc_extra = PlcHelper.ExtractParameter(address, "mfc");
        if (mfc_extra.IsSuccess) {
            mfc = mfc_extra.Content1.byteValue();
            address = mfc_extra.Content2;
        }
        byte sfc = 0x03;
        OperateResultExTwo<Integer, String> sfc_extra = PlcHelper.ExtractParameter(address, "x");
        if (sfc_extra.IsSuccess) {
            sfc = sfc_extra.Content1.byteValue();
            address = sfc_extra.Content2;
        }

        OperateResultExOne<byte[]> command = BuildReadCommand(mfc, sfc, memobus.getCpuTo(), memobus.getCpuFrom(), address, length);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return ExtraContent(address, read.Content);
    }


    /**
     * 连续的寄存器写入操作，默认功能码是0x10，如果需要写入扩展的寄存器，使用 x=0xA;100 或是 x=10;100 即可。<br />
     * For continuous register write operation, the default function code is 0x10. If you need to write an extended register, use x=0xA;100 or x=10;100.
     * @param memobus 通信对象
     * @param address 地址
     * @param value 写入的值
     * @return 是否写入成功
     */
    public static OperateResult Write( IMemobus memobus, String address, byte[] value ) {
        byte mfc = 0x20;
        OperateResultExTwo<Integer, String> mfc_extra = PlcHelper.ExtractParameter(address, "mfc");
        if (mfc_extra.IsSuccess) {
            mfc = mfc_extra.Content1.byteValue();
            address = mfc_extra.Content2;
        }
        byte sfc = 0x10;
        OperateResultExTwo<Integer, String> sfc_extra = PlcHelper.ExtractParameter(address, "x");
        if (sfc_extra.IsSuccess) {
            sfc = sfc_extra.Content1.byteValue();
            address = sfc_extra.Content2;
        }
        if (sfc == 0x03) sfc = 0x10;
        if (sfc == 0x09) sfc = 0x0B;

        OperateResultExOne<byte[]> command = BuildWriteCommand(mfc, sfc, memobus.getCpuTo(), memobus.getCpuFrom(), address, value);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResult.CreateSuccessResult();
    }


    /**
     * 单一保持寄存器的值变更，使用的主功能码为0x20, 默认子功能码为0x06，也可以写入扩展的保持型寄存器，子功能码为0x0B<br />
     * The value of a single hold register is changed, using a primary function code of 0x20 and a default subfunction code of 0x06, or an extended holding register with a subfunction code of 0x0B
     * @param memobus 通信对象
     * @param address 写入的地址
     * @param value 写入的值
     * @return 是否写入成功
     */
    public static OperateResult Write( IMemobus memobus, String address, short value ) {
        byte mfc = 0x20;
        OperateResultExTwo<Integer, String> mfc_extra = PlcHelper.ExtractParameter(address, "mfc");
        if (mfc_extra.IsSuccess) {
            mfc = mfc_extra.Content1.byteValue();
            address = mfc_extra.Content2;
        }
        byte sfc = 0x06;
        OperateResultExTwo<Integer, String> sfc_extra = PlcHelper.ExtractParameter(address, "x");
        if (sfc_extra.IsSuccess) {
            sfc = sfc_extra.Content1.byteValue();
            address = sfc_extra.Content2;
        }
        if (sfc == 0x0B || sfc == 0x09) {
            return memobus.Write("x=" + sfc + ";" + address, memobus.getByteTransform().TransByte(value));
        }
        if (sfc == 0x03) sfc = 0x06;

        OperateResultExOne<byte[]> command = BuildWriteCommand(mfc, sfc, memobus.getCpuTo(), memobus.getCpuFrom(), Integer.parseInt(address), value);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResult.CreateSuccessResult();
    }


    /**
     * 随机读取扩展的保持寄存器的内容，也即读取不连续地址的字数据，可以指定多个地址，然后一次性读取所有的数据，然后解析出实际的数据<br />
     * Randomly read the contents of the extended hold register, that is, read word data of discontinuous addresses,
     * you can specify multiple addresses, then read all the data at once, and then parse out the actual data
     * @param memobus PLC通信对象
     * @param address 地址信息
     * @return 读取的原始字节结果信息
     */
    public static OperateResultExOne<byte[]> ReadRandom( IMemobus memobus, short[] address ) {
        OperateResultExOne<byte[]> command = BuildReadRandomCommand((byte) 0x20, (byte) 0x0D, memobus.getCpuTo(), memobus.getCpuFrom(), address);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesReverseByWord(SoftBasic.BytesArrayRemoveBegin(read.Content, 8)));
    }

    /**
     * 本方法的地址支持 保持寄存器 M100, 数据寄存器 G100, 输入寄存器 I100, 输出寄存器 O100, 系统寄存器 S100,
     * @param memobus 通信对象
     * @param address 地址列表
     * @return 读取成功的结果对象
     */
    public static OperateResultExOne<byte[]> ReadRandom( IMemobus memobus, String[] address ) {
        OperateResultExOne<byte[]> command = BuildReadRandomCommand(memobus.getCpuTo(), memobus.getCpuFrom(), address);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesReverseByWord(SoftBasic.BytesArrayRemoveBegin(read.Content, 8)));
    }

    /**
     * 随机写入扩展的保持寄存器的内容，也即写入不连续的地址的字数据，字节数组的长度必须为地址数组长度的两倍，才能正确写入。<br />
     * Write the contents of the extended hold registers randomly, that is, write word data for discontinuous addresses, and the byte array must be twice the length of the address array to be written correctly.
     * @param memobus 通信对象
     * @param address 地址信息
     * @param value 写入的值
     * @return 是否写入成功
     */
    public static OperateResult WriteRandom( IMemobus memobus, int[] address, byte[] value ) {
        OperateResultExOne<byte[]> command = BuildWriteRandomCommand((byte) 0x20, (byte) 0x0E, memobus.getCpuTo(), memobus.getCpuFrom(), address, value);
        if (!command.IsSuccess) return command;

        OperateResultExOne<byte[]> read = memobus.ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResult.CreateSuccessResult();
    }

    // endregion
}
