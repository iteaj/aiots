package com.iteaj.quartz.controller;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.quartz.scheduler.SchedulerManager;
import com.iteaj.util.Result;
import com.iteaj.quartz.entity.QrtzTriggers;
import com.iteaj.quartz.service.IQrtzTriggersService;
import com.iteaj.util.module.mvc.result.PageResult;
import org.quartz.CronExpression;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Controller;
import com.iteaj.framework.BaseController;

/**
 * <p>
 *  控制器
 * </p>
 *
 * @author iteaj
 * @since 2020-07-26
 */
@Controller
@RequestMapping("/quartz/triggers")
public class QrtzTriggersController extends BaseController {

    @Autowired
    private SchedulerManager schedulerManager;
    @Autowired
    private IQrtzTriggersService qrtzTriggersService;

    @GetMapping
    public String view() {
        return "qrtzTriggers";
    }

    @ResponseBody
    @GetMapping("/view")
    public Result view(QrtzTriggers entity, Page page) {
        IPage<QrtzTriggers> jobByPage = this.qrtzTriggersService.joinJobByPage(page, entity);
        return new PageResult(jobByPage).build(success());
    }

    /**
     * 恢复作业
     * @param entity
     * @return
     */
    @ResponseBody
    @PostMapping("/resume")
    public Result add(@RequestBody QrtzTriggers entity) {
        schedulerManager.resumeJob(entity.getJobName());
        return success("恢复作业成功");
    }


    /**
     * 触发一次
     * @param entity
     * @return
     */
    @ResponseBody
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
    @ResponseBody
    @PostMapping("/paused")
    public Result detail(@RequestBody QrtzTriggers triggers) {
        schedulerManager.pauseJob(triggers.getJobName());

        return success("暂停作业成功");
    }

    /**
     * 更新某个作业
     * @param triggers
     * @return
     */
    @ResponseBody
    @PostMapping("/edit")
    public Result edit(@RequestBody QrtzTriggers triggers) {
        boolean validExpression = CronExpression.isValidExpression(triggers.getCronExpression());
        if(!validExpression) return fail("验证失败(cron): " + triggers.getCronExpression());

        schedulerManager.updateTrigger(triggers.getJobName(), triggers.getCronExpression());
        return success("暂停作业成功");
    }

    @ResponseBody
    @PostMapping("/del")
    public Result del(@RequestBody QrtzTriggers triggers) {
        schedulerManager.removeJob(triggers.getJobName(), triggers.getJobGroup()
                , triggers.getTriggerName(), triggers.getTriggerGroup());

        return success("删除记录成功");
    }

}

