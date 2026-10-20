package com.iteaj.framework.script;

import com.iteaj.framework.spi.iot.DataSupplier;
import com.iteaj.framework.spi.iot.IotCoreApiService;
import com.iteaj.framework.spi.iot.SignalOrFieldValue;
import com.iteaj.framework.spi.iot.data.ModelAttrMeta;
import com.iteaj.framework.spi.iot.data.ModelAttrRealtimeData;
import com.iteaj.framework.spi.iot.data.ProductMeta;
import com.iteaj.framework.spi.iot.data.RealtimeDeviceStatus;
import com.iteaj.iot.consts.ExecStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalcAttrScriptMethodProvide implements LoggerMethodProvide {

    public static final String NAME = "计算属性";
    private final ExecStatus status;
    private final DataSupplier dataSupplier;
    private final RealtimeDeviceStatus device;
    private static final List<ScriptComplete> COMPLTES = new ArrayList<>();

    static {
        COMPLTES.add(new ScriptComplete("methods.isSuccess()", "是否采集成功", 1));
        COMPLTES.add(new ScriptComplete("methods.isDevice(uid)", "是否为指定uid的设备", 1));
        COMPLTES.add(new ScriptComplete("methods.getValue(field)", "获取当前设备模型属性值", 1));
        COMPLTES.add(new ScriptComplete("methods.getInt(field)", "获取当前设备int值", 1));
        COMPLTES.add(new ScriptComplete("methods.getFloat(field)", "获取当前设备float值", 1));
        COMPLTES.add(new ScriptComplete("methods.getDouble(field)", "获取当前设备double值", 1));
        COMPLTES.add(new ScriptComplete("methods.getString(field)", "获取当前设备string值", 1));
        COMPLTES.add(new ScriptComplete("methods.getValue(uid, field)", "获取指定设备的模型属性值", 1));
        COMPLTES.add(new ScriptComplete("methods.addValue(field, value)", "添加指定模型属性的值", 1));
        COMPLTES.add(new ScriptComplete("methods.updateValue(field, value)", "更新指定模型属性值", 1));
        COMPLTES.addAll(LOGGER_COMPLETES);
    }

    public CalcAttrScriptMethodProvide(DataSupplier dataSupplier, RealtimeDeviceStatus device, ExecStatus status) {
        this.dataSupplier = dataSupplier;
        this.status = status;
        this.device = device;
    }

    public Float getFloat(String field) {
        return (Float) dataSupplier.getFieldValue(field).getValue();
    }
    public Double getDouble(String field) {
        return (Double) dataSupplier.getFieldValue(field).getValue();
    }
    public String getString(String field) {
        return (String) dataSupplier.getFieldValue(field).getValue();
    }
    public Integer getInt(String field) {
        return (Integer) dataSupplier.getFieldValue(field).getValue();
    }
    public Object getValue(String field) {
        return dataSupplier.getFieldValue(field).getValue();
    }
    public Object getValue(String uid, String field) {
        ModelAttrRealtimeData realtimeData = IotCoreApiService.getRealtimeData(uid, field);
        if(realtimeData == null || realtimeData.getRealtime() == null) return null;
        return realtimeData.getRealtime().getValue();
    }

    public void addValue(String field, Object value) {
        SignalOrFieldValue fieldValue = dataSupplier.getFieldValue(field);
        if(fieldValue == null) {
            ModelAttrMeta attrMeta = IotCoreApiService.getAttrMeta(device.getProductCode(), field);
            if(attrMeta != null) {
                dataSupplier.addValue(attrMeta.getId(), field, value);
            }
        } else {
            error(device.getUid(), "设备模型属性[" + field + "]不存在");
        }
    }

    public void updateValue(String field, Object value) {
        SignalOrFieldValue fieldValue = dataSupplier.getFieldValue(field);
        if(fieldValue != null) {
            fieldValue.setValue(value);
        } else {
            error(device.getUid(), "设备模型属性[" + field + "]不存在");
        }
    }

    public boolean isSuccess() {
        return status == ExecStatus.success;
    }

    public boolean isDevice(String uid) {
        return device.getUid().equals(uid);
    }

    public static List<ScriptComplete> getCompletions() {
        return COMPLTES;
    }

    public static void invokeCalcScript(DataSupplier supplier, RealtimeDeviceStatus device) {
        try {
            long currentTimeMillis = System.currentTimeMillis();
            ProductMeta product = IotCoreApiService.getProductByCache(device.getProductId());
            if(product.getConfig() != null) {
                String calcLanguage = product.getConfig().getString("calcLanguage");
                if(calcLanguage != null && !calcLanguage.equals("disabled")) {
                    ScriptInterface<?> scriptInterface = ScriptManager.get(calcLanguage);
                    if(scriptInterface != null) {
                        Map<String, Object> params = new HashMap<>(1);
                        params.put("methods", new CalcAttrScriptMethodProvide(supplier, device, ExecStatus.success));
                        String calcScript = product.getConfig().getString("calcScript");
                        scriptInterface.exec(calcScript, params); // 调用脚本

                        ScriptLoggerFilter.debug(NAME, device.getUid(), "模型属性计算脚本执行成功 - 耗时: {}ms - 设备编号: {}"
                                , System.currentTimeMillis() - currentTimeMillis, supplier.getKey());
                    }
                }
            }
        } catch (Exception e) {
            ScriptLoggerFilter.error(NAME, device.getUid(), "模型属性计算脚本执行异常 {} - 设备编号: {}", e.getMessage(), supplier.getKey(), e);
        }
    }

    @Override
    public String getSubType() {
        return NAME;
    }
}
