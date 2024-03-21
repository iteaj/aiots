package com.iteaj.iboot.plugin.protocol.dahua.video;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.consts.ExecStatus;

import java.util.Map;
import java.util.function.Consumer;

public class DahuaICCVideoProtocolModelApi extends AbstractFuncProtocolModelApi {

    public DahuaICCVideoProtocolModelApi(String code, String name, FuncType type) {
        super(code, name, type);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        return null;
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {

    }

}
