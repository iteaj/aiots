package com.iteaj.quartz.config;

import com.iteaj.framework.spi.admin.Module;
import com.iteaj.quartz.demo.DemoJob;
import com.iteaj.quartz.jobs.AutowireJobFactory;
import com.iteaj.quartz.scheduler.SchedulerManager;
import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * create time: 2020/7/18
 *
 * @author iteaj
 * @since 1.0
 */
@Configuration
@EnableScheduling
public class QuartzConfiguration implements InitializingBean {

    @Autowired
    private SchedulerManager sysSchedulerManager;

    @Bean
    public Module quartzModule() {
        return new Module() {
            @Override
            public int getOrder() {
                return 100;
            }

            @Override
            public String getMsn() {
                return "quartz";
            }

            @Override
            public String getName() {
                return "定时任务";
            }
        };
    }

    @Bean("sysSchedulerFactoryBean")
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory){

        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();

        // 去除默认的JobFactory, 使用可注入Bean的JobFactory
        factoryBean.setJobFactory(jobFactory);

        // 设置Quartz相关配置
        factoryBean.setConfigLocation(new ClassPathResource("quartz.properties"));


        // 使用数据库存储时需要用到的数据源
//        factoryBean.setDataSource(this.dataSource);

        return factoryBean;
    }

    @Bean("sysScheduler")
    public Scheduler sysScheduler(SchedulerFactoryBean sysSchedulerFactoryBean) {
        return sysSchedulerFactoryBean.getScheduler();
    }

    @Bean("sysSchedulerManager")
    public SchedulerManager sysSchedulerManager(Scheduler sysScheduler) {
        return new SchedulerManager(sysScheduler
                , "系统", "系统");
    }

    @Bean
    public JobFactory jobFactory() {
        return new AutowireJobFactory();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if(!sysSchedulerManager.exists("demo")) {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("demo", true);
            jobDataMap.put("author", "iteaj");

            sysSchedulerManager.addJob("demo", DemoJob.class
                    , "18 18 18 * * ?", jobDataMap, "系统作业调度测试");
        } else {
            sysSchedulerManager.fireJob("demo");
        }
    }
}
