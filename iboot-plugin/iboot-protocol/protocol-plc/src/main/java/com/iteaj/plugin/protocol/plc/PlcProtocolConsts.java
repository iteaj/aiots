package com.iteaj.plugin.protocol.plc;

public interface PlcProtocolConsts {

    String PLC_VALUE = "value";
    String PLC_VALUE_NAME = "读/写值";
    String PLC_VALUE_REMARK = "读/写PLC的数据";

    String PLC_NUMBER = "num";
    String PLC_NUMBER_NAME = "读取数量";
    String PLC_NUMBER_REMARK = "读取值的数量";

    String PLC_ADDRESS = "position";
    String PLC_ADDRESS_NAME = "PLC地址";
    String PLC_ADDRESS_REMARK= "读/写的PLC地址";
    String PLC_ADDRESS_CONFIG_REMARK= "要操作的PLC地址";
}
