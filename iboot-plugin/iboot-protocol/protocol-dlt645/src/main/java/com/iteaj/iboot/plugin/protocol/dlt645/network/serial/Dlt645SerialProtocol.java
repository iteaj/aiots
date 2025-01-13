package com.iteaj.iboot.plugin.protocol.dlt645.network.serial;

import com.iteaj.iboot.plugin.protocol.dlt645.Dlt645Util;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645DtuMessageBody;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645MessageHead;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645Protocol;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645ProtocolType;
import com.iteaj.iot.AbstractProtocol;
import com.iteaj.iot.ProtocolException;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.serial.SerialClient;
import com.iteaj.iot.utils.ByteUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.function.Consumer;

public class Dlt645SerialProtocol extends AbstractProtocol<Dlt645SerialMessage> implements Dlt645Protocol {

    private Object value;
    private final Dlt645ProtocolType protocolType;

    public Dlt645SerialProtocol(Dlt645ProtocolType protocolType, Dlt645SerialMessage dlt645SerialMessage) {
        this.protocolType = protocolType;
        this.setRequestMessage(dlt645SerialMessage);
    }

    @Override
    public Dlt645ProtocolType protocolType() {
        return this.protocolType;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public AbstractProtocol buildRequestMessage() {
        if(this.requestMessage.getMessage() == null) {
            this.requestMessage.writeBuild();
        }

        return this;
    }

    @Override
    public AbstractProtocol buildResponseMessage() {
        if(this.getExecStatus() == ExecStatus.success) {
            byte[] message = responseMessage().getMessage();
            Dlt645MessageHead responseHead = responseMessage().getHead();
            String reason = Dlt645Util.validate(message, responseHead.getPrefix());
            if(reason != null) {
                this.setReason(reason);
                this.setExecStatus(ExecStatus.fail);
            } else {
                byte[] bytes = Dlt645Util.getData(message, responseHead.getPrefix(), protocolType());
                if(bytes != null) {
                    String hexValue = ByteUtil.bytesToHex(bytes);
                    this.value = hexValue;
                }
            }
        } else {
            this.setReason(getExecStatus().desc);
        }
        return null;
    }

    @Override
    public void buildDlt645RequestMessage(Dlt645MessageHead dlt645MessageHead, Dlt645DtuMessageBody dlt645DtuMessageBody) {
        this.setRequestMessage(new Dlt645SerialMessage(dlt645MessageHead, dlt645DtuMessageBody));
    }

    public Dlt645SerialProtocol request(SerialClient iotClient) throws ProtocolException {
        return request(iotClient, null);
    }

    public Dlt645SerialProtocol request(SerialClient iotClient, Consumer<Dlt645SerialProtocol> consumer) throws ProtocolException {
        this.buildRequestMessage();
        synchronized (iotClient) {
            ClientConnectProperties config = iotClient.getConfig();
            if(!iotClient.isOpen()) {
                throw new ProtocolException("打开串口异常["+config.connectKey()+"]");
            }

            // 写新的报文
            byte[] message = this.requestMessage().getMessage();
            iotClient.write(message);
            // 以下协议不需要应答
            if(protocolType() == Dlt645ProtocolType.rts || protocolType() == Dlt645ProtocolType.ffCmd) {
                return this;
            }

            long currentTimeMillis = System.currentTimeMillis();
            ByteBuf buffer = Unpooled.buffer(256);
            while (readBytes(iotClient, buffer) != -1) {
                if(System.currentTimeMillis() - currentTimeMillis > config.getReaderIdleTime() * 1000) {
                    this.setExecStatus(ExecStatus.timeout); break;
                }
            }

            if(this.getExecStatus() == ExecStatus.success && buffer.readableBytes() > 0) {
                int prefix = buffer.bytesBefore((byte) 0x68);
                byte[] bytes = new byte[buffer.readableBytes()];
                buffer.readBytes(bytes);
                this.responseMessage = new Dlt645SerialMessage(bytes);
                Dlt645SerialMessage requestMessage = requestMessage();
                Dlt645MessageHead requestHead = requestMessage.getHead();
                Dlt645MessageHead responseHead = new Dlt645MessageHead(requestHead.getEquipCode()
                        , requestHead.getAddress(), requestHead.getType(), prefix);
                this.responseMessage().setHead(responseHead);
            }

            buffer.release();
        }

        this.buildResponseMessage();

        if(consumer != null) {
            consumer.accept(this);
        }

        return this;
    }

    protected int readBytes(SerialClient client, ByteBuf buffer) {
        if(client.bytesAvailable() == 0) {
            return 0; // 无需读取
        }

        int readableBytes = buffer.readableBytes();
        byte[] message = new byte[client.bytesAvailable()];
        client.read(message);
        buffer.writeBytes(message);
        if(readableBytes == 0) { // 先校验是否是错误报文
            if(message[0] != (byte) 0xFE && message[0] != (byte) 0x68) {
                this.setExecStatus(ExecStatus.fail);
                this.setReason("响应报文数据格式错误");
                return -1;
            }
        }

        Integer totalLength = null;
        int first = buffer.getByte(0) & 0xFF;
        readableBytes = buffer.readableBytes();
        if(first == 0xFE) { // 以0xFE开头
            int prefix = buffer.bytesBefore((byte) 0x68);
            if(prefix >= 0) {
                if(readableBytes >= 10 + prefix) { // 第prefix + 10个字节为长度字节
                    byte length = buffer.getByte(10 + prefix - 1);
                    totalLength = length + 10 + prefix + 2;
                }
            }
        } else { // 以0x68开头
            if(readableBytes >= 10) { // 第十个字节为长度字节
                byte length = buffer.getByte(9);
                totalLength = length + 10 + 2;
            }
        }

        if(totalLength != null) {
            if(readableBytes == totalLength) { // 读完
                return -1;
            } else if(readableBytes < totalLength){ // 读取剩余的数据
                byte[] remain = new byte[totalLength - readableBytes];
                client.read(remain);
                buffer.writeBytes(remain);
            }
        }

        return message.length;
    }
}
