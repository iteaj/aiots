package com.iteaj.iboot.plugin.protocol.opcua.driver;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.spi.iot.ProtocolInvokeException;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaException;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaProtocol;
import com.iteaj.iboot.plugin.protocol.opcua.network.OpcuaVariableProtocol;
import com.iteaj.iot.Protocol;
import org.eclipse.milo.opcua.stack.core.types.builtin.DateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Consumer;

public class OpcuaProtocolFuncModelApi extends AbstractFuncProtocolModelApi {

    public OpcuaProtocolFuncModelApi(String code, String name) {
        this(code, name, null);
    }

    public OpcuaProtocolFuncModelApi(String code, String name, String remark) {
        super(code, name, FuncType.W, TriggerMode.passive, remark);
    }

    @Override
    protected Protocol buildProtocol(ProtocolModelApiInvokeParam arg) {
        Protocol protocol = null;
        Integer ns = arg.getDeviceConfig("ns");
        String tag = arg.getDeviceConfig("tag");
        String address = arg.getString("address");
        if(StrUtil.isBlank(address)) {
            throw new ProtocolInvokeException("地址不能为空");
        }

        if(address.contains("{") && address.contains("}")) {
            HashMap<Object, Object> params = new HashMap<>();
            params.put("ns", ns); params.put("tag", tag);
            address = StrUtil.format(address, params);
        }

        OpcuaProtocol.OpcuaProtocolType type = OpcuaProtocol.OpcuaProtocolType.valueOf(getCode());
        if(type == OpcuaProtocol.OpcuaProtocolType.WRITE) {
            Object value = arg.getString("value");
            if(value == null) {
                throw new ProtocolInvokeException("未指定写入值");
            }

            String dataType = arg.getString("dataType");
            if(dataType == null) {
                throw new ProtocolInvokeException("未指定数据类型");
            }
            switch (dataType) {
                case "byte":
                    value = Byte.valueOf(value.toString());
                    break;
                case "short":
                    value = Short.valueOf(value.toString());
                    break;
                case "int":
                    value = Integer.valueOf(value.toString());
                    break;
                case "long":
                    value = Long.valueOf(value.toString());
                    break;
                case "float":
                    value = Float.valueOf(value.toString());
                    break;
                case "double":
                    value = Double.valueOf(value.toString());
                    break;
                case "boolean":
                    value = Boolean.valueOf(value.toString());
                    break;
                case "date":
                    String date = value.toString().toLowerCase().trim();
                    if(Objects.equals(date, "{now}")) {
                        value = DateTime.now();
                    } else {
                        Date datetime = DateUtil.parse(value.toString(), DatePattern.NORM_DATETIME_FORMAT);
                        value = new DateTime(datetime);
                    }
                    break;
                default: value = value.toString();
            }

            protocol = OpcuaVariableProtocol.write(address, value);
        } else if(type == OpcuaProtocol.OpcuaProtocolType.EXEC_METHOD) {
            //
        }

        return protocol;
    }

    @Override
    protected void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result) {
        if(protocol instanceof OpcuaProtocol) {
            try {
                OpcuaProtocol opcuaProtocol = (OpcuaProtocol) protocol;
                opcuaProtocol.request(arg.getParentDeviceSn());
                result.accept(InvokeResult.success(protocol));
            } catch (OpcuaException e) {
                result.accept(InvokeResult.fail(e.getMessage(), protocol));
            } catch (Exception e) {
                e.printStackTrace();
                result.accept(InvokeResult.fail("写入失败", protocol));
            }
        }
    }
}
