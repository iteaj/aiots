package com.iteaj.iboot.plugin.script.controller;

import java.util.List;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.plugin.script.entity.Script;
import com.iteaj.iboot.plugin.script.service.IScriptService;
import com.iteaj.framework.BaseController;

/**
 * 脚本引擎管理
 * @author iteaj
 * @since 2025-04-04
 */
@RestController
@RequestMapping("/iot/script")
public class ScriptController extends BaseController {

    private final IScriptService scriptService;

    public ScriptController(IScriptService scriptService) {
        this.scriptService = scriptService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览脚本引擎功能")
    @CheckPermission({"iot:script:view"})
    public Result<IPage<Script>> list(Page<Script> page, Script entity) {
        return this.scriptService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:script:edit"})
    public Result<Script> getById(Long id) {
        return this.scriptService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新脚本引擎记录")
    @CheckPermission(value = {"iot:script:edit", "iot:script:filter:edit"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody Script entity) {
        return this.scriptService.saveOrUpdate(entity);
    }

}

