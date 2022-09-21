package com.iteaj.iboot.msn.iot.collect;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.entity.CollectEntity;
import com.iteaj.iboot.msn.iot.entity.CollectTask;
import com.iteaj.iboot.msn.iot.entity.Device;
import com.iteaj.iboot.msn.iot.service.ICollectTaskService;
import com.iteaj.iot.plc.siemens.SiemensConnectProperties;
import com.iteaj.iot.plc.siemens.SiemensModel;
import com.iteaj.iot.plc.siemens.SiemensS7Protocol;
import com.iteaj.iot.utils.UniqueIdGen;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class CollectService implements InitializingBean {

    private final ThreadPoolTaskScheduler scheduler;
    private final ICollectTaskService collectTaskService;
    private Map<Long, ScheduledFuture> scheduledFutureMap = new ConcurrentHashMap<>();

    public CollectService(ThreadPoolTaskScheduler scheduler
            , ICollectTaskService collectTaskService) {
        this.scheduler = scheduler;
        this.collectTaskService = collectTaskService;
    }

    public void statusSwitch(Long id, String status) {
        CollectTaskDto taskDto = collectTaskService.signalsDetailById(id);

        if(taskDto == null) {
            throw new ServiceException("采集任务不存在");
        }

        if(taskDto.getStatus().equals(status)) {
            throw new ServiceException("状态切换异常");
        }

        if(status.equals("run")) {
            ScheduledFuture future = scheduledFutureMap.get(id);
            if(future == null) {
                createScheduledTask(taskDto);
            } else if(future.isCancelled()) {
                scheduledFutureMap.remove(id);
                createScheduledTask(taskDto);
            }
        } else if(status.equals("stop")){
            ScheduledFuture future = scheduledFutureMap.remove(id);
            if(future != null && !future.isCancelled()) {
                future.cancel(true);
            }
        } else {
            throw new IllegalArgumentException("不支持的状态["+status+"]");
        }

        collectTaskService.update(Wrappers.<CollectTask>lambdaUpdate()
                .set(CollectTask::getStatus, status)
                .eq(CollectTask::getId, id));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        collectTaskService.list(Wrappers.<CollectTask>lambdaQuery().eq(CollectTask::getStatus, "run"))
                .stream().filter(task -> StringUtils.hasText(task.getCron()))
                .map(item -> collectTaskService.signalsDetailById(item.getId()))
                .filter(item -> item != null && !CollectionUtils.isEmpty(item.getSignals()))
                .filter(item -> item.getDevice() != null && item.getDevice().getIp() != null && item.getDevice().getPort() != null)
                .forEach(item -> createScheduledTask(item));

    }

    private void createScheduledTask(CollectTaskDto taskDto) {
        if(taskDto == null) {
            throw new IllegalStateException("没有获取到对应的任务");
        }

        Device device = taskDto.getDevice();
        if(CollectionUtils.isEmpty(taskDto.getSignals()) || device == null) {
            throw new IllegalStateException("此采集任务数据不完整");
        }

        if(!StringUtils.hasText(device.getIp()) || device.getPort() == null) {
            throw new IllegalStateException("设备["+device.getName()+"]有没有指定ip和端口");
        }

        SiemensConnectProperties properties = new SiemensConnectProperties(device.getIp(), device.getPort(), SiemensModel.S1200);
        ScheduledFuture<?> schedule = scheduler.schedule(() -> {
            long cid = UniqueIdGen.nextLong();
            taskDto.getSignals().forEach(signal -> {
                SiemensS7Protocol protocol = new SiemensS7Protocol(properties);
                String type = signal.getFieldType();
                Object value = null;
                switch (type) {
//                    case "1":
//                        value = protocol.read()
                    case "2":
                        value = protocol.readInt16(signal.getAddress()); break;
                    case "3":
                        value = protocol.readInt32(signal.getAddress()); break;
                    case "4":
                        value = protocol.readInt64(signal.getAddress()); break;
                    case "5":
                        value = protocol.readFloat(signal.getAddress()); break;
                    case "6":
                        value = protocol.readDouble(signal.getAddress()); break;
                    case "7":
                        value = protocol.readBool(signal.getAddress()); break;
                }

                // 保存记录到mongo
                CollectEntity collectEntity = new CollectEntity().setCid(cid).setCollectTime(new Date())
                        .setUid(device.getUid()).setDeviceSn(device.getDeviceSn()).setValue(value)
                        .setSignalId(signal.getId()).setTaskId(taskDto.getId()).setAddress(signal.getAddress());

                System.out.println(collectEntity);
            });

        }, new CronTrigger(taskDto.getCron()));

        scheduledFutureMap.put(taskDto.getId(), schedule);
    }
}
