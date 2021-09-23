package com.iteaj.code;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

/**
 * 低代码配置
 */
@Data
@ConfigurationProperties("low.code")
public class LowCodeProperties {

    /**
     * 全局配置
     */
    private LcdGlobalConfig lgc;
    /**
     * 生成包信息配置
     */
    private LcdPackageConfig lpc;
    /**
     * 生成策略配置
     */
    private LcdStrategyConfig lsc;
    /**
     * 数据源配置
     */
    private LcdDataSourceConfig ldc;
    /**
     * 搜索配置
     */
    private LcdFormConfig form;

    public static class LcdGlobalConfig extends GlobalConfig { }
    public static class LcdPackageConfig extends PackageConfig { }
    public static class LcdStrategyConfig extends StrategyConfig { }

    /**
     * 前端表单配置
     */
    @Data
    public static class LcdFormConfig {

        /**
         * 表字段不包含以下内容加入编辑组件
         */
        private Set<String> edit;

        /**
         * 表字段包含以下内容加入搜索组件
         */
        private Set<String> search;
    }

    @Data
    public static class LcdDataSourceConfig extends DataSourceConfig {
        /**
         * 数据库(默认从url解析)
         */
        private String database;
    }
}
