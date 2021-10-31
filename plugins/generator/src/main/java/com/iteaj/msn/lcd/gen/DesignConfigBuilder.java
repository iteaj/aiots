package com.iteaj.msn.lcd.gen;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.spring.SpringUtils;
import com.iteaj.msn.lcd.design.LcdInjectionConfig;
import com.iteaj.msn.lcd.entity.LcdDesign;
import com.iteaj.msn.lcd.service.ILcdDesignService;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class DesignConfigBuilder {

    private DataSourceConfig dataSourceConfig;

    /**
     * 模板路径配置信息
     */
    private final TemplateConfig template;

    /**
     * 数据库表信息
     */
    private List<TableInfo> tableInfoList;
    /**
     * 包配置详情
     */
    private Map<String, String> packageInfo;
    /**
     * 路径配置信息
     */
    private Map<String, String> pathInfo;
    /**
     * 策略配置
     */
    private StrategyConfig strategyConfig;
    /**
     * 全局配置信息
     */
    private GlobalConfig globalConfig;
    /**
     * 注入配置信息
     */
    private LcdInjectionConfig injectionConfig;
    /**
     * 过滤正则
     */
    private static final Pattern REGX = Pattern.compile("[~!/@#$%^&*()+\\\\\\[\\]|{};:'\",<.>?]+");

    /**
     * 在构造器中处理配置
     *
     * @param packageConfig    包配置
     * @param strategyConfig   表配置
     * @param template         模板配置
     * @param globalConfig     全局配置
     */
    public DesignConfigBuilder(PackageConfig packageConfig, DataSourceConfig dataSourceConfig, StrategyConfig strategyConfig
            , TemplateConfig template, GlobalConfig globalConfig, List<String> tableNames) {

        // 全局配置
        this.globalConfig = Optional.ofNullable(globalConfig).orElseGet(GlobalConfig::new);
        // 模板配置
        this.template = Optional.ofNullable(template).orElseGet(TemplateConfig::new);

        // 包配置
        if (null == packageConfig) {
            handlerPackage(this.template, this.globalConfig.getOutputDir(), new PackageConfig());
        } else {
            handlerPackage(this.template, this.globalConfig.getOutputDir(), packageConfig);
        }
        this.dataSourceConfig = dataSourceConfig;
        // 策略配置
        this.strategyConfig = Optional.ofNullable(strategyConfig).orElseGet(StrategyConfig::new);

        this.tableInfoList = getTablesInfo(strategyConfig, tableNames);
    }


    // ************************ 曝露方法 BEGIN*****************************

    /**
     * 所有包配置信息
     *
     * @return 包配置
     */
    public Map<String, String> getPackageInfo() {
        return packageInfo;
    }


    /**
     * 所有路径配置
     *
     * @return 路径配置
     */
    public Map<String, String> getPathInfo() {
        return pathInfo;
    }

    /**
     * 表信息
     *
     * @return 所有表信息
     */
    public List<TableInfo> getTableInfoList() {
        return tableInfoList;
    }

    public DesignConfigBuilder setTableInfoList(List<TableInfo> tableInfoList) {
        this.tableInfoList = tableInfoList;
        return this;
    }

    /**
     * 模板路径配置信息
     *
     * @return 所以模板路径配置信息
     */
    public TemplateConfig getTemplate() {
        return this.template;
    }

    // ****************************** 曝露方法 END**********************************

    /**
     * 处理包配置
     *
     * @param template  TemplateConfig
     * @param outputDir
     * @param config    PackageConfig
     */
    protected void handlerPackage(TemplateConfig template, String outputDir, PackageConfig config) {
        // 包信息
        packageInfo = CollectionUtils.newHashMapWithExpectedSize(7);
        packageInfo.put(ConstVal.MODULE_NAME, config.getModuleName());
        packageInfo.put(ConstVal.ENTITY, joinPackage(config.getParent(), config.getEntity()));
        packageInfo.put(ConstVal.MAPPER, joinPackage(config.getParent(), config.getMapper()));
        packageInfo.put(ConstVal.XML, joinPackage(config.getParent(), config.getXml()));
        packageInfo.put(ConstVal.SERVICE, joinPackage(config.getParent(), config.getService()));
        packageInfo.put(ConstVal.SERVICE_IMPL, joinPackage(config.getParent(), config.getServiceImpl()));
        packageInfo.put(ConstVal.CONTROLLER, joinPackage(config.getParent(), config.getController()));

        // 自定义路径
        Map<String, String> configPathInfo = config.getPathInfo();
        if (null != configPathInfo) {
            pathInfo = configPathInfo;
        } else {
            // 生成路径信息
            pathInfo = CollectionUtils.newHashMapWithExpectedSize(6);
            setPathInfo(pathInfo, template.getEntity(getGlobalConfig().isKotlin()), outputDir, ConstVal.ENTITY_PATH, ConstVal.ENTITY);
            setPathInfo(pathInfo, template.getMapper(), outputDir, ConstVal.MAPPER_PATH, ConstVal.MAPPER);
            setPathInfo(pathInfo, template.getXml(), outputDir, ConstVal.XML_PATH, ConstVal.XML);
            setPathInfo(pathInfo, template.getService(), outputDir, ConstVal.SERVICE_PATH, ConstVal.SERVICE);
            setPathInfo(pathInfo, template.getServiceImpl(), outputDir, ConstVal.SERVICE_IMPL_PATH, ConstVal.SERVICE_IMPL);
            setPathInfo(pathInfo, template.getController(), outputDir, ConstVal.CONTROLLER_PATH, ConstVal.CONTROLLER);
        }
    }

    protected void setPathInfo(Map<String, String> pathInfo, String template, String outputDir, String path, String module) {
        if (StringUtils.isNotBlank(template)) {
            pathInfo.put(path, joinPath(outputDir, packageInfo.get(module)));
        }
    }

    /**
     * 处理表对应的类名称
     *
     * @param tableList 表名称
     * @param config    策略配置项
     * @return 补充完整信息后的表
     */
    protected List<TableInfo> processTable(List<TableInfo> tableList, StrategyConfig config) {
        for (TableInfo tableInfo : tableList) {
            String entityName;
            INameConvert nameConvert = strategyConfig.getNameConvert();
            if (null != nameConvert) {
                // 自定义处理实体名称
                entityName = nameConvert.entityNameConvert(tableInfo);
            } else {
                entityName = NamingStrategy.capitalFirst(processName(tableInfo.getName(), config.getNaming(), config.getTablePrefix()));
            }
            if (StringUtils.isNotBlank(globalConfig.getEntityName())) {
                tableInfo.setConvert(true);
                tableInfo.setEntityName(String.format(globalConfig.getEntityName(), entityName));
            } else {
                tableInfo.setEntityName(strategyConfig, entityName);
            }
            if (StringUtils.isNotBlank(globalConfig.getMapperName())) {
                tableInfo.setMapperName(String.format(globalConfig.getMapperName(), entityName));
            } else {
                tableInfo.setMapperName(entityName + ConstVal.MAPPER);
            }
            if (StringUtils.isNotBlank(globalConfig.getXmlName())) {
                tableInfo.setXmlName(String.format(globalConfig.getXmlName(), entityName));
            } else {
                tableInfo.setXmlName(entityName + ConstVal.MAPPER);
            }
            if (StringUtils.isNotBlank(globalConfig.getServiceName())) {
                tableInfo.setServiceName(String.format(globalConfig.getServiceName(), entityName));
            } else {
                tableInfo.setServiceName("I" + entityName + ConstVal.SERVICE);
            }
            if (StringUtils.isNotBlank(globalConfig.getServiceImplName())) {
                tableInfo.setServiceImplName(String.format(globalConfig.getServiceImplName(), entityName));
            } else {
                tableInfo.setServiceImplName(entityName + ConstVal.SERVICE_IMPL);
            }
            if (StringUtils.isNotBlank(globalConfig.getControllerName())) {
                tableInfo.setControllerName(String.format(globalConfig.getControllerName(), entityName));
            } else {
                tableInfo.setControllerName(entityName + ConstVal.CONTROLLER);
            }
            // 检测导入包
            checkImportPackages(tableInfo);
        }
        return tableList;
    }

    /**
     * 检测导入包
     *
     * @param tableInfo ignore
     */
    protected void checkImportPackages(TableInfo tableInfo) {
        if (StringUtils.isNotBlank(strategyConfig.getSuperEntityClass())) {
            // 自定义父类
            tableInfo.getImportPackages().add(strategyConfig.getSuperEntityClass());
        } else if (globalConfig.isActiveRecord()) {
            // 无父类开启 AR 模式
            tableInfo.getImportPackages().add(com.baomidou.mybatisplus.extension.activerecord.Model.class.getCanonicalName());
        }
        if (null != globalConfig.getIdType() && tableInfo.isHavePrimaryKey()) {
            // 指定需要 IdType 场景
            tableInfo.getImportPackages().add(IdType.class.getCanonicalName());
            tableInfo.getImportPackages().add(TableId.class.getCanonicalName());
        }
        if (StringUtils.isNotBlank(strategyConfig.getVersionFieldName())
                && CollectionUtils.isNotEmpty(tableInfo.getFields())) {
            tableInfo.getFields().forEach(f -> {
                if (strategyConfig.getVersionFieldName().equals(f.getName())) {
                    tableInfo.getImportPackages().add(com.baomidou.mybatisplus.annotation.Version.class.getCanonicalName());
                }
            });
        }
    }


    /**
     * 获取所有的数据库表信息
     */
    protected List<TableInfo> getTablesInfo(StrategyConfig config, List<String> tableNames) {
        List<TableInfo> tableInfos = new ArrayList<>();
        ILcdDesignService designService = SpringUtils.getBean(ILcdDesignService.class);
        ListResult<LcdDesign> listResult = designService.list(Wrappers
                .<LcdDesign>lambdaQuery().in(LcdDesign::getTableName, tableNames));

        listResult.forEach(item -> {
            LcdTableInfo tableInfo = new LcdTableInfo();
            tableInfo.setComment(item.getComment()).setConvert(true).setName(item.getTableName());

            tableInfos.add(convertTableFields(tableInfo, item));
        });

        return processTable(tableInfos, strategyConfig);
    }

    /**
     * 表名匹配
     *
     * @param setTableName 设置表名
     * @param dbTableName  数据库表单
     * @return ignore
     */
    protected boolean tableNameMatches(String setTableName, String dbTableName) {
        return setTableName.equalsIgnoreCase(dbTableName)
                || StringUtils.matches(setTableName, dbTableName);
    }

    /**
     * 将字段信息与表信息关联
     *
     * @param tableInfo 表信息
     * @param entity    配置实体
     * @return ignore
     */
    protected TableInfo convertTableFields(LcdTableInfo tableInfo, LcdDesign entity) {
        boolean haveId = false;
        List<TableField> fieldList = new ArrayList<>();
        List<FormField> formFields = new ArrayList<>();
        List<FormField> searchFields = new ArrayList<>();
        List<FormTableField> formTableFields = new ArrayList<>();

        Map<String, TableField> idMaps = new HashMap<>();
        List<TableField> commonFieldList = new ArrayList<>();
        DbType dbType = this.dataSourceConfig.getDbType();
        IDbQuery dbQuery = dataSourceConfig.getDbQuery();

        ArrayNode metas = entity.getEdit().withArray("metas");
        metas.forEach(item -> {
            TableField field = new TableField();
            ObjectNode meta = (ObjectNode) item;
            ObjectNode tabModel = (ObjectNode) meta.get("tabModel");

            idMaps.put(meta.get("id").asText(), field);
            String columnName = tabModel.get("field").asText();

            // 处理其它信息
            field.setName(columnName);
            String newColumnName = columnName;

            IKeyWordsHandler keyWordsHandler = dataSourceConfig.getKeyWordsHandler();
            if (keyWordsHandler != null && keyWordsHandler.isKeyWords(columnName)) {
                System.err.printf("当前表[%s]存在字段[%s]为数据库关键字或保留字!%n", tableInfo.getName(), columnName);
                field.setKeyWords(true);
                newColumnName = keyWordsHandler.formatColumn(columnName);
            }

            field.setColumnName(newColumnName);
            field.setType(tabModel.get("fieldType").asText());
            INameConvert nameConvert = strategyConfig.getNameConvert();
            if (null != nameConvert) {
                field.setPropertyName(nameConvert.propertyNameConvert(field));
            } else {
                field.setPropertyName(strategyConfig, processName(field.getName(), strategyConfig.getColumnNaming()));
            }
            field.setColumnType(dataSourceConfig.getTypeConvert().processTypeConvert(globalConfig, field));
            String fieldCommentColumn = dbQuery.fieldComment();
            if (StringUtils.isNotBlank(fieldCommentColumn)) {
                field.setComment(formatComment(tabModel.get("title").asText()));
            }
            // 填充逻辑判断
            List<TableFill> tableFillList = getStrategyConfig().getTableFillList();
            if (null != tableFillList) {
                // 忽略大写字段问题
                tableFillList.stream().filter(tf -> tf.getFieldName().equalsIgnoreCase(field.getName()))
                        .findFirst().ifPresent(tf -> field.setFill(tf.getFieldFill().name()));
            }

            // 数据库字段
            if(tabModel.get("isDb").asBoolean()) {
                fieldList.add(field);

                if (strategyConfig.includeSuperEntityColumns(field.getName())) {
                    // 跳过公共字段
                    commonFieldList.add(field);
                    return;
                }
            }

        });
        // 处理ivzone编辑组件
        final JsonNode edit = entity.getIvzone().get("edit");
        if(edit != null) {
            final Iterator<Map.Entry<String, JsonNode>> fields = edit.fields();
            while (fields.hasNext()) {
                final Map.Entry<String, JsonNode> next = fields.next();
                final TableField tableField = idMaps.get(next.getKey());
                final FormField formField = new FormField().setComponent(String
                        .format(next.getValue().asText(), tableField.getPropertyName()));
                formFields.add(formField);
            }
        }
        // 处理ivzone搜索组件
        final JsonNode search = entity.getIvzone().get("search");
        if(search != null) {
            final Iterator<Map.Entry<String, JsonNode>> fields = search.fields();
            while (fields.hasNext()) {
                final Map.Entry<String, JsonNode> next = fields.next();
                final TableField tableField = idMaps.get(next.getKey());
                final FormField formField = new FormField().setComponent(String
                        .format(next.getValue().asText(), tableField.getPropertyName()));
                searchFields.add(formField);
            }
        }
        // 处理ivzone表格组件columns
        final JsonNode table = entity.getIvzone().get("table");
        if(table != null) {
            final Iterator<Map.Entry<String, JsonNode>> fields = table.fields();
            while (fields.hasNext()) {
                FormTableField formTableField = new FormTableField();
                final Map.Entry<String, JsonNode> next = fields.next();
                if(next.getKey().equalsIgnoreCase("action")) {
                    formTableField.setComponent(String.format(next.getValue().asText(), "action"));
                } else {
                    final TableField tableField = idMaps.get(next.getKey());
                    formTableField.setComponent(String.format(next.getValue().asText(), tableField.getPropertyName()));
                }

                formTableFields.add(formTableField);
            }
        }

        tableInfo.setFields(fieldList);
        tableInfo.setFormFields(formFields);
        tableInfo.setSearchFields(searchFields);
        tableInfo.setCommonFields(commonFieldList);
        tableInfo.setFormTableFields(formTableFields);

        return tableInfo;
    }

    /**
     * 连接路径字符串
     *
     * @param parentDir   路径常量字符串
     * @param packageName 包名
     * @return 连接后的路径
     */
    protected String joinPath(String parentDir, String packageName) {
        if (StringUtils.isBlank(parentDir)) {
            parentDir = System.getProperty(ConstVal.JAVA_TMPDIR);
        }
        if (!StringUtils.endsWith(parentDir, File.separator)) {
            parentDir += File.separator;
        }
        packageName = packageName.replaceAll("\\.", StringPool.BACK_SLASH + File.separator);
        return parentDir + packageName;
    }


    /**
     * 连接父子包名
     *
     * @param parent     父包名
     * @param subPackage 子包名
     * @return 连接后的包名
     */
    protected String joinPackage(String parent, String subPackage) {
        return StringUtils.isBlank(parent) ? subPackage : (parent + StringPool.DOT + subPackage);
    }


    /**
     * 处理字段名称
     *
     * @return 根据策略返回处理后的名称
     */
    protected String processName(String name, NamingStrategy strategy) {
        return processName(name, strategy, strategyConfig.getFieldPrefix());
    }


    /**
     * 处理表/字段名称
     *
     * @param name     ignore
     * @param strategy ignore
     * @param prefix   ignore
     * @return 根据策略返回处理后的名称
     */
    protected String processName(String name, NamingStrategy strategy, Set<String> prefix) {
        String propertyName;
        if (prefix.size() > 0) {
            if (strategy == NamingStrategy.underline_to_camel) {
                // 删除前缀、下划线转驼峰
                propertyName = NamingStrategy.removePrefixAndCamel(name, prefix);
            } else {
                // 删除前缀
                propertyName = NamingStrategy.removePrefix(name, prefix);
            }
        } else if (strategy == NamingStrategy.underline_to_camel) {
            // 下划线转驼峰
            propertyName = NamingStrategy.underlineToCamel(name);
        } else {
            // 不处理
            propertyName = name;
        }
        return propertyName;
    }


    public StrategyConfig getStrategyConfig() {
        return strategyConfig;
    }


    public DesignConfigBuilder setStrategyConfig(StrategyConfig strategyConfig) {
        this.strategyConfig = strategyConfig;
        return this;
    }


    public GlobalConfig getGlobalConfig() {
        return globalConfig;
    }


    public DesignConfigBuilder setGlobalConfig(GlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        return this;
    }


    public LcdInjectionConfig getInjectionConfig() {
        return injectionConfig;
    }


    public DesignConfigBuilder setInjectionConfig(LcdInjectionConfig injectionConfig) {
        this.injectionConfig = injectionConfig;
        return this;
    }

    /**
     * 格式化数据库注释内容
     *
     * @param comment 注释
     * @return 注释
     * @since 3.4.0
     */
    public String formatComment(String comment) {
        return StringUtils.isBlank(comment) ? StringPool.EMPTY : comment.replaceAll("\r\n", "\t");
    }
}
