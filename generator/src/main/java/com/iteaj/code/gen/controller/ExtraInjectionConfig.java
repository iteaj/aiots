package com.iteaj.code.gen.controller;

import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.IFileCreate;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.iteaj.code.gen.FileOverrideType;
import com.iteaj.code.utils.LowCodeUtil;

import java.io.File;
import java.util.HashMap;

public class ExtraInjectionConfig extends InjectionConfig {

    private FileOverrideType fileOverride;

    public ExtraInjectionConfig(FileOverrideType fileOverride) {
        this.fileOverride = fileOverride;
    }

    @Override
    public void initMap() {
        this.setMap(new HashMap<>());
    }

    @Override
    public void initTableMap(TableInfo tableInfo) {
        this.initMap();
        String serviceName = tableInfo.getServiceName();

        this.getMap().put("moduleName", LowCodeUtil.getModuleName(tableInfo.getComment()));
        this.getMap().put("serviceName", LowCodeUtil.firstLowerServiceName(serviceName));
    }

    @Override
    public IFileCreate getFileCreate() {
        return (configBuilder, fileType, filePath) -> {
            File file = new File(filePath);
            boolean exist = file.exists();
            if (!exist) { // 文件不存在则创建
                return file.getParentFile().mkdirs();
            }

            // 不覆盖任何文件
            if(fileOverride == FileOverrideType.Non) {
                return false;

                // 全部覆盖
            } else if(fileOverride == FileOverrideType.Full) {
                return true;

                // 部分覆盖只覆盖实体类和xml文件
            } else if(fileOverride == FileOverrideType.Part) {

            }
            return false;
        };
    }
}
