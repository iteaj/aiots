package com.iteaj.iboot.plugin.script.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.script.ScriptCmdMethodsProvider;
import com.iteaj.framework.script.ScriptComplete;
import com.iteaj.framework.script.ScriptConfig;
import com.iteaj.framework.script.ScriptLanguage;
import com.iteaj.framework.spi.iot.DataValueResolverFactory;
import com.iteaj.iboot.plugin.script.entity.BindDto;
import com.iteaj.iboot.plugin.script.entity.Script;
import com.iteaj.iboot.plugin.script.service.IScriptService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 模型接口脚本解析
 * @author iteaj
 * @since 2025-04-11
 */
@RestController
@RequestMapping("/iot/script/api")
public class ModelApiScriptController extends BaseController {

    private final IScriptService scriptService;
    private List<ScriptComplete> completes = Arrays.asList();
    private final List<ScriptConfig> configs = Arrays.asList(
            new ScriptConfig(ScriptLanguage.javascript.getName(), "" +
                    "/**\r\n" +
                    "* 写值\r\n" +
                    "* 支持ECMAScript 5.1规范\r\n" +
                    "* @param data 调用功能接口的参数\r\n" +
                    "* @param methods 常用方法\r\n" +
                    "* 通过调用methods.writeValue(data, 要写入的值)方法将值写入\r\n" +
                    "*/\r\n" +
                    "function write(data, methods) {\r\n" +
                    "     methods.writeValue(data, '');\r\n" +
                    "}\r\n" +
                    "")
    );

    public ModelApiScriptController(IScriptService scriptService) {
        this.scriptService = scriptService;
    }

    /**
     * 自动不全列表
     * @return
     */
    @GetMapping("completes")
    public Result<List<ScriptComplete>> completes() {
        return success(ScriptCmdMethodsProvider.completes);
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
    public Result<Page<Script>> view(Page page, Script entity) {
        return scriptService.page(page, Wrappers.<Script>lambdaQuery(entity).eq(Script::getType, Script.TYPE_MODEL_API));
    }

    /**
     * 已绑定的接口列表
     * @param id
     * @return
     */
    @GetMapping("binds")
    public Result<List<BindDto>> binds(Long id) {
        return scriptService.bindApis(id);
    }

    /**
     * 更新或者保存记录
     * @param entity
     * @return
     */
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody Script entity) {
        entity.setType(Script.TYPE_MODEL_API);
        return this.scriptService.saveOrUpdate(entity);
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
        if(ids.size() > 1) {
            return fail("不支持批量删除");
        }

        final Script script = this.scriptService.getById(ids.get(0)).ifNotPresentThrow("记录不存在").getData();
        Boolean isJoin = this.scriptService.isJoinModelApi(script.getKey());
        return Boolean.FALSE.equals(isJoin) ? this.scriptService.removeById(script.getId()) : fail("此脚本已被使用, 请先删除关联");
    }

}
