package com.iteaj.plugin.protocol.plc.core.profinet.Siemens;
import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.basic.SoftBuffer;
import com.iteaj.plugin.protocol.plc.core.address.S7AddressData;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.S7Message;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDataServerBase;
import com.iteaj.plugin.protocol.plc.core.net.StateOne.AppSession;
import com.iteaj.plugin.protocol.plc.core.transfer.ByteTransformHelper;
import com.iteaj.plugin.protocol.plc.core.transfer.ReverseBytesTransform;
import com.iteaj.plugin.protocol.plc.core.types.Array;
import com.iteaj.plugin.protocol.plc.core.types.BitConverter;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.profinet.Siemens.Helper.SiemensS7Helper;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 西门子S7协议的虚拟服务器，支持TCP协议，模拟的是1200的PLC进行通信，在客户端进行操作操作的时候，最好是选择1200的客户端对象进行通信。<br />
 * The virtual server of Siemens S7 protocol supports TCP protocol. It simulates 1200 PLC for communication. When the client is operating, it is best to select the 1200 client object for communication.
 */
public class SiemensS7Server extends NetworkDataServerBase {

		//region Constructor
    /**
     * 实例化一个S7协议的服务器，支持I，Q，M，DB1.X, DB2.X, DB3.X 数据区块的读写操作<br />
     * Instantiate a server with S7 protocol, support I, Q, M, DB1.X data block read and write operations
     */
    public SiemensS7Server( )
    {
        // 四个数据池初始化，输入寄存器，输出寄存器，中间寄存器，DB块寄存器
        inputBuffer             = new SoftBuffer( DataPoolLength );
        outputBuffer            = new SoftBuffer( DataPoolLength );
        memeryBuffer            = new SoftBuffer( DataPoolLength );
        countBuffer             = new SoftBuffer( DataPoolLength * 2 );
        timerBuffer             = new SoftBuffer( DataPoolLength * 2 );
        aiBuffer                = new SoftBuffer( DataPoolLength );
        aqBuffer                = new SoftBuffer( DataPoolLength );
        systemBuffer            = new SoftBuffer( DataPoolLength );
        this.systemBuffer.SetBytes( SoftBasic.HexStringToBytes("43 50 55 20 32 32 36 20 43 4E 20 20 20 20 20 20 30 32 30 31" ), 0 );

        WordLength              = 2;
        setByteTransform(new ReverseBytesTransform( ));


        dbBlockBuffer           = new HashMap<Integer, SoftBuffer>( );
        dbBlockBuffer.put( 1, new SoftBuffer( DataPoolLength ) );      // DB1
        dbBlockBuffer.put( 2, new SoftBuffer( DataPoolLength ) );      // DB2
        dbBlockBuffer.put( 3, new SoftBuffer( DataPoolLength ) );      // DB3
    }

		//endregion

		//region NetworkDataServerBase Override

    /**
     * 根据S7格式的地址，获取当前的数据缓存类对象
     * @param s7Address S7格式的数据地址信息
     * @return 内存缓存对象
     */
    private OperateResultExOne<SoftBuffer> GetDataAreaFromS7Address(S7AddressData s7Address )
    {
        switch (s7Address.getDataCode())
        {
            case 0x03: return OperateResultExOne.CreateSuccessResult( systemBuffer );
            case 0x81: return OperateResultExOne.CreateSuccessResult( inputBuffer );
            case 0x82: return OperateResultExOne.CreateSuccessResult( outputBuffer );
            case 0x83: return OperateResultExOne.CreateSuccessResult( memeryBuffer );
            case 0x84:
                if (dbBlockBuffer.containsKey( s7Address.getDbBlock() )) return OperateResultExOne.CreateSuccessResult( dbBlockBuffer.get(s7Address.getDbBlock()) );
                else return new OperateResultExOne<SoftBuffer>( 0x0a, StringResources.Language.SiemensError000A() );
            case 0x1E: return OperateResultExOne.CreateSuccessResult( countBuffer );
            case 0x1F: return OperateResultExOne.CreateSuccessResult( timerBuffer );
            case 0x06: return OperateResultExOne.CreateSuccessResult( aiBuffer );
            case 0x07: return OperateResultExOne.CreateSuccessResult( aqBuffer );
            default: return new OperateResultExOne<SoftBuffer>( 0x06, StringResources.Language.SiemensError0006() );
        }
    }

    public OperateResultExOne<byte[]> Read( String address, short length )
    {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom( address, length );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        OperateResultExOne<SoftBuffer> buffer = GetDataAreaFromS7Address( analysis.Content );
        if(!buffer.IsSuccess) return OperateResultExOne.CreateFailedResult( buffer );

        if (analysis.Content.getDataCode() == 0x1E || analysis.Content.getDataCode() == 0x1F)
            return OperateResultExOne.CreateSuccessResult( buffer.Content.GetBytes( analysis.Content.getAddressStart() * 2, length * 2 ) );
        else
            return OperateResultExOne.CreateSuccessResult( buffer.Content.GetBytes( analysis.Content.getAddressStart() / 8, length ) );
    }

    public OperateResult Write(String address, byte[] value )
    {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        OperateResultExOne<SoftBuffer> buffer = GetDataAreaFromS7Address( analysis.Content );
        if (!buffer.IsSuccess) return OperateResultExOne.CreateFailedResult( buffer );

        if (analysis.Content.getDataCode() == 0x1E || analysis.Content.getDataCode() == 0x1F)
            buffer.Content.SetBytes( value, analysis.Content.getAddressStart() * 2 );
        else
            buffer.Content.SetBytes( value, analysis.Content.getAddressStart() / 8 );
        return OperateResultExOne.CreateSuccessResult( );
    }

		//endregion

		//region Byte Read Write Operate

    public OperateResultExOne<Byte> ReadByte( String address ) {
        return ByteTransformHelper.GetByteResultFromBytes( Read( address, (short) 1 ), this.getByteTransform() );
    }

    public OperateResult Write(String address, byte value ) {
        return Write( address, new byte[] { value } );
    }

		// endregion

		// region Bool Read Write Operate

    public OperateResultExOne<Boolean> ReadBool(String address )
    {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom( address );
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult( analysis );

        OperateResultExOne<SoftBuffer> buffer = GetDataAreaFromS7Address( analysis.Content );
        if (!buffer.IsSuccess) return OperateResultExOne.CreateFailedResult( buffer );

        return OperateResultExOne.CreateSuccessResult( buffer.Content.GetBool( analysis.Content.getAddressStart() ) );
    }

    /// <inheritdoc cref="SiemensS7Net.Write(string, bool)"/>
    public OperateResult Write(String address, boolean value )
    {
        OperateResultExOne<S7AddressData> analysis = S7AddressData.ParseFrom( address );
        if (!analysis.IsSuccess) return analysis;

        OperateResultExOne<SoftBuffer> buffer = GetDataAreaFromS7Address( analysis.Content );
        if (!buffer.IsSuccess) return buffer;

        buffer.Content.SetBool( value, analysis.Content.getAddressStart() );
        return OperateResult.CreateSuccessResult( );
    }

		// endregion

		// region ReadWrite String

    public OperateResult Write( String address, String value, Charset encoding ) {
        return SiemensS7Helper.Write( this, SiemensPLCS.S1200, address, value, encoding );
    }

    public OperateResult WriteWString( String address, String value )  {
        return SiemensS7Helper.WriteWString( this, SiemensPLCS.S1200, address, value );
    }

    public OperateResultExOne<String> ReadString( String address, short length, Charset encoding ) {
        return  (length == 0) ? ReadString( address, encoding ) : super.ReadString( address, length, encoding );
    }

    public OperateResultExOne<String> ReadString( String address ) {
        return ReadString( address, StandardCharsets.US_ASCII );
    }

    public OperateResultExOne<String> ReadString( String address, Charset encoding ) {
        return SiemensS7Helper.ReadString( this, SiemensPLCS.S1200, address, encoding );
    }

    /**
     * 读取西门子的地址的字符串信息，这个信息是和西门子绑定在一起，长度随西门子的信息动态变化的<br />
     * Read the Siemens address string information. This information is bound to Siemens and its length changes dynamically with the Siemens information
     * @param address 数据地址，具体的格式需要参照类的说明文档
     * @return 带有是否成功的字符串结果类对象
     */
    public OperateResultExOne<String> ReadWString( String address ) {
        return SiemensS7Helper.ReadWString( this, SiemensPLCS.S1200, address );
    }

		//endregion

		//region NetServer Override

    protected INetMessage GetNewNetMessage( ) {
        return new S7Message( );
    }


    protected void ThreadPoolLogin(Socket socket, InetAddress endPoint) {
        if (IsNeedShakeHands( ))
        {
            // 接收2次的握手协议
            OperateResultExOne<byte[]> read1 = ReceiveByMessage( socket, 5000, new S7Message( ) );
            if (!read1.IsSuccess) return;

            // 接收到错误数据的时候，可能会引发奔溃
            if (read1.Content == null || read1.Content.length < 10)
            {
                CloseSocket(socket);
                return;
            }

            read1.Content[5] = (byte) 0xD0;
            read1.Content[6] = read1.Content[8];
            read1.Content[7] = read1.Content[9];
            read1.Content[8] = 0x00;
            read1.Content[9] = 0x0c;

            OperateResult send1 = Send( socket, read1.Content);
            if (!send1.IsSuccess) return;

            OperateResultExOne<byte[]> read2 = ReceiveByMessage( socket, 5000, new S7Message( ) );
            if (!read2.IsSuccess) return;

            OperateResult send2 = Send( socket, SoftBasic.HexStringToBytes( "03 00 00 1B 02 f0 80 32 03 00 00 00 00 00 08 00 00 00 00 f0 01 00 01 00 f0 00 f0" ) );
            if (!send2.IsSuccess) return;
        }

        // 开始接收数据信息
        super.ThreadPoolLogin( socket, endPoint );
    }

    /**
     * 获取是否需要进行握手报文信息
     * @return 是否需要进行握手操作
     */
    protected boolean IsNeedShakeHands() {
        return true;
    }

    protected OperateResultExOne<byte[]> ReadFromCoreServer(AppSession session, byte[] receive ) {
        byte[] back = null;
        try {
            if (receive[17] == 0x04) back = ReadByMessage(receive);     // 读取数据
            else if (receive[17] == 0x05) back = WriteByMessage(receive);    // 写入数据
            else if (receive[17] == 0x00) back = ReadPlcType();               // 读取PLC型号信息
            else {
                return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedFunction());
            }

            Utilities.ByteArrayCopyTo(SoftBasic.BytesArraySelectMiddle(receive, 11, 2), back, 11); // 复制消息ID到返回的报文里
            return OperateResultExOne.CreateSuccessResult(back);
        } catch (Exception ex) {
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    private byte[] ReadPlcType( )
    {
        return SoftBasic.HexStringToBytes( "03 00 00 7D 02 F0 80 32 07 00 00 00 01 00 0C 00 60 00 01 12 08 12 84 01 01 00 00 00 00 FF" +
                " 09 00 5C 00 11 00 00 00 1C 00 03 00 01 36 45 53 37 20 32 31 35 2D 31 41 47 34 30 2D 30 58 42 30 20 00 00 00 06 20 20 00 06 36 45 53 37 20" +
                " 32 31 35 2D 31 41 47 34 30 2D 30 58 42 30 20 00 00 00 06 20 20 00 07 36 45 53 37 20 32 31 35 2D 31 41 47 34 30 2D 30 58 42 30 20 00 00 56 04 02 01" );
    }

    /**
     * 将读取的结果数据内容进行打包，返回客户端读取
     * @param command 接收的命令信息
     * @param content 读取的原始字节信息
     * @return 返回客户端的原始报文信息
     */
    protected byte[] PackReadBack( byte[] command, ArrayList<Byte> content ) {
        byte[] back = new byte[21 + content.size()];
        Utilities.ByteArrayCopyTo(SoftBasic.HexStringToBytes("03 00 00 1A 02 F0 80 32 03 00 00 00 01 00 02 00 05 00 00 04 01"), back, 0);
        back[2] = (byte) (back.length / 256);
        back[3] = (byte) (back.length % 256);
        back[15] = (byte) (content.size() / 256);
        back[16] = (byte) (content.size() % 256);
        back[20] = command[18];
        Utilities.ByteArrayCopyTo(Utilities.getBytes(content), back, 21);
        return back;
    }

    private byte[] ReadByMessage( byte[] packCommand ) throws Exception {
        ArrayList<Byte> content = new ArrayList<Byte>();
        int count = packCommand[18] & 0xff;
        int index = 19;
        for (int i = 0; i < count; i++) {
            int length = packCommand[index + 1] & 0xff;
            byte[] command = SoftBasic.BytesArraySelectMiddle(packCommand, index, length + 2);
            index += length + 2;

            byte[] read = ReadByCommand(command);
            Utilities.ArrayListAddArray(content, read);
        }

        return PackReadBack(packCommand, content);
    }


    private byte[] ReadByCommand(byte[] command ) throws Exception {
        if(command[3] == 0x01)
        {
            // 位读取
            int startIndex = (command[9] & 0xff) * 65536 + (command[10] & 0xff) * 256 + (command[11] & 0xff);
            int dbBlock = getByteTransform().TransUInt16( command, 6 );
            int length  = getByteTransform().TransUInt16( command, 4 );

            OperateResultExOne<SoftBuffer> buffer = GetDataAreaFromS7Address( new S7AddressData(  startIndex,  command[8] & 0xff,  dbBlock,  1 ) );
            if (!buffer.IsSuccess) throw new Exception( buffer.Message );

            return PackReadBitCommandBack( buffer.Content.GetBool( startIndex ) );
        }
        else if (command[3] == 0x1E || command[3] == 0x1F)
        {
            // 定时器，计数器读取
            int length = getByteTransform().TransUInt16( command, 4 );
            int startIndex =  (command[9] & 0xff) * 65536 + (command[10] & 0xff) * 256 + (command[11] & 0xff);

            OperateResultExOne<SoftBuffer> buffer = GetDataAreaFromS7Address( new S7AddressData( startIndex, command[8] & 0xff, 0,  length ) );
            if (!buffer.IsSuccess) throw new Exception( buffer.Message );

            return PackReadCTCommandBack( buffer.Content.GetBytes( startIndex * 2, length * 2 ), command[3] == 0x1E ? 0x03 : 0x05 );
        }
        else
        {
            // 字节读取
            int length = getByteTransform().TransUInt16( command, 4 );
            if (command[3] == 0x04) length *= 2;
            int dbBlock = getByteTransform().TransUInt16( command, 6 );
            int startIndex = ((command[9] & 0xff) * 65536 + (command[10] & 0xff) * 256 + (command[11] & 0xff)) / 8;

            OperateResultExOne<SoftBuffer> buffer = GetDataAreaFromS7Address( new S7AddressData( startIndex, command[8] & 0xff, dbBlock,  length ) );
            if (!buffer.IsSuccess) return PackReadWordCommandBack( (short)buffer.ErrorCode, null );

            return PackReadWordCommandBack( (short) 0, buffer.Content.GetBytes( startIndex, length ) );
        }
    }

    private byte[] PackReadWordCommandBack( short err, byte[] result )
    {
        if (err > 0)
        {
            byte[] back = new byte[4];
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes( err ), back, 0);
            return back;
        }
        else
        {
            byte[] back = new byte[4 + result.length];
            back[0] = (byte) 0xFF;
            back[1] = 0x04;

            Utilities.ByteArrayCopyTo(getByteTransform().TransByte( (short) (result.length * 8) ),back, 2 );
            Utilities.ByteArrayCopyTo(result, back, 4);
            return back;
        }
    }

    private byte[] PackReadCTCommandBack( byte[] result, int dataLength ) {
        byte[] back = new byte[4 + result.length * dataLength / 2];
        back[0] = (byte) 0xFF;
        back[1] = 0x09;

        Utilities.ByteArrayCopyTo(getByteTransform().TransByte((short) (back.length - 4)), back, 2);
        for (int i = 0; i < result.length / 2; i++) {
            Utilities.ByteArrayCopyTo(SoftBasic.BytesArraySelectMiddle(result, i * 2, 2), back, 4 + dataLength - 2 + i * dataLength);
        }
        return back;
    }

    private byte[] PackReadBitCommandBack( boolean value ) {
        byte[] back = new byte[5];
        back[0] = (byte) 0xFF;
        back[1] = 0x03;
        back[2] = 0x00;
        back[3] = 0x01;
        back[4] = (byte) (value ? 0x01 : 0x00);
        return back;
    }

    /**
     * 创建返回的报文信息
     * @param packCommand 接收到的报文命令
     * @param status 返回的状态信息
     * @return 返回的原始字节数组
     */
    protected byte[] PackWriteBack( byte[] packCommand, byte status ) {
        return PackWriteBack( packCommand, new byte[] { status } );
    }

    private byte[] PackWriteBack( byte[] packCommand, byte[] status )
    {
        byte[] buffer = new byte[21 + status.length];
        Utilities.ByteArrayCopyTo(SoftBasic.HexStringToBytes( "03 00 00 16 02 F0 80 32 03 00 00 00 01 00 02 00 01 00 00 05 01") , buffer, 0 );
        buffer[20] = (byte)status.length;
        Utilities.ByteArrayCopyTo(status, buffer, 21);
        buffer[2] = BitConverter.GetBytes( buffer.length )[1];
        buffer[3] = BitConverter.GetBytes( buffer.length )[0];
        return buffer;
    }

    private byte[] WriteByMessage( byte[] packCommand ) {
        // 00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31 32 33 34 35 36 37 38 39 40 41 42 43 44 45 46 47 48 49 50 51 52 53 54 55 56
        // 03 00 00 49 02 F0 80 32 01 00 00 00 01 00 26 00 12 05 03 12 0A 10 02 00 01 00 00 83 00 03 20 12 0A 10 02 00 01 00 00 83 00 06 40 12 0A 10 02 00 01 00 01 84 00 00 00 00 04 00 10 00 01 00 04 00 10 00 02 00 04 00 10 00 03
        // 先判断是否有写入的权利，没有的话，直接返回写入异常
        if (!EnableWrite) return PackWriteBack(packCommand, (byte) 0x04);
        int itemCount = packCommand[18] & 0xff;
        byte[] result = new byte[itemCount];
        int dataIndex = 19 + itemCount * 12;
        for (int i = 0; i < itemCount; i++) {
            if (packCommand[22 + 12 * i] == 0x02 || packCommand[22 + 12 * i] == 0x04) {
                // 字写入
                int dbBlock = getByteTransform().TransUInt16(packCommand, 25 + 12 * i);
                int count = getByteTransform().TransInt16(packCommand, 23 + 12 * i);
                if (packCommand[22 + 12 * i] == 0x04) count *= 2;

                int startIndex = 0;
                if ((packCommand[27 + 12 * i] & 0xff) >= 0x1C && (packCommand[27 + 12 * i] & 0xff) <= 0x1F)
                    startIndex = ((packCommand[28 + 12 * i] & 0xff) * 65536 + (packCommand[29 + 12 * i] & 0xff) * 256 + (packCommand[30 + 12 * i] & 0xff)) * 2;
                else
                    startIndex = ((packCommand[28 + 12 * i] & 0xff) * 65536 + (packCommand[29 + 12 * i] & 0xff) * 256 + (packCommand[30 + 12 * i] & 0xff)) / 8;


                byte[] data = getByteTransform().TransByte(packCommand, dataIndex + 4, count);
                dataIndex += 4 + count;
                if (i < itemCount - 1 && (count % 2 == 1)) dataIndex += 1;

                OperateResultExOne<SoftBuffer> buffer = GetDataAreaFromS7Address(new S7AddressData(packCommand[27 + 12 * i] & 0xff, dbBlock, 1));
                if (!buffer.IsSuccess) {
                    result[i] = (byte) buffer.ErrorCode;
                    continue;
                }

                buffer.Content.SetBytes(data, startIndex);
                result[i] = (byte) 0xff;   // 无错误
            } else {
                // 位写入
                int dbBlock = getByteTransform().TransUInt16(packCommand, 25 + 12 * i);
                int startIndex = (packCommand[28 + 12 * i] & 0xff) * 65536 + (packCommand[29 + 12 * i] & 0xff) * 256 + (packCommand[30 + 12 * i] & 0xff);
                boolean value = packCommand[dataIndex + 4] != 0x00;
                dataIndex += 4 + 1;
                if (i < itemCount - 1) dataIndex += 1;

                OperateResultExOne<SoftBuffer> buffer = GetDataAreaFromS7Address(new S7AddressData(packCommand[27 + 12 * i] & 0xff, dbBlock, 1));
                if (!buffer.IsSuccess) {
                    result[i] = (byte) buffer.ErrorCode;
                    continue;
                }

                buffer.Content.SetBool(value, startIndex);
                result[i] = (byte) 0xff;   // 无错误
            }
        }

        return PackWriteBack(packCommand, result);
    }

		//endregion

		//region DB Block

    /**
     * 新增一个独立的DB块数据区，如果这个DB块已经存在，则新增无效。<br />
     * Add a separate DB block data area, if the DB block already exists, the new one is invalid.
     * @param db DB块ID信息
     */
    public void AddDbBlock( int db ) {
        if (dbBlockBuffer.get(db) == null)
            dbBlockBuffer.put(db, new SoftBuffer(DataPoolLength));
    }

    /**
     * 移除指定的DB块数据区，如果这个DB块不存在的话，操作无效，本方法无法移除1，2，3的DB块<br />
     * Remove the specified DB block data area, if the DB block does not exist, the operation is invalid, and this method cannot remove the DB block of 1, 2, 3
     * @param db 指定的ID信息
     */
    public void RemoveDbBlock( int db ) {
        if (db == 1 || db == 2 || db == 3) return;
        if (dbBlockBuffer.get(db) != null)
            dbBlockBuffer.remove(db);
    }

		//endregion

		//region Data Save Load Override

    protected void LoadFromBytes( byte[] content )
    {
        if (content.length < DataPoolLength * 7) return;

        inputBuffer.SetBytes(          content, DataPoolLength * 0, 0, DataPoolLength );
        outputBuffer.SetBytes(         content, DataPoolLength * 1, 0, DataPoolLength );
        memeryBuffer.SetBytes(         content, DataPoolLength * 2, 0, DataPoolLength );
        dbBlockBuffer.get(1).SetBytes(     content, DataPoolLength * 3, 0, DataPoolLength );
        dbBlockBuffer.get(2).SetBytes(     content, DataPoolLength * 4, 0, DataPoolLength );
        dbBlockBuffer.get(3).SetBytes(     content, DataPoolLength * 5, 0, DataPoolLength );

        if (content.length < DataPoolLength * 11) return;
        countBuffer.SetBytes(          content, DataPoolLength * 7, 0, DataPoolLength * 2 );
        timerBuffer.SetBytes(          content, DataPoolLength * 9, 0, DataPoolLength * 2 );
    }

    protected byte[] SaveToBytes( )
    {
        byte[] buffer = new byte[DataPoolLength * 11];
        Array.Copy( inputBuffer.GetBytes( ),               0, buffer, DataPoolLength * 0, DataPoolLength );
        Array.Copy( outputBuffer.GetBytes( ),              0, buffer, DataPoolLength * 1, DataPoolLength );
        Array.Copy( memeryBuffer.GetBytes( ),              0, buffer, DataPoolLength * 2, DataPoolLength );
        Array.Copy( dbBlockBuffer.get(1).GetBytes( ),      0, buffer, DataPoolLength * 3, DataPoolLength);
        Array.Copy( dbBlockBuffer.get(2).GetBytes( ),      0, buffer, DataPoolLength * 4, DataPoolLength );
        Array.Copy( dbBlockBuffer.get(3).GetBytes( ),      0, buffer, DataPoolLength * 5, DataPoolLength );
        Array.Copy( countBuffer.GetBytes( ),               0, buffer, DataPoolLength * 7, DataPoolLength * 2 );
        Array.Copy( timerBuffer.GetBytes( ),               0, buffer, DataPoolLength * 9, DataPoolLength * 2 );

        return buffer;
    }

		//endregion

		//region IDisposable Support
//
//    /// <inheritdoc/>
//    protected override void Dispose( bool disposing )
//    {
//        if (disposing)
//        {
//            systemBuffer?.Dispose( );
//            inputBuffer?.Dispose( );
//            outputBuffer?.Dispose( );
//            memeryBuffer?.Dispose( );
//            countBuffer?.Dispose( );
//            timerBuffer?.Dispose( );
//
//            foreach (var item in dbBlockBuffer.Values)
//            {
//                item?.Dispose( );
//            }
//            aiBuffer?.Dispose( );
//            aqBuffer?.Dispose( );
//        }
//        base.Dispose( disposing );
//    }

		//endregion

		//region Private Member

    private SoftBuffer systemBuffer;                   // 系统的数据池
    private SoftBuffer inputBuffer;                    // 输入数据池
    private SoftBuffer outputBuffer;                   // 输出数据池
    private SoftBuffer memeryBuffer;                   // 内部数据池
    private SoftBuffer countBuffer;                    // 计数器的数据池
    private SoftBuffer timerBuffer;                    // 定时器的数据池
    private SoftBuffer aiBuffer;                       // AI缓存数据池
    private SoftBuffer aqBuffer;                       // AQ缓存数据池
    private Map<Integer, SoftBuffer> dbBlockBuffer; // DB块的数据信息
    private final int DataPoolLength = 65536;          // 数据的长度

		//endregion

		// region Object Override

    public String toString( ) {
        return "SiemensS7Server[" + getPort() + "]";
    }
		// endregion
}
