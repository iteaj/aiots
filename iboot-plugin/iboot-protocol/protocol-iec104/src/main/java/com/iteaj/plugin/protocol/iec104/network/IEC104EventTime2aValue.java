package com.iteaj.plugin.protocol.iec104.network;

import org.openmuc.j60870.ie.*;

import java.util.Date;

/**
 * 事件值
 */
public class IEC104EventTime2aValue implements Time2aResolver{

    /**
     * 信息对象地址
     */
    private int ioa;

    /**
     * 事件值
     * @see IeSingleProtectionEvent
     * @see IeProtectionStartEvent
     * @see IeProtectionOutputCircuitInformation
     */
     private InformationElement event;

    /**
     * 时间标签
     * @see IeTime16
     * @see org.openmuc.j60870.ie.IeTime24
     */
    private InformationElement time2a;

    /**
     * 时间标签
     * @see org.openmuc.j60870.ie.IeTime24
     * @see org.openmuc.j60870.ie.IeTime56
     */
    private InformationElement time2a_1;

    /**
     * 设备事件的质量描述
     * @see IeProtectionQuality#isBlocked() 是否被闭锁
     * @see IeProtectionQuality#isInvalid() 是否无效
     * @see IeProtectionQuality#isNotTopical() 是否非当前值
     * @see IeProtectionQuality#isSubstituted() 是否被取代
     * @see IeProtectionQuality#isElapsedTimeInvalid() 动作事件是否无效
     */
    private IeProtectionQuality quality;

    /**
     * 事件类型
     */
    private IEC104EventType type;

    public IEC104EventTime2aValue(int ioa, IeSingleProtectionEvent event, InformationElement time2a, InformationElement time2a_1) {
        this(ioa, event, time2a, time2a_1, null, IEC104EventType.SingleProtection);
    }

    public IEC104EventTime2aValue(int ioa, IeProtectionStartEvent event, InformationElement time2a, InformationElement time2a_1, IeProtectionQuality quality) {
        this(ioa, event, time2a, time2a_1, quality, IEC104EventType.ProtectionStartEvent);
    }

    public IEC104EventTime2aValue(int ioa, IeProtectionOutputCircuitInformation event, InformationElement time2a, InformationElement time2a_1, IeProtectionQuality quality) {
        this(ioa, event, time2a, time2a_1, quality, IEC104EventType.ProtectionOutputCircuit);
    }

    protected IEC104EventTime2aValue(int ioa, InformationElement event, InformationElement time2a, InformationElement time2a_1, IeProtectionQuality quality, IEC104EventType type) {
        this.ioa = ioa;
        this.type = type;
        this.event = event;
        this.time2a = time2a;
        this.quality = quality;
        this.time2a_1 = time2a_1;
    }

    public int getIoa() {
        return ioa;
    }

    public InformationElement getEvent() {
        return event;
    }

    public InformationElement getTime2a() {
        return time2a;
    }

    public IeProtectionQuality getQuality() {
        return quality;
    }

    public InformationElement getTime2a_1() {
        return time2a_1;
    }

    public IEC104EventType getType() {
        return type;
    }

    public Date getDate() {
        return resolverDate(getTime2a(), getTime2a_1());
    }

    /**
     * @see IeSingleProtectionEvent.EventState#ON 开
     * @see IeSingleProtectionEvent.EventState#OFF 合
     * @see IeSingleProtectionEvent.EventState#INDETERMINATE 不确定或中间状态
     * @return
     */
    public IeSingleProtectionEvent getSingleProtectionEvent() {
        return (IeSingleProtectionEvent) this.event;
    }

    /**
     * @see IeProtectionStartEvent#isGeneralStart() 是否总启动
     * @see IeProtectionStartEvent#isStartOperationIe() 是否接地电流保护启动
     * @see IeProtectionStartEvent#isStartOperationL1() 是否A相启动保护
     * @see IeProtectionStartEvent#isStartOperationL2()  是否B相启动保护
     * @see IeProtectionStartEvent#isStartOperationL3()  是否C相启动保护
     * @see IeProtectionStartEvent#isStartReverseOperation() 是否反向保护启动
     * @return
     */
    public IeProtectionStartEvent getProtectionStartEvent() {
        return (IeProtectionStartEvent) this.event;
    }

    /**
     * @see IeProtectionOutputCircuitInformation#isCommandToL1() 是否有命令输出至A相输出电路
     * @see IeProtectionOutputCircuitInformation#isCommandToL2() 是否有命令输出至B相输出电路
     * @see IeProtectionOutputCircuitInformation#isCommandToL3() 是否有命令输出至C相输出电路
     * @see IeProtectionOutputCircuitInformation#isGeneralCommand() 是否有总命令输出至输出电路
     * @return
     */
    public IeProtectionOutputCircuitInformation getProtectionOutputCircuit() {
        return (IeProtectionOutputCircuitInformation) this.event;
    }
}
