package com.iteaj.framework;

import com.iteaj.framework.logger.LoggerManager;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.admin.ResourceManager;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * 框架层提供的功能
 */
@Controller
@RequestMapping("/core/framework")
public class FrameworkController extends BaseController{

    @Resource
    private ResourceManager resourceManager;

    /**
     * 日志过滤列表
     * @return 列表
     */
    @ResponseBody
    @GetMapping("loggers")
    public Result<List<IVOption>> loggers() {
        return success(LoggerManager.filterOptions());
    }

    /**
     * 系统常用配置
     * @return
     */
    @ResponseBody
    @GetMapping("config")
    public Result<List<IVOption>> config() {
        List<IVOption> result = new ArrayList<>();
        List<IVOption> indicatorColors = resourceManager.getDictData("iot_indicator_color");

        result.add(new IVOption("iotIndicatorColor", indicatorColors));
        return success(result);
    }
}
