package com.iteaj.iboot.plugin.protocol.dtu.priv;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.server.dtu.impl.CommonDtuProtocol;
import com.iteaj.iot.utils.ByteUtil;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class DtuPrivateFuncProtocolModelApi extends AbstractFuncProtocolModelApi {

    public DtuPrivateFuncProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark);
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

        if(getCode().equals("write")) {
            byte[] write = ((CommonDtuProtocol) protocol).write(msg);
            if(write != null) {
                result.accept(InvokeResult.success(write, protocol));
            } else {
                result.accept(InvokeResult.fail(((CommonDtuProtocol) protocol).getReason(), protocol));
            }
        } else {
            ((CommonDtuProtocol) protocol).writeOfAsync(msg);
            result.accept(InvokeResult.success(protocol));
        }
    }
}
