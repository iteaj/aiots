package com.iteaj.iboot.plugin.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.security.Logical;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.iboot.plugin.ai.entity.AiDatasetItem;
import com.iteaj.iboot.plugin.ai.service.IAiDatasetItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI数据集明细管理
 * @author aiots-dev
 */
@RestController
@RequestMapping("/ai/dataset/item")
public class AiDatasetItemController extends BaseController {

    private final IAiDatasetItemService aiDatasetItemService;

    public AiDatasetItemController(IAiDatasetItemService aiDatasetItemService) {
        this.aiDatasetItemService = aiDatasetItemService;
    }

    /**
     * 分页查询列表
     */
    @Logger("分页查询数据集明细列表")
    @GetMapping("/view")
    @CheckPermission("ai:dataset:item:view")
    public Result<IPage<AiDatasetItem>> view(Page page, AiDatasetItem item) {
        return this.aiDatasetItemService.page(page, item);
    }

    /**
     * 列表查询
     */
    @Logger("列表查询数据集明细")
    @GetMapping("/list")
    @CheckPermission("ai:dataset:item:view")
    public Result<List<AiDatasetItem>> list(AiDatasetItem item) {
        return aiDatasetItemService.list(item);
    }

    /**
     * 获取编辑记录
     */
    @Logger("获取编辑记录")
    @GetMapping("/edit")
    public Result<AiDatasetItem> edit(Long id) {
        return this.aiDatasetItemService.getById(id);
    }

    /**
     * 新增或更新记录
     */
    @Logger("新增或者更新记录")
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"ai:dataset:item:edit", "ai:dataset:item:add"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody AiDatasetItem item) {
        return this.aiDatasetItemService.saveOrUpdate(item);
    }

    /**
     * 删除
     */
    @Logger("删除")
    @PostMapping("/del")
    @CheckPermission("ai:dataset:item:del")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.aiDatasetItemService.removeByIds(ids);
    }
}
