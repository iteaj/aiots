package com.iteaj.iboot.plugin.protocol.dlt645.driver;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.NetworkConfigImpl;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.StaticServerProtocolSupplier;
import com.iteaj.framework.spi.iot.consts.*;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.iboot.plugin.protocol.dlt645.network.dtu.Dlt645DtuComponent;
import com.iteaj.iboot.plugin.protocol.dlt645.network.dtu.Dlt645DtuMessage;
import com.iteaj.iboot.plugin.protocol.dlt645.network.dtu.Dlt645DtuMessageAware;
import com.iteaj.iboot.plugin.protocol.dlt645.network.dtu.Dlt645DtuProtocol;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.server.dtu.DefaultDtuMessageAware;
import com.iteaj.iot.server.dtu.DtuMessageAware;
import com.iteaj.iot.server.dtu.DtuMessageType;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

public class Dlt645DtuProtocolSupplier extends StaticServerProtocolSupplier<Dlt645DtuProtocolSupplier.DtuNetworkConfig, Dlt645DtuComponent> implements Dlt645ProtocolSupplier {

    @Override
    protected void doRefresh(Dlt645DtuComponent component, DtuNetworkConfig config) {
        if(!component.isStart()) {
            DtuMessageAware<Dlt645DtuMessage> delegation = component.getDtuMessageAwareDelegation();
            if(delegation.messageType() != config.getMessageType()) {
                if(delegation instanceof DefaultDtuMessageAware) {
                    ((DefaultDtuMessageAware<?>) delegation).setMessageType(config.getMessageType());
                }
            }

            ConnectProperties properties = component.config();
            if(!properties.getPort().equals(config.getPort())) {
                properties.setPort(config.getPort());
            }

            if(!properties.getHost().equals(config.getHost())) {
                properties.setHost(config.getHost());
            }

            if(config.getReadIdle() != null && config.getReadIdle() > 0) {
                properties.setReaderIdleTime(config.getReadIdle());
            }
        }
    }

    @Override
    protected Dlt645DtuComponent doCreateComponent(DtuNetworkConfig config) {
        ConnectProperties properties = new ConnectProperties(config.getHost(), config.getPort());
        return new Dlt645DtuComponent(properties, new Dlt645DtuMessageAware(config.getMessageType()));
    }

    @Override
    public String getDesc() {
        return "基于DTU设备的Dlt645协议驱动";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public ProtocolModel getProtocol() {
        return createProtocolModel(ProtocolCodes.DTU_DLT645.getValue(), TransportProtocol.TCP);
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

    @NotNull
    @Override
    public Dlt645ProtocolEventModelApi createModelApi(String name, String desc, String remark) {
        return new Dlt645DtuProtocolEventModelApi(name, desc, remark);
    }

    @Getter
    @Setter
    public static class DtuNetworkConfig extends NetworkConfigImpl.TcpNetworkConfig {
        private DtuMessageType messageType;
    }
}
