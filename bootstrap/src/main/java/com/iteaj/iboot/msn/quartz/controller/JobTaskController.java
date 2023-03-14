package com.iteaj.iboot.msn.quartz.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.framework.result.DetailResult;
import com.iteaj.framework.result.PageResult;
import com.iteaj.iboot.msn.quartz.entity.JobTask;
import com.iteaj.iboot.msn.quartz.service.IJobTaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quartz/task")
public class JobTaskController extends BaseController {

    private final IJobTaskService jobTaskService;

    public JobTaskController(IJobTaskService jobTaskService) {
        this.jobTaskService = jobTaskService;
    }

    /**
     * 获取定时任务列表
     * @param page
     * @param entity
     * @return
     */
    @GetMapping("view")
    public PageResult<Page<JobTask>> view(Page page, JobTask entity) {
        return this.jobTaskService.page(page, entity);
    }

    /**
     * 获取定时任务详情
     * @param id
     * @return
     */
    @GetMapping("detail")
    public DetailResult<JobTask> detail(Long id) {
        return this.jobTaskService.getById(id);
    }

    /**
     * 新增或者修改定时任务
     * @param entity
     * @return
     */
    @PostMapping("addOrUpdate")
    public BooleanResult addOrUpdate(@RequestBody JobTask entity) {
        return this.jobTaskService.saveOrUpdate(entity);
    }

    /**
     * 删除定时任务
     * @param ids
     * @return
     */
    @PostMapping("del")
    public BooleanResult del(@RequestBody List<Long> ids) {
        return this.jobTaskService.removeByIds(ids);
    }
}
