package com.iteaj.framework.spi.iot;

import com.iteaj.iot.FrameworkComponent;
import com.iteaj.iot.FrameworkManager;

import java.util.Objects;

/**
 * 静态协议实现
 */
public abstract class StaticProtocolSupplier<C extends NetworkConfigImpl, T extends FrameworkComponent> implements DeviceProtocolSupplier<C, T> {

    private T component;

    @Override
    public T getComponent() {
        return component;
    }

    @Override
    public synchronized T createComponent(C config) {
        if(component == null) {
            component = doCreateComponent(config);
            FrameworkManager.register(component);
            ProtocolSupplierManager.register(component.getMessageClass(), this);
        }

        return component;
    }

    protected abstract T doCreateComponent(C config);

    @Override
    public boolean equals(Object obj) {
        DeviceProtocolSupplier supplier = (DeviceProtocolSupplier) obj;
        return Objects.equals(supplier.getProtocol().getCode(), this.getProtocol().getCode());
    }
}
