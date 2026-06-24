package com.iteaj.iboot.plugin.script.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.script.ScriptComplete;
import com.iteaj.framework.script.ScriptConfig;
import com.iteaj.framework.script.ScriptLanguage;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.iboot.plugin.script.entity.Script;
import com.iteaj.iboot.plugin.script.model.LinkageScriptMethodsProvider;
import com.iteaj.iboot.plugin.script.model.LinkageScriptRealtimeListener;
import com.iteaj.iboot.plugin.script.service.IScriptService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 联动场景脚本管理
 * @author iteaj
 * @since 2025-04-11
 */
@RestController
@RequestMapping("/iot/script/linkage")
public class LinkageScriptController extends BaseController {

    private final IScriptService scriptService;
    private final List<ScriptConfig> configs = Arrays.asList(
            new ScriptConfig(ScriptLanguage.javascript.getName(), "trigger(data, methods);", "" +
                    "// 脚本支持ECMAScript 5.1规范 \r\n" +
                    "/**\r\n" +
                    "* 指定哪些产品下的模型属性需要被触发\r\n" +
                    "* 采集的数据只有匹配模型属性和状态才会执行trigger函数\r\n" +
                    "* @return {productCode: [field | #status | *, ...]} 要触发的产品属性[field]以及在线状态[#status]和所有属性[*]\r\n" +
                    "*/\r\n" +
                    "function matcher() {\r\n" +
                    "    return {};\r\n" +
                    "}\r\n\r\n" +
                    "/**\r\n" +
                    "* 数据采集或者上报以及在线状态改变的触发操作\r\n" +
                    "* @param data 采集或上报的数据\r\n" +
                    "* @param methods 常用方法\r\n" +
                    "*/\r\n" +
                    "function trigger(data, methods) {\r\n" +
                    "    // 数据采集或上报时触发\r\n" +
                    "    if(methods.isRealtimeData(data)) {\r\n" +
                    "        \r\n" +
                    "    }else{ // 设备在线状态改变时触发\r\n" +
                    "        \r\n" +
                    "    }\r\n" +
                    "}\r\n" +
                    "")
    );

    public LinkageScriptController(IScriptService scriptService) {
        this.scriptService = scriptService;
    }

    /**
     * 自动补全列表
     * @return
     */
    @GetMapping("completes")
    public Result<List<ScriptComplete>> completes() {
        return success(LinkageScriptMethodsProvider.completes);
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
     * 获取联动功能的脚本列表
     * @return
     */
    @GetMapping("view")
    public Result<Page<Script>> view(Page page, Script entity) {
        return scriptService.page(page, Wrappers.lambdaQuery(entity).eq(Script::getType, Script.TYPE_LINKAGE));
    }

    /**
     * 更新或者保存记录
     * @param entity
     * @return
     */
    @Logger("新增或者更新联动脚本记录")
    @PostMapping("saveOrUpdate")
//    @CheckPermission(value = {"iot:script:linkage:edit"})
    public Result saveOrUpdate(@RequestBody Script entity) {
        entity.setType(Script.TYPE_LINKAGE);
        return this.scriptService.saveOrUpdate(entity);
    }

    /**
     * 删除记录
     * @param ids
     * @return
     */
    @PostMapping("del")
    @Logger("删除联动脚本记录")
//    @CheckPermission({"iot:script:linkage:del"})
    public Result del(@RequestBody List<Long> ids) {
        if(ids.isEmpty()) {
            return fail("未指定要删除的记录");
        }
        if(ids.size() > 1) {
            return fail("不支持批量删除");
        }

        final Script script = this.scriptService.getById(ids.get(0)).ifNotPresentThrow("记录不存在").getData();
        return this.scriptService.removeById(script.getId()).ifPresentAndTrue(status -> {
            LinkageScriptRealtimeListener.remove(script.getKey());
        });
    }

}
