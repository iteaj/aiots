package com.iteaj.iboot.module.iot.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.module.iot.service.IProtocolService;
import com.iteaj.iot.consts.ExecStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 设备指令接口管理
 */
@RestController
@RequestMapping("/iot/instruct")
public class DeviceCtrlController extends BaseController {

    private final IProtocolService protocolService;

    public DeviceCtrlController(IProtocolService protocolService) {
        this.protocolService = protocolService;
    }

    /**
     * 设备控制
     * @param api 协议接口代码
     * @param protocol 协议代码
     * @param param 调用参数
     * @return
     */
    @PostMapping("/ctrl/{protocol}/{api}")
    public Mono<Result> ctrl(@PathVariable String protocol, @PathVariable String api, @RequestBody ProtocolModelApiInvokeParam param) {
        //查找对应的协议
        DeviceProtocolSupplier protocolSupplier = protocolService.getByCode(protocol)
                .ifNotPresentThrow("没有找到协议[protocol=" + protocol + "]")
                .of().map(item -> ProtocolSupplierManager.get(item.getCode()))
                .orElseThrow(() -> new ServiceException("没有找到协议[protocol=" + protocol + "]"));

        // 查找对应的协议接口
        AbstractProtocolModelApi modelApi = protocolSupplier.getProtocol().getApi(api);
        if(modelApi == null) {
            throw new ServiceException("没有找到接口[api="+api+"]");
        }

        return Mono.<InvokeResult>create(item -> {
            modelApi.invoke(param, result -> {
                item.success(result);
            });
        }).timeout(Duration.ofSeconds(20)).map(item -> {
            if(item.getStatus() == ExecStatus.success) {
                return success(item.getValue());
            } else {
                return fail(item.getReason());
            }
        });
    }

    /**
     * 设备调试
     * @return
     */
    @PostMapping("/debug/{protocol}/{api}")
    public Result debug(@PathVariable String protocol, @PathVariable String api, @RequestBody ProtocolModelApiInvokeParam param) {
        return success();
    }
}
