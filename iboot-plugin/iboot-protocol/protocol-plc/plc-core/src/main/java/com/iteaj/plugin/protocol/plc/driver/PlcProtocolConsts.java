package com.iteaj.plugin.protocol.plc.driver;

public interface PlcProtocolConsts {

    String PLC_VALUE = "value";
    String PLC_VALUE_NAME = "读/写值";
    String PLC_VALUE_REMARK = "读/写PLC的数据";

    String PLC_NUMBER_NAME = "读取数量";
    String PLC_NUMBER_REMARK = "读取的数量或长度";

    String PLC_ADDRESS_NAME = "PLC地址";
    String PLC_ADDRESS_REMARK= "读/写的PLC地址";
    String PLC_ADDRESS_CONFIG_REMARK= "要操作的PLC地址";

    String PLC_WRITE_STATUS = "status";

    String PLC_WRITE_HEX = "writeHex";
    String PLC_WRITE_BYTE = "writeByte";
    String PLC_WRITE_BOOL = "writeBool";
    String PLC_WRITE_DATE = "writeDate";
    String PLC_WRITE_INT16 = "writeInt16";
    String PLC_WRITE_INT32 = "writeInt32";
    String PLC_WRITE_INT64 = "writeInt64";
    String PLC_WRITE_FLOAT = "writeFloat";
    String PLC_WRITE_DOUBLE = "writeDouble";
    String PLC_WRITE_STRING = "writeString";
    String PLC_WRITE_DTL_DATETIME = "writeDatetime";

    String PLC_READ_BYTE = "readByte";
    String PLC_READ_BOOL = "readBool";
    String PLC_READ_DATE = "readDate";
    String PLC_READ_INT16 = "readInt16";
    String PLC_READ_UINT16 = "readUInt16";
    String PLC_READ_INT32 = "readInt32";
    String PLC_READ_UINT32 = "readUInt32";
    String PLC_READ_INT64 = "readInt64";
    String PLC_READ_FLOAT = "readFloat";
    String PLC_READ_DOUBLE = "readDouble";
    String PLC_READ_STRING = "readString";
    String PLC_READ_DTL_DATETIME = "readDtlDatetime";
}
