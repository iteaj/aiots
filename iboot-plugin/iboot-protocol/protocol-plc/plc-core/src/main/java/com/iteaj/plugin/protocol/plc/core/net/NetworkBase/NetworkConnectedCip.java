package com.iteaj.plugin.protocol.plc.core.net.NetworkBase;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.basic.SoftIncrementCount;
import com.iteaj.plugin.protocol.plc.core.message.AllenBradleyMessage;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExThree;
import com.iteaj.plugin.protocol.plc.core.profinet.AllenBradley.AllenBradleyDF1Serial;
import com.iteaj.plugin.protocol.plc.core.profinet.AllenBradley.AllenBradleyHelper;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;

/**
 * 基于连接的CIP协议的基类
 */
public class NetworkConnectedCip extends NetworkDeviceBase{

    @Override
    protected INetMessage GetNewNetMessage() {
        return new AllenBradleyMessage( );
    }

    @Override
    protected byte[] PackCommandWithHeader(byte[] command) {
        return AllenBradleyHelper.PackRequestHeader( 0x70, SessionHandle, AllenBradleyHelper.PackCommandSpecificData(
                GetOTConnectionIdService( ), command ) );
    }



    // region Double Mode Override
    protected OperateResult InitializationOnConnect(Socket socket )
    {
        // Registering Session Information
        OperateResultExOne<byte[]> read1 = ReadFromCoreServer(socket, AllenBradleyHelper.RegisterSessionHandle( Utilities.getBytes( context++ ) ),  true,  false );
        if (!read1.IsSuccess) return read1;

        // Check the returned status
        OperateResult check = AllenBradleyHelper.CheckResponse( read1.Content );
        if (!check.IsSuccess) return check;

        // Extract session ID
        int sessionHandle = Utilities.getInt( read1.Content, 4 );

        // Open forward 5 times
        for (int i = 0; i < 5; i++)
        {
            long id = openForwardId++;
            // Large Forward Open(Message Router)
            OperateResultExOne<byte[]> read2 = ReadFromCoreServer( socket, AllenBradleyHelper.PackRequestHeader( 0x6f, sessionHandle, GetLargeForwardOpen( (short)connectID.GetCurrentValue( ) ),
                    Utilities.getBytes( id ) ), true,  false );
            if (!read2.IsSuccess) return read2;

            try
            {
                if (read2.Content.length >= 46 && read2.Content[42] != 0x00)
                {
                    if (i >= 4)
                    {
                        if (Utilities.getShort( read2.Content, 44 ) == 0x100) return new OperateResult( "Connection in use or duplicate Forward Open" );
                        return new OperateResult( "Forward Open failed, Code: " + Utilities.getShort( read2.Content, 44 ) );
                    }
                    else
                    {
                        connectID.ResetCurrentValue( new Random().nextInt( 1000 ) + 3 );
                    }
                }
                else
                {
                    // Extract Connection ID
                    OTConnectionId = Utilities.getInt( read2.Content, 44 );
                    break;
                }
            }
            catch (Exception ex)
            {
                return new OperateResult( ex.getMessage() + "\r\nSource: " + SoftBasic.ByteToHexString(read2.Content,' ' ) );
            }
        }
        // Reset Message Id
        incrementCount.ResetCurrentValue( );
        SessionHandle = sessionHandle;

        return OperateResult.CreateSuccessResult( );
    }

    /// <inheritdoc/>
    protected OperateResult ExtraOnDisconnect( Socket socket )
    {
        if (socket == null) return OperateResult.CreateSuccessResult( );

        // Forward Close(Message Router)
        byte[] forwardClose = GetLargeForwardClose( );
        if (forwardClose != null)
        {
            OperateResultExOne<byte[]> close = ReadFromCoreServer( socket, AllenBradleyHelper.PackRequestHeader( 0x6f, SessionHandle, forwardClose ),  true,  false );
            if (!close.IsSuccess) return close;
        }

        // Unregister session Information
        OperateResultExOne<byte[]> read = ReadFromCoreServer( socket, AllenBradleyHelper.UnRegisterSessionHandle( SessionHandle ),  true,  false );
        if (!read.IsSuccess) return read;

        return OperateResult.CreateSuccessResult( );
    }

    // endregion

    // region Public Properties

    /**
     * 会话ID信息
     */
    public int SessionHandle = 0;

    /**
     * O -> T Network Connection ID
     */
    public int OTConnectionId = 0;

    /**
     * T -> O Network Connection ID
     */
    public int TOConnectionId = 0;

    // endregion

    // region Protect Method

    /**
     * 将多个的CIP命令打包成一个服务的命令
     * @param cip CIP命令列表
     * @return 服务命令
     */
    protected byte[] PackCommandService( byte[]... cip )
    {
        ByteArrayOutputStream ms = new ByteArrayOutputStream();
        // type id   0xB2:UnConnected Data Item  0xB1:Connected Data Item  0xA1:Connect Address Item
        ms.write( 0xB1 );
        ms.write( 0x00 );
        ms.write( 0x00 );     // 后续数据的长度
        ms.write( 0x00 );

        long messageId = incrementCount.GetCurrentValue( );
        ms.write( Utilities.getBytes( messageId )[0] );     // CIP Sequence Count 一个累加的CIP序号
        ms.write( Utilities.getBytes( messageId )[1] );

        if (cip.length == 1)
        {
            ms.write( cip[0], 0, cip[0].length );
        }
        else
        {
            ms.write( new byte[] { 0x0A, 0x02, 0x20, 0x02, 0x24, 0x01 }, 0, 6 );
            ms.write( Utilities.getBytes( cip.length )[0] );
            ms.write( Utilities.getBytes( cip.length )[1] );
            int offset = 2 + cip.length * 2;
            for (int i = 0; i < cip.length; i++)
            {
                ms.write( Utilities.getBytes( offset )[0] );     // 各个数据的长度
                ms.write( Utilities.getBytes( offset )[1] );
                offset += cip[i].length;
            }
            for (int i = 0; i < cip.length; i++)
            {
                ms.write( cip[i], 0, cip[i].length );     // 写入欧姆龙CIP的具体内容
            }
        }

        byte[] data = ms.toByteArray();
        Utilities.ByteArrayCopyTo(Utilities.getBytes( (short)(data.length - 4)), data, 2 );
        return data;
    }

    /**
     * 获取数据通信的前置打开命令，不同的PLC的信息不一样。
     * @param connectionID 连接的ID信息
     * @return 原始命令数据
     */
    protected byte[] GetLargeForwardOpen( short connectionID )
    {
        return SoftBasic.HexStringToBytes("00 00 00 00 00 00 02 00 00 00 00 00 b2 00 34 00 5b 02 20 06 24 01 0e 9c 02 00 00 80 01 00 fe 80 02 00 1b 05 30 a7 2b 03 02 00 00 00 80 84 1e 00" +
        "cc 07 00 42 80 84 1e 00 cc 07 00 42 a3 03 20 02 24 01 2c 01");
    }

    /**
     * 获取数据通信的后置关闭命令，不同的PLC的信息不一样。
     * @return 原始命令数据
     */
    protected byte[] GetLargeForwardClose( )
    {
        return null;
    }

    // endregion

    // region Private Method

    private byte[] GetOTConnectionIdService( )
    {
        byte[] buffer = new byte[8];
        buffer[0] = (byte) 0xA1;  // Connected Address Item
        buffer[1] = 0x00;
        buffer[2] = 0x04;  // Length
        buffer[3] = 0x00;
        Utilities.ByteArrayCopyTo(Utilities.getBytes( OTConnectionId ), buffer, 4 );
        return buffer;
    }

    // endregion

    // region Private

    private SoftIncrementCount incrementCount = new SoftIncrementCount( 65535, 3, 2 );
    private SoftIncrementCount connectID      = new SoftIncrementCount( 50000, 2 );
    private long openForwardId = 0x100;
    private long context = 0;

    // endregion

    // region Static Helper

    /**
     * 从PLC反馈的数据解析出真实的数据内容，结果内容分别是原始字节数据，数据类型代码，是否有很多的数据<br />
     * The real data content is parsed from the data fed back by the PLC. The result content is the original byte data, the data type code, and whether there is a lot of data.
     * @param response PLC的反馈数据
     * @param isRead 是否是返回的操作
     * @return 带有结果标识的最终数据
     */
    public static OperateResultExThree<byte[], Short, Boolean> ExtractActualData(byte[] response, boolean isRead )
    {
        ArrayList<Byte> data = new ArrayList<Byte>( );

        int offset = 42;
        boolean hasMoreData = false;
        Short dataType = 0;
        int count = Utilities.getUShort( response, offset );    // 剩余总字节长度，在剩余的字节里，有可能是一项数据，也有可能是多项
        if (Utilities.getInt( response, 46 ) == 0x8A)
        {
            // 多项数据
            offset = 50;
            int dataCount = Utilities.getShort( response, offset );
            for (int i = 0; i < dataCount; i++)
            {
                int offsetStart = Utilities.getShort( response, offset + 2 + i * 2 ) + offset;
                int offsetEnd = (i == dataCount - 1) ? response.length : (Utilities.getShort( response, (offset + 4 + i * 2) ) + offset);
                short err = Utilities.getShort( response, offsetStart + 2 );
                switch (err)
                {
                    case 0x04: return new OperateResultExThree<byte[], Short, Boolean>( err,  StringResources.Language.AllenBradley04());
                    case 0x05: return new OperateResultExThree<byte[], Short, Boolean>( err,  StringResources.Language.AllenBradley05());
                    case 0x06:
                    {
                        // 06的错误码通常是数据长度太多了
                        // CC是符号返回，D2是符号片段返回， D5是列表数据
                        if (response[offset + 2] == (byte) 0xD2 || response[offset + 2] == (byte) 0xCC)
                            return new OperateResultExThree<byte[], Short, Boolean>( err,StringResources.Language.AllenBradley06() );
                        break;
                    }
                    case 0x0A: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley0A() );
                    case 0x13: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley13() );
                    case 0x1C: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley1C() );
                    case 0x1E: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley1E() );
                    case 0x26: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley26() );
                    case 0x00: break;
                    default: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.UnknownError() );
                }

                if (isRead)
                {
                    for (int j = offsetStart + 6; j < offsetEnd; j++)
                    {
                        data.add( response[j] );
                    }
                }
            }
        }
        else
        {
            byte err = response[offset + 6];
            switch (err)
            {
                case 0x04: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley04() );
                case 0x05: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley05() );
                case 0x06: hasMoreData = true; break;
                case 0x0A: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley0A() );
                case 0x13: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley13() );
                case 0x1C: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley1C() );
                case 0x1E: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley1E() );
                case 0x26: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.AllenBradley26() );
                case 0x00: break;
                default: return new OperateResultExThree<byte[], Short, Boolean>( err, StringResources.Language.UnknownError() );
            }

            if (response[offset + 4] == (byte) 0xCD || response[offset + 4] == (byte)0xD3) return OperateResultExThree.CreateSuccessResult( Utilities.getBytes(data ), dataType, hasMoreData );

            if (response[offset + 4] == (byte)0xCC || response[offset + 4] == (byte)0xD2)
            {
                for (int i = offset + 10; i < offset + 2 + count; i++)
                {
                    data.add( response[i] );
                }
                dataType = Utilities.getShort( response, offset + 8 );
            }
            else if (response[offset + 4] == (byte) 0xD5)
            {
                for (int i = offset + 8; i < offset + 2 + count; i++)
                {
                    data.add( response[i] );
                }
            }
            else if (response[offset + 4] == (byte) 0xCB)
            {
                // PCCC的格式返回
                if (response[58] != 0x00) return new OperateResultExThree<byte[], Short, Boolean>( response[58], AllenBradleyDF1Serial.GetExtStatusDescription( response[58] ) + "\r\n" +
                        "Source: " + SoftBasic.ByteToHexString(SoftBasic.BytesArrayRemoveBegin(response, 57 ), ' ' ) );
                if (!isRead) return OperateResultExThree.CreateSuccessResult( Utilities.getBytes(data ), dataType, hasMoreData );
                return OperateResultExThree.CreateSuccessResult( SoftBasic.BytesArrayRemoveBegin(response, 61 ), dataType, hasMoreData );
            }
        }

        return OperateResultExThree.CreateSuccessResult( Utilities.getBytes(data ), dataType, hasMoreData );
    }

    // endregion
}
