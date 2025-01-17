package com.iteaj.plugin.protocol.plc.core.net;

import com.iteaj.plugin.protocol.plc.core.basic.SoftBasic;
import com.iteaj.plugin.protocol.plc.core.types.Convert;
import com.iteaj.plugin.protocol.plc.core.types.OperateResult;
import com.iteaj.plugin.protocol.plc.core.types.OperateResultExOne;
import com.iteaj.plugin.protocol.plc.core.Utilities;

public class ReadWriteNetHelper {

    /**
     * 写入位到字寄存器的功能，该功能先读取字寄存器的字数据，然后修改其中的位，再写入回去，可能存在脏数据的风险<br />
     * The function of writing bit-to-word registers, which first reads the word data of the word register, then modifies the bits in it, and then writes back, which may be the risk of dirty data
     * <br /><br />
     * 关于脏数据风险：从读取数据，修改位，再次写入数据时，大概需要经过3ms~10ms不等的时间，如果此期间内PLC修改了该字寄存器的其他位，再次写入数据时会恢复该点位的数据到读取时的初始值，可能引发设备故障，请谨慎开启此功能。
     * @param readWrite 通信对象信息
     * @param address 写入的地址信息，需要携带.号
     * @param values 写入的值信息
     * @param addLength 多少长度的bit位组成一个字地址信息
     * @return 是否写入成功
     */
    public static OperateResult WriteBoolWithWord(IReadWriteNet readWrite, String address, boolean[] values, int addLength  ) // addLength = 16
    {
        return WriteBoolWithWord(readWrite, address, values, addLength, false, null);
    }

    /**
     * 写入位到字寄存器的功能，该功能先读取字寄存器的字数据，然后修改其中的位，再写入回去，可能存在脏数据的风险<br />
     * The function of writing bit-to-word registers, which first reads the word data of the word register, then modifies the bits in it, and then writes back, which may be the risk of dirty data
     *
     * @apiNote 关于脏数据风险：从读取数据，修改位，再次写入数据时，大概需要经过3ms~10ms不等的时间，如果此期间内PLC修改了该字寄存器的其他位，再次写入数据时会恢复该点位的数据到读取时的初始值，可能引发设备故障，请谨慎开启此功能。
     * @param readWrite 通信对象信息
     * @param address 写入的地址信息，需要携带'.'号
     * @param values 写入的值信息
     * @param addLength 多少长度的bit位组成一个字地址信息
     * @param reverseWord 对原始数据是否按照字单位进行反转操作
     * @param bitStr 额外指定的位索引，如果为空，则使用 "address" 中的地址位偏移信息
     * @return 是否写入成功
     */
    public static OperateResult WriteBoolWithWord(IReadWriteNet readWrite, String address, boolean[] values, int addLength, boolean reverseWord, String bitStr  ) // addLength = 16
    {
        String[] adds = Utilities.SplitDot(address);
        int bit = 0;
        try {
            if (Utilities.IsStringNullOrEmpty(bitStr)) {
                if (adds.length > 1) bit = Convert.ToInt32(adds[1]);
            } else {
                bit = Convert.ToInt32(bitStr);
            }
        } catch (Exception ex) {
            return new OperateResult(address + " Bit index input wrong: " + ex.getMessage());
        }
        short wordLength = (short) ((bit + values.length + addLength - 1) / addLength);

        OperateResultExOne<byte[]> read = readWrite.Read(adds[0], wordLength);
        if (!read.IsSuccess) return OperateResultExOne.CreateFailedResult(read);

        boolean[] array = reverseWord ? SoftBasic.ByteToBoolArray(SoftBasic.BytesReverseByWord(read.Content)) : SoftBasic.ByteToBoolArray(read.Content);
        if (bit + values.length <= array.length)
            Utilities.ByteArrayCopyTo(values, array, bit);

        // 重新写入到PLC中
        return readWrite.Write(adds[0], reverseWord ? SoftBasic.BytesReverseByWord(SoftBasic.BoolArrayToByte(array)) : SoftBasic.BoolArrayToByte(array));
    }

    /**
     * @see ReadWriteNetHelper#WriteBoolWithWord(IReadWriteNet, String, boolean[], int) 
     */
    public static OperateResult WriteBoolWithWord(IReadWriteNet readWrite, String address, boolean[] values  ){
        return WriteBoolWithWord(readWrite,address,values,16);
    }
}
