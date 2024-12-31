package com.iteaj.plugin.protocol.plc.adapt;

import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.IotThreadManager;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.event.ClientStatus;
import com.iteaj.iot.event.IotEvent;
import com.iteaj.iot.event.StatusEvent;
import com.iteaj.plugin.protocol.plc.core.log.core.ILogNet;
import com.iteaj.plugin.protocol.plc.core.net.IReadWriteNet;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDoubleBase;
import com.iteaj.plugin.protocol.plc.core.types.IDataTransfer;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public abstract class PlcClientAdapter implements IotClient, IReadWriteNet {

    /**
     * 状态(close, disconnect, connecting, connected, reconnect)
     */
    private String status = "close";
    private IReadWriteNet readWriteNet;
    private PlcClientComponent clientComponent;
    private NetworkDoubleBase networkDoubleBase;
    private PlcConnectProperties connectProperties;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public PlcClientAdapter(PlcClientComponent clientComponent, PlcConnectProperties connectProperties) {
        this.clientComponent = clientComponent;
        this.connectProperties = connectProperties;
    }

    @Override
    public PlcConnectProperties getConfig() {
        return this.connectProperties;
    }

    @Override
    public void init(Object config) {
        this.networkDoubleBase = doInit(getConfig());
        if(this.networkDoubleBase instanceof IReadWriteNet) {
            this.readWriteNet = (IReadWriteNet) this.networkDoubleBase;
        }
    }

    protected abstract NetworkDoubleBase doInit(PlcConnectProperties config);

    public IReadWriteNet getReadWriteNet() {
        return readWriteNet;
    }

    @Override
    public Boolean connect() {
        OperateResult operateResult = this.networkDoubleBase.ConnectServer();
        PlcClientComponent clientComponent = getClientComponent();
        if(operateResult.IsSuccess) {
            if(this.status.equals("reconnect")) {
                logger.info("重连PLC成功({}) - 客户端: {}", clientComponent.getProtocolType().getDesc()
                        , getConfig(), getConfig().getReconnect());
            } else {
                logger.info("连接PLC成功({}) - 客户端: {}", getConfig(), clientComponent.getProtocolType().getDesc());
            }

            this.status = "connected";
            // todo 由IotNetworkUtil发布状态事件
//            FrameworkManager.publishEvent(new StatusEvent(this, ClientStatus.online, clientComponent));
        } else {
            if(this.status.equals("reconnect")) {
                logger.error("重连PLC失败({}) 重连时间: {}(s) - 客户端: {}", clientComponent.getProtocolType().getDesc()
                        , getConfig().getReconnect(), getConfig(), new PlcProtocolException(operateResult.Message));
            } else {
                logger.error("连接PLC失败({}) - 客户端: {}", clientComponent.getProtocolType().getDesc()
                        , getConfig(), new PlcProtocolException(operateResult.Message));
            }

            this.reconnect(); // 重连
        }

        return operateResult.IsSuccess;
    }

    @Override
    public Boolean disconnect() {
        // todo 由IotNetworkUtil发布状态事件
        boolean isSuccess = this.readWriteNet.ConnectClose().IsSuccess;
        this.status = isSuccess ? "disconnect" : this.status;
        return isSuccess;
    }

    @Override
    public Boolean close() {
        // todo 由IotNetworkUtil发布状态事件
        Boolean disconnect = disconnect();
        if(disconnect) {
            this.status = "close";
            this.getClientComponent().removeClient(getConfig());
        } else {
            return false;
        }

        return true;
    }

    public PlcClientAdapter publishEvent(ClientStatus status) {
        FrameworkManager.publishEvent(new StatusEvent(PlcClientAdapter.this, status, getClientComponent()));
        return this;
    }

    public synchronized void reconnect() {
        if(getClientComponent().isExists(getConfig().connectKey())) {
            this.status = "reconnect";
            long reconnect = getConfig().getReconnect();
            IotThreadManager.instance().getExecutorService().schedule(() -> {
                // 客户端还存在且处理重连状态
                if(getClientComponent().isExists(getConfig().connectKey()) && this.status.equals("reconnect")) {
                    Boolean connect = this.connect();
                    if(connect) {
                        publishEvent(ClientStatus.online);
                    }
                }
            }, reconnect, TimeUnit.SECONDS);
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public PlcClientComponent getClientComponent() {
        return this.clientComponent;
    }

    @Override
    public void setLogNet(ILogNet logNet) {
        this.readWriteNet.setLogNet(logNet);
    }

    @Override
    public String getConnectionId() {
        return this.readWriteNet.getConnectionId();
    }

    @Override
    public void setConnectionId(String connectionId) {
        this.readWriteNet.setConnectionId(connectionId);
    }

    @Override
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return this.readWriteNet.Read(address, length);
    }

    @Override
    public OperateResult Write(String address, byte[] value) {
        return this.readWriteNet.Write(address, value);
    }

    @Override
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return this.readWriteNet.ReadBool(address, length);
    }

    @Override
    public OperateResultExOne<Boolean> ReadBool(String address) {
        return this.readWriteNet.ReadBool(address);
    }

    @Override
    public OperateResult Write(String address, boolean[] value) {
        return this.readWriteNet.Write(address, value);
    }

    @Override
    public OperateResult Write(String address, boolean value) {
        return this.readWriteNet.Write(address, value);
    }

    @Override
    public OperateResultExOne<Short> ReadInt16(String address) {
        return this.readWriteNet.ReadInt16(address);
    }

    @Override
    public OperateResultExOne<short[]> ReadInt16(String address, short length) {
        return this.readWriteNet.ReadInt16(address, length);
    }

    @Override
    public OperateResultExOne<Integer> ReadUInt16(String address) {
        return this.readWriteNet.ReadUInt16(address);
    }

    @Override
    public OperateResultExOne<int[]> ReadUInt16(String address, short length) {
        return this.readWriteNet.ReadUInt16(address, length);
    }

    @Override
    public OperateResultExOne<Integer> ReadInt32(String address) {
        return this.readWriteNet.ReadInt32(address);
    }

    @Override
    public OperateResultExOne<int[]> ReadInt32(String address, short length) {
        return this.readWriteNet.ReadInt32(address, length);
    }

    @Override
    public OperateResultExOne<Long> ReadUInt32(String address) {
        return this.readWriteNet.ReadUInt32(address);
    }

    @Override
    public OperateResultExOne<long[]> ReadUInt32(String address, short length) {
        return this.readWriteNet.ReadUInt32(address, length);
    }

    @Override
    public OperateResultExOne<Long> ReadInt64(String address) {
        return this.readWriteNet.ReadInt64(address);
    }

    @Override
    public OperateResultExOne<long[]> ReadInt64(String address, short length) {
        return this.readWriteNet.ReadInt64(address, length);
    }

    @Override
    public OperateResultExOne<Float> ReadFloat(String address) {
        return this.readWriteNet.ReadFloat(address);
    }

    @Override
    public OperateResultExOne<float[]> ReadFloat(String address, short length) {
        return this.readWriteNet.ReadFloat(address, length);
    }

    @Override
    public OperateResultExOne<Double> ReadDouble(String address) {
        return this.readWriteNet.ReadDouble(address);
    }

    @Override
    public OperateResultExOne<double[]> ReadDouble(String address, short length) {
        return this.readWriteNet.ReadDouble(address, length);
    }

    @Override
    public OperateResultExOne<String> ReadString(String address, short length) {
        return this.readWriteNet.ReadString(address, length);
    }

    @Override
    public OperateResultExOne<String> ReadString(String address, short length, Charset encoding) {
        return this.readWriteNet.ReadString(address, length, encoding);
    }

    @Override
    public <T extends IDataTransfer> OperateResultExOne<T> ReadCustomer(String address, Class<T> tClass) {
        return this.readWriteNet.ReadCustomer(address, tClass);
    }

    @Override
    public OperateResult Write(String address, short value) {
        return this.readWriteNet.Write(address, value);
    }

    @Override
    public OperateResult Write(String address, short[] values) {
        return this.readWriteNet.Write(address, values);
    }

    @Override
    public OperateResult Write(String address, int value) {
        return this.readWriteNet.Write(address, value);
    }

    @Override
    public OperateResult Write(String address, int[] values) {
        return this.readWriteNet.Write(address, values);
    }

    @Override
    public OperateResult Write(String address, long value) {
        return this.readWriteNet.Write(address, value);
    }

    @Override
    public OperateResult Write(String address, long[] values) {
        return this.readWriteNet.Write(address, values);
    }

    @Override
    public OperateResult Write(String address, float value) {
        return this.readWriteNet.Write(address, value);
    }

    @Override
    public OperateResult Write(String address, float[] values) {
        return this.readWriteNet.Write(address, values);
    }

    @Override
    public OperateResult Write(String address, double value) {
        return this.readWriteNet.Write(address, value);
    }

    @Override
    public OperateResult Write(String address, double[] values) {
        return this.readWriteNet.Write(address, values);
    }

    @Override
    public OperateResult Write(String address, String value) {
        return this.readWriteNet.Write(address, value);
    }

    @Override
    public OperateResult Write(String address, String value, int length) {
        return this.readWriteNet.Write(address, value, length);
    }

    @Override
    public OperateResult Write(String address, String value, Charset encoding) {
        return this.readWriteNet.Write(address, value, encoding);
    }

    @Override
    public OperateResult Write(String address, String value, int length, Charset encoding) {
        return this.readWriteNet.Write(address, value, length, encoding);
    }

    @Override
    public <T extends IDataTransfer> OperateResult WriteCustomer(String address, T value) {
        return this.readWriteNet.WriteCustomer(address, value);
    }

    @Override
    public OperateResult ConnectClose() {
        return this.readWriteNet.ConnectClose();
    }
}
