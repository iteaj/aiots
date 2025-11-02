package com.iteaj.framework.spi.iot;

import com.iteaj.framework.spi.iot.data.DataCollectPublishService;
import com.iteaj.framework.spi.iot.data.IModelAttrRealtimeDataService;
import com.iteaj.framework.spi.iot.data.ModelAttrMetaService;
import com.iteaj.iot.Protocol;

public abstract class DirectProtocolHandleAbstract<T extends Protocol> implements DirectProtocolHandle<T>{

    private final ModelAttrMetaService modelAttrMetaService;
    private final DataCollectPublishService dataCollectPublishService;
    private final IModelAttrRealtimeDataService modelAttrRealtimeDataService;

    public DirectProtocolHandleAbstract(ModelAttrMetaService modelAttrMetaService
            , DataCollectPublishService dataCollectPublishService
            , IModelAttrRealtimeDataService modelAttrRealtimeDataService) {
        this.modelAttrMetaService = modelAttrMetaService;
        this.dataCollectPublishService = dataCollectPublishService;
        this.modelAttrRealtimeDataService = modelAttrRealtimeDataService;
    }

    @Override
    public ModelAttrMetaService getModelAttrMetaService() {
        return modelAttrMetaService;
    }

    @Override
    public IModelAttrRealtimeDataService getRealtimeDataService() {
        return modelAttrRealtimeDataService;
    }

    @Override
    public DataCollectPublishService getDataCollectPublishService() {
        return dataCollectPublishService;
    }
}
