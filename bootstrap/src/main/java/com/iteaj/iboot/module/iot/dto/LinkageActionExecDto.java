package com.iteaj.iboot.module.iot.dto;

import com.iteaj.iboot.module.iot.entity.LinkageAction;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import com.iteaj.iboot.module.iot.entity.WarnConfig;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LinkageActionExecDto extends LinkageAction {

    /**
     * 模型api
     */
    private ModelApi api;

    /**
     * 告警配置
     */
    private WarnConfig warn;
}
