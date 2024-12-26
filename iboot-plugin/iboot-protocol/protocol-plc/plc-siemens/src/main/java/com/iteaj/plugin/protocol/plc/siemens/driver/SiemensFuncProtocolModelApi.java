package com.iteaj.plugin.protocol.plc.siemens.driver;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.IotClient;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcFuncProtocolModelApi;
import com.iteaj.plugin.protocol.plc.siemens.network.SiemensMessage;
import com.iteaj.plugin.protocol.plc.siemens.network.SiemensS7Client;
import com.iteaj.plugin.protocol.plc.siemens.network.SiemensS7Protocol;

public class SiemensFuncProtocolModelApi extends AbstractPlcFuncProtocolModelApi {

    public SiemensFuncProtocolModelApi(String code, String name, FuncType type, String remark) {
        super(code, name, type, remark);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        IotClient client = FrameworkManager.getClient(arg.getParentDeviceSn(), SiemensMessage.class);
        if(client == null) {
            throw new ServiceException("西门子客户端["+arg.getParentDeviceSn()+"]不存在");
        }

        return new SiemensS7Protocol((SiemensS7Client) client);
    }
}
