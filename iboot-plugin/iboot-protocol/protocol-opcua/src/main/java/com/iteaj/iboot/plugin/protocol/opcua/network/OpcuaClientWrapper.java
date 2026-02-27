package com.iteaj.iboot.plugin.protocol.opcua.network;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.digitalpetri.netty.fsm.ChannelFsm;
import com.digitalpetri.netty.fsm.Event;
import com.digitalpetri.netty.fsm.State;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.IotThreadManager;
import com.iteaj.iot.client.TcpSocketClient;
import com.iteaj.iot.client.component.TcpClientComponent;
import com.iteaj.iot.event.ClientStatus;
import com.iteaj.iot.event.IotEvent;
import com.iteaj.iot.event.StatusEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.nio.NioEventLoopGroup;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.client.transport.AbstractTransport;
import org.eclipse.milo.opcua.stack.client.transport.UaTransport;
import org.eclipse.milo.opcua.stack.client.transport.tcp.OpcTcpTransport;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.*;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class OpcuaClientWrapper extends TcpSocketClient implements ChannelFsm.TransitionListener {

    private UaClient opcUaClient;

    public OpcuaClientWrapper(TcpClientComponent clientComponent, OpcuaClientConfig config) {
        super(clientComponent, config);
    }

    @Override
    public void init(Object config) {
        try {
            OpcuaClientConfig properties = getConfig();
            OpcUaClientConfigBuilder builder = properties.getBuilder();
            NioEventLoopGroup workerGroup = (NioEventLoopGroup) IotThreadManager.instance().getWorkerGroup();
            if(builder == null) {
                builder = OpcUaClientConfig.builder();
                List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(properties.getEndpointUrl()).get();

                // 根据安全策略选择合适的端点
                EndpointDescription endpoint = endpoints.stream()
                        .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
                        .findFirst().orElseThrow(() -> new Exception("没有找到匹配安全策略的端点"));

                builder.setEndpoint(endpoint)
                    .setConnectTimeout(UInteger.valueOf(properties.getConnectTimeout()))
                    .setApplicationName(new LocalizedText(Locale.CHINA.getLanguage(), "AIots"));

                if(StrUtil.isNotBlank(properties.getUsername())) {
                    builder.setIdentityProvider(new UsernameProvider(properties.getUsername(), properties.getPassword()));
                } else {
                    builder.setIdentityProvider(AnonymousProvider.INSTANCE);
                }
            }

            OpcUaClientConfig clientConfig = builder.setEventLoop(workerGroup).build();
            opcUaClient = OpcUaClient.create(clientConfig);
        } catch (Exception e) {
            reconnection();
            logger.error("OpcUa客户端初始化失败", e);
        }
    }

    @Override
    public OpcuaClientConfig getConfig() {
        return (OpcuaClientConfig) super.getConfig();
    }

    @Override
    public boolean isConnect() {
        try {
            return getOpcUaClient() != null && getOpcUaClient().getSession().get() != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public synchronized void reconnection() {
        long reconnect = getConfig().getReconnect();
        if(reconnect > 0) {
            if(getClientComponent().getClient(getConfig()) != null) {
                logger.error("OpcUa客户端将在{}秒之后重连({}) 客户端: {}", reconnect, getClientComponent().getName(), getConfig());
                IotThreadManager.instance().getExecutorService().schedule(() -> {
                    if(getClientComponent().getClient(getConfig()) != null && !isConnect()) {
                        if(getOpcUaClient() == null) {
                            this.init(getConfig()); // 重新初始化
                        }

                        if(this.getOpcUaClient() != null) {
                            this.connect(); // 连接
                        }
                    }
                }, reconnect, TimeUnit.SECONDS);
            }
        }
    }

    @Override
    public ChannelFuture connect() {
        OpcuaClientConfig config = getConfig();
        try {
            getOpcUaClient().connect().get(config.getConnectTimeout(), TimeUnit.MILLISECONDS);
            UaTransport transport = getOpcUaClient().getStackClient().getTransport();
            if(transport instanceof OpcTcpTransport) {
                ((OpcTcpTransport) transport).channelFsm().addTransitionListener(this);
            }

            ChannelFuture channelFuture = getChannel().newSucceededFuture();
            finishedConnect(channelFuture); return channelFuture;
        } catch (Exception e) {
            if(e instanceof TimeoutException) {
                logger.error("OpcUa客户端连接超时({}) 客户端: {}", getClientComponent().getName(), getConfig());
            } else {
                logger.error("OpcUa客户端连接失败({}) 客户端: {}", getClientComponent().getName(), getConfig(), e);
            }

            reconnection();
            return getChannel() != null ? getChannel().newFailedFuture(e) : null;
        }
    }

    @Override
    public ChannelFuture close() {
        if(getOpcUaClient() != null) {
            return disconnect().addListener(future -> {
                if (future.isSuccess()) {
                    getClientComponent().removeClient(getConfig());
                }
            });
        } else {
            getClientComponent().removeClient(getConfig());
            return null;
        }
    }

    @Override
    public ChannelFuture disconnect() {
        if(getOpcUaClient() != null) {
            ChannelPromise channelPromise = getChannel().newPromise();

            try {
                opcUaClient.disconnect().whenCompleteAsync((client, e) -> {
                    if(e != null) {
                        channelPromise.setFailure(e);
                    } else {
                        channelPromise.setSuccess();
                    }
                });
            } catch (Exception e) {
                return getChannel().newFailedFuture(e);
            }

            return channelPromise;
        } else {
            return null;
        }
    }

    public DataValue read(NodeId nodeId) {
        return read(0, nodeId);
    }

    /**
     * 读取节点值
     * @param maxAge 参数用于指定客户端可以接受的缓存数据的最大年龄(毫秒)
     * @param nodeId 要读取的节点
     * @return 值
     */
    public DataValue read(int maxAge, NodeId nodeId) {
        try {
            return opcUaClient.readValue(maxAge, TimestampsToReturn.Both, nodeId).get();
        } catch (Exception e) {
            throw new OpcuaException(e);
        }
    }

    /**
     * 批量读取节点值(默认实时)
     * @param nodeIds 要读取的节点
     * @return 值
     */
    public List<DataValue> read(List<NodeId> nodeIds) {
        return read(0, nodeIds);
    }

    /**
     * 批量读取节点值
     * @param maxAge 参数用于指定客户端可以接受的缓存数据的最大年龄(毫秒)
     * @param nodeIds 要读取的节点列表
     * @return 值
     */
    public List<DataValue> read(int maxAge, List<NodeId> nodeIds) {
        try {
            return opcUaClient.readValues(maxAge, TimestampsToReturn.Both, nodeIds).get();
        } catch (Exception e) {
            throw new OpcuaException(e);
        }
    }

    public void write(String address, Object value) {
        this.write(NodeId.parse(address), value);
    }

    public void write(NodeId nodeId, Object value) {
        StatusCode statusCode;
        try {
            Variant variant = new Variant(value);
            DataValue dataValue = DataValue.newValue().setValue(variant).build();
            statusCode = getOpcUaClient().writeValue(nodeId, dataValue).get();
        } catch (Exception e) {
            if(e instanceof UaException) {
                throw new OpcuaException(((UaException) e).getStatusCode().toString(), e);
            }

            Throwable cause = e.getCause();
            if(cause instanceof UaException) {
                throw new OpcuaException(((UaException) cause).getStatusCode().toString(), e);
            }

            throw new OpcuaException("写入值失败", e);
        }

        if(statusCode.isBad()) {
            throw new OpcuaException(statusCode.toString());
        }
    }

    public List<StatusCode> write(List<NodeWrapper> nodes) {
        try {
            if(CollectionUtil.isEmpty(nodes)) {
                List<NodeId> nodeIds = nodes.stream().map(NodeWrapper::getNodeId).collect(Collectors.toList());
                List<DataValue> values = nodes.stream().map(NodeWrapper::getValue).collect(Collectors.toList());

                return getOpcUaClient().writeValues(nodeIds, values).get();
            }

            return Collections.emptyList();
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if(cause instanceof UaException) {
                throw new OpcuaException(((UaException) cause).getStatusCode().toString(), e);
            }

            throw new OpcuaException("写入值失败", e);
        }
    }

    @Override
    protected ChannelInboundHandler createProtocolDecoder() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Channel getChannel() {
        if(getOpcUaClient() == null) {
            throw new OpcuaException("客户端未初始化");
        }

        AbstractTransport transport = (AbstractTransport) getOpcUaClient().getStackClient().getTransport();
        try {
            return transport.channel().get();
        } catch (Exception e) {
            return null;
        }
    }

    protected OpcUaClient getOpcUaClient() {
        return (OpcUaClient) opcUaClient;
    }

    @Override
    public void onStateTransition(State state, State state1, Event event) {
        if(event instanceof Event.ChannelInactive || event instanceof Event.DisconnectSuccess) {
            FrameworkManager.publishEvent(new StatusEvent(this, ClientStatus.offline, getClientComponent()));
        } else if(event instanceof Event.ConnectSuccess) {
            FrameworkManager.publishEvent(new StatusEvent(this, ClientStatus.online, getClientComponent()));
        }
    }
}
