package com.iteaj.msn.dev.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

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

        public CodeGen() {

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
    }
}
