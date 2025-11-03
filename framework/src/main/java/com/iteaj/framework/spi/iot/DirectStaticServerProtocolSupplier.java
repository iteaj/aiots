package com.iteaj.framework.spi.iot;

import com.iteaj.framework.spi.iot.data.ModelAttrMeta;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.server.ServerComponent;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public abstract class DirectStaticServerProtocolSupplier<C extends NetworkConfigImpl, T extends ServerComponent> extends StaticServerProtocolSupplier<C, T> {

    @Override
    public DataSupplier getDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, Object value) {
        return doGetDataSupplier(protocol, param, (Map<Serializable, ModelAttrMeta>) value);
    }

    /**
     * @param protocol 协议
     * @param param 请求参数
     * @param metas 模型属性 id -> modelAttrMeta
     * @return DataSupplier
     */
    protected abstract DataSupplier doGetDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, Map<Serializable, ModelAttrMeta> metas);
}
