package com.iteaj.iboot.module.iot.service;

import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.data.ModelAttrMeta;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.iboot.module.iot.dto.CmdInvokeUpdateDto;
import com.iteaj.iboot.module.iot.dto.DeviceCommandDto;
import com.iteaj.iboot.module.iot.dto.ModelApiInvokeContext;
import com.iteaj.iboot.module.iot.dto.ModelAttrContext;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 物模型接口调用服务
 */
public interface IModelApiInvokeService {

    /**
     * 解析 modelApiCode 为完整的调用上下文
     */
    ModelApiInvokeContext resolve(String modelApiCode, ProtocolModelApiInvokeParam param);

    /**
     * 执行接口调用（含设备在线检查 + 超时处理）
     */
    Mono<InvokeResult> invoke(ModelApiInvokeContext context, ProtocolModelApiInvokeParam param, long timeoutMillis);

    /**
     * 获取设备指令集列表
     * @param uid 设备uid
     * @param apiType 指令类型，为空获取所有
     */
    List<DeviceCommandDto> listCommands(String uid, ProtocolApiType apiType);

    /**
     * 通过事件发布方式执行指令调用（含实时数据更新）
     */
    Mono<InvokeResult> invokeByEvent(CmdInvokeUpdateDto dto);

    /**
     * 直接协议调试（不经过物模型，直接指定协议指令）
     */
    Mono<InvokeResult> debugInvoke(ProtocolModelApiInvokeParam param);

    /**
     * 解析物模型属性
     * @param attrs 属性列表
     * @return 属性上下文
     */
    ModelAttrContext resolver(List<? extends ModelAttrMeta> attrs);

    /**
     * 将属性上下文转换为属性值列表
     * @param uid 设备uid
     * @param context 属性上下文
     * @return 属性值列表
     */
    List<ModelAttrContext.ModelAttrValue> conver(String uid, ModelAttrContext context);
}
