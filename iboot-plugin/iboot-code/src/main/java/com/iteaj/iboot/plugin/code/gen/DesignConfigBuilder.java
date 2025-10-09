package com.iteaj.iboot.plugin.code.gen;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iteaj.framework.result.ListResult;
import com.iteaj.framework.spring.SpringUtils;
import com.iteaj.iboot.plugin.code.LowCodeConst;
import com.iteaj.iboot.plugin.code.design.LcdInjectionConfig;
import com.iteaj.iboot.plugin.code.entity.LcdDesign;
import com.iteaj.iboot.plugin.code.service.ILcdDesignService;

import java.io.File;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class DesignConfigBuilder {

    private DataSourceConfig dataSourceConfig;
    private final TemplateConfig template;
    private List<TableInfo> tableInfoList;
    private Map<String, String> packageInfo;
    private Map<String, String> pathInfo;
    private StrategyConfig strategyConfig;
    private GlobalConfig globalConfig;
    private LcdInjectionConfig injectionConfig;
    private static final Pattern REGX = Pattern.compile("[~!/@#$%^&*()+\\\\\\[\\]|{};:'\",<.>?]+");

    public DesignConfigBuilder(PackageConfig packageConfig, DataSourceConfig dataSourceConfig, StrategyConfig strategyConfig,
                               TemplateConfig template, GlobalConfig globalConfig, List<String> tableNames) {

        this.globalConfig = Optional.ofNullable(globalConfig).orElseGet((Supplier<? extends GlobalConfig>) new PackageConfig.Builder().build());

        // 使用 TemplateConfig.Builder 配置模板路径
        this.template = Optional.ofNullable(template).orElseGet(() -> new TemplateConfig.Builder()
                .entity("/templates/lcd.entity.java")
                .mapper("/templates/lcd.mapper.java")
                .xml("/templates/lcd.mapper.xml")
                .service("/templates/lcd.service.java")
                .serviceImpl("/templates/lcd.serviceImpl.java")
                .controller("/templates/lcd.controller.java")
                .build());

        if (packageConfig == null) {
            // 使用 PackageConfig.Builder 创建默认 PackageConfig
            handlerPackage(this.template, this.globalConfig.getOutputDir(), new PackageConfig.Builder().build());
        } else {
            handlerPackage(this.template, this.globalConfig.getOutputDir(), packageConfig);
        }
        this.dataSourceConfig = dataSourceConfig;
        this.strategyConfig = Optional.ofNullable(strategyConfig).orElseGet(() -> new StrategyConfig.Builder().build());
        this.tableInfoList = getTablesInfo(strategyConfig, tableNames);
    }

    public Map<String, String> getPackageInfo() {
        return packageInfo;
    }

    public Map<String, String> getPathInfo() {
        return pathInfo;
    }

    public List<TableInfo> getTableInfoList() {
        return tableInfoList;
    }

    public DesignConfigBuilder setTableInfoList(List<TableInfo> tableInfoList) {
        this.tableInfoList = tableInfoList;
        return this;
    }

    public TemplateConfig getTemplate() {
        return this.template;
    }

    protected void handlerPackage(TemplateConfig template, String outputDir, PackageConfig config) {
        packageInfo = CollectionUtils.newHashMapWithExpectedSize(7);
        packageInfo.put(ConstVal.MODULE_NAME, config.getModuleName());
        packageInfo.put(LowCodeConst.DTO, joinPackage(config.getParent(), "dto"));
        packageInfo.put(ConstVal.ENTITY, joinPackage(config.getParent(), config.getEntity()));
        packageInfo.put(ConstVal.MAPPER, joinPackage(config.getParent(), config.getMapper()));
        packageInfo.put(ConstVal.XML, joinPackage(config.getParent(), config.getXml()));
        packageInfo.put(ConstVal.SERVICE, joinPackage(config.getParent(), config.getService()));
        packageInfo.put(ConstVal.SERVICE_IMPL, joinPackage(config.getParent(), config.getServiceImpl()));
        packageInfo.put(ConstVal.CONTROLLER, joinPackage(config.getParent(), config.getController()));

        Map<String, String> configPathInfo = config.getPathInfo();
        if (configPathInfo != null) {
            pathInfo = configPathInfo;
        } else {
            pathInfo = CollectionUtils.newHashMapWithExpectedSize(6);
            setPathInfo(pathInfo, template.getEntity(), outputDir, ConstVal.ENTITY_PATH, ConstVal.ENTITY);
            setPathInfo(pathInfo, template.getMapper(), outputDir, ConstVal.MAPPER_PATH, ConstVal.MAPPER);
            setPathInfo(pathInfo, template.getXml(), outputDir, ConstVal.XML_PATH, ConstVal.XML);
            setPathInfo(pathInfo, "lcd.dto.java", outputDir, LowCodeConst.DTO_PATH, LowCodeConst.DTO);
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

    protected List<TableInfo> processTable(List<TableInfo> tableList, StrategyConfig config) {
        for (TableInfo tableInfo : tableList) {
            String entityName;
            INameConvert nameConvert = strategyConfig.getNameConvert();
            if (nameConvert != null) {
                entityName = nameConvert.entityNameConvert(tableInfo);
            } else {
                entityName = NamingStrategy.capitalFirst(processName(tableInfo.getName(), config.getNaming(), config.getTablePrefix()));
            }
            if (StringUtils.isNotBlank(globalConfig.getEntityName())) {
                tableInfo.setConvert(true);
                tableInfo.setEntityName(globalConfig.getEntityName().formatted(entityName));
            } else {
                tableInfo.setEntityName(strategyConfig, entityName);
            }
            if (StringUtils.isNotBlank(globalConfig.getMapperName())) {
                tableInfo.setMapperName(globalConfig.getMapperName().formatted(entityName));
            } else {
                tableInfo.setMapperName(entityName + ConstVal.MAPPER);
            }
            if (StringUtils.isNotBlank(globalConfig.getXmlName())) {
                tableInfo.setXmlName(globalConfig.getXmlName().formatted(entityName));
            } else {
                tableInfo.setXmlName(entityName + ConstVal.MAPPER);
            }
            if (StringUtils.isNotBlank(globalConfig.getServiceName())) {
                tableInfo.setServiceName(globalConfig.getServiceName().formatted(entityName));
            } else {
                tableInfo.setServiceName("I" + entityName + ConstVal.SERVICE);
            }
            if (StringUtils.isNotBlank(globalConfig.getServiceImplName())) {
                tableInfo.setServiceImplName(globalConfig.getServiceImplName().formatted(entityName));
            } else {
                tableInfo.setServiceImplName(entityName + ConstVal.SERVICE_IMPL);
            }
            if (StringUtils.isNotBlank(globalConfig.getControllerName())) {
                tableInfo.setControllerName(globalConfig.getControllerName().formatted(entityName));
            } else {
                tableInfo.setControllerName(entityName + ConstVal.CONTROLLER);
            }
            checkImportPackages(tableInfo);
        }
        return tableList;
    }

    protected void checkImportPackages(TableInfo tableInfo) {
        if (StringUtils.isNotBlank(strategyConfig.getSuperEntityClass())) {
            tableInfo.getImportPackages().add(strategyConfig.getSuperEntityClass());
        } else if (globalConfig.isActiveRecord()) {
            tableInfo.getImportPackages().add(com.baomidou.mybatisplus.extension.activerecord.Model.class.getCanonicalName());
        }
        if (null != globalConfig.getIdType() && tableInfo.isHavePrimaryKey()) {
            tableInfo.getImportPackages().add(IdType.class.getCanonicalName());
            tableInfo.getImportPackages().add(TableId.class.getCanonicalName());
        }
        if (StringUtils.isNotBlank(strategyConfig.getVersionFieldName()) && CollectionUtils.isNotEmpty(tableInfo.getFields())) {
            tableInfo.getFields().forEach(f -> {
                if (strategyConfig.getVersionFieldName().equals(f.getName())) {
                    tableInfo.getImportPackages().add(com.baomidou.mybatisplus.annotation.Version.class.getCanonicalName());
                }
            });
        }
    }

    protected List<TableInfo> getTablesInfo(StrategyConfig config, List<String> tableNames) {
        List<TableInfo> tableInfos = new ArrayList<>();
        ILcdDesignService designService = SpringUtils.getBean(ILcdDesignService.class);
        ListResult<LcdDesign> listResult = designService.list(Wrappers.<LcdDesign>lambdaQuery().in(LcdDesign::getTableName, tableNames));

        listResult.forEach(item -> {
            DesignTableInfo tableInfo = new DesignTableInfo();
            tableInfo.setComment(item.getComment()).setConvert(true).setName(item.getTableName());
            tableInfos.add(convertTableFields(tableInfo, item));
        });

        return processTable(tableInfos, config);
    }

    protected TableInfo convertTableFields(DesignTableInfo tableInfo, LcdDesign entity) {
        ArrayNode metas = entity.getEdit().withArray("metas");
        metas.forEach(item -> {
            LcdField field = new LcdField();
            ObjectNode meta = (ObjectNode) item;
            ObjectNode tabModel = (ObjectNode) meta.get("tabModel");
            String columnName = tabModel.get("field").asText();
            field.setName(columnName);
            handleFieldConvert(field);
        });
        return tableInfo;
    }

    /**
     * 适配 MyBatis Plus 3.5.14 的自动填充逻辑
     */
    private void handleFieldConvert(LcdField field) {
        INameConvert nameConvert = strategyConfig.getNameConvert();
        if (nameConvert != null) {
            field.setPropertyName(nameConvert.propertyNameConvert(field));
        } else {
            field.setPropertyName(strategyConfig, processName(field.getName(), strategyConfig.getColumnNaming()));
        }

        field.setColumnType(dataSourceConfig.getTypeConvert().processTypeConvert(globalConfig, field));

        // 处理自动填充
        List<com.baomidou.mybatisplus.generator.fill.Column> tableFillList = strategyConfig.getTableFillList();
        if (CollectionUtils.isNotEmpty(tableFillList)) {
            tableFillList.stream()
                    .filter(tf -> tf.getColumnName().equalsIgnoreCase(field.getName()))
                    .findFirst()
                    .ifPresent(tf -> {
                        field.setFill(tf.getFieldFill().name());
                        field.getImportPackages().add(FieldFill.class.getCanonicalName());
                        field.getImportPackages().add(TableField.class.getCanonicalName());
                    });
        }
    }

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

    protected String joinPackage(String parent, String subPackage) {
        return StringUtils.isBlank(parent) ? subPackage : (parent + StringPool.DOT + subPackage);
    }

    protected String processName(String name, NamingStrategy strategy) {
        return processName(name, strategy, strategyConfig.getFieldPrefix());
    }

    protected String processName(String name, NamingStrategy strategy, Set<String> prefix) {
        String propertyName;
        if (!prefix.isEmpty()) {
            if (strategy == NamingStrategy.underline_to_camel) {
                propertyName = NamingStrategy.removePrefixAndCamel(name, prefix);
            } else {
                propertyName = NamingStrategy.removePrefix(name, prefix);
            }
        } else if (strategy == NamingStrategy.underline_to_camel) {
            propertyName = NamingStrategy.underlineToCamel(name);
        } else {
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

    public String formatComment(String comment) {
        return StringUtils.isBlank(comment) ? StringPool.EMPTY : comment.replaceAll("\r\n", "\t");
    }
}