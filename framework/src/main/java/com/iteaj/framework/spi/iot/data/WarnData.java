package com.iteaj.framework.spi.iot.data;

import java.util.Date;

public interface WarnData {

    String getUid();

    String getValue();

    String getDeviceSn();

    String getContent();

    Date getWarnTime();

    Object getGrade();

    Long getWarnConfigId();

    String getProductCode();

}
