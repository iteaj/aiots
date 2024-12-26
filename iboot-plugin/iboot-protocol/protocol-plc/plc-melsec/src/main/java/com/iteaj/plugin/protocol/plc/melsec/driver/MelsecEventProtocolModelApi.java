package com.iteaj.plugin.protocol.plc.melsec.driver;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.NotIotClientException;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.IotClient;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcEventProtocolModelApi;
import com.iteaj.plugin.protocol.plc.melsec.network.MelsecClient;
import com.iteaj.plugin.protocol.plc.melsec.network.MelsecMessage;
import com.iteaj.plugin.protocol.plc.melsec.network.MelsecProtocol;

public class MelsecEventProtocolModelApi extends AbstractPlcEventProtocolModelApi {

    public MelsecEventProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark, TriggerMode.passive);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        IotClient client = FrameworkManager.getClient(arg.getParentDeviceSn(), MelsecMessage.class);
        if(client == null) {
            throw new NotIotClientException("三菱客户端["+arg.getParentDeviceSn()+"]不存在");
        }

        return new MelsecProtocol((MelsecClient) client);
    }
}
