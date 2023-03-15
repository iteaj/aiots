package com.iteaj.iboot.msn.quartz.service.impl;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.BaseServiceImpl;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.result.BooleanResult;
import com.iteaj.iboot.msn.quartz.entity.JobTask;
import com.iteaj.iboot.msn.quartz.jobs.SpringSupportJob;
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
        JobDataMap dataMap = getJobDataMap(entity);
        if(entity.getId() != null) {
            schedulerManager.updateJob(entity.getJobName(), entity.getConcurrent(), dataMap, entity.getRemark());
        } else {
            String jobName = getJobName(entity);
            entity.setJobName(jobName);
            schedulerManager.addJob(jobName, entity.getConcurrent(), entity.getCron(), dataMap, entity.getRemark());
        }

        return super.saveOrUpdate(entity);
    }

    private JobDataMap getJobDataMap(JobTask entity) {
        int indexOfArgs = entity.getMethod().indexOf("(");
        String args = entity.getMethod().substring(indexOfArgs + 1, entity.getMethod().length() - 1);
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(SpringSupportJob.JOB_EXPRESSION, entity.getMethod().substring(0, indexOfArgs));
        if(args.equals("")) {
            return dataMap;
        } else if(args.startsWith("'") || args.startsWith("\"")) {
            dataMap.put("param", args.substring(1, args.length() - 1));
        } else if(args.startsWith("{")) {
            dataMap.put("param", JSONObject.parse(args));
        } else {
            dataMap.put("param", args);
        }

        return dataMap;
    }

    private String getJobName(JobTask entity) {
        return UUID.fastUUID().toString(false);
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
