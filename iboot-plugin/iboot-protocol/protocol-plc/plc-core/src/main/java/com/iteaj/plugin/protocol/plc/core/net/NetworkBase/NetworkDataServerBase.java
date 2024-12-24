package com.iteaj.plugin.protocol.plc.core.net.NetworkBase;


import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.net.IReadWriteNet;
import com.iteaj.plugin.protocol.plc.core.net.StateOne.AppSession;
import com.iteaj.plugin.protocol.plc.core.thread.SimpleHybirdLock;
import com.iteaj.plugin.protocol.plc.core.transfer.ByteTransformHelper;
import com.iteaj.plugin.protocol.plc.core.transfer.IByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.log.core.ILogNet;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class NetworkDataServerBase extends NetworkServerBase implements IReadWriteNet {

    // region Constructor

    /**
     * 实例化一个默认的数据服务器的对象<br />
     * Instantiate an object of the default data server
     */
    public NetworkDataServerBase() {
        ConnectionId = SoftBasic.GetUniqueStringByGuidAndRandom();
        this.listLock = new SimpleHybirdLock();
        this.listSessions = new ArrayList<>();
    }

    // endregion

    // region File Load Save

    /// <summary>
    /// 将本系统的数据池数据存储到指定的文件<br />
    /// Store the data pool data of this system to the specified file
    /// </summary>
    /// <param name="path">指定文件的路径</param>
    /// <exception cref="ArgumentException"></exception>
    /// <exception cref="ArgumentNullException"></exception>
    /// <exception cref="System.IO.PathTooLongException"></exception>
    /// <exception cref="System.IO.DirectoryNotFoundException"></exception>
    /// <exception cref="System.IO.IOException"></exception>
    /// <exception cref="UnauthorizedAccessException"></exception>
    /// <exception cref="NotSupportedException"></exception>
    /// <exception cref="System.Security.SecurityException"></exception>
//    public void SaveDataPool( String path )
//    {
//        byte[] content = SaveToBytes( );
//        System..WriteAllBytes( path, content );
//    }

    /// <summary>
    /// 从文件加载数据池信息<br />
    /// Load datapool information from a file
    /// </summary>
    /// <param name="path">文件路径</param>
    /// <exception cref="ArgumentException"></exception>
    /// <exception cref="ArgumentNullException"></exception>
    /// <exception cref="System.IO.PathTooLongException"></exception>
    /// <exception cref="System.IO.DirectoryNotFoundException"></exception>
    /// <exception cref="System.IO.IOException"></exception>
    /// <exception cref="UnauthorizedAccessException"></exception>
    /// <exception cref="NotSupportedException"></exception>
    /// <exception cref="System.Security.SecurityException"></exception>
//    public void LoadDataPool( string path )
//    {
//        if (System.IO.File.Exists( path ))
//        {
//            byte[] buffer = System.IO.File.ReadAllBytes( path );
//            LoadFromBytes( buffer );
//        }
//    }

    /**
     * 从字节数据加载数据信息，需要进行重写方法<br />
     * Loading data information from byte data requires rewriting method
     *
     * @param content 字节数据
     */
    protected void LoadFromBytes(byte[] content) {
    }

    /**
     * 将数据信息存储到字节数组去，需要进行重写方法<br />
     * To store data information into a byte array, a rewrite method is required
     *
     * @return 所有的内容
     */
    protected byte[] SaveToBytes() {
        return new byte[0];
    }

    // endregion

    // region Public Members

    public String getConnectionId() {
        return ConnectionId;
    }

    public void setConnectionId(String connectionId) {
        ConnectionId = connectionId;
    }

    private String ConnectionId = "";
    private ArrayList<AppSession> listSessions;
    private SimpleHybirdLock listLock;
    private ILogNet logNet;

    /**
     * 设置当前的日志对象信息<br />
     * Set the current log object information
     * @param logNet 日志对象
     */
    public void setLogNet(ILogNet logNet){
        this.logNet = logNet;
    }

    public boolean isEnableWrite() {
        return EnableWrite;
    }

    /**
     * 设置当前的服务器是否支持远程客户端写入操作，默认为支持<br />
     * Specifies whether the current server supports remote client write operations, which is supported by default
     * @param enableWrite 是否支持写入
     */
    public void setEnableWrite(boolean enableWrite) {
        EnableWrite = enableWrite;
    }

    /**
     * 获取或设置当前的服务器是否允许远程客户端进行写入数据操作，默认为<c>True</c><br />
     * Gets or sets whether the current server allows remote clients to write data, the default is <c>True</c>
     */
    public boolean EnableWrite = true;

    /**
     * 接收到数据的时候就触发的事件，示例详细参考API文档信息<br />
     * An event that is triggered when data is received
     */
    public ActionOperateExTwo OnDataReceived;

    /**
     * 触发一个数据接收的事件信息<br />
     * Event information that triggers a data reception
     *
     * @param source  数据的发送方
     * @param receive 接收数据信息
     */
    protected void RaiseDataReceived(Object source, byte[] receive) {
        if (OnDataReceived != null) OnDataReceived.Action(source, receive);
    }

    /**
     * 数据发送的时候就触发的事件<br />
     * Events that are triggered when data is sent
     */
    public ActionOperateExTwo OnDataSend;

    /**
     * 触发一个数据发送的事件信息<br />
     * Event information that triggers a data transmission
     *
     * @param send 数据内容
     */
    protected void RaiseDataSend(byte[] send) {
        if (OnDataSend != null) OnDataSend.Action(this, send);
    }

    /**
     * 获取串口模式下消息的日志记录方式，可以继承重写。<br />
     * Get the logging method of messages in serial mode, which can be inherited and rewritten.
     *
     * @param data 原始数据
     * @return 消息
     */
    protected String GetSerialMessageLogText(byte[] data) {
        return LogMsgFormatBinary ? SoftBasic.ByteToHexString(data, ' ') : SoftBasic.GetAsciiStringRender(data);
    }

    // endregion

    // region Protect Member

    protected short WordLength = 1;

    protected boolean LogMsgFormatBinary = true;

    private IByteTransform byteTransform;


    /**
     * 获取当前的数据变换机制，当你需要从字节数据转换类型数据的时候需要。<br />
     * Get the current data transformation mechanism is required when you need to convert type data from byte data.
     *
     * @return 数据的变换对象
     */
    public IByteTransform getByteTransform() {
        return byteTransform;
    }

    /**
     * 设置当前的数据变换机制，当你需要从字节数据转换类型数据的时候需要。<br />
     * Set the current data transformation mechanism is required when you need to convert type data from byte data.
     *
     * @param transform 数据变换
     */
    public void setByteTransform(IByteTransform transform) {
        byteTransform = transform;
    }

    protected short GetWordLength(String address, int length, int dataTypeLength) {
        if (WordLength == 0) {
            int len = length * dataTypeLength * 2 / 4;
            return len == 0 ? (short) 1 : (short) len;
        }
        return (short) (WordLength * length * dataTypeLength);
    }

    protected INetMessage GetNewNetMessage() {
        return null;
    }

    protected OperateResultExOne<byte[]> ReadFromCoreServer(AppSession session, byte[] receive) {
        return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedFunction());
    }

    /**
     * 服务器关闭的时候需要做的事情<br />
     * Things to do when the server is down
     */
    @Override
    protected void CloseAction() {
        super.CloseAction();
        for(int i = this.listSessions.size() - 1; i >= 0; i--){
            RemoveClient(this.listSessions.get(i));
        }
    }

    // endregion

    // region NetworkAuthenticationServerBase Override

    /**
     * 移除指定的客户端连接对象<br />
     * Removes the specified client connection object
     * @param session 会话对象
     */
    public void RemoveClient(AppSession session) {
        RemoveClient(session, null);
    }

    /**
     * 移除指定的客户端连接对象，并指定错误消息<br />
     * Removes the specified client connection object and specifies the error message
     * @param session 会话消息
     * @param message 错误的消息
     */
    public void RemoveClient(AppSession session, String message) {
        this.listLock.Enter();
        boolean remove = this.listSessions.remove(session);
        this.listLock.Leave();
        if (remove) {
            CloseSocket(session.getWorkSocket());
            if (this.logNet != null)
                this.logNet.WriteDebug(toString(), session.getIpEndPoint().toString() + " Offline " + (Utilities.IsStringNullOrEmpty(message) ? "" : message));
        }
    }

    /**
     * 新增一个会话客户端对象<br />
     * A new session client object has been added
     * @param session 会话客户端信息
     */
    public void AddClient( AppSession session ){
        this.listLock.Enter();
        this.listSessions.add(session);
        this.listLock.Leave();
        if (this.logNet!=null && session != null) this.logNet.WriteDebug( toString(),  session.getIpEndPoint().toString() + " Online" );
    }

    /**
     * 获取当前在线的客户端会话数量<br />
     * Gets the number of client sessions that are currently online
     * @return 在线数量
     */
    public int GetOnlineCount(){
        return this.listSessions.size();
    }

    @Override
    protected void ThreadPoolLogin(Socket socket, InetAddress endPoint) {
        AppSession session = new AppSession();
        session.setWorkSocket(socket);
        session.setIpEndPoint(socket.getInetAddress());
        AddClient(session);
        while (true) {
            try {
                OperateResultExOne<byte[]> read1;

                read1 = ReceiveByMessage(session.getWorkSocket(), 3600 * 2 * 1000, GetNewNetMessage());
                if (!read1.IsSuccess) {
                    RemoveClient(session);
                    return;
                }

                if (this.logNet!= null) this.logNet.WriteDebug( toString( ), "[" + session.getIpEndPoint() + "] Tcp Recv：" + (LogMsgFormatBinary ? SoftBasic.ByteToHexString( read1.Content, ' ' ) : SoftBasic.GetAsciiStringRender( read1.Content )));

                OperateResultExOne<byte[]> read = ReadFromCoreServer(session, read1.Content);
                if (!read.IsSuccess) {
                    //LogNet?.WriteDebug( ToString( ), $"[{session.IpEndPoint}] Tcp {read.Message}" );
                    if (read.Content != null && read.Content.length > 0) read.IsSuccess = true;
                }

                if (read.IsSuccess) {
                    if (read.Content != null) {
                        Send(session.getWorkSocket(), read.Content);
                        if (this.logNet!= null) this.logNet.WriteDebug( toString( ), "[" + session.getIpEndPoint() + "] Tcp Send：" + (LogMsgFormatBinary ?  SoftBasic.ByteToHexString( read.Content, ' ' ) : SoftBasic.GetAsciiStringRender( read.Content )) );
                    } else {
                        RemoveClient(session);
                        return;
                    }

                }
                session.UpdateHeartTime();
                RaiseDataReceived(session, read1.Content);
            } catch (Exception ex) {
                RemoveClient(session, "SocketAsyncCallBack Exception -> {ex.Message}");
            }
        }
    }

    // endregion

    // region IDisposable Support

    /**
     * 释放当前的对象
     *
     * @param disposing 是否托管对象
     */
    protected void Dispose(boolean disposing) {
        if (disposing) {
            this.OnDataSend = null;
            this.OnDataReceived = null;
        }
        //super.Dispose( disposing );
    }

    // endregion

    // region Private Member

    // region Object Override

    public String toString() {
        return "NetworkDataServerBase[" + getPort() + "]";
    }

    // endregion

    // 以下内容和 NetworkDoubleBase 相同，因为不存在多继承，所以暂时选择了复制代码

    /**
     * 一个字单位的数据表示的地址长度，西门子为2，三菱，欧姆龙，modbusTcp就为1，AB PLC无效<br />
     * The address length represented by one word of data, Siemens is 2, Mitsubishi, Profinet.Omron, modbusTcp is 1, AB PLC is invalid
     */
    // region Read Write Bytes bool
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedFunction());
    }

    public OperateResult Write(String address, byte[] value) {
        return new OperateResult(StringResources.Language.NotSupportedFunction());
    }


    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return new OperateResultExOne<boolean[]>(StringResources.Language.NotSupportedFunction());
    }

    public OperateResultExOne<Boolean> ReadBool(String address) {
        OperateResultExOne<boolean[]> read = ReadBool(address, (short) 1);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(read.Content[0]);
    }

    public OperateResult Write(String address, boolean[] value) {
        return new OperateResult(StringResources.Language.NotSupportedFunction());
    }

    public OperateResult Write(String address, boolean value) {
        return Write(address, new boolean[]{value});
    }

    // endregion

    public <T extends IDataTransfer> OperateResultExOne<T> ReadCustomer(String address, Class<T> tClass) {
        OperateResultExOne<T> result = new OperateResultExOne<T>();
        T Content;
        try {
            Content = tClass.newInstance();
        } catch (Exception ex) {
            Content = null;
        }
        OperateResultExOne<byte[]> read = Read(address, Content.getReadCount());
        if (read.IsSuccess) {
            Content.ParseSource(read.Content);
            result.Content = Content;
            result.IsSuccess = true;
        } else {
            result.ErrorCode = read.ErrorCode;
            result.Message = read.Message;
        }
        return result;
    }

    public <T extends IDataTransfer> OperateResult WriteCustomer(String address, T data) {
        return Write(address, data.ToSource());
    }

    @Override
    public OperateResult ConnectClose() {
        ServerClose();
        return OperateResult.CreateSuccessResult();
    }

    public OperateResultExOne<Short> ReadInt16(String address) {
        OperateResultExOne<short[]> read = ReadInt16(address, (short) 1);
        if (!read.IsSuccess)
            return OperateResultExOne.CreateFailedResult(read);
        else
            return OperateResultExOne.<Short>CreateSuccessResult(read.Content[0]);
    }

    public OperateResultExOne<short[]> ReadInt16(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) (length * WordLength)), new FunctionOperateExOne<byte[], short[]>() {
            @Override
            public short[] Action(byte[] content) {
                return getByteTransform().TransInt16(content, 0, length);
            }
        });
    }

    public OperateResultExOne<Integer> ReadUInt16(String address) {
        OperateResultExOne<int[]> read = ReadUInt16(address, (short) 1);
        if (!read.IsSuccess)
            return OperateResultExOne.CreateFailedResult(read);
        else
            return OperateResultExOne.<Integer>CreateSuccessResult(read.Content[0]);
    }

    public OperateResultExOne<int[]> ReadUInt16(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) (length * WordLength)), new FunctionOperateExOne<byte[], int[]>() {
            @Override
            public int[] Action(byte[] content) {
                return getByteTransform().TransUInt16(content, 0, length);
            }
        });
    }

    public OperateResultExOne<Integer> ReadInt32(String address) {
        OperateResultExOne<int[]> read = ReadInt32(address, (short) 1);
        if (!read.IsSuccess)
            return OperateResultExOne.CreateFailedResult(read);
        else
            return OperateResultExOne.<Integer>CreateSuccessResult(read.Content[0]);
    }

    public OperateResultExOne<int[]> ReadInt32(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) (length * WordLength * 2)), new FunctionOperateExOne<byte[], int[]>() {
            @Override
            public int[] Action(byte[] content) {
                return getByteTransform().TransInt32(content, 0, length);
            }
        });
    }

    public OperateResultExOne<Long> ReadUInt32(String address) {
        OperateResultExOne<long[]> read = ReadUInt32(address, (short) 1);
        if (!read.IsSuccess)
            return OperateResultExOne.CreateFailedResult(read);
        else
            return OperateResultExOne.<Long>CreateSuccessResult(read.Content[0]);
    }

    public OperateResultExOne<long[]> ReadUInt32(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) (length * WordLength * 2)), new FunctionOperateExOne<byte[], long[]>() {
            @Override
            public long[] Action(byte[] content) {
                return getByteTransform().TransUInt32(content, 0, length);
            }
        });
    }

    public OperateResultExOne<Float> ReadFloat(String address) {
        OperateResultExOne<float[]> read = ReadFloat(address, (short) 1);
        if (!read.IsSuccess)
            return OperateResultExOne.CreateFailedResult(read);
        else
            return OperateResultExOne.<Float>CreateSuccessResult(read.Content[0]);
    }

    public OperateResultExOne<float[]> ReadFloat(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) (length * WordLength * 2)), new FunctionOperateExOne<byte[], float[]>() {
            @Override
            public float[] Action(byte[] content) {
                return getByteTransform().TransSingle(content, 0, length);
            }
        });
    }

    public OperateResultExOne<Long> ReadInt64(String address) {
        OperateResultExOne<long[]> read = ReadInt64(address, (short) 1);
        if (!read.IsSuccess)
            return OperateResultExOne.CreateFailedResult(read);
        else
            return OperateResultExOne.<Long>CreateSuccessResult(read.Content[0]);
    }

    public OperateResultExOne<long[]> ReadInt64(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) (length * WordLength * 4)), new FunctionOperateExOne<byte[], long[]>() {
            @Override
            public long[] Action(byte[] content) {
                return getByteTransform().TransInt64(content, 0, length);
            }
        });
    }

    public OperateResultExOne<Double> ReadDouble(String address) {
        OperateResultExOne<double[]> read = ReadDouble(address, (short) 1);
        if (!read.IsSuccess)
            return OperateResultExOne.CreateFailedResult(read);
        else
            return OperateResultExOne.<Double>CreateSuccessResult(read.Content[0]);
    }

    public OperateResultExOne<double[]> ReadDouble(String address, final short length) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, (short) (length * WordLength * 4)), new FunctionOperateExOne<byte[], double[]>() {
            @Override
            public double[] Action(byte[] content) {
                return getByteTransform().TransDouble(content, 0, length);
            }
        });
    }

    public OperateResultExOne<String> ReadString(String address, short length) {
        return ReadString(address, length, StandardCharsets.US_ASCII);
    }

    public OperateResultExOne<String> ReadString(String address, short length, Charset encoding) {
        return ByteTransformHelper.GetResultFromBytes(Read(address, length), new FunctionOperateExOne<byte[], String>() {
            @Override
            public String Action(byte[] content) {
                return getByteTransform().TransString(content, 0, content.length, encoding);
            }
        });
    }

    public OperateResult Write(String address, short[] values) {
        return Write(address, getByteTransform().TransByte(values));
    }

    public OperateResult Write(String address, short value) {
        return Write(address, new short[]{value});
    }

    public OperateResult Write(String address, int[] values) {
        return Write(address, getByteTransform().TransByte(values));
    }

    public OperateResult Write(String address, int value) {
        return Write(address, new int[]{value});
    }

    public OperateResult Write(String address, float[] values) {
        return Write(address, getByteTransform().TransByte(values));
    }

    public OperateResult Write(String address, float value) {
        return Write(address, new float[]{value});
    }

    public OperateResult Write(String address, long[] values) {
        return Write(address, getByteTransform().TransByte(values));
    }

    public OperateResult Write(String address, long value) {
        return Write(address, new long[]{value});
    }

    public OperateResult Write(String address, double[] values) {
        return Write(address, getByteTransform().TransByte(values));
    }

    public OperateResult Write(String address, double value) {
        return Write(address, new double[]{value});
    }

    public OperateResult Write(String address, String value) {
        return Write(address, value, StandardCharsets.US_ASCII);
    }

    public OperateResult Write(String address, String value, Charset encoding) {
        byte[] temp = getByteTransform().TransByte(value, encoding);
        if (WordLength == 1) temp = SoftBasic.ArrayExpandToLengthEven(temp);
        return Write(address, temp);
    }

    public OperateResult Write(String address, String value, int length) {
        return Write(address, value, length, StandardCharsets.US_ASCII);
    }

    public OperateResult Write(String address, String value, int length, Charset encoding) {
        byte[] temp = getByteTransform().TransByte(value, encoding);
        if (WordLength == 1) temp = SoftBasic.ArrayExpandToLengthEven(temp);
        temp = SoftBasic.ArrayExpandToLength(temp, length);
        return Write(address, temp);
    }

    public OperateResult WriteUnicodeString(String address, String value) {
        byte[] temp = Utilities.csharpString2Byte(value);
        return Write(address, temp);
    }

    public OperateResult WriteUnicodeString(String address, String value, int length) {
        byte[] temp = Utilities.csharpString2Byte(value);
        temp = SoftBasic.ArrayExpandToLength(temp, length * 2);
        return Write(address, temp);
    }
}
