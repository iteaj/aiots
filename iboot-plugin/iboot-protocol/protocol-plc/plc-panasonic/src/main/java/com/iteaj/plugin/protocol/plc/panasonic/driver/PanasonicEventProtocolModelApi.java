package com.iteaj.plugin.protocol.plc.panasonic.driver;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.NotIotClientException;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.IotClient;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcEventProtocolModelApi;

public class PanasonicEventProtocolModelApi extends AbstractPlcEventProtocolModelApi {

    public PanasonicEventProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark, TriggerMode.passive);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        IotClient client = FrameworkManager.getClient(arg.getParentDeviceSn(), PanasonicMessage.class);
        if(client == null) {
            throw new NotIotClientException("获取不到松下PLC客户端["+arg.getParentDeviceSn()+"]");
        }

        return new PanasonicProtocol((PlcClientAdapter) client);
    }
}
