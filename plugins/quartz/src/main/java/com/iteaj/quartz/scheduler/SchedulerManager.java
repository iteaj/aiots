package com.iteaj.quartz.scheduler;

import org.quartz.*;

/**
 * create time: 2020/7/19
 *
 * @author iteaj
 * @since 1.0
 */
public class SchedulerManager {

    private final Scheduler scheduler;
    private String JOB_GROUP_NAME;
    private String TRIGGER_GROUP_NAME;

    public SchedulerManager(Scheduler scheduler, String JOB_GROUP_NAME, String TRIGGER_GROUP_NAME) {
        this.scheduler = scheduler;
        this.JOB_GROUP_NAME = JOB_GROUP_NAME;
        this.TRIGGER_GROUP_NAME = TRIGGER_GROUP_NAME;
    }

    /**
     * @Description: 添加一个定时任务，使用默认的任务组名，触发器名，触发器组名
     * @param jobName
     *            任务名
     * @param cls
     *            任务
     */
    public void addJob(String jobName, Class cls, String cron, JobDataMap map) {
        addJob(jobName, JOB_GROUP_NAME, jobName, TRIGGER_GROUP_NAME, cls, cron, map);
    }

    /**
     * @Description: 添加一个定时任务
     * @param jobName
     *            任务名
     * @param jobGroupName
     *            任务组名
     * @param triggerName
     *            触发器名
     * @param triggerGroupName
     *            触发器组名
     * @param jobClass
     *            任务
     */
    public void addJob(String jobName, String jobGroupName, String triggerName
            , String triggerGroupName, Class jobClass, String cron, JobDataMap map) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if(jobDetail != null) throw new RuntimeException("作业已经存在: " + jobKey);

            map = map == null ? new JobDataMap() : map;
            JobDetail job = JobBuilder.newJob(jobClass).withIdentity(jobName
                    , jobGroupName).usingJobData(map).build();

            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);

            // 按新的cronExpression表达式构建一个新的trigger
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(triggerName, triggerGroupName)
                    .withSchedule(scheduleBuilder).build();

            // 交给scheduler去调度
            scheduler.scheduleJob(job, trigger);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 修改一个任务的触发时间(使用默认的任务组名，触发器名，触发器组名)
     * @param jobName
     */
    public void updateJobTime(String jobName, String cron) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, TRIGGER_GROUP_NAME);

        try {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null) throw new RuntimeException("触发器不存在: " + triggerKey);

            String oldTime = trigger.getCronExpression();
            if (!oldTime.equalsIgnoreCase(cron)) {
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
                // 按新的cronExpression表达式重新构建trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                // 按新的trigger重新设置job执行
                scheduler.rescheduleJob(triggerKey, trigger);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 修改任务名，任务类，触发时间
     * @param oldJobName ：原任务名
     * @param jobName
     * @param jobClass
     * @param cron
     * @date 2018年5月23日 上午9:13:10
     */
    public void updateJob(String oldJobName, String jobName, Class jobClass, String cron) {

        TriggerKey triggerKey = TriggerKey.triggerKey(oldJobName, TRIGGER_GROUP_NAME);
        JobKey jobKey = JobKey.jobKey(oldJobName, JOB_GROUP_NAME);
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if(null == jobDetail) throw new RuntimeException("查无此Job: " + jobKey);
            JobDataMap oriDataMap = jobDetail.getJobDataMap();

            Trigger trigger = scheduler.getTrigger(triggerKey);
            if (trigger == null) throw new RuntimeException("触发器不存在: " + triggerKey);

            scheduler.pauseTrigger(triggerKey);// 停止触发器
            scheduler.unscheduleJob(triggerKey);// 移除触发器
            scheduler.deleteJob(jobKey);// 删除任务

            addJob(jobName, jobClass, cron, oriDataMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 修改一个任务的触发时间
     * @param triggerName
     * @param triggerGroupName
     */
    public void updateTriggerCron(String triggerName, String triggerGroupName, String cron) {
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroupName);
        try {

            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            if (trigger == null)  throw new RuntimeException("触发器不存在: " + triggerKey);

            String oldTime = trigger.getCronExpression();

            if (!oldTime.equalsIgnoreCase(cron)) {
                // trigger已存在，则更新相应的定时设置
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cron);

                // 按新的cronExpression表达式重新构建trigger
                trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

                // 按新的trigger重新设置job执行
                scheduler.resumeTrigger(triggerKey);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 移除一个任务(使用默认的任务组名，触发器名，触发器组名)
     * @param jobName
     */
    public void removeJob(String jobName) {
        removeJob(jobName, JOB_GROUP_NAME, jobName, TRIGGER_GROUP_NAME);
    }

    /**
     * 移除一个任务
     * @param jobName
     * @param jobGroupName
     * @param triggerName
     * @param triggerGroupName
     */
    public void removeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName) {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, triggerGroupName);
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        try {

            Trigger trigger = scheduler.getTrigger(triggerKey);
            if (trigger == null) return;

            scheduler.pauseTrigger(triggerKey);// 停止触发器
            scheduler.unscheduleJob(triggerKey);// 移除触发器
            scheduler.deleteJob(jobKey);// 删除任务
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 暂停一个任务(使用默认组名)
     * @param jobName
     */
    public void pauseJob(String jobName) {
        JobKey jobKey = JobKey.jobKey(jobName, JOB_GROUP_NAME);
        try {
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 暂停一个任务
     * @param jobName
     * @param jobGroupName
     */
    public void pauseJob(String jobName, String jobGroupName) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        try {
            scheduler.pauseJob(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 恢复一个任务(使用默认组名)
     * @param jobName
     */
    public void resumeJob(String jobName) {
        resumeJob(jobName, JOB_GROUP_NAME);
    }

    /**
     * 恢复一个任务
     * @param jobName
     * @param jobGroupName
     */
    public void resumeJob(String jobName, String jobGroupName) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        try {
            scheduler.resumeJob(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 启动所有定时任务
     */
    public void startJobs() {
        try {
            scheduler.start();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @Description 关闭所有定时任务
     */
    public void shutdownJobs() {
        try {
            if (!scheduler.isShutdown()) {
                scheduler.shutdown();
            }
	    } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 立即运行任务，只会运行一次，方便测试时用。
     * @param jobName
     */
    public void fireJob(String jobName) {
        fireJob(jobName, JOB_GROUP_NAME);
    }

    /**
     * 立即运行任务，只会运行一次，方便测试时用。
     * @param jobName
     * @param jobGroupName
     */
    public void fireJob(String jobName, String jobGroupName) {
        JobKey jobKey = JobKey.jobKey(jobName, jobGroupName);
        try {
            scheduler.triggerJob(jobKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 获取任务状态
     * 		NONE: 不存在
     * 		NORMAL: 正常
     * 		PAUSED: 暂停
     * 		COMPLETE:完成
     * 		ERROR : 错误
     * 		BLOCKED : 阻塞
     * @param jobName 触发器名, 默认作业名
     */
    public Trigger.TriggerState getTriggerState(String jobName){
        return getTriggerState(jobName, TRIGGER_GROUP_NAME);
    }

    public Trigger.TriggerState getTriggerState(String triggerName, String triggerGroup){
        TriggerKey triggerKey = TriggerKey.triggerKey(triggerName, triggerGroup);
        try {
            return scheduler.getTriggerState(triggerKey);
        } catch (SchedulerException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
