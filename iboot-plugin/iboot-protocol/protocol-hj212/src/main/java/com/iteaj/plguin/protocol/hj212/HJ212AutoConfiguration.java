package com.iteaj.plguin.protocol.hj212;

import com.iteaj.framework.spi.iot.data.DataCollectPublishService;
import com.iteaj.framework.spi.iot.data.IModelAttrRealtimeDataService;
import com.iteaj.framework.spi.iot.data.ModelAttrMetaService;
import com.iteaj.plguin.protocol.hj212.driver.HJ212ProtocolSupplier;
import com.iteaj.plguin.protocol.hj212.network.HJ212RealtimeProtocolHandle;
import org.springframework.context.annotation.Bean;

public class HJ212AutoConfiguration {

    @Bean
    public HJ212ProtocolSupplier hj212ProtocolSupplier() {
        return new HJ212ProtocolSupplier();
    }

    @Bean
    public HJ212RealtimeProtocolHandle hj212RealtimeProtocolHandle(ModelAttrMetaService modelAttrMetaService
            , DataCollectPublishService dataCollectPublishService, IModelAttrRealtimeDataService modelAttrRealtimeDataService) {
        return new HJ212RealtimeProtocolHandle(modelAttrMetaService, dataCollectPublishService, modelAttrRealtimeDataService);
    }
}
