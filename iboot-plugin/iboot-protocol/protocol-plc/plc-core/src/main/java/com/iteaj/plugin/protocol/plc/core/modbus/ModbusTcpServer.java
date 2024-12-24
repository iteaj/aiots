package com.iteaj.plugin.protocol.plc.core.modbus;

import com.iteaj.plugin.protocol.plc.core.modbus.helper.ModbusDataDict;
import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.ModbusTcpMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDataServerBase;
import com.iteaj.plugin.protocol.plc.core.net.StateOne.AppSession;
import com.iteaj.plugin.protocol.plc.core.serial.SoftCRC16;
import com.iteaj.plugin.protocol.plc.core.transfer.DataFormat;
import com.iteaj.plugin.protocol.plc.core.transfer.ReverseWordTransform;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExTwo;
import com.iteaj.plugin.protocol.plc.core.types.PlcHelper;

public class ModbusTcpServer extends NetworkDataServerBase {
    /**
     * 实例化一个Modbus Tcp及Rtu的服务器，支持数据读写操作
     */
    public ModbusTcpServer() {
        this.dictModbusDataPool = new ModbusDataDict();
        setByteTransform(new ReverseWordTransform());
        WordLength = 1;
    }

    public DataFormat getDataFormat() {
        return getByteTransform().getDataFormat();
    }

    public void setDataFormat(DataFormat value) {
        getByteTransform().setDataFormat(value);
    }

    public boolean getIsStringReverse() {
        return getByteTransform().getIsStringReverse();
    }

    public void setIsStringReverse(boolean value) {
        getByteTransform().setIsStringReverse(value);
    }


    public byte getStation() {
        return this.station;
    }

    public void setStation(byte value) {
        this.station = value;

    }

    /**
     * 获取或设置当前的TCP服务器是否使用 modbus-rtu 报文进行通信，如果设置为 <c>True</c>，那么客户端需要使用 {@link ModbusRtuOverTcp}<br />
     * Get or set whether the current TCP server uses modbus-rtu messages for communication. If it is set to <c>True</c>, then the client needs to use {@link ModbusRtuOverTcp}
     */
    public boolean UseModbusRtuOverTcp = false;

    /**
     * 获取或设置两次请求直接的延时时间，单位毫秒，默认是0，不发生延时，设置为20的话，可以有效防止有客户端疯狂进行请求而导致服务器的CPU占用率上升。<br />
     * Get or set the direct delay time of two requests, in milliseconds, the default is 0, no delay occurs, if it is set to 20, it can effectively prevent the client from making crazy requests and causing the server's CPU usage to increase.
     */
    public int RequestDelayTime = 0;

    /**
     * 获取或设置是否启动站点数据隔离功能，默认为 <c>False</c>，也即虚拟服务器模拟一个站点服务器，客户端使用正确的站号才能通信。当设置为 <c>True</c> 时，虚拟服务器模式256个站点，无论客户端使用的什么站点，都能读取或是写入对应站点里去。服务器同时也可以访问任意站点自身的数据。<br />
     * Get or set whether to enable the site data isolation function, the default is <c>False</c>, that is, the virtual server simulates a site server, and the client can communicate with the correct site number.
     * When set to<c> True</c>, 256 sites in virtual server mode, no matter what site the client uses, can read or write to the corresponding site.The server can also access any site's own data.
     *
     * @return 值信息
     */
    public boolean getStationDataIsolation() {
        return this.stationDataIsolation;
    }

    /**
     * 设置是否启动站点数据隔离功能，默认为 <c>False</c>，也即虚拟服务器模拟一个站点服务器，客户端使用正确的站号才能通信。<br />
     * set whether to enable the site data isolation function, the default is <c>False</c>, that is, the virtual server simulates a site server, and the client can communicate with the correct site number.
     *
     * @param value 设置值
     */
    public void setStationDataIsolation(boolean value) {
        this.stationDataIsolation = value;
        this.dictModbusDataPool.Set(value);
    }

    protected byte[] SaveToBytes() {
        return dictModbusDataPool.GetModbusPool(station).SaveToBytes();
    }

    protected void LoadFromBytes(byte[] content) {
        dictModbusDataPool.GetModbusPool(station).LoadFromBytes(content, 0);
    }

    public boolean ReadCoil(String address) {
        OperateResultExTwo<Integer, String> opStation = PlcHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) address = opStation.Content2;

        return dictModbusDataPool.GetModbusPool(opStation.Content1.byteValue()).ReadCoil(address);
    }

    public boolean[] ReadCoil(String address, short length) {
        OperateResultExTwo<Integer, String> opStation = PlcHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) address = opStation.Content2;

        return dictModbusDataPool.GetModbusPool(opStation.Content1.byteValue()).ReadCoil(address, length);
    }

    public void WriteCoil(String address, boolean data) {
        OperateResultExTwo<Integer, String> opStation = PlcHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) address = opStation.Content2;

        dictModbusDataPool.GetModbusPool(opStation.Content1.byteValue()).WriteCoil(address, data);
    }

    public void WriteCoil(String address, boolean[] data) {
        OperateResultExTwo<Integer, String> opStation = PlcHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) address = opStation.Content2;

        dictModbusDataPool.GetModbusPool(opStation.Content1.byteValue()).WriteCoil(address, data);
    }

    public boolean ReadDiscrete(String address) {
        OperateResultExTwo<Integer, String> opStation = PlcHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) address = opStation.Content2;

        return dictModbusDataPool.GetModbusPool(opStation.Content1.byteValue()).ReadDiscrete(address);
    }

    public boolean[] ReadDiscrete(String address, short length) {
        OperateResultExTwo<Integer, String> opStation = PlcHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) address = opStation.Content2;

        return dictModbusDataPool.GetModbusPool(opStation.Content1.byteValue()).ReadDiscrete(address, length);
    }

    public void WriteDiscrete(String address, boolean data) {
        OperateResultExTwo<Integer, String> opStation = PlcHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) address = opStation.Content2;

        dictModbusDataPool.GetModbusPool(opStation.Content1.byteValue()).WriteDiscrete(address, data);
    }

    public void WriteDiscrete(String address, boolean[] data) {
        OperateResultExTwo<Integer, String> opStation = PlcHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) address = opStation.Content2;

        dictModbusDataPool.GetModbusPool(opStation.Content1.byteValue()).WriteDiscrete(address, data);
    }

    public OperateResultExOne<byte[]> Read(String address, short length) {
        OperateResultExTwo<Integer, String> opStation = PlcHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) address = opStation.Content2;

        return dictModbusDataPool.GetModbusPool(opStation.Content1.byteValue()).Read(address, length);
    }

    public OperateResult Write(String address, byte[] value) {
        OperateResultExTwo<Integer, String> opStation = PlcHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) address = opStation.Content2;

        return dictModbusDataPool.GetModbusPool(opStation.Content1.byteValue()).Write(address, value);
    }

    public OperateResultExOne<boolean[]> ReadBool(String address, short length) {
        OperateResultExTwo<Integer, String> opStation = PlcHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) address = opStation.Content2;

        return dictModbusDataPool.GetModbusPool(opStation.Content1.byteValue()).ReadBool(address, length);
    }

    public OperateResult Write(String address, boolean[] value) {
        OperateResultExTwo<Integer, String> opStation = PlcHelper.ExtractParameter(address, "s", this.station);
        if (opStation.IsSuccess) address = opStation.Content2;

        return dictModbusDataPool.GetModbusPool(opStation.Content1.byteValue()).Write(address, value);
    }

    /**
     * 写入寄存器的数据，指定字节数据信息
     *
     * @param address 起始地址，示例："100"，如果是输入寄存器："x=4;100"
     * @param high    高位数据
     * @param low     地位数据
     */
    public void Write(String address, byte high, byte low) {
        Write(address, new byte[]{high, low});
    }

    protected INetMessage GetNewNetMessage() {
        return UseModbusRtuOverTcp ? null : new ModbusTcpMessage();
    }

    protected OperateResultExOne<byte[]> ReadFromCoreServer(AppSession session, byte[] receive) {
        if (RequestDelayTime > 0) {
            try {
                Thread.sleep(RequestDelayTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (UseModbusRtuOverTcp) {
            if (!SoftCRC16.CheckCRC16(receive)) return new OperateResultExOne<>("CRC Check Failed ");
            byte[] modbusCore = SoftBasic.BytesArrayRemoveLast(receive, 2);
            // 指令长度验证错误，关闭网络连接
            if (!CheckModbusMessageLegal(modbusCore))
                return new OperateResultExOne<byte[]>("Modbus rtu message check failed ");
            if (!getStationDataIsolation() && station != modbusCore[0])
                return new OperateResultExOne<byte[]>("Station not match Modbus-rtu ");
            // 需要回发消息
            return OperateResultExOne.CreateSuccessResult(ModbusInfo.PackCommandToRtu(ReadFromModbusCore(modbusCore)));
        } else {
            if (!CheckModbusMessageLegal(SoftBasic.BytesArrayRemoveBegin(receive, 6)))
                return new OperateResultExOne<byte[]>("Modbus message check failed");
            int id = (receive[0] & 0xff) * 256 + (receive[1] & 0xff);
            if (!getStationDataIsolation() && station != receive[6])
                return new OperateResultExOne<byte[]>("Station not match Modbus-tcp ");

            byte[] back = ModbusInfo.PackCommandToTcp(ReadFromModbusCore(SoftBasic.BytesArrayRemoveBegin(receive, 6)), id);
            return OperateResultExOne.CreateSuccessResult(back);
        }
    }

    /**
     * 创建特殊的功能标识，然后返回该信息<br />
     * Create a special feature ID and return this information
     *
     * @param modbusCore modbus核心报文
     * @param error      错误码
     * @return 携带错误码的modbus报文
     */
    private byte[] CreateExceptionBack(byte[] modbusCore, byte error) {
        return new byte[]{modbusCore[0], (byte) (modbusCore[1] + 0x80), error};
    }

    /**
     * 创建返回消息<br />
     * Create return message
     *
     * @param modbusCore modbus核心报文
     * @param content    返回的实际数据内容
     * @return 携带内容的modbus报文
     */
    private byte[] CreateReadBack(byte[] modbusCore, byte[] content) {
        return SoftBasic.SpliceArray(new byte[]{modbusCore[0], modbusCore[1], (byte) content.length}, content);
    }

    /**
     * 创建写入成功的反馈信号<br />
     * Create feedback signal for successful write
     *
     * @param modbus modbus核心报文
     * @return 携带成功写入的信息
     */
    private byte[] CreateWriteBack(byte[] modbus) {
        return SoftBasic.BytesArraySelectBegin(modbus, 6);
    }

    private byte[] ReadCoilBack(byte[] modbus, String addressHead) {
        try {
            int address = getByteTransform().TransUInt16(modbus, 2);
            int length = getByteTransform().TransUInt16(modbus, 4);

            // 越界检测
            if ((address + length) > 65536) return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeOverBound);

            // 地址长度检测
            if (length > 2040) return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeQuantityOver);

            boolean[] read = dictModbusDataPool.GetModbusPool(modbus[0]).ReadBool(addressHead + address, (short) length).Content;
            return CreateReadBack(modbus, SoftBasic.BoolArrayToByte(read));
        } catch (Exception ex) {
            // LogNet?.WriteException( ToString( ), StringResources.Language.ModbusTcpReadCoilException, ex );
            return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeReadWriteException);
        }
    }

    private byte[] ReadRegisterBack(byte[] modbus, String addressHead) {
        try {
            int address = getByteTransform().TransUInt16(modbus, 2);
            int length = getByteTransform().TransUInt16(modbus, 4);

            // 越界检测
            if ((address + length) > 65536) return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeOverBound);

            // 地址长度检测
            if (length > 127) return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeQuantityOver);

            byte[] buffer = dictModbusDataPool.GetModbusPool(modbus[0]).Read(addressHead + String.valueOf(address), (short) length).Content;
            return CreateReadBack(modbus, buffer);
        } catch (Exception ex) {
            //LogNet?.WriteException( ToString( ), StringResources.Language.ModbusTcpReadRegisterException, ex );
            return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeReadWriteException);
        }
    }

    private byte[] WriteOneCoilBack(byte[] modbus) {
        try {
            // 先判断是否有写入的权利，没有的话，直接返回写入异常
            if (!this.EnableWrite) return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeReadWriteException);

            int address = getByteTransform().TransUInt16(modbus, 2);

            if (modbus[4] == (byte) 0xFF && modbus[5] == 0x00)
                dictModbusDataPool.GetModbusPool(modbus[0]).Write(String.valueOf(address), new boolean[]{true});
            else if (modbus[4] == 0x00 && modbus[5] == 0x00)
                dictModbusDataPool.GetModbusPool(modbus[0]).Write(String.valueOf(address), new boolean[]{false});

            return CreateWriteBack(modbus);
        } catch (Exception ex) {
            // LogNet?.WriteException( ToString( ), StringResources.Language.ModbusTcpWriteCoilException, ex );
            return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeReadWriteException);
        }
    }

    private byte[] WriteOneRegisterBack(byte[] modbus) {
        try {
            // 先判断是否有写入的权利，没有的话，直接返回写入异常
            if (!this.EnableWrite) return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeReadWriteException);

            int address = getByteTransform().TransUInt16(modbus, 2);
            short ValueOld = ReadInt16(String.valueOf(address)).Content;

            dictModbusDataPool.GetModbusPool(modbus[0]).Write(String.valueOf(address), new byte[]{modbus[4], modbus[5]});
            short ValueNew = ReadInt16(String.valueOf(address)).Content;
            return CreateWriteBack(modbus);
        } catch (Exception ex) {
            // ogNet?.WriteException( ToString( ), StringResources.Language.ModbusTcpWriteRegisterException, ex );
            return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeReadWriteException);
        }
    }

    private byte[] WriteCoilsBack(byte[] modbus) {
        try {
            // 先判断是否有写入的权利，没有的话，直接返回写入异常
            if (!this.EnableWrite) return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeReadWriteException);

            int address = getByteTransform().TransUInt16(modbus, 2);
            int length = getByteTransform().TransUInt16(modbus, 4);

            if ((address + length) > 65536) return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeOverBound);

            if (length > 2040) return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeQuantityOver);

            dictModbusDataPool.GetModbusPool(modbus[0]).Write(String.valueOf(address), SoftBasic.ByteToBoolArray(SoftBasic.BytesArrayRemoveBegin(modbus, 7), length));
            return CreateWriteBack(modbus);
        } catch (Exception ex) {
            // LogNet?.WriteException( ToString( ), StringResources.Language.ModbusTcpWriteCoilException, ex );
            return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeReadWriteException);
        }
    }

    private byte[] WriteRegisterBack(byte[] modbus) {
        try {
            // 先判断是否有写入的权利，没有的话，直接返回写入异常
            if (!this.EnableWrite) return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeReadWriteException);

            int address = getByteTransform().TransUInt16(modbus, 2);
            int length = getByteTransform().TransUInt16(modbus, 4);

            if ((address + length) > 65536) return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeOverBound);

            if (length > 127) return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeQuantityOver);

            byte[] oldValue = dictModbusDataPool.GetModbusPool(modbus[0]).Read(String.valueOf(address), (short) length).Content;
            dictModbusDataPool.GetModbusPool(modbus[0]).Write(String.valueOf(address), SoftBasic.BytesArrayRemoveBegin(modbus, 7));
            return CreateWriteBack(modbus);
        } catch (Exception ex) {
            // LogNet?.WriteException( ToString( ), StringResources.Language.ModbusTcpWriteRegisterException, ex );
            return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeReadWriteException);
        }
    }

    private byte[] WriteMaskRegisterBack(byte[] modbus) {
        try {
            // 先判断是否有写入的权利，没有的话，直接返回写入异常
            if (!this.EnableWrite) return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeReadWriteException);

            int address = getByteTransform().TransUInt16(modbus, 2);
            int and_Mask = getByteTransform().TransUInt16(modbus, 4);
            int or_Mask = getByteTransform().TransUInt16(modbus, 6);

            int ValueOld = ReadInt16("s=" + modbus[0] + "};" + address).Content;
            short ValueNew = (short) ((ValueOld & and_Mask) | or_Mask);
            Write("s=" + modbus[0] + "};" + address, ValueNew);

            // 触发写入请求
            return modbus;
        } catch (Exception ex) {
            //LogNet?.WriteException( ToString( ), StringResources.Language.ModbusTcpWriteRegisterException, ex );
            return CreateExceptionBack(modbus, ModbusInfo.FunctionCodeReadWriteException);
        }
    }

    /**
     * 检测当前的Modbus接收的指定是否是合法的<br />
     * Check if the current Modbus datad designation is valid
     *
     * @param buffer 缓存数据
     * @return 是否合格
     */
    private boolean CheckModbusMessageLegal(byte[] buffer) {
        boolean check = false;
        switch (buffer[1]) {
            case ModbusInfo.ReadCoil:
            case ModbusInfo.ReadDiscrete:
            case ModbusInfo.ReadRegister:
            case ModbusInfo.ReadInputRegister:
            case ModbusInfo.WriteOneCoil:
            case ModbusInfo.WriteOneRegister:
                check = buffer.length == 0x06;
                break;
            case ModbusInfo.WriteCoil:
            case ModbusInfo.WriteRegister:
                check = buffer.length > 6 && (buffer[6] == (buffer.length - 7));
                break;
            case ModbusInfo.WriteMaskRegister:
                check = buffer.length == 0x08;
                break;
            default:
                check = true;
                break;
        }
        //if (check == false) LogNet?.WriteError( ToString( ), $"Receive Nosense Modbus-rtu : " + buffer.ToHexString( ' ' ) );
        return check;
    }

    /**
     * Modbus核心数据交互方法，允许重写自己来实现，报文只剩下核心的Modbus信息，去除了MPAB报头信息<br />
     * The Modbus core data interaction method allows you to rewrite it to achieve the message.
     * Only the core Modbus information is left in the message, and the MPAB header information is removed.
     *
     * @param modbusCore 核心的Modbus报文
     * @return 进行数据交互之后的结果
     */
    protected byte[] ReadFromModbusCore(byte[] modbusCore) {
        byte[] buffer;
        switch (modbusCore[1]) {
            case ModbusInfo.ReadCoil:
                buffer = ReadCoilBack(modbusCore, "");
                break;
            case ModbusInfo.ReadDiscrete:
                buffer = ReadCoilBack(modbusCore, "x=2;");
                break;
            case ModbusInfo.ReadRegister:
                buffer = ReadRegisterBack(modbusCore, "");
                break;
            case ModbusInfo.ReadInputRegister:
                buffer = ReadRegisterBack(modbusCore, "x=4;");
                break;
            case ModbusInfo.WriteOneCoil:
                buffer = WriteOneCoilBack(modbusCore);
                break;
            case ModbusInfo.WriteOneRegister:
                buffer = WriteOneRegisterBack(modbusCore);
                break;
            case ModbusInfo.WriteCoil:
                buffer = WriteCoilsBack(modbusCore);
                break;
            case ModbusInfo.WriteRegister:
                buffer = WriteRegisterBack(modbusCore);
                break;
            case ModbusInfo.WriteMaskRegister:
                buffer = WriteMaskRegisterBack(modbusCore);
                break;
            default:
                buffer = CreateExceptionBack(modbusCore, ModbusInfo.FunctionCodeNotSupport);
                break;
        }

        return buffer;
    }

    private ModbusDataDict dictModbusDataPool;
    private byte station = 1;                                          // 服务器的站号数据，对于tcp无效，对于rtu来说，如果小于0，则忽略站号信息
    private boolean stationDataIsolation = false;                      // 各站点的数据是否进行隔离

    public String toString() {
        return "ModbusTcpServer[" + getPort() + "]";
    }

}
