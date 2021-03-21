package com.iteaj.framework.security;

import org.springframework.core.Ordered;

import java.util.LinkedHashMap;
import java.util.Map;

public class OrderFilterChainDefinition implements Ordered {
    private final Map<String, String> filterChainDefinitionMap = new LinkedHashMap();

    public OrderFilterChainDefinition addPathDefinition(String antPath, String definition) {
        this.filterChainDefinitionMap.put(antPath, definition);
        return this;
    }

    public OrderFilterChainDefinition addPathDefinitions(Map<String, String> pathDefinitions) {
        this.filterChainDefinitionMap.putAll(pathDefinitions);

        return this;
    }

    public Map<String, String> getFilterChainMap() {
        return this.filterChainDefinitionMap;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
