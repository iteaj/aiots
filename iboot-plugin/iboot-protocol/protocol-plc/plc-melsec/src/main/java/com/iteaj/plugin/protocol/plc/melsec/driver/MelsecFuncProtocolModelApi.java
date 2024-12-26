package com.iteaj.plugin.protocol.plc.melsec.driver;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.IotClient;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcFuncProtocolModelApi;
import com.iteaj.plugin.protocol.plc.melsec.network.MelsecClient;
import com.iteaj.plugin.protocol.plc.melsec.network.MelsecMessage;
import com.iteaj.plugin.protocol.plc.melsec.network.MelsecProtocol;

public class MelsecFuncProtocolModelApi extends AbstractPlcFuncProtocolModelApi {

    public MelsecFuncProtocolModelApi(String code, String name, FuncType type, String remark) {
        super(code, name, type, remark);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        IotClient client = FrameworkManager.getClient(arg.getParentDeviceSn(), MelsecMessage.class);
        if(client == null) {
            throw new ServiceException("三菱客户端["+arg.getParentDeviceSn()+"]不存在");
        }

        return new MelsecProtocol((MelsecClient) client);
    }
}
