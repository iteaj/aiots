package com.iteaj.framework.spi.iot;

import com.iteaj.framework.IVOption;
import com.iteaj.framework.spi.iot.resolver.DefaultResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataValueResolverFactory {

    private final static String DEFAULT_KEY = "DEFAULT";
    private final static DataValueResolver defaultResolver = new DefaultResolver();
    private final static Map<String, DataValueResolver> resolverMap = new HashMap<>();

    static {
        resolverMap.put(DEFAULT_KEY, defaultResolver);
    }

    public static DataValueResolver get(String key) {
        return resolverMap.get(key);
    }

    public static DataValueResolver getDefault() {
        return defaultResolver;
    }

    public static boolean contains(String key) {
        return resolverMap.containsKey(key);
    }

    public static void register(String key, DataValueResolver resolver) {
        resolverMap.put(key, resolver);
    }

    public static DataValueResolver update(String key, DataValueResolver resolver) {
        return resolverMap.putIfAbsent(key, resolver);
    }

    public static List<IVOption> options() {
        List<IVOption> options = new ArrayList<>();
        resolverMap.forEach((key, value) -> options.add(new IVOption(value.name(), key)));
        return options;
    }
}
