package com.iteaj.iboot.plugin.script.model;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.script.*;
import com.iteaj.framework.spi.iot.data.RealtimeCollectData;
import com.iteaj.framework.spi.iot.data.RealtimeDeviceStatus;
import com.iteaj.framework.spi.iot.listener.IotAsyncListener;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.RealtimeInfoListener;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;
import jdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.*;

/**
 * 监听实时数据时间, 通过脚本引擎做场景联动
 */
public class LinkageScriptRealtimeListener implements RealtimeInfoListener, IotAsyncListener<RealtimePayload> {

    private final static String SPEC = ":";
    private final static String ALL_FIELD = ":*";
    private final static String STATUS = ":#status";
    public static Map<String, Set<String>> keyAttrMaps = new HashMap<>();

    @Override
    public void onIotEvent(IotEventType eventType, RealtimePayload value) {
        final String type = value.getType();
        // 监听实时数据和状态数据
        if(Objects.equals("data", type) || Objects.equals("status", type)) {
            if(!keyAttrMaps.isEmpty() && CollectionUtil.isNotEmpty(value.getPayload())) {
                final boolean data = Objects.equals(type, "data");
                final boolean status = Objects.equals(type, "status");
                if((status || data)) {
                    keyAttrMaps.forEach((key, attrs) -> {
                        try {
                            if(status) {
                                statusHandle(key, value.getPayload(), attrs);
                            } else {
                                dataHandle(key, value.getPayload(), attrs);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }
    }

    private void dataHandle(String key, Collection payload, Set<String> attrs) {
        final Object[] values = payload.toArray();
        final RealtimeCollectData value = (RealtimeCollectData) values[0];
        final ScriptConfig scriptConfig = ScriptConfigService.instance.getScriptConfig(key);

        // 所有产品字段都处理
        if(attrs.contains(value.getProductCode() + ALL_FIELD)) {
            ScriptManager.exec(scriptConfig.getLanguage(), ScriptContext.build(key, scriptConfig.getScript(), scriptConfig.getInvoke())
                    .addParam("data", value).addParam("methods", LinkageScriptMethodsProvider.INSTANCE));
        } else { // 只处理指定字段
            payload.forEach(realtime -> {
                RealtimeCollectData data = (RealtimeCollectData) realtime;
                if(attrs.contains(data.getProductCode() + SPEC + data.getField())) {
                    ScriptManager.exec(scriptConfig.getLanguage(), ScriptContext.build(key, scriptConfig.getScript(), scriptConfig.getInvoke())
                            .addParam("data", data).addParam("methods", LinkageScriptMethodsProvider.INSTANCE));
                }
            });
        }
    }

    private void statusHandle(String key, Collection payload, Set<String> attrs) {
        final Object[] values = payload.toArray();
        RealtimeDeviceStatus status = (RealtimeDeviceStatus) values[0];
        if(attrs.contains(status.getProductCode()+STATUS)) {
            final ScriptConfig scriptConfig = ScriptConfigService.instance.getScriptConfig(key);
            payload.forEach(data -> {
                ScriptManager.exec(scriptConfig.getLanguage(), ScriptContext.build(key, scriptConfig.getScript(), scriptConfig.getInvoke())
                        .addParam("data", data).addParam("methods", LinkageScriptMethodsProvider.INSTANCE));
            });
        }
    }

    public static Set<String> remove(String key) {
        return keyAttrMaps.remove(key);
    }

    public static void register(String key, ScriptConfig script) {
        final Object exec = ScriptManager.exec(script.getLanguage(), script.getScript()+"\r\nmatcher();", null);
        if(exec instanceof ScriptObjectMirror) {
            Set<String> attrSet = new HashSet<>();
            final JSONObject o = (JSONObject) JSONObject.toJSON(exec);
            o.forEach((productCode, attrsJson) -> {
                final JSONObject attrs = (JSONObject) attrsJson;
                if(attrsJson != null && !attrs.isEmpty()) {
                    attrs.values().forEach(field -> {
                        if(StrUtil.isNotBlank((String) field)) {
                            attrSet.add(productCode + SPEC + field);
                        }
                    });
                } else {
                    throw new ServiceException("产品["+productCode+"]未指定任何配置");
                }
            });
            if(attrSet.isEmpty()) {
                throw new ServiceException("matcher方法未返回任何配置");
            }
            register(key, attrSet);
        } else {
            throw new ServiceException("matcher方法返回值错误");
        }

    }

    public static void register(String key, Map<String, List<String>> attrs) {
        if(CollectionUtil.isNotEmpty(attrs)) {
            Set<String> attrSet = new HashSet<>();
            attrs.forEach((productCode, fields) -> {
                fields.forEach(field -> {
                    if(StrUtil.isNotBlank(field)) {
                        attrSet.add(productCode + SPEC + field);
                    }
                });
            });

            register(key, attrSet);
        }
    }

    public static void register(String key, Set<String> attrs) {
        keyAttrMaps.put(key, attrs);
    }
}
