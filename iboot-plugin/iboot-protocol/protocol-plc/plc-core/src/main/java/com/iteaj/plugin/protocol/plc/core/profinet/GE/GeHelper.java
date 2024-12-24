package com.iteaj.plugin.protocol.plc.core.profinet.GE;


import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;
import com.iteaj.plugin.protocol.plc.core.address.GeSRTPAddress;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

public class GeHelper {

    /**
     * 构建一个读取数据的报文信息，需要指定操作的数据代码，读取的参数信息<br />
     * To construct a message information for reading data, you need to specify the data code of the operation and the parameter information to be read
     * @param id 消息号
     * @param code 操作代码
     * @param data 数据参数
     * @return 包含是否成功的报文信息
     */
    public static OperateResultExOne<byte[]> BuildReadCoreCommand(long id, byte code, byte[] data ) {
        byte[] buffer = new byte[56];
        buffer[0] = 0x02;
        buffer[1] = 0x00;
        buffer[2] = Utilities.getBytes(id)[0];
        buffer[3] = Utilities.getBytes(id)[1];
        buffer[4] = 0x00;        // Length
        buffer[5] = 0x00;
        buffer[9] = 0x01;
        buffer[17] = 0x01;
        buffer[18] = 0x00;
        buffer[30] = 0x06;
        buffer[31] = (byte) 0xC0;
        buffer[36] = 0x10;
        buffer[37] = 0x0E;
        buffer[40] = 0x01;
        buffer[41] = 0x01;
        buffer[42] = code;        // read system memory
        System.arraycopy(data, 0, buffer, 43, data.length);
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    /**
     * 构建一个读取数据的报文命令，需要指定消息号，读取的 GE 地址信息<br />
     * To construct a message command to read data, you need to specify the message number and read GE address information
     * @param id 消息号
     * @param address GE 的地址
     * @return 包含是否成功的报文信息
     */
    public static OperateResultExOne<byte[]> BuildReadCommand( long id, GeSRTPAddress address ) {
        if (address.getDataCode() == 0x0A ||
                address.getDataCode() == 0x0C ||
                address.getDataCode() == 0x08) {
            address.setLength(address.getLength() / 2);
        }

        byte[] buffer = new byte[5];
        buffer[0] = address.getDataCode();
        buffer[1] = Utilities.getBytes(address.getAddressStart())[0];
        buffer[2] = Utilities.getBytes(address.getAddressStart())[1];
        buffer[3] = Utilities.getBytes(address.getLength())[0];
        buffer[4] = Utilities.getBytes(address.getLength())[1];
        return BuildReadCoreCommand(id, (byte) 0x04, buffer);
    }

    /// <summary>
    /// 构建一个读取数据的报文命令，需要指定消息号，地址，长度，是否位读取，返回完整的报文信息。<br />
    /// To construct a message command to read data, you need to specify the message number,
    /// address, length, whether to read in bits, and return the complete message information.
    /// </summary>
    /// <param name="id">消息号</param>
    /// <param name="address">地址</param>
    /// <param name="length">读取的长度</param>
    /// <param name="isBit"></param>
    /// <returns>包含是否成功的报文对象</returns>
    public static OperateResultExOne<byte[]> BuildReadCommand( long id, String address, short length, boolean isBit )
    {
        OperateResultExOne<GeSRTPAddress> analysis = GeSRTPAddress.ParseFrom( address, length, isBit );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        return BuildReadCommand( id, analysis.Content );
    }


    public static OperateResultExOne<byte[]> BuildWriteCommand( long id, GeSRTPAddress address, byte[] value ) {
        int length = address.getLength();
        if (address.getDataCode() == 0x0A ||
                address.getDataCode() == 0x0C ||
                address.getDataCode() == 0x08) {
            length /= 2;
        }

        byte[] buffer = new byte[56 + value.length];
        buffer[0] = 0x02;
        buffer[1] = 0x00;
        buffer[2] = Utilities.getBytes(id)[0];
        buffer[3] = Utilities.getBytes(id)[1];
        buffer[4] = Utilities.getBytes(value.length)[0];        // Length
        buffer[5] = Utilities.getBytes(value.length)[1];
        buffer[9] = 0x02;
        buffer[17] = 0x02;
        buffer[18] = 0x00;
        buffer[30] = 0x09;
        buffer[31] = (byte) 0x80;
        buffer[36] = 0x10;
        buffer[37] = 0x0E;
        buffer[40] = 0x01;
        buffer[41] = 0x01;
        buffer[42] = 0x02;
        buffer[48] = 0x01;
        buffer[49] = 0x01;
        buffer[50] = 0x07;   // 写入数据
        buffer[51] = address.getDataCode();
        buffer[52] = Utilities.getBytes(address.getAddressStart())[0];
        buffer[53] = Utilities.getBytes(address.getAddressStart())[1];
        buffer[54] = Utilities.getBytes(length)[0];
        buffer[55] = Utilities.getBytes(length)[1];
        System.arraycopy(value, 0, buffer, 56, value.length);
        return OperateResultExOne.CreateSuccessResult(buffer);
    }

    /**
     * 构建一个批量写入 byte 数组变量的报文，需要指定消息号，写入的地址，地址参照 <see cref="GeSRTPNet"/> 说明。<br />
     * To construct a message to be written into byte array variables in batches,
     * you need to specify the message number and write address. For the address, refer to the description of <see cref="GeSRTPNet"/>.
     * @param id 消息的序号
     * @param address 地址信息
     * @param value byte数组的原始数据
     * @return 包含结果信息的报文内容
     */
    public static OperateResultExOne<byte[]> BuildWriteCommand( long id, String address, byte[] value ) {
        OperateResultExOne<GeSRTPAddress> analysis = GeSRTPAddress.ParseFrom(address, (short) value.length, false);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        return BuildWriteCommand(id, analysis.Content, value);
    }

    /**
     * 构建一个批量写入 bool 数组变量的报文，需要指定消息号，写入的地址，地址参照 <see cref="GeSRTPNet"/> 说明。<br />
     * To construct a message to be written into bool array variables in batches,
     * you need to specify the message number and write address. For the address, refer to the description of <see cref="GeSRTPNet"/>.
     * @param id 消息的序号
     * @param address 地址信息
     * @param value bool数组
     * @return 包含结果信息的报文内容
     */
    public static OperateResultExOne<byte[]> BuildWriteCommand( long id, String address, boolean[] value ) {
        OperateResultExOne<GeSRTPAddress> analysis = GeSRTPAddress.ParseFrom(address, (short) value.length, true);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        boolean[] boolArray = new boolean[analysis.Content.getAddressStart() % 8 + value.length];
        System.arraycopy(value, 0, boolArray, analysis.Content.getAddressStart() % 8, value.length);
        return BuildWriteCommand(id, analysis.Content, SoftBasic.BoolArrayToByte(boolArray));
    }

    /**
     * 从PLC返回的数据中，提取出实际的数据内容，最少6个字节的数据。超出实际的数据长度的部分没有任何意义。<br />
     * From the data returned by the PLC, extract the actual data content, at least 6 bytes of data. The part beyond the actual data length has no meaning.
     * @param content PLC返回的数据信息
     * @return 解析后的实际数据内容
     */
    public static OperateResultExOne<byte[]> ExtraResponseContent( byte[] content ) {
        try {
            if (content[0] != 0x03)
                return new OperateResultExOne<byte[]>(content[0], StringResources.Language.UnknownError() +
                        " Source:" + SoftBasic.ByteToHexString(content, ' '));
            if (content[31] == (byte) 0xD4) {
                int status = Utilities.getUShort(content, 42);
                if (status != 0) return new OperateResultExOne<byte[]>(status, StringResources.Language.UnknownError());
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArraySelectMiddle(content, 44, 6));
            }
            if (content[31] == (byte) 0x94)
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(content, 56));
            return new OperateResultExOne<byte[]>("Extra Wrong:" + StringResources.Language.UnknownError() + " Source:" + SoftBasic.ByteToHexString(content, ' '));
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("Extra Wrong:" + ex.getMessage() + " Source:" + (SoftBasic.ByteToHexString(content, ' ')));
        }
    }

    /**
     * 从实际的时间的字节数组里解析出C#格式的时间对象，这个时间可能是时区0的时间，需要自行转化本地时间。<br />
     * Analyze the time object in C# format from the actual time byte array.
     * This time may be the time in time zone 0, and you need to convert the local time yourself.
     * @param content 字节数组
     * @return 包含是否成功的结果对象
     */
    public static OperateResultExOne<Date> ExtraDateTime( byte[] content ) {

        try {
            Calendar calendar = Calendar.getInstance();
            calendar.set(
                    Integer.parseInt(String.format("%02X", content[5])) + 2000,
                    Integer.parseInt(String.format("%02X", content[4])),
                    Integer.parseInt(String.format("%02X", content[3])),
                    Integer.parseInt(String.format("%02X", content[2])),
                    Integer.parseInt(String.format("%02X", content[1])),
                    Integer.parseInt(String.format("%02X", content[0])));
            return OperateResultExOne.CreateSuccessResult(calendar.getTime());
        } catch (Exception ex) {
            return new OperateResultExOne<Date>(ex.getMessage() + " Source:" + SoftBasic.ByteToHexString(content, ' '));
        }
    }


    //0000   03 00 07 00 2a 00 00 00 00 00 00 00 00 00 00 00
    //0010   00 01 00 00 00 00 00 00 00 00 00 00 00 00 06 94
    //0020   00 0e 00 00 00 62 01 a0 00 00 2a 00 00 18 00 00
    //0030   01 01 ff 02 03 00 5c 01 00 00 00 00 00 00 00 00
    //0040   01 00 00 00 00 00 00 00 00 00 50 41 43 34 30 30
    //0050   00 00 00 00 00 00 00 00 00 00 03 00 01 50 05 18
    //0060   01 21

    /**
     * 实际的返回的字节数组里解析出PLC的程序的名称。<br />
     * @param content 字节数组
     * @return 包含是否成功的结果对象
     */
    public static OperateResultExOne<String> ExtraProgramName(byte[] content ) {

        try {
            return OperateResultExOne.CreateSuccessResult(new String(content, 18, 16, StandardCharsets.UTF_8).trim());
        } catch (Exception ex) {
            return new OperateResultExOne<String>(ex.getMessage() + " Source:" + SoftBasic.ByteToHexString(content, ' '));
        }
    }
}
