package com.iteaj.plugin.protocol.iec104.network;

import com.iteaj.iot.IotThreadManager;
import com.iteaj.iot.ProtocolException;
import com.iteaj.iot.client.ClientComponent;
import com.iteaj.iot.client.IotClient;
import org.openmuc.j60870.*;
import org.openmuc.j60870.ie.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class IEC104Client implements IotClient {

    private Boolean close = null;
    private Connection connection;
    private final String CV = "C";
    private final String PV = "P";
    private final String MV = "M";
    private IEC104Call iec104Call;
    private final IEC104Component component;
    private final IEC104ConnectProperties connectProperties;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public IEC104Client(IEC104Component component, IEC104ConnectProperties connectProperties) {
        this.component = component;
        this.connectProperties = connectProperties;
    }

    @Override
    public IEC104ConnectProperties getConfig() {
        return this.connectProperties;
    }

    /**
     * @see IEC104Call
     * @param config
     */
    @Override
    public void init(Object config) {
        this.iec104Call = (IEC104Call) config;
        getConfig().getConnectionBuilder().setConnectionEventListener(new ConnectionEventListener() {
            @Override
            public void newASdu(Connection connection, ASdu aSdu) {
                final int coa = aSdu.getCommonAddress();
                final CauseOfTransmission cot = aSdu.getCauseOfTransmission();
                final ASduType ti = aSdu.getTypeIdentification();
                final String[] types = ti.name().split("_");

                if(types[0].equals(CV)) { // 遥调, 总召唤, 读, 遥控等主站主动请求的指令
                    if(ti==ASduType.C_IC_NA_1) { // 总召唤的响应
                        if(cot != CauseOfTransmission.ACTIVATION_CON && cot != CauseOfTransmission.ACTIVATION_TERMINATION) {
                            System.out.println(cot);
                        }
                    } else {
                        logger.info("其他遥调或遥控指令({}) 站地址: {} - 传输原因: {}", ti.name(), coa, cot.name());
                        IEC104Client.this.iec104Call.ctrlCall(aSdu, ti, cot, coa);
                    }
                } else if(types[0].equals(MV)) { // 测量和遥信值
                    final InformationObject[] informationObjects = aSdu.getInformationObjects();
                    if(types[1].equals("ME")) { // 遥测值
                        if(types[2].startsWith("T")) { // 带时标
                            handleTETime2aCall(ti, cot, informationObjects, coa);
                        } else { // 不带时标
                            handleTECall(ti, cot, informationObjects, coa);
                        }
                    } else if(types[1].equals("SP") || types[1].equals("DP") || types[1].equals("ST")
                            || types[1].equals("BO") || types[1].equals("PS")){ // 遥信值
                        if(types[2].startsWith("T")) { // 带时标
                            handleTITime2aCall(ti, cot, informationObjects, coa);
                        } else { // 不带时标
                            handleTICall(ti, cot, informationObjects, coa);
                        }
                    } else if(types[1].equals("IT")) { // 累积量值
                        handleCounterCall(ti, cot, informationObjects, coa);
                    } else if(types[1].equals("EP")){ // 事件值
                        handleEventTime2aCall(ti, cot, informationObjects, coa);
                    } else {
                        logger.info("其他遥信/测({}) 站地址: {} - 传输原因: {}", ti.name(), coa, cot.name());
                    }

                } else if(types[0].equals(PV)) { // 参数值(测量值参数, 参数激活)

                } else {
                    logger.info("其他类型({}) 站地址: {} - 传输原因: {}", ti.name(), coa, cot.name());
                }
            }

            @Override
            public void connectionClosed(Connection connection, IOException cause) {
                if(IEC104Client.this.close == null) {
                    logger.error("连接异常关闭({}) 将在{}秒后重连 - 客户端: {}", getClientComponent().getName()
                            , getConfig().getReconnect(), getConfig().connectKey(), cause);

                    reconnect();
                } else if(IEC104Client.this.close == Boolean.TRUE) { // 正常关闭移除客户端
                    getClientComponent().removeClient(getConfig());
                }
            }

            @Override
            public void dataTransferStateChanged(Connection connection, boolean stopped) {
                if(!stopped) {
                    try {
                        final int coa = IEC104Client.this.iec104Call.getInterrogationCoa();
                        if(coa > -1) {
                            // 65535作为广播地址, 会总召所有的站点
                            connection.interrogation(coa, CauseOfTransmission.ACTIVATION, new IeQualifierOfInterrogation(20));
                        }
                    } catch (IOException e) {
                        logger.error("发送总召唤失败", e);
                    }
                }
            }
        });
    }

    private void handleCounterCall(ASduType ti, CauseOfTransmission cot, InformationObject[] informationObjects, int coa) {
        if(ti == ASduType.M_IT_NA_1) {
            List<IEC104CounterValue> values = new ArrayList<>();
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeBinaryCounterReading value = (IeBinaryCounterReading)element[0];
                    final IEC104CounterValue tiValue = new IEC104CounterValue(object.getInformationObjectAddress()
                            , value.getCounterReading(), value.getSequenceNumber(), value.getFlags());
                    values.add(tiValue);
                }
            }
            this.iec104Call.counterCall(values, cot, ti, coa);
        } else {
            List<IEC104CounterTime2aValue> values = new ArrayList<>();
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeBinaryCounterReading value = (IeBinaryCounterReading)element[0];
                    final InformationElement time2a = element[1];
                    final IEC104CounterTime2aValue tiValue = new IEC104CounterTime2aValue(object.getInformationObjectAddress()
                            , value.getCounterReading(), value.getSequenceNumber(), value.getFlags(), time2a);
                    values.add(tiValue);
                }
            }

            this.iec104Call.counterCallTime2a(values, cot, ti, coa);
        }
    }

    private void handleEventTime2aCall(ASduType ti, CauseOfTransmission cot, InformationObject[] informationObjects, int coa) {
        List<IEC104EventTime2aValue> values = new ArrayList<>();
        if(ti == ASduType.M_EP_TA_1 || ti == ASduType.M_EP_TD_1) {
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeSingleProtectionEvent value = (IeSingleProtectionEvent)element[0];
                    final InformationElement time16 = element[1];
                    final InformationElement time24 = element[2];
                    final IEC104EventTime2aValue tiValue = new IEC104EventTime2aValue(object.getInformationObjectAddress(), value, time16, time24);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_EP_TB_1 || ti == ASduType.M_EP_TE_1) {
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeProtectionStartEvent value = (IeProtectionStartEvent)element[0];
                    final IeProtectionQuality quality = (IeProtectionQuality) element[1];
                    final InformationElement time16 = element[2];
                    final InformationElement time24 = element[3];
                    final IEC104EventTime2aValue tiValue = new IEC104EventTime2aValue(object.getInformationObjectAddress(), value, time16, time24, quality);
                    values.add(tiValue);
                }
            }
        } else {
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeProtectionOutputCircuitInformation value = (IeProtectionOutputCircuitInformation)element[0];
                    final IeProtectionQuality quality = (IeProtectionQuality) element[1];
                    final InformationElement time16 = element[2];
                    final InformationElement time24 = element[3];
                    final IEC104EventTime2aValue tiValue = new IEC104EventTime2aValue(object.getInformationObjectAddress(), value, time16, time24, quality);
                    values.add(tiValue);
                }
            }
        }

        this.iec104Call.eventCallTime2a(values, cot, ti, coa);
    }

    private void handleTICall(ASduType ti, CauseOfTransmission cot, InformationObject[] informationObjects, int coa) {
        List<IEC104TIValue> values = new ArrayList<>();
        if(ti == ASduType.M_SP_NA_1) { // 单点
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeSinglePointWithQuality value = (IeSinglePointWithQuality)element[0];
                    final IeQuality quality = new IeQuality(false, value.isBlocked()
                            , value.isSubstituted(), value.isNotTopical(), value.isInvalid());
                    final IEC104TIValue tiValue = new IEC104TIValue(object.getInformationObjectAddress()
                            , value.isOn(), quality);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_DP_NA_1) { // 双点
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeDoublePointWithQuality value = (IeDoublePointWithQuality)element[0];
                    final IeQuality quality = new IeQuality(false, value.isBlocked()
                            , value.isSubstituted(), value.isNotTopical(), value.isInvalid());
                    final IEC104TIValue tiValue = new IEC104TIValue(object.getInformationObjectAddress()
                            , value.getDoublePointInformation(), quality);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_ST_NA_1) { // 步位置
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeValueWithTransientState value = (IeValueWithTransientState)element[0];
                    final IeQuality quality = (IeQuality) element[1];
                    final IEC104TIValue tiValue = new IEC104TIValue(object.getInformationObjectAddress(), value, quality);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_BO_NA_1) { // 32比特位
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeBinaryStateInformation value = (IeBinaryStateInformation)element[0];
                    final IeQuality quality = (IeQuality) element[1];
                    final IEC104TIValue tiValue = new IEC104TIValue(object.getInformationObjectAddress(), value, quality);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_PS_NA_1) {
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeStatusAndStatusChanges value = (IeStatusAndStatusChanges)element[0];
                    final IeQuality quality = (IeQuality) element[1];
                    final IEC104TIValue tiValue = new IEC104TIValue(object.getInformationObjectAddress(), value, quality);
                    values.add(tiValue);
                }
            }
        }

        this.iec104Call.tiCallValue(values, cot, ti, coa);
    }

    private void handleTITime2aCall(ASduType ti, CauseOfTransmission cot, InformationObject[] informationObjects, int coa) {
        List<IEC104TITime2aValue> values = new ArrayList<>();
        if(ti == ASduType.M_SP_TA_1) {
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeSinglePointWithQuality value = (IeSinglePointWithQuality) element[0];
                    final IeTime24 time24 = (IeTime24) element[1];
                    final IeQuality quality = new IeQuality(false, value.isBlocked()
                            , value.isSubstituted(), value.isNotTopical(), value.isInvalid());
                    final IEC104TITime2aValue tiValue = new IEC104TITime2aValue(object.getInformationObjectAddress()
                            , value.isOn(), quality).build(time24);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_DP_TA_1) {
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeDoublePointWithQuality value = (IeDoublePointWithQuality)element[0];
                    final IeTime24 time24 = (IeTime24) element[1];
                    final IeQuality quality = new IeQuality(false, value.isBlocked()
                            , value.isSubstituted(), value.isNotTopical(), value.isInvalid());
                    final IEC104TITime2aValue tiValue = new IEC104TITime2aValue(object.getInformationObjectAddress()
                            , value.getDoublePointInformation(), quality).build(time24);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_ST_TA_1) {
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeValueWithTransientState value = (IeValueWithTransientState)element[0];
                    final IeQuality quality = (IeQuality) element[1];
                    final IeTime24 time24 = (IeTime24) element[2];
                    final IEC104TITime2aValue tiValue = new IEC104TITime2aValue(object.getInformationObjectAddress()
                            , value, quality).build(time24);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_BO_TA_1) {
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeBinaryStateInformation value = (IeBinaryStateInformation)element[0];
                    final IeQuality quality = (IeQuality) element[1];
                    final IeTime24 time24 = (IeTime24) element[2];
                    final IEC104TITime2aValue tiValue = new IEC104TITime2aValue(object.getInformationObjectAddress(), value, quality).build(time24);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_SP_TB_1) {
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeSinglePointWithQuality value = (IeSinglePointWithQuality) element[0];
                    final IeTime56 time24 = (IeTime56) element[1];
                    final IeQuality quality = new IeQuality(false, value.isBlocked()
                            , value.isSubstituted(), value.isNotTopical(), value.isInvalid());
                    final IEC104TITime2aValue tiValue = new IEC104TITime2aValue(object.getInformationObjectAddress()
                            , value.isOn(), quality).build(time24);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_DP_TB_1) {
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeDoublePointWithQuality value = (IeDoublePointWithQuality)element[0];
                    final IeTime56 time24 = (IeTime56) element[1];
                    final IeQuality quality = new IeQuality(false, value.isBlocked()
                            , value.isSubstituted(), value.isNotTopical(), value.isInvalid());
                    final IEC104TITime2aValue tiValue = new IEC104TITime2aValue(object.getInformationObjectAddress()
                            , value.getDoublePointInformation(), quality).build(time24);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_ST_TB_1) {
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeValueWithTransientState value = (IeValueWithTransientState)element[0];
                    final IeQuality quality = (IeQuality) element[1];
                    final IeTime56 time24 = (IeTime56) element[2];
                    final IEC104TITime2aValue tiValue = new IEC104TITime2aValue(object.getInformationObjectAddress()
                            , value, quality).build(time24);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_BO_TB_1) {
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeBinaryStateInformation value = (IeBinaryStateInformation)element[0];
                    final IeQuality quality = (IeQuality) element[1];
                    final IeTime56 time24 = (IeTime56) element[2];
                    final IEC104TITime2aValue tiValue = new IEC104TITime2aValue(object.getInformationObjectAddress(), value, quality).build(time24);
                    values.add(tiValue);
                }
            }
        }

        this.iec104Call.tiCallTime2aValue(values, cot, ti, coa);
    }

    private void handleTETime2aCall(ASduType ti, CauseOfTransmission cot, InformationObject[] informationObjects, int coa) {
        List<IEC104TETime2aValue> values = new ArrayList<>();
        if(ti == ASduType.M_ME_TA_1) { // 归一化值
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeNormalizedValue value = (IeNormalizedValue)element[0];
                    final IeQuality quality = (IeQuality)element[1];
                    final IeTime24 time24 = (IeTime24)element[2];
                    final IEC104TETime2aValue tiValue = new IEC104TETime2aValue(object.getInformationObjectAddress()
                            , value.getUnnormalizedValue(), value.getNormalizedValue(), quality).build(time24);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_ME_TB_1) { // 标度值
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeScaledValue value = (IeScaledValue)element[0];
                    final IeQuality quality = (IeQuality)element[1];
                    final IeTime24 time24 = (IeTime24)element[2];
                    final IEC104TETime2aValue tiValue = new IEC104TETime2aValue(object.getInformationObjectAddress()
                            , value.getUnnormalizedValue(), quality).build(time24);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_ME_TC_1) { // 段浮点数值
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeShortFloat value = (IeShortFloat)element[0];
                    final IeQuality quality = (IeQuality)element[1];
                    final IeTime24 time24 = (IeTime24)element[2];
                    final IEC104TETime2aValue tiValue = new IEC104TETime2aValue(object.getInformationObjectAddress()
                            , value.getValue(), quality).build(time24);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_ME_TD_1) { // 带time56时标的归一化值
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeNormalizedValue value = (IeNormalizedValue)element[0];
                    final IeQuality quality = (IeQuality)element[1];
                    final IeTime56 time56 = (IeTime56)element[2];
                    final IEC104TETime2aValue tiValue = new IEC104TETime2aValue(object.getInformationObjectAddress()
                            , value.getUnnormalizedValue(), value.getNormalizedValue(), quality).build(time56);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_ME_TE_1) {// 带time56时标的标度值
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeScaledValue value = (IeScaledValue)element[0];
                    final IeQuality quality = (IeQuality)element[1];
                    final IeTime56 time56 = (IeTime56)element[2];
                    final IEC104TETime2aValue tiValue = new IEC104TETime2aValue(object.getInformationObjectAddress()
                            , value.getUnnormalizedValue(), quality).build(time56);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_ME_TF_1) { // 带time56短浮点数值
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeShortFloat value = (IeShortFloat)element[0];
                    final IeQuality quality = (IeQuality)element[1];
                    final IeTime56 time56 = (IeTime56)element[2];
                    final IEC104TETime2aValue tiValue = new IEC104TETime2aValue(object.getInformationObjectAddress()
                            , value.getValue(), quality).build(time56);
                    values.add(tiValue);
                }
            }
        }

        this.iec104Call.teCallTime2aValue(values, cot, ti, coa);
    }

    private void handleTECall(ASduType ti, CauseOfTransmission cot, InformationObject[] informationObjects, int coa) {
        List<IEC104TEValue> values = new ArrayList<>();
        if(ti == ASduType.M_ME_NA_1) { // 归一直
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeNormalizedValue value = (IeNormalizedValue)element[0];
                    final IeQuality quality = (IeQuality)element[1];
                    final IEC104TEValue tiValue = new IEC104TEValue(object.getInformationObjectAddress()
                            , value.getUnnormalizedValue(), value.getNormalizedValue(), quality);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_ME_NB_1) { // 标度值
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeScaledValue value = (IeScaledValue)element[0];
                    final IeQuality quality = (IeQuality)element[1];
                    final IEC104TEValue tiValue = new IEC104TEValue(object.getInformationObjectAddress()
                            , value.getUnnormalizedValue(), quality);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_ME_NC_1) { // 段浮点数值
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeShortFloat value = (IeShortFloat)element[0];
                    final IeQuality quality = (IeQuality)element[1];
                    final IEC104TEValue tiValue = new IEC104TEValue(object.getInformationObjectAddress()
                            , value.getValue(), quality);
                    values.add(tiValue);
                }
            }
        } else if(ti == ASduType.M_ME_ND_1) { // 不带品质描述的归一化值
            for (int i = 0; i < informationObjects.length; i++) {
                final InformationObject object = informationObjects[i];
                final InformationElement[][] elements = object.getInformationElements();
                for (int i1 = 0; i1 < elements.length; i1++) {
                    final InformationElement[] element = elements[i1];
                    final IeNormalizedValue value = (IeNormalizedValue)element[0];
                    final IEC104TEValue tiValue = new IEC104TEValue(object.getInformationObjectAddress()
                            , value.getUnnormalizedValue(), value.getNormalizedValue());
                    values.add(tiValue);
                }
            }
        }

        this.iec104Call.teCallValue(values, cot, ti, coa);
    }

    /**
     * 发送单点命令 (C_SC_NA_1, TI: 45).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param informationObjectAddress 点位地址
     * @param singleCommand 单点命令
     * @throws IOException
     */
    public void singleCommand(int commonAddress, CauseOfTransmission cot, int informationObjectAddress,
                              IeSingleCommand singleCommand) throws IOException {
        connection.singleCommand(commonAddress, cot, informationObjectAddress, singleCommand);
    }

    /**
     * 发送带时间标签的单点命令 CP56Time2a (C_SC_NA_1, TI: 45).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param informationObjectAddress 点位地址
     * @param singleCommand 单点命令
     * @param timeTag 时间标签
     * @throws IOException
     */
    public void singleCommandWithTimeTag(int commonAddress, CauseOfTransmission cot, int informationObjectAddress,
                                         IeSingleCommand singleCommand, IeTime56 timeTag) throws IOException {

        connection.singleCommandWithTimeTag(commonAddress, cot, informationObjectAddress, singleCommand, timeTag);
    }

    /**
     * 发送双点命令 (C_DC_NA_1, TI: 46).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param informationObjectAddress 点位地址
     * @param doubleCommand 双点命令
     * @throws IOException
     */
    public void doubleCommand(int commonAddress, CauseOfTransmission cot, int informationObjectAddress,
                              IeDoubleCommand doubleCommand) throws IOException {
        connection.doubleCommand(commonAddress, cot, informationObjectAddress, doubleCommand);
    }

    /**
     * 发送带时间标签的双点命令 CP56Time2a (C_DC_NA_1, TI: 46).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param informationObjectAddress 点位地址
     * @param doubleCommand 双点命令
     * @param timeTag
     * @throws IOException
     */
    public void doubleCommandWithTimeTag(int commonAddress, CauseOfTransmission cot, int informationObjectAddress,
                                         IeDoubleCommand doubleCommand, IeTime56 timeTag) throws IOException {

        connection.doubleCommandWithTimeTag(commonAddress, cot, informationObjectAddress, doubleCommand, timeTag);
    }

    /**
     * 发送步调节命令 (C_RC_NA_1, TI: 47).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param informationObjectAddress 点位地址
     * @param regulatingStepCommand 步进命令
     * @throws IOException
     */
    public void regulatingStepCommand(int commonAddress, CauseOfTransmission cot, int informationObjectAddress,
                                      IeRegulatingStepCommand regulatingStepCommand) throws IOException {

        connection.regulatingStepCommand(commonAddress, cot, informationObjectAddress, regulatingStepCommand);
    }

    /**
     *
     * 发送带时间标签的步调节命令 CP56Time2a (C_RC_TA_1, TI: 60).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param informationObjectAddress 点位地址
     * @param regulatingStepCommand 步调节命令
     * @param timeTag 时间标签
     * @throws IOException
     */
    public void regulatingStepCommandWithTimeTag(int commonAddress, CauseOfTransmission cot, int informationObjectAddress
            , IeRegulatingStepCommand regulatingStepCommand, IeTime56 timeTag) throws IOException {

        connection.regulatingStepCommandWithTimeTag(commonAddress, cot, informationObjectAddress, regulatingStepCommand, timeTag);
    }

    /**
     *
     * 发送归一化值调节命令 (C_SE_NA_1, TI: 48).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param informationObjectAddress 点位地址
     * @param normalizedValue 归一化值
     * @param qualifier 质量
     * @throws IOException
     */
    public void setNormalizedValueCommand(int commonAddress, CauseOfTransmission cot, int informationObjectAddress,
                                          IeNormalizedValue normalizedValue, IeQualifierOfSetPointCommand qualifier) throws IOException {
        connection.setNormalizedValueCommand(commonAddress, cot, informationObjectAddress, normalizedValue, qualifier);
    }

    /**
     * 发送带CP56Time2a的归一化值调节命令 (C_SE_NA_1, TI: 61).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param informationObjectAddress 点位地址
     * @param normalizedValue 归一化值
     * @param qualifier 质量
     * @param timeTag
     * @throws IOException
     */
    public void setNormalizedValueCommandWithTimeTag(int commonAddress, CauseOfTransmission cot, int informationObjectAddress
            , IeNormalizedValue normalizedValue, IeQualifierOfSetPointCommand qualifier, IeTime56 timeTag) throws IOException {

        connection.setNormalizedValueCommandWithTimeTag(commonAddress, cot, informationObjectAddress, normalizedValue, qualifier, timeTag);
    }

    /**
     *
     * 发送标度值调节命令 (C_SE_NA_1, TI: 49).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param informationObjectAddress 点位地址
     * @param scaledValue 标度值
     * @param qualifier 质量
     */
    public void setScaledValueCommand(int commonAddress, CauseOfTransmission cot, int informationObjectAddress,
                                      IeScaledValue scaledValue, IeQualifierOfSetPointCommand qualifier) throws IOException {

        connection.setScaledValueCommand(commonAddress, cot, informationObjectAddress, scaledValue, qualifier);
    }

    /**
     * 发送带CP56Time2a的标度值调节命令 (C_SE_NA_1, TI: 62).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param informationObjectAddress 点位地址
     * @param scaledValue 标度值
     * @param qualifier 质量
     * @param timeTag
     * @throws IOException
     */
    public void setScaledValueCommandWithTimeTag(int commonAddress, CauseOfTransmission cot, int informationObjectAddress
            , IeScaledValue scaledValue, IeQualifierOfSetPointCommand qualifier, IeTime56 timeTag) throws IOException {

        connection.setScaledValueCommandWithTimeTag(commonAddress, cot, informationObjectAddress, scaledValue, qualifier, timeTag);
    }

    /**
     *
     * 发送短浮点值调节命令 (C_SE_NC_1, TI: 50).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param informationObjectAddress 点位地址
     * @param floatVal 短浮点值
     * @param qualifier 质量
     */
    public void setShortFloatCommand(int commonAddress, CauseOfTransmission cot, int informationObjectAddress,
                                     IeShortFloat floatVal, IeQualifierOfSetPointCommand qualifier) throws IOException {

        connection.setShortFloatCommand(commonAddress, cot, informationObjectAddress, floatVal, qualifier);
    }

    /**
     *
     * 发送带CP56Time2a的短浮点值调节命令 (C_SE_TC_1, TI: 63).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param informationObjectAddress 点位地址
     * @param shortFloat 短浮点值
     * @param qualifier 质量
     * @param timeTag
     * @throws IOException
     */
    public void setShortFloatCommandWithTimeTag(int commonAddress, CauseOfTransmission cot, int informationObjectAddress
            , IeShortFloat shortFloat, IeQualifierOfSetPointCommand qualifier, IeTime56 timeTag) throws IOException {

        connection.setShortFloatCommandWithTimeTag(commonAddress, cot, informationObjectAddress, shortFloat, qualifier, timeTag);
    }

    /**
     *
     * 发送32位比特串值调节命令 (C_BO_NA_1, TI: 51).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param informationObjectAddress 点位地址
     * @param binaryStateInformation 32位比特值
     */
    public void bitStringCommand(int commonAddress, CauseOfTransmission cot, int informationObjectAddress,
                                 IeBinaryStateInformation binaryStateInformation) throws IOException {

        connection.bitStringCommand(commonAddress, cot, informationObjectAddress, binaryStateInformation);
    }

    /**
     *
     * 发送带CP56Time2a的32位比特串值调节命令 (C_BO_TA_1, TI: 64).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param informationObjectAddress 点位地址
     * @param binaryStateInformation 32位比特值
     * @param timeTag
     * @throws IOException
     */
    public void bitStringCommandWithTimeTag(int commonAddress, CauseOfTransmission cot, int informationObjectAddress,
                                            IeBinaryStateInformation binaryStateInformation, IeTime56 timeTag) throws IOException {

        connection.bitStringCommandWithTimeTag(commonAddress, cot, informationObjectAddress, binaryStateInformation, timeTag);
    }

    /**
     * 发送总召唤命令 (C_IC_NA_1, TI: 100).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param qualifier 取值范围(20 - 36)
     * @throws IOException
     */
    public void interrogation(int commonAddress, CauseOfTransmission cot, IeQualifierOfInterrogation qualifier)
            throws IOException {
        connection.interrogation(commonAddress, cot, qualifier);
    }

    /**
     * 发送计算量召唤命令 (C_CI_NA_1, TI: 101).
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param qualifier 发送质量
     */
    public void counterInterrogation(int commonAddress, CauseOfTransmission cot,
                                     IeQualifierOfCounterInterrogation qualifier) throws IOException {
        connection.counterInterrogation(commonAddress, cot, qualifier);
    }

    /**
     * 发送读命令 (C_RD_NA_1, TI: 102).
     * 包括读取遥测、遥信、参数等信息
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param informationObjectAddress 点位地址
     * @throws IOException
     */
    public void readCommand(int commonAddress, int informationObjectAddress) throws IOException {
        connection.readCommand(commonAddress, informationObjectAddress);
    }

    /**
     * 发送始终同步命令 (C_CS_NA_1, TI: 103).
     *
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param time 时间标签
     * @throws IOException
     */
    public void synchronizeClocks(int commonAddress, IeTime56 time) throws IOException {
        connection.synchronizeClocks(commonAddress, time);
    }

    /**
     * 发送测试命令 (C_TS_NA_1, TI: 104).
     * 用于验证通信链路的连通性或检测远方设备是否在线
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @throws IOException
     */
    public void testCommand(int commonAddress) throws IOException {
        connection.testCommand(commonAddress);
    }

    /**
     * 发送复位进程命令 (C_RP_NA_1, TI: 105).
     *
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param qualifier
     *                  0x01	General Reset	常规进程重置（重启应用层进程，如保护装置逻辑）。
     *                  0x02	Reset Communication	重置通信栈（如链路层、传输层状态，但不重启设备）。
     *                  0x80 - 0xFF	厂商自定义	设备厂商可扩展定义（需查阅具体设备手册）。
     * @throws IOException
     */
    public void resetProcessCommand(int commonAddress, IeQualifierOfResetProcessCommand qualifier) throws IOException {
        connection.resetProcessCommand(commonAddress, qualifier);
    }

    /**
     * Sends a delay acquisition command (C_CD_NA_1, TI: 106).
     *
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#SPONTANEOUS}
     * @param time
     * @throws IOException
     */
    public void delayAcquisitionCommand(int commonAddress, CauseOfTransmission cot, IeTime16 time) throws IOException {
        connection.delayAcquisitionCommand(commonAddress, cot, time);
    }

    /**
     * 发送带CP56Time2a的测试命令 (C_TS_TA_1, TI: 107).
     *
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param testSequenceCounter 测试序号
     * @param time
     * @throws IOException
     */
    public void testCommandWithTimeTag(int commonAddress, IeTestSequenceCounter testSequenceCounter, IeTime56 time)
            throws IOException {
        connection.testCommandWithTimeTag(commonAddress, testSequenceCounter, time);
    }

    /**
     * 发送一个调节测量值参数 归一化值命令 (P_ME_NA_1, TI: 110).
     *
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param informationObjectAddress 点位地址
     * @param normalizedValue 归一化值
     * @param qualifier
     * @throws IOException
     */
    public void parameterNormalizedValueCommand(int commonAddress, int informationObjectAddress, IeNormalizedValue normalizedValue
            , IeQualifierOfParameterOfMeasuredValues qualifier) throws IOException {
        connection.parameterNormalizedValueCommand(commonAddress, informationObjectAddress, normalizedValue, qualifier);
    }

    /**
     * 发送一个调节测量值参数 标度值命令 (P_ME_NB_1, TI: 111).
     *
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param informationObjectAddress 点位地址
     * @param scaledValue 标度值
     * @param qualifier
     * @throws IOException
     */
    public void parameterScaledValueCommand(int commonAddress, int informationObjectAddress, IeScaledValue scaledValue,
                                            IeQualifierOfParameterOfMeasuredValues qualifier) throws IOException {
        connection.parameterScaledValueCommand(commonAddress, informationObjectAddress, scaledValue, qualifier);
    }

    /**
     * 发送一个调节测量值参数 短浮点值命令 (P_ME_NC_1, TI: 112).
     *
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param informationObjectAddress 点位地址
     * @param shortFloat 短浮点值
     * @param qualifier
     * @throws IOException
     */
    public void parameterShortFloatCommand(int commonAddress, int informationObjectAddress, IeShortFloat shortFloat,
                                           IeQualifierOfParameterOfMeasuredValues qualifier) throws IOException {
        connection.parameterShortFloatCommand(commonAddress, informationObjectAddress, shortFloat, qualifier);
    }

    /**
     * 发送参数激活指令 (P_AC_NA_1, TI: 113).
     *
     * @param commonAddress 站地址(1...255 or 1...65535)
     * @param cot 发送原因 支持 {@link CauseOfTransmission#ACTIVATION} or {@link CauseOfTransmission#DEACTIVATION}
     * @param informationObjectAddress 点位地址
     * @param qualifier
     * @throws IOException
     */
    public void parameterActivation(int commonAddress, CauseOfTransmission cot, int informationObjectAddress,
                                    IeQualifierOfParameterActivation qualifier) throws IOException {
        connection.parameterActivation(commonAddress, cot, informationObjectAddress, qualifier);
    }


    /**
     *  发送一个激活确认
     * @param aSdu
     * @throws IOException
     */
    public void sendConfirmation(ASdu aSdu) throws IOException {
        sendConfirmation(aSdu, aSdu.getCommonAddress(), false);
    }

    /**
     * 发送响应时使用给定的ASDU。若为广播响应，则使用给定的站地址作为公共ASDU地址；否则使用给定ASDU原有的公共ASDU地址
     * @param aSdu 要响应的对应请求的asdu
     * @param stationAddress
     *            address of this station
     * @throws IOException
     *             if a fatal communication error occurred.
     */
    public void sendConfirmation(ASdu aSdu, int stationAddress) throws IOException {
        sendConfirmation(aSdu, stationAddress, false);
    }

    /**
     * 发送响应时使用给定的ASDU。若为广播响应，则使用给定的站地址作为公共ASDU地址；否则使用给定ASDU原有的公共ASDU地址
     *
     * @param aSdu
     * @param stationAddress 站地址
     * @param isNegativeConfirm 是否定确认
     * @throws IOException
     *             if a fatal communication error occurred.
     */
    public void sendConfirmation(ASdu aSdu, int stationAddress, boolean isNegativeConfirm) throws IOException {
        connection.sendConfirmation(aSdu, stationAddress, isNegativeConfirm);
    }

    /**
     * 发送响应时使用给定的ASDU。若为广播响应，则使用给定的站地址作为公共ASDU地址；否则使用给定ASDU原有的公共ASDU地址
     *
     * @param aSdu 源aSdu
     * @param stationAddress 站地址
     * @param isNegativeConfirm 是否定确认
     * @param cot
     *            传输原因 e.g. negative confirm UNKNOWN_TYPE_ID(44),
     *            UNKNOWN_CAUSE_OF_TRANSMISSION(45), UNKNOWN_COMMON_ADDRESS_OF_ASDU(46) and
     *            UNKNOWN_INFORMATION_OBJECT_ADDRESS(47)
     * @throws IOException
     */
    public void sendConfirmation(ASdu aSdu, int stationAddress, boolean isNegativeConfirm, CauseOfTransmission cot)
            throws IOException {
        connection.sendConfirmation(aSdu, stationAddress, isNegativeConfirm, cot);
    }

    /**
     *
     * 发送带有给定ASDU的激活终止信号。给定ASDU的公共ASDU地址将用作站点地址。
     * @param aSdu
     * @param stationAddress
     * @throws IOException
     */
    public void sendActivationTermination(ASdu aSdu, int stationAddress) throws IOException {
        connection.sendActivationTermination(aSdu, stationAddress);
    }

    /**
     * 发送带有给定ASDU的激活终止信号。给定ASDU的公共ASDU地址将用作站点地址。
     */
    public void sendActivationTermination(ASdu aSdu) throws IOException {
        sendActivationTermination(aSdu, aSdu.getCommonAddress());
    }

    protected void reconnect() {
        if(getConfig().getReconnect() > 0) {
            IotThreadManager.instance().getExecutorService().schedule(() -> {
                connect();
            }, getConfig().getReconnect(), TimeUnit.SECONDS);
        }
    }

    @Override
    public Object connect() {
        try {
            connection = getConfig().getConnectionBuilder().build();
            connection.setOriginatorAddress(getConfig().getOriginatorAddress());
            startDataTransfer();
            this.close = null; // 重置连接状态
            return !connection.isClosed();
        } catch (IOException e) {
            reconnect();
            return false;
        }
    }

    @Override
    public Object disconnect() {
        try {
            this.close = false;
            this.stopDataTransfer();
            this.connection.close();
            logger.info("断开客户端成功({}) 客户端: {}", getClientComponent().getName(), getConfig().connectKey());
        } catch (Exception e) {
            throw new ProtocolException(e.getMessage(), e);
        }

        return true;
    }

    @Override
    public Object close() {
        try {
            this.close = true;
            this.stopDataTransfer();
            this.connection.close();
            logger.info("关闭客户端成功({}) 客户端: {}", getClientComponent().getName(), getConfig().connectKey());
        } catch (Exception e) {
            throw new ProtocolException(e.getMessage(), e);
        }

        return this.close;
    }

    /**
     * 停止数据传输
     */
    public void stopDataTransfer() {
        if(!connection.isStopped()) {
            try {
                connection.stopDataTransfer();
            } catch (IOException e) {
                throw new ProtocolException("数据传输停止异常", e);
            }
        }
    }

    /**
     * 启动数据传输
     */
    public void startDataTransfer() {
        if(connection.isStopped()) {
            try {
                connection.startDataTransfer();
            } catch (IOException e) {
                throw new ProtocolException("数据传输启动异常", e);
            }
        }
    }

    @Override
    public ClientComponent getClientComponent() {
        return this.component;
    }
}
