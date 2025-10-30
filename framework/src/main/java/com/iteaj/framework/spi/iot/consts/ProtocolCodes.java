package com.iteaj.framework.spi.iot.consts;

/**
 * 已实现的协议码
 */
public enum ProtocolCodes {
    MODBUS_RTU("MODBUS_RTU"),
    MODBUS_TCP("MODBUS_TCP"),

    DTU_DLT645("DTU:DLT645"),
    SERIAL_DLT645("SERIAL:DLT645"),

    DTU_MODBUS_RTU("DTU:MODBUS_RTU"),
    DTU_MODBUS_TCP("DTU:MODBUS_TCP"),
    DTU_MODBUS_OVER_MQTT("DTU:MODBUS_OVER_MQTT"),

    PLC_MELSEC("PLC_MELSEC"),
    PLC_OMRON_TCP("PLC_OMRON_TCP"),
    PLC_SIEMENS_S7("PLC_SIEMENS_S7"),
    PLC_INOVANCE("PLC_INOVANCE"),
    PLC_PANASONIC("PLC_PANASONIC"),

    MQTT_DEFAULT_IMPL("MQTT_DEFAULT_IMPL"),

    OPCUA_DEFAULT_IMPL("OPCUA_DEFAULT_IMPL"),
    ;

    private String value;

    ProtocolCodes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 是否是modbus协议
     * @param protocolCode
     * @return
     */
    public static boolean isModbus(String protocolCode) {
        return protocolCode.contains("MODBUS");
    }

    /**
     * 是否是plc协议
     * @param protocolCode 协议码
     * @return true or false
     */
    public static boolean isPLC(String protocolCode) {
        return protocolCode.contains("PLC");
    }
}
