package com.iteaj.iboot.plugin.protocol.dahua.access;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.iot.consts.ExecStatus;

import java.util.Map;
import java.util.function.Consumer;

public class DahuaICCAccessProtocolModelApi extends AbstractFuncProtocolModelApi {

    public DahuaICCAccessProtocolModelApi(String code, String name) {
        super(code, name);
    }

    public DahuaICCAccessProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    public DahuaICCAccessProtocolModelApi(String code, String name, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, upConfig, downConfig);
    }

    public DahuaICCAccessProtocolModelApi(String code, String name, String remark, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, remark, upConfig, downConfig);
    }

    @Override
    public void invoke(ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        result.accept(InvokeResult.status(ExecStatus.offline, null));
    }
}
