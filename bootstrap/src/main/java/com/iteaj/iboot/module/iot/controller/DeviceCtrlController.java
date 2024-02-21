package com.iteaj.iboot.module.iot.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.module.iot.consts.DeviceType;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.dto.DebugRespDto;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.service.IDeviceService;
import com.iteaj.iboot.module.iot.service.IModelApiService;
import com.iteaj.iboot.module.iot.service.IProductService;
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

    private final IDeviceService deviceService;
    private final IProductService productService;
    private final IModelApiService modelApiService;
    public DeviceCtrlController(IDeviceService deviceService
            , IProductService productService, IModelApiService modelApiService) {
        this.deviceService = deviceService;
        this.productService = productService;
        this.modelApiService = modelApiService;
    }

    /**
     * 设备控制
     * @param modelApiCode 物模型接口代码
     * @param param 调用参数
     * @return
     */
    @PostMapping("/ctrl/{modelApiCode}")
    public Mono<Result> ctrl(@PathVariable String modelApiCode, @RequestBody ProtocolModelApiInvokeParam param) {
        AbstractProtocolModelApi protocolModelApi = this.getProtocolModelApi(modelApiCode, param);

        return Mono.<InvokeResult>create(item -> {
            protocolModelApi.invoke(param, result -> {
                item.success(result);
            });
        }).timeout(Duration.ofSeconds(10), Mono.error(new ServiceException("执行超时"))).map(item -> {
            if(item.getStatus() == ExecStatus.success) {
                return success(item.getValue());
            } else {
                return fail(item.getReason());
            }
        });
    }

    /**
     * 设备调试接口
     * @return
     */
    @PostMapping("/debug/{modelApiCode}")
    public Mono<Result<DebugRespDto>> debug(@PathVariable String modelApiCode, @RequestBody ProtocolModelApiInvokeParam param) {
        // 获取协议模型接口
        AbstractProtocolModelApi protocolModelApi = this.getProtocolModelApi(modelApiCode, param);

        DebugRespDto debugRespDto = new DebugRespDto(param.getDeviceSn());
        return Mono.<InvokeResult>create(item -> {
            // 调用接口
            protocolModelApi.invoke(param, result -> {
                item.success(result);
                debugRespDto.setRespTime(System.currentTimeMillis());
            });
        }).timeout(Duration.ofSeconds(10), Mono.error(new ServiceException("执行超时"))).map(item -> {
            debugRespDto.setStatus(item.getStatus());
            debugRespDto.setReqMsg(item.getRequestMessage().toString());
            debugRespDto.setRespMsg(item.getResponseMessage().toString());
            if(item.getStatus() == ExecStatus.success) {
                debugRespDto.setValue(item.getValue());
                return success(debugRespDto.setValue(item.getValue()));
            } else {
                return success(debugRespDto.setReason(item.getReason()));
            }
        });
    }

    private AbstractProtocolModelApi getProtocolModelApi(String modelApiCode, ProtocolModelApiInvokeParam param) {
        //查找对应的物模型api
        ModelApi modelApi = modelApiService.getByCode(modelApiCode)
                .ifNotPresentThrow("接口代码不存在[modelApiCode=" + modelApiCode + "]")
                .getData();

        ProductDto product = this.productService.detailById(modelApi.getProductId())
                .ifNotPresentThrow("未找到所属产品").getData();

        if(product.getStatus() == FuncStatus.disabled) {
            throw new ServiceException("请先启用产品["+product.getName()+"]");
        }

        // 网关子设备需要取网关设备
        if(product.getDeviceType() == DeviceType.Child) {
            deviceService.getByDeviceSn(param.getDeviceSn())
                .ifNotPresentThrow("查找不到设备[deviceSn="+param.getDeviceSn()+"]")
                .ifPresent(item -> {
                    if(item.getPid() == null) {
                        throw new ServiceException("此网关子设备没有关联对应的网关设备");
                    }
                    deviceService.getById(item.getPid())
                        .ifNotPresentThrow("查找不到绑定的网关设备")
                        // 设备编号改成父网关设备的编号
                        .ifPresent(parent -> param.setDeviceSn(parent.getDeviceSn()));
            });
        }

        DeviceProtocolSupplier protocolSupplier = ProtocolSupplierManager.get(product.getProtocolCode());
        if(protocolSupplier == null) {
            throw new ServiceException("没有找到协议[protocol=" + product.getProtocolCode() + "]");
        }

        // 查找对应的协议接口
        AbstractProtocolModelApi protocolModelApi = protocolSupplier.getProtocol().getApi(modelApi.getDirect());
        if(protocolModelApi == null) {
            throw new ServiceException("没有找到接口[apiCode="+modelApi.getDirect()+"]");
        }

        return protocolModelApi;
    }
}
