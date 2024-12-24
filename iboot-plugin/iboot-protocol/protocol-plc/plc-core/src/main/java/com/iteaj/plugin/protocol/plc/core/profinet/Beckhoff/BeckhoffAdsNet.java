package com.iteaj.plugin.protocol.plc.core.profinet.Beckhoff;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.basic.SoftIncrementCount;
import com.iteaj.plugin.protocol.plc.core.message.AdsNetMessage;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.thread.SimpleHybirdLock;
import com.iteaj.plugin.protocol.plc.core.transfer.ByteTransformHelper;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.profinet.Beckhoff.Helper.AdsHelper;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

/**
 * 倍福的ADS协议，支持读取倍福的地址数据，关于端口号的选择，TwinCAT2，端口号801；TwinCAT3，端口号为851，NETID可以选择手动输入，自动输入方式，具体参考API文档的示例代码<br />
 * Beckhoff's ADS protocol supports reading Beckhoff address data. Regarding the choice of port number, TwinCAT2, port number is 801; TwinCAT3, port number is 851, NETID can be input manually or automatically.
 * For details, please refer to the example of API documentation code<br /><br />
 *
 * 支持的地址格式分四种，第一种是绝对的地址表示，比如M100，I100，Q100；第二种是字符串地址，采用s=aaaa;的表示方式；第三种是绝对内存地址采用i=1000000;的表示方式，第四种是自定义的index group, IG=0xF020;0 的地址
 */
public class BeckhoffAdsNet extends NetworkDeviceBase {

		// region Constructor

    /**
     * 实例化一个默认的对象<br />
     * Instantiate a default object
     */
    public BeckhoffAdsNet( )
    {
        this.WordLength          = 2;
        this.targetAMSNetId[4]   = 1;
        this.targetAMSNetId[5]   = 1;
        this.targetAMSNetId[6]   = 0x53;
        this.targetAMSNetId[7]   = 0x03;
        this.sourceAMSNetId[4]   = 1;
        this.sourceAMSNetId[5]   = 1;
        this.setByteTransform(new RegularByteTransform());
        this.setUseServerActivePush(true);
    }

    /**
     * 通过指定的ip地址以及端口号实例化一个默认的对象<br />
     * Instantiate a default object with the specified IP address and port number
     * @param ipAddress IP地址信息
     * @param port 端口号
     */
    public BeckhoffAdsNet( String ipAddress, int port ) {
        this();
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    protected INetMessage GetNewNetMessage( ) {
        return new AdsNetMessage( );
    }

		// endregion

		// region Ip Port Override

    public void setIpAddress(String ipAddress) {
        super.setIpAddress(ipAddress);
        String[] ip = Utilities.SplitDot(super.getIpAddress());
        for (int i = 0; i < ip.length; i++) {
            targetAMSNetId[i] = (byte) Integer.parseInt(ip[i]);
        }
    }

    // endregion

    // region Public Properties

    /**
     * 是否使用标签的名称缓存功能，默认为 <c>False</c><br />
     *  Whether to use tag name caching. The default is <c>False</c>
     * @param value 是否缓存
     */
    public void setUseTagCache(boolean value) {
        this.useTagCache = value;
    }

    /**
     * 是否使用标签的名称缓存功能，默认为 <c>False</c><br />
     * Whether to use tag name caching. The default is <c>False</c>
     * @return 是否标签缓存
     */
    public boolean getUseTagCache(){
        return  useTagCache;
    }

    /**
     * 是否使用服务器自动的NETID信息，默认手动设置<br />
     * Whether to use the server's automatic NETID information, manually set by default
     * @return 是否使用自动的ams net id
     */
    public boolean getUseAutoAmsNetID(){
        return useAutoAmsNetID;
    }

    /**
     * 设置是否使用服务器自动的NETID信息，默认手动设置<br />
     * Whether to use the server's automatic NETID information, manually set by default
     * @param value 是否使用自动消息
     */
    public void setUseAutoAmsNetID(boolean value){
        useAutoAmsNetID = value;
    }

    /**
     * 获取端口号信息，TwinCAT2，端口号801,811,821,831；TwinCAT3，端口号为851,852,853<br />
     * Get the port number information of Ams, TwinCAT2, the port number is 801, 811, 821, 831; TwinCAT3, the port number is 851, 852, 853
     * @return 端口号信息
     */
    public int getAmsPort(){
        return BitConverter.ToUInt16( this.targetAMSNetId, 6 );
    }

    /**
     * 设置Ams的端口号信息，TwinCAT2，端口号801,811,821,831；TwinCAT3，端口号为851,852,853<br />
     * Set the port number information of Ams, TwinCAT2, the port number is 801, 811, 821, 831; TwinCAT3, the port number is 851, 852, 853
     * @param value 端口号信息
     */
    public void setAmsPort(int value){
        this.targetAMSNetId[6] = BitConverter.GetBytes( value )[0];
        this.targetAMSNetId[7] = BitConverter.GetBytes( value )[1];
    }

    //endregion

    //region AdsNetId

    private byte[] targetAMSNetId = new byte[8];
    private byte[] sourceAMSNetId = new byte[8];
    private String senderAMSNetId = "";
    private String _targetAmsNetID = "";

    /**
     * 目标的地址，举例 192.168.0.1.1.1；也可以是带端口号 192.168.0.1.1.1:801<br />
     * The address of the destination, for example 192.168.0.1.1.1; it can also be the port number 192.168.0.1.1.1: 801<br /><br />
     *
     * Port：1: AMS Router; 2: AMS Debugger; 800: Ring 0 TC2 PLC; 801: TC2 PLC Runtime System 1; 811: TC2 PLC Runtime System 2; <br />
     * 821: TC2 PLC Runtime System 3; 831: TC2 PLC Runtime System 4; 850: Ring 0 TC3 PLC; 851: TC3 PLC Runtime System 1<br />
     * 852: TC3 PLC Runtime System 2; 853: TC3 PLC Runtime System 3; 854: TC3 PLC Runtime System 4; ...
     * @param amsNetId AMSNet Id地址
     */
    public void SetTargetAMSNetId( String amsNetId ) {
        if (!Utilities.IsStringNullOrEmpty(amsNetId)) {
            Utilities.ByteArrayCopyTo(AdsHelper.StrToAMSNetId(amsNetId), targetAMSNetId, 0);
            this._targetAmsNetID = amsNetId;
        }
    }

    /**
     * 设置原目标地址 举例 192.168.0.100.1.1；也可以是带端口号 192.168.0.100.1.1:34567<br />
     * Set the original destination address Example: 192.168.0.100.1.1; it can also be the port number 192.168.0.100.1.1: 34567
     * @param amsNetId 原地址
     */
    public void SetSenderAMSNetId( String amsNetId ) {
        if (!Utilities.IsStringNullOrEmpty(amsNetId)) {
            Utilities.ByteArrayCopyTo(AdsHelper.StrToAMSNetId(amsNetId), sourceAMSNetId, 0);
            senderAMSNetId = amsNetId;
        }
    }

    /**
     * 获取当前发送的AMS的网络ID信息
     * @return AMS发送信息
     */
    public String GetSenderAMSNetId( ) {
        return AdsHelper.GetAmsNetIdString(sourceAMSNetId, 0);
    }

    /**
     * 获取当前目标的AMS网络的ID信息
     * @return AMS目标信息
     */
    public String GetTargetAMSNetId( ) {
        return AdsHelper.GetAmsNetIdString(targetAMSNetId, 0);
    }

    //endregion

    //region Pack Unpack

    public byte[] PackCommandWithHeader( byte[] command ) {
        int invokeId = (int) incrementCount.GetCurrentValue();
        Utilities.ByteArrayCopyTo(targetAMSNetId, command, 6 + 0);
        Utilities.ByteArrayCopyTo(sourceAMSNetId, command, 6 + 8);

        command[6 + 28] = BitConverter.GetBytes(invokeId)[0];
        command[6 + 29] = BitConverter.GetBytes(invokeId)[1];
        command[6 + 30] = BitConverter.GetBytes(invokeId)[2];
        command[6 + 31] = BitConverter.GetBytes(invokeId)[3];
        return super.PackCommandWithHeader(command);
    }

    public OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response ) {
        if (response.length >= 38) {
            int commandId = getByteTransform().TransUInt16(response, 22);


            OperateResult check = AdsHelper.CheckResponse(response);
            if (!check.IsSuccess) {
                if ((check.ErrorCode == 1809) && (commandId == BeckhoffCommandId.Read || commandId == BeckhoffCommandId.Write)) {
                    // 因为当前的数据句柄不存在了，设备可能已经重启了，清空标记缓存，重新设置所有的数据
                    tagLock.Enter();
                    tagCaches.clear();
                    tagLock.Leave();
                }
                return OperateResultExOne.CreateFailedResult(check);
            }

            try {
                if (commandId == BeckhoffCommandId.ReadDeviceInfo)
                    return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 42));
                if (commandId == BeckhoffCommandId.Read)
                    return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 46));
                if (commandId == BeckhoffCommandId.Write) return OperateResultExOne.CreateSuccessResult(new byte[0]);
                if (commandId == BeckhoffCommandId.ReadState)
                    return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 42));
                if (commandId == BeckhoffCommandId.WriteControl)
                    return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 42));
                if (commandId == BeckhoffCommandId.AddDeviceNotification)
                    return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 42));
                if (commandId == BeckhoffCommandId.DeleteDeviceNotification)
                    return OperateResultExOne.CreateSuccessResult(new byte[0]);
                if (commandId == BeckhoffCommandId.ReadWrite)
                    return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArrayRemoveBegin(response, 46));
            } catch (Exception ex) {
                return new OperateResultExOne<byte[]>("UnpackResponseContent failed: " + ex.getMessage() + "\r\nSource: " + SoftBasic.ByteToHexString(response, ' '));
            }
        }
        return super.UnpackResponseContent(send, response);
    }

    protected void ExtraAfterReadFromCoreServer( OperateResult read ) {
        if (!read.IsSuccess) {
            if (read.ErrorCode < 0) {
                // 当因为发生网络错误的时候，清空标记缓存，重新设置所有的数据
                if (useTagCache) {
                    tagLock.Enter();
                    tagCaches.clear();
                    tagLock.Leave();
                }
            }
        }
        super.ExtraAfterReadFromCoreServer(read);
    }

		//endregion

		//region Initialization Override

    protected OperateResult InitializationOnConnect( Socket socket ) {
        if (Utilities.IsStringNullOrEmpty(senderAMSNetId) && Utilities.IsStringNullOrEmpty(_targetAmsNetID))
            useAutoAmsNetID = true;
        if (useAutoAmsNetID) {
            // 请求 AMS 信息，这个在 TC3 上才支持
            OperateResultExOne<byte[]> read1 = GetLocalNetId();
            if (!read1.IsSuccess) return read1;
            if (read1.Content.length >= 12) Array.Copy(read1.Content, 6, this.targetAMSNetId, 0, 6);

            OperateResult send2 = Send(socket, AdsHelper.PackAmsTcpHelper(AmsTcpHeaderFlags.PortConnect, new byte[2]));
            if (!send2.IsSuccess) return send2;

            OperateResultExOne<byte[]> read2 = ReceiveByMessage(socket, getReceiveTimeOut(), GetNewNetMessage());
            if (!read2.IsSuccess) return read2;
            if (read2.Content.length >= 14) Array.Copy(read2.Content, 6, this.sourceAMSNetId, 0, 8);

            return super.InitializationOnConnect(socket);
        } else if (Utilities.IsStringNullOrEmpty(senderAMSNetId)) {
            InetAddress iPEndPoint = socket.getLocalAddress();
            sourceAMSNetId[6] = BitConverter.GetBytes(socket.getLocalPort())[0];
            sourceAMSNetId[7] = BitConverter.GetBytes(socket.getLocalPort())[1];
            Utilities.ByteArrayCopyTo(iPEndPoint.getAddress(), sourceAMSNetId, 0);
        }

        // 每次连接的时候，重置当前的标签ID信息
        if (useTagCache) {
            tagLock.Enter();
            tagCaches.clear();
            tagLock.Leave();
        }

        return super.InitializationOnConnect(socket);
    }

    /**
     * 获取本地的netid信息
     * @return netid信息
     */
    private OperateResultExOne<byte[]> GetLocalNetId( ) {
        OperateResultExOne<Socket> opSocket = CreateSocketAndConnect(this.getIpAddress(), this.getPort(), this.getConnectTimeOut());
        if (!opSocket.IsSuccess) return OperateResultExOne.CreateFailedResult(opSocket);

        OperateResult send = Send(opSocket.Content, AdsHelper.PackAmsTcpHelper(AmsTcpHeaderFlags.GetLocalNetId, new byte[4]));
        if (!send.IsSuccess) return OperateResultExOne.CreateFailedResult(send);

        OperateResultExOne<byte[]> read = ReceiveByMessage(opSocket.Content, getReceiveTimeOut(), GetNewNetMessage());
        if (!read.IsSuccess) return read;

        CloseSocket(opSocket.Content);
        return read;
    }

    protected boolean DecideWhetherQAMessage(Socket socket, OperateResultExOne<byte[]> receive ) {
        if (!receive.IsSuccess) {
            // 重置当前的标签ID信息
            if (useTagCache) {
                tagLock.Enter();
                tagCaches.clear();
                tagLock.Leave();
            }

            return false;
        }

        byte[] response = receive.Content;
        if (response.length >= 2) {
            int headerFlags = BitConverter.ToUInt16(response, 0);
            if (headerFlags == AmsTcpHeaderFlags.Command) {
                if (response.length >= 24) {
                    int commandId = getByteTransform().TransUInt16(response, 22);
                    if (commandId == BeckhoffCommandId.DeviceNotification) {
                        return false;    // 如果是设备的主动通知的消息，就先屏蔽掉
                    }
                }

                return true;
            }
        }
        return false;
    }
		//endregion

    /**
     * 根据当前标签的地址获取到内存偏移地址<br />
     * Get the memory offset address based on the address of the current label
     * @param address 带标签的地址信息，例如s=A,那么标签就是A
     * @return 内存偏移地址
     */
    public OperateResultExOne<Integer> ReadValueHandle( String address ) {
        if (!address.startsWith("s="))
            return new OperateResultExOne<Integer>("When read valueHandle, address must startwith 's=', forexample: s=MAIN.A");

        OperateResultExOne<byte[]> build = AdsHelper.BuildReadWriteCommand(address, 4, false, AdsHelper.StrToAdsBytes(address.substring(2)));
        if (!build.IsSuccess) return OperateResultExOne.CreateFailedResult(build);

        OperateResultExOne<byte[]> read = ReadFromCoreServer(build.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult((int) BitConverter.ToUInt32(read.Content, 0));
    }

    /**
     * 将字符串的地址转换为内存的地址，其他地址则不操作<br />
     * Converts the address of a string to the address of a memory, other addresses do not operate
     * @param address 地址信息，s=A的地址转换为i=100000的形式
     * @return 地址
     */
    public OperateResultExOne<String> TransValueHandle( String address ) {
        if (address.startsWith("s=") || address.startsWith("S=")) {
            if (useTagCache) {
                tagLock.Enter();
                if (tagCaches.containsKey(address)) {
                    return OperateResultExOne.CreateSuccessResult("i=" + tagCaches.get(address));
                }
                tagLock.Leave();
            }
            OperateResultExOne<Integer> read = ReadValueHandle(address);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            if (useTagCache) {
                tagLock.Enter();
                if (!tagCaches.containsKey(address)) {
                    tagCaches.put(address, read.Content);
                }
                tagLock.Leave();
            }
            return OperateResultExOne.CreateSuccessResult("i=" + read.Content);
        } else
            return OperateResultExOne.CreateSuccessResult(address);
    }

    /**
     * 读取Ads设备的设备信息。主要是版本号，设备名称<br />
     * Read the device information of the Ads device. Mainly version number, device name
     * @return 设备信息
     */
    public OperateResultExOne<AdsDeviceInfo> ReadAdsDeviceInfo( ) {
        OperateResultExOne<byte[]> build = AdsHelper.BuildReadDeviceInfoCommand();
        if (!build.IsSuccess) return OperateResultExOne.CreateFailedResult(build);

        OperateResultExOne<byte[]> read = ReadFromCoreServer(build.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(new AdsDeviceInfo(read.Content));
    }


    /**
     * 读取Ads设备的状态信息，其中Content1是Ads State，Content2是Device State<br />
     * Read the status information of the Ads device, where Content1 is Ads State，Content2 is Device State
     * @return 设备状态信息
     */
    public OperateResultExTwo<Integer, Integer> ReadAdsState( )
    {
        OperateResultExOne<byte[]> build = AdsHelper.BuildReadStateCommand( );
        if (!build.IsSuccess) return OperateResultExTwo.CreateFailedResult( build );

        OperateResultExOne<byte[]> read = ReadFromCoreServer( build.Content );
        if (!read.IsSuccess) return OperateResultExTwo.CreateFailedResult( read );

        return OperateResultExTwo.CreateSuccessResult( BitConverter.ToUInt16( read.Content, 0 ), BitConverter.ToUInt16( read.Content, 2 ) );
    }

    /**
     * 写入Ads的状态，可以携带数据信息，数据可以为空<br />
     * Write the status of Ads, can carry data information, and the data can be empty
     * @param state ads state
     * @param deviceState device state
     * @param data 数据信息
     * @return 是否写入成功
     */
    public OperateResult WriteAdsState( short state, short deviceState, byte[] data ) {
        OperateResultExOne<byte[]> build = AdsHelper.BuildWriteControlCommand(state, deviceState, data);
        if (!build.IsSuccess) return build;

        return ReadFromCoreServer(build.Content);
    }

    /**
     * 释放当前的系统句柄，该句柄是通过 {@link BeckhoffAdsNet#ReadValueHandle(String)} 读取的
     * @param handle 句柄
     * @return 是否释放成功
     */
    public OperateResult ReleaseSystemHandle( int handle ) {
        OperateResultExOne<byte[]> build = AdsHelper.BuildReleaseSystemHandle(handle);
        if (!build.IsSuccess) return build;

        return ReadFromCoreServer(build.Content);
    }

		// region Read Write Override

    /**
     * 读取PLC的数据，地址共有三种格式，一：I,Q,M数据信息，举例M0,M100；二：内存地址，i=100000；三：标签地址，s=A<br />
     * Read PLC data, there are three formats of address, one: I, Q, M data information, such as M0, M100; two: memory address, i = 100000; three: tag address, s = A
     * @param address 地址信息，地址共有三种格式，一：I,Q,M数据信息，举例M0,M100；二：内存地址，i=100000；三：标签地址，s=A
     * @param length 长度
     * @return 包含是否成功的结果对象
     */
    public OperateResultExOne<byte[]> Read( String address, short length ) {
        // 先检查地址
        OperateResultExOne<String> addressCheck = TransValueHandle(address);
        if (!addressCheck.IsSuccess) return OperateResultExOne.CreateFailedResult(addressCheck);

        address = addressCheck.Content;

        OperateResultExOne<byte[]> build = AdsHelper.BuildReadCommand(address, length, false);
        if (!build.IsSuccess) return build;

        return ReadFromCoreServer(build.Content);
    }

//    private void AddLengthAndOffset( List<ushort> length, List<int> offset, ref int index, int len )
//    {
//        length.Add( (ushort)len );
//        offset.Add( index );
//        index += len;
//    }

//    public OperateResult<T> Read<T>( )
//    {
//        var type = typeof( T );
//        var obj = type.Assembly.CreateInstance( type.FullName );
//
//        List<HslAddressProperty> array = HslReflectionHelper.GetHslPropertyInfos( type, this.GetType( ), null, this.ByteTransform );
//        string[] address = array.Select( m => m.DeviceAddressAttribute.Address ).ToArray( );
//        ushort[] length  = array.Select( m => (ushort)m.ByteLength ).ToArray( );
//
//        OperateResult<byte[]> read = Read( address, length );
//        if (!read.IsSuccess) return OperateResult.CreateFailedResult<T>( read );
//
//        HslReflectionHelper.SetPropertyValueFrom( this.ByteTransform, obj, array, read.Content );
//        return OperateResult.CreateSuccessResult( (T)obj );
//    }

    /// <summary>
    /// 读取结构体的信息，传入结构体的类型，以及结构体的起始地址<br />
    /// Read the information of the structure, the type of the incoming structure, and the start address of the structure
    /// </summary>
    /// <typeparam name="T">结构体类型</typeparam>
    /// <param name="address">结构体的地址信息</param>
    /// <returns>是否读取成功</returns>
//    public OperateResult<T> ReadStruct<T>( string address ) where T : struct
//    {
//        T m_struct = new T( );
//        OperateResult<byte[]> read = Read( address, (ushort)Marshal.SizeOf( m_struct ) );
//        if (!read.IsSuccess) return OperateResult.CreateFailedResult<T>( read );
//
//        GCHandle handle = GCHandle.Alloc( read.Content, GCHandleType.Pinned );
//        m_struct = (T)Marshal.PtrToStructure( handle.AddrOfPinnedObject( ), typeof( T ) );
//        handle.Free( );
//        return OperateResult.CreateSuccessResult( m_struct );
//    }

    /// <summary>
    /// 将一个结构体写入到指定的地址中去，需要指定写入的起始地址<br />
    /// To write a structure to a specified address, you need to specify the start address of the write
    /// </summary>
    /// <typeparam name="T">结构体类型</typeparam>
    /// <param name="address">起始地址</param>
    /// <param name="value">结构体的值</param>
    /// <returns>是否写入成功</returns>
//    public OperateResult WriteStruct<T>( String address, T value ) where T : struct
//    {
//        byte[] buffer = new byte[Marshal.SizeOf( value )];
//        GCHandle handle = GCHandle.Alloc( buffer, GCHandleType.Pinned );
//        Marshal.StructureToPtr( value, handle.AddrOfPinnedObject( ), false );
//        handle.Free( );
//        return Write( address, buffer );
//    }

    /**
     * 批量读取PLC的数据，需要传入地址数组，以及读取的长度数组信息，长度单位为字节单位，如果是读取bool变量的，则以bool为单位，统一返回一串字节数据信息，需要进行二次解析的操作。<br />
     * To read PLC data in batches, you need to pass in the address array and the read length array information. The unit of length is in bytes. If you read a bool variable,
     * it will return a string of byte data information in units of bool. , which requires a secondary parsing operation.
     * @param address 地址数组信息
     * @param length 读取的长度数组信息
     * @return 原始字节数组的结果对象
     */
    public OperateResultExOne<byte[]> Read( String[] address, short[] length ) {
        if (address.length != length.length)
            return new OperateResultExOne<byte[]>(StringResources.Language.TwoParametersLengthIsNotSame());

        // 先检查地址
        for (int i = 0; i < address.length; i++) {
            OperateResultExOne<String> addressCheck = TransValueHandle(address[i]);
            if (!addressCheck.IsSuccess) return OperateResultExOne.CreateFailedResult(addressCheck);

            address[i] = addressCheck.Content;
        }

        // 打包命令
        OperateResultExOne<byte[]> build = AdsHelper.BuildReadCommand(address, length);
        if (!build.IsSuccess) return build;

        return ReadFromCoreServer(build.Content);
    }

    /**
     * 写入PLC的数据，地址共有三种格式，一：I,Q,M数据信息，举例M0,M100；二：内存地址，i=100000；三：标签地址，s=A<br />
     * There are three formats for the data written into the PLC. One: I, Q, M data information, such as M0, M100; two: memory address, i = 100000; three: tag address, s = A
     * @param address 地址信息，地址共有三种格式，一：I,Q,M数据信息，举例M0,M100；二：内存地址，i=100000；三：标签地址，s=A
     * @param value 数据值
     * @return 是否写入成功
     */
    public OperateResult Write( String address, byte[] value ) {
        OperateResultExOne<String> addressCheck = TransValueHandle(address);
        if (!addressCheck.IsSuccess) return addressCheck;

        address = addressCheck.Content;

        OperateResultExOne<byte[]> build = AdsHelper.BuildWriteCommand(address, value, false);
        if (!build.IsSuccess) return build;

        return ReadFromCoreServer(build.Content);
    }

    // 调试不通
    //public OperateResult Write( string[] address, List<byte[]> value )
    //{
    //	if (address.Length != value.Count) return new OperateResult<byte[]>( StringResources.Language.TwoParametersLengthIsNotSame );

    //	// 先检查地址
    //	for (int i = 0; i < address.Length; i++)
    //	{
    //		OperateResult<string> addressCheck = TransValueHandle( address[i] );
    //		if (!addressCheck.IsSuccess) return OperateResult.CreateFailedResult<byte[]>( addressCheck );

    //		address[i] = addressCheck.Content;
    //	}

    //	// 打包命令
    //	OperateResult<byte[]> build = AdsHelper.BuildWriteCommand( address, value );
    //	if (!build.IsSuccess) return build;

    //	//Console.WriteLine( "Write data: " + Environment.NewLine + build.Content.ToHexString( ' ', 32 ) );
    //	return ReadFromCoreServer( build.Content );
    //}

    /**
     * 读取PLC的数据，地址共有三种格式，一：I,Q,M数据信息，举例M0,M100；二：内存地址，i=100000；三：标签地址，s=A<br />
     * Read PLC data, there are three formats of address, one: I, Q, M data information, such as M0, M100; two: memory address, i = 100000; three: tag address, s = A
     * @param address PLC的地址信息，例如 M10
     * @param length 数据长度
     * @return 包含是否成功的结果对象
     */
    public OperateResultExOne<boolean[]> ReadBool(String address, short length ) {
        if (address.matches("^[MIQ][0-9]+\\.[0-7]$") && length > 1) {
            return PlcHelper.ReadBool(this, address, length, 8, false);
        }

        OperateResultExOne<String> addressCheck = TransValueHandle(address);
        if (!addressCheck.IsSuccess) return OperateResultExOne.CreateFailedResult(addressCheck);

        address = addressCheck.Content;

        OperateResultExOne<byte[]> build = AdsHelper.BuildReadCommand(address, length, true);
        if (!build.IsSuccess) return OperateResultExOne.CreateFailedResult(build);

        OperateResultExOne<byte[]> read = ReadFromCoreServer(build.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(Array.GetBoolArrayFromBytes(read.Content, 0x01));
    }

    /**
     * 写入PLC的数据，地址共有三种格式，一：I,Q,M数据信息，举例M0,M100；二：内存地址，i=100000；三：标签地址，s=A<br />
     * There are three formats for the data written into the PLC. One: I, Q, M data information, such as M0, M100; two: memory address, i = 100000; three: tag address, s = A
     * @param address 地址信息
     * @param value 数据值
     * @return 是否写入成功
     */
    public OperateResult Write( String address, boolean[] value ) {
        OperateResultExOne<String> addressCheck = TransValueHandle(address);
        if (!addressCheck.IsSuccess) return addressCheck;

        address = addressCheck.Content;

        OperateResultExOne<byte[]> build = AdsHelper.BuildWriteCommand(address, value, true);
        if (!build.IsSuccess) return build;

        return ReadFromCoreServer(build.Content);
    }

    /**
     * 读取PLC的数据，地址共有三种格式，一：I,Q,M数据信息，举例M0,M100；二：内存地址，i=100000；三：标签地址，s=A<br />
     * Read PLC data, there are three formats of address, one: I, Q, M data information, such as M0, M100; two: memory address, i = 100000; three: tag address, s = A
     * @param address 地址信息
     * @return 包含是否成功的结果对象
     */
    public OperateResultExOne<Byte> ReadByte( String address ) {
        return ByteTransformHelper.GetByteResultFromBytes(Read(address, (short) 1), this.getByteTransform());
    }

    /**
     * 写入PLC的数据，地址共有三种格式，一：I,Q,M数据信息，举例M0,M100；二：内存地址，i=100000；三：标签地址，s=A<br />
     * There are three formats for the data written into the PLC. One: I, Q, M data information, such as M0, M100; two: memory address, i = 100000; three: tag address, s = A
     * @param address 地址信息
     * @param value 数据值
     * @return 是否写入成功
     */
    public OperateResult Write( String address, byte value ) {
        return Write( address, new byte[] { value } );
    }

		// endregion


    // region Private Member

    private boolean useAutoAmsNetID = false;
    private boolean useTagCache = false;
    private HashMap<String, Integer> tagCaches = new HashMap<>( );
    private SimpleHybirdLock tagLock = new SimpleHybirdLock( );
    private SoftIncrementCount incrementCount = new SoftIncrementCount( Integer.MAX_VALUE, 1, 1 );

		// endregion

		// region Object Override

    public String toString( ){
        return "BeckhoffAdsNet[" + getIpAddress() + ":" + getPort() + "]";
    }
		// endregion
}
