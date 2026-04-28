package com.iteaj.iboot.plugin.ai.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.iboot.plugin.ai.dto.AiTestTaskDto;
import com.iteaj.iboot.plugin.ai.entity.AiTestTask;
import com.iteaj.iboot.plugin.ai.service.IAiTestTaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI模型测试任务管理
 * @author aiots-dev
 */
@RestController
@RequestMapping("/ai/test/task")
public class AiTestTaskController extends BaseController {

    private final IAiTestTaskService aiTestTaskService;

    public AiTestTaskController(IAiTestTaskService aiTestTaskService) {
        this.aiTestTaskService = aiTestTaskService;
    }

    /**
     * 分页查询列表（含关联训练任务名称）
     */
    @Logger("分页查询测试任务列表")
    @GetMapping("/view")
    @CheckPermission("ai:test:task:view")
    public Result<IPage<AiTestTaskDto>> view(Page<AiTestTask> page, AiTestTaskDto dto) {
        return this.aiTestTaskService.pageOfDetail(page, dto);
    }

    /**
     * 列表查询
     */
    @Logger("列表查询测试任务")
    @GetMapping("/list")
    @CheckPermission("ai:test:task:view")
    public Result<List<AiTestTask>> list(AiTestTask aiTestTask) {
        return aiTestTaskService.list(aiTestTask);
    }

    /**
     * 获取编辑记录
     */
    @Logger("获取编辑记录")
    @GetMapping("/edit")
    @CheckPermission(value = {"ai:test:task:edit", "ai:test:task:add"}, logical = Logical.OR)
    public Result<AiTestTask> edit(Long id) {
        return this.aiTestTaskService.getById(id);
    }

    /**
     * 查询测试任务详情（含训练任务名称）
     */
    @Logger("查询测试任务详情")
    @GetMapping("/detail")
    @CheckPermission("ai:test:task:view")
    public Result<AiTestTaskDto> detail(Long id) {
        return this.aiTestTaskService.detailById(id);
    }

    /**
     * 新增或更新记录
     */
    @Logger("新增或者更新测试任务记录")
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"ai:test:task:add", "ai:test:task:edit"})
    public Result<Boolean> saveOrUpdate(@RequestBody AiTestTask aiTestTask) {
        return this.aiTestTaskService.saveOrUpdate(aiTestTask);
    }

    /**
     * 删除
     */
    @Logger("删除测试任务记录")
    @PostMapping("/del")
    @CheckPermission("ai:test:task:del")
    public Result<Boolean> del(@RequestBody List<Long> ids) {
        return this.aiTestTaskService.removeByIds(ids);
    }

    /**
     * 切换测试任务状态（开始/取消）
     * 通过gRPC与Python服务通信控制测试启停
     */
    @Logger("切换测试任务状态")
    @PostMapping("/toggle")
    @CheckPermission("ai:test:task:toggle")
    public Result<Boolean> toggle(@RequestBody AiTestTask task) {
        return Result.success(this.aiTestTaskService.toggle(task), "操作成功");
    }
}
