package com.iteaj.iboot.module.iot.dto;

import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 物模型接口调用上下文，包含解析后的完整调用链
 */
@Getter
@Setter
@Accessors(chain = true)
public class ModelApiInvokeContext {

    /** 协议层接口 */
    private AbstractProtocolModelApi protocolModelApi;

    /** 物模型接口 */
    private ModelApi modelApi;

    /** 所属产品 */
    private ProductDto product;

    /** 协议供应商 */
    private DeviceProtocolSupplier<?, ?> supplier;

    /** 设备实时状态 */
    private RealtimeStatus device;
}
