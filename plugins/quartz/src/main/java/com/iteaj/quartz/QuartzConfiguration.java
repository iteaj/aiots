package com.iteaj.quartz;

import com.iteaj.quartz.demo.DemoJob;
import com.iteaj.quartz.jobs.AutowireJobFactory;
import com.iteaj.quartz.scheduler.SchedulerManager;
import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Date;

/**
 * create time: 2020/7/18
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
@EnableScheduling
public class QuartzConfiguration {

    @Bean("defaultSchedulerFactoryBean")
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory){

        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();

        // 去除默认的JobFactory, 使用可注入Bean的JobFactory
        factoryBean.setJobFactory(jobFactory);

        // 设置Quartz相关配置
        factoryBean.setConfigLocation(new ClassPathResource("quartz.properties"));

        factoryBean.setOverwriteExistingJobs(true);

        // 使用数据库存储时需要用到的数据源
//        factoryBean.setDataSource(this.dataSource);

        return factoryBean;
    }

    @Bean("defaultScheduler")
    public Scheduler quartzScheduler(SchedulerFactoryBean defaultSchedulerFactoryBean) {
        Scheduler scheduler = defaultSchedulerFactoryBean.getScheduler();
        try {
            // 如果没有任何作业存在, 新增一个
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("demo", true);
            jobDataMap.put("author", "iteaj");

            JobDetail jobDetail = JobBuilder.newJob(DemoJob.class).usingJobData(jobDataMap)
                    .withIdentity("DemoJob", "DemoJobGroup")
                    .withDescription("this is job demo").build();

            Date startTime = new Date();
            startTime.setTime(startTime.getTime()+6000L);

            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(jobDetail).withIdentity("DemoTrigger", "DemoTriggerGroup")
                    .withDescription("this is trigger demo").startAt(startTime).build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return scheduler;
    }

    @Bean("DefaultSchedulerManager")
    public SchedulerManager schedulerManager(Scheduler defaultScheduler) {
        return new SchedulerManager(defaultScheduler
                , "DEFAULT_JOB_GROUP"
                , "DEFAULT_TRIGGER_GROUP");
    }

    @Bean
    public JobFactory jobFactory() {
        return new AutowireJobFactory();
    }
}
