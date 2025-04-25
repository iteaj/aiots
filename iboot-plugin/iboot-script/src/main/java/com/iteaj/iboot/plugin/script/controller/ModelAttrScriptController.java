package com.iteaj.iboot.plugin.script.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.script.ScriptComplete;
import com.iteaj.framework.spi.iot.DataValueResolverFactory;
import com.iteaj.framework.script.ScriptConfig;
import com.iteaj.framework.script.ScriptLanguage;
import com.iteaj.iboot.plugin.script.entity.Script;
import com.iteaj.iboot.plugin.script.model.ScriptModelAttrResolver;
import com.iteaj.iboot.plugin.script.service.IScriptService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 模型属性脚本解析
 * @author iteaj
 * @since 2025-04-11
 */
@RestController
@RequestMapping("/iot/script/attr")
public class ModelAttrScriptController extends BaseController implements InitializingBean {

    private final IScriptService scriptService;
    private final static String KEY_PREFIX = "ATTR:";
    private List<ScriptComplete> completes = Arrays.asList();
    private final List<ScriptConfig> configs = Arrays.asList(
            new ScriptConfig(ScriptLanguage.javascript.getName(), "" +
                    "/**\r\n" +
                    "* 支持ECMAScript 5.1规范\r\n" +
                    "* @param value 采集的值\r\n" +
                    "* @param model 模型属性配置\r\n" +
                    "* @param methods 常用方法\r\n" +
                    "* @return Object 解析后的值\r\n" +
                    "*/\r\n" +
                    "function resolver(value, model, methods) {\r\n" +
                    "    return value;\r\n" +
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
            final String key = KEY_PREFIX + entity.getId();
            if(StrUtil.isNotBlank(entity.getLanguage())) {
                if(entity.getConfigs() != null) {
                    entity.getConfigs().forEach(item -> {
                        if(Objects.equals(item.getLanguage(), entity.getLanguage())) {
                            DataValueResolverFactory.update(key, new ScriptModelAttrResolver(entity.getName(), item));
                        }
                    });
                }
            } else {
                DataValueResolverFactory.remove(key);
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

        if(ids.size() > 0) {
            return fail("不支持批量删除");
        }

        final Script data = this.scriptService.getById(ids.get(0)).ifNotPresentThrow("记录不存在").getData();
        Boolean isJoin = this.scriptService.isJoinModelAttr(KEY_PREFIX + data.getId());

        return Boolean.TRUE.equals(isJoin) ? fail("此脚本已被使用, 请先删除关联") :
                this.scriptService.removeById(data.getId()).ifPresent((status) ->
                        DataValueResolverFactory.remove(KEY_PREFIX + data.getId()));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        scriptService.listByType(Script.TYPE_MODEL_ATTR).ifPresent(scripts -> {
            scripts.forEach(item -> {
                item.getConfigs().forEach(config -> {
                    if(Objects.equals(config.getLanguage(), item.getLanguage())) {
                        DataValueResolverFactory.register(KEY_PREFIX + item.getId(), new ScriptModelAttrResolver(item.getName(), config));
                    }
                });
            });
        });
    }
}
