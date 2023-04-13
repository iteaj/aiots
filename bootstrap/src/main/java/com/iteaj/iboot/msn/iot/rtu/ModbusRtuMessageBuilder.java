package com.iteaj.iboot.msn.iot.rtu;

import com.iteaj.iot.modbus.client.rtu.ModbusRtuClientMessage;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.modbus.consts.ModbusCoilStatus;
import com.iteaj.iot.modbus.server.rtu.ModbusRtuBody;
import com.iteaj.iot.modbus.server.rtu.ModbusRtuHeader;
import com.iteaj.iot.modbus.server.rtu.ModbusRtuMessage;
import com.iteaj.iot.modbus.server.rtu.ModbusRtuServerMessage;

/**
 * ModbusRtu协议的Server端报文构建
 */
public class ModbusRtuMessageBuilder {

    /**
     * 构建Modbus读线圈报文
     * @param device 访问的设备
     * @param start 从哪个寄存器开始读
     * @param bitNum 读多少位(一个字节8位)
     * @return
     */
    public static ModbusRtuClientMessage buildRead01Message(int device, int start, int bitNum) {
        return doBuildReadMessage(ModbusCode.Read01, device, start, bitNum);
    }

    /**
     * 构建Modbus读线圈报文
     * @param device 访问的设备
     * @param start 从哪个寄存器开始读
     * @param bitNum 读多少位(一个字节8位)
     * @return
     */
    public static ModbusRtuClientMessage buildRead02Message(int device, int start, int bitNum) {
        return doBuildReadMessage(ModbusCode.Read02, device, start, bitNum);
    }

    /**
     * 构建Modbus读保持寄存器报文
     * @param device 访问的设备
     * @param start 从哪个寄存器开始读
     * @param num 读几个寄存器
     * @return
     */
    public static ModbusRtuClientMessage buildRead03Message(int device, int start, int num) {
        return doBuildReadMessage(ModbusCode.Read03, device, start, num);
    }

    /**
     * 构建Modbus读输入寄存器报文
     * @param device 访问的设备 (1-255)
     * @param start 从哪个寄存器开始读 (1-65535)
     * @param num 读几个寄存器(1-2000)
     * @return
     */
    public static ModbusRtuClientMessage buildRead04Message(int device, int start, int num) {
        return doBuildReadMessage(ModbusCode.Read04, device, start, num);
    }

    /**
     * 构建Modbus写单个线圈报文
     * @param device 访问的设备
     * @param start 从哪个寄存器开始写
     * @param status 写内容
     * @return
     */
    public static ModbusRtuClientMessage buildWrite05Message(int device, int start, ModbusCoilStatus status) {
        if(status == null) {
            throw new IllegalArgumentException("[status]必填");
        }

        return doBuildWriteMessage(ModbusCode.Write05, device, start, 0, status.getCode());
    }

    /**
     * 构建Modbus写单个寄存器报文
     * @param device 访问的设备
     * @param start 从哪个寄存器开始写
     * @param write 写内容
     * @return
     */
    public static ModbusRtuClientMessage buildWrite06Message(int device, int start, byte[] write) {
        return doBuildWriteMessage(ModbusCode.Write06, device, start, 0, write);
    }

    /**
     * 构建Modbus写多个线圈报文(按位计算)
     * @param device 访问的设备
     * @param start 从哪个寄存器开始写
     * @param write 写内容 0xFF = 11111111
     * @return
     */
    public static ModbusRtuClientMessage buildWrite0FMessage(int device, int start, byte[] write) {
        if(write == null) {
            throw new IllegalArgumentException("[status]必填");
        }

        return doBuildWriteMessage(ModbusCode.Write0F, device, start, write.length * 8, write);
    }

    /**
     * 构建Modbus写多个寄存器报文
     * @param device 访问的设备
     * @param start 从哪个寄存器开始写
     * @param num 写几个寄存器
     * @param write 写到设备的内容
     * @return
     */
    public static ModbusRtuClientMessage buildWrite10Message(int device, int start, int num, byte[] write) {
        return doBuildWriteMessage(ModbusCode.Write10, device, start, num, write);
    }

    protected static ModbusRtuClientMessage doBuildReadMessage(ModbusCode code, int device, int start, int num) {
        ModbusRtuBody body = ModbusRtuBody.read(code, (short) start, (short)num);
        ModbusRtuHeader header = ModbusRtuHeader.buildRequestHeader((byte) device);
        return new ModbusRtuClientMessage(header, body);
    }

    protected static ModbusRtuClientMessage doBuildWriteMessage(ModbusCode code, int device, int start, int num, byte[] write) {
        ModbusRtuBody body;
        switch (code) {
            case Write05:
            case Write06:
                body = ModbusRtuBody.writeSingle(code, (short) start, write); break;
            case Write0F:
                body = ModbusRtuBody.write0F((short) start, (short)num, write); break;
            case Write10:
                body = ModbusRtuBody.write10((short) start, (short)num, write); break;

            default: throw new IllegalStateException("不支持写功能码["+code+"]");
        }

        ModbusRtuHeader header = ModbusRtuHeader.buildRequestHeader((byte) device);
        return new ModbusRtuClientMessage(header, body);
    }

}
