package com.iteaj.iboot.plugin.protocol.opcua.driver;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractEventProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaException;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaProtocol;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaVariableProtocol;
import com.iteaj.iot.Protocol;
import com.iteaj.iot.consts.ExecStatus;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OpcuaProtocolEventModelApi extends AbstractEventProtocolModelApi implements BatchEventModelApi {

    public OpcuaProtocolEventModelApi(String code, String name) {
        this(code, name, null);
    }

    public OpcuaProtocolEventModelApi(String code, String name, String remark) {
        super(code, name, remark, TriggerMode.passive);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        Protocol protocol = null;
        String address = resolverAddress(arg, arg.getString("address"));
        arg.getParam().put("resolverAddress", address);
        OpcuaProtocol.OpcuaProtocolType type = OpcuaProtocol.OpcuaProtocolType.valueOf(getCode());
        if(type == OpcuaProtocol.OpcuaProtocolType.READ) {
            protocol = OpcuaVariableProtocol.read(address);
        } else if(type == OpcuaProtocol.OpcuaProtocolType.READ_OBJECT) {
//            protocol
        }

        return protocol;
    }

    private static String resolverAddress(ProtocolModelApiInvokeParam arg, String address) {
        if(StrUtil.isBlank(address)) {
            throw new ProtocolInvokeException("地址不能为空");
        }

        Integer ns = arg.getDeviceConfig("ns");
        String tag = arg.getDeviceConfig("tag");

        if(address.contains("{") && address.contains("}")) {
            HashMap<Object, Object> params = new HashMap<>();
            params.put("ns", ns); params.put("tag", tag);
            address = StrUtil.format(address, params);
        }

        return address;
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        if(protocol instanceof OpcuaProtocol) {
            try {
                OpcuaProtocol opcuaProtocol = (OpcuaProtocol) protocol;
                opcuaProtocol.request(arg.getParentDeviceSn());
                result.accept(InvokeResult.success(opcuaProtocol.getValue(), protocol));
            } catch (OpcuaException e) {
                result.accept(InvokeResult.fail(e.getMessage(), protocol));
            } catch (Exception e) {
                e.printStackTrace();
                result.accept(InvokeResult.fail("调用协议失败", protocol));
            }
        }
    }

    @Override
    public Map<String, List<List<BatchOfPoint>>> resolver(List<BatchOfPoint> points) {
        final Map<String, List<List<BatchOfPoint>>> result = new HashMap<>();
        
        // 分组
        final Map<String, List<BatchOfPoint>> collect = points.stream().collect(Collectors.groupingBy(BatchOfPoint::getDirect, Collectors.toList()));
        collect.forEach((direct, value) -> {
            List<List<BatchOfPoint>> objects = new ArrayList<>();
            objects.add(value);
            result.put(direct, objects);
        });

        return result;
    }

    @Override
    public void invoke(ProtocolModelApiInvokeParam arg, Map<String, List<List<BatchOfPoint>>> points, Consumer<BatchResult> consumer) {
        points.forEach((direct, value) -> {
            if(direct.equals(OpcuaProtocol.OpcuaProtocolType.READ.name())) {
                DataSupplier dataSupplier = new DataSupplier(getProtocolModel().getCode(), arg.buildKey());
                BatchResult batchResult = new BatchResult(dataSupplier);
                batchResult.setStatus(ExecStatus.fail); // 默认失败
                Integer limit = arg.getDeviceConfig("limit");
                List<BatchOfPoint> allPoints = value.get(0);
                List<List<BatchOfPoint>> splits = BatchUtils.splitStrAddress(allPoints, limit);
                splits.forEach(batchOfPoints -> {
                    List<String> addresses = batchOfPoints.stream()
                            .map(point -> resolverAddress(arg, point.getAddress()))
                            .collect(Collectors.toList());

                    try {
                        OpcuaVariableProtocol protocol = OpcuaVariableProtocol.read(addresses);
                        protocol.request(arg.getParentDeviceSn());
                        List<DataValue> dataValues = (List<DataValue>) protocol.getValue();
                        for (int i = 0; i < batchOfPoints.size(); i++) {
                            String address = addresses.get(i);
                            DataValue dataValue = dataValues.get(i);
                            BatchOfPoint ofPoint = batchOfPoints.get(i);
                            if(dataValue.getStatusCode() == StatusCode.GOOD) {
                                batchResult.setStatus(ExecStatus.success); // 只要有一个点成功说明设备在线
                                Object resolver = DataValueResolverFactory.resolver(ofPoint.getModelAttr(), OpcuaProtocolSupplier.getValue(dataValue));
                                dataSupplier.addValue(ofPoint.getModelAttr().getId(), address, ofPoint.getAttrField(), resolver);
                            } else {
                                dataSupplier.addValue(ofPoint.getModelAttr().getId(), address, ofPoint.getAttrField(), CollectStatus.Fail);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                consumer.accept(batchResult);
            }
        });
    }
}
