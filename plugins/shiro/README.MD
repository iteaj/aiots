  shiro一般用于后台管理端的用户认证和权限管理, 但其实它是一款和web容器无关的权限框架, 换句话说它也可以用于oauth2和移动端(原生,web,小程序,公众号)等授权认证. 所以此插件除了实现后台的授权认证功能之外,也集成了移动端的原生,web,oauth2客户端等认证的实现
#### 如何启用app端认证

```
// 1. 在启动类上导入配置类: FrameworkAppAutoConfiguration
@ImportAutoConfiguration({
        FrameworkAppAutoConfiguration.class,
        FrameworkWebAutoConfiguration.class
})
@SpringBootApplication
public class BootstrapBootApplication implements WebMvcConfigurer {
    public static void main(String[] args) {
        SpringApplication.run(BootstrapBootApplication.class, args);
    }
}
// 2. 在maven配置文件里面导入shiro插件模块
<dependency><!--  shiro权限管理  -->
    <groupId>com.iteaj</groupId>
    <artifactId>shiro</artifactId>
    <version>${project.version}</version>
</dependency>
```
