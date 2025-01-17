package com.iteaj.plugin.protocol.plc.core.modbus;

import com.iteaj.plugin.protocol.plc.core.net.IReadWriteDevice;
import com.iteaj.plugin.protocol.plc.core.transfer.DataFormat;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

/**
 * Modbus设备的接口，用来表示Modbus相关的设备对象，{@link ModbusTcpNet},{@link ModbusRtuOverTcp}均实现了该接口信息<br />
 */
public interface IModbus extends IReadWriteDevice {
    /**
     * 获取起始地址是否从0开始
     *
     * @return bool值
     */
    boolean getAddressStartWithZero();

    /**
     * 设置起始地址是否从0开始
     *
     * @param addressStartWithZero true代表从0开始，false代表从1开始
     */
    void setAddressStartWithZero(boolean addressStartWithZero);

    /**
     * 获取站号
     *
     * @return 站号
     */
    byte getStation();

    /**
     * 设置站号
     *
     * @param station 站号
     */
    void setStation(byte station);

    /**
     * 设置多字节数据的反转类型，适用于int,float,double,long类型的数据
     *
     * @param dataFormat 数据类型
     */
    void setDataFormat(DataFormat dataFormat);

    /**
     * 获取多字节数据的反转类型，适用于int,float,double,long类型的数据
     *
     * @return
     */
    DataFormat getDataFormat();


    /**
     * 字符串数据是否发生反转
     *
     * @return bool值
     */
    boolean isStringReverse();

    /**
     * 设置字符串数据是否反转
     *
     * @param stringReverse bool值
     */
    void setStringReverse(boolean stringReverse);

    /**
     * 将当前的地址信息转换成Modbus格式的地址，如果转换失败，返回失败的消息。默认不进行任何的转换。<br />
     * Convert the current address information into a Modbus format address. If the conversion fails, a failure message will be returned. No conversion is performed by default.
     * @param address 传入的地址
     * @param modbusCode Modbus的功能码
     * @return 转换之后Modbus的地址
     */
    OperateResultExOne<String> TranslateToModbusAddress(String address, byte modbusCode );

    /**
     * 获取或设置当前掩码写入的功能码是否激活状态，设置为 false 时，再执行写入位时，会通过读字，修改位，写字的方式来间接实现。<br />
     * When the function code is set to false, and then the write bit is executed, it will be indirectly implemented by reading, modifying the bit, and writing the word.
     * @return 是否启用掩码的功能码写入功能
     */
    boolean getEnableWriteMaskCode();

    /**
     * 设置当前掩码写入的功能码是否激活状态，设置为 false 时，再执行写入位时，会通过读字，修改位，写字的方式来间接实现。<br />
     * When the function code is set to false, and then the write bit is executed, it will be indirectly implemented by reading, modifying the bit, and writing the word.
     * @param value 是否使用掩码功能码
     */
    void setEnableWriteMaskCode(boolean value);


    /**
     * 使用0x17功能码来实现同时写入并读取数据的操作，使用一条报文来实现，需要指定读取的地址，长度，写入的地址，写入的数据信息，返回读取的结果数据。<br />
     * Use 0x17 function code to write and read data at the same time, and use a message to implement it,
     * you need to specify the read address, length, written address, written data information, and return the read result data.
     * @param readAddress 读取的地址信息
     * @param length 读取的长度信息
     * @param writeAddress 写入的地址信息
     * @param value 写入的字节数据信息
     * @return 读取的结果对象
     */
    OperateResultExOne<byte[]> ReadWrite( String readAddress, short length, String writeAddress, byte[] value );

}

