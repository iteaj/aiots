package com.iteaj.plguin.protocol.hj212.network;

import com.iteaj.framework.spi.iot.DirectProtocolHandleAbstract;
import com.iteaj.framework.spi.iot.data.DataCollectPublishService;
import com.iteaj.framework.spi.iot.data.IModelAttrRealtimeDataService;
import com.iteaj.framework.spi.iot.data.ModelAttrMetaService;

public class HJ212RealtimeProtocolHandle extends DirectProtocolHandleAbstract<HJ212RealtimeProtocol> {

    public HJ212RealtimeProtocolHandle(ModelAttrMetaService modelAttrMetaService, DataCollectPublishService dataCollectPublishService, IModelAttrRealtimeDataService modelAttrRealtimeDataService) {
        super(modelAttrMetaService, dataCollectPublishService, modelAttrRealtimeDataService);
    }

    @Override
    public Object doHandle(HJ212RealtimeProtocol protocol) {
        return null;
    }
}
