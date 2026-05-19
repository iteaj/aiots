// ===== 插件自动配置类示例 =====
//
// 注册方式一（推荐）：自动配置类直接放在插件包根目录下，
//   在 META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports 中声明全限定名
//   此方式不需要加 @AutoConfiguration 注解
//
// 注册方式二：将自动配置类放在 com.iteaj.iboot.plugin.*.autoconfig 包下，
//   由框架 PluginAutoConfiguration 自动扫描，需要加 @AutoConfiguration 注解
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.MapperScan;

// ========== 方式一示例（不加 @AutoConfiguration）==========
@ComponentScan({"com.iteaj.iboot.plugin.xxx"})
@MapperScan({"com.iteaj.iboot.plugin.xxx.mapper"})  // 有数据库操作时加
/**
 * Xxx插件自动配置类
 */
public class XxxAutoConfiguration {

}


// ========== 方式二示例（放 autoconfig 包下，加 @AutoConfiguration）==========

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.MapperScan;

@AutoConfiguration
@ComponentScan({"com.iteaj.iboot.plugin.xxx"})
@MapperScan({"com.iteaj.iboot.plugin.xxx.mapper"})  // 有数据库操作时加
/**
 * Xxx插件自动配置类
 */
public class XxxAutoConfiguration {

}
