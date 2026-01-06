package com.iteaj.plugin.protocol.iec104.network;

import org.openmuc.j60870.ie.IeQuality;

/**
 * 遥测值
 */
public class IEC104TEValue {

    /**
     * 信息对象地址
     */
    private int ioa;

    /**
     * 标度值或者未归一化前的值
     */
    private Integer value;

    /**
     * 遥测值类型
     */
    private IEC104TEType teType;

    /**
     * 品质描述
     * @see IeQuality#isOverflow()  是否溢出
     * @see IeQuality#isBlocked() 是否被闭锁
     * @see IeQuality#isInvalid() 是否无效
     * @see IeQuality#isNotTopical() 是否非当前值
     * @see IeQuality#isSubstituted() 是否被取代
     */
    private IeQuality quality;

    /**
     * 短浮点数值
     */
    private Float shortFloatValue;

    /**
     * 归一化值
     */
    private Double normalizedValue;

    public IEC104TEValue(int ioa, int value, IeQuality quality) {
        this.ioa = ioa;
        this.value = value;
        this.quality = quality;
        this.teType = IEC104TEType.scaled;
    }

    public IEC104TEValue(int ioa, int value, double normalizedValue) {
        this.ioa = ioa;
        this.value = value;
        this.normalizedValue = normalizedValue;
        this.teType = IEC104TEType.normalized_nd;
    }

    public IEC104TEValue(int ioa, int value, double normalizedValue, IeQuality quality) {
        this.ioa = ioa;
        this.value = value;
        this.quality = quality;
        this.teType = IEC104TEType.normalized;
        this.normalizedValue = normalizedValue;
    }

    public IEC104TEValue(int ioa, float shortFloatValue, IeQuality quality) {
        this.ioa = ioa;
        this.quality = quality;
        this.teType = IEC104TEType.short_float;
        this.shortFloatValue = shortFloatValue;
    }

    public int getValue() {
        return value;
    }

    public float getShortFloatValue() {
        return shortFloatValue;
    }

    public double getNormalizedValue() {
        return normalizedValue;
    }

    public IEC104TEType getTeType() {
        return teType;
    }

    public boolean isType(IEC104TEType type) {
        return this.teType == type;
    }

    public IeQuality getQuality() {
        return quality;
    }

    public int getIoa() {
        return ioa;
    }
}
