package com.iteaj.framework.spring.boot.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
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

    public static class CodeGen {

        private String sqlUrl; // url的数据库用%s替代
        private DbType dbType;
        private String rootDir; // 项目根路径, maven根项目
        private String sqlDriver;
        private String h5Template; // h5模板路径
        private GlobalConfig global;
        private String parentPackage;
        private TemplateConfig template;
        private StrategyConfig strategy;

        public CodeGen() {
            this.parentPackage = "com.iteaj";
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

        public String getSqlUrl() {
            return sqlUrl;
        }

        public CodeGen setSqlUrl(String sqlUrl) {
            this.sqlUrl = sqlUrl;
            return this;
        }

        public String getSqlDriver() {
            return sqlDriver;
        }

        public CodeGen setSqlDriver(String sqlDriver) {
            this.sqlDriver = sqlDriver;
            return this;
        }

        public String getParentPackage() {
            return parentPackage;
        }

        public CodeGen setParentPackage(String parentPackage) {
            this.parentPackage = parentPackage;
            return this;
        }

        public DbType getDbType() {
            return dbType;
        }

        public CodeGen setDbType(DbType dbType) {
            this.dbType = dbType;
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
    }

    public CodeGen getGen() {
        return gen;
    }

    public DevelopProperties setGen(CodeGen gen) {
        this.gen = gen;
        return this;
    }

}
