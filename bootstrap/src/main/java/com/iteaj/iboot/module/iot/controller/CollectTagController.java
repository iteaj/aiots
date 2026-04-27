package com.iteaj.iboot.module.iot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.iboot.module.iot.entity.CollectTag;
import com.iteaj.iboot.module.iot.service.ICollectTagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 采集标签管理
 *
 * @author iteaj
 * @since 2026-04-26
 */
@RestController
@RequestMapping("/iot/collectTag")
public class CollectTagController extends BaseController {

    private final ICollectTagService collectTagService;

    public CollectTagController(ICollectTagService collectTagService) {
        this.collectTagService = collectTagService;
    }

    /**
     * 分页查询列表
     * @param page 分页
     * @param entity 搜索条件
     */
    @GetMapping("/view")
    @Logger("浏览采集标签")
    @CheckPermission("iot:collectTag:view")
    public Result<IPage<CollectTag>> view(Page<CollectTag> page, CollectTag entity) {
        return this.collectTagService.page(page, entity);
    }

    /**
     * 列表查询
     * @param entity 搜索条件
     */
    @GetMapping("/list")
    @CheckPermission("iot:collectTag:view")
    public Result<List<CollectTag>> list(CollectTag entity) {
        return this.collectTagService.list(entity);
    }

    /**
     * 获取编辑记录
     * @param id 记录id
     */
    @GetMapping("/edit")
    @CheckPermission("iot:collectTag:edit")
    public Result<CollectTag> edit(Long id) {
        return this.collectTagService.getById(id);
    }

    /**
     * 新增或更新记录
     * @param entity
     */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新采集标签")
    @CheckPermission(value = {"iot:collectTag:edit", "iot:collectTag:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody CollectTag entity) {
        return this.collectTagService.saveOrUpdate(entity);
    }

    /**
     * 删除
     * @param ids 主键id列表
     */
    @PostMapping("/del")
    @Logger("删除采集标签")
    @CheckPermission("iot:collectTag:del")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.collectTagService.removeByIds(ids);
    }

}
