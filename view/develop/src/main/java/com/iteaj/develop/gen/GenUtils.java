package com.iteaj.develop.gen;

import com.baomidou.mybatisplus.annotation.DbType;
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
import com.iteaj.develop.entity.GenField;
import com.iteaj.develop.entity.GenModule;
import com.iteaj.develop.entity.GenTable;
import com.iteaj.develop.entity.dto.GenTableDto;
import com.iteaj.develop.enums.GenStatus;
import com.iteaj.framework.gen.GenInjectionConfig;
import com.iteaj.framework.gen.GenTableField;
import com.iteaj.framework.gen.GenTableInfo;
import com.iteaj.framework.gen.GenVelocityTemplateEngine;
import com.iteaj.framework.spring.boot.config.DevelopProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanInitializationException;
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

    private static String outputDir = "";
    private static DevelopProperties properties;
    private static DevelopProperties.CodeGen codeGen;
    private static String MAVEN_JAVA = "src/main/java";
    private static String titleRegex = "[\\s（($#@&,，。.].*";
    private static String MAVEN_RESOURCE = "src/main/resources";
    private static Map<String, IColumnType> columnTypeMap = new HashMap<>();

    /**
     * 生成指定模块下指定的表
     * @param module
     * @param tables
     */
    public static void GenCode(GenModule module, List<GenTableDto> tables) {
        GlobalConfig globalConfig = getGlobalConfig(module);
        StrategyConfig strategyConfig = getStrategyConfig(module, tables);

        PackageConfig packageConfig = getPackageConfig(module);
        DataSourceConfig dataSourceConfig = getDataSourceConfig(module);
        TemplateConfig templateConfig = getTemplateConfig(module);

        // h5模板注入
        InjectionConfig injectionConfig = new GenInjectionConfig(codeGen);

        ConfigBuilder builder = new ConfigBuilder(packageConfig
                , dataSourceConfig, strategyConfig, templateConfig, globalConfig);

        // 处理代码生成器要生成的文件的路径
        handlePathInfo(builder, module);

        // 转换数据库的表信息到代码生成器的表信息
        handleTableInfo(builder, tables);

        injectionConfig.setConfig(builder);
        new AutoGenerator().setTemplateEngine(new GenVelocityTemplateEngine())
                .setCfg(injectionConfig).setConfig(builder).execute();
    }

    /**
     * 导入表和表字段
     * @param module
     * @return
     */
    public static List<GenTable> importTablesAndFields(GenModule module, Collection<GenTable> tables) {

        PackageConfig packageConfig = getPackageConfig(module);
        DataSourceConfig sourceConfig = getDataSourceConfig(module);
        StrategyConfig strategyConfig = getStrategyConfig(module, tables)
                .setSuperEntityColumns(null);

        GlobalConfig globalConfig = getGlobalConfig(module);
        ConfigBuilder configBuilder = new ConfigBuilder(packageConfig
                , sourceConfig, strategyConfig, null, globalConfig);

        // 生成表信息
        List<GenTable> genTables = new ArrayList<>();
        List<TableInfo> tableInfoList = configBuilder.getTableInfoList();
        tableInfoList.forEach(tableInfo -> {
            GenTableDto genTable = new GenTableDto(module.getMsn());
            BeanUtils.copyProperties(tableInfo, genTable);
            genTable.setDbName(module.getDbName());
            genTable.setPrefix(getPrefix(genTable.getEntityName()));
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
                genField.setDbName(module.getDbName());
                genField.setTableName(genTable.getName());
                genField.setTitle(getTitle(genField));

                genTable.addGenField(genField);
            });
        });

        return genTables;
    }

    /**
     * 获取此数据库下的所有表的表明
     * @param module
     * @return
     */
    public static List<String> getDbAllTableName(GenModule module) {
        PackageConfig packageConfig = getPackageConfig(module);
        DataSourceConfig sourceConfig = getDataSourceConfig(module);

        ConfigBuilder configBuilder = new ConfigBuilder(packageConfig
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
     * 用来处理h5页面需要的数据
     * 将表字段信息转成生成器所需要的表字段
     * @see com.iteaj.develop.entity.GenField 转成 {@link com.iteaj.framework.gen.GenTableField}
     * @param builder
     * @param tables
     */
    private static void handleTableInfo(ConfigBuilder builder, List<GenTableDto> tables) {
        List<TableInfo> tableInfos = builder.getTableInfoList();
        if(!CollectionUtils.isEmpty(tableInfos)) {
            Map<String, GenTableDto> tableMap = tables.stream().
                    collect(Collectors.toMap(item -> item.getName(), item -> item));

            // 通用的列
            Set<String> commonList = new HashSet<>();
            String[] columns = builder.getStrategyConfig().getSuperEntityColumns();
            if(null != columns) Arrays.asList(columns).forEach(item->commonList.add(item));

            List<TableInfo> infoList = new ArrayList<>();
            tableInfos.forEach(item->{
                GenTableDto genTable = tableMap.get(item.getName());
                if(null == genTable) return;

                GenTableInfo tableInfo = new GenTableInfo();

                // 将数据库里面的表集合转成生成器需要的表集合
                BeanUtils.copyProperties(genTable, tableInfo);

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
                infoList.add(tableInfo.buildOriSql(genTable.getWriteType()));
            });

            builder.setTableInfoList(infoList);
        }
    }

    private static void handlePathInfo(ConfigBuilder builder, GenModule module) {
        Map<String, String> pathInfo = builder.getPathInfo();
        Map<String, String> packageInfo = builder.getPackageInfo();
        // h5页面放在templates目录下面
        pathInfo.put(GenInjectionConfig.H5_PATH, joinPath(module, "H5", "templates"));
        // xml配置文件放在mapper目录下面
        pathInfo.put(ConstVal.XML_PATH, joinPath(module, ConstVal.XML, "mapper"));

        pathInfo.put(ConstVal.ENTITY_PATH, joinPath(module, ConstVal.ENTITY, packageInfo.get(ConstVal.ENTITY)));
        pathInfo.put(ConstVal.MAPPER_PATH, joinPath(module, ConstVal.MAPPER, packageInfo.get(ConstVal.MAPPER)));
        pathInfo.put(ConstVal.SERVICE_PATH, joinPath(module, ConstVal.SERVICE, packageInfo.get(ConstVal.SERVICE)));
        pathInfo.put(ConstVal.CONTROLLER_PATH, joinPath(module, ConstVal.CONTROLLER, packageInfo.get(ConstVal.CONTROLLER)));
        pathInfo.put(ConstVal.SERVICE_IMPL_PATH, joinPath(module, ConstVal.SERVICE_IMPL, packageInfo.get(ConstVal.SERVICE_IMPL)));
    }

    private static TemplateConfig getTemplateConfig(GenModule module) {
        return codeGen.getTemplate();
    }

    private static GlobalConfig getGlobalConfig(GenModule genModuleDto) {
        return properties.getGen().getGlobal().setOutputDir("").setFileOverride(false);
    }

    private static StrategyConfig getStrategyConfig(GenModule genModuleDto, Collection<? extends GenTable> tables) {
        StrategyConfig strategyConfig = new StrategyConfig();
        BeanUtils.copyProperties(properties.getGen().getStrategy(), strategyConfig);
        strategyConfig.setSuperEntityColumns(strategyConfig.getSuperEntityColumns());
        strategyConfig.setTablePrefix(genModuleDto.getTablePrefix());

        // 处理所有的表
        if(CollectionUtils.isEmpty(tables)) return strategyConfig;

        // 指定只处理哪几张表
        String[] include = tables.stream().map(item -> item.getName()).toArray(String[]::new);
        return strategyConfig.setInclude(include);
    }

    private static DataSourceConfig getDataSourceConfig(GenModule genModuleDto) {
        return new DataSourceConfig().setDbType(DbType.MYSQL).setUsername(genModuleDto.getAccount())
                .setPassword(genModuleDto.getPassword()).setDriverName(codeGen.getSqlDriver())
                .setUrl(String.format(codeGen.getSqlUrl(), genModuleDto.getDbName()));
    }

    private static PackageConfig getPackageConfig(GenModule genModuleDto) {
        return new PackageConfig().setParent(codeGen.getParentPackage())
                .setModuleName(genModuleDto.getMsn());
    }

    /**
     * 连接路径字符串
     *
     * @param module   路径常量字符串
     * @param packageName 包名
     * @return 连接后的路径
     */
    public static String joinPath(GenModule module, String type, String packageName) {
        String parentDir = getParentDir(type, module.getProject());
        if (StringUtils.isBlank(parentDir)) {
            parentDir = System.getProperty(ConstVal.JAVA_TMPDIR);
        }
        if (!StringUtils.endsWith(parentDir, File.separator)) {
            parentDir += File.separator;
        }
        packageName = packageName.replaceAll("\\.", StringPool.BACK_SLASH + File.separator);
        return parentDir + packageName;
    }

    private static String getParentDir(String type, String project) {
        switch (type) {
            case "H5": return String.format("%s\\view\\%s\\%s", outputDir, project, MAVEN_RESOURCE);
            case ConstVal.XML: return String.format("%s\\dao\\%s-dao\\%s", outputDir, project, MAVEN_RESOURCE);
            case ConstVal.CONTROLLER: return String.format("%s\\view\\%s\\%s", outputDir, project, MAVEN_JAVA);
            case ConstVal.SERVICE_IMPL: return String.format("%s\\service\\%s-service\\%s", outputDir, project, MAVEN_JAVA);
            default: return String.format("%s\\dao\\%s-dao\\%s", outputDir, project, MAVEN_JAVA);
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
        switch ((DbColumnType)genField.getColumnType()) {
            case DATE:
            case TIME:
            case YEAR:
            case LOCAL_DATE:
            case LOCAL_TIME:
            case YEAR_MONTH:
            case LOCAL_DATE_TIME:
            case TIMESTAMP:
            case DATE_SQL:
                return "date";
            case LONG:
            case BYTE:
            case FLOAT:
            case SHORT:
            case DOUBLE:
            case INTEGER:
            case BASE_INT:
            case BASE_LONG:
            case BASE_FLOAT:
            case BASE_SHORT:
            case BASE_DOUBLE:
            case BIG_DECIMAL:
            case BASE_BYTE:
            case BIG_INTEGER:
                return "number";
            case CLOB:
                return "textarea";
            case BOOLEAN:
            case BASE_BOOLEAN:
                return "radio";
            case STRING:
            case BASE_CHAR:
            case CHARACTER:
            default: return "text";
        }
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
        GenUtils.outputDir = codeGen.getRootDir();

        // 如果没有指定代码输出目录, 则使用当前项目开发目录
        if(StringUtils.isBlank(codeGen.getRootDir())) {
            File file = new File(GenUtils.outputDir);

            if(file.exists()) {
                if(!file.isDirectory())
                    throw new BeanInitializationException("生成的代码的输出路径必须是目录");

                File parentFile = file.getParentFile().getParentFile();
                GenUtils.outputDir = parentFile.getPath();
            }
        }

        columnTypeMap = Arrays.asList(DbColumnType.values()).stream()
                .collect(Collectors.toMap(item->item.getType(), item->item, (v1,v2)-> v2));
    }
}
