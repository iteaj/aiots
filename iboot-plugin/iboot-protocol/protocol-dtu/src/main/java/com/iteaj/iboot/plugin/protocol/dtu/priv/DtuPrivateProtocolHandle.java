package com.iteaj.iboot.plugin.protocol.dtu.priv;

import cn.hutool.core.collection.CollectionUtil;
import com.iteaj.framework.spi.iot.*;
import com.iteaj.framework.spi.iot.consts.DataType;
import com.iteaj.framework.spi.iot.consts.ProtocolCodes;
import com.iteaj.framework.spi.iot.data.*;
import com.iteaj.iot.server.ServerMessage;
import com.iteaj.iot.server.ServerProtocolHandle;
import com.iteaj.iot.utils.ByteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class DtuPrivateProtocolHandle implements ServerProtocolHandle<DtuInitiativeProtocol> {

    @Autowired
    private ModelAttrMetaService getModelAttrMetaService;
    @Autowired
    private DataCollectPublishService dataCollectPublishService;
    @Autowired
    private IModelAttrRealtimeDataService modelAttrRealtimeDataService;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public Object handle(DtuInitiativeProtocol protocol) {
        ServerMessage serverMessage = protocol.requestMessage();
        if(logger.isDebugEnabled()) {
            logger.info("DTU主动上报数据 >>> 设备编号: {} - 数据: {}", protocol.getEquipCode(), ByteUtil.bytesToHexByFormat(serverMessage.getMessage()));
        }

        Date collectTime = new Date(); // fixed: 修复采集时间为null的问题
        DeviceKey deviceKey = DeviceKey.build("1", protocol.getEquipCode());
        RealtimeDeviceStatus deviceStatus = modelAttrRealtimeDataService.getDeviceStatus(ProtocolCodes.DTU_PRIVATE.getValue(), "1", protocol.getEquipCode());
        if(deviceStatus != null) {
            Map<Serializable, ModelAttrMeta> attrMetas = getModelAttrMetaService.getAttrMetas(deviceStatus.getProductCode());
            if(CollectionUtil.isNotEmpty(attrMetas)) {
                DataSupplier dataSupplier = new DataSupplier(ProtocolCodes.DTU_PRIVATE.getValue(), deviceKey);
                attrMetas.forEach((key, value) -> {
                    UpModelAttr upModelAttr = new UpModelAttr(value.getId(), value.getField(), value.getName(), DataType.build(value.getDataType())
                            , value.getAccuracy(), value.getGain(), value.getResolver(), value.getScript(), null);

                    Object resolver = DataValueResolverFactory.resolver(upModelAttr, serverMessage.getMessage());
                    if(resolver != null && !(resolver instanceof byte[])) {
                        dataSupplier.addValue(value.getId(), null, value.getField(), resolver).build(collectTime);
                    }
                });

                dataCollectPublishService.publish(dataSupplier, deviceStatus);
            }
        } else {
            logger.warn("DTU私有驱动 >>> 查找不到设备: {}", deviceKey);
        }

        return null;
    }
}
