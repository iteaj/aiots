package com.iteaj.framework.spring.boot.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.config.*;
import com.iteaj.util.CommonUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.Arrays;

/**
 * create time: 2020/4/27
 *
 * @author iteaj
 * @since 1.0
 */
@ConfigurationProperties(prefix = "dev")
public class DevelopProperties implements InitializingBean, EnvironmentAware {

    private CodeGen gen;
    private boolean isDev; // 是否是开发环境

    @Override
    public void setEnvironment(Environment environment) {
        String[] activeProfiles = environment.getActiveProfiles();
        this.isDev = Arrays.asList(activeProfiles).contains("dev");
    }

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

    @Override
    public void afterPropertiesSet() throws Exception {
        handleProjectRootForDev();
    }

    /**
     * 处理开发环境的项目根路径
     */
    private void handleProjectRootForDev() {
        if(this.getGen() == null) return;
        String rootDir = this.getGen().getRootDir();
        if(CommonUtils.isBlank(rootDir)) return;

        if(!this.isDev) {
            String outputDir = this.getGen().getGlobal().getOutputDir();
            this.getGen().setRootDir(outputDir); // 非开发环境, 生成的路径为用户配置的路径
            return;
        }

        String rootPom = "/pom.xml";
        File file = new File(rootDir), pomFile;
        while (true) {
            File parentFile = file.getParentFile();
            if(parentFile == null) return;

            // 如果目录下存在pom文件, 继续查找, 直到找不到pom文件
            // 无pom文件的目录的下一级就是项目根路径
            pomFile = new File(parentFile + rootPom);
            if(pomFile.exists() && pomFile.isFile()) {
                file = parentFile; continue;
            } else {
                this.getGen().setRootDir(file.getPath()); break;
            }
        }
    }
}
