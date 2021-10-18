package com.iteaj.code.gen.controller;

import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.admin.DictResource;
import com.iteaj.framework.spi.admin.ResourceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 在线低代码表单设计
 */
@RestController
@RequestMapping("/lcd/online")
public class OnlineGeneratorController extends BaseController {

    @Autowired
    private ResourceManager resourceManager;

    /**
     * 获取字典类型列表
     * @return
     */
    @GetMapping("dictType")
    public Result<List<DictResource>> dictType() {
        return success(resourceManager.getDictResources());
    }
}
