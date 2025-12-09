package com.iteaj.iboot.plugin.protocol.dtu.priv;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractEventProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.server.dtu.impl.CommonDtuProtocol;
import com.iteaj.iot.utils.ByteUtil;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class DtuPrivateEventProtocolModelApi extends AbstractEventProtocolModelApi {

    public DtuPrivateEventProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark, TriggerMode.passive);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        return new CommonDtuProtocol(arg.getParentDeviceSn());
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        byte[] msg;
        String data = arg.getString("data");
        String type = arg.getString("type");
        if(type.equals("hex")) {
            msg = ByteUtil.hexToByte(data);
        } else {
            msg = data.getBytes(StandardCharsets.UTF_8);
        }

        byte[] value = ((CommonDtuProtocol) protocol).timeout(arg.getTimeout()).read(msg);
        if(value != null) {
            System.out.println("带解析的中两只: " + new String( value));
            result.accept(InvokeResult.success(value, protocol));
        } else {
            result.accept(InvokeResult.fail(((CommonDtuProtocol) protocol).getReason(), protocol));
        }
    }
}
