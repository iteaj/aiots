package com.iteaj.iboot.plugin.ai.mcp.iot;

import com.iteaj.framework.spi.iot.data.ModelAttrMeta;
import com.iteaj.framework.spi.iot.data.ModelAttrMetaService;
import com.iteaj.framework.spi.iot.data.ProductModelService;
import com.iteaj.framework.spi.iot.listener.CmdInvokePayload;
import com.iteaj.framework.spi.iot.listener.IotEvenPublisher;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.modelcontextprotocol.server.McpAsyncServerExchange;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * 物联网模块的MCP工具服务，包含设备指令等
 */
@Service
public class IotMcpToolService {

    private final Configuration freemarkerConfig;
    private final ModelAttrMetaService modelAttrMetaService;
    private final ProductModelService productModelService;

    public IotMcpToolService(ModelAttrMetaService modelAttrMetaService
            , @Autowired(required = false) ProductModelService productModelService
            , Configuration freemarkerConfig) {
        this.freemarkerConfig = freemarkerConfig;
        this.modelAttrMetaService = modelAttrMetaService;
        this.productModelService = productModelService;
    }

    /**
     * 调用设备指令
     * @param code 指令码
     * @param uid 设备uid
     */
    @Tool(name = "direct", description = "用于调用aiots(iboot)物联网系统的设备指令, 要求指令码和设备uid必须是系统真实存在的不能凭空捏造")
    public void direct(McpAsyncServerExchange exchange, @ToolParam(description = "指令码") String code
            , @ToolParam(description = "设备uid") String uid) {

        CmdInvokePayload cmd = new CmdInvokePayload(uid, code, 15 * 1000);
        IotEvenPublisher.publish(IotEventType.CmdInvoke, cmd.build(invokeResult -> {
            this.notify();
        }));
//        return Mono.<InvokeResult>create(item -> {
//            try {
//                IotEvenPublisher.publish(IotEventType.CmdInvoke, cmd.build(item::success));
//            } catch (ProtocolInvokeException e) {
//                item.success(InvokeResult.fail(e.getMessage(), null));
//            }
//        }).timeout(Duration.ofMillis(cmd.getTimeout()), Mono.just(InvokeResult.fail("执行超时", null))).map(item -> {
//            if(item.getStatus() == ExecStatus.success) {
//                return Result.success(item.getValue(), "执行指令成功");
//            } else {
//                return Result.fail(item.getReason());
//            }
//        });
    }

    /**
     * 查询指定产品下的指令集和设备列表
     * @param productCode 产品码
     */
    @Tool(name = "queryDirectConfig", description = "用于查询aiots(iboot)物联网系统指定产品下的指令码列表和设备列表")
    public String queryDirectConfig(@ToolParam(description = "产品码") String productCode) {
        Object productDetail = productModelService.getProductDetail(productCode);
        if (productDetail == null) {
            return "未找到产品[" + productCode + "]的信息";
        }

        try {
            Template template = freemarkerConfig.getTemplate("templates/iot/product-info.ftl");
            StringWriter writer = new StringWriter();
            template.process(productDetail, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            throw new RuntimeException("产品信息模板解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 查询产品的物模型属性列表
     * @param productCode 产品码
     */
    @Tool(name = "queryModelAttrs", description = "用于查询aiots(iboot)系统下指定产品的物模型属性信息")
    public Collection<ModelAttrMeta> queryModelAttrs(@ToolParam(description = "产品码") String productCode) {
        Map<Serializable, ModelAttrMeta> attrMetas = modelAttrMetaService.getAttrMetas(productCode);
        if(attrMetas != null) {
            return attrMetas.values();
        }

        return Collections.emptyList();
    }

    /**
     * 查询产品的指令列表
     * @param productCode 产品码
     */
    @Tool(name = "queryModelApis", description = "用于查询aiots(iboot)系统下指定产品的物模型指令信息")
    public void queryModelApis(@ToolParam(description = "产品码") String productCode) {
        System.out.println("test");
    }
}
