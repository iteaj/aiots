package com.iteaj.framework.spi.iot.view;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.exception.FrameworkException;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.spi.iot.view.chart.ChartApiItem;
import com.iteaj.framework.spi.iot.view.chart.ChartApiParams;
import com.iteaj.framework.spi.iot.view.chart.ChartFetchResult;
import com.iteaj.framework.spi.iot.view.global.GlobalApi;
import com.iteaj.framework.spi.iot.view.global.GlobalApiParams;
import com.iteaj.framework.spi.iot.view.global.GlobalFuncApi;
import com.iteaj.framework.spi.iot.view.global.GlobalFuncApiParams;
import com.iteaj.framework.spi.iot.view.table.TableApiItem;
import com.iteaj.framework.spi.iot.view.table.TableApiParams;
import com.iteaj.framework.spi.iot.view.table.TableApiResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ViewApiManager {

    private static Map<String, ViewApi> itemMap = new LinkedHashMap<>(16);

    public static void initAndRegister(ViewApi item) {
        if(!itemMap.containsKey(item.getKey())) {
            itemMap.put(item.getKey(), item.init());
        } else {
            throw new FrameworkException("大屏视图接口冲突[" + item.getKey() + "]");
        }
    }

    public static boolean containsKey(String key) {
        return itemMap.containsKey(key);
    }

    public static List<DataItemOption> chartOptions() {
        return itemMap.values().stream()
                .filter(item -> item.getApiType() == ViewApiType.CHART_API)
                .flatMap(item -> item.basicOptions().stream())
                .collect(Collectors.toList());
    }

    public static List<DataItemOption> globalApiOptions() {
        return itemMap.values().stream()
                .filter(item -> item.getApiType() == ViewApiType.GLOBAL_API)
                .flatMap(item -> item.basicOptions().stream())
                .collect(Collectors.toList());
    }

    public static List<DataItemOption> globalFuncApiOptions() {
        return itemMap.values().stream()
                .filter(item -> item.getApiType() == ViewApiType.GLOBAL_FUNC_API)
                .flatMap(item -> item.basicOptions().stream())
                .collect(Collectors.toList());
    }

    public static List<DataItemOption> tableApiOptions() {
        return itemMap.values().stream()
                .filter(item -> item.getApiType() == ViewApiType.TABLE_API)
                .flatMap(item -> item.basicOptions().stream())
                .collect(Collectors.toList());
    }

    public static List<DataItemOption> loadOptions(String parentKey) {
        if(StrUtil.isBlank(parentKey)) {
            throw new ServiceException("参数错误");
        }
        final String[] split = parentKey.split(DataItemOption.SPLIT);
        final ChartApiItem chartApiItem = (ChartApiItem) itemMap.get(split[0]);
        if(chartApiItem == null) {
            throw new ServiceException("未找到对应的接口项["+split[0]+"]");
        }

        return chartApiItem.loadOptions(split);
    }

    public static TableApiResult fetch(TableApiParams params) {
        if(StrUtil.isBlank(params.getKey())) {
            throw new ServiceException("请求参数异常");
        }

        final String func = params.getKey(0);
        final TableApiItem viewApi = (TableApiItem) itemMap.get(func);
        if(viewApi == null) {
            throw new ServiceException("未找到数据项处理器");
        }

        return viewApi.fetch(params);
    }

    public static GlobalApiParams fetch(GlobalApiParams params) {
        if(StrUtil.isBlank(params.getKey())) {
            throw new ServiceException("请求参数异常");
        }

        final String func = params.resolveKeyItem(0);
        final GlobalApi viewApi = (GlobalApi) itemMap.get(func);
        if(viewApi == null) {
            throw new ServiceException("未找到数据项处理器");
        }

        return viewApi.fetch(params);
    }

    public static void invoke(GlobalFuncApiParams params) {
        if(StrUtil.isBlank(params.getApiKey())) {
            throw new ServiceException("请求参数异常");
        }

        final String func = params.resolveKeyItem(0);
        final GlobalFuncApi viewApi = (GlobalFuncApi) itemMap.get(func);
        if(viewApi == null) {
            throw new ServiceException("未找到数据项处理器");
        }

        viewApi.invoke(params);
    }

    public static List<ChartFetchResult> fetch(ChartApiParams params) {
        final List<ChartApiParams.Value> values = params.getValues();
        if(CollectionUtil.isNotEmpty(values)) {
            final List<String> types = values.stream()
                    .map(item -> item.getValueItem(0))
                    .distinct().collect(Collectors.toList());
            if(types.size() != 1) {
                throw new ServiceException("数据项不支持混合统计");
            }

            final ChartApiItem apiItem = (ChartApiItem) itemMap.get(types.get(0));
            if(apiItem == null) {
                throw new ServiceException("未找到数据项处理器");
            }

            return apiItem.fetch(params.getValues(), params);
        } else {
            throw new ServiceException("没有提供任何数据项");
        }
    }
}
