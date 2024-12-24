package com.iteaj.plugin.protocol.plc.core.profinet.Yokogawa;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.StringResources;

import java.nio.charset.StandardCharsets;

/**
 * 横河PLC的系统基本信息<br />
 * Basic system information of Yokogawa PLC
 */
public class YokogawaSystemInfo {
    /**
     * 当前系统的ID名称，例如F3SP21-ON<br />
     * The ID name of the current syParsetem, such as F3SP21-ON
     */
    public String SystemID = "";

    /**
     * 当前系统的修订版本号<br />
     * The revision number of the current system
     */
    public String Revision = "";

    /**
     * 当前系统的类型，分为 <b>Sequence</b> 和 <b>BASIC</b> <br />
     * The type of the current system, divided into <b>Sequence</b> and <b>BASIC</b>
     */
    public String CpuType = "";

    /**
     * 当前系统的程序大小，如果是Sequence系统，就是步序总量，如果是BASIC系统，就是字节数量<br />
     * The program size of the current system, if it is a Sequence system, it is the total number of steps, if it is a BASIC system, it is the number of bytes
     */
    public int ProgramAreaSize = 0;

    public String toString( ) {
        return "YokogawaSystemInfo[" + SystemID + "]";
    }

    /**
     * 根据原始的数据信息解析出{@link YokogawaSystemInfo}对象<br />
     * Analyze the {@link YokogawaSystemInfo} object according to the original data information
     * @param content 原始的数据信息
     * @return 是否解析成功的结果对象
     */
    public static OperateResultExOne<YokogawaSystemInfo> Parse(byte[] content ) {
        try {
            YokogawaSystemInfo systemInfo = new YokogawaSystemInfo();
            systemInfo.SystemID = new String(content, 0, 16, StandardCharsets.US_ASCII).trim();
            systemInfo.Revision = new String(content, 16, 8, StandardCharsets.US_ASCII).trim();

            if (content[25] == 0x01 || content[25] == 0x11) systemInfo.CpuType = "Sequence";
            else if (content[25] == 0x02 || content[25] == 0x12) systemInfo.CpuType = "BASIC";
            else systemInfo.CpuType = StringResources.Language.UnknownError();

            systemInfo.ProgramAreaSize = content[26] * 256 + content[27];
            return OperateResultExOne.CreateSuccessResult(systemInfo);
        } catch (Exception ex) {
            return new OperateResultExOne<YokogawaSystemInfo>("Parse YokogawaSystemInfo failed: " + ex.getMessage() + System.lineSeparator() +
                    "Source: " + SoftBasic.ByteToHexString(content));
        }
    }
}
