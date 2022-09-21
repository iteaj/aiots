package com.iteaj.iboot.msn.iot.collect;

import com.iteaj.iboot.msn.iot.dto.CollectTaskDto;
import com.iteaj.iboot.msn.iot.entity.CollectEntity;
import com.iteaj.iot.plc.siemens.SiemensS7Protocol;
import com.iteaj.iot.utils.UniqueIdGen;
import lombok.Data;

import java.util.Date;

@Data
public class CollectTask implements Runnable {

    private CollectTaskDto taskDto;

    public CollectTask(CollectTaskDto taskDto) {
        this.taskDto = taskDto;
    }

    @Override
    public void run() {
        long cid = UniqueIdGen.nextLong();
        taskDto.getSignals().forEach(signal -> {
//            SiemensS7Protocol protocol = new SiemensS7Protocol(properties);
//            String type = signal.getFieldType();
//            Object value = null;
//            switch (type) {
////                    case "1":
////                        value = protocol.read()
//                case "2":
//                    value = protocol.readInt16(signal.getAddress()); break;
//                case "3":
//                    value = protocol.readInt32(signal.getAddress()); break;
//                case "4":
//                    value = protocol.readInt64(signal.getAddress()); break;
//                case "5":
//                    value = protocol.readFloat(signal.getAddress()); break;
//                case "6":
//                    value = protocol.readDouble(signal.getAddress()); break;
//                case "7":
//                    value = protocol.readBool(signal.getAddress()); break;
//            }

            // 保存记录到mongo
//            CollectEntity collectEntity = new CollectEntity().setCid(cid).setCollectTime(new Date())
//                    .setUid(device.getUid()).setDeviceSn(device.getDeviceSn()).setValue(value)
//                    .setSignalId(signal.getId()).setTaskId(taskDto.getId()).setAddress(signal.getAddress());
//
//            System.out.println(collectEntity);
        });
    }
}
