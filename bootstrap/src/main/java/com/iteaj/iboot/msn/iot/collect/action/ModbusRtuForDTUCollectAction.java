package com.iteaj.iboot.msn.iot.collect.action;

import com.iteaj.iboot.msn.iot.consts.IotConsts;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.dto.DeviceDto;
import com.iteaj.iboot.msn.iot.entity.CollectDetail;
import com.iteaj.iboot.msn.iot.entity.Signal;
import com.iteaj.iot.modbus.ModbusCommonProtocol;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuCommonProtocol;

import java.util.function.Consumer;

/**
 * modbus rtu for dtu采集动作
 */
public class ModbusRtuForDTUCollectAction extends AbstractModbusCollectAction {

    @Override
    public String getName() {
        return IotConsts.COLLECT_ACTION_MODBUS_DTU_RTU;
    }

    @Override
    public String getDesc() {
        return "DTU+ModbusRtu采集器";
    }

    @Override
    protected ModbusCommonProtocol getModbusCommonProtocol(Integer type, String deviceSn, Integer childSn, Integer address, Integer num) {
        switch (type) {
            case IotConsts.FIELD_TYPE_BOOLEAN:
                return ModbusRtuForDtuCommonProtocol.buildRead01(deviceSn, childSn, address, 1);
            case IotConsts.FIELD_TYPE_SHORT:
                return ModbusRtuForDtuCommonProtocol.buildRead03(deviceSn, childSn, address, 1);
            case IotConsts.FIELD_TYPE_INT:
            case IotConsts.FIELD_TYPE_FLOAT:
                return ModbusRtuForDtuCommonProtocol.buildRead03(deviceSn, childSn, address, 2);
            case IotConsts.FIELD_TYPE_DOUBLE:
            case IotConsts.FIELD_TYPE_LONG:
                return ModbusRtuForDtuCommonProtocol.buildRead03(deviceSn, childSn, address, 4);
            default:
                return ModbusRtuForDtuCommonProtocol.buildRead03(deviceSn, childSn, address, num);
        }
    }
}
