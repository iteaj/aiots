package com.iteaj.plugin.protocol.plc.core.net.NetworkBase;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.SpecifiedCharacterMessage;
import com.iteaj.plugin.protocol.plc.core.net.Protocol;
import com.iteaj.plugin.protocol.plc.core.net.NetSupport;
import com.iteaj.plugin.protocol.plc.core.log.core.ILogNet;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.log.core.MessageDegree;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;


/**
 * 本系统所有网络类的基类，该类为抽象类，无法进行实例化<br />
 * network base class, support basic operation with socket
 */
public abstract class NetworkBase {

    // region Constructor
    /**
     * 实例化一个NetworkBase对象，令牌的默认值为空，都是0x00<br />
     * Instantiate a NetworkBase object, the default value of the token is empty, both are 0x00
     */
    public NetworkBase( )
    {
        Token = UUID.fromString("00000000-0000-0000-0000-000000000000");
    }

    // endregion

    // region Public Properties

    /**
     * 组件的日志工具，支持日志记录，只要实例化后，当前网络的基本信息，就以 {@link MessageDegree HslMessageDegree.Debug} 等级进行输出<br />
     * The component's logging tool supports logging. As long as the instantiation of the basic network information, the output will be output at {@link MessageDegree HslMessageDegree.Debug} "
     */
    public ILogNet LogNet = null;

    /**
     * 网络类的身份令牌，在hsl协议的模式下会有效，在和设备进行通信的时候是无效的<br />
     * Network-type identity tokens will be valid in the hsl protocol mode and will not be valid when communicating with the device
     */
    public UUID Token = null;

    // endregion

    // region Protect Member

    /**
     * 对客户端而言是的通讯用的套接字，对服务器来说是用于侦听的套接字<br />
     * A communication socket for the client, or a listening socket for the server
     */
    protected Socket CoreSocket = null;

    /**
     * 文件传输的时候的缓存大小，直接影响传输的速度，值越大，传输速度越快，越占内存，默认为100K大小<br />
     * The size of the cache during file transfer directly affects the speed of the transfer. The larger the value, the faster the transfer speed and the more memory it takes. The default size is 100K.
     */
    protected int fileCacheSize = 1024 * 100;

    // endregion

    // region Protect Method

    /**
     * 设置当前的日志对象信息<br />
     * Set the current log object information
     * @param logNet 日志对象
     */
    public void setLogNet(ILogNet logNet){
        this.LogNet = logNet;
    }

    /**
     * 检查网络套接字是否操作超时，传入的参数需要是 {@link TimeOut} 类型，封装socket操作。<br />
     * Check if the operation of the network socket has timed out. The parameters passed in need to be of type {@link TimeOut} to encapsulate the socket operation.
     * @param timeout HslTimeOut的对象
     */
    protected void ThreadPoolCheckTimeOut( TimeOut timeout ) {
        System.out.println("进入超时检测:" + timeout.DelayTime);
        while (!timeout.IsSuccessful) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            if (new Date().getTime() - timeout.StartTime.getTime() > timeout.DelayTime) {
                // 连接超时或是验证超时
                if (!timeout.IsSuccessful) {
                    System.out.println("检测到超时信息");
                    timeout.IsTimeout = true;
                    ILogNet logNet = LogNet;
                    if (logNet != null)
                        logNet.WriteWarn(toString(), "Wait Time Out : " + timeout.DelayTime);
                    CloseSocket(timeout.WorkSocket);
                }
                break;
            }
        }
    }

    // endregion

    // region Receive Content

    /**
     * 接收固定长度的字节数组，需要指定超时时间，当length大于0时，接收固定长度的数据内容，当length小于0时，接收不大于1024长度的随机数据信息<br />
     * To receive a fixed-length byte array, you need to specify the timeout period. When the length is greater than 0, the fixed-length data content is received.
     * When the length is less than 0, the random data information with a length not greater than 1024 is received.
     * @param socket 网络通讯的套接字，Network communication socket
     * @param length 准备接收的数据长度，当length大于0时，接收固定长度的数据内容，当length小于0时，接收不大于1024长度的随机数据信息
     * @param timeout 单位：毫秒，超时时间，默认为60秒，如果设置小于0，则不检查超时时间
     * @param reportProgress 当前接收数据的进度报告，有些协议支持传输非常大的数据内容，可以给与进度提示的功能
     * @return 包含了字节数据的结果类
     */
    protected OperateResultExOne<byte[]> Receive(Socket socket, int length, int timeout, ActionOperateExTwo<Long, Long> reportProgress ) {
        if (length == 0) return OperateResultExOne.CreateSuccessResult(new byte[0]);

        try {
            if(timeout < 0) timeout = 0;
            socket.setSoTimeout(timeout);
            if (length > 0) {
                byte[] data = NetSupport.ReadBytesFromSocket(socket, length, reportProgress);
                return OperateResultExOne.CreateSuccessResult(data);
            } else {
                byte[] bytes_receive = new byte[1024];
                InputStream input = socket.getInputStream();
                int receive_current = input.read(bytes_receive, 0, bytes_receive.length);

                if(receive_current <= 0) throw new RemoteCloseException();
                return OperateResultExOne.CreateSuccessResult(SoftBasic.BytesArraySelectBegin(bytes_receive, receive_current));
            }
        } catch (RemoteCloseException ex) {
            CloseSocket(socket);
            return new OperateResultExOne<byte[]>(StringResources.Language.RemoteClosedConnection());
        } catch (IOException ex) {
            CloseSocket(socket);
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    /**
     * 接收固定长度的字节数组，需要指定超时时间，当length大于0时，接收固定长度的数据内容，当length小于0时，接收不大于1024长度的随机数据信息<br />
     * To receive a fixed-length byte array, you need to specify the timeout period. When the length is greater than 0, the fixed-length data content is received.
     * When the length is less than 0, the random data information with a length not greater than 1024 is received.
     * @param socket 网络通讯的套接字，Network communication socket
     * @param length 准备接收的数据长度，当length大于0时，接收固定长度的数据内容，当length小于0时，接收不大于1024长度的随机数据信息
     * @param timeout 单位：毫秒，超时时间，默认为60秒，如果设置小于0，则不检查超时时间
     * @return 包含了字节数据的结果类
     */
    protected OperateResultExOne<byte[]> Receive(Socket socket, int length, int timeout ) {
        return Receive(socket, length, timeout, null);
    }

    /**
     * 接收固定长度的字节数组，允许指定超时时间，默认为60秒，当length大于0时，接收固定长度的数据内容，当length小于0时，接收不大于1024长度的随机数据信息<br />
     * Receiving a fixed-length byte array, allowing a specified timeout time. The default is 60 seconds. When length is greater than 0,
     * fixed-length data content is received. When length is less than 0, random data information of a length not greater than 1024 is received.
     * @param socket 网络通讯的套接字，Network communication socket
     * @param length 准备接收的数据长度，当length大于0时，接收固定长度的数据内容，当length小于0时，接收不大于1024长度的随机数据信息
     * @return 包含了字节数据的结果类
     */
    protected OperateResultExOne<byte[]> Receive(Socket socket, int length ) {
        return Receive(socket, length, 60_000, null);
    }

    /**
     * 接收一行命令数据，需要自己指定这个结束符，指定超时时间<br />
     * To receive a line of command data, you need to specify the terminator yourself. and also the timeout value
     * @param socket 网络套接字
     * @param endCode 结束符信息
     * @param timeout 超时时间，单位毫秒
     * @return 带有结果对象的数据信息
     */
    protected OperateResultExOne<byte[]> ReceiveCommandLineFromSocket( Socket socket, byte endCode, int timeout ) {
        ArrayList<Byte> bufferArray = new ArrayList<Byte>();
        try {
            Date st = new Date();
            boolean bOK = false;

            // 接收到endCode为止，此处的超时是针对是否接收到endCode为止的
            while (new Date().getTime() - st.getTime() < timeout) {
                OperateResultExOne<byte[]> headResult = Receive(socket, 1, timeout);
                if (!headResult.IsSuccess) return headResult;

                bufferArray.add(headResult.Content[0]);
                if (headResult.Content[0] == endCode) {
                    bOK = true;
                    break;
                }
            }

            if (!bOK) return new OperateResultExOne<byte[]>(StringResources.Language.ReceiveDataTimeout());

            // 指令头已经接收完成
            return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(bufferArray));
        } catch (Exception ex) {
            CloseSocket(socket);
            return new OperateResultExOne<byte[]>(ex.getMessage());
        }
    }

    /**
     * 接收一行命令数据，需要自己指定这个结束符，指定超时时间<br />
     * To receive a line of command data, you need to specify the terminator yourself. and also the timeout value
     * @param socket 网络套接字
     * @param endCode 结束符信息
     * @return 带有结果对象的数据信息
     */
    protected OperateResultExOne<byte[]> ReceiveCommandLineFromSocket( Socket socket, byte endCode ) {
        return ReceiveCommandLineFromSocket(socket, endCode, Integer.MAX_VALUE);
    }

    /**
     *  接收一行命令数据，需要自己指定这个结束符，指定超时时间，单位是毫秒<br />
     *  To receive a line of command data, you need to specify the terminator yourself. and also the timeout value
     * @param socket 网络套接字
     * @param endCode1 结束符1信息
     * @param endCode2 结束符2信息
     * @param timeout 超时时间，默认无穷大，单位毫秒
     * @return 带有结果对象的数据信息
     */
    protected OperateResultExOne<byte[]> ReceiveCommandLineFromSocket( Socket socket, byte endCode1, byte endCode2, int timeout ){
        ArrayList<Byte> bufferArray = new ArrayList<Byte>( );
        try
        {
            Date st = new Date();
            boolean bOK = false;
            // 接收到endCode为止
            while (new Date().getTime() - st.getTime() < timeout) {
                OperateResultExOne<byte[]> headResult = Receive(socket, 1, timeout);
                if (!headResult.IsSuccess) return headResult;

                bufferArray.add(headResult.Content[0]);
                if (headResult.Content[0] == endCode2) {
                    if (bufferArray.size() > 1 && bufferArray.get(bufferArray.size() - 2) == endCode1) {
                        bOK = true;
                        break;
                    }
                }
            }

            if (!bOK) return new OperateResultExOne<byte[]>( StringResources.Language.ReceiveDataTimeout() );

            // 指令头已经接收完成
            return OperateResultExOne.CreateSuccessResult( Utilities.getBytes(bufferArray) );
        }
        catch (Exception ex)
        {
            CloseSocket(socket);
            return new OperateResultExOne<byte[]>( ex.getMessage() );
        }
    }

    /**
     *  接收一行命令数据，需要自己指定这个结束符，默认超时时间60秒，单位是毫秒<br />
     *  To receive a line of command data, you need to specify the terminator yourself. The default timeout is 60 seconds, which is 60,000, in milliseconds.
     * @param socket 网络套接字
     * @param endCode1 结束符1信息
     * @param endCode2 结束符2信息
     * @return 带有结果对象的数据信息
     */
    protected OperateResultExOne<byte[]> ReceiveCommandLineFromSocket( Socket socket, byte endCode1, byte endCode2 ){
        return ReceiveCommandLineFromSocket(socket, endCode1, endCode2, 60_000);
    }

    /**
     * 接收一条完整的 {@link INetMessage} 数据内容，需要指定超时时间，单位为毫秒。 <br />
     * Receive a complete {@link INetMessage} data content, Need to specify a timeout period in milliseconds
     * @param socket 网络的套接字
     * @param timeOut 超时时间，单位：毫秒
     * @param netMessage 消息的格式定义
     * @param reportProgress 接收消息的时候的进度报告
     * @return 带有是否成功的byte数组对象
     */
    protected OperateResultExOne<byte[]> ReceiveByMessage( Socket socket, int timeOut, INetMessage netMessage, ActionOperateExTwo<Long, Long> reportProgress ) {
        if (netMessage == null) return Receive(socket, -1, timeOut);

        if (netMessage.ProtocolHeadBytesLength() < 0){
            // 表示接收的是固定的结束字符
            byte[] headCode = Utilities.getBytes( netMessage.ProtocolHeadBytesLength() );
            int codeLength = headCode[3] & 0x0f;
            OperateResultExOne<byte[]> receive = null;
            if      (codeLength == 1) receive = ReceiveCommandLineFromSocket( socket, headCode[1], timeOut );
            else if (codeLength == 2) receive = ReceiveCommandLineFromSocket( socket, headCode[1], headCode[0], timeOut );

            if (receive == null) return new OperateResultExOne<byte[]>( "Receive by specified code failed, length check failed" );
            if (!receive.IsSuccess) return receive;

            netMessage.setHeadBytes( receive.Content );
            if (netMessage instanceof SpecifiedCharacterMessage)
            {
                SpecifiedCharacterMessage message = (SpecifiedCharacterMessage)netMessage;
                if (message.getEndLength() == 0) return receive;
                OperateResultExOne<byte[]> endResult = Receive( socket, message.getEndLength(), timeOut );
                if (!endResult.IsSuccess) return endResult;

                return OperateResultExOne.CreateSuccessResult( SoftBasic.SpliceTwoByteArray( receive.Content, endResult.Content ) );
            }
            return receive;
        }
        else {
            // 表示接收的数据是固定长度+可变长度组成的数据
            OperateResultExOne<byte[]> headResult = Receive(socket, netMessage.ProtocolHeadBytesLength(), timeOut);
            if (!headResult.IsSuccess) return headResult;

            netMessage.setHeadBytes(headResult.Content);
            int contentLength = netMessage.GetContentLengthByHeadBytes();
            if (contentLength <= 0) return headResult;

            OperateResultExOne<byte[]> contentResult = Receive(socket, contentLength, timeOut, reportProgress);
            if (!contentResult.IsSuccess) return contentResult;

            netMessage.setContentBytes(contentResult.Content);
            return OperateResultExOne.CreateSuccessResult(SoftBasic.SpliceTwoByteArray(netMessage.getHeadBytes(), contentResult.Content));
        }
    }

    /**
     * {@link NetworkBase#ReceiveByMessage(Socket, int, byte[], INetMessage)}
     */
    protected OperateResultExOne<byte[]> ReceiveByMessage( Socket socket, int timeOut, INetMessage netMessage ) {
        return ReceiveByMessage( socket, timeOut, null, netMessage );
    }

    /**
     * 接收一条完整的 {@link INetMessage} 数据内容，需要指定超时时间，单位为毫秒。 <br />
     * Receive a complete {@link INetMessage} data content, Need to specify a timeout period in milliseconds
     * @param socket 网络的套接字
     * @param timeOut 超时时间，单位：毫秒
     * @param sendValue 发送的数据内容
     * @param netMessage 消息的格式定义
     * @return 带有是否成功的byte数组对象
     */
    protected OperateResultExOne<byte[]> ReceiveByMessage( Socket socket, int timeOut, byte[] sendValue, INetMessage netMessage ) {
        if (netMessage != null && netMessage.ProtocolHeadBytesLength() == -1) {
            Date startTime = new Date();                                                               // 加入超时，防止下面的数据接收，无限循环接收
            MemoryStream ms = new MemoryStream();
            INetMessage nullTmp = null;
            while (true) {
                OperateResultExOne<byte[]> read = ReceiveByMessage(socket, timeOut, nullTmp, null);
                if (!read.IsSuccess) return read;

                if (read.Content != null && read.Content.length > 0) {
                    ms.Write(read.Content);
                    //logMessage?.Invoke( read.Content );                             // 如果需要日志记录
                }

                if (netMessage.CheckReceiveDataComplete(sendValue, ms))
                    return OperateResultExOne.CreateSuccessResult(ms.ToArray());  // 根据消息类的重写情况，进行二次验证

                // 一直不完整的话，引发超时
                if (timeOut >= 0 && (new Date().getTime() - startTime.getTime()) > timeOut)
                    return new OperateResultExOne<byte[]>(StringResources.Language.ReceiveDataTimeout() + timeOut + " Received: " + SoftBasic.ByteToHexString(ms.ToArray(), ' '));
            }
        } else {
            return ReceiveByMessage(socket, timeOut, netMessage, null);
        }
    }

    // endregion

    // region Send Content

    /**
     * 发送消息给套接字，直到完成的时候返回，经过测试，本方法是线程安全的。<br />
     * Send a message to the socket until it returns when completed. After testing, this method is thread-safe.
     * @param socket 网络套接字
     * @param data 数据
     * @return 是否发送成功
     */
    protected OperateResult Send(Socket socket,byte[] data){
        if (data == null) return OperateResult.CreateSuccessResult( );
        return Send( socket, data, 0, data.length );
    }

    /**
     * 发送消息给套接字，直到完成的时候返回，经过测试，本方法是线程安全的。<br />
     * Send a message to the socket until it returns when completed. After testing, this method is thread-safe.
     * @param socket 网络套接字
     * @param data 字节数据
     * @param offset 偏移的位置信息
     * @param size 发送的数据总数
     * @return 发送是否成功的结果
     */
    protected OperateResult Send( Socket socket, byte[] data, int offset, int size ) {
        if (data == null) return OperateResult.CreateSuccessResult();
        try {
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.write(data, offset, size);
            return OperateResult.CreateSuccessResult();
        } catch (IOException ex) {
            CloseSocket(socket);
            return new OperateResult(ex.getMessage());
        }
    }
    // endregion

    // region Socket Connect

    /**
     * 创建一个新的socket对象并连接到远程的地址，需要指定ip地址以及端口号信息，还有超时时间，单位是毫秒<br />
     * To create a new socket object and connect to a remote address, you need to specify the IP address and port number information, and the timeout period in milliseconds
     * @param endPoint 目标节点
     * @param timeOut 超时时间
     * @return 连接成功的标志
     */
    protected  OperateResultExOne<Socket> CreateSocketAndConnect(SocketAddress endPoint, int timeOut){
        Socket socket = new Socket();
        try {
            socket.connect(endPoint,timeOut);
            return OperateResultExOne.CreateSuccessResult(socket);
        }
        catch (IOException ex)
        {
            if(LogNet != null) LogNet.WriteException( "CreateSocketAndConnect", ex );
            CloseSocket(socket);
            return new OperateResultExOne<>(ex.getMessage());
        }
    }

    /**
     * 创建一个新的socket对象并连接到远程的地址，需要指定ip地址以及端口号信息，还有超时时间，单位是毫秒<br />
     * To create a new socket object and connect to a remote address, you need to specify the IP address and port number information, and the timeout period in milliseconds
     * @param ipAddress ip地址
     * @param port 端口号
     * @param timeOut 超时时间
     * @return 连接成功的标志
     */
    protected  OperateResultExOne<Socket> CreateSocketAndConnect(String ipAddress,int port, int timeOut) {
        SocketAddress endPoint = new InetSocketAddress(ipAddress,port);
        return CreateSocketAndConnect(endPoint,timeOut);
    }

    /**
     * 创建一个新的socket对象并连接到远程的地址，默认超时时间为10秒钟，需要指定ip地址以及端口号信息<br />
     * Create a new socket object and connect to the remote address. The default timeout is 10 seconds. You need to specify the IP address and port number.
     * @param ipAddress ip地址
     * @param port 端口号
     * @return 连接成功的标志
     */
    protected  OperateResultExOne<Socket> CreateSocketAndConnect(String ipAddress,int port) {
        SocketAddress endPoint = new InetSocketAddress(ipAddress,port);
        return CreateSocketAndConnect(endPoint,10000);
    }

    // endregion

    // region Read Write Stream

    /**
     * 读取流中的数据到缓存区，读取的长度需要按照实际的情况来判断<br />
     * Read the data in the stream to the buffer area. The length of the read needs to be determined according to the actual situation.
     * @param stream 流数据
     * @param buffer 缓冲数据
     * @return 带有成功标志的读取数据长度
     */
    protected OperateResultExOne<Integer> ReadStream(InputStream stream, byte[] buffer) {
        try {
            int read_count = stream.read(buffer, 0, buffer.length);
            return OperateResultExOne.CreateSuccessResult(read_count);
        } catch (IOException ex) {
            return new OperateResultExOne<>(ex.getMessage());
        }
    }

    /**
     * 将缓冲区的数据写入到流里面去<br />
     * Write the buffer data to the stream
     * @param stream 字节流
     * @param buffer 缓存数据
     * @return 写入是否成功
     */
    protected OperateResult WriteStream(OutputStream stream, byte[] buffer ) {
        try {
            stream.write(buffer, 0, buffer.length);
            return OperateResult.CreateSuccessResult();
        } catch (IOException ex) {
            return new OperateResult(ex.getMessage());
        }
    }

    // endregion

    // region Token Check

    /**
     * 检查当前的头子节信息的令牌是否是正确的，仅用于某些特殊的协议实现<br />
     * Check whether the token of the current header subsection information is correct, only for some special protocol implementations
     * @param headBytes 头子节数据
     * @return 令牌是验证成功
     */
    protected boolean CheckRemoteToken( byte[] headBytes ) {
        return SoftBasic.IsByteTokenEquel(headBytes, Token);
    }

    //endregion

    // region Special Bytes Send

    /**
     * [自校验] 发送字节数据并确认对方接收完成数据，如果结果异常，则结束通讯<br />
     * [Self-check] Send the byte data and confirm that the other party has received the completed data. If the result is abnormal, the communication ends.
     * @param socket 网络套接字
     * @param headCode 头指令
     * @param customer 用户指令
     * @param send 发送的数据
     * @return 是否发送成功
     */
    protected OperateResult SendBaseAndCheckReceive( Socket socket, int headCode, int customer, byte[] send ) {
        // 数据处理
        send = Protocol.CommandBytes(headCode, customer, Token, send);

        // 发送数据
        OperateResult sendResult = Send(socket, send);
        if (!sendResult.IsSuccess) return sendResult;

        // 检查对方接收完成
        OperateResultExOne<Long> checkResult = ReceiveLong(socket);
        if (!checkResult.IsSuccess) return checkResult;

        // 检查长度接收
        if (checkResult.Content != send.length) {
            CloseSocket(socket);
            return new OperateResult(StringResources.Language.CommandLengthCheckFailed());
        }

        return checkResult;
    }

    /**
     * [自校验] 发送字节数据并确认对方接收完成数据，如果结果异常，则结束通讯<br />
     * [Self-check] Send the byte data and confirm that the other party has received the completed data. If the result is abnormal, the communication ends.
     * @param socket 网络套接字
     * @param customer 用户指令
     * @param send 发送的数据
     * @return 是否发送成功
     */
    protected OperateResult SendBytesAndCheckReceive( Socket socket, int customer, byte[] send ) {
        return SendBaseAndCheckReceive(socket, Protocol.ProtocolUserBytes, customer, send);
    }

    /**
     * [自校验] 直接发送字符串数据并确认对方接收完成数据，如果结果异常，则结束通讯<br />
     * [Self-checking] Send string data directly and confirm that the other party has received the completed data. If the result is abnormal, the communication ends.
     * @param socket 网络套接字
     * @param customer 用户指令
     * @param send 发送的数据
     * @return 是否发送成功
     */
    protected OperateResult SendStringAndCheckReceive( Socket socket, int customer, String send ) {
        byte[] data = Utilities.IsStringNullOrEmpty(send) ? null : Utilities.csharpString2Byte(send);

        return SendBaseAndCheckReceive(socket, Protocol.ProtocolUserString, customer, data);
    }

    /**
     * [自校验] 直接发送字符串数组并确认对方接收完成数据，如果结果异常，则结束通讯<br />
     * [Self-check] Send string array directly and confirm that the other party has received the completed data. If the result is abnormal, the communication ends.
     * @param socket 网络套接字
     * @param customer 用户指令
     * @param sends 发送的字符串数组
     * @return 是否发送成功
     */
    protected OperateResult SendStringAndCheckReceive( Socket socket, int customer, String[] sends ) {
        return SendBaseAndCheckReceive(socket, Protocol.ProtocolUserStringArray, customer, Protocol.PackStringArrayToByte(sends));
    }

    /**
     * [自校验] 直接发送字符串数组并确认对方接收完成数据，如果结果异常，则结束通讯<br />
     * [Self-check] Send string array directly and confirm that the other party has received the completed data. If the result is abnormal, the communication ends.
     * @param socket 网络套接字
     * @param customer 用户指令
     * @param name 用户名
     * @param pwd 密码
     * @return 是否发送成功
     */
    protected OperateResult SendAccountAndCheckReceive( Socket socket, int customer, String name, String pwd ) {
        return SendBaseAndCheckReceive(socket, Protocol.ProtocolAccountLogin, customer, Protocol.PackStringArrayToByte(new String[]{name, pwd}));
    }

    /**
     * [自校验] 接收一条完整的同步数据，包含头子节和内容字节，基础的数据，如果结果异常，则结束通讯<br />
     * [Self-checking] Receive a complete synchronization data, including header subsection and content bytes, basic data, if the result is abnormal, the communication ends
     * @param socket 套接字
     * @param timeout 超时时间设置，如果为负数，则不检查超时
     * @return 包含是否成功的结果对象
     */
    protected OperateResultExTwo<byte[], byte[]> ReceiveAndCheckBytes(Socket socket, int timeout ) {
        // 接收头指令
        OperateResultExOne<byte[]> headResult = Receive(socket, Protocol.HeadByteLength, timeout);
        if (!headResult.IsSuccess) return OperateResultExTwo.CreateFailedResult(headResult);

        // 检查令牌
        if (!CheckRemoteToken(headResult.Content)) {
            CloseSocket(socket);
            return new OperateResultExTwo(StringResources.Language.TokenCheckFailed());
        }

        int contentLength = Utilities.getInt(headResult.Content, Protocol.HeadByteLength - 4);
        // 接收内容
        OperateResultExOne<byte[]> contentResult = Receive(socket, contentLength, timeout);
        if (!contentResult.IsSuccess) return OperateResultExTwo.CreateFailedResult(contentResult);

        // 返回成功信息
        OperateResult checkResult = SendLong(socket, Protocol.HeadByteLength + contentLength);
        if (!checkResult.IsSuccess) return OperateResultExTwo.CreateFailedResult(checkResult);

        byte[] head = headResult.Content;
        byte[] content = contentResult.Content;
        content = Protocol.CommandAnalysis(head, content);
        return OperateResultExTwo.CreateSuccessResult(head, content);
    }

    /**
     * [自校验] 从网络中接收一个字符串数据，如果结果异常，则结束通讯<br />
     * [Self-checking] Receive a string of data from the network. If the result is abnormal, the communication ends.
     * @param socket 套接字
     * @return 包含是否成功的结果对象
     */
    protected OperateResultExTwo<Integer, String> ReceiveStringContentFromSocket( Socket socket )
    {
        return ReceiveStringContentFromSocket(socket, 30_000);
    }

    /**
     * [自校验] 从网络中接收一个字符串数据，如果结果异常，则结束通讯<br />
     * [Self-checking] Receive a string of data from the network. If the result is abnormal, the communication ends.
     * @param socket 套接字
     * @param timeout 超时时间
     * @return 包含是否成功的结果对象
     */
    protected OperateResultExTwo<Integer, String> ReceiveStringContentFromSocket( Socket socket, int timeout )
    {
        OperateResultExTwo<byte[], byte[]> receive = ReceiveAndCheckBytes( socket, timeout );
        if (!receive.IsSuccess) return OperateResultExTwo.CreateFailedResult( receive );

        // 检查是否是字符串信息
        if (Utilities.getInt( receive.Content1, 0 ) != Protocol.ProtocolUserString)
        {
            if (LogNet != null) LogNet.WriteError( toString( ), StringResources.Language.CommandHeadCodeCheckFailed() );
            CloseSocket(socket);
            return new OperateResultExTwo<>( StringResources.Language.CommandHeadCodeCheckFailed() );
        }

        if (receive.Content2 == null) receive.Content2 = new byte[0];
        // 分析数据
        return OperateResultExTwo.CreateSuccessResult( Utilities.getInt( receive.Content1, 4 ), Utilities.byte2CSharpString( receive.Content2 ) );
    }

    /**
     * [自校验] 从网络中接收一个字符串数组，如果结果异常，则结束通讯<br />
     * [Self-check] Receive an array of strings from the network. If the result is abnormal, the communication ends.
     * @param socket 套接字
     * @return 包含是否成功的结果对象
     */
    protected OperateResultExTwo<Integer, String[]> ReceiveStringArrayContentFromSocket( Socket socket ) {
        return ReceiveStringArrayContentFromSocket(socket, 30_000);
    }

    /**
     * [自校验] 从网络中接收一个字符串数组，如果结果异常，则结束通讯<br />
     * [Self-check] Receive an array of strings from the network. If the result is abnormal, the communication ends.
     * @param socket 套接字
     * @return 包含是否成功的结果对象
     */
    protected OperateResultExTwo<Integer, String[]> ReceiveStringArrayContentFromSocket( Socket socket, int timeout ) {
        OperateResultExTwo<byte[], byte[]> receive = ReceiveAndCheckBytes(socket, timeout);
        if (!receive.IsSuccess) return OperateResultExTwo.CreateFailedResult(receive);

        // 检查是否是字符串信息
        if (Utilities.getInt(receive.Content1, 0) != Protocol.ProtocolUserStringArray) {
            if (LogNet != null) LogNet.WriteError(toString(), StringResources.Language.CommandHeadCodeCheckFailed());
            CloseSocket(socket);
            return new OperateResultExTwo<>(StringResources.Language.CommandHeadCodeCheckFailed());
        }

        if (receive.Content2 == null) receive.Content2 = new byte[4];
        return OperateResultExTwo.CreateSuccessResult(Utilities.getInt(receive.Content1, 4), Protocol.UnPackStringArrayFromByte(receive.Content2));
    }

    /**
     * [自校验] 从网络中接收一串字节数据，如果结果异常，则结束通讯<br />
     * [Self-checking] Receive a string of byte data from the network. If the result is abnormal, the communication ends.
     * @param socket 套接字的网络
     * @return 包含是否成功的结果对象
     */
    protected OperateResultExTwo<Integer, byte[]> ReceiveBytesContentFromSocket( Socket socket )
    {
       return ReceiveBytesContentFromSocket(socket, 30000);
    }

    /**
     * [自校验] 从网络中接收一串字节数据，如果结果异常，则结束通讯<br />
     * [Self-checking] Receive a string of byte data from the network. If the result is abnormal, the communication ends.
     * @param socket 套接字的网络
     * @param timeout 超时时间
     * @return 包含是否成功的结果对象
     */
    protected OperateResultExTwo<Integer, byte[]> ReceiveBytesContentFromSocket( Socket socket, int timeout ) {
        OperateResultExTwo<byte[], byte[]> receive = ReceiveAndCheckBytes(socket, timeout);
        if (!receive.IsSuccess) return OperateResultExTwo.CreateFailedResult(receive);

        // 检查是否是字节信息
        if (Utilities.getInt(receive.Content1, 0) != Protocol.ProtocolUserBytes) {
            if (LogNet != null) LogNet.WriteError(toString(), StringResources.Language.CommandHeadCodeCheckFailed());
            CloseSocket(socket);
            return new OperateResultExTwo(StringResources.Language.CommandHeadCodeCheckFailed());
        }

        // 分析数据
        return OperateResultExTwo.CreateSuccessResult(Utilities.getInt(receive.Content1, 4), receive.Content2);
    }

    /**
     * 从网络中接收Long数据<br />
     * Receive Long data from the network
     * @param socket 套接字
     * @return 是否成功的类型
     */
    private OperateResultExOne<Long> ReceiveLong( Socket socket ) {
        OperateResultExOne<byte[]> read = Receive(socket, 8);
        if (read.IsSuccess) {
            return OperateResultExOne.CreateSuccessResult(Utilities.getLong(read.Content, 0));
        } else {
            return new OperateResultExOne<Long>(read.Message);
        }
    }

    /**
     * 将long数据发送到套接字<br />
     * Send long data to the socket
     * @param socket 套接字
     * @param value 值
     * @return 是否成功
     */
    private OperateResult SendLong( Socket socket, long value ) {
        return Send(socket, Utilities.getBytes(value));
    }

    // endregion

    // region Protect

    /**
     * 安全的关闭一个套接字
     * @param socket 网络套接字
     */
    protected void CloseSocket(Socket socket){
        NetSupport.CloseSocket(socket);
    }

    // endregion

    // region Stream Socket Write Read

    /**
     * 发送一个流的所有数据到指定的网络套接字，需要指定发送的数据长度，支持按照百分比的进度报告<br />
     * Send all the data of a stream to the specified network socket. You need to specify the length of the data to be sent. It supports the progress report in percentage.
     * @param socket 套接字
     * @param stream 内存流
     * @param receive 发送的数据长度
     * @param report 进度报告的委托
     * @param reportByPercent 进度报告是否按照百分比报告
     * @return 是否成功的结果对象
     */
    protected OperateResult SendStreamToSocket( Socket socket, InputStream stream, long receive, ActionOperateExTwo<Long, Long> report, boolean reportByPercent ) {
        byte[] buffer = new byte[fileCacheSize]; // 100K的数据缓存池
        long SendTotal = 0;
        long percent = 0;

        while (SendTotal < receive) {
            // 先从流中接收数据
            OperateResultExOne<Integer> read = ReadStream(stream, buffer);
            if (!read.IsSuccess) {
                CloseSocket(socket);
                return read;
            }

            SendTotal += read.Content;
            // 然后再异步写到socket中
            byte[] newBuffer = new byte[read.Content];
            System.arraycopy(buffer, 0, newBuffer, 0, newBuffer.length);
            OperateResult write = SendBytesAndCheckReceive(socket, read.Content, newBuffer);
            if (!write.IsSuccess) {
                CloseSocket(socket);
                return write;
            }
            // 报告进度
            if (reportByPercent) {
                long percentCurrent = SendTotal * 100 / receive;
                if (percent != percentCurrent) {
                    percent = percentCurrent;
                    if (report != null) report.Action(SendTotal, receive);
                }
            } else {
                // 报告进度
                if (report != null) report.Action(SendTotal, receive);
            }
        }

        return OperateResult.CreateSuccessResult();
    }

    /**
     * 从套接字中接收所有的数据然后写入到指定的流当中去，需要指定数据的长度，支持按照百分比进行进度报告<br />
     * Receives all data from the socket and writes it to the specified stream. The length of the data needs to be specified, and progress reporting is supported in percentage.
     * @param socket 套接字
     * @param stream 数据流
     * @param totalLength 所有数据的长度
     * @param report 进度报告
     * @param reportByPercent 进度报告是否按照百分比
     * @return 是否成功的结果对象
     */
    protected OperateResult WriteStreamFromSocket( Socket socket, OutputStream stream, long totalLength, ActionOperateExTwo<Long, Long> report, boolean reportByPercent ) {
        long count_receive = 0;
        long percent = 0;
        while (count_receive < totalLength) {
            // 先从流中异步接收数据
            OperateResultExTwo<Integer, byte[]> read = ReceiveBytesContentFromSocket(socket, 60_000);
            if (!read.IsSuccess) return read;

            count_receive += read.Content1;
            // 开始写入文件流
            OperateResult write = WriteStream(stream, read.Content2);
            if (!write.IsSuccess) {
                CloseSocket(socket);
                return write;
            }

            // 报告进度
            if (reportByPercent) {
                long percentCurrent = count_receive * 100 / totalLength;
                if (percent != percentCurrent) {
                    percent = percentCurrent;
                    if (report != null) report.Action(count_receive, totalLength);
                }
            } else {
                if (report != null) report.Action(count_receive, totalLength);
            }
        }
        return OperateResult.CreateSuccessResult();
    }

    // endregion

    // region HslMessage Receive

    /**
     * 接收一条hsl协议的数据信息，自动解析，解压，解码操作，获取最后的实际的数据，接收结果依次为暗号，用户码，负载数据<br />
     * Receive a piece of hsl protocol data information, automatically parse, decompress, and decode operations to obtain the last actual data.
     * The result is a opCode, user code, and payload data in order.
     * @param socket 网络套接字
     * @return 接收结果，依次为暗号，用户码，负载数据
     */
    protected OperateResultExThree<Integer, Integer, byte[]> ReceiveHslMessage( Socket socket ) {
        OperateResultExOne<byte[]> receiveHead = Receive(socket, Protocol.HeadByteLength, 10_000);
        if (!receiveHead.IsSuccess) return OperateResultExThree.CreateFailedResult(receiveHead);

        int receive_length = Utilities.getInt(receiveHead.Content, receiveHead.Content.length - 4);
        OperateResultExOne<byte[]> receiveContent = Receive(socket, receive_length);
        if (!receiveContent.IsSuccess) return OperateResultExThree.CreateFailedResult(receiveContent);

        byte[] Content = Protocol.CommandAnalysis(receiveHead.Content, receiveContent.Content);
        int protocol = Utilities.getInt(receiveHead.Content, 0);
        int customer = Utilities.getInt(receiveHead.Content, 4);
        return OperateResultExThree.CreateSuccessResult(protocol, customer, Content);
    }
    // endregion

    // region Object Override

    /**
     * 返回当前对象表示的字符串<br />
     * Returns the string represented by the current object
     * @return 字符串信息
     */
    @Override
    public String toString(){
        return "NetworkBase";
    }

    // endregion

}
