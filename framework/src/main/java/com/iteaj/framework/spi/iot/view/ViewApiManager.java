package com.iteaj.framework.spi.iot.view;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.exception.ServiceException;

import java.util.ArrayList;
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

    public static List<ViewFetchResult> fetch(ViewApiParams params) {
        List<ViewFetchResult> result = new ArrayList<>();
        final List<ViewApiParams.Value> values = params.getValues();
        if(CollectionUtil.isNotEmpty(values)) {
            final Map<String, List<ViewApiParams.Value>> listMap = values.stream()
                    .collect(Collectors.groupingBy(item -> item.getValueItem(0)));
            listMap.forEach((key, valueList) -> {
                if(!itemMap.containsKey(key)) {
                    throw new ServiceException("未找到处理接口");
                }
            });

            listMap.forEach((key, valueList) -> {
                final ViewApiItem viewApiItem = itemMap.get(key);
                final List<ViewFetchResult> results = viewApiItem.fetch(valueList, params);
                result.addAll(results);
            });
        } else {
            throw new ServiceException("没有提供统计项");
        }

        return result;
    }
}
