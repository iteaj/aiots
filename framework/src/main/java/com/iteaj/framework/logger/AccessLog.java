package com.iteaj.framework.logger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * create time: 2021/7/4
 *  访问日志
 * @author iteaj
 * @since 1.0
 */
@Target(ElementType.METHOD)
public @interface AccessLog {

    String value() default "";

    String type() default "";
}
