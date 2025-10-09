# 升级jdk17以及boot3.1.12的一些主要改动

## 为什么是jdk17+boot3.1.12

  **因为iot插件也是这个版本**

## Openrewrite 辅助

**OpenRewrite 是一个源码重构工具，可以自动修改源码以适配特定的框架或版本。**

**在本项目中作用**：

1. 检查 Spring Boot 3.1 不兼容的 API。
2. 提供可选的 recipes 来自动修改代码（例如：升级 `@RequestMapping`、替换已废弃的方法）。
3. **不会自动修改源码**，源码只有在手动执行 recipes 时才会被修改。

**本项目中实际使用**：

- 仅作为辅助工具检测不兼容点。
- 没有直接执行任何 recipes，源码保持不变

**具体引用： 根目录pom.xml**

```xml
  <plugin>
    <groupId>org.openrewrite.maven</groupId>
    <artifactId>rewrite-maven-plugin</artifactId>
    <version>6.19.0</version>
    <configuration>
        <activeRecipes>
            <recipe>com.iteaj.migrate.SpringBoot3Migration</recipe>
        </activeRecipes>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>org.openrewrite.recipe</groupId>
            <artifactId>rewrite-spring</artifactId>
            <version>6.15.0</version>
        </dependency>
        <dependency>
            <groupId>org.openrewrite.recipe</groupId>
            <artifactId>rewrite-migrate-java</artifactId>
            <version>3.18.0</version>
        </dependency>
    </dependencies>
</plugin>
```

## 依赖版本升级

1. pom.xml（iboot）

```xml
 <iot.version>3.1.8</iot.version>   
<!-- 升级为3.2.0 ,iot插件jdk17版本也是3.2.0-->
 <iot.version>3.2.0</iot.version>
```

```xml
  <shiro.version>1.7.1</shiro.version>
  <shiro.version>2.0.5</shiro.version>
```

```xml
<mybatis.plus.version>3.4.1</mybatis.plus.version>
<mybatis.plus.version>3.5.14</mybatis.plus.version>
```

```xml
<!--插件依赖版本-->
<knife4j.version>4.3.0</knife4j.version>
<SaToken.version>1.35.0.RC</SaToken.version>

<!-- 插件版本 -->
<knife4j.version>4.5.0</knife4j.version>
<SaToken.version>1.37.0</SaToken.version>
```

```xml
<!--            <dependency>-->
<!--                <groupId>com.baomidou</groupId>-->
<!--                <artifactId>mybatis-plus-extension</artifactId>-->
<!--                <version>${mybatis.plus.version}</version>-->
<!--            </dependency>-->
<!-- 新增 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-jsqlparser</artifactId>
    <version>${mybatis.plus.version}</version>
</dependency>




<dependency>
    <groupId>com.baomidou</groupId>
    <version>${mybatis.plus.version}</version>
    <artifactId>mybatis-plus-boot-starter</artifactId>
</dependency>
<!-- 升级 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>${mybatis.plus.version}</version>
</dependency>
```



```xml
<dependency>
    <version>${knife4j.version}</version>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-aggregation-spring-boot-starter</artifactId>
</dependency>
<!--  替换  springboot3 的 -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>${knife4j.version}</version>
</dependency>



```

配置文件替换如下（application-knife4j.yml）：

```yml
knife4j:
  enable-aggregation: true
  disk:
    enable: true
    routes:
      - name: 创源物联网关
        order: 1
        debugUrl: http://127.0.0.1:8085
        location: classpath:/static/doc/openapi.json
        
        
       
       
knife4j:
  enable: true   # 启用knife4j
```



```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<!--  替换  -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.1.0</version>
</dependency>
```





2. pom.xml(bootstrap)

​        **注释掉代码生成插件，后面再修改，因为mp的升级导致代码生成逻辑要重写（差不多）**

3. pom.xml（framework）

​     最重要的也是最核心的    **javax → jakarta** 

​     Spring Boot 2.x 还使用 **`javax.servlet.\*`** 相关类。

​     Spring Boot 3.x 基于 Jakarta EE 10，所有包名 **迁移到 `jakarta.\*`**

所以加了这个方便升级也为了兼容：

```xml
<dependency>
    <groupId>jakarta.servlet</groupId>
    <artifactId>jakarta.servlet-api</artifactId>
    <version>6.0.0</version>
    <scope>provided</scope>
</dependency>
```

4.   application-mybatis.properties（framework）

   ```properties
   #mybatis-plus.mapper-locations=classpath*:/mapper/**/*.xml,classpath*:/com/iteaj/iboot/module/**/mapper/xml/*.xml
   
   ## 改为   因为MyBatis-Plus 3.5.14 对 mapper XML 的扫描更严格，Boot 3.x 的 Spring 6 解析方式略有不同 ，检测更严格， 不要开头多 /
   mybatis-plus.mapper-locations=classpath*:mapper/**/*.xml,classpath*:com/iteaj/iboot/module/**/mapper/xml/*.xml
   
   ```

   

5. FastjsonTypeHandler.java  （framework）

   ```java
   @MappedTypes({JSONObject.class, JSONArray.class})
   public class FastjsonTypeHandler extends AbstractJsonTypeHandler<JSON> {
   
       //  必须有这个构造方法
       public FastjsonTypeHandler(Class<?> type) {
           super(type);
       }
   
       @Override
       public JSON parse(String json) {
           return (JSON) JSON.parse(json);
       }
   
       @Override
       public String toJson(JSON obj) {
           return obj.toJSONString();
       }
   }
   ```