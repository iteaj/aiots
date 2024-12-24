package com.iteaj.plugin.protocol.plc.omron;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.plc.omron.OmronConnectProperties;
import com.iteaj.iot.plc.omron.OmronMessage;
import com.iteaj.iot.plc.omron.OmronTcpProtocol;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcFuncProtocolModelApi;

public class OmronFuncProtocolModelApi extends AbstractPlcFuncProtocolModelApi {

    public OmronFuncProtocolModelApi(String code, String name, FuncType type, String remark) {
        super(code, name, type, remark);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        IotClient client = FrameworkManager.getClient(arg.getParentDeviceSn(), OmronMessage.class);
        if(client == null) {
            throw new ServiceException("欧姆龙客户端[deviceSn="+arg.getParentDeviceSn()+"]不在线");
        }

        return new OmronTcpProtocol((OmronConnectProperties) client.getConfig());
    }
}
