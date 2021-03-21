package com.iteaj.framework.spring.condition;

import com.iteaj.framework.spring.boot.config.FrameworkAppAutoConfiguration;
import com.iteaj.framework.spring.boot.config.FrameworkWebAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * create time: 2021/3/21
 *
 * @author iteaj
 * @since 1.0
 */
public class FrameworkConditionalImpl implements Condition {

    private static List<Type> appTypes = new ArrayList<>();

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Type value = metadata.getAnnotations()
                .get(ConditionalOnFramework.class).getEnum("value", Type.class);

        if(CollectionUtils.isEmpty(appTypes)) {
            String[] names = context.getBeanFactory()
                    .getBeanNamesForAnnotation(SpringBootApplication.class);
            if(names != null && names.length == 1) {
                Object bean = context.getBeanFactory().getBean(names[0]);
                ImportAutoConfiguration autoConfiguration = AnnotationUtils
                        .findAnnotation(bean.getClass(), ImportAutoConfiguration.class);

                if(autoConfiguration != null) {
                    Class<?>[] classes = autoConfiguration.classes();
                    for (Class<?> aClass : classes) {
                        if(FrameworkWebAutoConfiguration.class.isAssignableFrom(aClass)) {
                            appTypes.add(Type.Web);
                        } else if(FrameworkAppAutoConfiguration.class.isAssignableFrom(aClass)) {
                            appTypes.add(Type.App);
                        }
                    }
                }
            }
        }

        return appTypes.contains(value);
    }
}
