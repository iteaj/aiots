package com.iteaj.iboot.plugin.iotview.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.view.ViewApiManager;
import com.iteaj.framework.spi.iot.view.ViewApiParams;
import com.iteaj.framework.spi.iot.view.ViewFetchResult;
import com.iteaj.iboot.plugin.iotview.FetchResult;
import com.iteaj.iboot.plugin.iotview.IotViewResult;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.iboot.plugin.iotview.service.ISVDataService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 大屏数据管理
 */
@RestController
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
    @GetMapping("options")
    public Result<List<DataItemOption>> options() {
        return IotViewResult.Success(ViewApiManager.options());
    }

    /**
     * 加载配置项
     * @return
     */
    @GetMapping("load")
    public Result<List<DataItemOption>> load(String key) {
        return IotViewResult.Success(ViewApiManager.loadOptions(key));
    }

    /**
     * 基础范围项
     * @return
     */
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
}
