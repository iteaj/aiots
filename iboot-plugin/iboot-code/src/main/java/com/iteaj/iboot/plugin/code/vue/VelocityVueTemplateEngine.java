package com.iteaj.iboot.plugin.code.vue;

import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.iteaj.iboot.plugin.code.LowCodeProperties;

/**
 * 前端vue模板
 */
public class VelocityVueTemplateEngine extends VelocityTemplateEngine {

    protected String vueOutputDir;
    private LowCodeProperties properties;

    public VelocityVueTemplateEngine(LowCodeProperties properties) {
        this(null, properties);
    }

    public VelocityVueTemplateEngine(String vueOutputDir, LowCodeProperties properties) {
        this.vueOutputDir = vueOutputDir;
        this.properties = properties;
    }

    @Override
    public VelocityTemplateEngine init(ConfigBuilder configBuilder) {
        configBuilder.setInjectionConfig(new VueInjectionConfig(vueOutputDir, configBuilder, properties));
        return super.init(configBuilder);
    }

}
