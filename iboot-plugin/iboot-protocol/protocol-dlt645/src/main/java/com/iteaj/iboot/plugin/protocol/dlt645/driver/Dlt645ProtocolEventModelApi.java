package com.iteaj.iboot.plugin.protocol.dlt645.driver;

import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractEventProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645Protocol;
import com.iteaj.iboot.plugin.protocol.dlt645.network.Dlt645ProtocolType;
import com.iteaj.iboot.plugin.protocol.dlt645.network.dtu.Dlt645DtuProtocol;
import com.iteaj.iboot.plugin.protocol.dlt645.network.serial.Dlt645SerialProtocol;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.serial.SerialClient;
import com.iteaj.iot.serial.SerialComponent;

import java.util.function.Consumer;

public abstract class Dlt645ProtocolEventModelApi extends AbstractEventProtocolModelApi {

    public Dlt645ProtocolEventModelApi(String code, String name, String remark) {
        super(code, name, remark, TriggerMode.passive);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        String address = arg.getDeviceSn();
        String deviceSn = arg.getParentDeviceSn();
        String data = arg.getString("data");

        Dlt645ProtocolType type = Dlt645ProtocolType.valueOf(getCode());
        Dlt645Protocol dlt645Protocol = this.createProtocol(type);
        switch (type) {
            case readReq:
                return dlt645Protocol.readBuild(deviceSn, address, data);
            default:
                throw new ProtocolInvokeException("不支持的指令["+type+"]");
        }
    }

    protected abstract Dlt645Protocol createProtocol(Dlt645ProtocolType type);

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        if(protocol instanceof Dlt645DtuProtocol) {
            Dlt645DtuProtocol dtuProtocol = (Dlt645DtuProtocol) protocol;
            dtuProtocol.request();
            if(dtuProtocol.getExecStatus() == ExecStatus.success) {
                result.accept(InvokeResult.success(dtuProtocol.getValue(), dtuProtocol));
            } else {
                result.accept(InvokeResult.fail(dtuProtocol.getReason(), dtuProtocol));
            }
        } else {
            Dlt645SerialProtocol serialProtocol = (Dlt645SerialProtocol) protocol;
            IotClient iotClient = SerialComponent.instance().get(serialProtocol.getEquipCode());
            if(iotClient == null) {
                throw new ProtocolInvokeException("串口客户端不存在[com="+serialProtocol.getEquipCode()+"]");
            }

            serialProtocol.request((SerialClient) iotClient, null);
            if(serialProtocol.getExecStatus() == ExecStatus.success) {
                result.accept(InvokeResult.success(serialProtocol.getValue(), serialProtocol));
            } else {
                result.accept(InvokeResult.fail(serialProtocol.getReason(), serialProtocol));
            }
        }
    }
}
