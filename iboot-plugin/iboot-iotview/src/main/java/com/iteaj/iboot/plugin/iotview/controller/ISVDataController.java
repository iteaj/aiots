package com.iteaj.iboot.plugin.iotview.controller;

import com.iteaj.framework.result.Result;
import com.iteaj.iboot.plugin.iotview.IotViewResult;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import com.iteaj.iboot.plugin.iotview.service.IVSDataService;
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

    private final IVSDataService ivsDataService;

    public ISVDataController(IVSDataService ivsDataService) {
        this.ivsDataService = ivsDataService;
    }

    /**
     * 数据项配置
     * @return
     */
    @GetMapping("options")
    public Result<List<DataItemOption>> options() {
        List<DataItemOption> options = new ArrayList<>();
        options.add(new DataItemOption("设备状态", "STATUS")
                .addChild(new DataItemOption("在线", "online")
                        .addChild(new DataItemOption("统计", "count", true, true))
                        .addChild(new DataItemOption("占比", "ratio", true, true)))
                .addChild(new DataItemOption("离线", "offline")
                        .addChild(new DataItemOption("统计", "count", true, true))
                        .addChild(new DataItemOption("占比", "ratio", true, true))));
        options.add(new DataItemOption("模型属性", "MODEL", "/isv/data/model"));
        return IotViewResult.Success(options);
    }

    /**
     * 物模型
     * @return
     */
    @GetMapping
    public Result<List<DataItemOption>> model() {
        return null;
    }
}
