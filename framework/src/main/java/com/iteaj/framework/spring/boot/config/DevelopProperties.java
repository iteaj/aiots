package com.iteaj.framework.spring.boot.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
@ConfigurationProperties(prefix = "dev")
public class DevelopProperties {

    private CodeGen gen;

    /**
     * 代码生成器配置
     */
    public static class CodeGen {

        private String rootDir; // 项目根路径, maven根项目
        private String h5Template; // h5模板路径
        private GlobalConfig global;
        private TemplateConfig template;
        private StrategyConfig strategy;
        private DataSourceConfig source;
        private PackageConfig packageConfig;

        public CodeGen() {

        }

        public GlobalConfig getGlobal() {
            return global;
        }

        public CodeGen setGlobal(GlobalConfig global) {
            this.global = global;
            return this;
        }

        public StrategyConfig getStrategy() {
            return strategy;
        }

        public CodeGen setStrategy(StrategyConfig strategy) {
            this.strategy = strategy;
            return this;
        }

        public String getRootDir() {
            return rootDir;
        }

        public CodeGen setRootDir(String rootDir) {
            this.rootDir = rootDir;
            return this;
        }

        public TemplateConfig getTemplate() {
            return template;
        }

        public CodeGen setTemplate(TemplateConfig template) {
            this.template = template;
            return this;
        }

        public String getH5Template() {
            return h5Template;
        }

        public CodeGen setH5Template(String h5Template) {
            this.h5Template = h5Template;
            return this;
        }

        public DataSourceConfig getSource() {
            return source;
        }

        public CodeGen setSource(DataSourceConfig source) {
            this.source = source;
            return this;
        }

        public PackageConfig getPackageConfig() {
            return packageConfig;
        }

        public CodeGen setPackageConfig(PackageConfig packageConfig) {
            this.packageConfig = packageConfig;
            return this;
        }
    }

    public CodeGen getGen() {
        return gen;
    }

    public DevelopProperties setGen(CodeGen gen) {
        this.gen = gen;
        return this;
    }

}
