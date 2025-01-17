package com.iteaj.plugin.protocol.plc.core.serial;

import com.iteaj.plugin.protocol.plc.core.Utilities;
import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;

import java.nio.charset.StandardCharsets;

/**
 * 用于LRC验证的类，提供了标准的验证方法<br />
 * The class used for LRC verification provides a standard verification method
 */
public class SoftLRC {
    /**
     * 获取对应的数据的LRC校验码<br />
     * Class for LRC validation that provides a standard validation method
     * @param value 需要校验的数据，不包含LRC字节
     * @return 返回带LRC校验码的字节数组，可用于串口发送
     */
    public static byte[] LRC( byte[] value ) {
        if (value == null) return null;

        int sum = 0;
        for (int i = 0; i < value.length; i++) {
            sum += value[i] & 0xFF;
        }

        sum = sum % 256;
        sum = 256 - sum;

        byte[] LRC = new byte[]{(byte) sum};
        return SoftBasic.SpliceTwoByteArray(value, LRC);
    }

    /**
     * 检查数据是否符合LRC的验证<br />
     * Check data for compliance with LRC validation
     * @param value 等待校验的数据，是否正确
     * @return 是否校验成功
     */
    public static boolean CheckLRC( byte[] value ) {
        if (value == null) return false;

        int length = value.length;
        byte[] buf = new byte[length - 1];
        System.arraycopy(value, 0, buf, 0, buf.length);

        byte[] LRCbuf = LRC(buf);
        if (LRCbuf[length - 1] == value[length - 1]) {
            return true;
        }
        return false;
    }

    /**
     * 根据传入的原始字节数组，计算和校验信息，可以指定起始的偏移地址和尾部的字节数量信息<br />
     * Calculate and check the information according to the incoming original byte array, you can specify the starting offset address and the number of bytes at the end
     * @param buffer 原始字节数组信息
     * @param headCount 起始的偏移地址信息
     * @param lastCount 尾部的字节数量信息
     * @return 和校验的结果
     */
    public static int CalculateAcc( byte[] buffer, int headCount, int lastCount )
    {
        int count = 0;
        for (int i = headCount; i < buffer.length - lastCount; i++)
        {
            count += buffer[i];
        }
        return count;
    }

    /**
     * 计算数据的和校验，并且输入和校验的值信息<br />
     * Calculate the sum check of the data, and enter the value information of the sum check
     * @param buffer 原始字节数组信息
     * @param headCount 起始的偏移地址信息
     * @param lastCount 尾部的字节数量信息
     */
    public static void CalculateAccAndFill( byte[] buffer, int headCount, int lastCount )
    {
        byte acc = (byte)CalculateAcc( buffer, headCount, lastCount );
        Utilities.ByteArrayCopyTo( String.format("%02X",acc).getBytes(StandardCharsets.US_ASCII), buffer, buffer.length - lastCount );
    }

    /**
     * 计算数据的和校验，并且和当前已经存在的和校验信息进行匹配，返回是否匹配成功<br />
     * Calculate the sum check of the data, and match it with the existing sum check information, and return whether the match is successful
     * @param buffer 原始字节数组信息
     * @param headCount 起始的偏移地址信息
     * @param lastCount 尾部的字节数量信息
     * @return 和校验是否检查通过
     */
    public static boolean CalculateAccAndCheck( byte[] buffer, int headCount, int lastCount )
    {
        byte acc = (byte)CalculateAcc( buffer, headCount, lastCount );
        return String.format("%02X", acc).equals(new String(buffer, buffer.length - lastCount, 2, StandardCharsets.US_ASCII));
    }
}
