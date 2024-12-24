package com.iteaj.plugin.protocol.plc.core.profinet.Omron;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.types.Encoding;

/**
 * 欧姆龙Cpu的状态信息
 */
public class OmronCpuUnitStatus {
    /**
     * 实例化一个默认的对象
     */
    public OmronCpuUnitStatus() {

    }

    /**
     * 从原始的字节数组来实例化一个
     *
     * @param data 原始的字节数据
     */
    public OmronCpuUnitStatus(byte[] data) {
        Status = SoftBasic.BoolOnByteIndex(data[0], 0) ? "Run" : "Stop";
        BatteryStatus = SoftBasic.BoolOnByteIndex(data[0], 2) ? "Present" : "No";
        CpuStatus = SoftBasic.BoolOnByteIndex(data[0], 7) ? "Standby" : "Normal";
        Mode = data[1] == 0x00 ? "PROGRAM" : data[1] == 0x02 ? "MONITOR" : data[1] == 0x04 ? "RUN" : "";
        ErrorCode = data[8] * 256 + data[9];
        if (ErrorCode > 0)
            ErrorMessage = Encoding.ASCII.GetString(data, 10, 16).trim();
    }

    /**
     * Run 或是 Stop
     */
    public String Status = "";

    /**
     * No 或是 Present
     */
    public String BatteryStatus = "";

    /**
     * Normal 或是 Standby
     */
    public String CpuStatus = "";

    /**
     * PROGRAM, MONITOR, RUN
     */
    public String Mode = "";

    /**
     * Among errors that occur when the command is executed, the error code indicates the most serious. If there are no errors, it will be 0000 (hex)
     */
    public int ErrorCode = 0;

    /**
     * Indicates messages from execution of FAL(006) or FALS(007). If there is no error message,
     * or if FAL(006) or FALS(007) are not being executed, 16 spaces( ASCII 20) will be returned.
     */
    public String ErrorMessage = "";

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Status: " + Status + "\r\n");
        stringBuilder.append("BatteryStatus: " + BatteryStatus + "\r\n");
        stringBuilder.append("CpuStatus: " + CpuStatus + "\r\n");
        stringBuilder.append("ErrorCode: " + ErrorCode + "\r\n");
        stringBuilder.append("ErrorMessage: " + ErrorMessage + "\r\n");
        return stringBuilder.toString();
    }
}
