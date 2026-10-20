package com.iteaj.iboot.plugin.iotview.controller;

import java.io.IOException;
import java.util.*;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.file.UploadResult;
import com.iteaj.framework.spi.file.UploadService;
import com.iteaj.iboot.plugin.iotview.IotViewResult;
import com.iteaj.iboot.plugin.iotview.entity.Resource;
import com.iteaj.iboot.plugin.iotview.service.IResourceService;
import org.springframework.web.bind.annotation.*;
import com.iteaj.framework.security.CheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.iboot.plugin.iotview.entity.Project;
import com.iteaj.iboot.plugin.iotview.service.IProjectService;
import com.iteaj.framework.BaseController;
import org.springframework.web.multipart.MultipartFile;

import static com.iteaj.iboot.plugin.iotview.entity.Resource.BG_IMG;

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
    private final IResourceService resourceService;

    public ProjectController(UploadService uploadService
            , IProjectService projectService, IResourceService resourceService) {
        this.uploadService = uploadService;
        this.projectService = projectService;
        this.resourceService = resourceService;
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
    public Result<Project> create(@RequestBody Project entity) {
        this.projectService.save(entity);
        return IotViewResult.Success(entity);
    }

    /**
    * 更新大屏记录
    * @param entity 实体
    */
    @PostMapping("/edit")
    @Logger("新增或者更新大屏项目记录")
    @CheckPermission(value = {"isv:project:edit"})
    public Result<Boolean> edit(@RequestBody Project entity) {
        if(entity.getId()==null){
            return IotViewResult.FailNotJump("项目还未保存");
        }

        return new IotViewResult<>(this.projectService.updateById(entity));
    }

    /**
     * 获取大屏详情记录
     * @param projectId
     */
    @GetMapping("/getData")
    @Logger("新增或者更新大屏项目记录")
    public Result<Project> detail(String projectId) {
        Project data = this.projectService.getById(projectId).getData();
        if(data != null){
            if(StrUtil.isNotEmpty(data.getContent())){
                return IotViewResult.Success(data);
            } else {
                return IotViewResult.Success(null);
            }
        }

        return IotViewResult.FailNotJump("项目不存在");
    }

    /**
    * 删除指定记录
    * @param id
    */
    @PostMapping("/delete")
    @Logger("删除大屏项目记录")
    @CheckPermission({"isv:project:del"})
    public Result<Boolean> remove(String id) {
        return this.projectService.removeById(id).setMessage("移除项目成功");
    }

    /**
     * 上传大屏图片
     *
     * @param id
     * @param type (背景: bg, 预览图: preview)
     * @param file
     * @return fileurl
     */
    @PostMapping("upload")
    public IotViewResult upload(String id, String type, MultipartFile file) {
        try {
            Map<String, String> result = new HashMap<>();
            final UploadResult isv = uploadService.upload(file.getInputStream(), file.getOriginalFilename(), id, "/isv");
            if(Objects.equals("preview", type)) {
                final Project project = new Project().setIndexImage(isv.getUrl());
                project.setId(id);
                projectService.updateById(project);
            } else if(Objects.equals(BG_IMG, type)) {
                String originalFilename = isv.getOriginalFilename();
                if(originalFilename != null) {
                    final int indexOf = originalFilename.indexOf(".");
                    if(indexOf > 0) {
                        originalFilename = originalFilename.substring(0, indexOf);
                    }
                }

                final Resource resource = new Resource().setType(BG_IMG).setName(originalFilename).setUrl(isv.getUrl());
                resourceService.save(resource);
                result.put("id", resource.getId());
                result.put("fileName", originalFilename);
            }

            result.put("fileurl", isv.getUrl());
            return IotViewResult.Success(result);
        } catch (IOException e) {
            return IotViewResult.Fail("上传失败");
        }
    }
}

