package com.iteaj.dev.gen;

import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.iteaj.dev.dto.GenTableDto;
import com.iteaj.dev.entity.GenField;
import com.iteaj.dev.entity.GenModule;
import com.iteaj.dev.entity.GenTable;
import com.iteaj.dev.enums.GenStatus;
import com.iteaj.framework.gen.GenTableField;
import com.iteaj.framework.gen.GenTableInfo;
import com.iteaj.framework.gen.GenVelocityTemplateEngine;
import com.iteaj.dev.config.DevelopProperties;
import com.iteaj.util.module.mvc.ServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * create time: 2020/4/28
 *
 * @author iteaj
 * @since 1.0
 */
@Component
public class GenUtils implements InitializingBean {

    private static DevelopProperties properties;
    private static DevelopProperties.CodeGen codeGen;
    private static String MAVEN_JAVA = "src"+File.separator+"main"+File.separator+"java";
    private static String titleRegex = "[\\s（($#@&,，。.].*";
    private static String MAVEN_RESOURCE = "src"+File.separator+"main"+File.separator+"resources";
    private static Set<String> excludeEditField = new HashSet<>();
    private static Set<String> includeSearchField = new HashSet<>();
    private static Map<String, IColumnType> columnTypeMap = new HashMap<>();

    static {
        // 匹配以下名称的将不作为编辑字段
        excludeEditField.add("create_time");
        excludeEditField.add("update_time");

        // 包含以下关键字的会被作为搜索字段
        includeSearchField.add("name");
        includeSearchField.add("phone");
        includeSearchField.add("status");
    }

    /**
     * 生成指定模块下指定的表
     * @param tables
     */
    public static void GenCode(GenConfig config, List<GenTableDto> tables) {

        GlobalConfig globalConfig = new GlobalConfig();
        StrategyConfig strategyConfig = codeGen.getStrategy();
        PackageConfig packageConfig = new PackageConfig();
        DataSourceConfig dataSourceConfig = codeGen.getSource();
        TemplateConfig templateConfig = codeGen.getTemplate();

        BeanUtils.copyProperties(codeGen.getPackageConfig(), packageConfig);
        packageConfig.setModuleName(config.getAlias());

        BeanUtils.copyProperties(codeGen.getGlobal(), globalConfig);
        globalConfig.setFileOverride(config.isOverwrite());

        String outputDir;
        if(config.isWrite()) { // 输出到项目里
            outputDir = codeGen.getRootDir() + File.separator + config.getParent();
        } else { // 输出到其他路径
            outputDir = codeGen.getGlobal().getOutputDir() + File.separator;
        }
        config.setOutRootDir(outputDir);
        globalConfig.setOutputDir(outputDir);

        // h5模板注入
        InjectionConfig injectionConfig = new GenInjectionConfig(codeGen);

        ConfigBuilder builder = new ConfigBuilder(packageConfig
                , dataSourceConfig, strategyConfig, templateConfig, globalConfig);

        // 处理代码生成器要生成的文件的路径
        handlePathInfo(builder, config);

        // 转换数据库的表信息到代码生成器的表信息
        handleTableInfo(config, builder, tables);

        injectionConfig.setConfig(builder);
        new AutoGenerator().setTemplateEngine(new GenVelocityTemplateEngine())
                .setCfg(injectionConfig).setConfig(builder).execute();
    }

    /**
     * 导入表和表字段
     * @param tables 要导入的表信息
     * @return
     */
    public static List<GenTable> importTablesAndFields(List<GenTable> tables) {


        DataSourceConfig source = codeGen.getSource();
        StrategyConfig strategy = getStrategyConfig(tables);
        GlobalConfig global = codeGen.getGlobal();
        PackageConfig packageConfig = new PackageConfig();
        ConfigBuilder configBuilder = new ConfigBuilder(packageConfig
                , source, strategy, null, global);

        // 生成表信息
        List<GenTable> genTables = new ArrayList<>();
        List<TableInfo> tableInfoList = configBuilder.getTableInfoList();
        tableInfoList.forEach(tableInfo -> {
            GenTableDto genTable = new GenTableDto();
            BeanUtils.copyProperties(tableInfo, genTable);

            genTable.setDisabled(false);
            genTable.setPrefix(getPrefix(tableInfo.getEntityName()));
            genTables.add(genTable.setStatus(GenStatus.未生成));

            // 生成字段信息
            List<TableField> allFields = new ArrayList<>(tableInfo.getFields());
            if(!CollectionUtils.isEmpty(tableInfo.getCommonFields())) {
                allFields.addAll(tableInfo.getCommonFields());
            }

            genTable.setViewType(getViewType(allFields));

            allFields.forEach(item -> {
                GenField genField = new GenField(item.isKeyIdentityFlag(), item.getColumnType().getType());
                BeanUtils.copyProperties(item, genField);

                genField.setCtype(getCType(item));
                genField.setTitle(getTitle(genField));
                genField.setTableName(genTable.getName());
                genField.setEditFlag(editFlagHandle(item));
                genField.setSearchFlag(searchFlagHandle(item));

                genTable.addGenField(genField);
            });
        });

        return genTables;
    }

    private static Boolean searchFlagHandle(TableField item) {
        if(item.isKeyFlag()) return false;
        return includeSearchField.stream()
                .anyMatch(s->item.getName().contains(s));
    }

    private static Boolean editFlagHandle(TableField item) {
        if(item.isKeyFlag()) return false;
        return !excludeEditField.contains(item.getName());
    }

    /**
     * 获取此数据库下的所有表的表明
     * @param module
     * @return
     */
    public static List<String> getDbAllTableName(GenModule module) {
        DataSourceConfig sourceConfig = codeGen.getSource();

        ConfigBuilder configBuilder = new ConfigBuilder(null
                , sourceConfig, null, null, null);

        return configBuilder.getTableInfoList().stream()
                .map(item->item.getName()).collect(Collectors.toList());
    }

    private static String getPrefix(String name) {
        if(name.length()<=3) return name;
        char[] chars = name.toCharArray();

        StringBuilder sb = new StringBuilder();
        for(char item : chars) {
            if(item>='A' && item<='Z') {
                sb.append(item);
            }
        }

        if(sb.length() <= 2) {
            sb.append(chars[chars.length-1]);
        }

        return sb.toString().toLowerCase();
    }

    /**
     * 将表字段信息转成生成器所需要的表字段
     * @param builder
     * @param tables 用户自定要要生成的表信息
     */
    private static void handleTableInfo(GenConfig config, ConfigBuilder builder, List<GenTableDto> tables) {
        if(!CollectionUtils.isEmpty(tables)) {
            Map<String, TableInfo> tableInfoMap = builder.getTableInfoList().stream().
                    collect(Collectors.toMap(item -> item.getName(), item -> item));

            // 通用的列
            Set<String> commonList = new HashSet<>();
            String[] columns = builder.getStrategyConfig().getSuperEntityColumns();
            if(null != columns) Arrays.asList(columns).forEach(item->commonList.add(item));

            List<TableInfo> infoList = new ArrayList<>();
            tables.forEach(genTable->{
                // tableMap里面如果不包含此张表, 则此表不需要生成
                TableInfo info = tableInfoMap.get(genTable.getName());
                if(null == info) throw new ServiceException("表信息不存在: " + genTable.getName());

                GenTableInfo tableInfo = new GenTableInfo();

                // 将数据库里面的表集合转成生成器需要的表集合
                BeanUtils.copyProperties(info, tableInfo);
                tableInfo.setPrefix(genTable.getPrefix());
                tableInfo.setXmlName(genTable.getXmlName());
                tableInfo.setComment(genTable.getComment());
                tableInfo.setViewType(genTable.getViewType());
                tableInfo.setEntityName(genTable.getEntityName());
                tableInfo.setMapperName(genTable.getMapperName());
                tableInfo.setServiceName(genTable.getServiceName());
                tableInfo.setControllerName(genTable.getControllerName());
                tableInfo.setServiceImplName(genTable.getServiceImplName());

                List<TableField> newTableFields = new ArrayList<>();
                List<TableField> newCommonFields = new ArrayList<>();

                // 将数据库里面的字段集合转成生成器需要的字段集合
                genTable.getFieldList().forEach(field -> {
                    GenTableField tableField = new GenTableField();
                    BeanUtils.copyProperties(field, tableField);
                    tableField.setColumnType(columnTypeMap.get(field.getColumnType()));

                    if(commonList.contains(field.getName())) {
                        newCommonFields.add(tableField);
                    } else {
                        newTableFields.add(tableField);
                    }
                });

                tableInfo.setFields(newTableFields);
                tableInfo.setCommonFields(newCommonFields);
                infoList.add(tableInfo.buildOriSql(config.getWriteType()));
            });

            builder.setTableInfoList(infoList);
        }
    }

    private static void handlePathInfo(ConfigBuilder builder, GenConfig config) {
        Map<String, String> pathInfo = builder.getPathInfo();
        Map<String, String> packageInfo = builder.getPackageInfo();
        // h5页面放在templates目录下面
        pathInfo.put(GenInjectionConfig.H5_PATH, joinPath(config, "H5", "templates"));
        // xml配置文件放在mapper目录下面
        pathInfo.put(ConstVal.XML_PATH, joinPath(config, ConstVal.XML, "mapper.xml"));

        pathInfo.put(ConstVal.ENTITY_PATH, joinPath(config, ConstVal.ENTITY, packageInfo.get(ConstVal.ENTITY)));
        pathInfo.put(ConstVal.MAPPER_PATH, joinPath(config, ConstVal.MAPPER, packageInfo.get(ConstVal.MAPPER)));
        pathInfo.put(ConstVal.SERVICE_PATH, joinPath(config, ConstVal.SERVICE, packageInfo.get(ConstVal.SERVICE)));
        pathInfo.put(ConstVal.CONTROLLER_PATH, joinPath(config, ConstVal.CONTROLLER, packageInfo.get(ConstVal.CONTROLLER)));
        pathInfo.put(ConstVal.SERVICE_IMPL_PATH, joinPath(config, ConstVal.SERVICE_IMPL, packageInfo.get(ConstVal.SERVICE_IMPL)));
    }

    private static StrategyConfig getStrategyConfig(Collection<? extends GenTable> tables) {
        StrategyConfig strategyConfig = new StrategyConfig();
        BeanUtils.copyProperties(properties.getGen().getStrategy(), strategyConfig);
        strategyConfig.setSuperEntityColumns(strategyConfig.getSuperEntityColumns());

        // 处理所有的表
        if(CollectionUtils.isEmpty(tables)) return strategyConfig;

        // 指定只处理哪几张表
        String[] include = tables.stream().map(item -> item.getName()).toArray(String[]::new);
        return strategyConfig.setInclude(include);
    }

    /**
     * 连接路径字符串
     *
     * @param config   路径常量字符串
     * @param packageName 包名
     * @return 连接后的路径
     */
    public static String joinPath(GenConfig config, String type, String packageName) {
        String parentDir = getParentDir(config.getOutRootDir(), type, config.getProject());
        if (StringUtils.isBlank(parentDir)) {
            parentDir = System.getProperty(ConstVal.JAVA_TMPDIR);
        }
        if (!StringUtils.endsWith(parentDir, File.separator)) {
            parentDir += File.separator;
        }
        packageName = packageName.replaceAll("\\.", StringPool.BACK_SLASH + File.separator);
        return parentDir + packageName;
    }

    private static String getParentDir(String outRootDir, String type, String project) {
        switch (type) {
            case "H5":
            case ConstVal.XML:
                return String.format("%s%s%s%s%s", outRootDir, File.separator, project, File.separator, MAVEN_RESOURCE);
            case ConstVal.SERVICE_IMPL:
            case ConstVal.CONTROLLER:
            default: return String.format("%s%s%s%s%s", outRootDir, File.separator, project, File.separator, MAVEN_JAVA);
        }
    }

    /**
     * 连接父子包名
     *
     * @param parent     父包名
     * @param subPackage 子包名
     * @return 连接后的包名
     */
    private static String joinPackage(String parent, String subPackage) {
        if (StringUtils.isBlank(parent)) {
            return subPackage;
        }
        return parent + StringPool.DOT + subPackage;
    }

    public DevelopProperties getProperties() {
        return GenUtils.properties;
    }

    private static String getCType(TableField genField) {
//        switch ((DbColumnType)genField.getColumnType()) {
//            case DbColumnType.DATE:
//            case DbColumnType.TIME:
//            case DbColumnType.YEAR:
//            case DbColumnType.LOCAL_DATE:
//            case DbColumnType.LOCAL_TIME:
//            case DbColumnType.YEAR_MONTH:
//            case DbColumnType.LOCAL_DATE_TIME:
//            case DbColumnType.TIMESTAMP:
//            case DbColumnType.DATE_SQL:
//                return "date";
//            case DbColumnType.LONG:
//            case DbColumnType.BYTE:
//            case DbColumnType.FLOAT:
//            case DbColumnType.SHORT:
//            case DbColumnType.DOUBLE:
//            case DbColumnType.INTEGER:
//            case DbColumnType.BASE_INT:
//            case DbColumnType.BASE_LONG:
//            case DbColumnType.BASE_FLOAT:
//            case DbColumnType.BASE_SHORT:
//            case DbColumnType.BASE_DOUBLE:
//            case DbColumnType.BIG_DECIMAL:
//            case DbColumnType.BASE_BYTE:
//            case DbColumnType.BIG_INTEGER:
//                return "number";
//            case DbColumnType.CLOB:
//                return "textarea";
//            case DbColumnType.BOOLEAN:
//            case DbColumnType.BASE_BOOLEAN:
//                return "radio";
//            case DbColumnType.STRING:
//            case DbColumnType.BASE_CHAR:
//            case DbColumnType.CHARACTER:
//            default: return "text";
//        }
        return null;
    }

    /**
     * 返回视图类型
     * @param allFields
     * @return
     */
    private static String getViewType(List<TableField> allFields) {
        if(allFields.size() <= 7)
            return "ivz-drawer-view";
        else return "ivz-basic-view";
    }

    /**
     * 获取h5页面字段标题
     * @param genField
     * @return
     */
    private static String getTitle(GenField genField) {
        String comment = genField.getComment();
        if(comment == null) return "";

        String replaceAll = comment.replaceAll(titleRegex, "");
        return replaceAll;
    }

    @Autowired
    public GenUtils setProperties(DevelopProperties properties) {
        GenUtils.properties = properties;
        this.codeGen = properties.getGen();
        return this;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        columnTypeMap = Arrays.asList(DbColumnType.values()).stream()
                .collect(Collectors.toMap(item->item.getType(), item->item, (v1,v2)-> v2));
    }
}
