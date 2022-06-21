package com.iteaj.iboot.msn.iot.debug;

import com.alibaba.fastjson.JSON;
import org.springframework.core.GenericTypeResolver;

public interface DebugHandle<P extends DebugModel> {

    /**
     * 调试类型
     * @return
     */
    String type();

    /**
     * 解析json格式的model对象
     * @param model
     * @return
     */
    default P parse(String model) {
        Class<P> typeArgument = (Class<P>) GenericTypeResolver
                .resolveTypeArguments(getClass(), DebugHandle.class)[0];
        return JSON.parseObject(model, typeArgument);
    }

    /**
     *
     * @param model
     * @param write
     */
    void handle(P model, DebugWebsocketWrite write);
}
