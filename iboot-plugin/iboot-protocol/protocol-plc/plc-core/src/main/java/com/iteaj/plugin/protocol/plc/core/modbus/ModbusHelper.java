package com.iteaj.plugin.protocol.plc.core.modbus;

import com.iteaj.plugin.protocol.plc.core.StringResources;
import com.iteaj.plugin.protocol.plc.core.Utilities;
import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.net.ReadWriteNetHelper;
import com.iteaj.plugin.protocol.plc.core.serial.SoftCRC16;
import com.iteaj.plugin.protocol.plc.core.types.FunctionOperateExOne;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;

import java.util.ArrayList;

/**
 * Modbus协议相关辅助类，关于Modbus消息的组装以及拆分<br />
 *
 */
public class ModbusHelper {

    public static OperateResultExOne<byte[]> ExtraRtuResponseContent(byte[] send, byte[] response, boolean crcCheck) {
        // 长度校验
        if (response == null || response.length < 5)
            return new OperateResultExOne<byte[]>(StringResources.Language.ReceiveDataLengthTooShort() + "5 Content: " + SoftBasic.ByteToHexString(response, ' '));

        byte[] responseCopy = Utilities.CopyFrom(response);

        // 尝试两次机会，先根据长度信息移除后续干扰码，如果crc不通过，再次尝试移除头子节的干扰码，然后再次重试操作
        for (int i = 0; i < 2; i++){

            if (responseCopy[1] == 0x01 || responseCopy[1] == 0x02 || responseCopy[1] == 0x03 || responseCopy[1] == 0x04 || responseCopy[1] == 0x17) {
                if (responseCopy.length > 5 + (responseCopy[2] & 0xff))
                    responseCopy = SoftBasic.BytesArraySelectBegin(responseCopy, 5 + (responseCopy[2] & 0xff));
            } else if (responseCopy[1] == 0x05 || responseCopy[1] == 0x06 || responseCopy[1] == 0x0F || responseCopy[1] == 0x10) {
                if (responseCopy.length > 8) responseCopy = SoftBasic.BytesArraySelectBegin(responseCopy, 8);
            } else if ((responseCopy[1] & 0xff) > 0x80) {
                if (responseCopy.length > 5) responseCopy = SoftBasic.BytesArraySelectBegin(responseCopy, 5);
            }

            // 检查crc
            if ( crcCheck && !SoftCRC16.CheckCRC16(responseCopy)) {
                if (i == 0) {
                    responseCopy = SoftBasic.BytesArrayRemoveBegin(response, 1);
                    continue;
                }
                return new OperateResultExOne<byte[]>(StringResources.Language.ModbusCRCCheckFailed() +
                        SoftBasic.ByteToHexString(response, ' '));
            }
            break;
        }


        // 站号检查
        if (send[0] != responseCopy[0]) return new OperateResultExOne<byte[]>( "Station not match, request: " + send[0] + ", but response is " + responseCopy[0] );

        // 发生了错误
        if (((send[1] & 0xff) + 0x80) == (responseCopy[1] & 0xff))
            return new OperateResultExOne<byte[]>((responseCopy[2] & 0xff ), ModbusInfo.GetDescriptionByErrorCode(responseCopy[2]));

        if (send[1] != responseCopy[1])
            return new OperateResultExOne<byte[]>(responseCopy[1], "Receive Command Check Failed: ");

        // 移除CRC校验，返回真实数据
        return ModbusInfo.ExtractActualData(ModbusInfo.ExplodeRtuCommandToCore(responseCopy));
    }


    public static OperateResultExOne<byte[]> Read(IModbus modbus, String address, short length) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, ModbusInfo.ReadRegister);
        if (!modbusAddress.IsSuccess) return OperateResultExOne.CreateFailedResult(modbusAddress);

        OperateResultExOne<byte[][]> command = ModbusInfo.BuildReadModbusCommand(modbusAddress.Content, length,
                modbus.getStation(), modbus.getAddressStartWithZero(), ModbusInfo.ReadRegister);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        ArrayList<Byte> resultArray = new ArrayList<Byte>();
        for (int i = 0; i < command.Content.length; i++) {
            OperateResultExOne<byte[]> read = modbus.ReadFromCoreServer(command.Content[i]);
            if (!read.IsSuccess) return OperateResultExOne.<byte[]>CreateFailedResult(read);

            Utilities.ArrayListAddArray(resultArray, read.Content);
        }

        return OperateResultExOne.CreateSuccessResult(Utilities.getBytes(resultArray));
    }

    public static OperateResult Write(IModbus modbus, String address, byte[] value) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress( address, ModbusInfo.WriteRegister );
        if (!modbusAddress.IsSuccess) return modbusAddress;

        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteWordModbusCommand(modbusAddress.Content, value,
                modbus.getStation(), modbus.getAddressStartWithZero(), ModbusInfo.WriteRegister);
        if (!command.IsSuccess) return command;

        return modbus.ReadFromCoreServer(command.Content);
    }

    public static OperateResult Write(IModbus modbus, String address, short value) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress( address, ModbusInfo.WriteOneRegister );
        if (!modbusAddress.IsSuccess) return modbusAddress;

        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteWordModbusCommand(modbusAddress.Content, value,
                modbus.getStation(), modbus.getAddressStartWithZero(), ModbusInfo.WriteOneRegister);
        if (!command.IsSuccess) return command;

        return modbus.ReadFromCoreServer(command.Content);
    }

    public static OperateResult WriteMask(IModbus modbus, String address, short andMask, short orMask) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress( address, ModbusInfo.WriteMaskRegister );
        if (!modbusAddress.IsSuccess) return modbusAddress;

        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteMaskModbusCommand(modbusAddress.Content, andMask, orMask,
                modbus.getStation(), modbus.getAddressStartWithZero(), ModbusInfo.WriteMaskRegister);
        if (!command.IsSuccess) return command;

        return modbus.ReadFromCoreServer(command.Content);
    }

    public static OperateResultExOne<boolean[]> ReadBoolHelper(IModbus modbus, String address, short length, byte function) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress( address, function );
        if (!modbusAddress.IsSuccess) return OperateResultExOne.CreateFailedResult(modbusAddress);

        if (modbusAddress.Content.indexOf('.') > 0) {
            String[] addressSplits = Utilities.SplitDot(address);
            int bitIndex = 0;
            try
            {
                String[] modbusSplits = Utilities.SplitDot( modbusAddress.Content );
                bitIndex = Integer.parseInt( modbusSplits[1] );
            }
            catch(Exception ex)
            {
                return new OperateResultExOne<boolean[]>( "Bit Index format wrong, " + ex.getMessage() );
            }
            short len = (short) ((length + bitIndex + 15) / 16);

            OperateResultExOne<byte[]> read = modbus.Read(addressSplits[0], len);
            if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

            return OperateResultExOne.CreateSuccessResult(SoftBasic.BoolArraySelectMiddle(SoftBasic.ByteToBoolArray(SoftBasic.BytesReverseByWord(read.Content)), bitIndex, length));
        } else {
            OperateResultExOne<byte[][]> command = ModbusInfo.BuildReadModbusCommand(modbusAddress.Content, length,
                    modbus.getStation(), modbus.getAddressStartWithZero(), function);
            if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

            ArrayList<Boolean> resultArray = new ArrayList<>();
            for (int i = 0; i < command.Content.length; i++) {
                OperateResultExOne<byte[]> read = modbus.ReadFromCoreServer(command.Content[i]);
                if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

                int bitLength = (command.Content[i][4] & 0xff) * 256 + (command.Content[i][5] & 0xff);
                Utilities.ArrayListAddArray(resultArray, SoftBasic.ByteToBoolArray(read.Content, bitLength));
            }

            return OperateResultExOne.CreateSuccessResult(Utilities.getBools(resultArray));
        }
    }

    public static OperateResult Write(IModbus modbus, String address, boolean[] values) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress( address, ModbusInfo.WriteCoil );
        if (!modbusAddress.IsSuccess) return modbusAddress;

        if (modbusAddress.Content.indexOf( '.' ) > 0) {
            return ReadWriteNetHelper.WriteBoolWithWord(modbus, address, values, 16, true, Utilities.SplitDot(modbusAddress.Content)[1]);
        }

        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteBoolModbusCommand(modbusAddress.Content, values,
                modbus.getStation(), modbus.getAddressStartWithZero(), ModbusInfo.WriteCoil);
        if (!command.IsSuccess) return command;

        return modbus.ReadFromCoreServer(command.Content);
    }

    public static OperateResult Write(IModbus modbus, String address, boolean value) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(address, ModbusInfo.WriteOneCoil);
        if (!modbusAddress.IsSuccess) return modbusAddress;

        if (address.indexOf('.') > 0 && !modbus.getEnableWriteMaskCode())
            return Write(modbus, address, new boolean[]{value});

        OperateResultExOne<byte[]> command = ModbusInfo.BuildWriteBoolModbusCommand(modbusAddress.Content, value,
                modbus.getStation(), modbus.getAddressStartWithZero(), ModbusInfo.WriteOneCoil);
        if (!command.IsSuccess) return command;

        OperateResult write = modbus.ReadFromCoreServer(command.Content);
        if (write.IsSuccess) return write;

        if (address.indexOf('.') > 0 && write.ErrorCode == 0x01) {
            modbus.setEnableWriteMaskCode(false);
            // 再重新尝试一次
            return Write(modbus, address, new boolean[]{value});
        }
        return write;
    }

    /**
     * 使用0x17功能码来实现同时写入并读取数据的操作，使用一条报文来实现，需要指定读取的地址，长度，写入的地址，写入的数据信息，返回读取的结果数据。<br />
     * Use 0x17 function code to write and read data at the same time, and use a message to implement it,
     * you need to specify the read address, length, written address, written data information, and return the read result data.
     * @param modbus Modbus通信对象
     * @param readAddress 读取的地址信息
     * @param length 读取的长度信息
     * @param writeAddress 写入的地址信息
     * @param value 写入的字节数据信息
     * @return 读取的结果对象
     */
    public static OperateResultExOne<byte[]> ReadWrite( IModbus modbus, String readAddress, short length, String writeAddress, byte[] value ) {
        OperateResultExOne<String> modbusAddress = modbus.TranslateToModbusAddress(readAddress, ModbusInfo.ReadWrite);
        if (!modbusAddress.IsSuccess) return OperateResultExOne.CreateFailedResult(modbusAddress);

        OperateResultExOne<String> modbusAddress2 = modbus.TranslateToModbusAddress(writeAddress, ModbusInfo.ReadWrite);
        if (!modbusAddress2.IsSuccess) return OperateResultExOne.CreateFailedResult(modbusAddress2);

        OperateResultExOne<byte[]> command = ModbusInfo.BuildReadWriteModbusCommand(modbusAddress.Content, length, modbusAddress2.Content, value, modbus.getStation(), modbus.getAddressStartWithZero(), ModbusInfo.ReadWrite);
        if (!command.IsSuccess) return OperateResultExOne.CreateFailedResult(command);

        return modbus.ReadFromCoreServer(command.Content);
    }

    public static String TransAddressToModbus( String station, String address, String[] code, int[] offset,  FunctionOperateExOne<String, Integer> prase ) throws Exception {
        for (int i = 0; i < code.length; i++)
        {
            if (address.matches( "^" +code[i] + "[0-9]+" ))
            {
                return station + (prase.Action( address.substring( code[i].length() ) ) + offset[i]);
            }
        }
        throw new Exception( StringResources.Language.NotSupportedDataType() );
    }

    public static String TransPointAddressToModbus(String station, String address, String[] code, int[] offset, FunctionOperateExOne<String, Integer> prase ) throws Exception {
        int index = address.indexOf('.');
        if (index > 0) {
            String tail = address.substring(index);
            address = address.substring(0, index);

            return TransAddressToModbus(station, address, code, offset, prase) + tail;
        }
        throw new Exception(StringResources.Language.NotSupportedDataType());
    }
}
