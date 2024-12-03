package com.iteaj.iboot.plugin.protocol.opcua.network;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.client.protocol.ClientInitiativeProtocol;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpcuaProtocol extends ClientInitiativeProtocol<OpcuaMessage> {

    private String ns;
    private String address;
    private ProtocolType protocolType;

    protected OpcuaProtocol(String ns, String address, ProtocolType protocolType) {
        this.ns = ns;
        this.address = address;
        this.protocolType = protocolType;
    }

    @Override
    protected OpcuaMessage doBuildRequestMessage() {
        return null;
    }

    @Override
    public void doBuildResponseMessage(OpcuaMessage responseMessage) {

    }

    public static OpcuaProtocol read(String ns, String address) {
        return new OpcuaProtocol(ns, address, OpcuaProtocolType.READ);
    }

    public static OpcuaProtocol write(String ns, String address) {
        return new OpcuaProtocol(ns, address, OpcuaProtocolType.WRITE);
    }

    @Override
    public ProtocolType protocolType() {
        return this.protocolType;
    }

    public enum OpcuaProtocolType implements ProtocolType {
        READ("读取数据"),
        WRITE("写入数据"),
        ;

        private final String desc;

        OpcuaProtocolType(String desc) {
            this.desc = desc;
        }

        @Override
        public Enum getType() {
            return this;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }
}
