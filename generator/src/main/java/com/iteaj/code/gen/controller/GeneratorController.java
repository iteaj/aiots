package com.iteaj.code.gen.controller;

import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.baomidou.mybatisplus.core.parser.SqlInfo;
import com.baomidou.mybatisplus.core.parser.SqlParserHelper;
import com.baomidou.mybatisplus.extension.toolkit.SqlParserUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.iteaj.code.LowCodeProperties;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.result.Result;
import com.iteaj.util.CommonUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
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
    private final DataSourceProperties dataSourceProperties;
    private AutoGenerator generator = new AutoGenerator();

    public GeneratorController(DataSourceProperties dataSourceProperties, LowCodeProperties properties) {
        this.dataSourceProperties = dataSourceProperties;
        this.properties = properties;
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
     * 生成代码
     */
    @PostMapping("gen")
    public void generator() {

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
