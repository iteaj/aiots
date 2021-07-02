package com.iteaj.framework.spring.boot;

import com.iteaj.framework.spi.event.FrameworkEvent;
import com.iteaj.framework.spi.event.FrameworkListener;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.PayloadApplicationEvent;
import org.springframework.core.GenericTypeResolver;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create time: 2021/4/4
 *
 * @author iteaj
 * @since 1.0
 */
public class FrameworkPayloadListener implements ApplicationListener<PayloadApplicationEvent> {

    private Map<Class, List<FrameworkListener>> listenerMap;

    @Override
    public void onApplicationEvent(PayloadApplicationEvent event) {
        final ConfigurableApplicationContext source = (ConfigurableApplicationContext)event.getSource();

        if(listenerMap == null) {
            final ObjectProvider<FrameworkListener> beanProvider = source
                    .getBeanFactory().getBeanProvider(FrameworkListener.class);

            listenerMap = new HashMap<>();
            beanProvider.forEach(item -> {
                final Class<?> aClass = GenericTypeResolver
                        .resolveTypeArgument(item.getClass(), FrameworkListener.class);

                final List<FrameworkListener> listeners = listenerMap.get(aClass);
                if(listeners == null) {
                    listenerMap.put(aClass, Arrays.asList(item));
                } else {
                    listeners.add(item);
                }
            });
        }

        if(event.getPayload() instanceof FrameworkEvent) {
            final List<FrameworkListener> listeners = listenerMap.get(event.getPayload().getClass());
            if(listeners != null) {
                listeners.forEach(item -> {
                    item.onFrameworkEvent((FrameworkEvent) event.getPayload());
                });
            }
        }
    }
}
