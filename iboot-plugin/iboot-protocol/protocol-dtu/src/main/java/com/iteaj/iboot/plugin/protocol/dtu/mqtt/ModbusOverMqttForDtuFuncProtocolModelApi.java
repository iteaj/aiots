package com.iteaj.iboot.plugin.protocol.dtu.mqtt;

import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.data.RealtimeDeviceStatus;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.plugin.protocol.dtu.AbstractModbusFuncProtocolModelApi;
import com.iteaj.iot.FrameworkManager;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.IotClient;
import com.iteaj.iot.format.DataFormat;
import com.iteaj.iot.modbus.ModbusCommonProtocol;
import com.iteaj.iot.modbus.consts.ModbusCoilStatus;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuCommonProtocol;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuMessage;

import java.util.List;
import java.util.function.Consumer;

public class ModbusOverMqttForDtuFuncProtocolModelApi extends AbstractModbusFuncProtocolModelApi {

    public ModbusOverMqttForDtuFuncProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        buildModbusOverMqttPublish(protocol, arg).request(protocol1 -> {
            result.accept(InvokeResult.status(((ModbusRtuForDtuCommonProtocol) protocol).getExecStatus(), protocol));
        });
    }

    static ModbusOverMqttPublishProtocol buildModbusOverMqttPublish(Protocol protocol, ProtocolModelApiInvokeParam arg) {

        RealtimeDeviceStatus device = arg.getDevice();
        String productCode = device.getProductCode();
        IotClient client = getIotClient(productCode);
        if(client == null) {
            throw new ProtocolInvokeException("找不到对应的产品["+device.getProductCode()+"]客户端");
        }

        String topic = ((ModbusOverMqttConnectProperties) client.getConfig()).getPublishTopic();
        topic = topic.replace("{deviceSn}", arg.getParentDeviceSn());

        ModbusRtuForDtuCommonProtocol commonProtocol = (ModbusRtuForDtuCommonProtocol) protocol;
        ModbusRtuForDtuMessage message = commonProtocol.requestMessage();
        message.writeBuild();

        return new ModbusOverMqttPublishProtocol(message.getMessage(), arg.getParentDeviceSn(), topic, client).timeout(arg.getTimeout());
    }

    private static IotClient getIotClient(String productCode) {
        List<IotClient> clients = ModbusOverMqttVoidClient.getInstance().getClientComponent().clients();
        for (IotClient client : clients) {
            ModbusOverMqttConnectProperties clientConfig = (ModbusOverMqttConnectProperties)client.getConfig();
            if(clientConfig.getProductCode().equals(productCode)) {
                return client;
            }
        }

        return null;
    }

    @Override
    protected ModbusCommonProtocol buildWrite05(String equipCode, int device, int start, ModbusCoilStatus status) {
        return ModbusRtuForDtuCommonProtocol.buildWrite05(equipCode, device, start, status);
    }

    @Override
    protected ModbusCommonProtocol buildWrite06(String equipCode, int device, int start, short value) {
        return ModbusRtuForDtuCommonProtocol.buildWrite06(equipCode, device, start, value);
    }

    @Override
    protected ModbusCommonProtocol buildWrite0F(String equipCode, int device, int start, byte[] write) {
        return ModbusRtuForDtuCommonProtocol.buildWrite0F(equipCode, device, start, write);
    }

    @Override
    protected ModbusCommonProtocol buildWrite10(String equipCode, int device, int start, DataFormat format, Object value) {
        if(value instanceof byte[]) {
            byte[] message = (byte[]) value;
            int num = message.length % 2 == 0 ? message.length / 2 : message.length / 2 + 1;
            return ModbusRtuForDtuCommonProtocol.buildWrite10(equipCode, device, start, num, message);
        }

        return ModbusRtuForDtuCommonProtocol.buildWrite10(equipCode, device, start, format, value);
    }
}
