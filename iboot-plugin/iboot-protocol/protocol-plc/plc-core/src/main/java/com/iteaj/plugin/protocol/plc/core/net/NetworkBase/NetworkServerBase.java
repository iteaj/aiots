package com.iteaj.plugin.protocol.plc.core.net.NetworkBase;

import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.StringResources;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkServerBase extends NetworkBase {

    // region Constructor

    /// <summary>

    /**
     * 实例化一个默认的对象<br />
     * Instantiate a default object
     */
    public NetworkServerBase()
    {
        IsStarted = false;
        Port = 0;
    }

    // endregion

    // region Public Properties

    /**
     * 获取当前的系统是否处于已经启动状态
     * @return 是否启动
     */
    public boolean isStarted() {
        return IsStarted;
    }

    /**
     * 设置当前的系统是否处于启动状态
     * @param started 启动状态
     */
    protected void setStarted(boolean started) {
        IsStarted = started;
    }

    /**
     * 获取当前服务的端口信息
     * @return 端口号
     */
    public int getPort() {
        return Port;
    }

    /**
     * 设置当前的端口号信息
     * @param port 端口号
     */
    public void setPort(int port) {
        Port = port;
    }

    /**
     * 服务器引擎是否启动<br />
     * Whether the server engine is started
     */
    private boolean IsStarted =false;

    /**
     * 获取或设置服务器的端口号，如果是设置，需要在服务器启动前设置完成，才能生效。<br />
     * Gets or sets the port number of the server. If it is set, it needs to be set before the server starts to take effect.
     * <br /><br />
     * 需要在服务器启动之前设置为有效
     */
    private int Port = 0;
    private ServerSocket serverSocket;

    // endregion

    // region Protect Method

    private void ThreadPoolLogin( Socket socket ) {
        OperateResult check = SocketAcceptExtraCheck(socket, socket.getInetAddress());
        if (!check.IsSuccess) {
            if (LogNet != null) LogNet.WriteDebug(toString(), "[" + socket.getInetAddress() + "] Socket Accept Extra Check Failed : {check.Message}");
            CloseSocket(socket);
        } else {
            ThreadPoolLogin(socket, socket.getInetAddress());
        }
    }

    /**
     * 当客户端连接到服务器，并听过额外的检查后，进行回调的方法<br />
     * Callback method when the client connects to the server and has heard additional checks
     * @param socket socket对象
     * @param endPoint 远程的终结点
     */
    protected void ThreadPoolLogin( Socket socket, InetAddress endPoint ){
        CloseSocket(socket);
    }

    // endregion

    // region Start And Close

    /**
     * 当客户端的socket登录的时候额外检查的操作，并返回操作的结果信息。<br />
     * The operation is additionally checked when the client's socket logs in, and the result information of the operation is returned.
     * @param socket 套接字
     * @param endPoint 终结点
     * @return 验证的结果
     */
    protected OperateResult SocketAcceptExtraCheck( Socket socket, InetAddress endPoint ) {
        return OperateResult.CreateSuccessResult();
    }

    /**
     * 服务器启动时额外的初始化信息，可以用于启动一些额外的服务的操作。<br />
     * The extra initialization information when the server starts can be used to start some additional service operations.
     */
    protected void StartInitialization( ) { }

    /**
     * 指定端口号来启动服务器的引擎<br />
     * Specify the port number to start the server's engine
     * @param port 指定一个端口号
     * @throws IOException 发送的异常
     */
    public void ServerStart( int port ) throws IOException {
        if (!IsStarted)
        {
            StartInitialization( );
            serverSocket = new ServerSocket(port);
            new Thread(this::MethodThreadAccept).start();
            IsStarted = true;
            Port = port;
            if(LogNet!=null) LogNet.WriteInfo( toString( ), StringResources.Language.NetEngineStart() );
        }
    }

    private class MultiThreadServer implements Runnable {
        MultiThreadServer(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ThreadPoolLogin(this.socket);
        }

        private Socket socket;
    }

    private void MethodThreadAccept() {
        while (true) {
            Socket sock = null;
            try {
                sock = serverSocket.accept();
            } catch (IOException e) {
                if (!IsStarted) return;
                e.printStackTrace();
            }
            if (sock != null) new Thread(new MultiThreadServer(sock)).start();
        }
    }

    /**
     * 使用已经配置好的端口启动服务器的引擎<br />
     * Use the configured port to start the server's engine
     */
    public void ServerStart( ) throws IOException {
        ServerStart( Port );
    }

    /**
     * 服务器关闭的时候需要做的事情<br />
     * Things to do when the server is down
     */
    protected void CloseAction( ) { }

    /**
     * 关闭服务器的引擎<br />
     * Shut down the server's engine
     */
    public void ServerClose( ) {
        if (IsStarted) {
            IsStarted = false;
            CloseAction();
            try {
                serverSocket.close();
            }
            catch (Exception ex){

            }
            if (LogNet != null) LogNet.WriteInfo(toString(), StringResources.Language.NetEngineClose());
        }
    }

    // endregion

    // region Create Alien Connection

    /**************************************************************************************************
     *
     *    此处实现了连接Hsl异形客户端的协议，特殊的协议实现定制请联系作者
     *    QQ群：592132877
     *
     *************************************************************************************************/

//    /**
//     * 创建一个指定的异形客户端连接，使用协议来发送注册包<br />
//     * @param ipAddress Ip地址
//     * @param port 端口号
//     * @param dtuId 设备唯一ID号，最长11
//     * @return 是否成功连接
//     */
//    public OperateResult ConnectHslAlientClient(String ipAddress, int port , String dtuId)
//    {
//        if (dtuId.length() > 11) dtuId = dtuId.substring( 11 );
//        byte[] sendBytes = new byte[28];
//        sendBytes[0] = 0x48;
//        sendBytes[1] = 0x73;
//        sendBytes[2] = 0x6E;
//        sendBytes[3] = 0x00;
//        sendBytes[4] = 0x17;
//
//        Encoding.ASCII.GetBytes( dtuId ).CopyTo( sendBytes, 5 );
//        // 创建连接
//        OperateResultExOne<Socket> connect = CreateSocketAndConnect( ipAddress, port, 10000 );
//        if (!connect.IsSuccess) return connect;
//
//        // 发送数据
//        OperateResult send = Send( connect.Content, sendBytes );
//        if (!send.IsSuccess) return send;
//
//        // 接收数据
//        OperateResultExOne<byte[]> receive = ReceiveByMessage( connect.Content, 10000, new AlienMessage( ) );
//        if (!receive.IsSuccess) return receive;
//
//        switch (receive.Content[5])
//        {
//            case 0x01:
//            {
//                CloseSocket(connect.Content);
//                return new OperateResult( StringResources.Language.DeviceCurrentIsLoginRepeat() );
//            }
//            case 0x02:
//            {
//                CloseSocket(connect.Content);
//                return new OperateResult( StringResources.Language.DeviceCurrentIsLoginForbidden() );
//            }
//            case 0x03:
//            {
//                CloseSocket(connect.Content);
//                return new OperateResult( StringResources.Language.PasswordCheckFailed() );
//            }
//        }
//
//        ThreadPoolLogin( connect.Content );
//        return OperateResult.CreateSuccessResult( );
//    }

    // endregion

    // region Object Override


    public String toString( ) {
        return "NetworkServerBase[" + Port + "]";
    }

    // endregion
}
