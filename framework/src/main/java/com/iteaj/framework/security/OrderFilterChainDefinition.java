package com.iteaj.framework.security;

import cn.hutool.core.util.ArrayUtil;
import org.springframework.core.Ordered;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrderFilterChainDefinition implements Ordered {

    private final Map<String, String> filterChainDefinitionMap = new LinkedHashMap();

    /**
     * 增加匿名路径
     * @param antPath
     * @return
     */
    public OrderFilterChainDefinition addAnon(String... antPath) {
        if(ArrayUtil.isNotEmpty(antPath)) {
            Arrays.stream(antPath).forEach(item -> {
                this.filterChainDefinitionMap.put(item, "anon");
            });
        }

        return this;
    }

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
