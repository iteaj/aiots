package com.iteaj.msn.quartz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.PageResult;
import com.iteaj.framework.result.Result;
import com.iteaj.msn.quartz.QuartzJobContext;
import com.iteaj.msn.quartz.entity.QrtzTriggers;
import com.iteaj.msn.quartz.jobs.SpringSupportJob;
import com.iteaj.msn.quartz.scheduler.SchedulerManager;
import com.iteaj.msn.quartz.service.IQrtzTriggersService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.quartz.CronExpression;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  控制器
 * </p>
 *
 * @author iteaj
 * @since 2020-07-26
 */
@RestController
@RequestMapping("/quartz/triggers")
public class QrtzTriggersController extends BaseController {

    private final SpringSupportJob springSupportJob;
    private final SchedulerManager schedulerManager;
    private final IQrtzTriggersService qrtzTriggersService;

    public QrtzTriggersController(SpringSupportJob springSupportJob
            , SchedulerManager schedulerManager, IQrtzTriggersService qrtzTriggersService) {
        this.springSupportJob = springSupportJob;
        this.schedulerManager = schedulerManager;
        this.qrtzTriggersService = qrtzTriggersService;
    }

    @GetMapping("/view")
    public Result<IPage<QrtzTriggers>> view(QrtzTriggers entity, Page page) {
        IPage<QrtzTriggers> jobByPage = this.qrtzTriggersService.joinJobByPage(page, entity);
        return new PageResult(jobByPage);
    }

    /**
     * 恢复作业
     * @param entity
     * @return
     */
    @PostMapping("/resume")
    @RequiresPermissions("quartz:triggers:status")
    public Result resume(@RequestBody QrtzTriggers entity) {
        schedulerManager.resumeJob(entity.getJobName());
        return success("恢复作业成功");
    }

    /**
     * 触发一次
     * @param entity
     * @return
     */
    @PostMapping("/run")
    public Result run(@RequestBody QrtzTriggers entity) {
        schedulerManager.fireJob(entity.getJobName());
        return success("触发作业成功");
    }


    /**
     * 暂停某个作业
     * @param triggers
     * @return
     */
    @PostMapping("/paused")
    @RequiresPermissions("quartz:triggers:status")
    public Result paused(@RequestBody QrtzTriggers triggers) {
        schedulerManager.pauseJob(triggers.getJobName());
        return success("暂停作业成功");
    }

    /**
     * 新增作业
     * @param triggers
     * @return
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody QrtzTriggers triggers) {
        boolean validExpression = CronExpression.isValidExpression(triggers.getCron());
        if(!validExpression) return fail("cron验证失败[" + triggers.getCron()+"]");

        final String expression = triggers.getParams().getString(SpringSupportJob.JOB_EXPRESSION);
        final QuartzJobContext context = springSupportJob.getContext(expression);
        if(context == null) {
            return fail("不存在此方法["+expression+"]");
        }

        schedulerManager.addJob(triggers.getJobName(), triggers.isConcurrent()
                , triggers.getCron(), triggers.getParams(), triggers.getDescription());
        return success("修改作业成功");
    }

    /**
     * 获取某个作业
     * @param jobName
     * @return
     */
    @GetMapping("/edit")
    public Result<QrtzTriggers> edit(String jobName) {
        return success(qrtzTriggersService.getDetail("SBootQuartz", jobName));
    }

    /**
     * 更新某个作业
     * @param triggers
     * @return
     */
    @PostMapping("/edit")
    public Result edit(@RequestBody QrtzTriggers triggers) {
        boolean validExpression = CronExpression.isValidExpression(triggers.getCron());
        if(!validExpression) return fail("cron验证失败[" + triggers.getCron()+"]");

        schedulerManager.updateTrigger(triggers.getJobName(), triggers.getCron());
        schedulerManager.updateJob(triggers.getJobName(), triggers.isConcurrent(), triggers.getDescription());
        return success("修改作业成功");
    }

    @PostMapping("/del")
    public Result del(@RequestBody List<String> jobNames) {
        if(CollectionUtils.isEmpty(jobNames)) {
            return fail("请选择要删除的作业");
        }

        final String jobName = jobNames.get(0);
        schedulerManager.removeJob(jobName, jobName);

        return success("删除记录成功");
    }

}

