package com.iteaj.msn.lcd.controller;

import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.admin.DictResource;
import com.iteaj.framework.spi.admin.ResourceManager;
import com.iteaj.msn.lcd.LowCodeProperties;
import com.iteaj.msn.lcd.dto.ReviewDto;
import com.iteaj.msn.lcd.gen.DesignConfigBuilder;
import com.iteaj.util.CommonUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 在线低代码表单设计
 */
@RestController
@RequestMapping("/lcd/online")
public class OnlineGeneratorController extends BaseController {

    private final ResourceManager resourceManager;
    private final LowCodeProperties properties;

    public OnlineGeneratorController(ResourceManager resourceManager, LowCodeProperties properties) {
        this.resourceManager = resourceManager;
        this.properties = properties;
    }

    /**
     * 获取字典类型列表
     * @return
     */
    @GetMapping("dictType")
    public Result<List<DictResource>> dictType() {
        return success(resourceManager.getDictResources());
    }

    /**
     * 预览生成的源代码
     * @param tableName
     * @return
     */
    @GetMapping("review")
    public Result<ReviewDto> review(String tableName) {
        if(CommonUtils.isBlank(tableName)) {
            return fail("请指定要预览的表");
        }

        DesignConfigBuilder configBuilder = new DesignConfigBuilder(properties.getLpc(), properties.getLdc()
                , properties.getLsc(), new TemplateConfig(), properties.getLgc(), Arrays.asList(tableName));

        return success(new ReviewDto());
    }
}
