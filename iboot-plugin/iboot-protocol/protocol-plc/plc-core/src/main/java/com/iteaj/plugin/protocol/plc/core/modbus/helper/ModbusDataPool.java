package com.iteaj.plugin.protocol.plc.core.modbus.helper;

import com.iteaj.plugin.protocol.plc.core.modbus.ModbusInfo;
import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;
import com.iteaj.plugin.protocol.plc.core.address.ModbusAddress;
import com.iteaj.plugin.protocol.plc.core.basic.SoftBuffer;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

/**
 *
 */
public class ModbusDataPool {

    public ModbusDataPool( byte station ) {
        this.coilBuffer = new SoftBuffer(DataPoolLength);
        this.inputBuffer = new SoftBuffer(DataPoolLength);
        this.registerBuffer = new SoftBuffer(DataPoolLength * 2);
        this.inputRegisterBuffer = new SoftBuffer(DataPoolLength * 2);

        this.registerBuffer.setIsBoolReverseByWord(true);
        this.inputRegisterBuffer.setIsBoolReverseByWord(true);
        this.station = station;
    }


    public OperateResultExOne<byte[]> Read(String address, short length ) {
        OperateResultExOne<ModbusAddress> analysis = ModbusInfo.AnalysisAddress(address, station, true, ModbusInfo.ReadRegister);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        if (analysis.Content.getFunction() == ModbusInfo.ReadRegister)
            return OperateResultExOne.CreateSuccessResult(registerBuffer.GetBytes(analysis.Content.getAddress() * 2, length * 2));
        else if (analysis.Content.getFunction() == ModbusInfo.ReadInputRegister)
            return OperateResultExOne.CreateSuccessResult(inputRegisterBuffer.GetBytes(analysis.Content.getAddress() * 2, length * 2));
        else
            return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
    }

    public OperateResult Write(String address, byte[] value ) {
        OperateResultExOne<ModbusAddress> analysis = ModbusInfo.AnalysisAddress(address, station, true, ModbusInfo.ReadRegister);
        if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

        if (analysis.Content.getFunction() == ModbusInfo.ReadRegister || analysis.Content.getFunction() == ModbusInfo.WriteOneRegister || analysis.Content.getFunction() == ModbusInfo.WriteRegister) {
            registerBuffer.SetBytes(value, analysis.Content.getAddress() * 2);
            return OperateResult.CreateSuccessResult();
        } else if (analysis.Content.getFunction() == ModbusInfo.ReadInputRegister) {
            inputRegisterBuffer.SetBytes(value, analysis.Content.getAddress() * 2);
            return OperateResult.CreateSuccessResult();
        } else {
            return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
        }
    }

    public OperateResultExOne<boolean[]> ReadBool( String address, short length ) {
        if (address.indexOf('.') < 0) {
            OperateResultExOne<ModbusAddress> analysis = ModbusInfo.AnalysisAddress(address, station, true, ModbusInfo.ReadCoil);
            if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

            if (analysis.Content.getFunction() == ModbusInfo.ReadCoil || analysis.Content.getFunction() == ModbusInfo.WriteOneCoil || analysis.Content.getFunction() == ModbusInfo.WriteCoil)
                return OperateResultExOne.CreateSuccessResult(Utilities.getBoolArray(coilBuffer.GetBytes(analysis.Content.getAddress(), length)));
            else if (analysis.Content.getFunction() == ModbusInfo.ReadDiscrete)
                return OperateResultExOne.CreateSuccessResult(Utilities.getBoolArray(inputBuffer.GetBytes(analysis.Content.getAddress(), length)));
            else
                return new OperateResultExOne<boolean[]>(StringResources.Language.NotSupportedDataType());
        } else {
            // 以位的方式读取寄存器，输入寄存器的值
            try {
                int bitIndex = Integer.parseInt(address.substring(address.indexOf('.') + 1));
                address = address.substring(0, address.indexOf('.'));

                OperateResultExOne<ModbusAddress> analysis = ModbusInfo.AnalysisAddress(address, station, true, ModbusInfo.ReadRegister);
                if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

                bitIndex = analysis.Content.getAddress() * 16 + bitIndex;
                if (analysis.Content.getFunction() == ModbusInfo.ReadRegister) {
                    return OperateResultExOne.CreateSuccessResult(registerBuffer.GetBool(bitIndex, length));
                } else if (analysis.Content.getFunction() == ModbusInfo.ReadInputRegister) {
                    return OperateResultExOne.CreateSuccessResult(inputRegisterBuffer.GetBool(bitIndex, length));
                } else {
                    return new OperateResultExOne<boolean[]>(StringResources.Language.NotSupportedDataType());
                }
            } catch (Exception ex) {
                return new OperateResultExOne<boolean[]>(ex.getMessage());
            }
        }
    }

    public OperateResult Write( String address, boolean[] value ) {
        if (address.indexOf('.') < 0) {
            OperateResultExOne<ModbusAddress> analysis = ModbusInfo.AnalysisAddress(address, station, true, ModbusInfo.ReadCoil);
            if (!analysis.IsSuccess) return OperateResultExOne.CreateFailedResult(analysis);

            if (analysis.Content.getFunction() == ModbusInfo.ReadCoil || analysis.Content.getFunction() == ModbusInfo.WriteCoil || analysis.Content.getFunction() == ModbusInfo.WriteOneCoil) {
                coilBuffer.SetBytes(Utilities.ToByteArray(value), analysis.Content.getAddress());
                return OperateResult.CreateSuccessResult();
            } else if (analysis.Content.getFunction() == ModbusInfo.ReadDiscrete) {
                inputBuffer.SetBytes(Utilities.ToByteArray(value), analysis.Content.getAddress());
                return OperateResult.CreateSuccessResult();
            } else {
                return new OperateResultExOne<byte[]>(StringResources.Language.NotSupportedDataType());
            }
        } else {
            try {
                int bitIndex = Integer.parseInt(address.substring(address.indexOf('.') + 1));
                address = address.substring(0, address.indexOf('.'));

                OperateResultExOne<ModbusAddress> analysis = ModbusInfo.AnalysisAddress(address, station, true, ModbusInfo.ReadRegister);
                if (!analysis.IsSuccess) return analysis;

                bitIndex = analysis.Content.getAddress() * 16 + bitIndex;
                if (analysis.Content.getFunction() == ModbusInfo.ReadRegister) {
                    registerBuffer.SetBool(value, bitIndex);
                    return OperateResult.CreateSuccessResult();
                } else if (analysis.Content.getFunction() == ModbusInfo.ReadInputRegister) {
                    inputRegisterBuffer.SetBool(value, bitIndex);
                    return OperateResult.CreateSuccessResult();
                } else {
                    return new OperateResult(StringResources.Language.NotSupportedDataType());
                }
            } catch (Exception ex) {
                return new OperateResult(ex.getMessage());
            }
        }
    }

	// region Coil Read Write

    /**
     * 读取地址的线圈的通断情况
     * @param address 起始地址，示例："100"
     * @return <c>True</c>或是<c>False</c>
     */
    public boolean ReadCoil( String address ) {
        int add = Integer.parseInt(address);
        return coilBuffer.GetByte(add) != 0x00;
    }

    /**
     * 批量读取地址的线圈的通断情况
     * @param address 起始地址，示例："100"
     * @param length 读取长度
     * @return <c>True</c>或是<c>False</c>
     */
    public boolean[] ReadCoil( String address, short length ) {
        int add = Integer.parseInt(address);
        return Utilities.getBoolArray(coilBuffer.GetBytes(add, length));
    }

    /**
     * 写入线圈的通断值
     * @param address 起始地址，示例："100"
     * @param data 是否通断
     */
    public void WriteCoil( String address, boolean data ) {
        int add = Integer.parseInt(address);
        coilBuffer.SetValue((byte) (data ? 0x01 : 0x00), add);
    }

    /**
     * 写入线圈数组的通断值
     * @param address 起始地址，示例："100"
     * @param data 是否通断
     */
    public void WriteCoil( String address, boolean[] data ) {
        if (data == null) return;

        int add = Integer.parseInt(address);
        coilBuffer.SetBytes(Utilities.ToByteArray(data), add);
    }

	// endregion

	// region Discrete Read Write

    /**
     * 读取地址的离散线圈的通断情况
     * @param address 起始地址，示例："100"
     * @return <c>True</c>或是<c>False</c>
     */
    public boolean ReadDiscrete( String address ) {
        int add = Integer.parseInt(address);
        return inputBuffer.GetByte(add) != 0x00;
    }

    /**
     * 批量读取地址的离散线圈的通断情况
     * @param address 起始地址，示例："100"
     * @param length 读取长度
     * @return <c>True</c>或是<c>False</c>
     */
    public boolean[] ReadDiscrete( String address, short length ) {
        int add = Integer.parseInt(address);
        return Utilities.getBoolArray(inputBuffer.GetBytes(add, length));
    }

    /**
     * 写入离散线圈的通断值
     * @param address 起始地址，示例："100"
     * @param data 是否通断
     */
    public void WriteDiscrete( String address, boolean data ) {
        int add = Integer.parseInt(address);
        inputBuffer.SetValue((byte) (data ? 0x01 : 0x00), add);
    }

    /**
     * 写入离散线圈数组的通断值
     * @param address 起始地址，示例："100"
     * @param data 是否通断
     */
    public void WriteDiscrete( String address, boolean[] data ) {
        if (data == null) return;

        int add = Integer.parseInt(address);
        inputBuffer.SetBytes(Utilities.ToByteArray(data), add);
    }

	// endregion

    public byte[] SaveToBytes( )
    {
        byte[] buffer = new byte[DataPoolLength * 6];
        System.arraycopy( coilBuffer.GetBytes( ), 0,          buffer, DataPoolLength * 0, DataPoolLength );
        System.arraycopy( inputBuffer.GetBytes( ), 0,         buffer, DataPoolLength * 1, DataPoolLength );
        System.arraycopy( registerBuffer.GetBytes( ), 0,      buffer, DataPoolLength * 2, DataPoolLength * 2 );
        System.arraycopy( inputRegisterBuffer.GetBytes( ), 0, buffer, DataPoolLength * 4, DataPoolLength * 2 );
        return buffer;
    }

    public void LoadFromBytes( byte[] content, int index )
    {
        coilBuffer.         SetBytes( content, DataPoolLength * 0 + index, 0, DataPoolLength );
        inputBuffer.        SetBytes( content, DataPoolLength * 1 + index, 0, DataPoolLength );
        registerBuffer.     SetBytes( content, DataPoolLength * 2 + index, 0, DataPoolLength * 2 );
        inputRegisterBuffer.SetBytes( content, DataPoolLength * 4 + index, 0, DataPoolLength * 2 );
    }

    private byte station = 1;                     // 当前的数据池的站号信息
    private SoftBuffer coilBuffer;                // 线圈的数据池
    private SoftBuffer inputBuffer;               // 离散输入的数据池
    private SoftBuffer registerBuffer;            // 寄存器的数据池
    private SoftBuffer inputRegisterBuffer;       // 输入寄存器的数据池
    private final int DataPoolLength = 65536;     // 数据的长度
}
