package com.iteaj.iboot.plugin.script.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.script.ScriptComplete;
import com.iteaj.framework.spi.iot.DataValueResolverFactory;
import com.iteaj.framework.spi.iot.listener.filter.ScriptConfig;
import com.iteaj.framework.spi.iot.listener.filter.ScriptLanguage;
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
    private final List<ScriptConfig> configs = Arrays.asList(
            new ScriptConfig(ScriptLanguage.javascript.getName(), "" +
                    "/**\r\n" +
                    "* @param value 采集的值\r\n" +
                    "* @param model 模型属性配置\r\n" +
                    "* @return Object 解析后的值\r\n" +
                    "*/\r\n" +
                    "function resolver(value, model) {\r\n" +
                    "   return value;\r\n" +
                    "}\r\n" +
                    "")
    );

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
     * 获取脚本配置
     * @return
     */
    @GetMapping("configs")
    public Result<List<ScriptConfig>> configs() {
        return success(configs);
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
        entity.setType(Script.TYPE_MODEL_ATTR);
        return this.scriptService.saveOrUpdate(entity).ifPresentAndTrue(status -> {
            if(StrUtil.isNotBlank(entity.getLanguage())) {
                if(entity.getConfigs() != null) {
                    entity.getConfigs().forEach(item -> {
                        if(Objects.equals(item.getLanguage(), entity.getLanguage())) {
                            DataValueResolverFactory.update(entity.getName(), new ScriptModelAttrResolver(entity.getName(), item));
                        }
                    });
                }
            } else {
                DataValueResolverFactory.remove(entity.getName());
            }
        });
    }

    /**
     * 删除记录
     * @param ids
     * @return
     */
    @PostMapping("del")
    public Result del(@RequestBody List<Long> ids) {
        if(ids.isEmpty()) {
            return fail("未指定要删除的记录");
        }

        this.scriptService.listByIds(ids).ifPresent(scripts -> {
            this.scriptService.removeByIds(ids);
            scripts.forEach(script -> DataValueResolverFactory.remove(script.getName()));
        });

        return success("删除成功");
    }
}
