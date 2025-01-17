package com.iteaj.plugin.protocol.plc.ab.driver;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.IotClient;
import com.iteaj.plugin.protocol.plc.adapt.PlcClientAdapter;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcFuncProtocolModelApi;

public class ABFuncProtocolModelApi extends AbstractPlcFuncProtocolModelApi {

    public ABFuncProtocolModelApi(String code, String name, FuncType type, String remark) {
        super(code, name, type, remark);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        IotClient client = FrameworkManager.getClient(arg.getParentDeviceSn(), ABMessage.class);
        if(client == null) {
            throw new ServiceException("获取不到罗克韦尔客户端[deviceSn="+arg.getParentDeviceSn()+"]");
        }

        return new ABProtocol((PlcClientAdapter) client);
    }
}
