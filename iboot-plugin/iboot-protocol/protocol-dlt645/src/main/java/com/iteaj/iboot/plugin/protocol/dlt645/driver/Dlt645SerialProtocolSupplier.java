package com.iteaj.iboot.plugin.protocol.dlt645.driver;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.ParamMeta;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.iboot.plugin.protocol.dlt645.network.dtu.Dlt645DtuProtocol;
import com.iteaj.iboot.plugin.protocol.dlt645.network.serial.Dlt645SerialComponent;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.serial.SerialClient;
import com.iteaj.iot.serial.SerialConnectProperties;
import org.jetbrains.annotations.NotNull;

public class Dlt645SerialProtocolSupplier extends StaticClientProtocolSupplier<NetworkConfigImpl.SerialConfig, Dlt645SerialComponent, SerialClient> implements Dlt645ProtocolSupplier {

    @Override
    public String getDesc() {
        return "基于串口的Dlt645协议驱动";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public ProtocolModel getProtocol() {
        return createProtocolModel(ProtocolCodes.SERIAL_DLT645.getValue(), TransportProtocol.SERIAL);
    }

    @Override
    public ParamMeta[] getGatewayConfig() {
        return NetworkConfigImpl.SerialConfig.serialMetas;
    }

    @Override
    public GatewayType getGatewayType() {
        return GatewayType.Gateway;
    }

    @Override
    public ProtocolImplMode getImplMode() {
        return ProtocolImplMode.Internal;
    }

    @Override
    public DataSupplier getDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, Object value) {
        DataSupplier supplier = new DataSupplier(getProtocol().getCode(), param.buildKey());
        if(CollectionUtil.isNotEmpty(param.listUpAttrs())) {
            Dlt645DtuProtocol abstractProtocol = (Dlt645DtuProtocol) protocol;
            CollectStatus status = CollectStatus.from(abstractProtocol.getExecStatus());
            param.listUpAttrs().forEach(item -> {
                String address = param.getString(PointProtocolConfig.POINT_ADDRESS);
                if(status == CollectStatus.Success) {
                    Object resolverValue = dataResolver(item, value);
                    supplier.addValue(item.getId(), address, item.getField(), resolverValue);
                } else {
                    supplier.addValue(item.getId(), address, item.getField(), status);
                }
            });
        }

        return supplier;
    }

    @Override
    protected SerialClient doCreateClient(NetworkConfigImpl.SerialConfig config, Dlt645SerialComponent component) {
        SerialConnectProperties serialConnectProperties = new SerialConnectProperties(config.getCom())
                .config(config.getBaudRate(), config.getDataBits(), config.getStopBits(), config.getParity());
        return component.createNewClient(serialConnectProperties);
    }

    @Override
    protected Dlt645SerialComponent doCreateComponent(NetworkConfigImpl.SerialConfig config) {
        return new Dlt645SerialComponent();
    }

    @NotNull
    @Override
    public Dlt645ProtocolEventModelApi createModelApi(String name, String desc, String remark) {
        return new Dlt645SerialProtocolEventModelApi(name, desc, remark);
    }
}
