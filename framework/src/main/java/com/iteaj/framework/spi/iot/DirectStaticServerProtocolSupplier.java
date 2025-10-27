package com.iteaj.framework.spi.iot;

import com.iteaj.framework.spi.iot.data.ModelAttrMeta;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.server.ServerComponent;

import java.util.List;

public abstract class DirectStaticServerProtocolSupplier<C extends NetworkConfigImpl, T extends ServerComponent> extends StaticServerProtocolSupplier<C, T> {

    @Override
    public DataSupplier getDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, Object value) {
        return doGetDataSupplier(protocol, param, (List<ModelAttrMeta>) value);
    }

    protected abstract DataSupplier doGetDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, List<ModelAttrMeta> metas);
}
