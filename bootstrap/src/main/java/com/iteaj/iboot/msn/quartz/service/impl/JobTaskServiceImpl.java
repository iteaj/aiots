package com.iteaj.iboot.msn.quartz.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.iboot.msn.quartz.entity.JobTask;
import com.iteaj.iboot.msn.quartz.mapper.JobTaskMapper;
import com.iteaj.iboot.msn.quartz.scheduler.SchedulerManager;
import com.iteaj.iboot.msn.quartz.service.IJobTaskService;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Collection;

@Service
public class JobTaskServiceImpl extends BaseServiceImpl<JobTaskMapper, JobTask> implements IJobTaskService {

    private final SchedulerManager schedulerManager;

    public JobTaskServiceImpl(@Qualifier("defaultSchedulerManager") SchedulerManager schedulerManager) {
        this.schedulerManager = schedulerManager;
    }

    @Override
    @Transactional
    public BooleanResult saveOrUpdate(JobTask entity) {
        String jobName = getJobName(entity);
        JobDataMap dataMap = getJobDataMap(entity);
        JobTask data = this.getOne(Wrappers.<JobTask>lambdaQuery().eq(JobTask::getJobName, jobName)).getData();
        if(entity.getId() != null) {
            if(!data.getId().equals(entity.getId())) {
                return new BooleanResult(false, "执行方法已经存在");
            }

            schedulerManager.updateJob(jobName, entity.getConCurrent(), dataMap, entity.getRemark());
        } else {
            schedulerManager.addJob(jobName, entity.getConCurrent(), entity.getCron(), dataMap, entity.getRemark());
        }

        return super.saveOrUpdate(entity);
    }

    private JobDataMap getJobDataMap(JobTask entity) {
        int indexOfArgs = entity.getMethod().indexOf("(");
        String args = entity.getMethod().substring(indexOfArgs, entity.getMethod().length() - 1);
        JobDataMap dataMap = new JobDataMap();
        if(args.startsWith("'") || args.startsWith("\"")) {
            dataMap.put("param", args.substring(1, args.length() - 1));
        } else if(args.startsWith("{")) {
            dataMap.put("param", JSONObject.parse(args));
        } else {
            dataMap.put("param", args);
        }

        return dataMap;
    }

    private String getJobName(JobTask entity) {
        String method = entity.getMethod();
        if(!StringUtils.hasText(method)) {
            throw new ServiceException("未指定要执行的方法");
        }

        int indexOfArgs = method.indexOf("(");
        return method.substring(0, indexOfArgs);
    }

    @Override
    @Transactional
    public BooleanResult removeByIds(Collection<? extends Serializable> idList) {
        this.listByIds(idList).stream().forEach(item -> {
            schedulerManager.removeJob(item.getJobName());
        });

        return super.removeByIds(idList);
    }
}
