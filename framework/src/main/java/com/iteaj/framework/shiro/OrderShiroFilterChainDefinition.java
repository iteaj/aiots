package com.iteaj.framework.shiro;

import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.springframework.core.Ordered;

public class OrderShiroFilterChainDefinition
        extends DefaultShiroFilterChainDefinition implements Ordered {
    @Override
    public int getOrder() {
        return 0;
    }
}
