package com.iteaj.iboot.module.iot.collect;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.spi.iot.DeviceProtocolSupplier;
import com.iteaj.framework.spi.iot.ProtocolModelApiInvokeParam;
import com.iteaj.framework.spi.iot.ProtocolSupplierManager;
import com.iteaj.framework.spi.iot.consts.PointProtocolConfig;
import com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi;
import com.iteaj.framework.spi.iot.protocol.ProtocolModel;
import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfig;
import com.iteaj.iboot.module.iot.collect.store.StoreAction;
import com.iteaj.iboot.module.iot.collect.store.StoreActionFactory;
import com.iteaj.iboot.module.iot.dto.CollectTaskDto;
import com.iteaj.iboot.module.iot.entity.CollectData;
import com.iteaj.iboot.module.iot.entity.CollectDetail;
import com.iteaj.iot.consts.ExecStatus;
import com.iteaj.iot.utils.UniqueIdGen;
import com.iteaj.plugin.protocol.plc.omron.OmronProtocolSupplier;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class CollectActionTask implements Runnable {

    private CollectTaskDto taskDto;
    private Logger logger = LoggerFactory.getLogger("IOT:DATA:COLLECT");

    public CollectActionTask(CollectTaskDto taskDto) {
        this.taskDto = taskDto;
    }

    @Override
    public void run() {
        if(taskDto != null && !CollectionUtils.isEmpty(taskDto.getDetails())) {
            Date date = new Date(); // 任务开始时间
            taskDto.getDetails().forEach(item -> {
                long cid = UniqueIdGen.nextLong();
                List<CollectDevice> devices = item.getDevices();
                if(CollectionUtil.isNotEmpty(devices)) {
                    devices.forEach(collectDevice -> {
                        List<CollectData> values = new ArrayList<>();
                        DeviceProtocolSupplier supplier = ProtocolSupplierManager.get(collectDevice.getProtocolCode());
                        if(supplier != null) {
                            ProtocolModel protocol = supplier.getProtocol();
                            collectDevice.getSignals().forEach(signal -> {
                                CollectData data = new CollectData()
                                        .setCid(cid).setReason("")
                                        .setStatus(false).setCreateTime(date)
                                        .setUid(collectDevice.getUid())
                                        .setSignalId(signal.getId())
                                        .setCollectTaskId(taskDto.getId());

                                try {
                                    data.setAddress(signal.getAddress());
//                            data.setFieldType(signal.getFieldType());
                                    AbstractProtocolModelApi modelApi = protocol.getApi(signal.getDirect());
                                    JSONObject param = new JSONObject();
                                    param.put(PointProtocolConfig.POINT_NUMBER, signal.getNum());
                                    param.put(PointProtocolConfig.POINT_ADDRESS, signal.getAddress());
                                    param.put(PointProtocolConfig.POINT_CHILD_SN, collectDevice.getDeviceSn());

                                    modelApi.invoke(new ProtocolModelApiInvokeParam(collectDevice.getParentDeviceSn(), param), invokeResult -> {
                                        data.setCollectTime(new Date());
                                        if(invokeResult.getStatus() == ExecStatus.success) {
                                            data.setValue(invokeResult.getValue().toString()).setStatus(true);
                                        } else {
                                            data.setStatus(false).setReason(invokeResult.getReason());
                                        }
                                    });
                                } catch (CollectException e) {
                                    String message = e.getMessage() == null ? e.getCause().getMessage() : e.getMessage();
                                    data.setStatus(false).setCollectTime(new Date()).setReason(message);
                                    logger.error("采集任务失败 {} - 任务: {} - 周期: {} - 设备: {} - 点位: {}({})"
                                            , message, taskDto.getName(), taskDto.getCron()
                                            , collectDevice.getDeviceSn(), signal.getName(), signal.getAddress(), e);
                                } catch (Exception e) {
                                    data.setStatus(false).setCollectTime(new Date()).setReason("未知错误");
                                    logger.error("采集任务失败 {} - 任务: {} - 周期: {} - 设备: {} - 点位: {}({})"
                                            , e.getMessage(), taskDto.getName(), taskDto.getCron()
                                            , collectDevice.getDeviceSn(), signal.getName(), signal.getAddress(), e);
                                }

                                values.add(data);
                            });
                        }

                        // 存储
                        if(!CollectionUtils.isEmpty(values)) {
                            StoreAction storeAction = StoreActionFactory.getInstance().get(item.getStoreAction());
                            if(storeAction != null) {
                                storeAction.store(item, values);
                            }
                        }
                    });
                }
            });
        } else {
            logger.error("采集任务失败 任务不存在");
        }

    }

    private void collectOmronData(CollectDetail item, CollectDevice collectDevice, OmronProtocolSupplier supplier) {
        List<CollectSignal> signals = collectDevice.getSignals();
//        new OmronTcpProtocol().batchRead()
        supplier.getProtocol().getApi(collectDevice.getProtocolCode());
    }
}
