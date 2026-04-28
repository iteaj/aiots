package com.iteaj.iboot.plugin.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.iboot.plugin.ai.dto.AiDatasetDto;
import com.iteaj.iboot.plugin.ai.entity.AiDataset;
import com.iteaj.iboot.plugin.ai.service.IAiDatasetService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI数据集管理
 * @author aiots-dev
 */
@RestController
@RequestMapping("/ai/dataset")
public class AiDatasetController extends BaseController {

    private final IAiDatasetService aiDatasetService;

    public AiDatasetController(IAiDatasetService aiDatasetService) {
        this.aiDatasetService = aiDatasetService;
    }

    /**
     * 分页查询列表
     */
    @Logger("分页查询数据集列表")
    @GetMapping("/view")
    @CheckPermission("ai:dataset:view")
    public Result<IPage<AiDataset>> view(Page page, AiDatasetDto dto) {
        return this.aiDatasetService.page(page, dto);
    }

    /**
     * 列表查询
     */
    @Logger("列表查询数据集")
    @GetMapping("/list")
    @CheckPermission("ai:dataset:view")
    public Result<List<AiDataset>> list(AiDataset aiDataset) {
        return aiDatasetService.list(aiDataset);
    }

    /**
     * 获取编辑记录
     */
    @Logger("获取编辑记录")
    @GetMapping("/edit")
    public Result<AiDataset> edit(Long id) {
        return this.aiDatasetService.getById(id);
    }

    /**
     * 获取记录详情
     */
    @Logger("获取记录详情")
    @GetMapping("/detail")
    public Result<AiDatasetDto> detail(Long id) {
        return this.aiDatasetService.detailById(id);
    }

    /**
     * 新增或更新记录
     */
    @Logger("新增或者更新记录")
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"ai:dataset:edit", "ai:dataset:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody AiDataset aiDataset) {
        return this.aiDatasetService.saveOrUpdate(aiDataset);
    }

    /**
     * 删除
     */
    @Logger("删除")
    @PostMapping("/del")
    @CheckPermission("ai:dataset:del")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.aiDatasetService.removeByIds(ids);
    }
}
