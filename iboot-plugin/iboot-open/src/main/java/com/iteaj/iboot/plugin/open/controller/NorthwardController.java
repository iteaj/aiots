package com.iteaj.iboot.plugin.open.controller;

import java.util.List;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.plugin.open.entity.Northward;
import com.iteaj.iboot.plugin.open.service.INorthwardService;
import com.iteaj.framework.BaseController;

/**
 * 北向接入管理
 * @author iteaj
 * @since 2025-02-07
 */
@RestController
@RequestMapping("/open/northward")
public class NorthwardController extends BaseController {

    private final INorthwardService northwardService;

    public NorthwardController(INorthwardService northwardService) {
        this.northwardService = northwardService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览北向接入功能")
    @CheckPermission({"open:northward:view"})
    public Result<IPage<Northward>> list(Page<Northward> page, Northward entity) {
        return this.northwardService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"open:northward:edit"})
    public Result<Northward> getById(Long id) {
        return this.northwardService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新北向接入记录")
    @CheckPermission(value = {"open:northward:edit", "open:northward:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody Northward entity) {
        return this.northwardService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除北向接入记录")
    @CheckPermission({"open:northward:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.northwardService.removeByIds(idList);
    }
}

