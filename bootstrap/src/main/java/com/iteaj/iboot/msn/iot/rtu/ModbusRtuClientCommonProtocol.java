package com.iteaj.iboot.msn.iot.rtu;

import com.iteaj.iot.ProtocolException;
import com.iteaj.iot.ProtocolHandle;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.client.SocketClient;
import com.iteaj.iot.client.protocol.ClientInitiativeSyncProtocol;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.modbus.*;
import com.iteaj.iot.modbus.client.rtu.ModbusRtuClientMessage;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.modbus.consts.ModbusCoilStatus;
import com.iteaj.iot.modbus.consts.ModbusErrCode;
import com.iteaj.iot.modbus.server.rtu.ModbusRtuBody;
import com.iteaj.iot.modbus.server.rtu.ModbusRtuHeader;
import com.iteaj.iot.serial.SerialClient;
import com.iteaj.iot.serial.SerialComponent;
import com.iteaj.iot.serial.SerialConnectProperties;
import com.iteaj.iot.server.dtu.DtuCommonProtocolType;
import com.iteaj.iot.utils.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ModbusRtuClientCommonProtocol extends ClientInitiativeSyncProtocol<ModbusRtuClientMessage> implements ModbusCommonProtocol {

    private int start;
    private ModbusCode code;
    private Payload payload;

    protected ModbusRtuClientCommonProtocol(ModbusRtuClientMessage message) {
        this.requestMessage = message;
        this.code = message.getBody().getCode();
        this.start = message.getBody().getStart();
    }

    protected ModbusRtuClientCommonProtocol(ModbusCode code, ModbusRtuClientMessage message) {
        this.code = code;
        this.requestMessage = message;
    }

    protected ModbusRtuClientCommonProtocol(int start, ModbusCode code, ModbusRtuClientMessage message) {
        this.code = code;
        this.start = start;
        this.requestMessage = message;
    }

    @Override
    protected ModbusRtuClientMessage doBuildRequestMessage() {
        if(this.requestMessage.getMessage() == null) {
            this.requestMessage.writeBuild();
        }

        return this.requestMessage;
    }

    @Override
    protected void sendRequest() throws ProtocolException {
        this.buildRequestMessage();
        IotClient iotClient = this.getRtuClient();
        if(iotClient instanceof SerialClient) {
            synchronized (iotClient) {
                if(!((SerialClient) iotClient).isOpen()) {
                    if(!((SerialClient) iotClient).open()) {
                        throw new ProtocolException("打开串口异常["+getClientKey()+"]");
                    }
                }

                // 写之前清空没有读取的报文
                int available = ((SerialClient) iotClient).bytesAvailable();
                if(available > 0) {
                    byte[] bytes = new byte[available];
                    ((SerialClient) iotClient).read(bytes);
                }

                // 写新的报文
                byte[] message = this.requestMessage().getMessage();
                ((SerialClient) iotClient).writeOfSync(message, message.length);
                long timeout = this.getTimeout();
                long currentTimeMillis = System.currentTimeMillis();

                ByteBuf buffer = Unpooled.buffer(256);
                while (readBytes((SerialClient) iotClient, buffer, protocolType()) != -1) {
                    if(System.currentTimeMillis() - currentTimeMillis > timeout) {
                        this.setExecStatus(ExecStatus.timeout); break;
                    }
                }

                if(this.getExecStatus() == ExecStatus.success && buffer.readableBytes() > 0) {
                    byte[] bytes = new byte[buffer.readableBytes()];
                    buffer.readBytes(bytes); buffer.release();

                    ModbusRtuHeader rtuHeader = ModbusRtuHeader.buildResponseHeader(bytes);
                    ModbusRtuBody modbusRtuBody = ModbusRtuBody.buildResponseBody(bytes);
                    this.responseMessage = new ModbusRtuClientMessage(rtuHeader, modbusRtuBody);
                    this.responseMessage.setMessage(bytes);
                }
            }

            this.buildResponseMessage();

            ProtocolHandle protocolHandle = this.getProtocolHandle();
            if(protocolHandle != null) {
                protocolHandle.handle(this);
            }
        }
    }

    public IotClient getRtuClient() {
        ClientConnectProperties clientKey = getClientKey();
        if(clientKey instanceof SerialConnectProperties) {
            SerialComponent instance = SerialComponent.instance();
            SerialClient client = instance.getClient(clientKey);
            if(client == null) {
                client = instance.createNewClient(clientKey);
                client.init(null);
                if(client.open()) {
                    instance.addClient(clientKey, client);
                } else {
                    throw new ProtocolException("开启串口失败["+clientKey+"]");
                }
            }

            return client;
        } else {
            throw new ProtocolException("获取客户端失败");
        }
    }

    protected int readBytes(SerialClient client, ByteBuf buffer, ModbusCode code) {
        if(client.bytesAvailable() == 0) {
            return 0; // 无需读取
        }

        int readableBytes = buffer.readableBytes();
        if(readableBytes == 0) { // 先校验是否是错误报文
            // 先读取两个字节
            byte[] message = new byte[2];
            client.readOfSync(message, (int) getTimeout());
            buffer.writeBytes(message);
            int respCode = message[1] & 0xFF;
            if(respCode > 0x80) { // 错误报文
                message = new byte[3];
                // 读取剩下的3个字节
                client.readOfSync(message, (int) getTimeout());
                buffer.writeBytes(message);

                return -1; // 结束
            }
        }

        // 写类型的报文
        if(code.getCode() >= 0x05) {
            if(readableBytes < 8) {
                // 读取剩下的六个字节
                byte[] message = new byte[6];
                client.readOfSync(message, (int) getTimeout());
                buffer.writeBytes(message);
            }

            return -1; // 结束
        } else { // 读类型的报文
            if(readableBytes > 2) { // 已经获取长度字段
                buffer.readerIndex(2);
                int length = buffer.readByte() & 0xFF;
                // 读取剩下的报文
                byte[] message = new byte[length + 2]; // 长度字段 + CRC
                client.readOfSync(message, (int) getTimeout());
                buffer.writeBytes(message).resetReaderIndex();
                return -1; //读取结束
            } else {
                // 读取第三个字节
                byte[] message = new byte[1];
                client.readOfSync(message, (int) getTimeout());
                buffer.writeBytes(message); return message.length;
            }
        }
    }

    @Override
    public SocketClient getIotClient() {
        throw new UnsupportedOperationException("不支持,请用getRtuClient()替代");
    }

    @Override
    public void doBuildResponseMessage(ModbusRtuClientMessage responseMessage) {
        if(getExecStatus() == ExecStatus.success) {
            ModbusCode code = responseMessage.getBody().getCode();
            byte[] content = responseMessage.getBody().getContent();
            if(responseMessage.getBody().getErrCode() != null) {
                return;
            }

            switch (code) {
                case Read01:
                case Read02:
                    this.payload = new RealCoilPayload(this.start, content); break;
                case Read03:
                case Read04:
                    this.payload = new ReadPayload(content, this.start); break;
                default:
                    this.payload = WritePayload.getInstance();
            }
        }
    }

    /**
     * 构建Modbus读线圈协议
     * @see ModbusCode#Read01
     * @param device 从机的设备地址
     * @param start 从哪个寄存器开始读
     * @param num 读多少个
     * @return
     */
    public static ModbusRtuClientCommonProtocol buildRead01(int device, int start, int num) {
        ModbusRtuClientMessage message = ModbusRtuMessageBuilder.buildRead01Message(device, start, num);
        return new ModbusRtuClientCommonProtocol(num, ModbusCode.Read01, message);
    }

    /**
     * 构建Modbus读线圈协议
     * @see ModbusCode#Read02
     * @param device 从机的设备地址
     * @param start 从哪个寄存器开始读
     * @param num 读多少个
     * @return
     */
    public static ModbusRtuClientCommonProtocol buildRead02(int device, int start, int num) {
        ModbusRtuClientMessage message = ModbusRtuMessageBuilder.buildRead02Message(device, start, num);
        return new ModbusRtuClientCommonProtocol(num, ModbusCode.Read02, message);
    }

    /**
     * 构建Modbus读保持寄存器报文
     * @param device 从机的设备地址
     * @param start 从哪个寄存器开始读
     * @param num 读几个寄存器
     * @return
     */
    public static ModbusRtuClientCommonProtocol buildRead03(int device, int start, int num) {
        ModbusRtuClientMessage message = ModbusRtuMessageBuilder.buildRead03Message(device, start, num);
        return new ModbusRtuClientCommonProtocol(start, ModbusCode.Read03, message);
    }

    /**
     * 构建Modbus读输入寄存器协议
     * @param device 从机的设备地址 (1-255)
     * @param start 从哪个寄存器开始读 (1-65535)
     * @param num 读几个寄存器(1-2000)
     * @return
     */
    public static ModbusRtuClientCommonProtocol buildRead04(int device, int start, int num) {
        ModbusRtuClientMessage message = ModbusRtuMessageBuilder.buildRead04Message(device, start, num);
        return new ModbusRtuClientCommonProtocol(start, ModbusCode.Read04, message);
    }

    /**
     * 构建Modbus写单个线圈报文
     * @param device 访问的设备
     * @param start 写哪个寄存器
     * @param status 写内容
     * @return
     */
    public static ModbusRtuClientCommonProtocol buildWrite05(int device, int start, ModbusCoilStatus status) {
        ModbusRtuClientMessage message = ModbusRtuMessageBuilder.buildWrite05Message(device, start, status);
        return new ModbusRtuClientCommonProtocol(ModbusCode.Write05, message);
    }

    /**
     * 构建Modbus写单个寄存器报文
     * @param device 从机的设备地址 (1-255)
     * @param start 从哪个寄存器开始写 (1-65535)
     * @param write 写内容
     * @return
     */
    public static ModbusRtuClientCommonProtocol buildWrite06(int device, int start, byte[] write) {
        ModbusRtuClientMessage message = ModbusRtuMessageBuilder.buildWrite06Message(device, start, write);
        return new ModbusRtuClientCommonProtocol(ModbusCode.Write06, message);
    }

    /**
     * 构建Modbus写单个寄存器报文
     * @param device 从机的设备地址 (1-255)
     * @param start 从哪个寄存器开始写 (1-65535)
     * @param value 写内容
     * @return
     */
    public static ModbusRtuClientCommonProtocol buildWrite06(int device, int start, short value) {
        byte[] write = ByteUtil.getBytesOfReverse(value);
        ModbusRtuClientMessage message = ModbusRtuMessageBuilder.buildWrite06Message(device, start, write);
        return new ModbusRtuClientCommonProtocol(ModbusCode.Write06, message);
    }

    /**
     * 构建Modbus写多个线圈
     *
     * @param device 从机的设备地址 (1-255)
     * @param start 从哪个寄存器开始写
     * @param write 写到设备的内容
     * @return
     */
    public static ModbusRtuClientCommonProtocol buildWrite0F(int device, int start, byte[] write) {
        ModbusRtuClientMessage message = ModbusRtuMessageBuilder.buildWrite0FMessage(device, start, write);
        return new ModbusRtuClientCommonProtocol(ModbusCode.Write0F, message);
    }

    /**
     * 构建Modbus写多个寄存器报文
     * @see com.iteaj.iot.utils.ByteUtil#getBytes(int)
     * @see com.iteaj.iot.utils.ByteUtil#getBytes(byte)
     * @see com.iteaj.iot.utils.ByteUtil#getBytes(long)
     * @see com.iteaj.iot.utils.ByteUtil#getBytes(float)
     * ......
     * @param device 从机的设备地址 (1-255)
     * @param start 从哪个寄存器开始写
     * @param num 写几个寄存器
     * @param write 写到设备的内容
     * @return
     */
    public static ModbusRtuClientCommonProtocol buildWrite10(int device, int start, int num, byte[] write) {
        ModbusRtuClientMessage message = ModbusRtuMessageBuilder.buildWrite10Message(device, start, num, write);
        return new ModbusRtuClientCommonProtocol(ModbusCode.Write10, message);
    }

    /**
     * 构建Modbus写多个寄存器报文(字符串类型使用UTF-8)
     * @see com.iteaj.iot.utils.ByteUtil#getBytes(int)
     * @see com.iteaj.iot.utils.ByteUtil#getBytes(byte)
     * @see com.iteaj.iot.utils.ByteUtil#getBytes(long)
     * @see com.iteaj.iot.utils.ByteUtil#getBytes(float)
     * ......
     * @param device 从机的设备地址 (1-255)
     * @param start 从哪个寄存器开始写
     * @param args 写到设备的内容(可以是Number类型和String类型) 如果是字符串类型使用UTF-8编码
     * @return
     */
    public static ModbusRtuClientCommonProtocol buildWrite10(int device, int start, Object... args) {
        if(ObjectUtils.isEmpty(args)) {
            throw new ModbusProtocolException("未指定要写的内容", ModbusCode.Write10);
        }

        ModbusUtil.Write10Build write10Build = ModbusUtil.write10Build(args);
        ModbusRtuClientMessage message = ModbusRtuMessageBuilder.buildWrite10Message(
                device, start, write10Build.num, write10Build.message);
        return new ModbusRtuClientCommonProtocol(ModbusCode.Write10, message);
    }

    /**
     * 根据使用自定义的message构建报文
     * @return
     */
    public static ModbusRtuClientCommonProtocol build(byte[] message) {
        return new ModbusRtuClientCommonProtocol(new ModbusRtuClientMessage(message));
    }

    @Override
    public Payload getPayload() {
        return this.payload;
    }

    @Override
    public ModbusCode protocolType() {
        return this.code;
    }
}
