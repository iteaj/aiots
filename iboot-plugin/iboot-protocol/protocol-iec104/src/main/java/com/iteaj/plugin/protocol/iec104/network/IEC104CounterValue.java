package com.iteaj.plugin.protocol.iec104.network;

import org.openmuc.j60870.ie.IeBinaryCounterReading;

import java.util.Set;

/**
 * 累积量统计值
 */
public class IEC104CounterValue {

    /**
     * 信息对象地址
     */
    private int ioa;

    /**
     * 累计值
     */
    private final int counterReading;

    /**
     * 序号
     */
    private final int sequenceNumber;

    /**
     * @see org.openmuc.j60870.ie.IeBinaryCounterReading.Flag#CARRY 在相应的累加周期内计数器溢出
     * @see org.openmuc.j60870.ie.IeBinaryCounterReading.Flag#INVALID 无效
     * @see org.openmuc.j60870.ie.IeBinaryCounterReading.Flag#COUNTER_ADJUSTED 上次计数后计数器被调整
     */
    private final Set<IeBinaryCounterReading.Flag> flags;

    public IEC104CounterValue(int ioa, int counterReading, int sequenceNumber, Set<IeBinaryCounterReading.Flag> flags) {
        this.ioa = ioa;
        this.counterReading = counterReading;
        this.sequenceNumber = sequenceNumber;
        this.flags = flags;
    }

    public int getIoa() {
        return ioa;
    }

    public int getCounterReading() {
        return counterReading;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public Set<IeBinaryCounterReading.Flag> getFlags() {
        return flags;
    }
}
