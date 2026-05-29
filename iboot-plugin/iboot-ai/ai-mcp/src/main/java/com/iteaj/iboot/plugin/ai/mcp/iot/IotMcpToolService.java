package com.iteaj.iboot.plugin.ai.mcp.iot;

import com.iteaj.framework.spi.iot.data.*;
import com.iteaj.framework.spi.iot.listener.CmdInvokePayload;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.protocol.InvokeResult;
import com.iteaj.iboot.plugin.ai.mcp.dto.DeviceDriverDto;
import com.iteaj.iboot.plugin.ai.mcp.dto.DeviceGroupInfoDto;
import com.iteaj.iboot.plugin.ai.mcp.dto.ProductAiDto;
import com.iteaj.iboot.plugin.ai.mcp.service.IotInfoService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * 物联网模块的MCP工具服务，包含设备指令等
 */
@RestController
@RequestMapping("/ai/mcp")
public class IotMcpToolService {

    private final IotInfoService iotInfoService;
    private final Configuration freemarkerConfig;
    private final ProductModelService productModelService;
    private final IModelAttrRealtimeDataService modelAttrRealtimeDataService;
    private final Logger logger = LoggerFactory.getLogger(IotMcpToolService.class);
    public IotMcpToolService(IotInfoService iotInfoService, ProductModelService productModelService, Configuration freemarkerConfig
            , IModelAttrRealtimeDataService modelAttrRealtimeDataService) {
        this.iotInfoService = iotInfoService;
        this.freemarkerConfig = freemarkerConfig;
        this.productModelService = productModelService;
        this.modelAttrRealtimeDataService = modelAttrRealtimeDataService;
    }

    static class WaitLock {
        String status; // waiting, finished
        InvokeResult result;
    }

    /**
     * 调用设备指令
     * @param code 指令码
     * @param uid 设备uid
     */
    @Tool(name = "direct", description = "用于调用aiots(iboot)物联网系统的设备指令, 要求指令码和设备uid必须是系统真实存在的不能凭空捏造")
    public String direct(@ToolParam(description = "指令码") String code, @ToolParam(description = "设备uid") String uid) {

        CmdInvokePayload cmd = new CmdInvokePayload(uid, code, 15 * 1000).setScenario(CmdInvokePayload.SCENARIO_AI);
        final WaitLock waitLock = new WaitLock();
        IotEvenPublisher.publish(IotEventType.CmdInvoke, cmd.build(invokeResult -> {
            if(waitLock.status != null) {
                waitLock.notify();
            }

            waitLock.status = "finished";
            waitLock.result = invokeResult;
        }));

        if(waitLock.status == null) {
            synchronized (waitLock) {
                try {
                    waitLock.status = "waiting"; // 已经在等待
                    waitLock.wait(15 * 1000);
                } catch (Exception e) {
                    logger.error("等待指令执行结果时发生异常", e);
                    return "执行超时";
                }
            }
        }

        return waitLock.result.getStatus() != null ? waitLock.result.getStatus().desc : "执行失败";
    }

    /**
     * 查询指定产品下的指令集和设备列表
     * @param productCode 产品码
     */
    @Tool(name = "queryDirectConfig", description = "用于查询aiots(iboot)物联网系统指定产品下的指令码列表和设备列表")
    public String queryDirectConfig(@ToolParam(description = "产品码") String productCode) {
        try {
            ProductModelService.ProductModel productModel = productModelService.getProductModel(productCode);
            if (productModel == null) {
                return "未找到产品[" + productCode + "]的信息";
            }

            Collection<? extends RealtimeDeviceStatus> devices = modelAttrRealtimeDataService.listByProductCode(productCode);
            List<DeviceGroupInfoDto> infoDtos = iotInfoService.listDeviceGroupInfo(null, null);
            Map<String, String> groupMap = new HashMap<>();
            if(infoDtos != null) {
                infoDtos.forEach(dto -> groupMap.put(dto.getId(), dto.getName()));
            }
            Map<String, Object> context = new HashMap<>();
            context.put("devices", devices);
            context.put("groupMap", groupMap);
            context.put("product", productModel);
            return resolverTemplate("iot/product-model.ftl", context);
        } catch (Exception e) {
            logger.error("产品信息模板解析失败", e);
           return "获取产品模型信息(指令码和设备列表)失败";
        }
    }

    /**
     * 查询系统下面的设备组信息
     * @param groupId 设备组id
     */
    @Tool(name = "queryDeviceGroup", description = "用于查询aiots(iboot)物联网系统下的设备组信息, 每台设备都属于某个设备组(比如家庭、办公室、厂房等)")
    public String queryDeviceGroup(@ToolParam(description = "设备组id", required = false) Long groupId
            , @ToolParam(description = "设备组名", required = false) String groupName) {
        try {
            List<DeviceGroupInfoDto> infoDtos = iotInfoService.listDeviceGroupInfo(groupId, groupName);

            Map<String, Object> context = new HashMap<>();
            context.put("groups", infoDtos);
            return resolverTemplate("iot/deviceGroup.ftl", context);
        } catch (Exception e) {
            logger.error("设备组信息模板解析失败", e);
            return "获取设备组信息失败";
        }
    }

    private String resolverTemplate(String name, Map<String, Object> context) throws IOException, TemplateException {
        Template template = freemarkerConfig.getTemplate(name);
        StringWriter writer = new StringWriter();
        template.process(context, writer);
        return writer.toString();
    }

    /**
     * 查询系统所有产品
     * @param productCode 产品码
     */
    @Tool(name = "queryProducts", description = "用于查询aiots(iboot)物联网系统下所有产品信息")
    public String queryProducts(@ToolParam(description = "产品码", required = false) String productCode
            , @ToolParam(description = "产品名称", required = false) String productName) {
        ProductAiDto productAiDto = new ProductAiDto();
        productAiDto.setCode(productCode).setName(productName);
        try {
            List<? extends ProductMeta> productMetas = productModelService.listProducts(productAiDto);
            if(productMetas != null) {
                Map<String, Object> context = new HashMap<>();
                context.put("products", productMetas);
                return resolverTemplate("iot/product.ftl", context);
            } else {
                return "未找到产品信息";
            }
        } catch (Exception e) {
            logger.error("获取产品信息列表失败", e);
            return "获取产品信息列表失败";
        }
    }

    /**
     * 查询系统所有驱动
     */
    @Tool(name = "queryDrivers", description = "用于查询aiots系统下所有设备驱动信息")
    public String queryDrivers() {
        try {
            List<? extends DeviceDriverDto> deviceProtocolMetas = iotInfoService.listDeviceProtocol();
            if(deviceProtocolMetas != null) {
                Map<String, Object> context = new HashMap<>();
                context.put("drivers", deviceProtocolMetas);
                return resolverTemplate("iot/driver.ftl", context);
            } else {
                return "未找到驱动信息";
            }
        } catch (Exception e) {
            logger.error("获取驱动信息列表失败", e);
            return "获取驱动信息列表失败";
        }
    }

}
