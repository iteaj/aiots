package com.iteaj.framework.spi.iot;

import com.iteaj.framework.spi.iot.resolver.DefaultResolver;

import java.util.HashMap;
import java.util.Map;

public class DataTypeResolverFactory {

    private final static String DEFAULT = "DEFAULT:RESOLVER";
    private final static DataTypeResolver defaultResolver = new DefaultResolver();
    private final static Map<String, DataTypeResolver> resolverMap = new HashMap<>();

    static {
        resolverMap.put(DEFAULT, defaultResolver);
    }

    public static DataTypeResolver get(String name) {
        return resolverMap.get(name);
    }

    public static DataTypeResolver getDefault() {
        return defaultResolver;
    }

    public static boolean contains(String name) {
        return resolverMap.containsKey(name);
    }

    public static void register(String name, DataTypeResolver resolver) {
        resolverMap.put(name, resolver);
    }

    public static DataTypeResolver update(String name, DataTypeResolver resolver) {
        return resolverMap.putIfAbsent(name, resolver);
    }
}
