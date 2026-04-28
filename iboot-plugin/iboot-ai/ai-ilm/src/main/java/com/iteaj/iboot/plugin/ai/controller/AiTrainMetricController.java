package com.iteaj.iboot.plugin.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.iboot.plugin.ai.entity.AiTrainMetric;
import com.iteaj.iboot.plugin.ai.service.IAiTrainMetricService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI训练指标管理
 * @author aiots-dev
 */
@RestController
@RequestMapping("/ai/train/metric")
public class AiTrainMetricController extends BaseController {

    private final IAiTrainMetricService aiTrainMetricService;

    public AiTrainMetricController(IAiTrainMetricService aiTrainMetricService) {
        this.aiTrainMetricService = aiTrainMetricService;
    }

    /**
     * 分页查询列表
     */
    @Logger("分页查询训练指标列表")
    @GetMapping("/view")
    @CheckPermission("ai:train:metric:view")
    public Result<IPage<AiTrainMetric>> view(Page page, AiTrainMetric aiTrainMetric) {
        return this.aiTrainMetricService.page(page, aiTrainMetric);
    }

    /**
     * 列表查询
     */
    @Logger("列表查询训练指标")
    @GetMapping("/list")
    @CheckPermission("ai:train:metric:view")
    public Result<List<AiTrainMetric>> list(AiTrainMetric aiTrainMetric) {
        return aiTrainMetricService.list(aiTrainMetric);
    }

    /**
     * 获取编辑记录
     */
    @Logger("获取编辑记录")
    @GetMapping("/edit")
    @CheckPermission("ai:train:metric:saveOrUpdate")
    public Result<AiTrainMetric> edit(Long id) {
        return this.aiTrainMetricService.getById(id);
    }

    /**
     * 新增或更新记录
     */
    @Logger("新增或者更新训练指标记录")
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"ai:train:metric:add", "ai:train:metric:edit"}, logical = Logical.OR)
    public Result<Boolean> saveOrUpdate(@RequestBody AiTrainMetric aiTrainMetric) {
        return this.aiTrainMetricService.saveOrUpdate(aiTrainMetric);
    }

    /**
     * 删除
     */
    @Logger("删除训练指标记录")
    @PostMapping("/del")
    @CheckPermission("ai:train:metric:del")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.aiTrainMetricService.removeByIds(ids);
    }

    /**
     * 根据任务ID查询指标列表
     */
    @Logger("根据任务ID查询指标列表")
    @GetMapping("/listByTaskId")
    @CheckPermission("ai:train:metric:view")
    public Result<List<AiTrainMetric>> listByTaskId(Long taskId) {
        AiTrainMetric metric = new AiTrainMetric();
        metric.setTaskId(taskId);
        return aiTrainMetricService.list(metric);
    }

    /**
     * 批量保存训练指标
     */
    @Logger("批量保存训练指标")
    @PostMapping("/batchSave")
    @CheckPermission("ai:train:metric:saveOrUpdate")
    public Result<Boolean> batchSave(@RequestBody List<AiTrainMetric> metrics) {
        return this.aiTrainMetricService.saveOrUpdateBatch(metrics);
    }
}
