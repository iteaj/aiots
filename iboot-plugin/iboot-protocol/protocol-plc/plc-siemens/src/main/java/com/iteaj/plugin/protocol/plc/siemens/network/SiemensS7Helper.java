package com.iteaj.plugin.protocol.plc.siemens.network;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.net.IReadWriteNet;
import com.iteaj.plugin.protocol.plc.core.types.BitConverter;
import com.iteaj.plugin.protocol.plc.core.types.List;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SiemensS7Helper {
    /**
     * 读取BOOL时，根据S7协议的返回报文，正确提取出实际的数据内容
     * @param content PLC返回的原始字节信息
     * @return 解析之后的结果对象
     */
    public static OperateResultExOne<byte[]> AnalysisReadBit(byte[] content ) {
        try {
            int receiveCount = 1;
            if (content.length >= 21 && content[20] == 1) {
                byte[] buffer = new byte[receiveCount];
                if (22 < content.length) {
                    if (content[21] == (byte) 0xFF &&
                            content[22] == 0x03) {
                        buffer[0] = content[25];
                    } else if (content[21] == 0x05 &&
                            content[22] == 0x00) {
                        return new OperateResultExOne<byte[]>(content[21], StringResources.Language.SiemensReadLengthOverPlcAssign());
                    } else if (content[21] == 0x06 &&
                            content[22] == 0x00) {
                        return new OperateResultExOne<byte[]>(content[21], StringResources.Language.SiemensError0006());
                    } else if (content[21] == 0x0A &&
                            content[22] == 0x00) {
                        return new OperateResultExOne<byte[]>(content[21], StringResources.Language.SiemensError000A());
                    } else {
                        return new OperateResultExOne<byte[]>(content[21], StringResources.Language.UnknownError() + " Source: " + SoftBasic.ByteToHexString(content, ' '));
                    }
                }

                return OperateResultExOne.CreateSuccessResult(buffer);
            } else {
                return new OperateResultExOne<byte[]>(StringResources.Language.SiemensDataLengthCheckFailed());
            }
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("AnalysisReadBit failed: " + ex.getMessage() + "\r\n Msg:" + SoftBasic.ByteToHexString(content, ' '));
        }
    }

    public static ArrayList<S7AddressData[]> ArraySplitByLength(S7AddressData[] s7Addresses, int pduLength ) {
        List<S7AddressData[]> array = new List<S7AddressData[]>();
        List<S7AddressData> tmpArray = new List<S7AddressData>();

        int lengthTotle = 0;
        for (int i = 0; i < s7Addresses.length; i++) {
            if (tmpArray.size() >= 19 || (lengthTotle + s7Addresses[i].getLength()) >= pduLength) {
                if (tmpArray.size() > 0) {
                    array.Add(tmpArray.toArray(new S7AddressData[0]));
                    tmpArray.clear();
                }
                lengthTotle = 0;
            }
            tmpArray.Add(s7Addresses[i]);
            lengthTotle += s7Addresses[i].getLength();
        }
        if (tmpArray.size() > 0) array.Add(tmpArray.toArray(new S7AddressData[0]));
        return array;
    }

    /**
     * 读取字数据时，根据S7协议返回的报文，解析出实际的原始字节数组信息
     * @param content PLC返回的原始字节数组
     * @return 实际的结果数据对象
     */
    public static OperateResultExOne<byte[]> AnalysisReadByte( byte[] content ) {
        try {
            ArrayList<Byte> list = new ArrayList<>();
            if (content.length >= 21) {
                for (int i = 21; i < content.length - 1; i++) {
                    if (content[i] == (byte) 0xFF && content[i + 1] == 0x04) {
                        int count = (content[i + 2] * 256 + content[i + 3]) / 8;
                        Utilities.ArrayListAddArray(list, SoftBasic.BytesArraySelectMiddle(content, i + 4, count));
                        i += count + 3;
                    } else if (content[i] == (byte) 0xFF && content[i + 1] == 0x09) {
                        int count = content[i + 2] * 256 + content[i + 3];
                        if (count % 3 == 0) {
                            for (int j = 0; j < count / 3; j++) {
                                Utilities.ArrayListAddArray(list, SoftBasic.BytesArraySelectMiddle(content, i + 5 + 3 * j, 2));
                            }
                        } else {
                            for (int j = 0; j < count / 5; j++) {
                                Utilities.ArrayListAddArray(list, SoftBasic.BytesArraySelectMiddle(content, i + 7 + 5 * j, 2));
                            }
                        }
                        i += count + 4;
                    } else if (content[i] == 0x05 && content[i + 1] == 0x00)
                        return new OperateResultExOne<byte[]>(content[i], StringResources.Language.SiemensReadLengthOverPlcAssign());
                    else if (content[i] == 0x06 && content[i + 1] == 0x00)
                        return new OperateResultExOne<byte[]>(content[i], StringResources.Language.SiemensError0006());
                    else if (content[i] == 0x0A && content[i + 1] == 0x00)
                        return new OperateResultExOne<byte[]>(content[i], StringResources.Language.SiemensError000A());
                }
                return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(list));
            } else {
                return new OperateResultExOne<byte[]>(StringResources.Language.SiemensDataLengthCheckFailed() + " Msg: " + SoftBasic.ByteToHexString(content, ' '));
            }
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>("AnalysisReadByte failed: " + ex.getMessage() + "\r\n Msg:" + SoftBasic.ByteToHexString(content, ' '));
        }
    }


    /**
     * 读取西门子的地址的字符串信息，这个信息是和西门子绑定在一起，长度随西门子的信息动态变化的<br />
     * Read the Siemens address string information. This information is bound to Siemens and its length changes dynamically with the Siemens information
     * @param plc PLC的通信对象
     * @param currentPlc PLC的系列信息
     * @param address 数据地址，具体的格式需要参照类的说明文档
     * @param encoding 自定的编码信息，一般 {@link StandardCharsets#US_ASCII} 即可，中文需要 {@link Charset#forName(String)}
     * @return 带有是否成功的字符串结果类对象
     */
    public static OperateResultExOne<String> ReadString(IReadWriteNet plc, SiemensPLCS currentPlc, String address, Charset encoding ) {
        if (currentPlc != SiemensPLCS.S200Smart) {
            OperateResultExOne<byte[]> read = plc.Read(address, (short) 2);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            if (read.Content[0] == 0 || read.Content[0] == (byte) 255)
                return new OperateResultExOne<String>("Value in plc is not string type");    // max string length can't be zero

            OperateResultExOne<byte[]> readString = plc.Read(address, (short) (2 + read.Content[1]));
            if (!readString.IsSuccess) return OperateResultExOne.CreateFailedResult(readString);

            return OperateResultExOne.CreateSuccessResult(new String(readString.Content, 2, readString.Content.length - 2, encoding));
        } else {
            OperateResultExOne<byte[]> read = plc.Read(address, (short) 1);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            OperateResultExOne<byte[]> readString = plc.Read(address, (short) (1 + read.Content[0]));
            if (!readString.IsSuccess) return OperateResultExOne.CreateFailedResult(readString);

            return OperateResultExOne.CreateSuccessResult(new String(readString.Content, 1, readString.Content.length - 1, encoding));
        }
    }

    /**
     * 将指定的字符串写入到西门子PLC里面去，将自动添加字符串长度信息，方便PLC识别字符串的内容。<br />
     * Write the specified string into Siemens PLC, and the string length information will be automatically added, which is convenient for PLC to identify the content of the string.
     * @param plc PLC的通信对象
     * @param currentPlc PLC的系列信息
     * @param address 数据地址，具体的格式需要参照类的说明文档
     * @param value 写入的字符串值
     * @param encoding 编码信息
     * @return 是否写入成功
     */
    public static OperateResult Write(IReadWriteNet plc, SiemensPLCS currentPlc, String address, String value, Charset encoding ) {
        if (value == null) value = "";

        byte[] buffer = value.getBytes(encoding);
        if (encoding == StandardCharsets.UTF_16) buffer = SoftBasic.BytesReverseByWord(buffer);

        if (currentPlc != SiemensPLCS.S200Smart) {
            // need read one time
            OperateResultExOne<byte[]> readLength = plc.Read(address, (short) 2);
            if (!readLength.IsSuccess) return readLength;

            if (readLength.Content[0] == (byte) 255)
                return new OperateResultExOne<String>("Value in plc is not string type");
            if (readLength.Content[0] == 0) readLength.Content[0] = (byte) 254; // allow to create new string
            if (value.length() > readLength.Content[0])
                return new OperateResultExOne<String>("String length is too long than plc defined");

            return plc.Write(address, SoftBasic.SpliceArray(new byte[]{readLength.Content[0], (byte) buffer.length}, buffer));
        } else {
            return plc.Write(address, SoftBasic.SpliceArray(new byte[]{(byte) buffer.length}, buffer));
        }
    }

    /**
     * 读取西门子的地址的字符串信息，这个信息是和西门子绑定在一起，长度随西门子的信息动态变化的<br />
     * Read the Siemens address string information. This information is bound to Siemens and its length changes dynamically with the Siemens information
     * @param plc PLC的通信对象
     * @param currentPlc PLC的系列信息
     * @param address 数据地址，具体的格式需要参照类的说明文档
     * @return 带有是否成功的字符串结果类对象
     */
    public static OperateResultExOne<String> ReadWString( IReadWriteNet plc, SiemensPLCS currentPlc, String address ) {
        if (currentPlc != SiemensPLCS.S200Smart) {
            OperateResultExOne<byte[]> read = plc.Read(address, (short) 4); // 2
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            OperateResultExOne<byte[]> readString = plc.Read(address, (short) (4 + (read.Content[2] * 256 + read.Content[3]) * 2));
            if (!readString.IsSuccess) return OperateResultExOne.CreateFailedResult(readString);

            return OperateResultExOne.CreateSuccessResult(new String(SoftBasic.BytesReverseByWord(SoftBasic.BytesArrayRemoveBegin(readString.Content, 4)), StandardCharsets.UTF_16));
        } else {
            OperateResultExOne<byte[]> read = plc.Read(address, (short) 1);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            OperateResultExOne<byte[]> readString = plc.Read(address, (short) (1 + read.Content[0] * 2));
            if (!readString.IsSuccess) return OperateResultExOne.CreateFailedResult(readString);

            return OperateResultExOne.CreateSuccessResult(new String(readString.Content, 1, readString.Content.length - 1, StandardCharsets.UTF_16));
        }
    }

    /**
     * 使用双字节编码的方式，将字符串以 Unicode 编码写入到PLC的地址里，可以使用中文。<br />
     * Use the double-byte encoding method to write the character string to the address of the PLC in Unicode encoding. Chinese can be used.
     * @param plc PLC的通信对象
     * @param currentPlc PLC的系列信息
     * @param address 起始地址，格式为I100，M100，Q100，DB20.100 -> Starting address, formatted as I100,mM100,Q100,DB20.100
     * @param value 字符串的值
     * @return 是否写入成功的结果对象
     */
    public static OperateResult WriteWString( IReadWriteNet plc, SiemensPLCS currentPlc, String address, String value ) {
        //await WriteAsync( address, value, Encoding.Unicode );
        if (currentPlc != SiemensPLCS.S200Smart) {
            if (value == null) value = "";
            byte[] buffer = value.getBytes(StandardCharsets.UTF_16);
            buffer = SoftBasic.BytesReverseByWord(buffer);

            // need read one time
            OperateResultExOne<byte[]> readLength = plc.Read(address, (short) 4);
            if (!readLength.IsSuccess) return readLength;

            int defineLength = readLength.Content[0] * 256 + readLength.Content[1];
            if (value.length() > defineLength)
                return new OperateResultExOne<String>("String length is too long than plc defined");

            byte[] write = new byte[buffer.length + 4];
            write[0] = readLength.Content[0];
            write[1] = readLength.Content[1];
            write[2] = BitConverter.GetBytes(value.length())[1];
            write[3] = BitConverter.GetBytes(value.length())[0];
            Utilities.ByteArrayCopyTo(buffer, write, 4);
            return plc.Write(address, write);
        } else {
            return plc.Write(address, value, StandardCharsets.UTF_16);
        }
    }

}
