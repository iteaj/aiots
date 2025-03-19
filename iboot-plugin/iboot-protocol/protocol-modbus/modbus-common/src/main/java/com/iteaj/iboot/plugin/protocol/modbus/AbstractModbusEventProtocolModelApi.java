package com.iteaj.iboot.plugin.protocol.modbus;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.framework.spi.iot.consts.PointProtocolConfig;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractEventProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.ProtocolException;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.format.DataFormat;
import com.iteaj.iot.modbus.ModbusCommonProtocol;
import com.iteaj.iot.modbus.Payload;
import com.iteaj.iot.modbus.ReadPayload;
import com.iteaj.iot.modbus.RealCoilPayload;
import com.iteaj.iot.modbus.consts.ModbusBitStatus;
import com.iteaj.iot.modbus.consts.ModbusCode;
import com.iteaj.iot.utils.ByteUtil;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractModbusEventProtocolModelApi extends AbstractEventProtocolModelApi implements BatchEventModelApi {

    public AbstractModbusEventProtocolModelApi(String code, String name) {
        this(code, name, null);
    }

    public AbstractModbusEventProtocolModelApi(String code, String name, String remark) {
        super(code, name, remark, TriggerMode.passive);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        ModbusCode modbusCode = ModbusCode.valueOf(getCode());
        String dataType = arg.getString(AbstractModbusProtocolSupplier.dataType);

        int start;
        try {
            start = arg.getInteger(PointProtocolConfig.POINT_ADDRESS);
        } catch (Exception e) {
            throw new ProtocolInvokeException("寄存器地址必须是[0-65535]的数字");
        }

        int childSn;
        try {
            childSn = Integer.valueOf(arg.getDeviceSn());
        } catch (NumberFormatException e) {
            throw new ProtocolInvokeException("从机设备地址必须是[0-255]的数字");
        }

        int num = 1;
        ModbusCommonProtocol protocol;
        switch (modbusCode) {
            case Read01:
                protocol = this.buildRead01(childSn, start, num);
                break;
            case Read02:
                protocol = this.buildRead02(childSn, start, num);
                break;
            case Read03:
                if(StrUtil.isBlank(dataType)) {
                    throw new ProtocolInvokeException("未找到数据类型参数");
                }

                num = getNum(arg, dataType);
                protocol = this.buildRead03(childSn, start, num);
                break;
            case Read04:
                if(StrUtil.isBlank(dataType)) {
                    throw new ProtocolInvokeException("未找到数据类型参数");
                }

                num = getNum(arg, dataType);
                protocol = this.buildRead04(childSn, start, num);
                break;
            default:
                throw new ProtocolInvokeException(getProtocolModel().getCode(), getCode(), "不支持的功能代码["+getCode()+"]");
        }

        return protocol;
    }

    public abstract ModbusCommonProtocol buildRead01(int device, int start, int num);
    public abstract ModbusCommonProtocol buildRead02(int device, int start, int num);
    public abstract ModbusCommonProtocol buildRead03(int device, int start, int num);
    public abstract ModbusCommonProtocol buildRead04(int device, int start, int num);

    private int getNum(ProtocolModelApiInvokeParam arg, String dataType) {
        switch (dataType) {
            case "short":
            case "ushort":
                return 1;
            case "int":
            case "uint":
            case "float":
                return 2;
            case "long":
            case "double":
            case "timestamp":
                return 4;
            case "string":
            case "hex":
                try {
                    return arg.getInteger(PointProtocolConfig.POINT_NUMBER);
                } catch (Exception e) {
                    throw new ProtocolInvokeException("未指定要读取的寄存器数量");
                }
            default:
                throw new ProtocolInvokeException("不支持的数据类型["+dataType+"]");
        }
    }

    @Override
    public Map<String, List<List<BatchOfPoint>>> resolver(List<BatchOfPoint> points) {
        final Map<String, List<List<BatchOfPoint>>> result = new HashMap<>();

        // 分组
        final Map<String, List<BatchOfPoint>> collect = points.stream().collect(Collectors.groupingBy(BatchOfPoint::getDirect, Collectors.toList()));

        // 排序
        collect.forEach((key, value) -> value.sort((Comparator.comparingInt(o -> (Integer.parseInt(o.getAddress()))))));

        // 批量长度切割
        collect.forEach((direct, sortPoints) -> {
            final List<List<BatchOfPoint>> splitNumAddress;
            if(direct.equals("Read03") || direct.equals("Read04")) {
                splitNumAddress = BatchUtils.splitNumAddress(sortPoints, 110);
            } else {
                splitNumAddress = BatchUtils.splitNumAddress(sortPoints, 1800);
            }

            result.put(direct, splitNumAddress);
        });

        return result;
    }

    @Override
    public void invoke(ProtocolModelApiInvokeParam arg, Map<String, List<List<BatchOfPoint>>> points, Consumer<BatchResult> consumer) {
        try {
            final int deviceSn = Integer.parseInt(arg.getDeviceSn());
            DataSupplier dataSupplier = new DataSupplier(getProtocolModel().getCode(), arg.buildKey());
            final BatchResult batchResult = new BatchResult(dataSupplier);
            points.forEach((direct, value) -> {
                final ModbusCode code = ModbusCode.valueOf(direct);
                value.forEach(splitPoints -> {
                    final int start = Integer.parseInt(splitPoints.get(0).getAddress());
                    final BatchOfPoint endPoint = splitPoints.get(splitPoints.size() - 1);
                    final int end = Integer.parseInt(endPoint.getAddress());
                    final int num = end - start;

                    ModbusCommonProtocol protocol;
                    switch (code) {
                        case Read01:
                            protocol = this.buildRead01(deviceSn, start, num + 1);
                            break;
                        case Read02:
                            protocol = this.buildRead02(deviceSn, start, num + 1);
                            break;
                        case Read03:
                            final int end03Num = getNum(arg, endPoint.getDownConfig(AbstractModbusProtocolSupplier.dataType));
                            protocol = this.buildRead03(deviceSn, start, num + end03Num);
                            break;
                        case Read04:
                            final int end04Num = getNum(arg, endPoint.getDownConfig(AbstractModbusProtocolSupplier.dataType));
                            protocol = this.buildRead04(deviceSn, start, num + end04Num);
                            break;
                        default:
                            throw new ProtocolInvokeException(getProtocolModel().getCode(), getCode(), "不支持的功能代码["+getCode()+"]");
                    }

                    arg.addConfig("isBatch", Boolean.TRUE);
                    doInvoke(protocol, arg, (result) -> {
                        try {
                            batchResult.setStatus(result.getStatus());
                            if(result.getStatus() == ExecStatus.success) {
                                final Payload payload = protocol.getPayload();
                                final int startAddress = Integer.parseInt(splitPoints.get(0).getAddress());
                                splitPoints.forEach(point -> {
                                    final int address = Integer.parseInt(point.getAddress());
                                    if(payload instanceof ReadPayload) {
                                        final String dataType = endPoint.getDownConfig(AbstractModbusProtocolSupplier.dataType);
                                        final String strNum = point.getDownConfig(PointProtocolConfig.POINT_NUMBER);
                                        final Object pointValue = resolverValue(payload, Integer.valueOf(strNum), address, dataType);
                                        dataSupplier.addValue(point.getAttrId(), point.getAddress(), point.getAttrField(), pointValue);
                                    } else {
                                        int readNum = address - startAddress;
                                        final ModbusBitStatus status = payload.readStatus(readNum);
                                        dataSupplier.addValue(point.getAttrId(), point.getAddress(), point.getAttrField(), status == ModbusBitStatus.ON ? true : false);
                                    }
                                });
                            } else {
                                splitPoints.forEach(point -> {
                                    dataSupplier.addValue(point.getAttrId(), point.getAddress(), point.getAttrField(), CollectStatus.from(result.getStatus()));
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });
            });

            consumer.accept(batchResult);
        } catch (NumberFormatException e) {
            consumer.accept(new BatchResult("解析设备编号失败["+e.getMessage()+"]", ExecStatus.fail));
        } catch (Exception e) {
            consumer.accept(new BatchResult("未知异常["+e.getMessage()+"]", ExecStatus.fail));
        }
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        DataFormat dataFormat = DataFormat.valueOf(arg.getDeviceConfig("dataFormat"));
        try {
            InvokeResult invokeResult = doProtocolInvoke(protocol, arg, dataFormat);
            result.accept(invokeResult);
        } catch (ProtocolException e) {
            e.printStackTrace();
            result.accept(InvokeResult.fail(e.getMessage(), protocol));
        } catch (Exception e) {
            e.printStackTrace();
            result.accept(InvokeResult.fail("未知错误["+e.getMessage()+"]", protocol));
        }
    }

    protected abstract InvokeResult doProtocolInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, DataFormat dataFormat);

    public Object getValue(Payload payload, ProtocolModelApiInvokeParam arg) {
        if(arg.getConfig("isBatch") != null) { // 批量读取
            return payload;
        }

        if(payload instanceof RealCoilPayload) {
            List<ModbusBitStatus> statuses = ((RealCoilPayload) payload).getStatuses();
            return statuses.get(0) == ModbusBitStatus.ON ? Boolean.TRUE : Boolean.FALSE;
        }

        int start = arg.getInteger(PointProtocolConfig.POINT_ADDRESS);
        final Integer num = arg.getInteger(PointProtocolConfig.POINT_NUMBER);
        String dataType = arg.getString(AbstractModbusProtocolSupplier.dataType);
        return resolverValue(payload, num, start, dataType);
    }

    private Object resolverValue(Payload payload, Integer num, int start, String dataType) {
        switch (dataType) {
            case "short":
                return payload.readShort(start);
            case "ushort":
                return payload.readUShort(start);
            case "int":
                return payload.readInt(start);
            case "uint":
                return payload.readUInt(start);
            case "float":
                return payload.readFloat(start);
            case "long":
                return payload.readLong(start);
            case "double":
                return payload.readDouble(start);
            case "timestamp":
                return new Date(payload.readLong(start));
            case "string":
                return payload.readString(start, num);
            case "hex":
                    return ByteUtil.bytesToHex(payload.getPayload());
            default:
                throw new ProtocolInvokeException("不支持的数据类型["+ dataType +"]");
        }
    }

    @Override
    protected void setProtocolModel(ProtocolModel protocolModel) {
        super.setProtocolModel(protocolModel);
    }
}
