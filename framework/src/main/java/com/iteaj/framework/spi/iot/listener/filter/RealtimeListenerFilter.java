package com.iteaj.framework.spi.iot.listener.filter;

import com.iteaj.framework.script.ScriptComplete;
import com.iteaj.framework.script.ScriptConfigService;
import com.iteaj.framework.script.ScriptContext;
import com.iteaj.framework.script.ScriptManager;
import com.iteaj.framework.spi.iot.listener.RealtimePayload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 实时数据监听的过滤
 */
public interface RealtimeListenerFilter extends ListenerScriptFilter<RealtimePayload> {

    String FILTER_DATABASE = "数据入库";
    String FILTER_MQTT_FORWARD = "MQTT转发";

    RealtimeMethodProvide methodProvide = new RealtimeMethodProvide();


    @Override
    default String getScriptRemark() {
        return "";
    }

    @Override
    default List<ScriptConfig> getScripts() {
        return Arrays.asList(new ScriptConfig(ScriptLanguage.javascript.getName(),
                "/**\r\n" +
                "* 数据过滤\r\n" +
                "* 支持ECMAScript 5.1规范\r\n" +
                "* @param data 采集的数据或设备的状态数据\r\n" +
                "* @param methods 过滤器提供的方法\r\n" +
                "* @return boolean\r\n" +
                "*/\r\n" +
                "function filter(data, methods) {\r\n" +
                "   return true;" +
                "\r\n}"));
    }

    @Override
    default RealtimePayload filter(RealtimePayload param) {
        if(isFilter(param.getType(), param)) {
            final ScriptConfig scriptConfig = ScriptConfigService.instance.getScriptConfig(getFilterName());
            if(scriptConfig != null) { // 包含过滤脚本
                List payload = new ArrayList();
                param.getPayload().forEach(item -> {
                    // 执行脚本
                    final Object result = ScriptManager.exec(ScriptLanguage.javascript.getName()
                            , ScriptContext.build(scriptConfig.getScript(), "filter(data, methods)")
                                    .addParam("data", item).addParam("methods", methodProvide)
                                    .addParam("console", LOGGER_PROVIDE));

                    // 校验执行结果, 是否被过滤
                    if(Boolean.TRUE.equals(result)) {
                        payload.add(item);
                    }
                });
                return new RealtimePayload(param.getType(), payload);
            }
        }

        return param;
    }

    boolean isFilter(String type, RealtimePayload payload);

    @Override
    default List<ScriptComplete> getCompletes() {
        return RealtimeMethodProvide.completes;
    }
}
