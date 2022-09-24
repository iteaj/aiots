package com.iteaj.iboot.msn.iot.collect;

import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.entity.CollectData;
import com.iteaj.iboot.msn.iot.service.ICollectDataService;
import com.iteaj.iot.utils.UniqueIdGen;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class CollectTask implements Runnable {

    private CollectTaskDto taskDto;
    private Logger logger = LoggerFactory.getLogger("IOT:DATA:COLLECT");

    public CollectTask(CollectTaskDto taskDto) {
        this.taskDto = taskDto;
    }

    @Override
    public void run() {
        if(taskDto != null && !CollectionUtils.isEmpty(taskDto.getDetails())) {
            long cid = UniqueIdGen.nextLong();
            List<CollectData> values = new ArrayList<>();
            taskDto.getDetails().forEach(item -> {
                CollectData data = new CollectData();
                CollectAction action = CollectActionFactory.getInstance().get(item.toString());
                if(action != null) {
                    item.getSignals().forEach(signal -> {

                        try {
                            action.exec(taskDto, item.getDevice(), signal, l -> {});
                        } catch (Exception e) {
                            logger.error("采集任务失败 接口调用异常 - 任务: {} - 周期: {} - 设备: {}({}) - 点位: {}({})"
                                    , e.getMessage(), taskDto.getName(), taskDto.getCron()
                                    , item.getDevice().getName(), item.getDevice().getUid()
                                    , signal.getName(), signal.getAddress(), e);
                        }
                    });
                } else {

                }

                values.add(data);
            });


        } else {
            logger.error("采集任务失败 任务不存在");
        }

    }
}
