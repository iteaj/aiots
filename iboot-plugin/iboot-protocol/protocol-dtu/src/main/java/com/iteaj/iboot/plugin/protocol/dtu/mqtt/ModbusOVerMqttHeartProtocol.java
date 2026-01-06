package com.iteaj.iboot.plugin.protocol.dtu.mqtt;

import com.iteaj.framework.spi.iot.consts.ProtocolCodes;
import com.iteaj.framework.spi.iot.data.RealtimeDeviceStatus;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.iboot.plugin.protocol.dtu.ModbusProtocolSupplierAutoConfiguration;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.client.protocol.ServerInitiativeProtocol;
import com.iteaj.iot.server.dtu.DtuCommonProtocolType;
import com.iteaj.iot.utils.ByteUtil;

public class ModbusOVerMqttHeartProtocol extends ServerInitiativeProtocol<ModbusOverMqttMessage> {

    private static final String STATUS = "online";

    public ModbusOVerMqttHeartProtocol(ModbusOverMqttMessage requestMessage) {
        super(requestMessage);
    }

    @Override
    protected void doBuildRequestMessage(ModbusOverMqttMessage requestMessage) {
        String deviceSn = new String(requestMessage.getMessage());
        if(requestMessage.getTopic().contains(deviceSn)) {
            RealtimeDeviceStatus deviceStatus = ModbusProtocolSupplierAutoConfiguration.realtimeDataService
                    .getDeviceStatus(ProtocolCodes.DTU_MODBUS_OVER_MQTT.getValue(), deviceSn);
            if(deviceStatus != null) {
                if(!STATUS.equals(deviceStatus.getDeviceStatus())) {
                    deviceStatus.triggerStatus();
                    IotEvenPublisher.publish(IotEventType.DeviceStatus, deviceStatus);
                }
            } else if(logger.isWarnEnabled()){
                logger.warn("DTU设备不存在 设备编号: {}", deviceSn);
            }
        } else {
            if(logger.isDebugEnabled()) {
                logger.warn("<<< 未知的DTU报文: {}", ByteUtil.bytesToHexByFormat(requestMessage.getMessage()));
            }
        }
    }

    @Override
    protected ModbusOverMqttMessage doBuildResponseMessage() {
        return null;
    }

    @Override
    public ProtocolType protocolType() {
        return DtuCommonProtocolType.HEARTBEAT;
    }
}
