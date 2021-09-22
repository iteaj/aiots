package com.iteaj.code.gen.controller;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.iteaj.code.LowCodeProperties;
import com.iteaj.code.gen.vue.VelocityVueTemplateZipEngine;
import com.iteaj.code.gen.vue.VueInjectionConfig;
import com.iteaj.code.gen.dto.GenConfigDto;
import com.iteaj.code.gen.dto.GenDirDto;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.autoconfigure.FrameworkProperties;
import com.iteaj.framework.result.Result;
import com.iteaj.util.CommonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * lc = low code 低代码
 * 代码生成器接口
 * @author iteaj
 */
@RestController
@RequestMapping("/lcd/gen")
public class GeneratorController extends BaseController implements InitializingBean {

    private final LowCodeProperties properties;
    private final FrameworkProperties frameworkProperties;
    private final DataSourceProperties dataSourceProperties;
    private AutoGenerator generator = new AutoGenerator();

    public GeneratorController(DataSourceProperties dataSourceProperties, LowCodeProperties properties, FrameworkProperties frameworkProperties) {
        this.dataSourceProperties = dataSourceProperties;
        this.properties = properties;
        this.frameworkProperties = frameworkProperties;
    }

    /**
     * 获取数据库表列表
     * @return
     */
    @GetMapping("list")
    public Result<List<TableInfo>> list(final String name, final String prefix) {
        initQuerySql(name, prefix);
        List<TableInfo> tableInfoList = new ConfigBuilder(properties.getLpc(), properties.getLdc()
                , properties.getLsc(), new TemplateConfig(), properties.getLgc()).getTableInfoList();
        return success(tableInfoList);
    }

    /**
     * 获取生成路径
     * @return
     */
    @GetMapping("getOutDir")
    public Result<GenDirDto> getOutDir() {
        return success(new GenDirDto(properties.getLgc().getOutputDir(), frameworkProperties.getProjectRootPath()));
    }

    /**
     * 生成后端代码
     * @param dto
     */
    @PostMapping("java")
    public Result<Boolean> generator(@RequestBody GenConfigDto dto) {
        if(CollectionUtils.isEmpty(dto.getTables())) {
            return fail("请选择要生成的表");
        }

        GlobalConfig globalConfig = new GlobalConfig();
        BeanUtils.copyProperties(properties.getLgc(), globalConfig);
        ConfigBuilder configBuilder = new ConfigBuilder(properties.getLpc(), properties.getLdc()
                , properties.getLsc(), new TemplateConfig(), globalConfig);

        // 设置输出目录
        globalConfig.setOutputDir(dto.getOutDir());
        // 文件存在是否覆盖
        globalConfig.setFileOverride(dto.isFileOverride());

        // 生成文件
        generator.setConfig(configBuilder).execute();

        return success(true);
    }

    /**
     * 生成前端代码
     * @param dto
     */
    @GetMapping("vue")
    public void download(GenConfigDto dto, HttpServletResponse response) throws IOException {
//        if(CollectionUtils.isEmpty(dto.getTables())) {
//            return fail("请选择要生成的表");
//        }

        ConfigBuilder configBuilder = new ConfigBuilder(properties.getLpc(), properties.getLdc()
                , properties.getLsc(), new TemplateConfig(), properties.getLgc());

        // 注入vue模板生成参数
        configBuilder.setInjectionConfig(new VueInjectionConfig(configBuilder));

        // 生成文件
        generator.setTemplateEngine(new VelocityVueTemplateZipEngine(response.getOutputStream()))
                .setConfig(configBuilder).execute();

        response.setHeader("Content-Disposition", "attachment; filename=\"ivzone.zip\"");
        response.setContentType("application/octet-stream; charset=UTF-8");
        response.getOutputStream().flush();
    }

    private void initQuerySql(String name, String prefix) {
        properties.getLdc().setDbQuery(new MySqlQuery() {
            @Override
            public String tablesSql() {
                StringBuilder sql = new StringBuilder("select * from information_schema.tables where TABLE_SCHEMA='")
                        .append(properties.getLdc().getDatabase()).append("'");

                if(CommonUtils.isNotBlank(name) && CommonUtils.isNotBlank(prefix)) {
                    sql.append("and TABLE_NAME LIKE '%").append(name).append("%' and TABLE_NAME LIKE ").append(prefix).append("%");
                } else if(CommonUtils.isNotBlank(name)) {
                    sql.append("and TABLE_NAME LIKE '%").append(name).append("%'");
                } else if(CommonUtils.isNotBlank(prefix)) {
                    sql.append("and TABLE_NAME LIKE '").append(prefix).append("%'");
                }

                return sql.append(" order by UPDATE_TIME desc").toString();
            }

            @Override
            public String tableName() {
                return "TABLE_NAME";
            }

            @Override
            public String tableComment() {
                return "TABLE_COMMENT";
            }

            @Override
            public boolean isKeyIdentity(ResultSet results) throws SQLException {
                return false;
            }
        });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        generator.setTemplateEngine(new VelocityTemplateEngine());
        // 数据源配置
        LowCodeProperties.LcdDataSourceConfig ldc = properties.getLdc();
        if(CommonUtils.isBlank(ldc.getDatabase())) {
            Pattern pattern = Pattern.compile("jdbc:.*((//)|@)(?<host>.+):(?<port>\\d+)/(?<db>.*)\\?.*");
            Matcher matcher = pattern.matcher(ldc.getUrl());
            if(matcher.find()) {
                String db = matcher.group("db");
                ldc.setDatabase(db);
            }
        }
    }
}
