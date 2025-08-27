package com.iteaj.iboot.plugin.iotview.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.iboot.plugin.iotview.entity.Resource;
import com.iteaj.iboot.plugin.iotview.service.IResourceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 大屏资源管理
 * @author iteaj
 * @since 2025-07-17
 */
@RestController
@RequestMapping("/isv/resource")
public class ResourceController extends BaseController {

    private final IResourceService resourceService;

    public ResourceController(IResourceService resourceService) {
        this.resourceService = resourceService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @Logger("浏览大屏资源功能")
    public Result<IPage<Resource>> list(Page<Resource> page, Resource entity) {
        return this.resourceService.page(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"isv:resource:edit"})
    public Result<Resource> getById(Long id) {
        return this.resourceService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新大屏资源记录")
    @CheckPermission(value = {"isv:resource:edit", "isv:resource:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody Resource entity) {
        return this.resourceService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除大屏资源记录")
    @CheckPermission({"isv:resource:del"})
    public Result<Boolean> remove(@RequestBody List<String> idList) {
        return this.resourceService.removeByIds(idList);
    }
}

