package com.iteaj.plugin.protocol.iec104.network;

import org.openmuc.j60870.ie.*;

/**
 * 遥信值
 */
public class IEC104TIValue {

    /**
     * 信息对象地址
     */
    private int ioa;

    /**
     * 单点状态值
     */
    private boolean singlePointState;

    /**
     * 步位置状态值
     * @see IeValueWithTransientState#getValue() -64-63的值
     * @see IeValueWithTransientState#getTransientState() 是否处于顺便状态
     */
    private IeValueWithTransientState transientState;

    /**
     * 双点状态值
     * @see org.openmuc.j60870.ie.IeDoublePointWithQuality.DoublePointInformation#ON
     * @see org.openmuc.j60870.ie.IeDoublePointWithQuality.DoublePointInformation#OFF
     * @see org.openmuc.j60870.ie.IeDoublePointWithQuality.DoublePointInformation#INDETERMINATE
     * @see org.openmuc.j60870.ie.IeDoublePointWithQuality.DoublePointInformation#INDETERMINATE_OR_INTERMEDIATE
     */
    private IeDoublePointWithQuality.DoublePointInformation doublePointState;

    /**
     * 带状态改变的16位单点信息
     */
    private IeStatusAndStatusChanges statusAndStatusChanges;

    /**
     * 32位比特状态值
     * @see IeBinaryStateInformation#getBinaryState(int) 某一比特位的状态
     */
    private IeBinaryStateInformation binaryState;

    private IEC104TIType type;
    /**
     * 品质描述
     * @see IeQuality#isOverflow()  是否溢出
     * @see IeQuality#isBlocked() 是否被闭锁
     * @see IeQuality#isInvalid() 是否无效
     * @see IeQuality#isNotTopical() 是否非当前值
     * @see IeQuality#isSubstituted() 是否被取代
     */
    private IeQuality quality;

    public IEC104TIValue(int ioa, boolean singlePointState, IeQuality quality) {
        this.ioa = ioa;
        this.quality = quality;
        this.type = IEC104TIType.SinglePoint;
        this.singlePointState = singlePointState;
    }

    public IEC104TIValue(int ioa, IeValueWithTransientState transientState, IeQuality quality) {
        this.ioa = ioa;
        this.quality = quality;
        this.transientState = transientState;
        this.type = IEC104TIType.TransientState;
    }

    public IEC104TIValue(int ioa, IeDoublePointWithQuality.DoublePointInformation doublePointState, IeQuality quality) {
        this.ioa = ioa;
        this.quality = quality;
        this.type = IEC104TIType.DoublePoint;
        this.doublePointState = doublePointState;
    }

    public IEC104TIValue(int ioa, IeBinaryStateInformation binaryState, IeQuality quality) {
        this.ioa = ioa;
        this.quality = quality;
        this.binaryState = binaryState;
        this.type = IEC104TIType.BinaryState;
    }

    public IEC104TIValue(int ioa, IeStatusAndStatusChanges statusAndStatusChanges, IeQuality quality) {
        this.ioa = ioa;
        this.statusAndStatusChanges = statusAndStatusChanges;
        this.quality = quality;
    }

    public int getIoa() {
        return ioa;
    }

    public boolean isSinglePointState() {
        return singlePointState;
    }

    public IeValueWithTransientState getTransientState() {
        return transientState;
    }

    public IeDoublePointWithQuality.DoublePointInformation getDoublePointState() {
        return doublePointState;
    }

    public IeBinaryStateInformation getBinaryState() {
        return binaryState;
    }

    public IeStatusAndStatusChanges getStatusAndStatusChanges() {
        return statusAndStatusChanges;
    }

    public boolean isType(IEC104TIType type) {
        return this.type == type;
    }

    public IEC104TIType getType() {
        return type;
    }

    public IeQuality getQuality() {
        return quality;
    }
}
