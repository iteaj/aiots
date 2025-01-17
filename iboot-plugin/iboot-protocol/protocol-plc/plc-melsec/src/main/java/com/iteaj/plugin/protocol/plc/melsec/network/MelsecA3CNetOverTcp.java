package com.iteaj.plugin.protocol.plc.melsec.network;

import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

/**
 * 基于Qna 兼容3C帧的格式一的通讯，具体的地址需要参照三菱的基本地址，本类是基于tcp通讯的实现<br />
 * Based on Qna-compatible 3C frame format one communication, the specific address needs to refer to the basic address of Mitsubishi. This class is based on TCP communication.
 */
public class MelsecA3CNetOverTcp extends NetworkDeviceBase {

		//region Constructor

    /**
     * 实例化默认的对象<br />
     * Instantiate the default object
     */
    public MelsecA3CNetOverTcp( ) {
        this.WordLength = 1;
        this.setByteTransform(new RegularByteTransform());
        this.setSleepTime(20);
    }

    /**
     * 指定ip地址和端口号来实例化对象<br />
     * Specify the IP address and port number to instantiate the object
     * @param ipAddress Ip地址信息
     * @param port 端口号信息
     */
    public MelsecA3CNetOverTcp( String ipAddress, int port ) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

		//endregion

		//region Public Member

    public byte Station = 0x00;

    public boolean SumCheck = true;

    public int Format = 1;

    public boolean EnableWriteBitToWordRegister = false;

    //endregion

		//region Read Write Support

    /**
     * 批量读取PLC的数据，以字为单位，支持读取X,Y,M,S,D,T,C，具体的地址范围需要根据PLC型号来确认<br />
     * Read PLC data in batches, in units of words, supports reading X, Y, M, S, D, T, C. The specific address range needs to be confirmed according to the PLC model
     * @param address 地址信息
     * @param length 数据长度
     * @return 读取结果信息
     */
    public OperateResultExOne<byte[]> Read(String address, short length ) {
        return MelsecA3CNetHelper.Read( this, address, length );
    }

    /**
     * 批量写入PLC的数据，以字为单位，也就是说最少2个字节信息，支持X,Y,M,S,D,T,C，具体的地址范围需要根据PLC型号来确认<br />
     * The data written to the PLC in batches is in units of words, that is, at least 2 bytes of information. It supports X, Y, M, S, D, T, and C. The specific address range needs to be confirmed according to the PLC model.
     * @param address 地址信息
     * @param value 数据值
     * @return 是否写入成功
     */
    public OperateResult Write(String address, byte[] value ) {
        return MelsecA3CNetHelper.Write( this, address, value );
    }

		//endregion

		//Region Bool Read Write

    /**
     * 批量读取bool类型数据，支持的类型为X,Y,S,T,C，具体的地址范围取决于PLC的类型<br />
     * Read bool data in batches. The supported types are X, Y, S, T, C. The specific address range depends on the type of PLC.
     * @param address 地址信息，比如X10,Y17，注意X，Y的地址是8进制的
     * @param length 读取的长度
     * @return 读取结果信息
     */
    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        return MelsecA3CNetHelper.ReadBool( this, address, length );
    }

    /**
     * 批量写入bool类型的数组，支持的类型为X,Y,S,T,C，具体的地址范围取决于PLC的类型<br />
     * Write arrays of type bool in batches. The supported types are X, Y, S, T, C. The specific address range depends on the type of PLC.<br /><br />
     * 当需要写入D寄存器的位时，可以开启{@link MelsecA3CNetOverTcp#EnableWriteBitToWordRegister}为<c>True</c>，然后地址使用 D100.2 等格式进行批量写入位操作，该操作有一定风险，参考{@link MelsecA3CNetOverTcp#EnableWriteBitToWordRegister}属性。
     * @param address PLC的地址信息
     * @param value 数据信息
     * @return 是否写入成功
     */
    public OperateResult Write(String address, boolean[] value ) {
        return MelsecA3CNetHelper.Write( this, address, value );
    }

		//endregion

		//region Remote Operate

    /**
     * @see MelsecA3CNetHelper#RemoteRun(MelsecA3CNetOverTcp) 
     */
    public OperateResult RemoteRun( ) {
        return MelsecA3CNetHelper.RemoteRun( this );
    }

    /**
     * @see MelsecA3CNetHelper#RemoteStop(MelsecA3CNetOverTcp) 
     */
    public OperateResult RemoteStop( ) {
        return MelsecA3CNetHelper.RemoteStop( this );
    }

    /**
     * @see MelsecA3CNetHelper#ReadPlcType(MelsecA3CNetOverTcp)
     */
    public OperateResultExOne<String> ReadPlcType( ) {
        return MelsecA3CNetHelper.ReadPlcType( this );
    }

		//endregion

		//region Object Override

    public String toString( ) {
         return "MelsecA3CNetOverTcp[" + getIpAddress() + ":" + getPort() + "]";
    }

		//endregion

}
