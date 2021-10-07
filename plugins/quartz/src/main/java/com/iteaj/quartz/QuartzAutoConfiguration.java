package com.iteaj.quartz;

import com.iteaj.framework.spi.admin.DefaultModule;
import com.iteaj.framework.spi.admin.Module;
import com.iteaj.quartz.jobs.SpringSupportJob;
import com.iteaj.quartz.scheduler.SchedulerManager;
import org.mybatis.spring.annotation.MapperScan;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;

/**
 * create time: 2020/7/18
 *  默认定时任务配置
 * @author iteaj
 * @since 1.0
 */
@Configuration
@EnableScheduling
@MapperScan("com.iteaj.quartz.mapper")
public class QuartzAutoConfiguration implements BeanFactoryPostProcessor, BeanDefinitionRegistryPostProcessor {

    @Bean
    public Module quartzModule() {
        return DefaultModule.build("quartz", "定时任务", 60);
    }

    @Bean("schedulerFactoryBean")
    public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory, DataSource dataSource){

        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();

        // 去除默认的JobFactory, 使用可注入Bean的JobFactory
        factoryBean.setJobFactory(jobFactory);

        // 设置Quartz相关配置
        factoryBean.setConfigLocation(new ClassPathResource("quartz.properties"));

        // 延时20秒启动
        factoryBean.setStartupDelay(20);
        // 使用数据库存储时需要用到的数据源
        factoryBean.setDataSource(dataSource);
        return factoryBean;
    }

    @Bean("scheduler")
    public Scheduler sysScheduler(SchedulerFactoryBean schedulerFactoryBean) {
        return schedulerFactoryBean.getScheduler();
    }

    @Bean("schedulerManager")
    public SchedulerManager sysSchedulerManager(Scheduler scheduler) {
        return new SchedulerManager(scheduler, Scheduler.DEFAULT_GROUP, Scheduler.DEFAULT_GROUP);
    }

//    @Bean
//    public JobFactory jobFactory() {
//        return new SpringSupportJob();
//    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        final SpringSupportJob bean = beanFactory.getBean(SpringSupportJob.class);
        beanFactory.addBeanPostProcessor(bean);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        final boolean b = registry.containsBeanDefinition(SpringSupportJob.class.getName());
        if(b) {
            return;
        } else {
            final GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(SpringSupportJob.class);
            beanDefinition.setAutowireCandidate(true);
            registry.registerBeanDefinition(SpringSupportJob.class.getName(), beanDefinition);
        }
    }
}
