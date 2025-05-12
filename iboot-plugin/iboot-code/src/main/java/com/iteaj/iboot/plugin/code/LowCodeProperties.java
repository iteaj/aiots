package com.iteaj.iboot.plugin.code;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.IFill;
import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import lombok.Data;
import lombok.Getter;
import org.apache.ibatis.cache.Cache;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 低代码配置
 */
@Data
@ConfigurationProperties("low.code")
public class LowCodeProperties implements InitializingBean {

    /**
     * 实体类配置
     */
    private Entity entity = new Entity();

    /**
     * mapper配置
     */
    private Mapper mapper = new Mapper();

    /**
     * 业务层配置
     */
    private Service service  = new Service();

    /**
     * 控制层配置
     */
    private Controller controller  = new Controller();

    /**
     * 数据源配置
     */
    private DataSource dataSource;
    /**
     * 搜索配置
     */
    private LcdFormConfig form;

    /**
     * 作者
     */
    private String author;

    /**
     * 父包名
     */
    private String parent;

    /**
     * 表前缀
     */
    private List<String> tablePrefix;

    /**
     * 字段前缀
     */
    private List<String> fieldPrefix;

    /**
     * 需要包含的表名，允许正则表达式（与exclude二选一配置）<br/>
     * 当{@link #enableSqlFilter}为true时，正则表达式无效.
     */
    private final List<String> include = new ArrayList<>();

    /**
     * 需要排除的表名，允许正则表达式<br/>
     * 当{@link #enableSqlFilter}为true时，正则表达式无效.
     */
    private final List<String> exclude = new ArrayList<>();

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 本地项目根路径
     */
    private String projectPath;

    /**
     * 前端项目根路径
     */
    private String webProjectPath;

    /**
     * 是否覆盖已有文件（默认 false）
     *
     * @since 3.5.2
     */
    private boolean fileOverride;

    @Override
    public void afterPropertiesSet() throws Exception {
        if(StringUtils.hasText(this.getProjectPath())) {
            // 获取父路径
            File file = new File(this.getProjectPath());
            if(file.exists() && file.isDirectory()) {
                this.projectPath = file.getParentFile().getParentFile().getPath();
            }
        }
    }

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
    public static class DataSource {
        /**
         * url
         */
        private String url;

        /**
         * 用户名
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         *
         */
        private String driver;

        /**
         * 数据库类型
         */
        private DbType dbType;
    }


    @Data
    public static class Entity {

        /**
         * 自定义继承的Entity类全称，带包名
         */
        private String superClass = "com.iteaj.framework.BaseEntity";

        /**
         * 自定义基础的Entity类，公共字段
         */
        @Getter
        private final Set<String> superEntityColumns = new HashSet<>();

        /**
         * 自定义忽略字段
         * <a href="https://github.com/baomidou/generator/issues/46">...</a>
         */
        private final Set<String> ignoreColumns = new HashSet<>();

        /**
         * 实体是否生成 serialVersionUID
         */
        private boolean serialVersionUID = true;

        /**
         * 【实体】是否生成字段常量（默认 false）<br>
         * -----------------------------------<br>
         * public static final String ID = "test_id";
         */
        private boolean columnConstant;

        /**
         * 【实体】是否为链式模型（默认 false）
         *
         * @since 3.3.2
         */
        private boolean chain = true;

        /**
         * 【实体】是否为lombok模型（默认 false）<br>
         * <a href="https://projectlombok.org/">document</a>
         */
        private boolean lombok = true;

        /**
         * 乐观锁字段名称(数据库字段)
         *
         * @since 3.5.0
         */
        private String versionColumnName;

        /**
         * 乐观锁属性名称(实体字段)
         *
         * @since 3.5.0
         */
        private String versionPropertyName;

        /**
         * 逻辑删除字段名称(数据库字段)
         *
         * @since 3.5.0
         */
        private String logicDeleteColumnName;

        /**
         * 逻辑删除属性名称(实体字段)
         *
         * @since 3.5.0
         */
        private String logicDeletePropertyName;

        /**
         * 表填充字段
         */
        private final List<IFill> tableFillList = new ArrayList<>();

        /**
         * 数据库表映射到实体的命名策略，默认下划线转驼峰命名
         */
        private NamingStrategy naming = NamingStrategy.underline_to_camel;

        /**
         * 数据库表字段映射到实体的命名策略
         * <p>未指定按照 naming 执行</p>
         */
        private NamingStrategy columnNaming = null;

        /**
         * 开启 ActiveRecord 模式（默认 false）
         *
         * @since 3.5.0
         */
        private boolean activeRecord;

        /**
         * 指定生成的主键的ID类型
         *
         * @since 3.5.0
         */
        private IdType idType = IdType.AUTO;

    }

    @Data
    public static class Mapper {
        /**
         * 是否覆盖已有文件（默认 false）
         *
         * @since 3.5.2
         */
        private boolean fileOverride;

        /**
         * 设置缓存实现类
         */
        private Class<? extends Cache> cache;

        /**
         * 是否生成XML
         */
        private boolean generateMapperXml = true;

        /**
         * 是否生成Mapper
         */
        private boolean generateMapper = true;

        /**
         * 自定义继承的Mapper类全称，带包名
         */
        private String superClass = ConstVal.SUPER_MAPPER_CLASS;
    }

    @Data
    public static class Service {

        /**
         * 自定义继承的Service类全称，带包名
         */
        private String superServiceClass = "com.iteaj.framework.IBaseService";

        /**
         * 自定义继承的ServiceImpl类全称，带包名
         */
        private String superServiceImplClass = "com.iteaj.framework.BaseServiceImpl";
    }

    @Data
    public static class Controller {

        /**
         * 自定义继承的Controller类全称，带包名
         */
        private String superClass = "com.iteaj.framework.BaseController";
    }
}
