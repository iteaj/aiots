package com.iteaj.plugin.protocol.plc.core.profinet.Siemens;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.basic.SoftIncrementCount;
import com.iteaj.plugin.protocol.plc.core.address.S7AddressData;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.S7Message;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.ByteTransformHelper;
import com.iteaj.plugin.protocol.plc.core.transfer.ReverseBytesTransform;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * 西门子的数据交互类，采用s7协议实现，地址支持I,Q,M,DB，AI,AQ，举例：I100,Q0,M100,DB1.0,AI0,AQ16，支持单个位的读写，暂时不支持批量位写入操作
 */
public class SiemensS7Net extends NetworkDeviceBase {
    /**
     * 实例化一个西门子的S7协议的通讯对象
     *
     * @param siemens 指定西门子的型号
     */
    public SiemensS7Net(SiemensPLCS siemens) {
        Initialization(siemens, "");
    }

    /**
     * 实例化一个西门子的S7协议的通讯对象并指定Ip地址
     *
     * @param siemens   指定西门子的型号
     * @param ipAddress Ip地址
     */
    public SiemensS7Net(SiemensPLCS siemens, String ipAddress) {
        Initialization(siemens, ipAddress);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new S7Message();
    }

    /**
     * 初始化方法
     *
     * @param siemens   西门子类型
     * @param ipAddress Ip地址
     */
    private void Initialization(SiemensPLCS siemens, String ipAddress) {
        WordLength = 2;
        setIpAddress(ipAddress);
        setPort(102);
        CurrentPlc = siemens;
        setByteTransform(new ReverseBytesTransform());

        switch (siemens) {
            case S1200:
                plcHead1[21] = 0;
                break;
            case S300:
                plcHead1[21] = 2;
                break;
            case S400:
                plcHead1[21] = 3;
                plcHead1[17] = 0;
                break;
            case S1500:
                plcHead1[21] = 0;
                break;
            case S200Smart: {
                plcHead1 = plcHead1_200smart;
                plcHead2 = plcHead2_200smart;
                break;
            }
            case S200: {
                plcHead1 = plcHead1_200;
                plcHead2 = plcHead2_200;
                break;
            }
            default:
                plcHead1[18] = 0;
                break;
        }
    }

    /**
     * 获取 PLC的槽号，针对S7-400的PLC设置的
     *
     * @return 槽号
     */
    public byte getSlot() {
        return plc_slot;
    }

    /**
     * 设置PLC的槽号信息，针对S7-400的PLC设置的
     *
     * @param value 槽号信息
     */
    public void setSlot(byte value) {
        plc_slot = value;
        if (CurrentPlc != SiemensPLCS.S200 && CurrentPlc != SiemensPLCS.S200Smart){
            plcHead1[21] = (byte) ((this.plc_rack * 0x20) + this.plc_slot);
        }
    }

    /**
     * 获取PLC的机架号，针对S7-400的PLC设置的
     *
     * @return Plc的机架号
     */
    public byte getRack() {
        return plc_rack;
    }

    /**
     * 设置PLC的机架号，针对S7-400的PLC设置的
     *
     * @param value 机架号
     */
    public void setRack(byte value) {
        plc_rack = value;
        if (CurrentPlc != SiemensPLCS.S200 && CurrentPlc != SiemensPLCS.S200Smart) {
            plcHead1[21] = (byte) ((this.plc_rack * 0x20) + this.plc_slot);
        }
    }

    /**
     * 获取当前PLC的连接方式，PG: 0x01，OP: 0x02，S7Basic: 0x03...0x10<br />
     * Get the current PLC connection mode, PG: 0x01, OP: 0x02, S7Basic: 0x03...0x10
     * @return 连接方式
     */
    public byte getConnectionType(){
        return this.plcHead1[20];
    }

    /**
     * 设置当前PLC的连接方式，PG: 0x01，OP: 0x02，S7Basic: 0x03...0x10<br />
     * Set the current PLC connection mode, PG: 0x01, OP: 0x02, S7Basic: 0x03...0x10
     * @param value 连接方式
     */
    public void setConnectionType( byte value ) {
        if (CurrentPlc == SiemensPLCS.S200 || CurrentPlc == SiemensPLCS.S200Smart) {

        } else {
            this.plcHead1[20] = value;
        }
    }

    /**
     * 获取西门子相关的一个参数信息<br />
     * @return 本地的一个参数
     */
    public int getLocalTSAP() {
        if (CurrentPlc == SiemensPLCS.S200 || CurrentPlc == SiemensPLCS.S200Smart) {
            return (this.plcHead1[13] & 0xff) * 256 + this.plcHead1[14] & 0xff;
        }
        else {
            return (this.plcHead1[16] & 0xff) * 256 + this.plcHead1[17] & 0xff;
        }
    }

    /**
     * 设置西门子相关的一个参数信息<br />
     * @param value 本地的一个参数
     */
    public void setLocalTSAP( int value ) {
        if (CurrentPlc == SiemensPLCS.S200 || CurrentPlc == SiemensPLCS.S200Smart) {
            this.plcHead1[13] = Utilities.getBytes( value )[1];
            this.plcHead1[14] = Utilities.getBytes( value )[0];
        } else {
            this.plcHead1[16] = Utilities.getBytes(value)[1];
            this.plcHead1[17] = Utilities.getBytes(value)[0];
        }
    }

    /**
     * 获取西门子的远程TSAP参数信息
     * @return 参数信息
     */
    public int getDestTSAP() {
        if (CurrentPlc == SiemensPLCS.S200 || CurrentPlc == SiemensPLCS.S200Smart)
            return (this.plcHead1[17] & 0xff) * 256 + this.plcHead1[18] & 0xff;
        else
            return (this.plcHead1[20] & 0xff) * 256 + this.plcHead1[21] & 0xff;
    }

    /**
     * 设置西门子的远程TASP的参数信息
     * @param value 参数信息
     */
    public void setDestTSAP(int value ) {
        if (CurrentPlc == SiemensPLCS.S200 || CurrentPlc == SiemensPLCS.S200Smart) {
            this.plcHead1[17] = Utilities.getBytes(value)[1];
            this.plcHead1[18] = Utilities.getBytes(value)[0];
        } else {
            this.plcHead1[20] = Utilities.getBytes(value)[1];
            this.plcHead1[21] = Utilities.getBytes(value)[0];
        }
    }

    /**
     * 获取当前的PDU长度信息
     * @return pdu值
     */
    public int getPduLength() {
        return pdu_length;
    }

    /**
     * 在客户端连接上服务器后，所做的一些初始化操作 ->
     * Two handshake actions required after connecting to the server
     *
     * @param socket 网络套接字
     * @return 返回连接结果
     */
    @Override
    protected OperateResult InitializationOnConnect(Socket socket) {
        // 第一层通信的初始化
        OperateResultExOne<byte[]> read_first = ReadFromCoreServer(socket, plcHead1, true, true);
        if (!read_first.IsSuccess) return read_first;

        // 第二层通信的初始化
        OperateResultExOne<byte[]> read_second = ReadFromCoreServer(socket, plcHead2, true, true);
        if (!read_second.IsSuccess) return read_second;

        // 调整单次接收的pdu长度信息
        pdu_length = getByteTransform().TransUInt16( SoftBasic.BytesArraySelectLast(read_second.Content, 2 ), 0 ) - 28;
        if (pdu_length < 200) pdu_length = 200;

        incrementCount = new SoftIncrementCount(65535, 1);
        // 返回成功的信号
        return OperateResult.CreateSuccessResult();
    }

    /**
     * 从PLC读取订货号信息
     *
     * @return 订货号信息
     */
    public OperateResultExOne<String> ReadOrderNumber() {
        OperateResultExOne<String> result = new OperateResultExOne<String>();
        OperateResultExOne<byte[]> read = ReadFromCoreServer(plcOrderNumber);
        if (read.IsSuccess) {
            if (read.Content.length > 100) {
                result.IsSuccess = true;
                result.Content = Utilities.getString(Arrays.copyOfRange(read.Content, 71, 91), "ASCII");
            }
        }

        if (!result.IsSuccess) result.CopyErrorFromOther(read);
        return result;
    }


    /**
     * 从PLC读取数据，地址格式为I100，Q100，DB20.100，M100，T100，C100以字节为单位<br />
     * Read the original byte data from the PLC, the address format is I100, Q100, DB20.100, M100, length parameters in bytes
     * @param address 起始地址，格式为I100，M100，Q100，DB20.100
     * @param length  读取的数量，以字节为单位
     * @return 结果对象
     */
    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExOne<S7AddressData> addressResult = S7AddressData.ParseFrom(address, length);
        if (!addressResult.IsSuccess) return OperateResultExOne.CreateFailedResult(addressResult);

        // 如果长度超过 pdu_length，分批次读取 -> If the length is more than pdu_length, read in batches
        ArrayList<Byte> bytesContent = new ArrayList<>();
        short alreadyFinished = 0;
        while (alreadyFinished < length) {
            short readLength = (short) Math.min(length - alreadyFinished, pdu_length);
            addressResult.Content.setLength(readLength);
            OperateResultExOne<byte[]> read = Read(new S7AddressData[]{addressResult.Content});
            if (!read.IsSuccess) return read;

            Utilities.ArrayListAddArray(bytesContent, read.Content);
            alreadyFinished += readLength;
            if (addressResult.Content.getDataCode() == 0x1F || addressResult.Content.getDataCode() == 0x1E)
                addressResult.Content.setAddressStart(addressResult.Content.getAddressStart() + readLength / 2);
            else
                addressResult.Content.setAddressStart(addressResult.Content.getAddressStart() + readLength * 8);
        }

        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(bytesContent));
    }


    /**
     * 从PLC读取数据，地址格式为I100，Q100，DB20.100，M100，以位为单位<br />
     * Read the data from the PLC, the address format is I100，Q100，DB20.100，M100, in bits units
     * @param address 起始地址，格式为I100，M100，Q100，DB20.100
     * @return 结果对象
     */
    private OperateResultExOne<byte[]> ReadBitFromPLC(String address) {
        OperateResultExOne<byte[]> command = BuildBitReadCommand(address, GetMessageId( ));
        if (!command.IsSuccess) return command;

        // 核心交互 ->  Core interactive
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 分析结果 -> Analysis read result
        return AnalysisReadBit(read.Content);
    }


    /**
     * 一次性从PLC获取所有的数据，按照先后顺序返回一个统一的Buffer，需要按照顺序处理，两个数组长度必须一致
     *
     * @param address 起始地址数组
     * @param length  数据长度数组
     * @return 结果数据对象
     */
    public OperateResultExOne<byte[]> Read(String[] address, short[] length) {

        S7AddressData[] addressResult = new S7AddressData[address.length];
        for (int i = 0; i < address.length; i++) {
            OperateResultExOne<S7AddressData> tmp = S7AddressData.ParseFrom(address[i], length[i]);
            if (!tmp.IsSuccess) return OperateResultExOne.CreateFailedResult(tmp);

            addressResult[i] = tmp.Content;
        }

        return Read(addressResult);
    }

    /**
     * 读取西门子的地址数据信息，支持任意个数的数据读取<br />
     * Read Siemens address data information, support any number of data reading
     * @param s7Addresses  西门子的数据地址<br />Siemens data address</param>
     * @return 返回的结果对象信息 -> Whether to read the successful result object
     */
    public OperateResultExOne<byte[]> Read( S7AddressData[] s7Addresses )
    {
        if (s7Addresses.length > 19)
        {
            ArrayList<Byte> bytes = new ArrayList<Byte>( );
            ArrayList<S7AddressData[]> groups = SoftBasic.ArraySplitByLength( S7AddressData.class, s7Addresses, 19 );
            for (int i = 0; i < groups.size(); i++)
            {
                OperateResultExOne<byte[]> read = Read( groups.get(i) );
                if (!read.IsSuccess) return read;

                Utilities.ArrayListAddArray(bytes, read.Content);
            }
            return OperateResultExOne.CreateSuccessResult( Utilities.ToByteArray(bytes ) );
        }
        else
        {
            return ReadS7AddressData( s7Addresses );
        }
    }

    private OperateResultExOne<byte[]> ReadS7AddressData( S7AddressData[] s7Addresses ) {
        // 构建指令 -> Build read command
        OperateResultExOne<byte[]> command = BuildReadCommand(s7Addresses, GetMessageId( ));
        if (!command.IsSuccess) return command;

        // 核心交互 -> Core Interactions
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 分析结果 -> Analysis results
        return AnalysisReadByte(s7Addresses, read.Content);
    }

    /**
     * 读取指定地址的bool数据
     *
     * @param address 起始地址，格式为I100，M100，Q100，DB20.100
     * @return 结果类对象
     */
    public OperateResultExOne<Boolean> ReadBool(String address) {
        return ByteTransformHelper.GetResultFromBytes(ReadBitFromPLC(address), new FunctionOperateExOne<byte[], Boolean>() {
            public Boolean Action(byte[] content) {
                return content[0] != 0x00;
            }
        });
    }

    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        OperateResultExThree<Integer, Short, Integer> calculate = PlcHelper.CalculateStartBitIndexAndLength(analysis.Content.getAddressStart(), length);
        analysis.Content.setAddressStart(calculate.Content1);
        analysis.Content.setLength(calculate.Content2);

        OperateResultExOne<byte[]> read = Read(new S7AddressData[]{analysis.Content});
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray(read.Content), calculate.Content3, length));
    }

    /**
     * 读取指定地址的byte数据
     *
     * @param address 起始地址，格式为I100，M100，Q100，DB20.100
     * @return 结果类对象
     */
    public OperateResultExOne<Byte> ReadByte(String address) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) 1), new FunctionOperateExOne<byte[], Byte>() {
            public Byte Action(byte[] content) {
                return content[0];
            }
        });
    }

    /**
     * 基础的写入数据的操作支持
     *
     * @param entireValue 完整的字节数据
     * @return 写入结果
     */
    private OperateResult WriteBase(byte[] entireValue) {
        OperateResultExOne<byte[]> write = ReadFromCoreServer(entireValue);
        if (!write.IsSuccess) return write;

        if (write.Content.length >= 22) {
            byte code = write.Content[21];
            if (code != (byte) 0xFF)
                return new OperateResult(code, StringResources.Language.SiemensWriteError() + code + " Msg:" + SoftBasic.ByteToHexString(write.Content, ' '));
            else
                return OperateResult.CreateSuccessResult();
        } else {
            return new OperateResult(StringResources.Language.UnknownError() + " Msg:" + SoftBasic.ByteToHexString(write.Content, ' '));
        }
    }

    /**
     * 将数据写入到PLC数据，地址格式为I100，Q100，DB20.100，M100，以字节为单位
     *
     * @param address 起始地址，格式为I100，M100，Q100，DB20.100
     * @param value   写入的数据，长度根据data的长度来指示
     * @return 写入结果
     */
    @Override
    public OperateResult Write(String address, byte[] value) {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        return Write( analysis.Content, value );
    }

    private OperateResult Write( S7AddressData address, byte[] value )
    {
        int length = value.length;
        int alreadyFinished = 0;
        while (alreadyFinished < length) {
            short writeLength = (short) Math.min(length - alreadyFinished, 200);
            byte[] buffer = getByteTransform().TransByte(value, alreadyFinished, writeLength);

            OperateResultExOne<byte[]> command = BuildWriteByteCommand(address, buffer, GetMessageId( ));
            if (!command.IsSuccess) return command;

            OperateResult write = WriteBase(command.Content);
            if (!write.IsSuccess) return write;

            alreadyFinished += writeLength;
            address.setAddressStart( address.getAddressStart() + writeLength * 8);
        }

        return OperateResult.CreateSuccessResult();
    }


    /**
     * 写入PLC的一个位，例如"M100.6"，"I100.7"，"Q100.0"，"DB20.100.0"，如果只写了"M100"默认为"M100.0
     *
     * @param address 起始地址，格式为I100，M100，Q100，DB20.100
     * @param value   写入的数据，True或是False
     * @return 写入结果
     */
    public OperateResult Write(String address, boolean value) {
        // 生成指令
        OperateResultExOne<byte[]> command = BuildWriteBitCommand(address, value, GetMessageId( ));
        if (!command.IsSuccess) return command;

        return WriteBase(command.Content);
    }

    /**
     * 向PLC中写入bool数组，返回值说明，比如你写入M100,那么data[0]对应M100.0
     *
     * @param address 要写入的数据地址
     * @param values  要写入的实际数据，长度为8的倍数
     * @return 写入结果
     */
    public OperateResult Write(String address, boolean[] values) {// 先读取出来字节数据，修改位，再写入操作
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        OperateResultExThree<Integer,Short,Integer> calcu = PlcHelper.CalculateStartBitIndexAndLength( analysis.Content.getAddressStart(), (short) values.length );
        int newStart = calcu.Content1;
        short byteLength = calcu.Content2;
        int offset = calcu.Content3;
        analysis.Content.setAddressStart(newStart);
        analysis.Content.setLength(byteLength);

        OperateResultExOne<byte[]> read = Read( new S7AddressData[] { analysis.Content } );
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

        boolean[] boolArray = SoftBasic.ByteToBoolArray(read.Content);
        System.arraycopy( values, 0, boolArray, offset, values.length );

        return Write( analysis.Content, SoftBasic.BoolArrayToByte( boolArray ) );
    }

    /**
     * 向PLC中写入byte数据，返回值说明
     *
     * @param address 要写入的数据地址
     * @param value   要写入的实际数据
     * @return 写入结果
     */
    public OperateResult Write(String address, byte value) {
        return Write(address, new byte[]{value});
    }


    @Override
    public OperateResultExOne<String> ReadString(String address, short length, Charset encoding) {
        if(length == 0)
            return ReadString(address, encoding);
        else
            return super.ReadString(address, length, encoding);
    }

    /**
     * 读取西门子的地址的字符串信息，这个信息是和西门子绑定在一起，长度随西门子的信息动态变化的
     *
     * @param address 数据地址，具体的格式需要参照类的说明文档
     * @return 带有是否成功的字符串结果类对象
     */
    public OperateResultExOne<String> ReadString(String address){
        return ReadString(address, StandardCharsets.US_ASCII);
    }

    /**
     * 读取西门子的地址的字符串信息，这个信息是和西门子绑定在一起，长度随西门子的信息动态变化的
     *
     * @param address 数据地址，具体的格式需要参照类的说明文档
     * @param encoding 数据编码信息
     * @return 带有是否成功的字符串结果类对象
     */
    public OperateResultExOne<String> ReadString(String address, Charset encoding) {
        if (CurrentPlc != SiemensPLCS.S200Smart) {
            OperateResultExOne<byte[]> read = Read(address, (short) 2);
            if (!read.IsSuccess) return OperateResultExOne.<String>CreateFailedResult(read);

            if (read.Content[0] == (byte) 0 || read.Content[0] == (byte) 255 ) return new OperateResultExOne<String>("Value in plc is not string type");

            OperateResultExOne<byte[]> readString = Read(address, (short) (2 + read.Content[1]));
            if (!readString.IsSuccess) return OperateResultExOne.CreateFailedResult(readString);

            try {
                byte[] buffer = new byte[read.Content[1]];
                System.arraycopy(readString.Content, 2, buffer, 0, buffer.length);
                return OperateResultExOne.CreateSuccessResult(new String(buffer, encoding));
            } catch (Exception ex) {
                return new OperateResultExOne<>(ex.getMessage());
            }
        } else {
            OperateResultExOne<byte[]> read = Read(address, (short) 1);
            if (!read.IsSuccess) return OperateResultExOne.<String>CreateFailedResult(read);

            OperateResultExOne<byte[]> readString = Read(address, (short) (1 + read.Content[0]));
            if (!readString.IsSuccess) return OperateResultExOne.CreateFailedResult(readString);

            try {
                byte[] buffer = new byte[read.Content[0]];
                System.arraycopy(readString.Content, 1, buffer, 0, buffer.length);
                return OperateResultExOne.CreateSuccessResult(new String(buffer, encoding));
            } catch (Exception ex) {
                return new OperateResultExOne<>(ex.getMessage());
            }
        }
    }

    /**
     * 读取西门子的地址的字符串信息，这个信息是和西门子绑定在一起，长度随西门子的信息动态变化的<br />
     * Read the Siemens address string information. This information is bound to Siemens and its length changes dynamically with the Siemens information
     * @param address 数据地址，具体的格式需要参照类的说明文档
     * @return 带有是否成功的字符串结果类对象
     */
    public OperateResultExOne<String> ReadWString( String address )
    {
        if (CurrentPlc != SiemensPLCS.S200Smart)
        {
            OperateResultExOne<byte[]> read = Read( address, (short) 4 );
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

            OperateResultExOne<byte[]> readString = Read( address, (short) (4 + (read.Content[2] * 256 + read.Content[3]) * 2) );
            if (!readString.IsSuccess) return OperateResultExOne.CreateFailedResult( readString );

            byte[] buffer =  SoftBasic.BytesArrayRemoveBegin(readString.Content, 4 );
            return OperateResultExOne.CreateSuccessResult( new String(buffer, StandardCharsets.UTF_16) );
        }
        else
        {
            OperateResultExOne<byte[]> read = Read( address, (short) 1 );
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult( read );

            OperateResultExOne<byte[]> readString = Read( address, (short) (1 + read.Content[0] * 2) );
            if (!readString.IsSuccess) return OperateResultExOne.CreateFailedResult( readString );

            byte[] buffer =  SoftBasic.BytesReverseByWord( SoftBasic.BytesArrayRemoveBegin(readString.Content, 1 ) );
            return OperateResultExOne.CreateSuccessResult( new String(buffer, StandardCharsets.UTF_16) );
        }
    }


    @Override
    public OperateResult Write(String address, String value, Charset encoding) {
        byte[] temp = getByteTransform().TransByte(value, encoding);
        if (WordLength == 1) temp = SoftBasic.ArrayExpandToLengthEven(temp);

        if (CurrentPlc != SiemensPLCS.S200Smart)
        {
            // need read one time
            OperateResultExOne<byte[]> readLength = Read( address, (short) 2 );
            if (!readLength.IsSuccess) return readLength;

            if (readLength.Content[0] == (byte) 255) return new OperateResultExOne<String>( "Value in plc is not string type" );
            if (readLength.Content[0] == 0) readLength.Content[0] = (byte) 254; // allow to create new string
            if (temp.length > (readLength.Content[0] & 0xff)) return new OperateResultExOne<String>( "String length is too long than plc defined" );

            return Write( address, SoftBasic.SpliceArray( new byte[] { readLength.Content[0], (byte)temp.length }, temp ) );
        }
        else
        {
            return Write( address, SoftBasic.SpliceArray( new byte[] { (byte)temp.length }, temp ) );
        }
    }

    /**
     * 使用双字节编码的方式，将字符串以 Unicode 编码写入到PLC的地址里，可以使用中文。<br />
     * Use the double-byte encoding method to write the character string to the address of the PLC in Unicode encoding. Chinese can be used.
     * @param address 起始地址，格式为I100，M100，Q100，DB20.100 -> Starting address, formatted as I100,mM100,Q100,DB20.100
     * @param value 字符串的值
     * @return 是否写入成功的结果对象
     */
    public OperateResult WriteWString( String address, String value )
    {
        if (CurrentPlc != SiemensPLCS.S200Smart)
        {
            if (value == null) value = "";
            byte[] buffer = value.getBytes(StandardCharsets.UTF_16);
            if (buffer.length > 2 && buffer[0] == -2 && buffer[1] == -1){
                buffer = SoftBasic.BytesArrayRemoveBegin(buffer, 2);
            }

            // need read one time
            OperateResultExOne<byte[]> readLength = Read( address, (short) 4 );
            if (!readLength.IsSuccess) return readLength;

            int defineLength = readLength.Content[0] * 256 + readLength.Content[1];
            if (defineLength == 0)
            {
                defineLength = 254;    // 创建一个新的字符串
                readLength.Content[1] = (byte) 254;
            }
            if (value.length() > defineLength) return new OperateResultExOne<>( "String length is too long than plc defined" );

            byte[] write = new byte[buffer.length + 4];
            write[0] = readLength.Content[0];
            write[1] = readLength.Content[1];
            write[2] = Utilities.getBytes( value.length() )[1];
            write[3] = Utilities.getBytes( value.length() )[0];
            System.arraycopy(buffer, 0, write, 4, buffer.length);
            return Write( address, write );
        }
        else
        {
            return Write( address, value, StandardCharsets.UTF_16 );
        }
    }

    //region ReadWrite DateTime

    /**
     * 从PLC中读取时间格式的数据<br />
     * Read time format data from PLC
     * @param address 地址
     * @return 时间对象
     */
    public OperateResultExOne<Date> ReadDateTime(String address ) {
        OperateResultExOne<byte[]> read = Read(address, (short) 8);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        try {
            return OperateResultExOne.CreateSuccessResult(SiemensDateTime.FromByteArray(read.Content));
        } catch (Exception ex) {
            return new OperateResultExOne<Date>(ex.getMessage());
        }
    }

    /**
     * 从PLC中读取DTL时间格式的数据
     * @param address 地址信息
     * @return 时间对象
     */
    public OperateResultExOne<Date> ReadDTLDataTime( String address ) {
        OperateResultExOne<byte[]> read = Read(address, (short) 12);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(SiemensDateTime.GetDTLTime(this.getByteTransform(), read.Content, 0));
    }

    /**
     * 向PLC中写入时间格式的数据<br />
     * Writes data in time format to the PLC
     * @param address 地址
     * @param dateTime 时间
     * @return 是否写入成功
     */
    public OperateResult Write(String address, Date dateTime ) {
        try {
            return Write(address, SiemensDateTime.ToByteArray(dateTime));
        } catch (Exception ex) {
            return new OperateResult(ex.getMessage());
        }
    }

    /**
     * 向PLC中写入DTL格式的时间数据信息
     * @param address 写入的地址信息
     * @param dateTime 时间
     * @return 是否写入成功
     */
    public OperateResult WriteDTLTime( String address, Date dateTime ) {
        return Write( address, SiemensDateTime.GetBytesFromDTLTime( this.getByteTransform(), dateTime ) );
    }

    /**
     * 从PLC中读取日期格式的数据<br />
     * Read data in date format from PLC
     * @param address PLC的地址
     * @return 日期对象
     */
    public OperateResultExOne<Date> ReadDate(String address ) {
        OperateResultExOne<Integer> read = ReadUInt16(address);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        try {
            Calendar c = Calendar.getInstance();
            c.set(1990,Calendar.JANUARY, 1);
            c.add(Calendar.DATE, read.Content);
            return OperateResultExOne.CreateSuccessResult(c.getTime());
        } catch (Exception ex) {
            return new OperateResultExOne<Date>(ex.getMessage());
        }
    }

    /**
     * 向PLC中写入日期格式的数据，日期格式里只有年，月，日<br />
     * Write data in date format to PLC, only year, month, day in date format
     * @param address 等待写入的PLC地址
     * @param dateTime 等待写入的日期
     * @return 是否写入成功
     */
    public OperateResult WriteDate( String address, Date dateTime ) {
        Calendar c = Calendar.getInstance();
        c.set(1990, Calendar.JANUARY, 1);
        long tick = dateTime.getTime() - c.getTime().getTime();

        return Write(address, (short) (tick / (24 * 60 * 60 * 1000)));
    }

    //endregion

    private byte[] plcHead1 = new byte[]
            {
                    0x03, 0x00, 0x00, 0x16, 0x11, (byte) 0xE0, 0x00, 0x00, 0x00, 0x01, 0x00, (byte) 0xC0, 0x01,
                    0x0A, (byte) 0xC1, 0x02, 0x01, 0x02, (byte) 0xC2, 0x02, 0x01, 0x00
            };
    private byte[] plcHead2 = new byte[]
            {
                    0x03, 0x00, 0x00, 0x19, 0x02, (byte) 0xF0, (byte) 0x80, 0x32, 0x01, 0x00, 0x00, 0x04, 0x00, 0x00, 0x08, 0x00, 0x00,
                    (byte) 0xF0, 0x00, 0x00, 0x01, 0x00, 0x01, 0x01, (byte) 0xE0
            };
    private byte[] plcOrderNumber = new byte[]
            {
                    0x03, 0x00, 0x00, 0x21, 0x02, (byte) 0xF0, (byte) 0x80, 0x32, 0x07, 0x00, 0x00, 0x00, 0x01, 0x00, 0x08, 0x00, 0x08,
                    0x00, 0x01, 0x12, 0x04, 0x11, 0x44, 0x01, 0x00, (byte) 0xFF, 0x09, 0x00, 0x04, 0x00, 0x11, 0x00, 0x00
            };
    private SiemensPLCS CurrentPlc = SiemensPLCS.S1200;
    private byte[] plcHead1_200smart = new byte[]
            {
                    0x03, 0x00, 0x00, 0x16, 0x11, (byte) 0xE0, 0x00, 0x00, 0x00, 0x01, 0x00, (byte) 0xC1, 0x02, 0x10, 0x00, (byte) 0xC2,
                    0x02, 0x03, 0x00, (byte) 0xC0, 0x01, 0x0A
            };
    private byte[] plcHead2_200smart = new byte[]
            {
                    0x03, 0x00, 0x00, 0x19, 0x02, (byte) 0xF0, (byte) 0x80, 0x32, 0x01, 0x00, 0x00, (byte) 0xCC, (byte) 0xC1,
                    0x00, 0x08, 0x00, 0x00, (byte) 0xF0, 0x00, 0x00, 0x01, 0x00, 0x01, 0x03, (byte) 0xC0
            };
    private byte[] plcHead1_200 = new byte[]
            {
                    0x03, 0x00, 0x00, 0x16, 0x11, (byte) 0xE0, 0x00, 0x00, 0x00, 0x01, 0x00, (byte) 0xC1, 0x02, 0x4D, 0x57, (byte) 0xC2,
                    0x02, 0x4D, 0x57, (byte) 0xC0, 0x01, 0x09
            };
    private byte[] plcHead2_200 = new byte[]
            {
                    0x03, 0x00, 0x00, 0x19, 0x02, (byte) 0xF0, (byte) 0x80, 0x32, 0x01, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x08, 0x00, 0x00, (byte) 0xF0, 0x00, 0x00, 0x01, 0x00, 0x01, 0x03, (byte) 0xC0
            };

    private byte plc_slot = 0;
    private byte plc_rack = 0;
    private int pdu_length = 200;
    private SoftIncrementCount incrementCount = new SoftIncrementCount(65535, 1);
    private int GetMessageId() {
        return (int) incrementCount.GetCurrentValue();
    }

    /**
     * 返回表示当前对象的字符串
     *
     * @return 字符串信息
     */
    @Override
    public String toString() {
        return "SiemensS7Net";
    }


    /**
     * 计算特殊的地址信息
     *
     * @param address 字符串信息
     * @return 实际值
     */
    public static int CalculateAddressStarted(String address) {
        if (address.indexOf('.') < 0) {
            return Integer.parseInt(address) * 8;
        } else {
            String[] temp = address.split("\\.");
            return Integer.parseInt(temp[0]) * 8 + Integer.parseInt(temp[1]);
        }
    }

    /**
     * 生成一个读取字数据指令头的通用方法
     *
     * @param s7Addresses siemens address
     * @param msgId message id information
     * @return Message containing the result object
     */
    public static OperateResultExOne<byte[]> BuildReadCommand( S7AddressData[] s7Addresses, int msgId) {
        if (s7Addresses == null) throw new RuntimeException( "s7Addresses" );
        if (s7Addresses.length > 19) throw new RuntimeException( StringResources.Language.SiemensReadLengthCannotLargerThan19() );

        int readCount = s7Addresses.length;
        byte[] _PLCCommand = new byte[19 + readCount * 12];

        // ======================================================================================
        // Header
        // 报文头
        _PLCCommand[0] = 0x03;
        _PLCCommand[1] = 0x00;
        // 长度
        _PLCCommand[2] = (byte) (_PLCCommand.length / 256);
        _PLCCommand[3] = (byte) (_PLCCommand.length % 256);
        // 固定
        _PLCCommand[4] = 0x02;
        _PLCCommand[5] = (byte) 0xF0;
        _PLCCommand[6] = (byte) 0x80;
        // 协议标识
        _PLCCommand[7] = 0x32;
        // 命令：发
        _PLCCommand[8] = 0x01;
        // redundancy identification (reserved): 0x0000;
        _PLCCommand[9] = 0x00;
        _PLCCommand[10] = 0x00;
        // protocol data unit reference; it’s increased by request event;
        _PLCCommand[11] = BitConverter.GetBytes(msgId)[1];
        _PLCCommand[12] = BitConverter.GetBytes(msgId)[0];
        // 参数命令数据总长度
        _PLCCommand[13] = (byte) ((_PLCCommand.length - 17) / 256);
        _PLCCommand[14] = (byte) ((_PLCCommand.length - 17) % 256);

        // 读取内部数据时为00，读取CPU型号为Data数据长度
        _PLCCommand[15] = 0x00;
        _PLCCommand[16] = 0x00;


        // ======================================================================================
        // Parameter

        // 读写指令，04读，05写
        _PLCCommand[17] = 0x04;
        // 读取数据块个数
        _PLCCommand[18] = (byte) readCount;


        for (int ii = 0; ii < readCount; ii++) {
            //===========================================================================================
            // 指定有效值类型
            _PLCCommand[19 + ii * 12] = 0x12;
            // 接下来本次地址访问长度
            _PLCCommand[20 + ii * 12] = 0x0A;
            // 语法标记，ANY
            _PLCCommand[21 + ii * 12] = 0x10;
            // 按字为单位 -> by word
            if (s7Addresses[ii].getDataCode() == 0x1E || s7Addresses[ii].getDataCode() == 0x1F)
            {
                _PLCCommand[22 + ii * 12] = (byte) s7Addresses[ii].getDataCode();
                // 访问数据的个数 -> Number of Access data
                _PLCCommand[23 + ii * 12] = (byte)(s7Addresses[ii].getLength() / 2 / 256);
                _PLCCommand[24 + ii * 12] = (byte)(s7Addresses[ii].getLength() / 2 % 256);
            }
            else
            {
                if(s7Addresses[ii].getDataCode() == 0x06 | s7Addresses[ii].getDataCode() == 0x07)
                {
                    // 访问数据的个数 -> Number of Access data
                    _PLCCommand[22 + ii * 12] = 0x04;
                    _PLCCommand[23 + ii * 12] = (byte)(s7Addresses[ii].getLength() / 2 / 256);
                    _PLCCommand[24 + ii * 12] = (byte)(s7Addresses[ii].getLength() / 2 % 256);
                }
                else
                {
                    _PLCCommand[22 + ii * 12] = 0x02;
                    // 访问数据的个数 -> Number of Access data
                    _PLCCommand[23 + ii * 12] = (byte)(s7Addresses[ii].getLength() / 256);
                    _PLCCommand[24 + ii * 12] = (byte)(s7Addresses[ii].getLength() % 256);
                }
            }
            // DB块编号，如果访问的是DB块的话
            _PLCCommand[25 + ii * 12] = (byte) (byte)(s7Addresses[ii].getDbBlock() / 256);
            _PLCCommand[26 + ii * 12] = (byte) (byte)(s7Addresses[ii].getDbBlock() % 256);
            // 访问数据类型
            _PLCCommand[27 + ii * 12] = (byte) s7Addresses[ii].getDataCode();
            // 偏移位置
            _PLCCommand[28 + ii * 12] = (byte) (s7Addresses[ii].getAddressStart() / 256 / 256 % 256);
            _PLCCommand[29 + ii * 12] = (byte) (s7Addresses[ii].getAddressStart() / 256 % 256);
            _PLCCommand[30 + ii * 12] = (byte) (s7Addresses[ii].getAddressStart() % 256);
        }

        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    /**
     * 生成一个位读取数据指令头的通用方法
     *
     * @param address 起始地址
     * @param msgId message id information
     * @return 指令
     */
    public static OperateResultExOne<byte[]> BuildBitReadCommand(String address, int msgId) {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        byte[] _PLCCommand = new byte[31];
        // 报文头
        _PLCCommand[0] = 0x03;
        _PLCCommand[1] = 0x00;
        // 长度
        _PLCCommand[2] = (byte) (_PLCCommand.length / 256);
        _PLCCommand[3] = (byte) (_PLCCommand.length % 256);
        // 固定
        _PLCCommand[4] = 0x02;
        _PLCCommand[5] = (byte) 0xF0;
        _PLCCommand[6] = (byte) 0x80;
        _PLCCommand[7] = 0x32;
        // 命令：发
        _PLCCommand[8] = 0x01;
        // 标识序列号
        _PLCCommand[9] = 0x00;
        _PLCCommand[10] = 0x00;
        _PLCCommand[11] = BitConverter.GetBytes(msgId)[1];
        _PLCCommand[12] = BitConverter.GetBytes(msgId)[0];
        // 命令数据总长度
        _PLCCommand[13] = (byte) ((_PLCCommand.length - 17) / 256);
        _PLCCommand[14] = (byte) ((_PLCCommand.length - 17) % 256);

        _PLCCommand[15] = 0x00;
        _PLCCommand[16] = 0x00;

        // 命令起始符
        _PLCCommand[17] = 0x04;
        // 读取数据块个数
        _PLCCommand[18] = 0x01;

        //===========================================================================================
        // 读取地址的前缀
        _PLCCommand[19] = 0x12;
        _PLCCommand[20] = 0x0A;
        _PLCCommand[21] = 0x10;
        // 读取的数据时位
        _PLCCommand[22] = 0x01;
        // 访问数据的个数
        _PLCCommand[23] = 0x00;
        _PLCCommand[24] = 0x01;
        // DB块编号，如果访问的是DB块的话
        _PLCCommand[25] = (byte) (analysis.Content.getDbBlock() / 256);
        _PLCCommand[26] = (byte) (analysis.Content.getDbBlock() % 256);
        // 访问数据类型
        _PLCCommand[27] = (byte) analysis.Content.getDataCode();
        // 偏移位置
        _PLCCommand[28] = (byte) (analysis.Content.getAddressStart() / 256 / 256 % 256);
        _PLCCommand[29] = (byte) (analysis.Content.getAddressStart() / 256 % 256);
        _PLCCommand[30] = (byte) (analysis.Content.getAddressStart() % 256);

        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    /**
     * 生成一个写入字节数据的指令 -> Generate an instruction to write byte data
     *
     * @param address 起始地址，示例M100,I100,Q100,DB1.100 -> Start Address, example M100,I100,Q100,DB1.100
     * @param data    原始的字节数据 -> Raw byte data
     * @param msgId message id information
     * @return 包含结果对象的报文 -> Message containing the result object
     */
    public static OperateResultExOne<byte[]> BuildWriteByteCommand(String address, byte[] data, int msgId) {
        if (data == null) data = new byte[0];

        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        return BuildWriteByteCommand(analysis.Content, data, msgId);
    }

    /**
     * 生成一个写入字节数据的指令
     *
     * @param s7Address 西门子的地址
     * @param data     数据
     * @param msgId message id information
     * @return 指令
     */
    public static OperateResultExOne<byte[]> BuildWriteByteCommand(S7AddressData s7Address, byte[] data, int msgId) {
        if (data == null) data = new byte[0];

        byte[] _PLCCommand = new byte[35 + data.length];
        _PLCCommand[0] = 0x03;
        _PLCCommand[1] = 0x00;
        // 长度
        _PLCCommand[2] = (byte) ((35 + data.length) / 256);
        _PLCCommand[3] = (byte) ((35 + data.length) % 256);
        // 固定
        _PLCCommand[4] = 0x02;
        _PLCCommand[5] = (byte) 0xF0;
        _PLCCommand[6] = (byte) 0x80;
        _PLCCommand[7] = 0x32;
        // 命令 发
        _PLCCommand[8] = 0x01;
        // 标识序列号
        _PLCCommand[9] = 0x00;
        _PLCCommand[10] = 0x00;
        _PLCCommand[11] = BitConverter.GetBytes(msgId)[1];
        _PLCCommand[12] = BitConverter.GetBytes(msgId)[0];
        // 固定
        _PLCCommand[13] = 0x00;
        _PLCCommand[14] = 0x0E;
        // 写入长度+4
        _PLCCommand[15] = (byte) ((4 + data.length) / 256);
        _PLCCommand[16] = (byte) ((4 + data.length) % 256);
        // 读写指令
        _PLCCommand[17] = 0x05;
        // 写入数据块个数
        _PLCCommand[18] = 0x01;
        // 固定，返回数据长度
        _PLCCommand[19] = 0x12;
        _PLCCommand[20] = 0x0A;
        _PLCCommand[21] = 0x10;
        if(s7Address.getDataCode() == 0x06 || s7Address.getDataCode() == 0x07)
        {
            // 写入方式，1是按位，2是按字 -> Write mode, 1 is bitwise, 2 is by byte, 4 is by word
            _PLCCommand[22] = 0x04;
            // 写入数据的个数 -> Number of Write Data
            _PLCCommand[23] = (byte)(data.length / 2 / 256);
            _PLCCommand[24] = (byte)(data.length / 2 % 256);
        }
        else
        {
            // 写入方式，1是按位，2是按字 -> Write mode, 1 is bitwise, 2 is by word
            _PLCCommand[22] = 0x02;
            // 写入数据的个数 -> Number of Write Data
            _PLCCommand[23] = (byte)(data.length / 256);
            _PLCCommand[24] = (byte)(data.length % 256);
        }
        // DB块编号，如果访问的是DB块的话
        _PLCCommand[25] = (byte) (s7Address.getDbBlock() / 256);
        _PLCCommand[26] = (byte) (s7Address.getDbBlock() % 256);
        // 写入数据的类型
        _PLCCommand[27] = (byte) s7Address.getDataCode();
        // 偏移位置
        _PLCCommand[28] = (byte) (s7Address.getAddressStart() / 256 / 256 % 256);
        _PLCCommand[29] = (byte) (s7Address.getAddressStart() / 256 % 256);
        _PLCCommand[30] = (byte) (s7Address.getAddressStart() % 256);
        // 按字写入
        _PLCCommand[31] = 0x00;
        _PLCCommand[32] = 0x04;
        // 按位计算的长度
        _PLCCommand[33] = (byte) (data.length * 8 / 256);
        _PLCCommand[34] = (byte) (data.length * 8 % 256);

        System.arraycopy(data, 0, _PLCCommand, 35, data.length);

        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }

    /**
     * 生成一个写入位数据的指令
     *
     * @param address 起始地址
     * @param data    数据
     * @param msgId message id information
     * @return 指令
     */
    public static OperateResultExOne<byte[]> BuildWriteBitCommand(String address, boolean data, int msgId) {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );


        byte[] buffer = new byte[1];
        buffer[0] = data ? (byte) 0x01 : (byte) 0x00;

        byte[] _PLCCommand = new byte[35 + buffer.length];
        _PLCCommand[0] = 0x03;
        _PLCCommand[1] = 0x00;
        // 长度
        _PLCCommand[2] = (byte) ((35 + buffer.length) / 256);
        _PLCCommand[3] = (byte) ((35 + buffer.length) % 256);
        // 固定
        _PLCCommand[4] = 0x02;
        _PLCCommand[5] = (byte) 0xF0;
        _PLCCommand[6] = (byte) 0x80;
        _PLCCommand[7] = 0x32;
        // 命令 发
        _PLCCommand[8] = 0x01;
        // 标识序列号
        _PLCCommand[9] = 0x00;
        _PLCCommand[10] = 0x00;
        _PLCCommand[11] = BitConverter.GetBytes(msgId)[1];
        _PLCCommand[12] = BitConverter.GetBytes(msgId)[0];
        // 固定
        _PLCCommand[13] = 0x00;
        _PLCCommand[14] = 0x0E;
        // 写入长度+4
        _PLCCommand[15] = (byte) ((4 + buffer.length) / 256);
        _PLCCommand[16] = (byte) ((4 + buffer.length) % 256);
        // 命令起始符
        _PLCCommand[17] = 0x05;
        // 写入数据块个数
        _PLCCommand[18] = 0x01;
        _PLCCommand[19] = 0x12;
        _PLCCommand[20] = 0x0A;
        _PLCCommand[21] = 0x10;
        // 写入方式，1是按位，2是按字
        _PLCCommand[22] = 0x01;
        // 写入数据的个数
        _PLCCommand[23] = (byte) (buffer.length / 256);
        _PLCCommand[24] = (byte) (buffer.length % 256);
        // DB块编号，如果访问的是DB块的话
        _PLCCommand[25] = (byte) (analysis.Content.getDbBlock() / 256);
        _PLCCommand[26] = (byte) (analysis.Content.getDbBlock() % 256);
        // 写入数据的类型
        _PLCCommand[27] = (byte) analysis.Content.getDataCode();
        // 偏移位置
        _PLCCommand[28] = (byte) (analysis.Content.getAddressStart() / 256 / 256);
        _PLCCommand[29] = (byte) (analysis.Content.getAddressStart() / 256);
        _PLCCommand[30] = (byte) (analysis.Content.getAddressStart() % 256);
        // 按位写入 -> Bitwise Write
        if (analysis.Content.getDataCode() == 0x1C)
        {
            _PLCCommand[31] = 0x00;
            _PLCCommand[32] = 0x09;
        }
        else
        {
            _PLCCommand[31] = 0x00;
            _PLCCommand[32] = 0x03;
        }
        // 按位计算的长度
        _PLCCommand[33] = (byte) (buffer.length / 256);
        _PLCCommand[34] = (byte) (buffer.length % 256);

        System.arraycopy(buffer, 0, _PLCCommand, 35, buffer.length);

        return OperateResultExOne.CreateSuccessResult(_PLCCommand);
    }


    private static OperateResultExOne<byte[]> AnalysisReadByte( S7AddressData[] s7Addresses, byte[] content ) {
        // 分析结果 -> Analysis results
        int receiveCount = 0;
        for (int i = 0; i < s7Addresses.length; i++) {
            if (s7Addresses[i].getDataCode() == 0x1F || s7Addresses[i].getDataCode() == 0x1E)
                receiveCount += s7Addresses[i].getLength() * 2;
            else
                receiveCount += s7Addresses[i].getLength();
        }

        if (content.length >= 21 && content[20] == s7Addresses.length) {
            byte[] buffer = new byte[receiveCount];
            int kk = 0;
            int ll = 0;
            for (int ii = 21; ii < content.length; ii++) {
                if ((ii + 1) < content.length) {
                    if (content[ii] == (byte) 0xFF && content[ii + 1] == 0x04) {
                        System.arraycopy(content, ii + 4, buffer, ll, s7Addresses[kk].getLength());
                        ii += s7Addresses[kk].getLength() + 3;
                        ll += s7Addresses[kk].getLength();
                        kk++;
                    } else if (content[ii] == (byte) 0xFF && content[ii + 1] == 0x09) {
                        int count = content[ii + 2] * 256 + content[ii + 3];
                        if (count % 3 == 0) {
                            for (int i = 0; i < count / 3; i++) {
                                System.arraycopy(content, ii + 5 + 3 * i, buffer, ll, 2);
                                ll += 2;
                            }
                        } else {
                            for (int i = 0; i < count / 5; i++) {
                                System.arraycopy(content, ii + 7 + 5 * i, buffer, ll, 2);
                                ll += 2;
                            }
                        }
                        ii += count + 4;
                        kk++;
                    } else if (content[ii] == 0x05 &&
                            content[ii + 1] == 0x00) {
                        return new OperateResultExOne<byte[]>(content[ii], StringResources.Language.SiemensReadLengthOverPlcAssign());
                    } else if (content[ii] == 0x06 &&
                            content[ii + 1] == 0x00) {
                        return new OperateResultExOne<byte[]>(content[ii], StringResources.Language.SiemensError0006());
                    } else if (content[ii] == 0x0A &&
                            content[ii + 1] == 0x00) {
                        return new OperateResultExOne<byte[]>(content[ii], StringResources.Language.SiemensError000A());
                    }
                }
            }
            return OperateResultExOne.CreateSuccessResult(buffer);
        } else {
            return new OperateResultExOne<byte[]>(StringResources.Language.SiemensDataLengthCheckFailed() + " Msg:" + SoftBasic.ByteToHexString(content, ' '));
        }
    }

    private static OperateResultExOne<byte[]> AnalysisReadBit(byte[] content ) {
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
    }

}
