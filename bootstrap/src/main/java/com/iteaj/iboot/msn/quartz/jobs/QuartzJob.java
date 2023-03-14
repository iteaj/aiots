package com.iteaj.iboot.msn.quartz.jobs;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Component
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface QuartzJob {

    /**
     * 作业说明
     * @return
     */
    String value() default "";
}
