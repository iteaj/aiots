package com.iteaj.iboot.plugin.protocol.opcua.network;

import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.ProtocolException;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.client.*;
import com.iteaj.iot.client.component.SingleTcpClientComponent;
import com.iteaj.iot.client.protocol.ClientInitiativeProtocol;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

@Getter
public abstract class OpcuaProtocol extends ClientInitiativeProtocol<OpcuaMessage> {

    /**
     * 读取或者写入的值
     */
    private Object value;

    /**
     * 标签地址
     */
    private String address;

    /**
     * 协议类型
     */
    private ProtocolType protocolType;

    protected OpcuaProtocol(String address, ProtocolType protocolType) {
        this.address = address;
        this.protocolType = protocolType;
    }

    protected OpcuaProtocol(String address, Object value, ProtocolType protocolType) {
        this.value = value;
        this.address = address;
        this.protocolType = protocolType;
    }

    @Override
    protected OpcuaMessage doBuildRequestMessage() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void doBuildResponseMessage(OpcuaMessage responseMessage) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void request(String deviceSn) {
        this.setClientKey(new OpcuaClientConfig(deviceSn).setConnectKey(deviceSn));
        this.sendRequest();
    }

    @Override
    protected void sendRequest() throws OpcuaException {
        ClientComponent clientComponent = FrameworkManager.getClientComponent(this.getMessageClass());
        if(clientComponent instanceof SingleTcpClientComponent && getClientKey() != null) {
            throw new ProtocolException("组件["+clientComponent.getName()+"]属于单客户端组件, 不支持使用方法 #request(host, port)");
        }

        Object clientKey = this.getClientKey() != null ? this.getClientKey().connectKey() : clientComponent.getConfig();
        if(clientKey == null) {
            throw new ClientProtocolException("未找到可用客户端");
        }

        IotClient client = clientComponent.getClient(clientKey);
        if(client == null) {
            throw new OpcuaException("客户端不存在["+clientKey+"]");
        }

        /**
         * 执行指令并且获取返回值
         */
        this.value = doInvoke((OpcuaClientWrapper) client, NodeId.parse(getAddress()));
    }

    protected abstract Object doInvoke(OpcuaClientWrapper client, NodeId nodeId) throws OpcuaException;

    @Override
    public ProtocolType protocolType() {
        return this.protocolType;
    }

    public enum OpcuaProtocolType implements ProtocolType {
        READ("读取数据"),
        READ_OBJECT("读取对象"),
        EXEC_METHOD("执行方法"),
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
