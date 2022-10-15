package com.iteaj.iboot.msn.iot.collect;

import com.iteaj.iboot.msn.iot.collect.action.CollectAction;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.entity.CollectData;
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
            long cid = UniqueIdGen.nextLong();
            List<CollectData> values = new ArrayList<>();
            taskDto.getDetails().forEach(item -> {
                CollectData data = new CollectData()
                        .setCid(cid)
                        .setUid(item.getUid())
                        .setCollectTime(new Date())
                        .setCollectTaskId(taskDto.getId());
                CollectAction action = CollectActionFactory.getInstance().get(item.toString());
                if(action != null) {
                    item.getSignals().forEach(signal -> {
                        try {
                            data.setAddress(signal.getAddress());
                            data.setFieldType(signal.getFieldType());

                            action.exec(taskDto, item.getDevice(), signal, value -> {
                                // 设置采集的值和采集时间
                                data.setValue(value).setCollectTime(new Date()).setStatus(true);
                            });
                        } catch (CollectException e) {
                            data.setStatus(false).setReason(e.getMessage());
                            logger.error("采集任务失败 接口调用异常 - 任务: {} - 周期: {} - 设备: {}({}) - 点位: {}({})"
                                    , e.getMessage(), taskDto.getName(), taskDto.getCron()
                                    , item.getDevice().getName(), item.getDevice().getUid()
                                    , signal.getName(), signal.getAddress(), e);
                        } catch (Exception e) {
                            data.setStatus(false).setReason("未知错误");
                            logger.error("采集任务失败 接口调用异常 - 任务: {} - 周期: {} - 设备: {}({}) - 点位: {}({})"
                                    , e.getMessage(), taskDto.getName(), taskDto.getCron()
                                    , item.getDevice().getName(), item.getDevice().getUid()
                                    , signal.getName(), signal.getAddress(), e);
                        }
                    });
                } else {
                    data.setStatus(false).setReason("没有设置采集动作");
                }

                values.add(data);
            });
        } else {
            logger.error("采集任务失败 任务不存在");
        }

    }
}
