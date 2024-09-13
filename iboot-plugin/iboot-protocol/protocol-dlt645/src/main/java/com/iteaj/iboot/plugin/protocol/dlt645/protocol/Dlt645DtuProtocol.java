package com.iteaj.iboot.plugin.protocol.dlt645.protocol;

import com.iteaj.iot.Message;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.server.protocol.ServerInitiativeSyncProtocol;

import java.io.IOException;
import java.util.Date;

public class Dlt645DtuProtocol extends ServerInitiativeSyncProtocol<Dlt645DtuMessage> {

    private final Dlt645ProtocolType protocolType;

    public Dlt645DtuProtocol(Dlt645ProtocolType protocolType, Dlt645DtuMessage message) {
        this.setRequestMessage(message);
        this.protocolType = protocolType;
    }

    @Override
    protected Dlt645DtuMessage doBuildRequestMessage() throws IOException {
        return this.requestMessage();
    }

    @Override
    protected void doBuildResponseMessage(Dlt645DtuMessage message) {

    }

    public static Dlt645DtuProtocol read(String deviceSn, String address) {
        return read(deviceSn, address, null, null);
    }

    public static Dlt645DtuProtocol read(String deviceSn, String address, Byte blocks) {
        return read(deviceSn, address, blocks, null);
    }

    /**
     * 构建读协议
     * @param deviceSn 十六进制的地址域
     * @param address 十六进制的数据域
     * @param blocks 要读取的块数量  {@link com.sun.istack.internal.Nullable}
     * @param date 要读取的时间  {@link com.sun.istack.internal.Nullable}
     * @return 协议
     */
    public static Dlt645DtuProtocol read(String deviceSn, String address, Byte blocks, Date date) {
        Dlt645DtuMessageBody dlt645DtuMessageBody = new Dlt645DtuMessageBody(Message.EMPTY);
        Dlt645MessageHead dlt645MessageHead = new Dlt645MessageHead(deviceSn, Dlt645ProtocolType.readReq, address, blocks, date);
        Dlt645DtuMessage dtuMessage = new Dlt645DtuMessage(dlt645MessageHead, dlt645DtuMessageBody);
        return new Dlt645DtuProtocol(Dlt645ProtocolType.readReq, dtuMessage);
    }

    @Override
    public ProtocolType protocolType() {
        return this.protocolType;
    }
}
