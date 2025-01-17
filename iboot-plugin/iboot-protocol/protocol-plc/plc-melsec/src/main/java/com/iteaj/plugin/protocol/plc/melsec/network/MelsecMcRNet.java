package com.iteaj.plugin.protocol.plc.melsec.network;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.MelsecQnA3EBinaryMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.StringResources;

/**
 * 三菱的R系列的MC协议，支持的地址类型和 {@link MelsecMcNet} 有区别，详细请查看对应的API文档说明
 */
public class MelsecMcRNet extends NetworkDeviceBase implements IReadWriteMc {
    /**
     * 实例化三菱R系列的Qna兼容3E帧协议的通讯对象<br />
     * Instantiate the communication object of Mitsubishi's Qna compatible 3E frame protocol
     */
    public MelsecMcRNet( )
    {
        this.WordLength    = 1;
        this.setByteTransform(new RegularByteTransform( ));
    }

    /**
     * 指定ip地址和端口号来实例化一个默认的对象<br />
     * Specify the IP address and port number to instantiate a default object
     * @param ipAddress PLC的Ip地址
     * @param port PLC的端口
     */
    public MelsecMcRNet( String ipAddress, int port )
    {
        this.WordLength    = 1;
        this.setIpAddress(ipAddress);
        this.setPort(port);
        this.setByteTransform(new RegularByteTransform( ));
    }

    protected INetMessage GetNewNetMessage( ) {
        return  new MelsecQnA3EBinaryMessage( );
    }

    public McType getMcType(){
        return McType.McRBinary;
    }

    private byte NetworkNumber = 0x00;                       // 网络号
    private byte NetworkStationNumber = 0x00;                // 网络站号

    /**
     * 获取网络号
     *
     * @return 网络号
     */
    public byte getNetworkNumber() {
        return NetworkNumber;
    }

    /**
     * 设置网络号
     *
     * @param networkNumber 网络号
     */
    public void setNetworkNumber(byte networkNumber) {
        NetworkNumber = networkNumber;
    }

    /**
     * 获取网络站号
     *
     * @return 网络站号
     */
    public byte getNetworkStationNumber() {
        return NetworkStationNumber;
    }

    /**
     * 设置网络站号
     *
     * @param networkStationNumber 网络站号
     */
    public void setNetworkStationNumber(byte networkStationNumber) {
        NetworkStationNumber = networkStationNumber;
    }

    public OperateResultExOne<McAddressData> McAnalysisAddress(String address, short length , boolean isBit) {
        return McAddressData.ParseMelsecRFrom( address, length );
    }

    public byte[] PackCommandWithHeader( byte[] command )
    {
        return McBinaryHelper.PackMcCommand( command, this.NetworkNumber, this.NetworkStationNumber );
    }

    public OperateResultExOne<byte[]> UnpackResponseContent( byte[] send, byte[] response )
    {
        OperateResult check = McBinaryHelper.CheckResponseContentHelper( response );
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult( check );

        return OperateResultExOne.CreateSuccessResult( SoftBasic.BytesArrayRemoveBegin(response, 11 ) );
    }

    public byte[] ExtractActualData( byte[] response, boolean isBit ) {
        return McBinaryHelper.ExtractActualDataHelper( response, isBit );
    }

    public OperateResultExOne<byte[]> Read( String address, short length ) {
        return McHelper.Read( this, address, length );
    }

    public OperateResult Write( String address, byte[] value ) {
        return McHelper.Write( this, address, value );
    }

    /**
     * 参考{@link McHelper#ReadRandom(IReadWriteMc, String[])}注释
     * @param address PLC的地址数组
     * @return 原始字节数组
     */
    public OperateResultExOne<byte[]> ReadRandom( String[] address ) {
        return McHelper.ReadRandom( this, address );
    }

    /**
     * 参考{@link McHelper#ReadRandom(IReadWriteMc, String[], short[])}注释
     * @param address PLC的地址数组
     * @param length 长度数组
     * @return 原始字节数组
     */
    public OperateResultExOne<byte[]> ReadRandom( String[] address, short[] length ) {
        return McHelper.ReadRandom( this, address, length );
    }


    /**
     * 参考{@link McHelper#ReadRandomInt16(IReadWriteMc, String[])}注释
     * @param address PLC的地址数组
     * @return 原始字节数组
     */
    public OperateResultExOne<short[]> ReadRandomInt16( String[] address ) {
        return McHelper.ReadRandomInt16( this, address );
    }


    /**
     * 参考{@link McHelper#ReadRandomUInt16(IReadWriteMc, String[])}注释
     * @param address PLC的地址数组
     * @return 原始字节数组
     */
    public OperateResultExOne<int[]> ReadRandomUInt16( String[] address ) {
        return McHelper.ReadRandomUInt16( this, address );
    }

    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        return McHelper.ReadBool( this, address, length );
    }

    public OperateResult Write( String address, boolean[] values ) {
        return McHelper.Write( this, address, values );
    }

    public String toString( ) {
        return "MelsecMcRNet[" + getIpAddress() + ":" + getPort() + "]";
    }

    //endregion

    /**
     * 远程Run操作<br />
     * Remote Run Operation
     * @return 是否成功
     */
    public OperateResult RemoteRun( ) {
        return  McHelper.RemoteRun(this);
    }

    /**
     * 远程Stop操作<br />
     * Remote Stop operation
     * @return 是否成功
     */
    public OperateResult RemoteStop( ) {
        return McHelper.RemoteStop(this);
    }

    /**
     * 远程Reset操作<br />
     * Remote Reset Operation
     * @return 是否成功
     */
    public OperateResult RemoteReset() {
        return McHelper.RemoteReset(this);
    }


    /**
     * 读取PLC的型号信息，例如 Q02HCPU<br />
     * Read PLC model information, such as Q02HCPU
     * @return 返回型号的结果对象
     */
    public OperateResultExOne<String> ReadPlcType( ) {
        return McHelper.ReadPlcType(this);
    }

    /**
     * LED 熄灭 出错代码初始化<br />
     * LED off Error code initialization
     * @return 是否成功
     */
    public OperateResult ErrorStateReset( ) {
        return McHelper.ErrorStateReset(this);
    }

    // region Static Helper

    /**
     * 分析三菱R系列的地址，并返回解析后的数据对象
     * @param address 字符串地址
     * @return 是否解析成功
     */
    public static OperateResultExTwo<MelsecMcDataType,Integer> AnalysisAddress(String address )
    {
        try
        {
            if      (address.startsWith( "LSTS" )) return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_LSTS, Convert.ToInt32( address.substring( 4 ), MelsecMcDataType.R_LSTS.getFromBase() ) );
            else if (address.startsWith( "LSTC" )) return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_LSTC, Convert.ToInt32( address.substring( 4 ), MelsecMcDataType.R_LSTC.getFromBase() ) );
            else if (address.startsWith( "LSTN" )) return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_LSTN, Convert.ToInt32( address.substring( 4 ), MelsecMcDataType.R_LSTN.getFromBase() ) );
            else if (address.startsWith( "STS" ))  return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_STS,  Convert.ToInt32( address.substring( 3 ), MelsecMcDataType.R_STS.getFromBase() ) );
            else if (address.startsWith( "STC" ))  return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_STC,  Convert.ToInt32( address.substring( 3 ), MelsecMcDataType.R_STC.getFromBase() ) );
            else if (address.startsWith( "STN" ))  return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_STN,  Convert.ToInt32( address.substring( 3 ), MelsecMcDataType.R_STN.getFromBase() ) );
            else if (address.startsWith( "LTS" ))  return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_LTS,  Convert.ToInt32( address.substring( 3 ), MelsecMcDataType.R_LTS.getFromBase() ) );
            else if (address.startsWith( "LTC" ))  return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_LTC,  Convert.ToInt32( address.substring( 3 ), MelsecMcDataType.R_LTC.getFromBase() ) );
            else if (address.startsWith( "LTN" ))  return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_LTN,  Convert.ToInt32( address.substring( 3 ), MelsecMcDataType.R_LTN.getFromBase() ) );
            else if (address.startsWith( "LCS" ))  return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_LCS,  Convert.ToInt32( address.substring( 3 ), MelsecMcDataType.R_LCS.getFromBase() ) );
            else if (address.startsWith( "LCC" ))  return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_LCC,  Convert.ToInt32( address.substring( 3 ), MelsecMcDataType.R_LCC.getFromBase() ) );
            else if (address.startsWith( "LCN" ))  return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_LCN,  Convert.ToInt32( address.substring( 3 ), MelsecMcDataType.R_LCN.getFromBase() ) );
            else if (address.startsWith( "TS" ))   return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_TS,   Convert.ToInt32( address.substring( 2 ), MelsecMcDataType.R_TS.getFromBase() ) );
            else if (address.startsWith( "TC" ))   return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_TC,   Convert.ToInt32( address.substring( 2 ), MelsecMcDataType.R_TC.getFromBase() ) );
            else if (address.startsWith( "TN" ))   return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_TN,   Convert.ToInt32( address.substring( 2 ), MelsecMcDataType.R_TN.getFromBase() ) );
            else if (address.startsWith( "CS" ))   return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_CS,   Convert.ToInt32( address.substring( 2 ), MelsecMcDataType.R_CS.getFromBase() ) );
            else if (address.startsWith( "CC" ))   return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_CC,   Convert.ToInt32( address.substring( 2 ), MelsecMcDataType.R_CC.getFromBase() ) );
            else if (address.startsWith( "CN" ))   return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_CN,   Convert.ToInt32( address.substring( 2 ), MelsecMcDataType.R_CN.getFromBase() ) );
            else if (address.startsWith( "SM" ))   return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_SM,   Convert.ToInt32( address.substring( 2 ), MelsecMcDataType.R_SM.getFromBase() ) );
            else if (address.startsWith( "SB" ))   return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_SB,   Convert.ToInt32( address.substring( 2 ), MelsecMcDataType.R_SB.getFromBase() ) );
            else if (address.startsWith( "DX" ))   return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_DX,   Convert.ToInt32( address.substring( 2 ), MelsecMcDataType.R_DX.getFromBase() ) );
            else if (address.startsWith( "DY" ))   return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_DY,   Convert.ToInt32( address.substring( 2 ), MelsecMcDataType.R_DY.getFromBase() ) );
            else if (address.startsWith( "SD" ))   return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_SD,   Convert.ToInt32( address.substring( 2 ), MelsecMcDataType.R_SD.getFromBase() ) );
            else if (address.startsWith( "SW" ))   return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_SW,   Convert.ToInt32( address.substring( 2 ), MelsecMcDataType.R_SW.getFromBase() ) );
            else if (address.startsWith( "X" ))    return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_X,    Convert.ToInt32( address.substring( 1 ), MelsecMcDataType.R_X.getFromBase() ) );
            else if (address.startsWith( "Y" ))    return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_Y,    Convert.ToInt32( address.substring( 1 ), MelsecMcDataType.R_Y.getFromBase() ) );
            else if (address.startsWith( "M" ))    return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_M,    Convert.ToInt32( address.substring( 1 ), MelsecMcDataType.R_M.getFromBase() ) );
            else if (address.startsWith( "L" ))    return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_L,    Convert.ToInt32( address.substring( 1 ), MelsecMcDataType.R_L.getFromBase() ) );
            else if (address.startsWith( "F" ))    return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_F,    Convert.ToInt32( address.substring( 1 ), MelsecMcDataType.R_F.getFromBase() ) );
            else if (address.startsWith( "V" ))    return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_V,    Convert.ToInt32( address.substring( 1 ), MelsecMcDataType.R_V.getFromBase() ) );
            else if (address.startsWith( "S" ))    return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_S,    Convert.ToInt32( address.substring( 1 ), MelsecMcDataType.R_S.getFromBase() ) );
            else if (address.startsWith( "B" ))    return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_B,    Convert.ToInt32( address.substring( 1 ), MelsecMcDataType.R_B.getFromBase() ) );
            else if (address.startsWith( "D" ))    return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_D,    Convert.ToInt32( address.substring( 1 ), MelsecMcDataType.R_D.getFromBase() ) );
            else if (address.startsWith( "W" ))    return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_W,    Convert.ToInt32( address.substring( 1 ), MelsecMcDataType.R_W.getFromBase() ) );
            else if (address.startsWith( "R" ))    return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_R,    Convert.ToInt32( address.substring( 1 ), MelsecMcDataType.R_R.getFromBase() ) );
            else if (address.startsWith( "Z" ))    return OperateResultExTwo.CreateSuccessResult( MelsecMcDataType.R_Z,    Convert.ToInt32( address.substring( 1 ), MelsecMcDataType.R_Z.getFromBase() ) );
            else return new OperateResultExTwo<MelsecMcDataType, Integer>( StringResources.Language.NotSupportedDataType() );
        }
        catch(Exception ex)
        {
            return new OperateResultExTwo<MelsecMcDataType, Integer>( ex.getMessage() );
        }
    }

    /**
     * 从三菱地址，是否位读取进行创建读取的MC的核心报文
     * @param address 地址数据
     * @param isBit 是否进行了位读取操作
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildReadMcCoreCommand( McAddressData address, boolean isBit )
    {
        byte[] command = new byte[12];
        command[ 0] = 0x01;                                                      // 批量读取数据命令
        command[ 1] = 0x04;
        command[ 2] = isBit ? (byte)0x01 : (byte)0x00;                           // 以点为单位还是字为单位成批读取
        command[ 3] = 0x00;
        command[ 4] = BitConverter.GetBytes( address.getAddressStart() )[0];          // 起始地址的地位
        command[ 5] = BitConverter.GetBytes( address.getAddressStart() )[1];
        command[ 6] = BitConverter.GetBytes( address.getAddressStart() )[2];
        command[ 7] = BitConverter.GetBytes( address.getAddressStart() )[3];
        command[ 8] = BitConverter.GetBytes( address.getMcDataType().getDataCode() )[0];     // 指明读取的数据
        command[ 9] = BitConverter.GetBytes( address.getMcDataType().getDataCode() )[1];
        command[10] = (byte)(address.getLength() % 256);                              // 软元件的长度
        command[11] = (byte)(address.getLength() / 256);

        return command;
    }

    /**
     * 以字为单位，创建数据写入的核心报文
     * @param address 三菱的数据地址
     * @param value 实际的原始数据信息
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildWriteWordCoreCommand( McAddressData address, byte[] value )
    {
        if (value == null) value = new byte[0];
        byte[] command = new byte[12 + value.length];
        command[ 0] = 0x01;                                                        // 批量写入数据命令
        command[ 1] = 0x14;
        command[ 2] = 0x00;                                                        // 以字为单位成批读取
        command[ 3] = 0x00;
        command[ 4] = BitConverter.GetBytes( address.getAddressStart() )[0];            // 起始地址的地位
        command[ 5] = BitConverter.GetBytes( address.getAddressStart() )[1];
        command[ 6] = BitConverter.GetBytes( address.getAddressStart() )[2];
        command[ 7] = BitConverter.GetBytes( address.getAddressStart() )[3];
        command[ 8] = BitConverter.GetBytes( address.getMcDataType().getDataCode() )[0];     // 指明读取的数据
        command[ 9] = BitConverter.GetBytes( address.getMcDataType().getDataCode() )[1];
        command[10] = (byte)(value.length / 2 % 256);                              // 软元件长度的地位
        command[11] = (byte)(value.length / 2 / 256);
        System.arraycopy(value, 0, command, 12, value.length);

        return command;
    }

    /**
     * 以位为单位，创建数据写入的核心报文
     * @param address 三菱的地址信息
     * @param value 原始的bool数组数据
     * @return 带有成功标识的报文对象
     */
    public static byte[] BuildWriteBitCoreCommand( McAddressData address, boolean[] value )
    {
        if (value == null) value = new boolean[0];
        byte[] buffer = MelsecHelper.TransBoolArrayToByteData( value );
        byte[] command = new byte[12 + buffer.length];
        command[ 0] = 0x01;                                                        // 批量写入数据命令
        command[ 1] = 0x14;
        command[ 2] = 0x01;                                                        // 以位为单位成批写入
        command[ 3] = 0x00;
        command[ 4] = BitConverter.GetBytes( address.getAddressStart() )[0];            // 起始地址的地位
        command[ 5] = BitConverter.GetBytes( address.getAddressStart() )[1];
        command[ 6] = BitConverter.GetBytes( address.getAddressStart() )[2];
        command[ 7] = BitConverter.GetBytes( address.getAddressStart() )[3];
        command[ 8] = BitConverter.GetBytes( address.getMcDataType().getDataCode() )[0];     // 指明读取的数据
        command[ 9] = BitConverter.GetBytes( address.getMcDataType().getDataCode() )[1];
        command[10] = (byte)(value.length % 256);                                  // 软元件长度的地位
        command[11] = (byte)(value.length / 256);
        System.arraycopy(buffer, 0, command, 12, buffer.length);

        return command;
    }

    // endregion
}
