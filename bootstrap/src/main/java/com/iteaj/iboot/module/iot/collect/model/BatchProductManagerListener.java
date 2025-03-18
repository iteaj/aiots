package com.iteaj.iboot.module.iot.collect.model;

import com.iteaj.framework.spi.iot.BatchOfPoint;
import com.iteaj.framework.spi.iot.listener.EntityPayload;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.StatusSwitchListener;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BatchProductManagerListener implements StatusSwitchListener {

    public static final Map<String, List<List<BatchOfPoint>>> NULL_POINTS = new HashMap<>();
    private static Map<Long, Map<String, List<List<BatchOfPoint>>>> productModelApis = new HashMap<>(16);

    @Override
    public boolean isEventMatcher(IotEventType eventType) {
        return eventType == IotEventType.ProductSwitch;
    }

    @Override
    public void onIotEvent(IotEventType eventType, EntityPayload payload) {
        ProductDto productDto = (ProductDto) payload.getPayload();
        final FuncStatus status = productDto.getStatus();
        if(status == FuncStatus.disabled) {
            productModelApis.remove(productDto.getId());
        }
    }

    public static Map<String, List<List<BatchOfPoint>>> getPoints(Long productId) {
        return productModelApis.get(productId);
    }

    public static synchronized void register(Long productId, Map<String, List<List<BatchOfPoint>>> points) {
        if(!productModelApis.containsKey(productId)) {
            productModelApis.put(productId, points);
        }
    }
}
