package com.iteaj.code.gen.vue;

import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.FileOutConfig;
import com.baomidou.mybatisplus.generator.config.IFileCreate;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.FileType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VueInjectionConfig extends InjectionConfig {

    public VueInjectionConfig(ConfigBuilder builder) {
        setConfig(builder);
    }

    @Override
    public void initMap() {
        this.setMap(new HashMap<>());
    }

    @Override
    public void initTableMap(TableInfo tableInfo) {
        this.getMap().clear(); // 清空上一张表信息的所有配置
        tableInfo.getFields().stream().forEach(item -> {
            this.getMap().put(item.getName(), new VueFieldInfo(item).build());
        });
    }

    @Override
    public List<FileOutConfig> getFileOutConfigList() {
        return Arrays.asList(new FileOutConfig() {

            @Override
            public String getTemplatePath() {
                return "/templates/ivzone.vue.vm";
            }

            @Override
            public String outputFile(TableInfo tableInfo) {
                String moduleName = getConfig().getPackageInfo().get(ConstVal.MODULE_NAME);
                String entityName = tableInfo.getEntityPath();
                return moduleName + "/" + entityName + "/index.vue";
            }
        });
    }

    @Override
    public IFileCreate getFileCreate() {
        return (configBuilder, fileType, filePath) -> {
            // 只生成前端代码
            if(fileType != FileType.OTHER) {
                return false;
            } else {
                // 只生成.vue结尾的文件
                return filePath.endsWith(".vue");
            }
        };
    }
}
