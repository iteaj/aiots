package com.iteaj.iboot.module.iot.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.CheckPermission;
import com.iteaj.framework.security.Logical;
import com.iteaj.iboot.module.iot.dto.CollectTaskDto;
import com.iteaj.iboot.module.iot.collect.CollectActionFactory;
import com.iteaj.iboot.module.iot.collect.CollectException;
import com.iteaj.iboot.module.iot.collect.CollectOption;
import com.iteaj.iboot.module.iot.collect.CollectService;
import com.iteaj.iboot.module.iot.collect.store.StoreActionFactory;
import com.iteaj.iboot.module.iot.entity.CollectTask;
import com.iteaj.iboot.module.iot.service.ICollectTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据采集任务管理
 *
 * @author iteaj
 * @since 2022-08-28
 */
@RestController
@RequestMapping("/iot/collectTask")
public class CollectTaskController extends BaseController {

    private final CollectService collectService;
    private final ICollectTaskService collectTaskService;
    private final StoreActionFactory storeActionFactory;
    private final CollectActionFactory collectActionFactory;
    public CollectTaskController(@Autowired(required = false) CollectService collectService, ICollectTaskService collectTaskService
            , StoreActionFactory storeActionFactory, CollectActionFactory collectActionFactory) {
        this.collectService = collectService;
        this.collectTaskService = collectTaskService;
        this.storeActionFactory = storeActionFactory;
        this.collectActionFactory = collectActionFactory;
    }

    /**
    * 列表查询
    * @param page 分页
    * @param entity 搜索条件
    */
    @GetMapping("/view")
    @CheckPermission({"iot:collectTask:view"})
    public Result<IPage<CollectTaskDto>> list(Page<CollectTaskDto> page, CollectTaskDto entity) {
        return this.collectTaskService.detailOfPage(page, entity);
    }

    /**
    * 获取编辑记录
    * @param id 记录id
    */
    @GetMapping("/edit")
    @CheckPermission({"iot:collectTask:edit"})
    public Result<CollectTaskDto> getById(Long id) {
        return this.collectTaskService.detailById(id);
    }

    /**
    * 新增或者更新记录
    * @param entity
    */
    @PostMapping("/saveOrUpdate")
    @CheckPermission(value = {"iot:collectTask:edit", "iot:collectTask:add"}, logical = Logical.OR)
    public Result<Boolean> save(@RequestBody CollectTask entity) {
        if(entity.getStatus() != null && entity.getStatus().equals("run")) {
            return fail("请先停止任务后在更新");
        }

        try {
            CronExpression.parse(entity.getCron());
        } catch (Exception e) {
            return fail("任务调度表达式错误");
        }

        return this.collectTaskService.saveOrUpdate(entity);
    }

    /**
    * 删除指定记录
    * @param idList
    */
    @PostMapping("/del")
    @CheckPermission({"iot:collectTask:del"})
    public Result<Boolean> remove(@RequestBody List<Long> idList) {
        return this.collectTaskService.removeByIds(idList);
    }

    /**
     * 切换采集状态
     * @param entity
     */
    @PostMapping("/status")
    @CheckPermission({"iot:collectTask:status"})
    public Result<Boolean> status(@RequestBody CollectTask entity) {
        try {
            collectService.statusSwitch(entity.getId(), entity.getStatus());
        } catch (CollectException e) {
            return Result.fail(e.getMessage());
        }
        return success();
    }

    /**
     * 采集动作
     * @return
     */
    @GetMapping("collectActions")
    public Result<List<CollectOption>> collectActions() {
        return success(collectActionFactory.options());
    }

    /**
     * 存储动作
     * @return
     */
    @GetMapping("storeActions")
    public Result<List<CollectOption>> storeActions() {
        return success(storeActionFactory.options());
    }
}

