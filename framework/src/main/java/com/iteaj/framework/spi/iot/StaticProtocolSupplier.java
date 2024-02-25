package com.iteaj.framework.spi.iot;

import com.iteaj.iot.FrameworkComponent;

import java.util.Objects;

/**
 * 静态协议实现
 */
public abstract class StaticProtocolSupplier<C extends NetworkConfigImpl, T extends FrameworkComponent> implements DeviceProtocolSupplier<C, T> {

    @Override
    public boolean equals(Object obj) {
        DeviceProtocolSupplier supplier = (DeviceProtocolSupplier) obj;
        return Objects.equals(supplier.getProtocol().getCode(), this.getProtocol().getCode());
    }
}
