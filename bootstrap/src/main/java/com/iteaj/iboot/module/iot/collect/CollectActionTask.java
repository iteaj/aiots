package com.iteaj.iboot.module.iot.collect;

import com.iteaj.iboot.module.iot.collect.action.CollectAction;
import com.iteaj.iboot.module.iot.collect.store.StoreAction;
import com.iteaj.iboot.module.iot.collect.store.StoreActionFactory;
import com.iteaj.iboot.module.iot.dto.CollectTaskDto;
import com.iteaj.iboot.module.iot.entity.CollectData;
import com.iteaj.iot.utils.UniqueIdGen;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            long cid = UniqueIdGen.nextLong();
            taskDto.getDetails().forEach(item -> {
                List<CollectData> values = new ArrayList<>();
                CollectAction action = CollectActionFactory.getInstance().get(item.getCollectAction());
                if(action != null) {
                    item.getSignals().forEach(signal -> {
                        CollectData data = new CollectData()
                                .setCid(cid).setReason("")
                                .setStatus(false).setCreateTime(date)
                                .setUid(item.getUid()).setSignalId(signal.getId())
                                .setCollectTaskId(taskDto.getId()).setChildSn(item.getChildSn());

                        try {
                            data.setAddress(signal.getAddress());
                            data.setFieldType(signal.getFieldType());

                            action.exec(taskDto, item, signal, value -> {
                                // 设置采集的值和采集时间
                                data.setValue(value).setCollectTime(new Date()).setStatus(true);
                            });
                        } catch (CollectException e) {
                            data.setStatus(false).setCollectTime(new Date()).setReason(e.getMessage());
                            logger.error("采集任务失败 接口调用异常 - 任务: {} - 周期: {} - 设备: {}({}) - 点位: {}({})"
                                    , e.getMessage(), taskDto.getName(), taskDto.getCron()
                                    , item.getDevice().getName(), item.getDevice().getUid()
                                    , signal.getName(), signal.getAddress(), e);
                        } catch (Exception e) {
                            data.setStatus(false).setCollectTime(new Date()).setReason("未知错误");
                            logger.error("采集任务失败 接口调用异常 - 任务: {} - 周期: {} - 设备: {}({}) - 点位: {}({})"
                                    , e.getMessage(), taskDto.getName(), taskDto.getCron()
                                    , item.getDevice().getName(), item.getDevice().getUid()
                                    , signal.getName(), signal.getAddress(), e);
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
        } else {
            logger.error("采集任务失败 任务不存在");
        }

    }
}
