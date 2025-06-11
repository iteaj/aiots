package com.iteaj.framework.spi.iot.view;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.exception.ServiceException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ViewApiManager {

    private static Map<String, ViewApiItem> itemMap = new LinkedHashMap<>(16);

    public static void register(ViewApiItem item) {
        if(!itemMap.containsKey(item.getKey())) {
            itemMap.put(item.getKey(), item);
        }
    }

    public static void initAndRegister(ViewApiItem item) {
        if(!itemMap.containsKey(item.getKey())) {
            itemMap.put(item.getKey(), item.init());
        }
    }

    public static boolean containsKey(String key) {
        return itemMap.containsKey(key);
    }

    public static List<DataItemOption> options() {
        return itemMap.values().stream()
                .flatMap(item -> item.basicOptions().stream())
                .collect(Collectors.toList());
    }

    public static List<DataItemOption> loadOptions(String parentKey) {
        if(StrUtil.isBlank(parentKey)) {
            throw new ServiceException("参数错误");
        }
        final String[] split = parentKey.split(DataItemOption.SPLIT);
        final ViewApiItem viewApiItem = itemMap.get(split[0]);
        if(viewApiItem == null) {
            throw new ServiceException("未找到对应的接口项["+split[0]+"]");
        }

        return viewApiItem.loadOptions(split);
    }

    public static Object fetch(ViewApiParams params) {
        return null;
    }
}
