package com.iteaj.plugin.protocol.plc.core.profinet.OpenProtocol;

import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.OpenProtocolMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDoubleBase;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.types.Convert;
import com.iteaj.plugin.protocol.plc.core.types.Encoding;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 开放以太网协议，在拧紧枪中应用广泛，本通信支持基本的问答机制以及订阅机制，支持完全自定义的参数指定及数据读取。<br />
 * Open Ethernet protocol, widely used in tightening guns, this communication supports basic question answering mechanism and subscription mechanism, supports fully customized parameter specification and data reading.
 */
public class OpenProtocolNet extends NetworkDoubleBase {

    // region Constructor

    /// <summary>
    /// 实例化一个默认的对象<br />
    /// Instantiate a default object
    /// </summary>

    /**
     * 实例化一个默认的对象<br />
     * Instantiate a default object
     */
    public OpenProtocolNet( ) {
        this.revisonOnConnected = 1;
        this.timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!GetPipeSocket().IsConnectitonError())
                {
                    OperateResultExOne<byte[]> command = BuildReadCommand(  9999,  1,  -1, -1,  null );
                    if (!command.IsSuccess) return;

                    Send( GetPipeSocket().getSocket(), command.Content );
                }
            }
        };

        //this.parameterSetMessages = new ParameterSetMessages(this);
        //this.jobMessage = new JobMessage(this);
        //this.tighteningResultMessages = new TighteningResultMessages(this);
        //this.toolMessages = new ToolMessages(this);
        //this.timeMessages = new TimeMessages(this);
        this.LogMsgFormatBinary = false;
        this.setUseServerActivePush(true);
    }

    /**
     * 使用指定的IP地址来初始化对象<br />
     * Use the specified IP address to initialize the object
     * @param ipAddress Ip地址
     */
    public OpenProtocolNet( String ipAddress ){
        this(ipAddress, 4545);
    }

    /**
     * 使用指定的IP地址及端口来初始化对象<br />
     * Use the specified IP address and port to initialize the object
     * @param ipAddress Ip地址
     * @param port 端口号
     */
    public OpenProtocolNet( String ipAddress, int port ) {
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    protected INetMessage GetNewNetMessage( ) {
        return new OpenProtocolMessage( );
    }

    // endregion

    // region Override NetworkDoubleBase

    protected OperateResult InitializationOnConnect(Socket socket ) {
        // 此处的 revison 共有三种，1，2，3
        if (this.revisonOnConnected >= 0) {
            OperateResultExOne<byte[]> command = BuildReadCommand(1, this.revisonOnConnected, -1, -1, null);
            if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

            OperateResult send = Send(socket, command.Content);
            if (!send.IsSuccess) return OperateResultExOne.CreateFailedResult(send);

            OperateResultExOne<byte[]> receive = ReceiveByMessage(socket, this.getReceiveTimeOut(), GetNewNetMessage());
            if (!receive.IsSuccess) return OperateResultExOne.CreateFailedResult(receive);

            String reply = Encoding.ASCII.GetString(receive.Content);
            if (reply.substring(4, 8).equals( "0002"))
                return super.InitializationOnConnect(socket);
            else
                return new OperateResult("Failed:" + reply.substring(4, 8));
        } else
            return super.InitializationOnConnect(socket);
    }

    /**
     * 根据实际的协议选择是否重写本方法，有些协议在断开连接之前，需要发送一些报文来关闭当前的网络通道<br />
     * Select whether to rewrite this method according to the actual protocol. Some protocols need to send some packets to close the current network channel before disconnecting.
     *
     * @param socket 网络套接字
     * @return 结果类对象
     */
    @Override
    protected OperateResult ExtraOnDisconnect(Socket socket) {
        OperateResultExOne<byte[]> command = BuildReadCommand(3, 1, -1, -1, null);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        return ReadFromCoreServer(socket, command.Content, true, true);
    }


    private int DecideSubscribeData( int mid ) {
        if (mid == 15 || mid == 35 || mid == 52 || mid == 61 || mid == 71 || mid == 74 || mid == 76 || mid == 91 || mid == 101)
            return mid + 1;

        if (mid == 106 || mid == 107)
            return 108;

        if (mid == 121 || mid == 122 || mid == 123 || mid == 124)
            return 125;

        if (mid == 152) return 153;
        if (mid == 211) return 212;
        if (mid == 217) return 218;
        if (mid == 221) return 222;
        if (mid == 242) return 243;
        if (mid == 251) return 252;
        if (mid == 401) return 402;
        if (mid == 421) return 422;
        return -1;
    }

    protected boolean DecideWhetherQAMessage(Socket socket, OperateResultExOne<byte[]> receive ) {
        if (receive.Content.length >= 20) {
            int mid = Convert.ToInt32(Encoding.ASCII.GetString(receive.Content, 4, 4));
            boolean ack = receive.Content[11] == 0x30;

            if (mid == 9999) return false;                   // 如果是心跳，直接返回

            int id = DecideSubscribeData(mid);
            if (id > 0) {
                if (ack || AutoAckControllerMessage) Send(socket, BuildReadCommand(id, 1, -1, -1, null).Content);
                OnReceivedOpenMessageMethod(this, Encoding.ASCII.GetString(receive.Content).trim());
                return false;
            }
        }
        return super.DecideWhetherQAMessage(socket, receive);
    }

    /**
     * 当收到对方消息的时候触发的方法
     * @param openProtocolNet OpenProtocol 对象信息
     * @param content 字符串内容
     */
    public void OnReceivedOpenMessageMethod( OpenProtocolNet openProtocolNet, String content )
    {

    }

    // endregion

    /**
     * 使用自定义的命令读取数据，需要指定每个参数信息，然后返回字符串数据内容，根据实际的功能码，解析出实际的数据信息<br />
     * To use a custom command to read data, you need to specify each parameter information, then return the string data content, and parse the actual data information according to the actual function code
     * @param mid The MID is four bytes long and is specified by four ASCII digits(‘0’…’9’). The MID describes how to interpret the message.
     * @param revison The revision of the MID is specified by three ASCII digits(‘0’…’9’).The MID revision is unique per MID and is used in case several versions are available for the same MID.
     * @param stationId The station the message is addressed to in the case of controller with multi-station configuration.The station ID is 1 byte long and is specified by one ASCII digit(‘0’…’9’).
     * @param spindleId The spindle the message is addressed to in the case several spindles are connected to the same controller. The spindle ID is 2 bytes long and is specified by two ASCII digits (‘0’…’9’).
     * @param parameters The Data Field is ASCII data representing the data. The data contains a list of parameters depending on the MID.Each parameter is represented with an ID and the parameter value.
     * @return 结果数据信息
     */
    public OperateResultExOne<String> ReadCustomer( int mid, int revison, int stationId, int spindleId, String[] parameters ) {
        if (parameters == null) parameters = new String[0];
        OperateResultExOne<byte[]> command = BuildReadCommand(mid, revison, stationId, spindleId, parameters);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        OperateResult check = CheckRequestReplyMessages(read.Content);
        if (!check.IsSuccess) return OperateResultExOne.CreateFailedResult(check);

        return OperateResultExOne.CreateSuccessResult(Encoding.ASCII.GetString(read.Content));
    }
    // region Public Properties

    /**
     * 获取或设置初始化连接时 MID0001 命令的版本号，默认为 1，如果设置小于 0，则表示不发送 MID0001 命令。<br />
     * Get or set the version of the MID0001 command when initializing the connection, the default is 1, if the setting is less than 0, it means that the MID0001 command is not sent.
     *
     * @apiNote 当连接的设备是 MT Focus 6000的控制器时，本值需要设置为 6
     */
    public int RevisonOnConnected = 1;

    /// <summary>
    /// 针对控制器的主动发送的消息，是否无视<c>Ack</c>标记，全部进行返回信号操作，默认为 <c>False</c>，将根据报文里的<c>Ack</c>信号来决定是否返回数据<br />
    /// If the default value is <c>False</c>, the controller will<c></c> decide whether to return data based on the <c>Ack</c> signal in the packet
    /// </summary>
    public boolean AutoAckControllerMessage = false;

    /// <summary>
    /// 参数集合操作的相关属性，可以用来获取参数ID列表，设置数据等操作。<br />
    /// The properties related to parameter collection operations can be used to obtain parameter ID lists, set data, and other operations.
    /// </summary>
    //public ParameterSetMessages ParameterSetMessages => this.parameterSetMessages;

    /// <summary>
    /// 任务消息的相关属性，可以用来获取任务的数据，订阅任务，取消订阅任务，选择任务，启动任务。<br />
    /// The relevant properties of task messages can be used to obtain task data, subscribe to tasks, unsubscribe tasks, select tasks, and start tasks.
    /// </summary>
    //public JobMessage JobMessage => this.jobMessage;

    /// <summary>
    /// 拧紧结果消息的操作属性
    /// </summary>
    //public TighteningResultMessages TighteningResultMessages => this.tighteningResultMessages;

    /// <summary>
    /// 工具消息的操作属性
    /// </summary>
    //public ToolMessages ToolMessages => this.toolMessages;

    /// <summary>
    /// 时间消息的属性
    /// </summary>
    //public TimeMessages TimeMessages => this.timeMessages;

    // endregion


    // region Private Member

    private java.util.Timer timer;
    // private ParameterSetMessages parameterSetMessages;
    // private JobMessage jobMessage;
    // private TighteningResultMessages tighteningResultMessages;
    // private ToolMessages toolMessages;
    // private TimeMessages timeMessages;
    private int revisonOnConnected = 0;

    // endregion

    // region Object Override

    public String toString( ) {
        return "OpenProtocolNet[" + getIpAddress() + ":" + getPort() + "]";
    }


    // endregion

    public static OperateResultExOne<byte[]> BuildReadCommand( int mid, int revison, int stationId, int spindleId, String[] parameters )
    {
        if (mid < 0 || mid > 9999) return new OperateResultExOne<byte[]>( "Mid must be between 0 - 9999" );
        if (revison < 0 || revison > 999) return new OperateResultExOne<byte[]>( "revison must be between 0 - 999" );
        if (stationId > 9) return new OperateResultExOne<byte[]>( "stationId must be between 0 - 9" );
        if (spindleId > 99) return new OperateResultExOne<byte[]>( "spindleId must be between 0 - 99" );

        int count = 0;
        if (parameters != null) {
            for (int i = 0; i < parameters.length; i++) {
                count = count + parameters[i].length();
            }
        }

        StringBuilder sb = new StringBuilder( );
        sb.append( String.format( "%04d", (20 + count) ) );
        sb.append( String.format( "%04d", mid ) );
        sb.append( String.format( "%03d", revison ) );
        sb.append( '0' );                                // No ack flag: 0
        sb.append( stationId < 0 ? "  " : String.format( "%02d", stationId ) );
        sb.append( spindleId < 0 ? "  " : String.format( "%02d", spindleId ) );
        sb.append( ' ' );
        sb.append( ' ' );
        sb.append( ' ' );
        sb.append( ' ' );

        if (parameters != null)
            for (int i = 0; i < parameters.length; i++)
            {
                sb.append( parameters[i] );
            }
        sb.append( '\0' );
        return OperateResultExOne.CreateSuccessResult( Encoding.ASCII.GetBytes( sb.toString( ) ) );
    }

    /**
     * 构建一个读取的初始报文
     * @param mid The MID is four bytes long and is specified by four ASCII digits(‘0’…’9’). The MID describes how to interpret the message.
     * @param revison The revision of the MID is specified by three ASCII digits(‘0’…’9’).The MID revision is unique per MID and is used in case several versions are available for the same MID.
     * @param ack The No Ack Flag is used when setting a subscription. If the No Ack flag is not set in a subscription it means that the subscriber will acknowledge each “push” message sent by the controller (reliable mode).
     * @param stationId The station the message is addressed to in the case of controller with multi-station configuration.The station ID is 1 byte long and is specified by one ASCII digit(‘0’…’9’).
     * @param spindleId The spindle the message is addressed to in the case several spindles are connected to the same controller. The spindle ID is 2 bytes long and is specified by two ASCII digits (‘0’…’9’).
     * @param withIndex 每个参数的前面，是否携带索引信息
     * @param parameters The Data Field is ASCII data representing the data. The data contains a list of parameters depending on the MID.Each parameter is represented with an ID and the parameter value.
     * @return 原始字节的报文信息
     */
    public static OperateResultExOne<byte[]> BuildOpenProtocolMessage( int mid, int revison, int ack, int stationId, int spindleId, boolean withIndex, String... parameters ) {
        if (mid < 0 || mid > 9999) return new OperateResultExOne<byte[]>( "Mid must be between 0 - 9999" );
        if (revison < 0 || revison > 999) return new OperateResultExOne<byte[]>( "revison must be between 0 - 999" );
        if (stationId > 9) return new OperateResultExOne<byte[]>( "stationId must be between 0 - 9" );
        if (spindleId > 99) return new OperateResultExOne<byte[]>( "spindleId must be between 0 - 99" );

        int count = 0;
        StringBuilder sb = new StringBuilder( );
        sb.append( String.format( "%04d", mid ) );
        sb.append( String.format( "%03d", revison ) );
        sb.append( ack < 0 ? " " : String.format( "%1d", ack ) );
        sb.append( stationId < 0 ? "  " : String.format( "%02d", stationId ) );
        sb.append( spindleId < 0 ? "  " : String.format( "%02d", spindleId ) );
        sb.append( ' ' );
        sb.append( ' ' );
        sb.append( ' ' );
        sb.append( ' ' );

        if (parameters != null)
            for (int i = 0; i < parameters.length; i++)
            {
                if (withIndex)
                {
                    sb.append( String.format( "%02d", (i + 1) ) );
                    sb.append( parameters[i] );
                    count += 2 + parameters[i].length();
                }
                else
                {
                    sb.append( parameters[i] );
                    count += parameters[i].length();
                }
            }
        sb.append( '\0' );
        sb.insert( 0, String.format( "%04d", (20 + count) ) );
        return OperateResultExOne.CreateSuccessResult( Encoding.ASCII.GetBytes( sb.toString( ) ) );
    }

    /**
     * 根据错误代码来获取到错误文本信息
     * @param code 错误代号
     * @return 错误文本
     */
    public static String GetErrorText( int code ) {
        switch (code) {
            case 1: return "Invalid data";
            case 2: return "Parameter set ID not present";
            case 3: return "Parameter set can not be set.";
            case 4: return "Parameter set not running";
            case 6: return "VIN upload subscription already exists";
            case 7: return "VIN upload subscription does not exists";
            case 8: return "VIN input source not granted";
            case 9: return "Last tightening result subscription already exists";
            case 10: return "Last tightening result subscription does not exist";
            case 11: return "Alarm subscription already exists";
            case 12: return "Alarm subscription does not exist";
            case 13: return "Parameter set selection subscription already exists";
            case 14: return "Parameter set selection subscription does not exist";
            case 15: return "Tightening ID requested not found";
            case 16: return "Connection rejected protocol busy";
            case 17: return "Job ID not present";
            case 18: return "Job info subscription already exists";
            case 19: return "Job info subscription does not exist";
            case 20: return "Job can not be set";
            case 21: return "Job not running";
            case 22: return "Not possible to execute dynamic Job request";
            case 23: return "Job batch decrement failed";
            case 30: return "Controller is not a sync Master/station controller";
            case 31: return "Multi-spindle status subscription already exists";
            case 32: return "Multi-spindle status subscription does not exist";
            case 33: return "Multi-spindle result subscription already exists";
            case 34: return "Multi-spindle result subscription does not exist";
            case 40: return "Job line control info subscription already exists";
            case 41: return "Job line control info subscription does not exist";
            case 42: return "Identifier input source not granted";
            case 43: return "Multiple identifiers work order subscription already exists";
            case 44: return "Multiple identifiers work order subscription does not exist";
            case 50: return "Status external monitored inputs subscription already exists";
            case 51: return "Status external monitored inputs subscription does not exist";
            case 52: return "IO device not connected";
            case 53: return "Faulty IO device ID";
            case 58: return "No alarm present";
            case 59: return "Tool currently in use";
            case 60: return "No histogram available";
            case 70: return "Calibration failed";
            case 79: return "Command failed";
            case 80: return "Audi emergency status subscription exists";
            case 81: return "Audi emergency status subscription does not exist";
            case 82: return "Automatic/Manual mode subscribe already exist";
            case 83: return "Automatic/Manual mode subscribe does not exist";
            case 84: return "The relay function subscription already exists";
            case 85: return "The relay function subscription does not exist";
            case 86: return "The selector socket info subscription already exist";
            case 87: return "The selector socket info subscription does not exist";
            case 88: return "The digin info subscription already exist";
            case 89: return "The digin info subscription does not exist";
            case 90: return "Lock at bach done subscription already exist";
            case 91: return "Lock at bach done subscription does not exist";
            case 92: return "Open protocol commands disabled";
            case 93: return "Open protocol commands disabled subscription already exists";
            case 94: return "Open protocol commands disabled subscription does not exist";
            case 95: return "Reject request, PowerMACS is in manual mode";
            case 96: return "Client already connected";
            case 97: return "MID revision unsupported";
            case 98: return "Controller internal request timeout";
            case 99: return "Unknown MID";
            default: return StringResources.Language.UnknownError();
        }
    }

    /**
     * 检查请求返回的消息是否合法的
     * @param reply 返回的消息
     * @return 是否合法的结果对象
     */
    public static OperateResult CheckRequestReplyMessages( byte[] reply ) {
        try {
            if (Encoding.ASCII.GetString(reply, 4, 4).equals("0004")) {
                String mid = Encoding.ASCII.GetString(reply, 20, 4);
                int code = Convert.ToInt32(Encoding.ASCII.GetString(reply, 24, 2));

                if (code == 0) return OperateResult.CreateSuccessResult();
                return new OperateResult(code, "The request MID " + mid + " Select parameter set failed: " + GetErrorText(code));
            }
            return OperateResult.CreateSuccessResult();
        } catch (Exception e) {
            return new OperateResult(e.getMessage());
        }
    }
}

