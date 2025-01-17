package com.iteaj.plugin.protocol.plc.core.modbus.helper;
import java.util.HashMap;

public class ModbusDataDict {
    public ModbusDataDict() {
        dictModbusDataPool = new HashMap<>();
        dictModbusDataPool.put(1, new ModbusDataPool((byte) 1));
    }

    public void Set(boolean stationDataIsolation) {
        if (this.stationDataIsolation != stationDataIsolation) {
            this.stationDataIsolation = stationDataIsolation;
            if (this.stationDataIsolation) {
                dictModbusDataPool = new HashMap<>();
                for (int i = 0; i < 255; i++) {
                    dictModbusDataPool.put(i, new ModbusDataPool((byte) i));
                }
            } else {
                dictModbusDataPool = new HashMap<>();
                dictModbusDataPool.put(0, new ModbusDataPool((byte) 0));
            }
        }
    }

    public ModbusDataPool GetModbusPool(byte station) {
        if (this.stationDataIsolation) {
            return dictModbusDataPool.get(station);
        } else {
            for (ModbusDataPool modbusPool :  dictModbusDataPool.values() ) {
                return modbusPool;
            }
        }
        return null;
    }


    public void Dispose() {
        dictModbusDataPool.clear();
    }

    private HashMap<Integer, ModbusDataPool> dictModbusDataPool;
    private boolean stationDataIsolation = false;
}
