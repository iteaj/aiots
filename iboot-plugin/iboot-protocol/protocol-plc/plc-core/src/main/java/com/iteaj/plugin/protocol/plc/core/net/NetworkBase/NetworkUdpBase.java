package com.iteaj.plugin.protocol.plc.core.net.NetworkBase;


import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.pipe.PipeSocket;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class NetworkUdpBase extends NetworkBase {
    /**
     * 默认的无参的构造方法
     */
    public NetworkUdpBase() {
        this.pipeSocket = new PipeSocket();
        this.setConnectionId( SoftBasic.GetUniqueStringByGuidAndRandom());
    }

    /**
     * 获取远程服务器的IP地址，如果是本机测试，那么需要设置为127.0.0.1 <br />
     * Get the IP address of the remote server. If it is a local test, then it needs to be set to 127.0.0.1
     *
     * @return Ip地址信息
     */
    public String getIpAddress() {
        return this.pipeSocket.getIpAddress();
    }

    /**
     * 设置远程服务器的IP地址，如果是本机测试，那么需要设置为127.0.0.1 <br />
     * Set the IP address of the remote server. If it is a local test, then it needs to be set to 127.0.0.1
     *
     * @param ipAddress IP地址
     */
    public void setIpAddress(String ipAddress) {
        this.pipeSocket.setIpAddress(ipAddress);
    }

    /**
     * 获取服务器的端口号，具体的值需要取决于对方的配置<br />
     * Gets the port number of the server. The specific value depends on the configuration of the other party.
     *
     * @return 端口
     */
    public int getPort() {
        return this.pipeSocket.getPort();
    }

    /**
     * 设置服务器的端口号，具体的值需要取决于对方的配置<br />
     * Sets the port number of the server. The specific value depends on the configuration of the other party.
     *
     * @param port 端口号
     */
    public void setPort(int port) {
        this.pipeSocket.setPort(port);
    }

    /**
     * 获取接收服务器反馈的时间，如果为负数，则不接收反馈 <br />
     * Gets the time to receive server feedback, and if it is a negative number, does not receive feedback
     * @return 接收超时的信息
     */
    public int getReceiveTimeOut() {
        return receiveTimeOut;
    }

    /**
     * 设置接收服务器反馈的时间，如果为负数，则不接收反馈 <br />
     * Sets the time to receive server feedback, and if it is a negative number, does not receive feedback
     * @param receiveTimeOut 数值
     */
    public void setReceiveTimeOut(int receiveTimeOut) {
        this.receiveTimeOut = receiveTimeOut;
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
     * 获取一次接收时的数据长度，默认2KB数据长度，特殊情况的时候需要调整<br />
     * Gets the length of data received at a time. The default length is 2KB
     * @return 长度大小
     */
    public int getReceiveCacheLength() {
        return receiveCacheLength;
    }

    /**
     * 设置一次接收时的数据长度，默认2KB数据长度，特殊情况的时候需要调整<br />
     * Sets the length of data received at a time. The default length is 2KB
     * @param receiveCacheLength 长度大小信息
     */
    public void setReceiveCacheLength(int receiveCacheLength) {
        this.receiveCacheLength = receiveCacheLength;
    }

    /**
     * 对当前的命令进行打包处理，通常是携带命令头内容，标记当前的命令的长度信息，需要进行重写，否则默认不打包
     * @param command 发送的数据命令内容
     * @return 打包之后的数据结果信息
     */
    protected byte[] PackCommandWithHeader( byte[] command ) {
        return command;
    }

    /**
     * 根据对方返回的报文命令，对命令进行基本的拆包，例如各种Modbus协议拆包为统一的核心报文，还支持对报文的验证
     * @param send 发送的原始报文数据
     * @param response 设备方反馈的原始报文内容
     * @return 返回拆包之后的报文信息，默认不进行任何的拆包操作
     */
    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response ) {
        return OperateResultExOne.CreateSuccessResult( response );
    }

    /**
     * 核心的数据交互读取，发数据发送到通道上去，然后从通道上接收返回的数据
     * @param send 完整的报文内容
     * @return 是否成功的结果对象
     */
    public OperateResultExOne<byte[]> ReadFromCoreServer(byte[] send){
        return ReadFromCoreServer(send,true,true);
    }

    /**
     * 核心的数据交互读取，发数据发送到通道上去，然后从通道上接收返回的数据
     * @param send 完整的报文内容
     * @param hasResponseData 是否有等待的数据返回
     * @param usePackAndUnpack 是否需要对命令重新打包
     * @return 是否成功的结果对象
     */
    public OperateResultExOne<byte[]> ReadFromCoreServer(byte[] send, boolean hasResponseData, boolean usePackAndUnpack ) {
        byte[] sendValue = usePackAndUnpack ? PackCommandWithHeader( send ) : send;
        if (LogNet != null)
            LogNet.WriteDebug(toString(), StringResources.Language.Send() + " : " + SoftBasic.ByteToHexString(sendValue));
        this.pipeSocket.PipeLockEnter();
        try {
            datagramSocket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(getIpAddress());
            datagramPacket = new DatagramPacket(sendValue, sendValue.length, address, getPort());
            datagramSocket.send(datagramPacket);
            if(getReceiveTimeOut() > 0) datagramSocket.setSoTimeout(getReceiveTimeOut());

            if (getReceiveTimeOut() < 0) {
                this.pipeSocket.PipeLockLeave();
                return OperateResultExOne.CreateSuccessResult(new byte[0]);
            }

            if (!hasResponseData)
            {
                this.pipeSocket.PipeLockLeave();
                return OperateResultExOne.CreateSuccessResult( new byte[0] );
            }

            // 对于不存在的IP地址，加入此行代码后，可以在指定时间内解除阻塞模式限制
            byte[] buffer = new byte[getReceiveCacheLength()];
            DatagramPacket recePacket = new DatagramPacket(buffer, buffer.length);
            datagramSocket.receive(recePacket);

            byte[] receive = SoftBasic.BytesArraySelectBegin(buffer, recePacket.getLength());

            this.pipeSocket.PipeLockLeave();

            if (LogNet != null)
                LogNet.WriteDebug(toString(), StringResources.Language.Receive() + " : " + SoftBasic.ByteToHexString(receive));
            connectErrorCount = 0;
            this.pipeSocket.setIsSocketError(false);
            try{
                return usePackAndUnpack ? UnpackResponseContent( sendValue, receive ) : OperateResultExOne.CreateSuccessResult( receive );
            }catch (Exception ex){
                return new OperateResultExOne<byte[]>( "UnpackResponseContent failed: " + ex.getMessage() );
            }
        } catch (Exception ex) {
            this.pipeSocket.ChangePorts();
            this.pipeSocket.setIsSocketError(true);
            if (connectErrorCount < 1_0000_0000) connectErrorCount++;
            this.pipeSocket.PipeLockLeave();
            return new OperateResultExOne<byte[]>(-connectErrorCount, ex.getMessage());
        }
    }


    // region Public Method

    /**
     * 针对当前的设备进行 PING 操作
     * @return 是否可以 PING 通
     * @throws IOException 网络异常
     */
    public boolean IpAddressPing( ) throws IOException {
        return InetAddress.getByName(this.getIpAddress()).isReachable(3000);
    }

    /**
     * 设置当前的网络通道信息
     * @param pipeSocket 网络通道
     */
    public void SetPipeSocket( PipeSocket pipeSocket ) {
        if (this.pipeSocket != null) {
            this.pipeSocket = pipeSocket;
        }
    }

    /**
     * 获取当前的网络管道信息
     * @return 网络管道
     */
    public PipeSocket GetPipeSocket( ) {
        return this.pipeSocket;
    }

    // endregion

    private PipeSocket pipeSocket;                    // 默认的网络管道对象
    private int connectErrorCount = 0;                // 连接错误次数
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private int receiveTimeOut = 10000;                   // 数据接收的超时时间
    private String connectionId = "";                     // 当前连接
    private int receiveCacheLength = 2048;                // 接收的长度限制

    @Override
    public String toString() {
        return "NetworkUdpBase[" + getIpAddress() + ":" + getPort() + "]";
    }
}
