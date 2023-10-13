package com.iteaj.framework.spi.iot;

import java.util.Objects;

/**
 * 静态协议实现
 */
public abstract class StaticProtocolSupplier<C extends NetworkConfigImpl> implements DeviceProtocolSupplier<C> {

    @Override
    public boolean equals(Object obj) {
        DeviceProtocolSupplier supplier = (DeviceProtocolSupplier) obj;
        return Objects.equals(supplier.getProtocol().getCode(), this.getProtocol().getCode());
    }
}
