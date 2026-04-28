package com.iteaj.iboot.plugin.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.spi.file.UploadResult;
import com.iteaj.framework.spi.file.UploadService;
import com.iteaj.iboot.plugin.ai.dto.AiModelDto;
import com.iteaj.iboot.plugin.ai.entity.AiModel;
import com.iteaj.iboot.plugin.ai.service.IAiModelService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * AI模型管理
 * @author aiots-dev
 */
@RestController
@RequestMapping("/ai/model")
public class AiModelController extends BaseController {

    private final UploadService uploadService;
    private final IAiModelService aiModelService;

    public AiModelController(UploadService uploadService, IAiModelService aiModelService) {
        this.uploadService = uploadService;
        this.aiModelService = aiModelService;
    }

    /**
     * 分页查询列表
     */
    @Logger("分页查询模型列表")
    @GetMapping("/view")
    @CheckPermission("ai:model:view")
    public Result<IPage<AiModel>> view(Page page, AiModel aiModel) {
        return this.aiModelService.page(page, aiModel);
    }

    /**
     * 列表查询
     */
    @Logger("列表查询模型")
    @GetMapping("/list")
    @CheckPermission("ai:model:view")
    public Result<List<AiModel>> list(AiModel aiModel) {
        return aiModelService.list(aiModel);
    }

    /**
     * 获取编辑记录
     */
    @Logger("获取编辑记录")
    @GetMapping("/edit")
    public Result<AiModel> edit(Long id) {
        return this.aiModelService.getById(id);
    }

    /**
     * 查询模型详情（含参数列表）
     */
    @Logger("查询模型详情")
    @GetMapping("/detail")
    @CheckPermission("ai:model:view")
    public Result<AiModelDto> detail(Long id) {
        return this.aiModelService.detailById(id);
    }

    /**
     * 新增或更新记录
     */
    @Logger("新增或者更新模型记录")
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"ai:model:add", "ai:model:edit"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody AiModel aiModel) {
        return this.aiModelService.saveOrUpdate(aiModel);
    }

    /**
     * 删除
     */
    @Logger("删除模型记录")
    @PostMapping("/del")
    @CheckPermission("ai:model:del")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.aiModelService.removeByIds(ids);
    }

    /**
     * 上传模型文件
     * @param file 模型文件
     * @return 文件上传后的路径
     */
    @Logger("上传模型文件")
    @PostMapping("upload")
    public Result<String> upload(MultipartFile file) throws IOException {
        UploadResult model = uploadService.upload(file.getInputStream(), file.getOriginalFilename(), "/ai/model");
        return success(model.getUrl(), "上传成功");
    }
}

