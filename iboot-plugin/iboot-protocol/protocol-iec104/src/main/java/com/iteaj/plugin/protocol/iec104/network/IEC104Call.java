package com.iteaj.plugin.protocol.iec104.network;

import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;

import java.util.List;

/**
 * IEC104遥信，遥测，遥调、遥控的回调
 * @see CauseOfTransmission#PERIODIC 周期性上报(用于一些比较重要的点位 上报时间周期较短)
 * @see CauseOfTransmission#BACKGROUND_SCAN 背景扫描上报(用于一些比较次要的点位 上报周期时间长)
 * @see CauseOfTransmission#SPONTANEOUS 突发性上报(点位值改变时上报)
 *
 * @see CauseOfTransmission#REQUEST 主站请求从站(用于读取遥测, 遥信等点位值)
 * @see CauseOfTransmission#ACTIVATION 激活操作(用于遥控, 遥调, 参数设定等)
 * @see CauseOfTransmission#ACTIVATION_CON 激活确认(从站响应主站激活命令的确认)
 * @see CauseOfTransmission#ACTIVATION_TERMINATION 明确标识一条控制命令（如遥控、设点）已成功执行完成
 *
 * @see org.openmuc.j60870.ie.IeSingleCommand#isSelect()  是否选择执行模式 true为选择执行, 主站需要再次确认, false子站直接执行
 * @see org.openmuc.j60870.ie.IeSingleCommand#getQualifier() 0表示选择模式 1表示直接执行
 */
public interface IEC104Call {

    /**
     * 连接成功后的总召唤站地址
     * 65535 默认所有占(广播)
     * -1 不发送总召
     * @return
     */
    default int getInterrogationCoa() {
        return 65535;
    }

    /**
     * 不带时标的遥测值
     * @param values
     * @param cot 发送原因
     * @param type 报文类型
     * @param coa 从站地址
     */
    void teCallValue(List<IEC104TEValue> values, CauseOfTransmission cot, ASduType type, int coa);

    /**
     * 带时标的遥测值
     * @param values
     * @param cot 发送原因
     * @param type 报文类型
     * @param coa 从站地址
     */
    void teCallTime2aValue(List<IEC104TETime2aValue> values, CauseOfTransmission cot, ASduType type, int coa);

    /**
     * 不带时标的遥信值
     * @param values
     * @param cot 发送原因
     * @param type 报文类型
     * @param coa 从站地址
     */
    void tiCallValue(List<IEC104TIValue> values, CauseOfTransmission cot, ASduType type, int coa);

    /**
     * 带时标的遥信值
     * @param values
     * @param cot 发送原因
     * @param type 报文类型
     * @param coa 从站地址
     */
    void tiCallTime2aValue(List<IEC104TITime2aValue> values, CauseOfTransmission cot, ASduType type, int coa);

    /**
     * 带时标的事件
     * @param values
     * @param cot 发送原因
     * @param type 报文类型
     * @param coa 从站地址
     */
    void eventCallTime2a(List<IEC104EventTime2aValue> values, CauseOfTransmission cot, ASduType type, int coa);

    /**
     * 不带时间标签的累计值回调
     * @param values
     * @param cot
     * @param type
     * @param coa
     */
    void counterCall(List<IEC104CounterValue> values, CauseOfTransmission cot, ASduType type, int coa);

    /**
     * 带时间标签的累计值回调
     * @param values
     * @param cot
     * @param type
     * @param coa
     */
    void counterCallTime2a(List<IEC104CounterTime2aValue> values, CauseOfTransmission cot, ASduType type, int coa);

    /**
     * 控制指令回调
     * @param aSdu
     * @param ti
     * @param cot
     * @param coa
     */
    void ctrlCall(ASdu aSdu, ASduType ti, CauseOfTransmission cot, int coa);
}
