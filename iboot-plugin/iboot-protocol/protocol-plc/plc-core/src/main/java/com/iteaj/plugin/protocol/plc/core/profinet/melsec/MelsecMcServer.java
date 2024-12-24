package com.iteaj.plugin.protocol.plc.core.profinet.melsec;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.basic.SoftBuffer;
import com.iteaj.plugin.protocol.plc.core.address.McAddressData;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.MelsecQnA3EAsciiMessage;
import com.iteaj.plugin.protocol.plc.core.message.MelsecQnA3EBinaryMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDataServerBase;
import com.iteaj.plugin.protocol.plc.core.net.StateOne.AppSession;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.profinet.melsec.helper.McBinaryHelper;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.nio.charset.StandardCharsets;

/**
 * 三菱MC协议的虚拟服务器，支持M,X,Y,D,W的数据池读写操作，支持二进制及ASCII格式进行读写操作，需要在实例化的时候指定。<br />
 * The Mitsubishi MC protocol virtual server supports M, X, Y, D, W data pool read and write operations,
 * and supports binary and ASCII format read and write operations, which need to be specified during instantiation.
 */
public class MelsecMcServer extends NetworkDataServerBase {
        //region Constructor
    /**
     * 实例化一个默认参数的mc协议的服务器<br />
     * Instantiate a mc protocol server with default parameters
     * @param isBinary 是否是二进制，默认是二进制，否则是ASCII格式
     */
    public MelsecMcServer( boolean isBinary ) {
        // 共计使用了五个数据池
        xBuffer = new SoftBuffer(DataPoolLength);
        yBuffer = new SoftBuffer(DataPoolLength);
        mBuffer = new SoftBuffer(DataPoolLength);
        lBuffer = new SoftBuffer(DataPoolLength);
        dBuffer = new SoftBuffer(DataPoolLength * 4);
        wBuffer = new SoftBuffer(DataPoolLength * 2);
        bBuffer = new SoftBuffer(DataPoolLength);
        sBuffer = new SoftBuffer(DataPoolLength);
        fBuffer = new SoftBuffer(DataPoolLength);
        rBuffer = new SoftBuffer(DataPoolLength * 2);
        zrBuffer = new SoftBuffer(DataPoolLength * 4);

        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.isBinary = isBinary;
        this.LogMsgFormatBinary = isBinary;
    }

        //endregion

        //region NetworkDataServerBase Override

    public OperateResultExOne<byte[]> Read(String address, short length ) {
        // 分析地址
        OperateResultExOne<McAddressData> analysis = McAddressData.ParseMelsecFrom(address, length, false);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.M.getDataCode()) {
            boolean[] buffer = Array.GetBoolArrayFromBytes(mBuffer.GetBytes(analysis.Content.getAddressStart(), length * 16), 0x01);
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArrayToByte(buffer));
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.X.getDataCode()) {
            boolean[] buffer = Array.GetBoolArrayFromBytes(xBuffer.GetBytes(analysis.Content.getAddressStart(), length * 16), 0x01);
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArrayToByte(buffer));
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.Y.getDataCode()) {
            boolean[] buffer = Array.GetBoolArrayFromBytes(yBuffer.GetBytes(analysis.Content.getAddressStart(), length * 16), 0x01);
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArrayToByte(buffer));
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.L.getDataCode()) {
            boolean[] buffer = Array.GetBoolArrayFromBytes(lBuffer.GetBytes(analysis.Content.getAddressStart(), length * 16), 0x01);
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArrayToByte(buffer));
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.B.getDataCode()) {
            boolean[] buffer = Array.GetBoolArrayFromBytes(bBuffer.GetBytes(analysis.Content.getAddressStart(), length * 16), 0x01);
            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArrayToByte(buffer));
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.D.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(dBuffer.GetBytes(analysis.Content.getAddressStart() * 2, length * 2));
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.W.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(wBuffer.GetBytes(analysis.Content.getAddressStart() * 2, length * 2));
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.R.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(rBuffer.GetBytes(analysis.Content.getAddressStart() * 2, length * 2));
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.ZR.getDataCode()) {
            return OperateResultExOne.CreateSuccessResult(zrBuffer.GetBytes(analysis.Content.getAddressStart() * 2, length * 2));
        } else {
            return new OperateResultExOne(StringResources.Language.NotSupportedDataType());
        }
    }

    public OperateResult Write(String address, byte[] value ) {
        // 分析地址
        OperateResultExOne<McAddressData> analysis = McAddressData.ParseMelsecFrom(address, 0, false);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.M.getDataCode()) {
            byte[] buffer = Array.GetByteFromBoolArray(SoftBasic.ByteToBoolArray(value));
            mBuffer.SetBytes(buffer, analysis.Content.getAddressStart());
            return OperateResult.CreateSuccessResult();
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.X.getDataCode()) {
            byte[] buffer = Array.GetByteFromBoolArray(SoftBasic.ByteToBoolArray(value));
            xBuffer.SetBytes(buffer, analysis.Content.getAddressStart());
            return OperateResult.CreateSuccessResult();
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.Y.getDataCode()) {
            byte[] buffer = Array.GetByteFromBoolArray(SoftBasic.ByteToBoolArray(value));
            yBuffer.SetBytes(buffer, analysis.Content.getAddressStart());
            return OperateResult.CreateSuccessResult();
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.L.getDataCode()) {
            byte[] buffer = Array.GetByteFromBoolArray(SoftBasic.ByteToBoolArray(value));
            lBuffer.SetBytes(buffer, analysis.Content.getAddressStart());
            return OperateResult.CreateSuccessResult();
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.B.getDataCode()) {
            byte[] buffer = Array.GetByteFromBoolArray(SoftBasic.ByteToBoolArray(value));
            bBuffer.SetBytes(buffer, analysis.Content.getAddressStart());
            return OperateResult.CreateSuccessResult();
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.D.getDataCode()) {
            dBuffer.SetBytes(value, analysis.Content.getAddressStart() * 2);
            return OperateResult.CreateSuccessResult();
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.W.getDataCode()) {
            wBuffer.SetBytes(value, analysis.Content.getAddressStart() * 2);
            return OperateResult.CreateSuccessResult();
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.R.getDataCode()) {
            rBuffer.SetBytes(value, analysis.Content.getAddressStart() * 2);
            return OperateResult.CreateSuccessResult();
        } else if (analysis.Content.getMcDataType().getDataCode() == MelsecMcDataType.ZR.getDataCode()) {
            zrBuffer.SetBytes(value, analysis.Content.getAddressStart() * 2);
            return OperateResult.CreateSuccessResult();
        } else {
            return new OperateResult(StringResources.Language.NotSupportedDataType());
        }
    }
    private OperateResultExOne<SoftBuffer> GetBoolSoftBufferByDataCode( short dataCode ) {
        if (dataCode == MelsecMcDataType.M.getDataCode()) return OperateResultExOne.CreateSuccessResult(mBuffer);
        else if (dataCode == MelsecMcDataType.X.getDataCode()) return OperateResultExOne.CreateSuccessResult(xBuffer);
        else if (dataCode == MelsecMcDataType.Y.getDataCode()) return OperateResultExOne.CreateSuccessResult(yBuffer);
        else if (dataCode == MelsecMcDataType.L.getDataCode()) return OperateResultExOne.CreateSuccessResult(lBuffer);
        else if (dataCode == MelsecMcDataType.B.getDataCode()) return OperateResultExOne.CreateSuccessResult(bBuffer);
        else if (dataCode == MelsecMcDataType.S.getDataCode()) return OperateResultExOne.CreateSuccessResult(sBuffer);
        else if (dataCode == MelsecMcDataType.F.getDataCode()) return OperateResultExOne.CreateSuccessResult(fBuffer);
        else return new OperateResultExOne<SoftBuffer>(StringResources.Language.NotSupportedDataType());
    }
    private OperateResultExOne<SoftBuffer> GetWordSoftBufferByDataCode( short dataCode ) {
        if (dataCode == MelsecMcDataType.D.getDataCode()) return OperateResultExOne.CreateSuccessResult(dBuffer);
        else if (dataCode == MelsecMcDataType.W.getDataCode()) return OperateResultExOne.CreateSuccessResult(wBuffer);
        else if (dataCode == MelsecMcDataType.R.getDataCode()) return OperateResultExOne.CreateSuccessResult(rBuffer);
        else if (dataCode == MelsecMcDataType.ZR.getDataCode()) return OperateResultExOne.CreateSuccessResult(zrBuffer);
        else return new OperateResultExOne<SoftBuffer>(StringResources.Language.NotSupportedDataType());
    }

        //endregion

        //region Bool Read Write Operate

    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {

        if (address.indexOf(".") > 0) return PlcHelper.ReadBool(this, address, length);

        // 分析地址
        OperateResultExOne<McAddressData> analysis = McAddressData.ParseMelsecFrom(address, 0, true);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        if (analysis.Content.getMcDataType().getDataType() == 0)
            return new OperateResultExOne<boolean[]>(StringResources.Language.MelsecCurrentTypeNotSupportedWordOperate());

        OperateResultExOne<SoftBuffer> bufferResult = GetBoolSoftBufferByDataCode(analysis.Content.getMcDataType().getDataCode());
        if (!bufferResult.IsSuccess) return OperateResultExOne.CreateFailedResult(bufferResult);

        return OperateResultExOne.CreateSuccessResult(Array.GetBoolArrayFromBytes(bufferResult.Content.GetBytes(analysis.Content.getAddressStart(), length), 0x01));
    }

    public OperateResult Write( String address, boolean[] value ) {
        int bitIndex = -1;
        if (address.indexOf(".") > 0) {
            bitIndex = PlcHelper.CalculateBitStartIndex(address.substring(address.indexOf('.') + 1));
            address = address.substring(0, address.indexOf('.'));
        }

        // 分析地址
        OperateResultExOne<McAddressData> analysis = McAddressData.ParseMelsecFrom(address, 0, true);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        if (bitIndex >= 0) {
            if (analysis.Content.getMcDataType().getDataType() == 1)
                return new OperateResultExOne<boolean[]>(StringResources.Language.MelsecCurrentTypeNotSupportedBitOperate());

            OperateResultExOne<SoftBuffer> bufferResult = GetWordSoftBufferByDataCode(analysis.Content.getMcDataType().getDataCode());
            if (!bufferResult.IsSuccess) return OperateResultExOne.CreateFailedResult(bufferResult);

            bufferResult.Content.SetBool(value, analysis.Content.getAddressStart() * 16 + bitIndex);
            return OperateResult.CreateSuccessResult();
        } else {
            if (analysis.Content.getMcDataType().getDataType() == 0)
                return new OperateResultExOne<boolean[]>(StringResources.Language.MelsecCurrentTypeNotSupportedWordOperate());

            OperateResultExOne<SoftBuffer> bufferResult = GetBoolSoftBufferByDataCode(analysis.Content.getMcDataType().getDataCode());
            if (!bufferResult.IsSuccess) return OperateResultExOne.CreateFailedResult(bufferResult);

            bufferResult.Content.SetBytes(Array.GetByteFromBoolArray(value), analysis.Content.getAddressStart());
            return OperateResult.CreateSuccessResult();
        }
    }

        //endregion

        //region NetServer Override

    protected INetMessage GetNewNetMessage( ) {
        if (isBinary)
            return new MelsecQnA3EBinaryMessage();
        else
            return new MelsecQnA3EAsciiMessage();
    }

    protected OperateResultExOne<byte[]> ReadFromCoreServer(AppSession session, byte[] receive ) {
        if (isBinary)
            return OperateResultExOne.CreateSuccessResult(ReadFromMcCore(SoftBasic.BytesArrayRemoveBegin(receive,11)));
        else
            return OperateResultExOne.CreateSuccessResult(ReadFromMcAsciiCore(SoftBasic.BytesArrayRemoveBegin(receive,22)));
    }

    /**
     * 当收到mc协议的报文的时候应该触发的方法，允许继承重写，来实现自定义的返回，或是数据监听。<br />
     * The method that should be triggered when a message of the mc protocol is received,
     * allowing inheritance to be rewritten to implement custom return or data monitoring.
     * @param mcCore mc报文
     * @return 返回的报文信息
     */
    protected byte[] ReadFromMcCore( byte[] mcCore ) {
        if (mcCore[0] == 0x01 && mcCore[1] == 0x04) {
            return ReadByCommand(mcCore); // 读数据
        } else if (mcCore[0] == 0x03 && mcCore[1] == 0x04) {
            return ReadRandomByCommand(mcCore);
        } else if (mcCore[0] == 0x06 && mcCore[1] == 0x04) {
            return ReadBlockByCommand(mcCore);
        } else if (mcCore[0] == 0x01 && mcCore[1] == 0x14) {
            // 先判断是否有写入的权利，没有的话，直接返回写入异常
            if (!EnableWrite) return PackCommand((short) 0xC062, null);
            try {
                return PackCommand((short) 0, WriteByMessage(mcCore)); // 写数据
            } catch (Exception ex) {
                return PackCommand((short) 0xC062, null);
            }
        } else return null;
    }

    /**
     * 当收到mc协议的报文的时候应该触发的方法，允许继承重写，来实现自定义的返回，或是数据监听。<br />
     * The method that should be triggered when a message of the mc protocol is received,
     * allowing inheritance to be rewritten to implement custom return or data monitoring.
     * @param mcCore mc报文
     * @return 返回的报文信息
     */
    protected byte[] ReadFromMcAsciiCore( byte[] mcCore ) {
        if (mcCore[0] == 0x30 && mcCore[1] == 0x34 && mcCore[2] == 0x30 && mcCore[3] == 0x31)
            return ReadAsciiByCommand(mcCore);
        else if (mcCore[0] == 0x31 && mcCore[1] == 0x34 && mcCore[2] == 0x30 && mcCore[3] == 0x31) {
            // 先判断是否有写入的权利，没有的话，直接返回写入异常
            if (!EnableWrite) return PackCommand((short) 0xC062, null);
            try {
                return PackCommand((short) 0, WriteAsciiByMessage(mcCore));
            }catch (Exception ex){
                return PackCommand((short) 0xC062, null);
            }
        } else
            return null;
    }

    /**
     * 将状态码，数据打包成一个完成的回复报文信息
     * @param status 状态信息
     * @param data 数据
     * @return 状态信息
     */
    protected byte[] PackCommand( short status, byte[] data ) {
        if (data == null) data = new byte[0];
        if (isBinary) {
            byte[] back = new byte[11 + data.length];
            Utilities.ByteArrayCopyTo(SoftBasic.HexStringToBytes("D0 00 00 FF FF 03 00 00 00 00 00"), back, 0);
            if (data.length > 0) Utilities.ByteArrayCopyTo(data, back, 11);

            Utilities.ByteArrayCopyTo(BitConverter.GetBytes((short) (data.length + 2)), back, 7);
            Utilities.ByteArrayCopyTo(BitConverter.GetBytes(status), back, 9);
            return back;
        } else {
            byte[] back = new byte[22 + data.length];
            Utilities.ByteArrayCopyTo(Encoding.ASCII.GetBytes("D00000FF03FF0000000000"), back, 0);
            if (data.length > 0) Utilities.ByteArrayCopyTo(data, back, 22);

            Utilities.ByteArrayCopyTo(MelsecHelper.BuildBytesFromData(data.length + 4), back, 14);
            Utilities.ByteArrayCopyTo(MelsecHelper.BuildBytesFromData(status), back, 18);
            return back;
        }
    }

    private byte[] ReadByCommand( byte[] command )
    {
        int length = getByteTransform().TransUInt16( command, 8 );
        int startIndex = ((command[6] & 0xff) * 65536 + (command[5]&0xff) * 256 + (command[4]&0xff));

        if (command[2] == 0x01)
        {
            // 二进制位读取
            if (length > 7168) return PackCommand( (short)0xC051, null );
            short dataCode = (short) (command[7] & 0xff);
            if (     dataCode == MelsecMcDataType.M.getDataCode()) return PackCommand( (short)0, MelsecHelper.TransBoolArrayToByteData( mBuffer.GetBytes( startIndex, length ) ) );
            else if (dataCode == MelsecMcDataType.X.getDataCode()) return PackCommand( (short)0, MelsecHelper.TransBoolArrayToByteData( xBuffer.GetBytes( startIndex, length ) ) );
            else if (dataCode == MelsecMcDataType.Y.getDataCode()) return PackCommand( (short)0, MelsecHelper.TransBoolArrayToByteData( yBuffer.GetBytes( startIndex, length ) ) );
            else if (dataCode == MelsecMcDataType.B.getDataCode()) return PackCommand( (short)0, MelsecHelper.TransBoolArrayToByteData( bBuffer.GetBytes( startIndex, length ) ) );
            else if (dataCode == MelsecMcDataType.L.getDataCode()) return PackCommand( (short)0, MelsecHelper.TransBoolArrayToByteData( lBuffer.GetBytes( startIndex, length ) ) );
            else if (dataCode == MelsecMcDataType.S.getDataCode()) return PackCommand( (short)0, MelsecHelper.TransBoolArrayToByteData( sBuffer.GetBytes( startIndex, length ) ) );
            else if (dataCode == MelsecMcDataType.F.getDataCode()) return PackCommand( (short)0, MelsecHelper.TransBoolArrayToByteData( fBuffer.GetBytes( startIndex, length ) ) );
            else return PackCommand( (short)0xC05A, null );
            }
        else {
            // 字读取
            short dataCode = (short) (command[7] & 0xff);
            if (length > 960) return PackCommand( (short)0xC051, null );
            if (dataCode == MelsecMcDataType.M.getDataCode())       return PackCommand( (short)0, SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(mBuffer.GetBytes(  startIndex, length * 16 ), 0x01 ) ) );
            else if (dataCode == MelsecMcDataType.X.getDataCode())  return PackCommand( (short)0, SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(xBuffer.GetBytes(  startIndex, length * 16 ), 0x01 ) ) );
            else if (dataCode == MelsecMcDataType.Y.getDataCode())  return PackCommand( (short)0, SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(yBuffer.GetBytes(  startIndex, length * 16 ), 0x01 ) ) );
            else if (dataCode == MelsecMcDataType.B.getDataCode())  return PackCommand( (short)0, SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(bBuffer.GetBytes(  startIndex, length * 16 ), 0x01 ) ) );
            else if (dataCode == MelsecMcDataType.L.getDataCode())  return PackCommand( (short)0, SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(lBuffer.GetBytes(  startIndex, length * 16 ), 0x01 ) ) );
            else if (dataCode == MelsecMcDataType.S.getDataCode())  return PackCommand( (short)0, SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(sBuffer.GetBytes(  startIndex, length * 16 ), 0x01 ) ) );
            else if (dataCode == MelsecMcDataType.F.getDataCode())  return PackCommand( (short)0, SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(fBuffer.GetBytes(  startIndex, length * 16 ), 0x01 ) ) );
            else if (dataCode == MelsecMcDataType.D.getDataCode())  return PackCommand( (short)0, dBuffer.GetBytes(  startIndex * 2, length * 2 ) );
            else if (dataCode == MelsecMcDataType.W.getDataCode())  return PackCommand( (short)0, wBuffer.GetBytes(  startIndex * 2, length * 2 ) );
            else if (dataCode == MelsecMcDataType.R.getDataCode())  return PackCommand( (short)0, rBuffer.GetBytes(  startIndex * 2, length * 2 ) );
            else if (dataCode == MelsecMcDataType.ZR.getDataCode()) return PackCommand( (short)0, zrBuffer.GetBytes( startIndex * 2, length * 2 ) );
            else return PackCommand( (short) 0xC05A, null );
        }
    }

    private byte[] ReadRandomByCommand( byte[] command )
    {
            int count = command[4] & 0xff;
            byte[] buffer = new byte[count * 2];

            for (int i = 0; i < count; i++)
            {
                short dataCode = (short) (command[9 + 4 * i] & 0xff);
                int startIndex = (command[8 + 4 * i]&0xff) * 65536 + (command[7 + 4 * i]&0xff) * 256 + (command[6 + 4 * i]&0xff);
                if      (dataCode == MelsecMcDataType.M.getDataCode())  Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(mBuffer.GetBytes( startIndex, 16 ), 0x01 ) ), buffer, i * 2 );
                else if (dataCode == MelsecMcDataType.X.getDataCode())  Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(xBuffer.GetBytes( startIndex, 16 ), 0x01 ) ), buffer, i * 2 );
                else if (dataCode == MelsecMcDataType.Y.getDataCode())  Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(yBuffer.GetBytes( startIndex, 16 ), 0x01 ) ), buffer, i * 2 );
                else if (dataCode == MelsecMcDataType.B.getDataCode())  Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(bBuffer.GetBytes( startIndex, 16 ), 0x01 ) ), buffer, i * 2 );
                else if (dataCode == MelsecMcDataType.L.getDataCode())  Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(lBuffer.GetBytes( startIndex, 16 ), 0x01 ) ), buffer, i * 2 );
                else if (dataCode == MelsecMcDataType.S.getDataCode())  Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(sBuffer.GetBytes( startIndex, 16 ), 0x01 ) ), buffer, i * 2 );
                else if (dataCode == MelsecMcDataType.F.getDataCode())  Utilities.ByteArrayCopyTo(SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(fBuffer.GetBytes( startIndex, 16 ), 0x01 ) ), buffer, i * 2 );
                else if (dataCode == MelsecMcDataType.D.getDataCode())  Utilities.ByteArrayCopyTo(dBuffer.GetBytes( startIndex * 2, 2 ), buffer, i * 2 );
                else if (dataCode == MelsecMcDataType.W.getDataCode())  Utilities.ByteArrayCopyTo(wBuffer.GetBytes( startIndex * 2, 2 ), buffer, i * 2 );
                else if (dataCode == MelsecMcDataType.R.getDataCode())  Utilities.ByteArrayCopyTo(rBuffer.GetBytes( startIndex * 2, 2 ), buffer, i * 2 );
                else if (dataCode == MelsecMcDataType.ZR.getDataCode()) Utilities.ByteArrayCopyTo(zrBuffer.GetBytes( startIndex * 2, 2 ), buffer, i * 2 );
            }
             return PackCommand( (short) 0, buffer );
    }

    private byte[] ReadBlockByCommand( byte[] command )
    {
        int count = command[4] & 0xff;
        MemoryStream ms = new MemoryStream( );

        for (int i = 0; i < count; i++)
        {
            short dataCode = (short) (command[9 + 6 * i] & 0xff);
            int startIndex = (command[8 + 6 * i]&0xff) * 65536 + (command[7 + 6 * i]&0xff) * 256 + (command[6 + 6 * i]&0xff);
            int length = getByteTransform().TransUInt16( command, 10 + 6 * i );

            if      (dataCode == MelsecMcDataType.M.getDataCode()) ms.Write( SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(mBuffer.GetBytes( startIndex, length * 16 ), 0x01 ) ) );
            else if (dataCode == MelsecMcDataType.X.getDataCode()) ms.Write( SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(xBuffer.GetBytes( startIndex, length * 16 ), 0x01 ) ) );
            else if (dataCode == MelsecMcDataType.Y.getDataCode()) ms.Write( SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(yBuffer.GetBytes( startIndex, length * 16 ), 0x01 ) ) );
            else if (dataCode == MelsecMcDataType.B.getDataCode()) ms.Write( SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(bBuffer.GetBytes( startIndex, length * 16 ), 0x01 ) ) );
            else if (dataCode == MelsecMcDataType.L.getDataCode()) ms.Write( SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(lBuffer.GetBytes( startIndex, length * 16 ), 0x01 ) ) );
            else if (dataCode == MelsecMcDataType.S.getDataCode()) ms.Write( SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(sBuffer.GetBytes( startIndex, length * 16 ), 0x01 ) ) );
            else if (dataCode == MelsecMcDataType.F.getDataCode()) ms.Write( SoftBasic.BoolArrayToByte(Array.GetBoolArrayFromBytes(fBuffer.GetBytes( startIndex, length * 16 ), 0x01 ) ) );
            else if (dataCode == MelsecMcDataType.D.getDataCode()) ms.Write( dBuffer.GetBytes( startIndex * 2, length * 2 ) );
            else if (dataCode == MelsecMcDataType.W.getDataCode()) ms.Write( wBuffer.GetBytes( startIndex * 2, length * 2 ) );
            else if (dataCode == MelsecMcDataType.R.getDataCode()) ms.Write( rBuffer.GetBytes( startIndex * 2, length * 2 ) );
            else if (dataCode == MelsecMcDataType.ZR.getDataCode()) ms.Write( zrBuffer.GetBytes( startIndex * 2, length * 2 ) );
        }
        return PackCommand( (short) 0, ms.ToArray( ) );
    }

    private byte[] ReadAsciiPackCommand( SoftBuffer softBuffer, int startIndex, int length, boolean isBool ) {
        if (isBool) {
            boolean[] buffer = Array.GetBoolArrayFromBytes(softBuffer.GetBytes(startIndex, length * 16), 0x01);
            return PackCommand((short) 0, MelsecHelper.TransByteArrayToAsciiByteArray(SoftBasic.BoolArrayToByte(buffer)));
        } else {
            return PackCommand((short) 0, MelsecHelper.TransByteArrayToAsciiByteArray(softBuffer.GetBytes(startIndex * 2, length * 2)));
        }
    }

    private byte[] GetBoolAsciiFromSoftBuffer( SoftBuffer softBuffer, int startIndex, int length ) {
        byte[] buffer= softBuffer.GetBytes(startIndex, length);
        for (int i = 0; i < buffer.length; i ++){
            if(buffer[i] ==0x01)
                buffer[i] = 0x31;
            else
                buffer[i] = 0x30;
        }
        return buffer;
    }

    private byte[] ReadAsciiByCommand( byte[] command )
    {
        int length = Convert.ToInt32( Encoding.ASCII.GetString( command, 16, 4 ), 16 );
        String typeCode = Encoding.ASCII.GetString( command, 8, 2 );
        int startIndex = 0;
        if(typeCode.equals(MelsecMcDataType.X.getAsciiCode()) ||
                typeCode.equals(MelsecMcDataType.Y.getAsciiCode()) ||
                typeCode.equals(MelsecMcDataType.W.getAsciiCode()) ||
                typeCode.equals(MelsecMcDataType.B.getAsciiCode()) ||
                typeCode.equals(MelsecMcDataType.L.getAsciiCode()))
            startIndex = Convert.ToInt32( Encoding.ASCII.GetString( command, 10, 6 ), 16 );
        else
            startIndex = Convert.ToInt32( Encoding.ASCII.GetString( command, 10, 6 ) );

        if (command[7] == 0x31)
        {
            if (length > 3584) return PackCommand( (short)0xC051, null );
            // 位读取
            if      (typeCode.equals(MelsecMcDataType.M.getAsciiCode()))      return PackCommand( (short)0, GetBoolAsciiFromSoftBuffer(mBuffer, startIndex, length ) );
            else if (typeCode.equals(MelsecMcDataType.X.getAsciiCode())) return PackCommand( (short)0, GetBoolAsciiFromSoftBuffer(xBuffer, startIndex, length ) );
            else if (typeCode.equals(MelsecMcDataType.Y.getAsciiCode())) return PackCommand( (short)0, GetBoolAsciiFromSoftBuffer(yBuffer, startIndex, length ) );
            else if (typeCode.equals(MelsecMcDataType.B.getAsciiCode())) return PackCommand( (short)0, GetBoolAsciiFromSoftBuffer(bBuffer, startIndex, length ) );
            else if (typeCode.equals(MelsecMcDataType.L.getAsciiCode())) return PackCommand( (short)0, GetBoolAsciiFromSoftBuffer(lBuffer, startIndex, length ) );
            else if (typeCode.equals(MelsecMcDataType.S.getAsciiCode())) return PackCommand( (short)0, GetBoolAsciiFromSoftBuffer(sBuffer, startIndex, length ) );
            else if (typeCode.equals(MelsecMcDataType.F.getAsciiCode())) return PackCommand( (short)0, GetBoolAsciiFromSoftBuffer(fBuffer, startIndex, length ) );
            else return PackCommand( (short)0xC05A, null );
        }
        else
        {
            // 字读取
            if (length > 960) return PackCommand( (short) 0xC051, null );
            if (typeCode.equals(MelsecMcDataType.M.getAsciiCode()))      return ReadAsciiPackCommand( mBuffer, startIndex, length, true );
            else if (typeCode.equals(MelsecMcDataType.X.getAsciiCode())) return ReadAsciiPackCommand( xBuffer, startIndex, length, true );
            else if (typeCode.equals(MelsecMcDataType.Y.getAsciiCode())) return ReadAsciiPackCommand( yBuffer, startIndex, length, true );
            else if (typeCode.equals(MelsecMcDataType.B.getAsciiCode())) return ReadAsciiPackCommand( bBuffer, startIndex, length, true );
            else if (typeCode.equals(MelsecMcDataType.L.getAsciiCode())) return ReadAsciiPackCommand( lBuffer, startIndex, length, true );
            else if (typeCode.equals(MelsecMcDataType.D.getAsciiCode())) return ReadAsciiPackCommand( dBuffer, startIndex, length, false );
            else if (typeCode.equals(MelsecMcDataType.W.getAsciiCode())) return ReadAsciiPackCommand( wBuffer, startIndex, length, false );
            else if (typeCode.equals(MelsecMcDataType.R.getAsciiCode())) return ReadAsciiPackCommand( rBuffer, startIndex, length, false );
            else if (typeCode.equals(MelsecMcDataType.ZR.getAsciiCode())) return ReadAsciiPackCommand( zrBuffer, startIndex, length, false );
            else return PackCommand( (short) 0xC05A, null );
        }
    }

    private  byte[] GetByteFromBinaryData( byte[] command ) {
        boolean[] data = SoftBasic.ByteToBoolArray( SoftBasic.BytesArrayRemoveBegin( command, 10 ) );
        return Array.GetByteFromBoolArray(data);
    }
    private byte[] WriteByMessage( byte[] command ) throws Exception
    {
        // 先判断是否有写入的权利，没有的话，直接返回写入异常
        if (!this.EnableWrite) return null;

        int length = getByteTransform().TransUInt16( command, 8 );
        int startIndex = (command[6]&0xff) * 65536 + (command[5]&0xff) * 256 + (command[4]&0xff);
        if (command[2] == 0x01)
        {
            // 位写入
            byte[] buffer = McBinaryHelper.ExtractActualDataHelper( SoftBasic.BytesArrayRemoveBegin(command, 10 ), true );
            short dataCode = (short)(command[7]&0xff);

            if (     dataCode == MelsecMcDataType.M.getDataCode()) mBuffer.SetBytes( SoftBasic.BytesArraySelectBegin(buffer, length ), startIndex );
            else if (dataCode == MelsecMcDataType.X.getDataCode()) xBuffer.SetBytes( SoftBasic.BytesArraySelectBegin(buffer, length ), startIndex );
            else if (dataCode == MelsecMcDataType.Y.getDataCode()) yBuffer.SetBytes( SoftBasic.BytesArraySelectBegin(buffer, length ), startIndex );
            else if (dataCode == MelsecMcDataType.B.getDataCode()) bBuffer.SetBytes( SoftBasic.BytesArraySelectBegin(buffer, length ), startIndex );
            else if (dataCode == MelsecMcDataType.L.getDataCode()) lBuffer.SetBytes( SoftBasic.BytesArraySelectBegin(buffer, length ), startIndex );
            else if (dataCode == MelsecMcDataType.S.getDataCode()) sBuffer.SetBytes( SoftBasic.BytesArraySelectBegin(buffer, length ), startIndex );
            else if (dataCode == MelsecMcDataType.F.getDataCode()) fBuffer.SetBytes( SoftBasic.BytesArraySelectBegin(buffer, length ), startIndex );
            else throw new Exception( StringResources.Language.NotSupportedDataType() );

            return new byte[0];
        }
        else
        {
            short dataCode = (short)(command[7]&0xff);
            // 字写入
            if (dataCode == MelsecMcDataType.M.getDataCode())
            {
                byte[] buffer = GetByteFromBinaryData( command );
                mBuffer.SetBytes( buffer, startIndex );
                return new byte[0];
            }
            else if (dataCode == MelsecMcDataType.X.getDataCode())
            {
                byte[] buffer = GetByteFromBinaryData( command );
                xBuffer.SetBytes( buffer, startIndex );
                return new byte[0];
            }
            else if (dataCode == MelsecMcDataType.Y.getDataCode())
            {
                byte[] buffer = GetByteFromBinaryData( command );
                yBuffer.SetBytes( buffer, startIndex );
                return new byte[0];
            }
            else if (dataCode == MelsecMcDataType.B.getDataCode())
            {
                byte[] buffer = GetByteFromBinaryData( command );
                bBuffer.SetBytes( buffer, startIndex );
                return new byte[0];
            }
            else if (dataCode == MelsecMcDataType.L.getDataCode())
            {
                byte[] buffer = GetByteFromBinaryData( command );
                lBuffer.SetBytes( buffer, startIndex );
                return new byte[0];
            }
            else if (dataCode == MelsecMcDataType.S.getDataCode())
            {
                byte[] buffer = GetByteFromBinaryData( command );
                sBuffer.SetBytes( buffer, startIndex );
                return new byte[0];
            }
            else if (dataCode == MelsecMcDataType.F.getDataCode())
            {
                byte[] buffer = GetByteFromBinaryData( command );
                fBuffer.SetBytes( buffer, startIndex );
                return new byte[0];
            }
            else if (dataCode == MelsecMcDataType.D.getDataCode())
            {
                dBuffer.SetBytes( SoftBasic.BytesArrayRemoveBegin( command, 10 ), startIndex * 2 );
                return new byte[0];
            }
            else if (dataCode == MelsecMcDataType.W.getDataCode())
            {
                wBuffer.SetBytes( SoftBasic.BytesArrayRemoveBegin( command, 10 ), startIndex * 2 );
                return new byte[0];
            }
            else if (dataCode == MelsecMcDataType.R.getDataCode())
            {
                rBuffer.SetBytes( SoftBasic.BytesArrayRemoveBegin( command, 10 ), startIndex * 2 );
                return new byte[0];
            }
            else if (dataCode == MelsecMcDataType.ZR.getDataCode())
            {
                zrBuffer.SetBytes( SoftBasic.BytesArrayRemoveBegin( command, 10 ), startIndex * 2 );
                return new byte[0];
            }
            else
            {
                throw new Exception( StringResources.Language.NotSupportedDataType() );
            }
        }
    }

    private  byte[] GetByteFromAsciiData( byte[] command ) {
        boolean[] data = SoftBasic.ByteToBoolArray(MelsecHelper.TransAsciiByteArrayToByteArray(SoftBasic.BytesArrayRemoveBegin(command, 20)));
        return Array.GetByteFromBoolArray(data);
    }



    private byte[] WriteAsciiByMessage( byte[] command ) throws Exception {
        int length = Convert.ToInt32( new String( command, 16, 4, StandardCharsets.US_ASCII), 16 );
        String typeCode = Encoding.ASCII.GetString( command, 8, 2 );
        int startIndex = 0;
        if (typeCode.equals(MelsecMcDataType.X.getAsciiCode()) ||
                typeCode.equals(MelsecMcDataType.Y.getAsciiCode()) ||
                typeCode.equals(MelsecMcDataType.W.getAsciiCode()) ||
                typeCode.equals(MelsecMcDataType.B.getAsciiCode()) ||
                typeCode.equals(MelsecMcDataType.L.getAsciiCode()) ||
                typeCode.equals(MelsecMcDataType.S.getAsciiCode()) ||
                typeCode.equals(MelsecMcDataType.F.getAsciiCode()))
            startIndex = Convert.ToInt32( Encoding.ASCII.GetString( command, 10, 6 ), 16 );
        else
            startIndex = Convert.ToInt32( Encoding.ASCII.GetString( command, 10, 6 ) );

        if (command[7] == 0x31)
        {
            // 位写入
            byte[] buffer = SoftBasic.BytesArrayRemoveBegin(command, 20 );
            for(int i = 0; i < buffer.length; i ++){
                buffer[i] = (byte) (buffer[i] & 0xff - 0x30);
            }
            if (buffer.length > length) buffer = SoftBasic.BytesArraySelectBegin(buffer, length);

            if      (typeCode.equals(MelsecMcDataType.M.getAsciiCode())) mBuffer.SetBytes( buffer, startIndex );
            else if (typeCode.equals(MelsecMcDataType.X.getAsciiCode())) xBuffer.SetBytes( buffer, startIndex );
            else if (typeCode.equals(MelsecMcDataType.Y.getAsciiCode())) yBuffer.SetBytes( buffer, startIndex );
            else if (typeCode.equals(MelsecMcDataType.B.getAsciiCode())) bBuffer.SetBytes( buffer, startIndex );
            else if (typeCode.equals(MelsecMcDataType.L.getAsciiCode())) lBuffer.SetBytes( buffer, startIndex );
            else if (typeCode.equals(MelsecMcDataType.S.getAsciiCode())) sBuffer.SetBytes( buffer, startIndex );
            else if (typeCode.equals(MelsecMcDataType.F.getAsciiCode())) fBuffer.SetBytes( buffer, startIndex );
            else throw new Exception( StringResources.Language.NotSupportedDataType() );

            return new byte[0];
        }
        else {
            // 字写入
            if (typeCode.equals(MelsecMcDataType.M.getAsciiCode())) {
                byte[] buffer = GetByteFromAsciiData(command);
                mBuffer.SetBytes(buffer, startIndex);
                return new byte[0];
            } else if (typeCode.equals(MelsecMcDataType.X.getAsciiCode())) {
                byte[] buffer = GetByteFromAsciiData(command);
                xBuffer.SetBytes(buffer, startIndex);
                return new byte[0];
            } else if (typeCode.equals(MelsecMcDataType.Y.getAsciiCode())) {
                byte[] buffer = GetByteFromAsciiData(command);
                yBuffer.SetBytes(buffer, startIndex);
                return new byte[0];
            } else if (typeCode.equals(MelsecMcDataType.B.getAsciiCode())) {
                byte[] buffer = GetByteFromAsciiData(command);
                bBuffer.SetBytes(buffer, startIndex);
                return new byte[0];
            } else if (typeCode.equals(MelsecMcDataType.L.getAsciiCode())) {
                byte[] buffer = GetByteFromAsciiData(command);
                lBuffer.SetBytes(buffer, startIndex);
                return new byte[0];
            } else if (typeCode.equals(MelsecMcDataType.S.getAsciiCode())) {
                byte[] buffer = GetByteFromAsciiData(command);
                sBuffer.SetBytes(buffer, startIndex);
                return new byte[0];
            } else if (typeCode.equals(MelsecMcDataType.F.getAsciiCode())) {
                byte[] buffer = GetByteFromAsciiData(command);
                fBuffer.SetBytes(buffer, startIndex);
                return new byte[0];
            } else if (typeCode.equals(MelsecMcDataType.D.getAsciiCode())) {
                dBuffer.SetBytes(MelsecHelper.TransAsciiByteArrayToByteArray(SoftBasic.BytesArrayRemoveBegin(command, 20)), startIndex * 2);
                return new byte[0];
            } else if (typeCode.equals(MelsecMcDataType.W.getAsciiCode())) {
                wBuffer.SetBytes(MelsecHelper.TransAsciiByteArrayToByteArray(SoftBasic.BytesArrayRemoveBegin(command, 20)), startIndex * 2);
                return new byte[0];
            } else if (typeCode.equals(MelsecMcDataType.R.getAsciiCode())) {
                rBuffer.SetBytes(MelsecHelper.TransAsciiByteArrayToByteArray(SoftBasic.BytesArrayRemoveBegin(command, 20)), startIndex * 2);
                return new byte[0];
            } else if (typeCode.equals(MelsecMcDataType.ZR.getAsciiCode())) {
                zrBuffer.SetBytes(MelsecHelper.TransAsciiByteArrayToByteArray(SoftBasic.BytesArrayRemoveBegin(command, 20)), startIndex * 2);
                return new byte[0];
            } else {
                throw new Exception(StringResources.Language.NotSupportedDataType());
            }
        }
    }

        //endregion

        //region Data Save Load Override

    protected void LoadFromBytes( byte[] content )
    {
        //if (content.length < DataPoolLength * 7) throw new Exception( "File is not correct" );

        mBuffer.SetBytes( content, DataPoolLength * 0, 0, DataPoolLength );
        xBuffer.SetBytes( content, DataPoolLength * 1, 0, DataPoolLength );
        yBuffer.SetBytes( content, DataPoolLength * 2, 0, DataPoolLength );
        dBuffer.SetBytes( content, DataPoolLength * 3, 0, DataPoolLength * 2 );
        wBuffer.SetBytes( content, DataPoolLength * 5, 0, DataPoolLength * 2 );

        if(content.length >= 12)
        {
            bBuffer.SetBytes( content, DataPoolLength * 7, 0, DataPoolLength );
            rBuffer.SetBytes( content, DataPoolLength * 8, 0, DataPoolLength * 2 );
            zrBuffer.SetBytes( content, DataPoolLength * 10, 0, DataPoolLength * 2 );
        }
        if (content.length >= 15)
        {
            dBuffer.SetBytes( content, DataPoolLength * 12, DataPoolLength * 2, DataPoolLength * 2 );
            lBuffer.SetBytes( content, DataPoolLength * 14, 0, DataPoolLength );
        }
    }

    protected byte[] SaveToBytes( )
    {
        byte[] buffer = new byte[DataPoolLength * 15];
        Array.Copy( mBuffer.GetBytes( ), 0, buffer, DataPoolLength * 0, DataPoolLength );
        Array.Copy( xBuffer.GetBytes( ), 0, buffer, DataPoolLength * 1, DataPoolLength );
        Array.Copy( yBuffer.GetBytes( ), 0, buffer, DataPoolLength * 2, DataPoolLength );
        Array.Copy( dBuffer.GetBytes( ), 0, buffer, DataPoolLength * 3, DataPoolLength * 2 );
        Array.Copy( wBuffer.GetBytes( ), 0, buffer, DataPoolLength * 5, DataPoolLength * 2 );
        Array.Copy( bBuffer.GetBytes( ), 0, buffer, DataPoolLength * 7, DataPoolLength );
        Array.Copy( rBuffer.GetBytes( ), 0, buffer, DataPoolLength * 8, DataPoolLength * 2 );
        Array.Copy( zrBuffer.GetBytes( ), 0, buffer, DataPoolLength * 10, DataPoolLength * 2 );
        Array.Copy( dBuffer.GetBytes( ), DataPoolLength * 2, buffer, DataPoolLength * 12, DataPoolLength * 2 );
        Array.Copy( lBuffer.GetBytes( ), 0, buffer, DataPoolLength * 14, DataPoolLength );
        return buffer;
    }

        //endregion

        //region IDisposable Support

        /// <summary>
        /// 释放当前的对象
        /// </summary>
        /// <param name="disposing">是否托管对象</param>
//        protected void Dispose( boolean disposing )
//        {
//            if (disposing)
//            {
//                xBuffer.Dispose( );
//                yBuffer.Dispose( );
//                mBuffer.Dispose( );
//                dBuffer.Dispose( );
//                wBuffer.Dispose( );
//            }
//            base.Dispose( disposing );
//        }

        //endregion

        //region Public Properties


    /**
     * 获取当前的通信格式是否是二进制<br />
     * @return 是否二进制
     */
    public boolean getIsBinary()
    {
        return this.isBinary;
    }

    /**
     * 设置当前的通信格式是否是二进制
     * @param value 是否二进制
     */
    public void setIsBinary(boolean value) {
        this.LogMsgFormatBinary = value;
        this.isBinary = value;
    }

        //endregion

        //region Private Member

    private SoftBuffer xBuffer;                    // x寄存器的数据池
    private SoftBuffer yBuffer;                    // y寄存器的数据池
    private SoftBuffer mBuffer;                    // m寄存器的数据池
    private SoftBuffer lBuffer;                    // l寄存器的数据池
    private SoftBuffer dBuffer;                    // d寄存器的数据池
    private SoftBuffer wBuffer;                    // w寄存器的数据池
    private SoftBuffer bBuffer;                    // b继电器的数据池
    private SoftBuffer sBuffer;                    // s继电器的数据池
    private SoftBuffer fBuffer;                    // f继电器的数据池
    private SoftBuffer rBuffer;                    // r文件寄存器的数据池
    private SoftBuffer zrBuffer;                   // zr文件寄存器的数据池

    private final int DataPoolLength = 65536;      // 数据的长度
    private boolean isBinary = true;                  // 当前的服务器是否是二进制服务器

        //endregion

        //region Object Override

    public String toString( ) {
        return "MelsecMcServer[" + getPort() + "]";
    }

        //endregion
}
