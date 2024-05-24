package com.iteaj.framework.spi.iot;

import com.iteaj.iot.server.ServerComponent;

public abstract class StaticServerProtocolSupplier<C extends NetworkConfigImpl, T extends ServerComponent> extends StaticProtocolSupplier<C, T> implements ServerProtocolSupplier<C, T>{

    @Override
    protected abstract void doRefresh(T component, C config);
}
