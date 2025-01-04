package com.iteaj.plugin.protocol.plc.ab.profinet;

import com.iteaj.plugin.protocol.plc.core.net.IReadWriteNet;
import com.iteaj.plugin.protocol.plc.core.transfer.IByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExTwo;

import java.time.Duration;
import java.util.Date;

public interface IReadWriteCip extends IReadWriteNet {


    /**
     * 获取当前的数据变换机制，当你需要从字节数据转换类型数据的时候需要。<br />
     * Get the current data transformation mechanism is required when you need to convert type data from byte data.
     * @return 数据的变换对象
     */
    IByteTransform getByteTransform();

    /**
     * 使用指定的类型写入指定的节点数据，类型信息参考API文档，地址支持携带类型代号信息，可以强制指定本次写入数据的类型信息，例如 "type=0xD1;A"<br />
     * Use the specified type to write the specified node data. For the type information, refer to the API documentation. The address supports carrying type code information.
     * You can force the type information of the data to be written this time. For example, "type=0xD1;A"<br /><br />
     *
     * 关于参数 length 的含义，表示的是地址长度，一般的标量数据都是 1，如果PLC有个标签是 A，数据类型为 byte[10]，那我们写入 3 个byte就是 WriteTag( "A[5]", 0xD1, new byte[]{1,2,3}, 3 );<br />
     * Regarding the meaning of the parameter length, it represents the address length. The general scalar data is 1. If the PLC has a tag of A and the data type is byte[10], then we write 3 bytes as WriteTag( "A[5 ]", 0xD1, new byte[]{1,2,3}, 3 );
     * @param address 节点的名称 -> Name of the node
     * @param typeCode 类型代码，详细参见{@link AllenBradleyHelper}上的常用字段 ->  Type code, see the commonly used Fields section on the {@link AllenBradleyHelper} in detail
     * @param value 实际的数据值 -> The actual data value
     * @param length 如果节点是数组，就是数组长度 -> If the node is an array, it is the array length
     * @return 是否写入成功
     */
    OperateResult WriteTag(String address, short typeCode, byte[] value, int length );

    /**
     * 从PLC里读取一个指定标签名的原始数据信息及其数据类型信息<br />
     * Read the original data information of a specified tag name and its data type information from the PLC
     * @param address PLC的标签地址信息
     * @param length 读取的数据长度
     * @return 包含原始数据信息及数据类型的结果对象
     */
    OperateResultExTwo<Short, byte[]> ReadTag(String address, int length );

    /**
     * 从PLC里读取当前PLC的型号信息<br />
     * Read the current PLC model information from the PLC
     * @return 型号信息
     */
    OperateResultExOne<String> ReadPlcType( );

    /**
     * 读取指定地址的日期数据，最小日期为 1970年1月1日，当PLC的变量类型为 "Date" 和 "TimeAndDate" 时，都可以用本方法读取。<br />
     * Read the date data of the specified address. The minimum date is January 1, 1970. When the PLC variable type is "Date" and "TimeAndDate", this method can be used to read.
     * @param address PLC里变量的地址
     * @return 日期结果对象
     */
    OperateResultExOne<Date> ReadDate(String address );

    /**
     * 使用日期格式（Date）将指定的数据写入到指定的地址里，PLC的地址类型变量必须为 "Date"，否则写入失败。<br />
     * Use the date format (Date) to write the specified data to the specified address. The PLC address type variable must be "Date", otherwise the writing will fail.
     * @param address PLC里变量的地址
     * @param date 时间信息
     * @return 是否写入成功
     */
    OperateResult WriteDate( String address, Date date );

    /**
     * 使用日期 + 时间格式（TimeAndDate）将指定的数据写入到指定的地址里，PLC的地址类型变量必须为 "TimeAndDate"，否则写入失败。<br />
     * Use the date + time format (TimeAndDate) to write the specified data to the specified address. The PLC address type variable must be "TimeAndDate", otherwise the writing will fail.
     * @param address PLC里变量的地址
     * @param date 时间信息
     * @return 是否写入成功
     */
    OperateResult WriteTimeAndDate( String address, Date date );

    /**
     * 读取指定地址的时间数据，最小时间为 0，如果获取秒，可以访问 {@link Duration#getSeconds()}，当PLC的变量类型为 "Time" 和 "TimeOfDate" 时，都可以用本方法读取。<br />
     * Read the time data of the specified address. The minimum time is 0. If you get seconds, you can access {@link Duration#getSeconds()}.
     * When the PLC variable type is "Time" and "TimeOfDate", you can use this Method to read.
     * @param address PLC里变量的地址
     * @return 时间的结果对象
     */
    OperateResultExOne<Duration> ReadTime(String address );

    /**
     * 使用时间格式（TIME）将时间数据写入到PLC中指定的地址里去，PLC的地址类型变量必须为 "TIME"，否则写入失败。<br />
     * Use the time format (TIME) to write the time data to the address specified in the PLC. The PLC address type variable must be "TIME", otherwise the writing will fail.
     * @param address PLC里变量的地址
     * @param time 时间参数变量
     * @return 是否写入成功
     */
    OperateResult WriteTime( String address, Duration time );

    /**
     * 使用时间格式（TimeOfDate）将时间数据写入到PLC中指定的地址里去，PLC的地址类型变量必须为 "TimeOfDate"，否则写入失败。<br />
     * Use the time format (TimeOfDate) to write the time data to the address specified in the PLC. The PLC address type variable must be "TimeOfDate", otherwise the writing will fail.
     * @param address PLC里变量的地 PLC里变量的地址
     * @param timeOfDate 时间参数变量
     * @return 是否写入成功
     */
    OperateResult WriteTimeOfDate( String address, Duration timeOfDate );
}
