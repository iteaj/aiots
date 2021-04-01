package com.iteaj.framework.spi.event.spring;

import com.iteaj.framework.spi.event.FrameworkListener;
import org.springframework.context.ApplicationListener;

/**
 * create time: 2021/3/31
 *
 * @author iteaj
 * @since 1.0
 */
public class SpringListener implements ApplicationListener<SpringEvent>, FrameworkListener<SpringEvent> {

    @Override
    public void onApplicationEvent(SpringEvent event) {
        this.onEvent(event);
    }

    @Override
    public void onEvent(SpringEvent event) {

    }
}
