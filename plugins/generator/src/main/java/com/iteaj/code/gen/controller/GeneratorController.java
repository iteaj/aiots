package com.iteaj.code.gen.controller;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.TemplateConfig;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.iteaj.code.LowCodeProperties;
import com.iteaj.code.gen.VelocityReviewTemplateEngine;
import com.iteaj.code.gen.dto.GenConfigDto;
import com.iteaj.code.gen.dto.GenDirDto;
import com.iteaj.code.gen.dto.ReviewDto;
import com.iteaj.code.gen.vue.VelocityVueTemplateZipEngine;
import com.iteaj.code.utils.LowCodeUtil;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.autoconfigure.FrameworkProperties;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.spi.admin.MenuResource;
import com.iteaj.framework.spi.admin.MenuType;
import com.iteaj.framework.spi.admin.ResourceManager;
import com.iteaj.framework.spi.admin.UrlResource;
import com.iteaj.util.CommonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 代码生成器功能
 * @author iteaj
 */
@RestController
@RequestMapping("/lcd/gen")
public class GeneratorController extends BaseController implements InitializingBean {

    private String database;
    private final LowCodeProperties properties;
    private final ResourceManager resourceManager;
    private final FrameworkProperties frameworkProperties;
    private Pattern pattern = Pattern.compile("jdbc:.*((//)|@)(?<host>.+):(?<port>\\d+)/(?<db>.*)\\?.*");

    public GeneratorController(LowCodeProperties properties
            , ResourceManager resourceManager
            , FrameworkProperties frameworkProperties) {
        this.properties = properties;
        this.resourceManager = resourceManager;
        this.frameworkProperties = frameworkProperties;
    }

    /**
     * 获取数据库表列表
     * @return
     */
    @GetMapping("list")
    public Result<List<TableInfo>> list(final String name, final String prefix) {
        initTableQuerySql(name, prefix, null);
        List<TableInfo> tableInfoList = new ConfigBuilder(properties.getLpc(), properties.getLdc()
                , properties.getLsc(), new TemplateConfig(), properties.getLgc()).getTableInfoList();
        if(CommonUtils.isNotEmpty(tableInfoList)) {
            tableInfoList.forEach(item -> {
                item.setFields(null); // 不写字段信息到前端
            });
        }
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
     * 查询父菜单
     * @return
     */
    @GetMapping("parent")
    public Result<Collection<MenuResource>> parentMenus() {
        return success(this.resourceManager.listResourcesByType(MenuType.M));
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

        initTableQuerySql(null, null, dto.getTables());
        GlobalConfig globalConfig = new GlobalConfig();
        BeanUtils.copyProperties(properties.getLgc(), globalConfig);

        // 设置输出目录
        globalConfig.setOutputDir(dto.getOutDir());
        // 文件是否覆盖
        globalConfig.setFileOverride(dto.isFileOverride());
        ConfigBuilder configBuilder = new ConfigBuilder(properties.getLpc(), properties.getLdc()
                , properties.getLsc(), new TemplateConfig(), globalConfig);

        configBuilder.setInjectionConfig(new ExtraInjectionConfig().setConfig(configBuilder));

        // 生成文件
        new AutoGenerator().setConfig(configBuilder).execute();

        // 生成菜单
        genMenu(dto, configBuilder);

        return success(true);
    }

    /**
     *  生成菜单
     * @param dto
     * @param configBuilder
     */
    private void genMenu(GenConfigDto dto, ConfigBuilder configBuilder) {
        if(dto.getMenuId() != null && CommonUtils.isNotEmpty(configBuilder.getTableInfoList())) {
            StringBuilder url = new StringBuilder();
            Connection conn = properties.getLdc().getConn();
            try(Statement statement = conn.createStatement()) {
                configBuilder.getTableInfoList().forEach(tableInfo -> {
                    url.append("/").append(properties.getLpc().getModuleName())
                            .append("/").append(tableInfo.getEntityPath());

                    // 菜单url已经存在
                    UrlResource byUrl = resourceManager.getByUrl(url.toString());
                    if(byUrl == null) {
                        // 写菜单到数据库
                        writeMenuToDb(tableInfo, dto, properties, statement);
                    }

                    url.setLength(0);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void writeMenuToDb(TableInfo tableInfo, GenConfigDto dto, LowCodeProperties properties, Statement statement) {
        try {
            final String comment = LowCodeUtil.getModuleName(tableInfo.getComment());
            String url = "/"+properties.getLpc().getModuleName()+"/" + tableInfo.getEntityPath();
            String perm = properties.getLpc().getModuleName()+":"+tableInfo.getEntityPath();
            String insertSql= String.format("insert into t_menu (pid, name, url, perms, type, msn, sort) value (%s, '%s', '%s', '%s', 'V', 'core', 158);"
                    , dto.getMenuId(), comment, url, perm+":"+"view");
            statement.executeUpdate(insertSql);

            // 查找刚刚插入的菜单id
            final ResultSet resultSet = statement.executeQuery("select last_insert_id()");
            resultSet.next();
            final long pid = resultSet.getLong(1);

            StringBuilder batchSql = new StringBuilder("insert into t_menu (pid, name, url, perms, type, msn, log_desc, sort) values ");
            // 查询列表
            batchSql.append(String.format("(%s, '%s', '%s', '%s', 'A', 'core', '查询%s列表', 5),", pid, "查询", url+"/view", perm+":view", comment));
            // 新增
            batchSql.append(String.format("(%s, '%s', '%s', '%s', 'A', 'core', '新增%s记录', 10),", pid, "新增", url+"/add", perm+":add", comment));
            // 修改
            batchSql.append(String.format("(%s, '%s', '%s', '%s', 'A', 'core', '修改%s记录', 15),", pid, "修改", url+"/edit", perm+":edit", comment));
            // 删除
            batchSql.append(String.format("(%s, '%s', '%s', '%s', 'A', 'core', '删除%s记录', 20);", pid, "删除", url+"/del", perm+":del", comment));
            statement.executeUpdate(batchSql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成前端代码
     * @param dto
     */
    @GetMapping("vue")
    public void download(GenConfigDto dto, HttpServletResponse response) throws IOException {
        initTableQuerySql(null, null, dto.getTables());
        ConfigBuilder configBuilder = new ConfigBuilder(properties.getLpc(), properties.getLdc()
                , properties.getLsc(), new TemplateConfig(), properties.getLgc());

        // 生成文件
        new AutoGenerator().setTemplateEngine(new VelocityVueTemplateZipEngine(response.getOutputStream()
                , properties)).setConfig(configBuilder).execute();

        response.setHeader("Content-Disposition", "attachment; filename=\"ivzone.zip\"");
        response.setContentType("application/octet-stream; charset=UTF-8");
        response.getOutputStream().flush();
    }

    /**
     * 预览生成的源代码
     * @param tableName
     * @return
     */
    @GetMapping("review")
    public Result<ReviewDto> review(String tableName) {
        if(CommonUtils.isBlank(tableName)) {
            return fail("请指定要预览的表");
        }

        initTableQuerySql(null, null, Arrays.asList(tableName));
        ConfigBuilder configBuilder = new ConfigBuilder(properties.getLpc(), properties.getLdc()
                , properties.getLsc(), new TemplateConfig(), properties.getLgc());

        // 生成文件
        VelocityReviewTemplateEngine engine = new VelocityReviewTemplateEngine(properties);
        new AutoGenerator().setConfig(configBuilder).setTemplateEngine(engine).execute();

        return success(engine.getReviewDto());
    }

    private void initTableQuerySql(String name, String prefix, List<String> tables) {
        properties.getLdc().setDbQuery(new MySqlQuery() {
            @Override
            public String tablesSql() {
                StringBuilder sql = new StringBuilder("select * from information_schema.tables where TABLE_SCHEMA='").append(database).append("'");

                if(CommonUtils.isNotBlank(name) && CommonUtils.isNotBlank(prefix)) {
                    sql.append("and TABLE_NAME LIKE '%").append(name).append("%' and TABLE_NAME LIKE ").append(prefix).append("%");
                } else if(CommonUtils.isNotBlank(name)) {
                    sql.append("and TABLE_NAME LIKE '%").append(name).append("%'");
                } else if(CommonUtils.isNotBlank(prefix)) {
                    sql.append("and TABLE_NAME LIKE '").append(prefix).append("%'");
                } else if(!CollectionUtils.isEmpty(tables)) {
                    String collect = tables.stream().map(item -> "'" + item + "'").collect(Collectors.joining(","));
                    sql.append("and TABLE_NAME in (").append(collect).append(")");
                }

                return sql.append(" order by UPDATE_TIME desc").toString();
            }

            @Override
            public String[] fieldCustom() {
                return new String[] {"key", "null", "default"};
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
        final String moduleName = this.properties.getLpc().getModuleName();
        if(CommonUtils.isBlank(moduleName)) {
            throw new BeanInitializationException("没有指定代码生成器要生成的模块[low.code.lpc.module-name]");
        }

        // 数据源配置
        LowCodeProperties.LcdDataSourceConfig ldc = properties.getLdc();
        Matcher matcher = pattern.matcher(ldc.getUrl());
        if(matcher.find()) {
            this.database = matcher.group("db");
        }

        if(CommonUtils.isBlank(this.database)) {
            throw new BeanInitializationException("没有指定代码生成器所要生成的数据库");
        }
    }
}
