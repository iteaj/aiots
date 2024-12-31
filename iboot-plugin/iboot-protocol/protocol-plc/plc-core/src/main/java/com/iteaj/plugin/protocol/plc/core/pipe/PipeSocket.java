package com.iteaj.plugin.protocol.plc.core.pipe;

import com.iteaj.iot.event.ClientStatus;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.adapt.PlcProtocolAbstract;
import com.iteaj.plugin.protocol.plc.core.net.NetSupport;
import com.iteaj.plugin.protocol.plc.core.types.PlcHelper;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * 基于网络通信的管道信息，可以设置额外的一些参数信息，例如连接超时时间，读取超时时间等等。<br />
 * Based on the pipe information of network communication, some additional parameter information can be set, such as connection timeout time, read timeout time and so on.
 */
public class PipeSocket extends PipeBase{
    /**
     * 实例化一个默认的对象<br />
     * Instantiate a default object
     */
    public PipeSocket( )
    {
        this._port = new int[] { 2000 };
    }

    /**
     * 通过指定的IP地址和端口号来实例化一个对象<br />
     * Instantiate an object with the specified IP address and port number
     * @param ipAddress IP地址信息
     * @param port 端口号
     */
    public PipeSocket( String ipAddress, int port )
    {
        this.ipAddress = ipAddress;
        this._port      = new int[] { port };
    }

    /**
     * 获取当前的连接状态是否发生了异常，如果发生了异常，返回 False<br />
     * Gets whether an exception has occurred in the current connection state, and returns False if an exception has occurred
     * @return 如果有异常，返回 True, 否则返回 False
     */
    public boolean IsConnectitonError( ) {
        return getIsSocketError() || this.socket == null;
    }

    //public IPEndPoint LocalBinding { get; set; }

    /**
     * 设置当前的IP地址信息，可以是网站的地址
     * @param ipAddress IP地址
     */
    public void setIpAddress( String ipAddress ) {
        this.ipAddress = PlcHelper.GetIpAddressFromInput(ipAddress);
    }

    /**
     * 获取当前的IP地址信息
     * @return ip 地址
     */
    public String getIpAddress()
    {
        return this.ipAddress;
    }

    /**
     * 获取当前的端口号信息
     * @return 端口号
     */
    public int getPort(){
        if (this._port.length == 1)
        {
            return this._port[0];
        }
        else
        {
            int index = this.indexPort;
            if (index < 0 || index >= this._port.length) index = 0;
            return this._port[index];
        }
    }

    /**
     * 设置当前的端口号信息<br />
     * Sets the current port number information
     * @param value 端口信息
     */
    public void setPort(int value){
        if (this._port.length == 1)
        {
            this._port[0] = value;
        }
        else
        {
            int index = this.indexPort;
            if (index < 0 || index >= this._port.length) index = 0;
            _port[index] = value;
        }
    }

    /**
     * 获取指示长连接的套接字是否处于错误的状态
     * @return 是否套接字异常
     */
    public boolean getIsSocketError(){
        return this.isSocketError;
    }

    /**
     * 设置指示长连接的套接字是否处于错误的状态
     * @param value 状态值信息
     */
    public void setIsSocketError(boolean value){
        this.isSocketError = value;
        if(value) {
            PlcProtocolAbstract protocol = PlcProtocolAbstract.get();
            if(protocol != null) {
                PlcClientAdapter iotClient = protocol.getIotClient();
                if(iotClient != null && iotClient.getStatus().equals("connected")) {
                    iotClient.publishEvent(ClientStatus.offline).reconnect();
                }
            }
        }
    }

    /**
     * 获取当前的客户端用于服务器连接的套接字。<br />
     * Gets the socket currently used by the client for server connection.
     * @return 获取到的socket对象
     */
    public Socket getSocket()
    {
        return this.socket;
    }

    /**
     * 设置当前的客户端用于服务器连接的套接字。<br />
     * Sets the socket currently used by the client for server connection.
     * @param value 新的socket对象
     */
    public void setSocket(Socket value){
        this.socket = value;
    }

    /**
     * 获取连接的超时时间，单位是毫秒<br />
     * Gets the timeout for the connection, in milliseconds
     * @return 连接超时的值
     */
    public int getConnectTimeOut() {
        return this.connectTimeOut;
    }

    /**
     * 置连接的超时时间，单位是毫秒 <br />
     * Sets the timeout for the connection, in milliseconds
     * @param value 超时时间值
     */
    public void setConnectTimeOut(int value){
        this.connectTimeOut = value;
    }

    /**
     * 获取接收服务器反馈的时间，如果为负数，则不接收反馈<br />
     * Gets the time to receive server feedback, and if it is a negative number, does not receive feedback
     * @return 时间值信息
     */
    public int getReceiveTimeOut(){
        return this.receiveTimeOut;
    }

    /**
     * 设置接收服务器反馈的时间，如果为负数，则不接收反馈<br />
     * Sets the time to receive server feedback, and if it is a negative number, does not receive feedback
     * @param value 设置值
     */
    public void setReceiveTimeOut(int value){
        this.receiveTimeOut = value;
    }

    /**
     * 获取正式接收对方返回数据前的时候，需要休息的时间，当设置为0的时候，不需要休息。
     * @return Get the time required to rest before officially receiving the data from the other party. When it is set to 0, no rest is required.
     */
    public int getSleepTime(){
        return this.sleepTime;
    }

    /**
     * 设置在正式接收对方返回数据前的时候，需要休息的时间，当设置为0的时候，不需要休息。<br />
     * Set the time required to rest before officially receiving the data from the other party. When it is set to 0, no rest is required.
     * @param value 设置的睡眠时间值
     */
    public void setSleepTime(int value){
        this.sleepTime = value;
    }

    /**
     * 关闭当前的连接套接字<br />
     * Close the current connection socket
     */
    public void Dispose( ) {
        NetSupport.CloseSocket(this.socket);
    }

    /**
     * 设置多个可选的端口号信息，例如在三菱的PLC里，支持配置多个端口号，当一个网络发生异常时，立即切换端口号连接读写，提升系统的稳定性<br />
     * Set multiple optional port number information. For example, in Mitsubishi PLC, it supports to configure multiple port numbers.
     * When an abnormality occurs in a network, the port number is immediately switched to connect to read and write to improve the stability of the system.
     * @param ports 端口号数组信息
     */
    public void SetMultiPorts( int[] ports ) {
        if (ports != null && ports.length > 0) {
            this._port = ports;
            this.indexPort = -1;
        }
    }

    /**
     * 获取当前的远程连接信息，如果端口号设置了可选的数组，那么每次获取对象就会发生端口号切换的操作。<br />
     * Get the current remote connection information. If the port number is set to an optional array, the port number switching operation will occur every time the object is obtained.
     * @return 远程连接的对象
     */
    public SocketAddress GetConnectIPEndPoint( )
    {
        if (_port.length == 1) return new InetSocketAddress( getIpAddress(), _port[0] );
        ChangePorts( );
        int port = _port[this.indexPort];
        return new InetSocketAddress( getIpAddress(), port );
    }

    /**
     * 变更当前的端口号信息，如果设置了多个端口号的话，就切换其他可用的端口<br />
     * Change the current port number information, and if multiple port numbers are set, switch to other available ports
     */
    public void ChangePorts( )
    {
        if (_port.length == 1) return;

        if (this.indexPort < this._port.length - 1)
            this.indexPort++;
        else
            this.indexPort = 0;
    }

    public String toString( ){
        return "PipeSocket[" + ipAddress + ":" + getPort() + "]";
    }

    private String ipAddress = "127.0.0.1";            // IP地址信息
    private int[] _port = null;                        // 端口号数组信息
    private int indexPort = -1;                        // 端口的索引
    private Socket socket;                             // 核心的通信的方法
    private int receiveTimeOut = 5000;                 // 接收数据超时的时间
    private int connectTimeOut = 10_000;               // 连接设备超时的时间
    private int sleepTime = 0;                         // 设备请求应答时，中间休眠的时间，如果设置为0，不需要休眠
    private boolean isSocketError = false;             // 当前的状态字是否发生了错误
}
