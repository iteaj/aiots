package com.iteaj.plugin.protocol.plc.core.net.NetworkBase;

import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolAbstract;
import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.net.StateOne.AlienSession;
import com.iteaj.plugin.protocol.plc.core.pipe.PipeSocket;
import com.iteaj.plugin.protocol.plc.core.transfer.IByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.log.core.ILogNet;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

/**
 * 支持长连接，短连接两个模式的通用客户端基类 <br />
 * Universal client base class that supports long connections and short connections to two modes
 */
public class NetworkDoubleBase extends NetworkBase {
    /**
     * 默认的无参构造函数
     */
    public NetworkDoubleBase() {
        this.pipeSocket = new PipeSocket();
        this.connectionId = SoftBasic.GetUniqueStringByGuidAndRandom();
    }

    protected PipeSocket pipeSocket;
    private IByteTransform byteTransform;                 // 数据变换的接口
    protected boolean isPersistentConn = false;           // 是否处于长连接的状态
    private boolean useServerActivePush = false;          // 是否使用设备方主动发送数据的功能
    private AutoResetEvent autoResetEvent;                     // 当启用设备方主动发送数据时，用于同步访问方法的信号同步功能
    private byte[] bufferQA = null;                       // 当启用设备方主动发送数据时，用于应答服务机制的数据缓存
    private Thread threadBackReceive;

    /**
     * 设置日志记录报文是否二进制，如果为False，那就使用ASCII码<br />
     * Set whether the log message is binary, if it is False, then use ASCII code
     */
    protected boolean LogMsgFormatBinary = true;

    private boolean isUseSpecifiedSocket = false;         // 指示是否使用指定的网络套接字访问数据
    private String connectionId = "";                     // 当前连接
    private int sleepTime = 0;                            // 获取或设置在正式接收对方返回数据前的时候，需要休息的时间，当设置为0的时候，不需要休息。

    /**
     * 设置当前的连接是否激活从服务器主动推送的功能
     * @param value 是否激活的值信息
     */
    protected void setUseServerActivePush( boolean value ){
        if (value){
            if (this.autoResetEvent == null) this.autoResetEvent = new AutoResetEvent(false);
            this.isPersistentConn = true;
        }
        this.useServerActivePush = value;
    }

    /**
     * 决定当前的消息是否是应答机制的消息内容，需要在客户端进行重写实现，如果是应答机制，返回 <c>True</c>, 否则返回 <c>False</c><br />
     * To determine whether the current message is the message content of the response mechanism,
     * it needs to be rewritten on the client side. If it is the response mechanism, return <c>True</c>, otherwise return <c>False</c>
     * @param socket 通信使用的网络套接字
     * @param receive 服务器返回的内容
     * @return 是否应答机制的数据报文
     */
    protected boolean DecideWhetherQAMessage( Socket socket, OperateResultExOne<byte[]> receive ) {
        return true;
    }

    /**
     * 获取一个新的消息对象的方法，需要在继承类里面进行重写<br />
     * The method to get a new message object needs to be overridden in the inheritance class
     * @return 消息类对象
     */
    protected INetMessage GetNewNetMessage() {
        return null;
    }

    /**
     * 获取当前的数据变换机制，当你需要从字节数据转换类型数据的时候需要。<br />
     * Get the current data transformation mechanism is required when you need to convert type data from byte data.
     * @return 数据的变换对象
     */
    public IByteTransform getByteTransform() {
        return byteTransform;
    }

    /**
     * 设置当前的数据变换机制，当你需要从字节数据转换类型数据的时候需要。<br />
     * Set the current data transformation mechanism is required when you need to convert type data from byte data.
     * @param transform 数据变换
     */
    public void setByteTransform(IByteTransform transform) {
        byteTransform = transform;
    }

    /**
     * 获取连接的超时时间，单位是毫秒 <br />
     * Gets the timeout for the connection, in milliseconds
     */
    public int getConnectTimeOut() {
        return this.pipeSocket.getConnectTimeOut();
    }

    /**
     * 设置连接的超时时间，单位是毫秒 <br />
     * Sets the timeout for the connection, in milliseconds
     * @param connectTimeOut 超时时间，单位是毫秒
     */
    public void setConnectTimeOut(int connectTimeOut) {
        this.pipeSocket.setConnectTimeOut( connectTimeOut );
    }

    /**
     * 获取接收服务器反馈的时间，如果为负数，则不接收反馈 <br />
     * Gets the time to receive server feedback, and if it is a negative number, does not receive feedback
     * @return 接收超时的信息
     */
    public int getReceiveTimeOut() {
        return this.pipeSocket.getReceiveTimeOut();
    }

    /**
     * 设置接收服务器反馈的时间，如果为负数，则不接收反馈 <br />
     * Sets the time to receive server feedback, and if it is a negative number, does not receive feedback
     * @param receiveTimeOut 数值
     */
    public void setReceiveTimeOut(int receiveTimeOut) {
        this.pipeSocket.setReceiveTimeOut( receiveTimeOut );
    }

    /**
     * 获取远程服务器的IP地址，如果是本机测试，那么需要设置为127.0.0.1 <br />
     * Get the IP address of the remote server. If it is a local test, then it needs to be set to 127.0.0.1
     * @return Ip地址信息
     */
    public String getIpAddress() {
        return this.pipeSocket.getIpAddress();
    }

    /**
     * 设置远程服务器的IP地址，如果是本机测试，那么需要设置为127.0.0.1 <br />
     * Set the IP address of the remote server. If it is a local test, then it needs to be set to 127.0.0.1
     * @param ipAddress IP地址
     */
    public void setIpAddress(String ipAddress) {
        if (!ipAddress.isEmpty()) {
            this.pipeSocket.setIpAddress(ipAddress);
        }
    }

    /**
     * 获取服务器的端口号，具体的值需要取决于对方的配置<br />
     * Gets the port number of the server. The specific value depends on the configuration of the other party.
     * @return 端口
     */
    public int getPort() {
        return this.pipeSocket.getPort();
    }

    /**
     * 设置服务器的端口号，具体的值需要取决于对方的配置<br />
     * Sets the port number of the server. The specific value depends on the configuration of the other party.
     * @param port 端口号
     */
    public void setPort(int port) {
        this.pipeSocket.setPort( port );
    }

    /**
     * 当前连接的唯一ID号，默认为长度20的guid码加随机数组成，也可以自己指定
     * @return 字符串信息
     */
    public String getConnectionId() {
        return connectionId;
    }

    /**
     * 当前连接的唯一ID号，默认为长度20的guid码加随机数组成，方便列表管理，也可以自己指定<br />
     * The unique ID number of the current connection. The default is a 20-digit guid code plus a random number.
     * @param connectionId 连接的id信息
     */
    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    /**
     * 获取在正式接收对方返回数据前的时候，需要休息的时间。单位毫秒<br />
     * Get the wait time before receiving the data from the other party. In milliseconds
     * @return 休息时间
     */
    public int getSleepTime() {
        return sleepTime;
    }

    /**
     * 设置接收对方返回数据前的时候，需要休息的时间，当设置为0的时候，不需要休息。<br />
     * Set the time to rest before receiving the data from the other party. When set to 0, no rest is required.
     * @param sleepTime 睡眠时间
     */
    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    /**
     * 当前的异形连接对象，如果设置了异性连接的话
     */
    public com.iteaj.plugin.protocol.plc.core.net.StateOne.AlienSession AlienSession = null;

    /**
     * 设置一个新的网络管道，一般来说不需要调用本方法，当多个网口设备共用一个网络连接时才需要使用本方法进行设置共享的管道。<br />
     * To set up a new network channel, generally speaking, you do not need to call this method. This method is only needed to set up a shared channel when multiple network port devices share a network connection.
     * @param pipeSocket 共享的网络通道
     */
    public void SetPipeSocket( PipeSocket pipeSocket )
    {
        if (this.pipeSocket != null)
        {
            this.pipeSocket = pipeSocket;
            SetPersistentConnection( );
        }
    }

    /**
     * 获取当前用于通信的管道信息<br />
     * Get the current pipe information used for communication
     * @return 管道对象
     */
    public PipeSocket GetPipeSocket( ) {
        return  this.pipeSocket;
    }

    /**
     * 在读取数据之前可以调用本方法将客户端设置为长连接模式，相当于跳过了ConnectServer的结果验证，对异形客户端无效，当第一次进行通信时再进行创建连接请求。<br />
     * Before reading the data, you can call this method to set the client to the long connection mode, which is equivalent to skipping the result verification of ConnectServer,
     * and it is invalid for the alien client. When the first communication is performed, the connection creation request is performed.
     */
    public void SetPersistentConnection() {
        isPersistentConn = true;
    }

    /**
     * 对当前设备的IP地址进行PING的操作，返回PING的结果，正常来说，返回 True
     * @return PING the IP address of the current device and return the PING result. Normally, it returns True
     * @throws IOException 异常信息
     */
    public boolean IpAddressPing( ) throws IOException {
        return InetAddress.getByName(this.getIpAddress()).isReachable(3000);
    }

    /**
     * 尝试连接远程的服务器，如果连接成功，就切换短连接模式到长连接模式，后面的每次请求都共享一个通道，使得通讯速度更快速<br />
     * Try to connect to a remote server. If the connection is successful, switch the short connection mode to the long connection mode.
     * Each subsequent request will share a channel, making the communication speed faster.
     * @return 返回连接结果，如果失败的话（也即IsSuccess为False），包含失败信息
     */
    public OperateResult ConnectServer() {
        isPersistentConn = true;
        // 重新连接之前，先将旧的数据进行清空
        CloseSocket(this.pipeSocket.getSocket());

        OperateResultExOne<Socket> rSocket = CreateSocketAndInitialication();

        if (!rSocket.IsSuccess) {
            this.pipeSocket.setIsSocketError(true);                         // 创建失败
            rSocket.Content = null;
        } else {
            this.pipeSocket.setSocket( rSocket.Content );                 // 创建成功
            this.pipeSocket.setIsSocketError(false);
            ILogNet logNet = LogNet;
            if (logNet != null) logNet.WriteDebug(toString(), StringResources.Language.NetEngineStart());
        }
        return rSocket;
    }

    /**
     * 使用指定的套接字创建异形客户端，在异形客户端的模式下，网络通道需要被动创建。<br />
     * Use the specified socket to create the alien client. In the alien client mode, the network channel needs to be created passively.
     * @param session 会话
     * @return 通常都为成功
     */
    public OperateResult ConnectServer(AlienSession session) {
        isPersistentConn = true;
        isUseSpecifiedSocket = true;

        if (session != null) {
            if (AlienSession != null) CloseSocket(AlienSession.getSocket());

            if (connectionId.isEmpty()) {
                connectionId = session.getDTU();
            }

            if (connectionId.equals(session.getDTU())) {
                if (session.getIsStatusOk()){
                    OperateResult ini = InitializationOnConnect(session.getSocket());
                    if(ini.IsSuccess){
                        this.pipeSocket.setSocket( session.getSocket() );
                        this.pipeSocket.setIsSocketError( !session.getIsStatusOk() );
                        AlienSession = session;
                    }
                    else {
                        this.pipeSocket.setIsSocketError( true );
                    }
                    return  ini;
                }
                return new OperateResult();
            } else {
                this.pipeSocket.setIsSocketError( true );
                return new OperateResult();
            }
        } else {
            this.pipeSocket.setIsSocketError( true );
            return new OperateResult();
        }
    }

    /**
     * 手动断开与远程服务器的连接，如果当前是长连接模式，那么就会切换到短连接模式<br />
     * Manually disconnect from the remote server, if it is currently in long connection mode, it will switch to short connection mode
     * @return 关闭连接，不需要查看IsSuccess属性查看
     */
    public OperateResult ConnectClose() {
        OperateResult result = new OperateResult();
        isPersistentConn = false;

        this.pipeSocket.PipeLockEnter();
        try {

            // 额外操作
            result = this.pipeSocket.getSocket() != null ? ExtraOnDisconnect(this.pipeSocket.getSocket()) : OperateResult.CreateSuccessResult();
            // 关闭信息
            CloseSocket(this.pipeSocket.getSocket());
            this.pipeSocket.setSocket(null);

            this.pipeSocket.PipeLockLeave();
        } catch (Exception ex) {
            this.pipeSocket.PipeLockLeave();
        }

        if (LogNet != null) LogNet.WriteDebug(toString(), StringResources.Language.NetEngineClose());
        return result;
    }


    /**
     * 根据实际的协议选择是否重写本方法，有些协议在创建连接之后，需要进行一些初始化的信号握手，才能最终建立网络通道。<br />
     * Whether to rewrite this method is based on the actual protocol. Some protocols require some initial signal handshake to establish a network channel after the connection is created.
     * @param socket 网络套接字
     * @return 结果类对象
     */
    protected OperateResult InitializationOnConnect(Socket socket) {
        if (this.useServerActivePush){
            // 启动后台接收
            threadBackReceive = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        OperateResultExOne<byte[]> receive = ReceiveByMessage(socket, -1, null,  GetNewNetMessage());
                        if (!receive.IsSuccess) {
                            pipeSocket.setIsSocketError(true);
                            return;
                        }

                        if(LogNet!= null) LogNet.WriteDebug( toString( ), StringResources.Language.Receive() + " : " + (LogMsgFormatBinary ? SoftBasic.ByteToHexString(receive.Content, ' ' ) : SoftBasic.GetAsciiStringRender( receive.Content )) );

                        if (DecideWhetherQAMessage( socket, receive ))
                        {
                            bufferQA = receive.Content;
                            autoResetEvent.set( );
                        }
                    }
                }
            });
            threadBackReceive.start();
        }
        return OperateResult.CreateSuccessResult();
    }


    /**
     * 根据实际的协议选择是否重写本方法，有些协议在断开连接之前，需要发送一些报文来关闭当前的网络通道<br />
     * Select whether to rewrite this method according to the actual protocol. Some protocols need to send some packets to close the current network channel before disconnecting.
     * @param socket 网络套接字
     * @return 结果类对象
     */
    protected OperateResult ExtraOnDisconnect(Socket socket) {
        return OperateResult.CreateSuccessResult();
    }

    /**
     * 和服务器交互完成的时候调用的方法，可以根据读写结果进行一些额外的操作，具体的操作需要根据实际的需求来重写实现<br />
     * The method called when the interaction with the server is completed can perform some additional operations based on the read and write results.
     * The specific operations need to be rewritten according to actual needs.
     * @param read 读取结果
     */
    protected void ExtraAfterReadFromCoreServer( OperateResult read ) {
    }


    // region Account Control

    /************************************************************************************************
     *
     *    这部分的内容是为了实现账户控制的，如果服务器按照hsl协议强制socket账户登录的话，本客户端类就需要额外指定账户密码
     *
     *    The content of this part is for account control. If the server forces the socket account to log in according to the hsl protocol,
     *    the client class needs to specify the account password.
     *
     *    适用于hsl实现的modbus服务器，三菱及西门子，NetSimplify服务器类等
     *
     *    Modbus server for hsl implementation, Mitsubishi and Siemens, NetSimplify server class, etc.
     *
     ************************************************************************************************/

    /**
     * 是否使用账号登录，这个账户登录的功能是 <c>HSL</c> 组件创建的服务器特有的功能。<br />
     * Whether to log in using an account. The function of this account login is a server-specific function created by the <c> HSL </c> component.
     */
    protected boolean isUseAccountCertificate = false;
    private String userName = "";
    private String password = "";

    /**
     * 设置当前的登录的账户名和密码信息，并启用账户验证的功能，账户名为空时设置不生效<br />
     * Set the current login account name and password information, and enable the account verification function. The account name setting will not take effect when it is empty
     * @param userName 账户名
     * @param password 密码
     */
    public void SetLoginAccount( String userName, String password ) {
        if (!Utilities.IsStringNullOrEmpty(userName)) {
            isUseAccountCertificate = true;
            this.userName = userName;
            this.password = password;
        } else {
            isUseAccountCertificate = false;
        }
    }

    /**
     * 认证账号，根据已经设置的用户名和密码，进行发送服务器进行账号认证。<br />
     * Authentication account, according to the user name and password that have been set, sending server for account authentication.
     * @param socket 套接字
     * @return 认证结果
     */
    protected OperateResult AccountCertificate( Socket socket ) {
        OperateResult send = SendAccountAndCheckReceive(socket, 1, this.userName, this.password);
        if (!send.IsSuccess) return send;

        OperateResultExTwo<Integer, String[]> read = ReceiveStringArrayContentFromSocket(socket);
        if (!read.IsSuccess) return read;

        if (read.Content1 == 0) return new OperateResult(read.Content2[0]);
        return OperateResult.CreateSuccessResult();
    }

    // endregion

    /***************************************************************************************
     *
     *    主要的数据交互分为4步
     *    1. 连接服务器，或是获取到旧的使用的网络信息
     *    2. 发送数据信息
     *    3. 接收反馈的数据信息
     *    4. 关闭网络连接，如果是短连接的话
     *
     **************************************************************************************/

    /**
     * 对当前的命令进行打包处理，通常是携带命令头内容，标记当前的命令的长度信息，需要进行重写，否则默认不打包
     * @param command 发送的数据命令内容
     * @return 打包之后的数据结果信息
     */
    protected byte[] PackCommandWithHeader( byte[] command ) {
        return  command;
    }

    /**
     * 根据对方返回的报文命令，对命令进行基本的拆包，例如各种Modbus协议拆包为统一的核心报文，还支持对报文的验证
     * @param send 发送的原始报文数据
     * @param response 设备方反馈的原始报文内容
     * @return 返回拆包之后的报文信息，默认不进行任何的拆包操作
     */
    protected OperateResultExOne<byte[]> UnpackResponseContent( byte[] send, byte[] response ) {
        return  OperateResultExOne.CreateSuccessResult( response );
    }

    /**
     * 获取本次操作的可用的网络通道，如果是短连接，就重新生成一个新的网络通道，如果是长连接，就复用当前的网络通道。<br />
     * Obtain the available network channels for this operation. If it is a short connection, a new network channel is regenerated.
     * If it is a long connection, the current network channel is reused.
     * @return 是否成功，如果成功，使用这个套接字
     */
    protected OperateResultExOne<Socket> GetAvailableSocket() {
        if (isPersistentConn) {
            // 如果是异形模式
            if (isUseSpecifiedSocket) {
                if (this.pipeSocket.getIsSocketError()) {
                    return new OperateResultExOne<>(StringResources.Language.ConnectionIsNotAvailable());
                } else {
                    return OperateResultExOne.CreateSuccessResult(this.pipeSocket.getSocket());
                }
            } else {
                // 长连接模式
                if (this.pipeSocket.IsConnectitonError()) {
                    OperateResult connect = ConnectServer();
                    if (!connect.IsSuccess) {
                        this.pipeSocket.setIsSocketError(true);
                        return OperateResultExOne.<Socket>CreateFailedResult(connect);
                    } else {
                        this.pipeSocket.setIsSocketError(false);
                        return OperateResultExOne.CreateSuccessResult(this.pipeSocket.getSocket());
                    }
                } else {
                    return OperateResultExOne.CreateSuccessResult(this.pipeSocket.getSocket());
                }
            }
        } else {
            // 短连接模式
            return CreateSocketAndInitialication();
        }
    }

    /**
     * 尝试连接服务器，如果成功，并执行 {@link NetworkDoubleBase#InitializationOnConnect(Socket)} 的初始化方法，并返回最终的结果。<br />
     * @return 最终的连接对象
     */
    private OperateResultExOne<Socket> CreateSocketAndInitialication() {
        OperateResultExOne<Socket> result = CreateSocketAndConnect(this.pipeSocket.GetConnectIPEndPoint(), this.getConnectTimeOut());
        if (result.IsSuccess) {
            // 初始化
            OperateResult initi = InitializationOnConnect(result.Content);
            if (!initi.IsSuccess) {
                CloseSocket(result.Content);
                result.IsSuccess = initi.IsSuccess;
                result.CopyErrorFromOther(initi);
            }
        }
        return result;
    }

    /**
     * 将数据报文发送指定的网络通道上，根据当前指定的 {@link INetMessage} 类型，返回一条完整的数据指令<br />
     * Sends a data message to the specified network channel, and returns a complete data command according to the currently specified <see cref = "INetMessage" /> type
     * @param socket 指定的套接字
     * @param send   发送的完整的报文信息
     * @param hasResponseData 是否有等待的数据返回
     * @param usePackAndUnpack 是否需要对命令重新打包
     * @return 接收的完整的报文信息
     */
    public OperateResultExOne<byte[]> ReadFromCoreServer(Socket socket, byte[] send, boolean hasResponseData, boolean usePackAndUnpack ) {

        byte[] sendValue = usePackAndUnpack ? PackCommandWithHeader(send) : send;
        PlcProtocolAbstract plcProtocolAbstract = PlcProtocolAbstract.get();
        if(plcProtocolAbstract != null) {
            plcProtocolAbstract.setRequestMessage(plcProtocolAbstract.build(sendValue));
        }

        ILogNet logNet = LogNet;
        if (logNet != null) logNet.WriteDebug(this.toString(), StringResources.Language.Send() + " : " +
                (LogMsgFormatBinary ? SoftBasic.ByteToHexString(sendValue, ' ') : SoftBasic.GetAsciiStringRender(sendValue)));
        INetMessage netMessage = GetNewNetMessage();
        if (netMessage != null) netMessage.setSendBytes(sendValue);

        // send data
        OperateResult resultSend = Send(socket, sendValue);
        if (!resultSend.IsSuccess) return OperateResultExOne.CreateFailedResult(resultSend);
        if (this.getReceiveTimeOut() < 0)
            return OperateResultExOne.CreateSuccessResult(new byte[0]); // 接收超时时间大于0时才允许接收远程的数据
        if (!hasResponseData) return OperateResultExOne.CreateSuccessResult(new byte[0]);
        try {
            if (sleepTime > 0) Thread.sleep(sleepTime);
        } catch (Exception ignored) {

        }

        // 接收数据信息
        OperateResultExOne<byte[]> resultReceive = null;
        Date start = new Date();
        int times = 0;
        while (true) {
            if (this.useServerActivePush) {
                try {
                    boolean waitOne = this.autoResetEvent.waitOne(this.getReceiveTimeOut());
                    if (waitOne) {
                        if (netMessage != null) netMessage.setHeadBytes(bufferQA);
                        resultReceive = OperateResultExOne.CreateSuccessResult(bufferQA);
                    } else {
                        CloseSocket(socket);
                        pipeSocket.setIsSocketError(true);
                        return new OperateResultExOne<byte[]>(-10000, StringResources.Language.ReceiveDataTimeout() + getReceiveTimeOut());
                    }
                } catch (Exception ex) {
                    return new OperateResultExOne<byte[]>(-10000, "tryAcquire failed: " + ex.getMessage());
                }
            } else {
                resultReceive = ReceiveByMessage(socket, this.getReceiveTimeOut(), sendValue, netMessage);
            }

            if (!resultReceive.IsSuccess) return OperateResultExOne.CreateFailedResult(resultReceive);
            if(plcProtocolAbstract != null) {
                plcProtocolAbstract.setResponseMessage(plcProtocolAbstract.build(resultReceive.Content));
            }

            if (logNet != null) logNet.WriteDebug(this.toString(), StringResources.Language.Receive() + " : " +
                    (LogMsgFormatBinary ? SoftBasic.ByteToHexString(resultReceive.Content, ' ') : SoftBasic.GetAsciiStringRender(resultReceive.Content)));


            if (netMessage == null) break;

            // 检查消息一致性
            int check = netMessage.CheckMessageMatch(sendValue, resultReceive.Content);
            if (check == 1) break;
            if (check == 0)
                return new OperateResultExOne<byte[]>("INetMessage.CheckMessageMatch failed" + Environment.NewLine +
                        StringResources.Language.Send() + ": " + SoftBasic.ByteToHexString(sendValue, ' ') + Environment.NewLine +
                        StringResources.Language.Receive() + ": " + SoftBasic.ByteToHexString(resultReceive.Content, ' '));

            times++;
            if (getReceiveTimeOut() >= 0 && (new Date().getTime() - start.getTime()) > this.getReceiveTimeOut())
                return new OperateResultExOne<byte[]>("Receive Message timeout: " + this.getReceiveTimeOut() + " CheckMessageMatch times:" + times);
        }


        if (netMessage != null && !netMessage.CheckHeadBytesLegal(Utilities.UUID2Byte(Token))) {
            CloseSocket(socket);
            return new OperateResultExOne<>(StringResources.Language.CommandHeadCodeCheckFailed());
        }
        return usePackAndUnpack ? UnpackResponseContent(sendValue, resultReceive.Content) : resultReceive;
    }

    /**
     * 将数据发送到当前的网络通道中，并从网络通道中接收一个 {@link INetMessage} 指定的完整的报文，网络通道将根据 {@link NetworkDoubleBase#GetAvailableSocket()} 方法自动获取，本方法是线程安全的。<br />
     * Send data to the current network channel and receive a complete message specified by {@link INetMessage} from the network channel.
     * The network channel will be automatically obtained according to the {@link NetworkDoubleBase#GetAvailableSocket()} method This method is thread-safe.
     * @param send 发送的完整的报文信息
     * @param hasResponseData 是否有等待的数据返回
     * @param usePackAndUnpack 是否需要对命令重新打包
     * @return 接收的完整的报文信息
     */
    public OperateResultExOne<byte[]> ReadFromCoreServer(byte[] send, boolean hasResponseData, boolean usePackAndUnpack ) {
        OperateResultExOne<byte[]> result = new OperateResultExOne<byte[]>();
        // string tmp1 = BasicFramework.SoftBasic.ByteToHexString( send, '-' );

        this.GetPipeSocket().PipeLockEnter();

        // 获取有用的网络通道，如果没有，就建立新的连接
        OperateResultExOne<Socket> resultSocket = GetAvailableSocket();
        if (!resultSocket.IsSuccess) {
            this.pipeSocket.setIsSocketError(true);
            if (AlienSession != null) AlienSession.setIsStatusOk(false);
            this.pipeSocket.PipeLockLeave();
            result.CopyErrorFromOther(resultSocket);
            return result;
        }

        OperateResultExOne<byte[]> read = ReadFromCoreServer(resultSocket.Content, send, hasResponseData, usePackAndUnpack);

        if (read.IsSuccess) {
            this.GetPipeSocket().setIsSocketError(false);
            result.IsSuccess = true;
            result.Content = read.Content;
            result.Message = StringResources.Language.SuccessText();
            // string tmp2 = BasicFramework.SoftBasic.ByteToHexString( result.Content ) ;
        } else {
            this.GetPipeSocket().setIsSocketError(true);
            if (AlienSession != null) AlienSession.setIsStatusOk(false);
            result.CopyErrorFromOther(read);
        }

        ExtraAfterReadFromCoreServer(read);

        this.GetPipeSocket().PipeLockLeave();
        if (!isPersistentConn) CloseSocket(resultSocket.Content);
        return result;
    }

    /**
     * 将数据发送到当前的网络通道中，并从网络通道中接收一个 {@link INetMessage} 指定的完整的报文，网络通道将根据 {@link NetworkDoubleBase#GetAvailableSocket()} 方法自动获取，本方法是线程安全的。<br />
     * Send data to the current network channel and receive a complete message specified by {@link INetMessage} from the network channel.
     * The network channel will be automatically obtained according to the {@link NetworkDoubleBase#GetAvailableSocket()} method This method is thread-safe.
     * @param send 发送的完整的报文信息
     * @return 接收的完整的报文信息
     */
    public OperateResultExOne<byte[]> ReadFromCoreServer(byte[] send) {
        return ReadFromCoreServer(send, true, true);
    }

    /**
     * @see NetworkDoubleBase#ReadFromCoreServer(byte[])
     */
    public OperateResultExOne<byte[]> ReadFromCoreServer(ArrayList<byte[]> send) {
        ArrayList<Byte> array = new ArrayList<Byte>();
        for (int i = 0; i < send.size(); i++) {
            byte[] data = send.get(i);
            OperateResultExOne<byte[]> read = ReadFromCoreServer(data);
            if (!read.IsSuccess) return read;

            if (read.Content != null) Utilities.ArrayListAddArray(array, read.Content);
        }
        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(array));
    }

    @Override
    public String toString() {
        if (GetNewNetMessage() == null){
            return "NetworkDoubleBase[" + getIpAddress() + ":" + getPort() + "]";
        }
        else {
            return "NetworkDoubleBase<" + GetNewNetMessage().getClass().getTypeName() + ">[" + getIpAddress() + ":" + getPort() + "]";
        }
    }
}
