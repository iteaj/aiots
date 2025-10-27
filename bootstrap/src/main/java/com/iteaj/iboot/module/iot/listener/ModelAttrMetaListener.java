package com.iteaj.iboot.module.iot.listener;

import com.iteaj.framework.spi.iot.data.ModelAttrMeta;
import com.iteaj.framework.spi.iot.data.ModelAttrMetaService;
import com.iteaj.framework.spi.iot.listener.EntityPayload;
import com.iteaj.framework.spi.iot.listener.IotEventType;
import com.iteaj.framework.spi.iot.listener.StatusSwitchListener;
import com.iteaj.iboot.module.iot.consts.FuncStatus;
import com.iteaj.iboot.module.iot.dto.ProductDto;
import com.iteaj.iboot.module.iot.service.IModelAttrService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ModelAttrMetaListener implements ModelAttrMetaService, StatusSwitchListener {

    private final IModelAttrService modelAttrService;
    private final Map<String, List<ModelAttrMeta>> modelAttrMetaCache = new ConcurrentHashMap<>(32);

    public ModelAttrMetaListener(IModelAttrService modelAttrService) {
        this.modelAttrService = modelAttrService;
    }

    @Override
    public List<ModelAttrMeta> getAttrMetas(String productCode) {
        List<ModelAttrMeta> modelAttrMetas = modelAttrMetaCache.get(productCode);
        return modelAttrMetas != null ? modelAttrMetas : Collections.emptyList();
    }

    @Override
    public boolean isEventMatcher(IotEventType eventType) {
        return eventType == IotEventType.ProductSwitch;
    }

    @Override
    public void onIotEvent(IotEventType eventType, EntityPayload entity) {
        ProductDto payload = (ProductDto) entity.getPayload();
        if(payload.getStatus() == FuncStatus.disabled) {
            modelAttrMetaCache.remove(payload.getCode());
        } else {
            List modelAttrs = modelAttrService.listDetailByProductId(payload.getId());
            modelAttrMetaCache.put(payload.getCode(), modelAttrs);
        }
    }
}
