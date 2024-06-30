package com.iteaj.framework.spi.iot.data;

import java.util.Date;

public interface RealtimeCollectData {

    /**
     * 设备uid
     * @return
     */
    String getUid();

    /**
     * 采集cid(可空)
     * @return
     */
    String getCid();

    /**
     * 采集字段id
     * @return
     */
    Long getFieldId();

    /**
     * 采集字段
     * @return
     */
    String getField();

    /**
     * 所属任务(可空)
     * @return
     */
    Long getTaskId();

    /**
     * 采集的值
     * @return
     */
    String getValue();

    /**
     * 采集状态
     * @return
     */
    Boolean getStatus();

    /**
     * 失败原因
     * @return
     */
    String getReason();

    /**
     * 点位地址(可空)
     * @return
     */
    String getAddress();

    /**
     * 采集方式(model、signal)
     * @return
     */
    String getMode();

    /**
     * 采集时间
     * @return
     */
    Date getCollectTime();
}
