package com.iteaj.iboot.msn.iot.debug;

import lombok.Data;

@Data
public class DebugRequestModel {

    private String type;

    private String clientSn;

    private DebugModel model;
}
