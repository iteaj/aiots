package com.iteaj.plugin.protocol.iec104.network;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import org.openmuc.j60870.ie.IeQuality;
import org.openmuc.j60870.ie.IeTime24;
import org.openmuc.j60870.ie.IeTime56;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 带时间标签的遥测值(CP16Time2a, CP24Time2a, CP56Time2a)
 * @see org.openmuc.j60870.ie.IeTime16
 * @see org.openmuc.j60870.ie.IeTime24
 * @see org.openmuc.j60870.ie.IeTime56
 */
public class IEC104TETime2aValue implements Time2aResolver{
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

    private IeTime24 time24;

    private IeTime56 time56;

    public IEC104TETime2aValue(int ioa, int value, IeQuality quality) {
        this.ioa = ioa;
        this.value = value;
        this.quality = quality;
        this.teType = IEC104TEType.scaled;
    }

    public IEC104TETime2aValue(int ioa, int value, double normalizedValue, IeQuality quality) {
        this.ioa = ioa;
        this.value = value;
        this.quality = quality;
        this.teType = IEC104TEType.normalized;
        this.normalizedValue = normalizedValue;
    }

    public IEC104TETime2aValue(int ioa, float shortFloatValue, IeQuality quality) {
        this.ioa = ioa;
        this.quality = quality;
        this.teType = IEC104TEType.short_float;
        this.shortFloatValue = shortFloatValue;
    }

    public IEC104TETime2aValue build(IeTime24 time24) {
        this.time24 = time24; return this;
    }

    public IEC104TETime2aValue build(IeTime56 time56) {
        this.time56 = time56; return this;
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

    /**
     * 到分钟的时间精度
     * @return
     */
    public IeTime24 getTime24() {
        return time24;
    }

    /**
     * 到毫秒的时间精度
     * @return
     */
    public IeTime56 getTime56() {
        return time56;
    }

    public long getTimestamp() {
        if(getTime24() != null) {
            // 到分钟的毫秒数
            return resolverTimestamp(getTime24());
        } else if(getTime56() != null){
            return resolverTimestamp(getTime56());
        } else {
            return 0l;
        }
    }

    public Date getDate() {
        if(getTime24() != null) {
            return resolverDate(getTime24());
        } else if(getTime56() != null) {
            return resolverDate(getTime56());
        } else {
            return null;
        }
    }
}
