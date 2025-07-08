package com.iteaj.iboot.plugin.iotview.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.view.*;
import com.iteaj.framework.spi.open.OpenApiUtil;
import com.iteaj.iboot.plugin.iotview.FetchResult;
import com.iteaj.iboot.plugin.iotview.IotViewResult;
import com.iteaj.iboot.plugin.iotview.service.ISVDataService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 大屏数据管理
 */
@Controller
@RequestMapping("/isv/data")
public class ISVDataController extends BaseController {

    private final ISVDataService isvDataService;
    public ISVDataController(ISVDataService isvDataService) {
        this.isvDataService = isvDataService;
    }

    /**
     * 数据项配置
     * @return
     */
    @ResponseBody
    @GetMapping("options")
    public Result<List<DataItemOption>> options() {
        return IotViewResult.Success(ViewApiManager.options());
    }

    /**
     * 表格数据项配置
     * @return
     */
    @ResponseBody
    @GetMapping("table/options")
    public Result<List<DataItemOption>> tableOptions() {
        return IotViewResult.Success(ViewApiManager.options());
    }

    /**
     * 全局接口数据项配置
     * @return
     */
    @ResponseBody
    @GetMapping("global/options")
    public Result<List<DataItemOption>> globalOptions() {
        return IotViewResult.Success(ViewApiManager.globalApiOptions());
    }

    /**
     * 表格数据项配置
     * @return
     */
    @ResponseBody
    @GetMapping("device/options")
    public Result<List<DataItemOption>> deviceOptions(String productCode) {
        final List<DataItemOption> dataItemOptions = isvDataService.listDeviceOptions(productCode);
        return IotViewResult.Success(dataItemOptions);
    }

    /**
     * 物模型属性等项配置
     * @return
     */
    @ResponseBody
    @GetMapping("realtime/options")
    public Result<List<DataItemOption>> realtimeOptions(String key) {
        List<DataItemOption> results = new ArrayList<>();
        if(StrUtil.isBlank(key)) {
            results.add(new DataItemOption("物模型属性", ViewRealtimeData.Attr_PREFIX));
            results.add(new DataItemOption("设备状态", ViewRealtimeData.STATUS_PREFIX));
            final List<DataItemOption> attrProduct = isvDataService.listProductOption(false);
            if(CollectionUtil.isNotEmpty(attrProduct)) {
                attrProduct.forEach(dataItemOption -> results.get(0).addChild(dataItemOption, false));
            }
            final List<DataItemOption> deviceProduct = isvDataService.listProductOption(true);
            if(CollectionUtil.isNotEmpty(deviceProduct)) {
                deviceProduct.forEach(dataItemOption -> results.get(1).addChild(dataItemOption.setLeaf(true), false));
            }
        } else {
            final String[] split = key.split(DataItemOption.SPLIT);
            if(key.startsWith(ViewRealtimeData.Attr_PREFIX)) {
                final List<DataItemOption> itemOptions = isvDataService.listModelAttrOption(split[1]);
                if(CollectionUtil.isNotEmpty(itemOptions)) {
                    itemOptions.forEach(item -> results.add(item.setValue(key+DataItemOption.SPLIT+item.getValue()).setLeaf(true)));
                }
            }
        }
        return IotViewResult.Success(results);
    }

    /**
     * 加载配置项
     * @return
     */
    @ResponseBody
    @GetMapping("load")
    public Result<List<DataItemOption>> load(String key) {
        return IotViewResult.Success(ViewApiManager.loadOptions(key));
    }

    /**
     * 表格加载配置项
     * @return
     */
    @ResponseBody
    @GetMapping("table/load")
    public Result<List<DataItemOption>> tableLoad(String key) {
        return IotViewResult.Success(ViewApiManager.loadOptions(key));
    }

    /**
     * 基础范围项
     * @return
     */
    @ResponseBody
    @GetMapping("/range/basic")
    public Result<List<DataItemOption>> basic() {
        List<DataItemOption> result = new ArrayList<>();
        final DataItemOption product = new DataItemOption("按产品", "product").asName();
        final DataItemOption group = new DataItemOption("按分组", "group").asName();
        final DataItemOption device = new DataItemOption("按设备", "device").asName();
        result.add(product);
        result.add(group);
        result.add(device);
        result.add(0, new DataItemOption("按所有", "all", true).asName());
        final List<DataItemOption> groupOptions = isvDataService.listGroupOption();
        final List<DataItemOption> productOptions = isvDataService.listProductOption(true);
        if(CollectionUtil.isNotEmpty(groupOptions)) {
            groupOptions.forEach(item -> group.addChild(item.setLeaf(true).asName()));
        }

        if(CollectionUtil.isNotEmpty(productOptions)) {
            productOptions.forEach(item -> {
                try {
                    product.addChild(item.clone().asName().setLeaf(true));
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            });

            productOptions.forEach(item -> {
                try {
                    device.addChild(item.clone().asName());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            });
        }

        return IotViewResult.Success(result);
    }

    /**
     * 加载范围配置项
     * @return
     */
    @ResponseBody
    @GetMapping("range/load")
    public Result<List<DataItemOption>> rangeLoad(String key) {
        final String[] args = key.split(DataItemOption.SPLIT);
        if(Objects.equals(args[0], "device")) {
            List<DataItemOption> devices = isvDataService.listDeviceOptions(args[1]);
            if(CollectionUtil.isNotEmpty(devices)) {
                devices.forEach(item -> {
                    item.setValue(key + DataItemOption.SPLIT + item.getValue()).setLeaf(true);
                });

                return IotViewResult.Success(devices);
            }
        }
        return IotViewResult.Success(Collections.EMPTY_LIST);
    }

    /**
     * 获取数据
     * @param params
     * @return
     */
    @ResponseBody
    @PostMapping("fetch")
    public Result<List<ViewFetchResult>> fetch(@RequestBody ViewApiParams params) {
        try {
            final List<ViewFetchResult> results = ViewApiManager.fetch(params);
            return FetchResult.builder(params.getChart(), IotViewResult.Success(results));
        } catch (ServiceException e) {
            return FetchResult.FailNotJump(e.getMessage());
        } catch (Exception e) {
            logger.error("fetch接口未知异常", e);
            return FetchResult.FailNotJump("获取数据异常");
        }
    }


    /**
     * 获取全局接口数据
     * @param params
     * @return
     */
    @ResponseBody
    @PostMapping("global/fetch")
    public Result<GlobalApiParams> globalFetch(@RequestBody GlobalApiParams params) {
        try {
            final GlobalApiParams response = ViewApiManager.fetch(params);
            return IotViewResult.Success(response);
        } catch (ServiceException e) {
            return FetchResult.FailNotJump(e.getMessage());
        } catch (Exception e) {
            logger.error("fetch接口未知异常", e);
            return FetchResult.FailNotJump("获取数据异常");
        }
    }

    /**
     * 获取产品项列表
     * @param gateway 是否包含网关设备
     * @return
     */
    @ResponseBody
    @GetMapping("products")
    public Result<List<DataItemOption>> products(Boolean gateway) {
        return IotViewResult.Success(isvDataService.listProductOption(gateway));
    }

    /**
     * 获取指令集列表
     * @param request
     * @return
     */
    @GetMapping("directs")
    public String directs(HttpServletRequest request) {
        OpenApiUtil.buildOpenRequest(request);
        return "forward:/iot/product/commands/view";
    }

    /**
     * 执行设备指令
     * @param request
     * @return
     */
    @PostMapping("cmd")
    public String cmd(HttpServletRequest request) {
        OpenApiUtil.buildOpenRequest(request);
        OpenApiUtil.buildPassRequest(request);
        return "forward:/iot/instruct/command/invoke";
    }
}
