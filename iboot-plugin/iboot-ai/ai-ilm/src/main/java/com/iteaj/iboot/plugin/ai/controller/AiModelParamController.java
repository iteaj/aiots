package com.iteaj.iboot.plugin.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.iboot.plugin.ai.entity.AiModelParam;
import com.iteaj.iboot.plugin.ai.service.IAiModelParamService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI模型参数管理
 * @author aiots-dev
 */
@RestController
@RequestMapping("/ai/model/param")
public class AiModelParamController extends BaseController {

    private final IAiModelParamService aiModelParamService;

    public AiModelParamController(IAiModelParamService aiModelParamService) {
        this.aiModelParamService = aiModelParamService;
    }

    /**
     * 分页查询列表
     */
    @Logger("分页查询模型参数列表")
    @GetMapping("/view")
    @CheckPermission("ai:model:param:view")
    public Result<IPage<AiModelParam>> view(Page page, AiModelParam aiModelParam) {
        return this.aiModelParamService.page(page, aiModelParam);
    }

    /**
     * 列表查询
     */
    @Logger("列表查询模型参数")
    @GetMapping("/list")
    @CheckPermission("ai:model:param:view")
    public Result<List<AiModelParam>> list(AiModelParam aiModelParam) {
        return aiModelParamService.list(aiModelParam);
    }

    /**
     * 获取编辑记录
     */
    @Logger("获取编辑记录")
    @GetMapping("/edit")
    @CheckPermission("ai:model:param:saveOrUpdate")
    public Result<AiModelParam> edit(Long id) {
        return this.aiModelParamService.getById(id);
    }

    /**
     * 新增或更新记录
     */
    @Logger("新增或者更新模型参数记录")
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"ai:model:param:add", "ai:model:param:edit"})
    public Result<Boolean> saveOrUpdate(@RequestBody AiModelParam aiModelParam) {
        return this.aiModelParamService.saveOrUpdate(aiModelParam);
    }

    /**
     * 删除
     */
    @Logger("删除模型参数记录")
    @PostMapping("/del")
    @CheckPermission("ai:model:param:del")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.aiModelParamService.removeByIds(ids);
    }

    /**
     * 根据模型ID查询参数列表
     */
    @Logger("根据模型ID查询参数列表")
    @GetMapping("/listByModelId")
    @CheckPermission("ai:model:param:view")
    public Result<List<AiModelParam>> listByModelId(Long modelId) {
        AiModelParam param = new AiModelParam();
        param.setModelId(modelId);
        return aiModelParamService.list(param);
    }

    /**
     * 批量保存模型参数
     */
    @Logger("批量保存模型参数")
    @PostMapping("/batchSave")
    @CheckPermission("ai:model:param:saveOrUpdate")
    public Result<Boolean> batchSave(@RequestBody List<AiModelParam> params) {
        return this.aiModelParamService.saveOrUpdateBatch(params);
    }
}
