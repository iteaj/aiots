package com.iteaj.plugin.protocol.plc.core.modbus;

import com.iteaj.plugin.protocol.plc.core.basic.SoftIncrementCount;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkUdpDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.DataFormat;
import com.iteaj.plugin.protocol.plc.core.transfer.ReverseWordTransform;
import com.iteaj.plugin.protocol.plc.core.types.FunctionOperateExTwo;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

public class ModbusUdpNet extends NetworkUdpDeviceBase implements IModbus {
    /**
     * 实例化一个Modbus-Tcp协议的客户端对象<br />
     * Instantiate a client object of the Modbus-Tcp protocol
     */
    public ModbusUdpNet() {
        this.softIncrementCount = new SoftIncrementCount(65535, 0);
        this.WordLength = 1;
        this.station = 1;
        setByteTransform(new ReverseWordTransform());
    }

    /**
     * 指定服务器地址，端口号，客户端自己的站号来初始化<br />
     * Specify the server address, port number, and client's own station number to initialize
     *
     * @param ipAddress 服务器的Ip地址
     * @param port      服务器的端口号
     * @param station   客户端自身的站号，可以在读取的时候动态配置
     */
    public ModbusUdpNet(String ipAddress, int port, byte station) {
        this.softIncrementCount = new SoftIncrementCount(65535, 0);
        setIpAddress(ipAddress);
        setPort(port);
        this.WordLength = 1;
        this.station = station;
        setByteTransform(new ReverseWordTransform());
    }

    private byte station = 0x01;                                // 本客户端的站号
    private SoftIncrementCount softIncrementCount;              // 自增消息的对象
    private boolean isAddressStartWithZero = true;                 // 线圈值的地址值是否从零开始
    private boolean enableWriteMaskCode = true;
    private FunctionOperateExTwo<String, Byte, OperateResultExOne<String>> addressMapping;  // 地址映射关系


    public boolean getAddressStartWithZero() {
        return isAddressStartWithZero;
    }

    public void setAddressStartWithZero(boolean addressStartWithZero) {
        this.isAddressStartWithZero = addressStartWithZero;
    }

    public byte getStation() {
        return station;
    }

    public void setStation(byte station) {
        this.station = station;
    }

    public void setDataFormat(DataFormat dataFormat) {
        getByteTransform().setDataFormat(dataFormat);
    }

    public DataFormat getDataFormat() {
        return getByteTransform().getDataFormat();
    }

    public boolean isStringReverse() {
        return getByteTransform().getIsStringReverse();
    }

    public void setStringReverse(boolean stringReverse) {
        getByteTransform().setIsStringReverse(stringReverse);
    }

    public boolean getEnableWriteMaskCode(){
        return this.enableWriteMaskCode;
    }

    public void setEnableWriteMaskCode(boolean value){
        this.enableWriteMaskCode = value;
    }
    // region Core Override

    protected byte[] PackCommandWithHeader(byte[] command) {
        return ModbusInfo.PackCommandToTcp(command, (int) softIncrementCount.GetCurrentValue());
    }

    protected OperateResultExOne<byte[]> UnpackResponseContent(byte[] send, byte[] response) {
        return ModbusInfo.ExtractActualData(ModbusInfo.ExplodeTcpCommandToCore(response));
    }

    @Override
    public OperateResultExOne<String> TranslateToModbusAddress(String address, byte modbusCode) {
        if (this.addressMapping != null) return this.addressMapping.Action(address, modbusCode);
        return OperateResultExOne.CreateSuccessResult(address);
    }

    /**
     * 注册一个新的地址映射关系，注册地址映射关系后，就可以使用新的地址来读写Modbus数据了，通常用于其他的支持Modbus协议的PLC。<br />
     * After registering a new address mapping, you can use the new address to read and write Modbus data, which is usually used for other PLCs that support the Modbus protocol.
     * @param mapping 地址映射关系信息
     */
    public  void RegisteredAddressMapping( FunctionOperateExTwo<String, Byte,OperateResultExOne<String>> mapping ) {
        this.addressMapping = mapping;
    }
    // endregion


    // region Read Write Support

    /**
     * 读取线圈，需要指定起始地址，如果富文本地址不指定，默认使用的功能码是 0x01<br />
     * To read the coil, you need to specify the start address. If the rich text address is not specified, the default function code is 0x01.
     *
     * @param address 起始地址，格式为"1234"
     * @return 带有成功标志的bool对象
     */
    public OperateResultExOne<Boolean> ReadCoil(String address) {
        return ReadBool(address);
    }

    /**
     * 批量的读取线圈，需要指定起始地址，读取长度，如果富文本地址不指定，默认使用的功能码是 0x01<br />
     * For batch reading coils, you need to specify the start address and read length. If the rich text address is not specified, the default function code is 0x01.
     *
     * @param address 起始地址，格式为"1234"
     * @param length  读取长度
     * @return 带有成功标志的bool数组对象
     */
    public OperateResultExOne<boolean[]> ReadCoil(String address, short length) {
        return ReadBool(address, length);
    }

    /**
     * 读取输入线圈，需要指定起始地址，如果富文本地址不指定，默认使用的功能码是 0x02<br />
     * To read the input coil, you need to specify the start address. If the rich text address is not specified, the default function code is 0x02.
     *
     * @param address 起始地址，格式为"1234"
     * @return 带有成功标志的bool对象
     */
    public OperateResultExOne<Boolean> ReadDiscrete(String address) {
        OperateResultExOne<boolean[]> read = ReadDiscrete(address, (short) 1);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        return OperateResultExOne.CreateSuccessResult(read.Content[0]);
    }

    /**
     * 批量的读取输入点，需要指定起始地址，读取长度，如果富文本地址不指定，默认使用的功能码是 0x02<br />
     * To read input points in batches, you need to specify the start address and read length. If the rich text address is not specified, the default function code is 0x02
     *
     * @param address 起始地址，格式为"1234"
     * @param length  读取长度
     * @return 带有成功标志的bool数组对象
     */
    public OperateResultExOne<boolean[]> ReadDiscrete(String address, short length) {
        return ModbusHelper.ReadBoolHelper(this, address, length, ModbusInfo.ReadDiscrete);
    }

    /**
     * 从Modbus服务器批量读取寄存器的信息，需要指定起始地址，读取长度，如果富文本地址不指定，默认使用的功能码是 0x03，如果需要使用04功能码，那么地址就写成 "x=4;100"<br />
     * To read the register information from the Modbus server in batches, you need to specify the start address and read length. If the rich text address is not specified,
     * the default function code is 0x03. If you need to use the 04 function code, the address is written as "x=4;100"
     *
     * @param address 起始地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param length  读取的数量
     * @return 带有成功标志的字节信息
     */
    public OperateResultExOne<byte[]> Read(String address, short length) {
        return ModbusHelper.Read(this, address, length);
    }

    /**
     * 将数据写入到Modbus的寄存器上去，需要指定起始地址和数据内容，如果富文本地址不指定，默认使用的功能码是 0x10<br />
     * To write data to Modbus registers, you need to specify the start address and data content. If the rich text address is not specified, the default function code is 0x10
     *
     * @param address 起始地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param value   写入的数据，长度根据data的长度来指示
     * @return 返回写入结果
     */
    public OperateResult Write(String address, byte[] value) {
        return ModbusHelper.Write(this, address, value);
    }

    /**
     * 将数据写入到Modbus的单个寄存器上去，需要指定起始地址和数据值，如果富文本地址不指定，默认使用的功能码是 0x06<br />
     * To write data to a single register of Modbus, you need to specify the start address and data value. If the rich text address is not specified, the default function code is 0x06.
     *
     * @param address 起始地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param value   写入的short数据
     * @return 是否写入成功
     */
    public OperateResult Write(String address, short value) {
        return ModbusHelper.Write(this, address, value);
    }

    /**
     * 向设备写入掩码数据，使用0x16功能码，需要确认对方是否支持相关的操作，掩码数据的操作主要针对寄存器。<br />
     * To write mask data to the server, using the 0x16 function code, you need to confirm whether the other party supports related operations.
     * The operation of mask data is mainly directed to the register.
     *
     * @param address 起始地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param andMask 等待与操作的掩码数据
     * @param orMask  等待或操作的掩码数据
     * @return 是否写入成功
     */
    public OperateResult WriteMask(String address, short andMask, short orMask) {
        return ModbusHelper.WriteMask(this, address, andMask, orMask);
    }

    // endregion

    // Region Write One Register

    /**
     * 将数据写入到Modbus的单个寄存器上去，需要指定起始地址和数据值，如果富文本地址不指定，默认使用的功能码是 0x06<br />
     * To write data to a single register of Modbus, you need to specify the start address and data value. If the rich text address is not specified, the default function code is 0x06.
     *
     * @param address 起始地址，比如"100"，"x=4;100"，"s=1;100","s=1;x=4;100"
     * @param value   写入的short数据
     * @return 是否写入成功
     */
    public OperateResult WriteOneRegister(String address, short value) {
        return Write(address, value);
    }

    // endregion

    // region Read Write Bool

    /**
     * 批量读取线圈或是离散的数据信息，需要指定地址和长度，具体的结果取决于实现，如果富文本地址不指定，默认使用的功能码是 0x01<br />
     * To read coils or discrete data in batches, you need to specify the address and length. The specific result depends on the implementation. If the rich text address is not specified, the default function code is 0x01.
     *
     * @param address 数据地址
     * @param length  数据长度
     * @return 带有成功标识的bool[]数组
     */
    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        return ModbusHelper.ReadBoolHelper(this, address, length, ModbusInfo.ReadCoil);
    }

    /**
     * 向线圈中写入bool数组，返回是否写入成功，如果富文本地址不指定，默认使用的功能码是 0x0F<br />
     * Write the bool array to the coil, and return whether the writing is successful. If the rich text address is not specified, the default function code is 0x0F.
     *
     * @param address 要写入的数据地址
     * @param values  要写入的实际数组
     * @return 返回写入结果
     */
    public OperateResult Write(String address, boolean[] values) {
        return ModbusHelper.Write(this, address, values);
    }

    /**
     * 向线圈中写入bool数值，返回是否写入成功，如果富文本地址不指定，默认使用的功能码是 0x05<br />
     * Write bool value to the coil and return whether the writing is successful. If the rich text address is not specified, the default function code is 0x05.
     *
     * @param address 要写入的数据地址
     * @param value   要写入的实际数据
     * @return 返回写入结果
     */
    public OperateResult Write(String address, boolean value) {
        return ModbusHelper.Write(this, address, value);
    }

    public OperateResultExOne<byte[]> ReadWrite( String readAddress, short length, String writeAddress, byte[] value ) {
        return ModbusHelper.ReadWrite(this, readAddress, length, writeAddress, value);
    }

    // endregion

    @Override
    public String toString() {
        return "ModbusUdpNet[" + getIpAddress() + ":" + getPort() + "]";
    }
}
