package com.iteaj.plugin.protocol.plc.panasonic.profinet;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.message.INetMessage;
import com.iteaj.plugin.protocol.plc.core.message.SpecifiedCharacterMessage;
import com.iteaj.plugin.protocol.plc.core.net.NetworkBase.NetworkDeviceBase;
import com.iteaj.plugin.protocol.plc.core.transfer.ByteTransformHelper;
import com.iteaj.plugin.protocol.plc.core.transfer.DataFormat;
import com.iteaj.plugin.protocol.plc.core.transfer.RegularByteTransform;
import com.iteaj.plugin.protocol.plc.core.types.*;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;

import java.util.ArrayList;

public class PanasonicMewtocolOverTcp extends NetworkDeviceBase {
    /**
     * 实例化一个默认的松下PLC通信对象，默认站号为0xEE<br />
     * Instantiate a default Panasonic PLC communication object, the default station number is 0xEE
     * @param station 站号信息，默认为0xEE
     */
    public PanasonicMewtocolOverTcp( int station ) {
        this.setByteTransform(new RegularByteTransform());
        this.Station = station;
        this.getByteTransform().setDataFormat(DataFormat.DCBA);
        this.LogMsgFormatBinary  = false;
    }

    /**
     * 实例化一个默认的松下PLC通信对象，指定ip地址，端口，默认站号为0xEE<br />
     * Instantiate a default Panasonic PLC communication object, specify the IP address, port, and the default station number is 0xEE
     * @param ipAddress Ip地址数据
     * @param port 端口号
     * @param station 站号信息，默认为0xEE
     */
    public PanasonicMewtocolOverTcp( String ipAddress, int port, byte station ) {
        this(station);
        this.setIpAddress(ipAddress);
        this.setPort(port);
    }

    @Override
    protected INetMessage GetNewNetMessage() {
        return new SpecifiedCharacterMessage(AsciiControl.CR);
    }

    /**
     * 获取当前的设备站号信息
     * @return 站号信息
     */
    public int getStation() {
        return Station;
    }

    /**
     * 设置当前的设备站号信息
     * @param station 站号信息
     */
    public void setStation(int station) {
        Station = station;
    }

    /**
     * PLC设备的目标站号，需要根据实际的设置来填写<br />
     * The target station number of the PLC device needs to be filled in according to the actual settings
     */
    private int Station = (byte) 0xEE;

    /**
     * 读取指定地址的原始数据，地址示例：D0  F0  K0  T0  C0, 地址支持携带站号的访问方式，例如：s=2;D100<br />
     * Read the original data of the specified address, address example: D0 F0 K0 T0 C0, the address supports carrying station number information, for example: s=2;D100
     * @param address 起始地址
     * @param length 长度
     * @return 原始的字节数据的信息
     */
    public OperateResultExOne<byte[]> Read(String address, short length ) {
        int station = this.getStation();
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractParameter(address, "s", this.getStation());
        if (extra.IsSuccess) {
            station = extra.Content1;
            address = extra.Content2;
        }

        // 创建指令
        OperateResultExOne<ArrayList<byte[]>> command = PanasonicHelper.BuildReadCommand(station, address, length, false);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Byte> list = new ArrayList<Byte>();
        for (int i = 0; i < command.Content.size(); i++) {
            // 数据交互
            OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content.get(i));
            if (!read.IsSuccess) return read;

            // 提取数据
            OperateResultExOne<byte[]> extraData = PanasonicHelper.ExtraActualData(read.Content);
            if (!extraData.IsSuccess) return extraData;

            Utilities.ArrayListAddArray(list, extraData.Content);
        }

        return OperateResultExOne.CreateSuccessResult(Utilities.ToByteArray(list));
    }

    /**
     * 将数据写入到指定的地址里去，地址示例：D0  F0  K0  T0  C0, 地址支持携带站号的访问方式，例如：s=2;D100<br />
     * Write data to the specified address, address example: D0 F0 K0 T0 C0, the address supports carrying station number information, for example: s=2;D100
     * @param address 起始地址
     * @param value 真实数据
     * @return 是否写入成功
     */
    public OperateResult Write(String address, byte[] value ) {
        int station = this.getStation();
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractParameter(address, "s", this.getStation());
        if (extra.IsSuccess) {
            station = extra.Content1;
            address = extra.Content2;
        }

        // 创建指令
        OperateResultExOne<byte[]> command = PanasonicHelper.BuildWriteCommand(station, address, value);
        if (!command.IsSuccess) return command;

        // 数据交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 提取结果
        return PanasonicHelper.ExtraActualData(read.Content);
    }


    /**
     * 批量读取松下PLC的位数据，按照字为单位，地址为 X0,X10,Y10，读取的长度为16的倍数<br />
     * Read the bit data of Panasonic PLC in batches, the unit is word, the address is X0, X10, Y10, and the read length is a multiple of 16
     * @param address 起始地址
     * @param length 数据长度
     * @return 读取结果对象
     */
    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        if (PanasonicHelper.CheckBoolOnWordAddress(address)) {
            return PlcHelper.ReadBool(this, address, length, 16, false);
        }

        int station = this.getStation();
        OperateResultExTwo<Integer, String> extractParameter = PlcHelper.ExtractParameter(address, "s", this.getStation());
        if (extractParameter.IsSuccess) {
            station = extractParameter.Content1;
            address = extractParameter.Content2;
        }

        OperateResultExTwo<String, Integer> analysis = PanasonicHelper.AnalysisAddress(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        // 创建指令
        OperateResultExOne<ArrayList<byte[]>> command = PanasonicHelper.BuildReadCommand(station, address, length, true);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Byte> list = new ArrayList<Byte>();
        for (int i = 0; i < command.Content.size(); i++) {
            // 数据交互
            OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content.get(i));
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            // 提取数据
            OperateResultExOne<byte[]> extra = PanasonicHelper.ExtraActualData(read.Content);
            if (!extra.IsSuccess) return OperateResultExOne.CreateFailedResult(extra);

            // 提取bool
            Utilities.ArrayListAddArray(list, extra.Content);
        }

        return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray(Utilities.ToByteArray(list)), analysis.Content2 % 16, length));
    }

    /**
     * 读取单个的地址信息的bool值，地址举例：SR0.0  X0.0  Y0.0  R0.0  L0.0<br />
     * Read the bool value of a single address, for example: SR0.0 X0.0 Y0.0 R0.0 L0.0
     * @param address 起始地址
     * @return 读取结果对象
     */
    public OperateResultExOne<Boolean> ReadBool( String address ) {
        if (PanasonicHelper.CheckBoolOnWordAddress( address )) {
            return ByteTransformHelper.GetBoolResultFromArray(PlcHelper.ReadBool(this, address, (short) 1, 16, false));
        }

        int station = this.getStation();
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractParameter(address, "s", this.getStation());
        if (extra.IsSuccess) {
            station = extra.Content1;
            address = extra.Content2;
        }

        // 创建指令
        OperateResultExOne<byte[]> command = PanasonicHelper.BuildReadOneCoil(station, address);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        // 数据交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        // 提取数据
        return PanasonicHelper.ExtraActualBool(read.Content);
    }

    /**
     * 往指定的地址写入 <see cref="bool"/> 数组，地址举例：SR0.0  X0.0  Y0.0  R0.0  L0.0，往指定的地址写入 <see cref="bool"/> 数组，地址举例：SR0.0  X0.0  Y0.0  R0.0  L0.0，
     * 起始的位地址必须为16的倍数，写入的 <see cref="bool"/> 数组长度也为16的倍数。<br />
     * Write the <see cref="bool"/> array to the specified address, address example: SR0.0 X0.0 Y0.0 R0.0 L0.0,
     * the starting bit address must be a multiple of 16. <see cref="bool"/> The length of the array is also a multiple of 16. <br />
     * @param address 起始地址
     * @param values 数据值信息
     * @return 返回是否成功的结果对象
     */
    public OperateResult Write( String address, boolean[] values ) {
        int station = this.getStation();
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractParameter(address, "s", this.getStation());
        if (extra.IsSuccess) {
            station = extra.Content1;
            address = extra.Content2;
        }

        // 强制地址从字单位开始，强制写入长度为16个长度
        OperateResultExTwo<String, Integer> analysis = PanasonicHelper.AnalysisAddress(address);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        if (analysis.Content2 % 16 != 0)
            return new OperateResult(StringResources.Language.PanasonicAddressBitStartMulti16());
        if (values.length % 16 != 0) return new OperateResult(StringResources.Language.PanasonicBoolLengthMulti16());

        // 计算字节数据
        byte[] buffer = SoftBasic.BoolArrayToByte(values);

        // 创建指令
        OperateResultExOne<byte[]> command = PanasonicHelper.BuildWriteCommand(station, address, buffer);
        if (!command.IsSuccess) return command;

        // 数据交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 提取结果
        return PanasonicHelper.ExtraActualData(read.Content);
    }

    /**
     * 往指定的地址写入bool数据，地址举例：SR0.0  X0.0  Y0.0  R0.0  L0.0<br />
     * Write bool data to the specified address. Example address: SR0.0 X0.0 Y0.0 R0.0 L0.0
     * @param address 起始地址
     * @param value 数据值信息
     * @return 返回是否成功的结果对象
     */
    public OperateResult Write(String address, boolean value ) {
        int station = this.getStation();
        OperateResultExTwo<Integer, String> extra = PlcHelper.ExtractParameter(address, "s", this.getStation());
        if (extra.IsSuccess) {
            station = extra.Content1;
            address = extra.Content2;
        }

        // 创建指令
        OperateResultExOne<byte[]> command = PanasonicHelper.BuildWriteOneCoil(station, address, value);
        if (!command.IsSuccess) return command;

        // 数据交互
        OperateResultExOne<byte[]> read = ReadFromCoreServer(command.Content);
        if (!read.IsSuccess) return read;

        // 提取结果
        return PanasonicHelper.ExtraActualData(read.Content);
    }

    public String toString( ) {
        return "PanasonicMewtocolOverTcp[" + getIpAddress() + ":" + getPort() + "]";
    }

}
