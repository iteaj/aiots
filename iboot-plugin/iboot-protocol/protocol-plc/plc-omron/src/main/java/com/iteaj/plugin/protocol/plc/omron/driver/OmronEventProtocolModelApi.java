package com.iteaj.plugin.protocol.plc.omron.driver;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.NotIotClientException;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.IotClient;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcEventProtocolModelApi;
import com.iteaj.plugin.protocol.plc.omron.network.OmromProtocol;
import com.iteaj.plugin.protocol.plc.omron.network.OmronMessage;

public class OmronEventProtocolModelApi extends AbstractPlcEventProtocolModelApi {

    public OmronEventProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark, TriggerMode.passive);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        IotClient client = FrameworkManager.getClient(arg.getParentDeviceSn(), OmronMessage.class);
        if(client == null) {
            throw new NotIotClientException("获取不到欧姆龙客户端["+arg.getParentDeviceSn()+"]");
        }

        return new OmromProtocol((PlcClientAdapter) client);
    }
}
