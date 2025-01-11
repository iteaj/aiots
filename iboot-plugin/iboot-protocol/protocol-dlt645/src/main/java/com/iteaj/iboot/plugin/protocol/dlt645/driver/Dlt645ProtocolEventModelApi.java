package com.iteaj.iboot.plugin.protocol.dlt645.driver;

import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractEventProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645ProtocolType;
import com.iteaj.iboot.plugin.protocol.dlt645.network.dtu.Dlt645DtuProtocol;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.consts.ExecStatus;

import java.util.function.Consumer;

public class Dlt645ProtocolEventModelApi extends AbstractEventProtocolModelApi {

    public Dlt645ProtocolEventModelApi(String code, String name) {
        this(code, name, null);
    }

    public Dlt645ProtocolEventModelApi(String code, String name, String remark) {
        super(code, name, remark, TriggerMode.passive);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        String address = arg.getDeviceSn();
        String deviceSn = arg.getParentDeviceSn();
        String data = arg.getString("data");

        Dlt645ProtocolType type = Dlt645ProtocolType.valueOf(getCode());
        switch (type) {
            case readReq:
                return Dlt645DtuProtocol.read(deviceSn, address, data);
            default:
                throw new ProtocolInvokeException("不支持的指令["+type+"]");
        }
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        Dlt645DtuProtocol dtuProtocol = (Dlt645DtuProtocol) protocol;
        dtuProtocol.request();
        if(dtuProtocol.getExecStatus() == ExecStatus.success) {
            result.accept(InvokeResult.success(dtuProtocol.getValue(), dtuProtocol));
        } else {
            result.accept(InvokeResult.fail(dtuProtocol.getReason(), dtuProtocol));
        }
    }
}
