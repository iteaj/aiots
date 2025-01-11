package com.iteaj.iboot.plugin.protocol.dlt645.network.dtu;

import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645DtuMessageBody;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645MessageHead;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645ProtocolType;
import com.iteaj.iot.Message;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.server.protocol.ServerInitiativeSyncProtocol;

import java.io.IOException;
import java.util.Date;

public class Dlt645DtuProtocol extends ServerInitiativeSyncProtocol<Dlt645DtuMessage> {

    private Object value;
    private final Dlt645ProtocolType protocolType;

    public Dlt645DtuProtocol(Dlt645ProtocolType protocolType, Dlt645DtuMessage message) {
        this.setRequestMessage(message);
        this.protocolType = protocolType;
    }

    @Override
    protected Dlt645DtuMessage doBuildRequestMessage() throws IOException {
        this.requestMessage().writeBuild();
        return this.requestMessage();
    }

    @Override
    protected void doBuildResponseMessage(Dlt645DtuMessage message) {

    }

    public static Dlt645DtuProtocol read(String deviceSn, String address, String data) {
        return read(deviceSn, address, data, null);
    }

    public static Dlt645DtuProtocol read(String deviceSn, String address, String data, Byte blocks) {
        return read(deviceSn, address, data, blocks, null);
    }

    /**
     * 构建读协议
     * @param deviceSn 设备编号
     * @param address 地址域
     * @param data 数据域
     * @param blocks 要读取的块数量  {@link com.sun.istack.internal.Nullable}
     * @param date 要读取的时间  {@link com.sun.istack.internal.Nullable}
     * @return 协议
     */
    public static Dlt645DtuProtocol read(String deviceSn, String address, String data, Byte blocks, Date date) {
        Dlt645DtuMessageBody dlt645DtuMessageBody = new Dlt645DtuMessageBody(data).read();
        Dlt645MessageHead dlt645MessageHead = new Dlt645MessageHead(deviceSn, address, Dlt645ProtocolType.readReq);
        Dlt645DtuMessage dtuMessage = new Dlt645DtuMessage(dlt645MessageHead, dlt645DtuMessageBody);
        return new Dlt645DtuProtocol(Dlt645ProtocolType.readReq, dtuMessage);
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public ProtocolType protocolType() {
        return this.protocolType;
    }
}
