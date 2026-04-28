package com.iteaj.iboot.plugin.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.iboot.plugin.ai.dto.AiTrainTaskDto;
import com.iteaj.iboot.plugin.ai.entity.AiTrainTask;
import com.iteaj.iboot.plugin.ai.service.IAiTrainTaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI训练任务管理
 * @author aiots-dev
 */
@RestController
@RequestMapping("/ai/train/task")
public class AiTrainTaskController extends BaseController {

    private final IAiTrainTaskService aiTrainTaskService;

    public AiTrainTaskController(IAiTrainTaskService aiTrainTaskService) {
        this.aiTrainTaskService = aiTrainTaskService;
    }

    /**
     * 分页查询列表（含关联模型名称）
     */
    @Logger("分页查询训练任务列表")
    @GetMapping("/view")
    @CheckPermission("ai:train:task:view")
    public Result<IPage<AiTrainTaskDto>> view(Page<AiTrainTask> page, AiTrainTaskDto dto) {
        return this.aiTrainTaskService.pageOfDetail(page, dto);
    }

    /**
     * 列表查询
     */
    @Logger("列表查询训练任务")
    @GetMapping("/list")
    @CheckPermission("ai:train:task:view")
    public Result<List<AiTrainTask>> list(AiTrainTask aiTrainTask) {
        return aiTrainTaskService.list(aiTrainTask);
    }

    /**
     * 获取编辑记录
     */
    @Logger("获取编辑记录")
    @GetMapping("/edit")
    @CheckPermission("ai:train:task:saveOrUpdate")
    public Result<AiTrainTask> edit(Long id) {
        return this.aiTrainTaskService.getById(id);
    }

    /**
     * 查询训练任务详情（含模型名称、训练指标列表）
     */
    @Logger("查询训练任务详情")
    @GetMapping("/detail")
    @CheckPermission("ai:train:task:view")
    public Result<AiTrainTaskDto> detail(Long id) {
        return this.aiTrainTaskService.detailById(id);
    }

    /**
     * 新增或更新记录
     */
    @Logger("新增或者更新训练任务记录")
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"ai:train:task:add", "ai:train:task:edit"})
    public Result<Boolean> saveOrUpdate(@RequestBody AiTrainTask aiTrainTask) {
        return this.aiTrainTaskService.saveOrUpdate(aiTrainTask);
    }

    /**
     * 删除
     */
    @Logger("删除训练任务记录")
    @PostMapping("/del")
    @CheckPermission("ai:train:task:del")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.aiTrainTaskService.removeByIds(ids);
    }

    /**
     * 切换训练任务状态（开始/取消）
     * 通过gRPC与Python服务通信控制训练启停
     */
    @Logger("切换训练任务状态")
    @PostMapping("/toggle")
    @CheckPermission("ai:train:task:toggle")
    public Result<Boolean> toggle(@RequestBody AiTrainTask task) {
        return Result.success(this.aiTrainTaskService.toggle(task), "操作成功");
    }
}
