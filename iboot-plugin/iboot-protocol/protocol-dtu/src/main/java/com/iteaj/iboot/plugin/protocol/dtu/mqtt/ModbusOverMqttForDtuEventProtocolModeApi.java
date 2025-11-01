package com.iteaj.iboot.plugin.protocol.dtu.mqtt;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.plugin.protocol.dtu.AbstractDtuForModbusProtocolSupplier;
import com.iteaj.iboot.plugin.protocol.dtu.AbstractModbusEventProtocolModelApi;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.format.DataFormat;
import com.iteaj.iot.modbus.ModbusCommonProtocol;
import com.iteaj.iot.modbus.Payload;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuCommonProtocol;
import com.iteaj.iot.modbus.server.dtu.ModbusRtuForDtuMessage;
import com.iteaj.iot.modbus.server.rtu.ModbusRtuBody;
import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.iteaj.iboot.plugin.protocol.dtu.mqtt.ModbusOverMqttForDtuFuncProtocolModelApi.buildModbusOverMqttPublish;

public class ModbusOverMqttForDtuEventProtocolModeApi extends AbstractModbusEventProtocolModelApi {

    public ModbusOverMqttForDtuEventProtocolModeApi(String code, String name, String remark) {
        super(code, name, remark);
    }

    @Override
    public void invoke(ProtocolModelApiInvokeParam arg, Map<String, List<List<BatchOfPoint>>> points, Consumer<BatchResult> consumer) {
        try {
            final int childSn = Integer.parseInt(arg.getDeviceSn());
            String deviceSn = StrUtil.isNotBlank(arg.getParentDeviceSn()) ? arg.getParentDeviceSn() : arg.getDeviceSn();
            DataSupplier dataSupplier = new DataSupplier(getProtocolModel().getCode(), arg.buildKey());
            final BatchResult batchResult = new BatchResult(dataSupplier);

            arg.addConfig("isBatch", Boolean.TRUE);
            List<Point> pointList = points.entrySet().stream().map(entry -> {
                ModbusCode code = ModbusCode.valueOf(entry.getKey());
                return entry.getValue().stream()
                        .map(items -> new Point(code, items))
                        .collect(Collectors.toList());
            }).flatMap(Collection::stream).collect(Collectors.toList());

            doPublish(arg, pointList, consumer, batchResult, dataSupplier, deviceSn, childSn, 0);
        } catch (NumberFormatException e) {
            consumer.accept(new BatchResult("解析设备编号失败["+e.getMessage()+"]", ExecStatus.fail));
        } catch (Exception e) {
            consumer.accept(new BatchResult("未知异常["+e.getMessage()+"]", ExecStatus.fail));
        }
    }

    private void doPublish(ProtocolModelApiInvokeParam arg, List<Point> pointList
            , Consumer<BatchResult> consumer, BatchResult batchResult, DataSupplier dataSupplier, String deviceSn, int childSn, int index) {
        Point execPoint = pointList.get(index);

        final int start = Integer.parseInt(execPoint.points.get(0).getAddress());
        final BatchOfPoint endPoint = execPoint.points.get(execPoint.points.size() - 1);
        final int end = Integer.parseInt(endPoint.getAddress());
        final int num = end - start;

        ModbusCommonProtocol protocol;
        switch (execPoint.code) {
            case Read01:
                protocol = this.buildRead01(deviceSn, childSn, start, num + 1);
                break;
            case Read02:
                protocol = this.buildRead02(deviceSn, childSn, start, num + 1);
                break;
            case Read03:
                final int end03Num = getNum(arg, endPoint.getDownConfig(AbstractDtuForModbusProtocolSupplier.dataType));
                protocol = this.buildRead03(deviceSn, childSn, start, num + end03Num);
                break;
            case Read04:
                final int end04Num = getNum(arg, endPoint.getDownConfig(AbstractDtuForModbusProtocolSupplier.dataType));
                protocol = this.buildRead04(deviceSn, childSn, start, num + end04Num);
                break;
            default:
                throw new ProtocolInvokeException(getProtocolModel().getCode(), getCode(), "不支持的功能代码["+getCode()+"]");
        }

        doInvoke(protocol, arg, (result) -> {
            try {
                handleBatchResult(execPoint.points, result, batchResult, protocol, endPoint, dataSupplier);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(index < pointList.size() - 1) {
                    doPublish(arg, pointList, consumer, batchResult, dataSupplier, deviceSn, childSn, index + 1);
                } else {
                    consumer.accept(batchResult);
                }
            }
        });
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        buildModbusOverMqttPublish(protocol, arg).request((publishProtocol) -> {
            ExecStatus execStatus = publishProtocol.getExecStatus();

            // 设置modbus响应报文
            ModbusRtuForDtuCommonProtocol commonProtocol = (ModbusRtuForDtuCommonProtocol) protocol;
            commonProtocol.setExecStatus(execStatus);
            ModbusRtuForDtuMessage dtuMessage = new ModbusRtuForDtuMessage(publishProtocol.responseMessage().getMessage());
            dtuMessage.readBuild();
            commonProtocol.setResponseMessage(dtuMessage).buildResponseMessage();

            // 计算响应结果
            InvokeResult invokeResult;
            if(publishProtocol.getExecStatus() == ExecStatus.success) {
                DataFormat dataFormat = DataFormat.valueOf(arg.getDeviceConfig("dataFormat"));
                ModbusRtuBody body = dtuMessage.getBody();
                if(body.isSuccess()) {
                    Payload payload = commonProtocol.getPayload(dataFormat);
                    Object value = getValue(payload, arg);
                    invokeResult = InvokeResult.status(execStatus, value, protocol);
                } else {
                    invokeResult = InvokeResult.fail(body.getErrCode().getDesc(), protocol);
                }
            } else {
                invokeResult = InvokeResult.status(execStatus, protocol);
            }

            result.accept(invokeResult);
        });
    }

    @Override
    public ModbusCommonProtocol buildRead01(String equipCode, int device, int start, int num) {
        return ModbusRtuForDtuCommonProtocol.buildRead01(equipCode, device, start, num);
    }

    @Override
    public ModbusCommonProtocol buildRead02(String equipCode, int device, int start, int num) {
        return ModbusRtuForDtuCommonProtocol.buildRead02(equipCode, device, start, num);
    }

    @Override
    public ModbusCommonProtocol buildRead03(String equipCode, int device, int start, int num) {
        return ModbusRtuForDtuCommonProtocol.buildRead03(equipCode, device, start, num);
    }

    @Override
    public ModbusCommonProtocol buildRead04(String equipCode, int device, int start, int num) {
        return ModbusRtuForDtuCommonProtocol.buildRead04(equipCode, device, start, num);
    }

    @Getter
    public static class Point {
        private final ModbusCode code;
        private final List<BatchOfPoint> points;

        public Point(ModbusCode code, List<BatchOfPoint> points) {
            this.code = code;
            this.points = points;
        }
    }
}
