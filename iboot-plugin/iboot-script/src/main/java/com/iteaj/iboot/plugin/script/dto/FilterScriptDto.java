package com.iteaj.iboot.plugin.script.dto;

import com.iteaj.framework.spi.iot.listener.filter.ScriptConfig;
import com.iteaj.iboot.plugin.script.entity.Script;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class FilterScriptDto extends Script {

    /**
     * 脚本配置
     */
    private List<ScriptConfig> configs;
}
