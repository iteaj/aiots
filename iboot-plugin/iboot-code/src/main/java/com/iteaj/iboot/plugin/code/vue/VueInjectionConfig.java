package com.iteaj.iboot.plugin.code.vue;

import com.baomidou.mybatisplus.generator.config.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.iteaj.iboot.plugin.code.LowCodeProperties;
import com.iteaj.iboot.plugin.code.utils.LowCodeUtil;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class VueInjectionConfig extends InjectionConfig {

    protected String vueOutputDir;
    protected ConfigBuilder builder;
    protected LowCodeProperties properties;

    public VueInjectionConfig(LowCodeProperties properties, ConfigBuilder builder) {
        this(null, builder, properties);
    }

    public VueInjectionConfig(String vueOutputDir, ConfigBuilder builder, LowCodeProperties properties) {
        this.vueOutputDir = vueOutputDir;
        this.builder = builder;
        this.properties = properties;
        getCustomFiles().add(buildCustomFile());
    }

    @Override
    public void beforeOutputFile(TableInfo tableInfo, Map<String, Object> objectMap) {
        super.beforeOutputFile(tableInfo, objectMap);
        List<VueFieldInfo> vueFields = tableInfo.getFields().stream()
                .map(item -> {
                    if(item.isKeyIdentityFlag()) {
                        objectMap.put("rowKey", item.getPropertyName()); return null;
                    }
                    return new VueFieldInfo(item).build(properties);
                }).filter(Objects::nonNull).collect(Collectors.toList());

        objectMap.put("vueFields", vueFields);
        objectMap.put("moduleName", LowCodeUtil.getModuleName(tableInfo.getComment()));
        objectMap.put("serviceName", LowCodeUtil.firstLowerServiceName(tableInfo.getServiceName()));
    }

    protected CustomFile buildCustomFile() {
        vueOutputDir = vueOutputDir == null ? builder.getPackageConfig().getModuleName()
                : vueOutputDir + File.separator + builder.getPackageConfig().getModuleName();
        return new CustomFile.Builder()
                .fileName("index.vue")
                .templatePath("/templates/ivzone.vue.vm")
                .filePath(vueOutputDir)
                .formatNameFunction(info -> LowCodeUtil.firstLowerServiceName(info.getEntityName()) + File.separator)
                .build();
    }

}
