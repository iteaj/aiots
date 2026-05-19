# 插件开发功能速查

> 开发可插拔功能模块时，遇到不确定的规范、结构、配置等问题，优先查阅本文档。

---

## 插件创建必备文件

| 文件 | 说明 | 必须 |
|------|------|------|
| `pom.xml` | 子模块 Maven 配置，parent 指向父模块 | 是 |
| `XxxAutoConfiguration.java` | 自动配置类 | 是 |
| `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` | 自动配置注册（推荐方式） | 推荐 |

---

## 自动配置类加载方式

| 方式 | 说明 | 推荐 |
|------|------|------|
| `AutoConfiguration.imports` 文件 | 内容为全限定类名，类放包根目录 | ✅ |
| `autoconfig` 子包 | 由框架 `PluginAutoConfiguration` 自动扫描 | ❌ 可能有加载顺序问题 |

---

## 插件目录结构

```
iboot-plugin/iboot-xxx/
├── pom.xml
├── src/main/java/com/iteaj/iboot/plugin/xxx/
│   ├── XxxAutoConfiguration.java   # 自动配置类
│   ├── controller/                  # 管理端Controller
│   ├── entity/                      # 实体类
│   ├── dto/                         # DTO
│   ├── mapper/                      # Mapper接口
│   ├── service/                     # Service接口和实现
│   └── app/controller/             # 移动端Controller（可选）
├── src/main/resources/
│   ├── META-INF/spring/
│   │   └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
│   └── mapper/                      # Mapper XML
└── src/test/
```

---

## 常见问题

**Q: 插件如何被主项目扫描到？**
A: 通过 `AutoConfiguration.imports` 文件注册自动配置类，配置类上用 `@ComponentScan` 和 `@MapperScan`（有数据库时）指定扫描包路径。

**Q: 插件的 REST 前缀怎么定？**
A: 在 Controller 的 `@RequestMapping` 中指定，如 AI 插件用 `/ai`，格式为 `/ai/子模块/操作`。

**Q: 父模块 pom.xml 需要改什么？**
A: 在父模块的 `<modules>` 中加入新子模块的 artifactId。
