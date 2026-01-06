package com.iteaj.plugin.protocol.iec104;

import cn.hutool.core.date.DateUtil;
import com.iteaj.iot.client.IotClient;
import com.iteaj.plugin.protocol.iec104.network.*;
import org.openmuc.j60870.ASdu;
import org.openmuc.j60870.ASduType;
import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.ie.IeSingleCommand;
import org.openmuc.j60870.ie.IeTime56;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.List;

public class IEC104AutoConfiguration {

    private IEC104Client iotClient;

    @Bean
    public IEC104Component iec104Component() {
        final IEC104Component iec104Component = new IEC104Component();
        iotClient = iec104Component.createNewClientAndConnect(new IEC104ConnectProperties(), new IEC104Call() {
            @Override
            public void teCallValue(List<IEC104TEValue> values, CauseOfTransmission cot, ASduType type, int coa) {
                System.out.println("遥测值回调 类型: " + type + " - 原因: " + cot.name() + " - 数量: " + values.size());
            }

            @Override
            public void teCallTime2aValue(List<IEC104TETime2aValue> values, CauseOfTransmission cot, ASduType type, int coa) {
                values.forEach(item -> System.out.println(DateUtil.format(item.getDate(), "yyyy-MM-dd HH:mm:ss")));
                System.out.println("遥测值带时间回调 类型: " + type + " - 原因: " + cot.name() + " - 数量: " + values.size());
            }

            @Override
            public void tiCallValue(List<IEC104TIValue> values, CauseOfTransmission cot, ASduType type, int coa) {
                System.out.println("遥信值回调 类型: " + type + " - 原因: " + cot.name() + " - 数量: " + values.size());
            }

            @Override
            public void tiCallTime2aValue(List<IEC104TITime2aValue> values, CauseOfTransmission cot, ASduType type, int coa) {
                values.forEach(item -> System.out.println(DateUtil.format(item.getDate(), "yyyy-MM-dd HH:mm:ss")));
                System.out.println("遥信值带time2a回调 类型: " + type + " - 原因: " + cot.name() + " - 数量: " + values.size());
            }

            @Override
            public void eventCallTime2a(List<IEC104EventTime2aValue> values, CauseOfTransmission cot, ASduType type, int coa) {
                values.forEach(item -> System.out.println(DateUtil.format(item.getDate(), "yyyy-MM-dd HH:mm:ss.SSS")));
                if (cot == CauseOfTransmission.SPONTANEOUS) { // 触发控制测试
                    try {
                        iotClient.testCommand(coa);
//                        iotClient.singleCommand(coa, CauseOfTransmission.ACTIVATION, 22
//                                , new IeSingleCommand(true, 0, false));
//                        iotClient.synchronizeClocks(coa, new IeTime56(System.currentTimeMillis()));
//                        iotClient.readCommand(coa, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("事件值带time2a回调 类型: " + type + " - 原因: " + cot.name() + " - 数量: " + values.size());
            }

            @Override
            public void counterCall(List<IEC104CounterValue> values, CauseOfTransmission cot, ASduType type, int coa) {
                System.out.println("累积量值回调 类型: " + type + " - 原因: " + cot.name() + " - 数量: " + values.size());
            }

            @Override
            public void counterCallTime2a(List<IEC104CounterTime2aValue> values, CauseOfTransmission cot, ASduType type, int coa) {
                values.forEach(item -> System.out.println(DateUtil.format(item.getDate(), "yyyy-MM-dd HH:mm:ss.SSS")));
                System.out.println("累计量值带time2a回调 类型: " + type + " - 原因: " + cot.name() + " - 数量: " + values.size());
            }

            @Override
            public void ctrlCall(ASdu aSdu, ASduType ti, CauseOfTransmission cot, int coa) {
                if(!aSdu.isTestFrame() && cot == CauseOfTransmission.ACTIVATION_CON) { // 执行确认
                    try {
                        iotClient.sendActivationTermination(aSdu); // 发送激活确认
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return iec104Component;
    }
}
