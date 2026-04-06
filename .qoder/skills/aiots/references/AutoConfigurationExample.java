// ===== 插件自动配置类示例 =====
//
// 注册方式一（推荐）：自动配置类直接放在插件包根目录下，
//   在 META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 中声明全限定名
//   此方式不需要加 @AutoConfiguration 注解
//
// 注册方式二：将自动配置类放在 com.iteaj.iboot.plugin.*.autoconfig 包下，
//   由框架 PluginAutoConfiguration 自动扫描，需要加 @AutoConfiguration 注解


// ========== 方式一示例（不加 @AutoConfiguration）==========

// @EnableConfigurationProperties(XxxProperties.class)  // 可选：有自定义配置属性时使用
// @PropertySource("classpath:application-xxx.properties")  // 可选：有插件自有配置文件时使用
@ComponentScan({"com.iteaj.iboot.plugin.xxx"})
@MapperScan({"com.iteaj.iboot.plugin.xxx.mapper"})  // 有数据库操作时加
/**
 * Xxx插件自动配置类
 */
public class XxxAutoConfiguration {

}


// ========== 方式二示例（放 autoconfig 包下，加 @AutoConfiguration）==========

import org.springframework.boot.autoconfigure.AutoConfiguration;

// @EnableConfigurationProperties(XxxProperties.class)  // 可选
// @PropertySource("classpath:application-xxx.properties")  // 可选
@AutoConfiguration
@ComponentScan({"com.iteaj.iboot.plugin.xxx"})
@MapperScan({"com.iteaj.iboot.plugin.xxx.mapper"})  // 有数据库操作时加
/**
 * Xxx插件自动配置类
 */
public class XxxAutoConfiguration {

    @Override
    public String getPluginName() {
        return "Xxx插件";
    }

    @Override
    public String getPluginDesc() {
        return "插件功能描述";
    }
}
