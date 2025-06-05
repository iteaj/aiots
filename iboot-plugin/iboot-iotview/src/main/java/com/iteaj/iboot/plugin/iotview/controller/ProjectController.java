package com.iteaj.iboot.plugin.iotview.controller;

import java.util.List;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.Logical;
import com.iteaj.iboot.plugin.iotview.IotViewResult;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.plugin.iotview.entity.Project;
import com.iteaj.iboot.plugin.iotview.service.IProjectService;
import com.iteaj.framework.BaseController;

/**
 * 大屏项目管理
 * @author iteaj
 * @since 2025-06-04
 */
@RestController
@RequestMapping("/isv/project")
public class ProjectController extends BaseController {

    private final IProjectService projectService;

    public ProjectController(IProjectService projectService) {
        this.projectService = projectService;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/list")
    @Logger("浏览大屏项目功能")
    @CheckPermission({"isv:project:view"})
    public Result<IPage<Project>> list(Page<Project> page, Project entity) {
        return IotViewResult.build(this.projectService.page(page, entity));
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"isv:project:edit"})
    public Result<Project> getById(Long id) {
        return this.projectService.getById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @Logger("新增或者更新大屏项目记录")
    @CheckPermission(value = {"isv:project:edit", "isv:project:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody Project entity) {
        return this.projectService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @Logger("删除大屏项目记录")
    @CheckPermission({"iotview:project:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.projectService.removeByIds(idList);
    }
}

