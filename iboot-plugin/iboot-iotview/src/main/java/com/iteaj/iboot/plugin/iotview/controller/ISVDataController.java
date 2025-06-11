package com.iteaj.iboot.plugin.iotview.controller;

import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.iot.view.ViewApiManager;
import com.iteaj.iboot.plugin.iotview.IotViewResult;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.iboot.plugin.iotview.service.ISVDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 大屏数据管理
 */
@RestController
@RequestMapping("/isv/data")
public class ISVDataController {

    private final ISVDataService ISVDataService;

    public ISVDataController(ISVDataService ISVDataService) {
        this.ISVDataService = ISVDataService;
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
}
