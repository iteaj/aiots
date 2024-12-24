package com.iteaj.plugin.protocol.plc.siemens;

import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.plc.siemens.SiemensS7Message;
import com.iteaj.iot.plc.siemens.SiemensS7Protocol;
import com.iteaj.plugin.protocol.plc.driver.AbstractPlcFuncProtocolModelApi;

public class SiemensFuncProtocolModelApi extends AbstractPlcFuncProtocolModelApi {

    public SiemensFuncProtocolModelApi(String code, String name, FuncType type, String remark) {
        super(code, name, type, remark);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        IotClient client = FrameworkManager.getClient(arg.getParentDeviceSn(), SiemensS7Message.class);
        if(client == null) {
            throw new ServiceException("欧姆龙客户端[deviceSn="+arg.getParentDeviceSn()+"]不在线");
        }

        return new SiemensS7Protocol(client.getConfig());
    }
}
