package com.iteaj.iboot.plugin.protocol.dahua.access;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.consts.ExecStatus;

import java.util.Map;
import java.util.function.Consumer;

public class DahuaICCAccessProtocolModelApi extends AbstractFuncProtocolModelApi {

    public DahuaICCAccessProtocolModelApi(String code, String name, String remark) {
        super(code, name, FuncType.write, remark);
    }

    @Override
    public void invoke(ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        result.accept(InvokeResult.status(ExecStatus.offline, null));
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        return null;
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {

    }
}
