package com.iteaj.framework.spi.iot;

import com.iteaj.framework.spi.iot.consts.ConnectionType;
import com.iteaj.iot.server.ServerComponent;

public interface ServerProtocolSupplier<C extends NetworkConfig, T extends ServerComponent> extends DeviceProtocolSupplier<C, T> {

    default ConnectionType getConnectionType() {
        return ConnectionType.Server;
    }
}
