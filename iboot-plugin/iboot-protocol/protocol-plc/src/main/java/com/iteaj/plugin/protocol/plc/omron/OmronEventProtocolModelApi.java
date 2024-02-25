package com.iteaj.plugin.protocol.plc.omron;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.EventTriggerMode;
import com.iteaj.framework.spi.iot.protocol.NotIotClientException;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.plc.PlcClientProtocol;
import com.iteaj.iot.plc.omron.OmronConnectProperties;
import com.iteaj.iot.plc.omron.OmronMessage;
import com.iteaj.iot.plc.omron.OmronTcpProtocol;
import com.iteaj.plugin.protocol.plc.AbstractPlcEventProtocolModelApi;

import java.util.Map;

public class OmronEventProtocolModelApi extends AbstractPlcEventProtocolModelApi {

    public OmronEventProtocolModelApi(String code, String name) {
        this(code, name, null);
    }

    public OmronEventProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark, EventTriggerMode.passive);
    }

    public OmronEventProtocolModelApi(String code, String name, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        this(code, name, null, upConfig, downConfig);
    }

    public OmronEventProtocolModelApi(String code, String name, String remark, Map<String, ProtocolModelApiConfig> upConfig, Map<String, ProtocolModelApiConfig> downConfig) {
        super(code, name, remark, EventTriggerMode.passive, upConfig, downConfig);
    }

    @Override
    protected PlcClientProtocol createPlcProtocol(ProtocolModelApiInvokeParam arg) {
        IotClient client = FrameworkManager.getClient(arg.getDeviceSn(), OmronMessage.class);
        if(client == null) {
            throw new NotIotClientException("获取不到欧姆龙客户端["+arg.getDeviceSn()+"]");
        }

        return new OmronTcpProtocol((OmronConnectProperties) client.getConfig());
    }
}
