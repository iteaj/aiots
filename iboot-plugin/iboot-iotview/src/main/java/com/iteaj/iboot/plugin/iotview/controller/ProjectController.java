package com.iteaj.iboot.plugin.iotview.controller;

import java.io.IOException;
import java.util.*;

import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.file.UploadResult;
import com.iteaj.framework.spi.file.UploadService;
import com.iteaj.iboot.plugin.iotview.IotViewResult;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.plugin.iotview.entity.Project;
import com.iteaj.iboot.plugin.iotview.service.IProjectService;
import com.iteaj.framework.BaseController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 大屏项目管理
 * @author iteaj
 * @since 2025-06-04
 */
@RestController
@RequestMapping("/isv/project")
public class ProjectController extends BaseController {

    private final UploadService uploadService;
    private final IProjectService projectService;

    public ProjectController(UploadService uploadService, IProjectService projectService) {
        this.uploadService = uploadService;
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
        return new IotViewResult(this.projectService.page(page, entity));
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
     * 创建项目
     * @param entity
     */
    @PostMapping("/create")
    @Logger("创建大屏项目")
    @CheckPermission(value = {"isv:project:add"})
    public Result<Boolean> create(@RequestBody Project entity) {
        return new IotViewResult(this.projectService.save(entity));
    }

    /**
    * 更新大屏记录
    * @param entity
    */
    @PostMapping("/edit")
    @Logger("新增或者更新大屏项目记录")
    @CheckPermission(value = {"isv:project:edit"})
    public Result<Boolean> edit(@RequestBody Project entity) {
        return new IotViewResult(this.projectService.updateById(entity));
    }


    /**
     * 获取大屏详情记录
     * @param projectId
     */
    @GetMapping("/getData")
    @Logger("新增或者更新大屏项目记录")
    public Result<Project> detail(String projectId) {
        return new IotViewResult(this.projectService.getById(projectId));
    }

    /**
    * 删除指定记录
    * @param ids
    */
    @DeleteMapping("/delete")
    @Logger("删除大屏项目记录")
    @CheckPermission({"iotview:project:del"})
    public Result<Boolean> remove(String ids) {
        final String[] idList = ids.split(",");
        return this.projectService.removeByIds(Arrays.asList(idList));
    }

    /**
     * 上传大屏略缩图
     * @param id
     * @param type (背景: bg, 预览图: preview)
     * @param file
     * @return fileurl
     */
    @PostMapping("upload")
    public Result<Map<String, String>> upload(String id, String type, MultipartFile file) {
        try {
            final UploadResult isv = uploadService.upload(file.getInputStream(), file.getOriginalFilename(), id, "/isv");
            if(Objects.equals("preview", type)) {
                final Project project = new Project().setIndexImage(isv.getUrl());
                project.setId(id);
                projectService.updateById(project);
            }
            Map<String, String> result = new HashMap<>();
            result.put("fileurl", "http://127.0.0.1:8085" + isv.getUrl());
            return IotViewResult.Success(result);
        } catch (IOException e) {
            return IotViewResult.Fail("上传失败");
        }
    }
}

