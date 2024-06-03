package com.iteaj.framework.spi.iot;

import com.iteaj.iot.server.ServerComponent;

public abstract class StaticServerProtocolSupplier<C extends NetworkConfigImpl, T extends ServerComponent> extends StaticProtocolSupplier<C, T> implements ServerProtocolSupplier<C, T>{

    @Override
    protected abstract void doRefresh(T component, C config);

    protected Object dataResolver(UpModelAttr attr, Object value) {
        DataValueResolver resolver = DataValueResolverFactory.getDefault();
        if(attr.getResolver() != null && attr.getResolver().length() > 0) {
            resolver = DataValueResolverFactory.get(attr.getResolver());
        }

        if(resolver == null) {
            if(supplierLogger.isWarnEnabled()) {
                supplierLogger.warn("未找到数据解析器({}) {} - 字段: {} - 值: {}", attr.getResolver(), getDesc(), attr.getField(), value);
            }

            resolver = DataValueResolverFactory.getDefault();
        }

        return resolver.resolver(attr, value);
    }
}
