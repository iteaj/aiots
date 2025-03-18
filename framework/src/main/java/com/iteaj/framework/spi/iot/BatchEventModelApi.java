package com.iteaj.framework.spi.iot;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface BatchEventModelApi {

    /**
     * 做地址的批量转换
     * @param points
     * @return
     */
    Map<String, List<List<BatchOfPoint>>> resolver(List<BatchOfPoint> points);

    /**
     * 批量获取点位
     * @param arg 调用参数
     * @param points 点位
     * @return
     */
     void invoke(ProtocolModelApiInvokeParam arg, Map<String, List<List<BatchOfPoint>>> points, Consumer<BatchResult> consumer);
}
