package com.iteaj.iboot.msn.quartz.jobs;

import com.iteaj.iboot.msn.quartz.QuartzJob;
import com.iteaj.iboot.msn.quartz.QuartzJobContext;
import com.iteaj.util.CommonUtils;
import org.quartz.JobDetail;
import org.quartz.*;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringSupportJob implements Job, JobFactory, BeanFactoryAware, BeanPostProcessor {

    private AutowireCapableBeanFactory beanFactory;
    public final static Class<EmptyJob> EMPTY_JOB = EmptyJob.class;
    public final static String JOB_EXPRESSION = "expression";
    private Logger logger = LoggerFactory.getLogger(SpringSupportJob.class);
    private Map<String, QuartzJobContext> cache = new ConcurrentHashMap<>(16);
    public final static Class<NonConcurrentEmptyJob> NON_CONCURRENT_EMPTY_JOB = NonConcurrentEmptyJob.class;

    @Override
    public Job newJob(TriggerFiredBundle triggerFiredBundle, Scheduler scheduler) throws SchedulerException {
        final Class<? extends Job> jobClass = triggerFiredBundle.getJobDetail().getJobClass();
        if(EmptyJob.class.isAssignableFrom(jobClass)) {
            return this;
        }

        final boolean containsBean = beanFactory.containsBean(jobClass.getName());
        if(containsBean) {
            return beanFactory.getBean(jobClass);
        }

        final Job job = BeanUtils.instantiateClass(jobClass);
        beanFactory.autowireBean(job);
        return job;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        final JobDetail jobDetail = jobExecutionContext.getJobDetail();
        final JobDataMap dataMap = jobDetail.getJobDataMap();
        final String expression = dataMap.getString(JOB_EXPRESSION);

        final QuartzJobContext context = this.getContext(expression);
        if(context != null) {
            if(context != null) {
                final Object result = context.invoke(expression, jobExecutionContext);
                jobExecutionContext.setResult(result);
            } else {
                jobExecutionContext.setResult("方法不存在或者不能访问["+expression+"]");
                logger.warn("[quartz]执行方法不存在或者不能访问["+expression+"]");
            }
        } else {
            jobExecutionContext.setResult("job["+ jobDetail.getKey().getName()+"]没有指定要执行的方法");
            logger.warn("[quartz]没有找到对应的执行方法["+expression+"]");
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (AutowireCapableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final QuartzJob annotation = bean.getClass().getAnnotation(QuartzJob.class);
        if(annotation != null) {

            ReflectionUtils.doWithLocalMethods(bean.getClass(), method -> {
                if(!Modifier.isPublic(method.getModifiers())) {
                    return;
                }

                final String name = method.getName();
                final QuartzJobContext context = cache.get(name);
                if(context != null) {
                    throw new BeanCreationException(String.format("定时任务方法重复[%s.%s -> %s.%s]"
                            , beanName, name, context.getBean().getClass().getSimpleName(), name));
                } else {
                    cache.put(name, new QuartzJobContext(bean, method));
                }
            });
        }

        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    public QuartzJobContext getContext(String expression) {
        if(CommonUtils.isNotBlank(expression)) {
            return cache.get(expression.substring(0, expression.indexOf("(")));
        } else {
            return null;
        }
    }

    /**
     * 运行并发执行
     */
    public static class EmptyJob implements Job {
        protected EmptyJob() {}

        @Override
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException { }
    }

    /**
     * 不允许并发执行的作业
     */
    @DisallowConcurrentExecution
    @PersistJobDataAfterExecution
    public static class NonConcurrentEmptyJob extends EmptyJob {
        protected NonConcurrentEmptyJob() {}
    }
}
