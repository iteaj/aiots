package com.iteaj.iboot.plugin.script.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.script.ScriptComplete;
import com.iteaj.framework.spi.iot.DataValueResolverFactory;
import com.iteaj.iboot.plugin.script.entity.Script;
import com.iteaj.iboot.plugin.script.model.ScriptModelAttrResolver;
import com.iteaj.iboot.plugin.script.service.IScriptService;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 模型属性脚本解析
 * @author iteaj
 * @since 2025-04-11
 */
@RestController
@RequestMapping("/iot/script/attr")
public class ModelAttrScriptController extends BaseController {

    private final IScriptService scriptService;
    private List<ScriptComplete> completes = Arrays.asList();

    public ModelAttrScriptController(IScriptService scriptService) {
        this.scriptService = scriptService;
    }

    /**
     * 自动不全列表
     * @return
     */
    @GetMapping("completes")
    public Result<List<ScriptComplete>> completes() {
        return success(completes);
    }

    /**
     * 获取过滤功能的脚本列表
     * @return
     */
    @GetMapping("view")
    public Result<Page<Script>> view(Page page) {
        return scriptService.page(page, Wrappers.<Script>lambdaQuery().eq(Script::getType, Script.TYPE_MODEL_ATTR));
    }

    /**
     * 更新或者保存记录
     * @param entity
     * @return
     */
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody Script entity) {
        return this.scriptService.saveOrUpdate(entity).ifPresentAndTrue(status -> {
            DataValueResolverFactory.update(entity.getName(), new ScriptModelAttrResolver(entity.getName(), null));
        });
    }
    
}
