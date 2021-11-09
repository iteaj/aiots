package com.iteaj.msn.lcd.design;

import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.iteaj.msn.lcd.LowCodeConst;
import com.iteaj.msn.lcd.gen.DesignConfigBuilder;
import com.iteaj.msn.lcd.utils.LowCodeUtil;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LcdDtoInjectionConfig extends LcdInjectionConfig {

    public LcdDtoInjectionConfig(DesignConfigBuilder configBuilder) {
        this.setConfig(configBuilder);
    }

    @Override
    public void initMap() {
        this.setMap(new HashMap<>());
    }

    @Override
    public void initTableMap(TableInfo tableInfo) {
        this.setMap(new HashMap<>()); // 清空上一张表信息的所有配置
        getMap().put("moduleName", LowCodeUtil.getModuleName(tableInfo.getComment()));

        String serviceName = tableInfo.getServiceName();
        this.getMap().put("serviceName", LowCodeUtil.firstLowerServiceName(serviceName));
    }

    @Override
    public List<FileOutConfig> getFileOutConfigList() {
        return Arrays.asList(new DtoFileOutConfig(getConfig()));
    }

    @Override
    public ILcdFileCreate getFileCreate() {
        return (configBuilder, fileType, filePath) -> true;
    }
}
