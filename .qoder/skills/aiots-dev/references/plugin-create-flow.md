# 插件创建流程与开发规范

---

## 插件创建流程
### 步骤一：确认基本信息

- 插件名称（对应 Maven artifactId，如 `ai-vision`）
- 插件功能描述（一句话说明用途）
- 插件所属父模块（如 `iboot-ai`、`iboot-plugin` 直属等）

### 步骤二：创建必需文件

无论是否有 CRUD，以下文件必须创建：

| 文件 | 说明 |
|------|------|
| `pom.xml` | 子模块 Maven 配置，parent 指向父模块 |
| `XxxAutoConfiguration.java` | 自动配置类，包含 `@ComponentScan` / `@MapperScan`（有数据库时） |

**自动配置类模板** [AutoConfigurationTemplate.java](AutoConfigurationTemplate.java)

自动配置类的加载方式二选一：

- **方式一（推荐）**：在 `src/main/resources/META-INF/spring/` 下创建文件 `org.springframework.boot.autoconfigure.AutoConfiguration.imports`，内容为自动配置类全限定名。此方式自动配置类直接放在插件包根目录下，**不需要** `autoconfig/` 子包
- **方式二**：将自动配置类放在 `com.iteaj.iboot.plugin.*.autoconfig` 包下，由框架 `PluginAutoConfiguration` 自动扫描加载（可能有 Bean 加载顺序问题）
并在父模块 `pom.xml` 的 `<modules>` 中注册新子模块。

### 步骤三:开发CRUD功能(有数据库操作时)

**如插件需要数据库操作和标准增删改查功能,严格按照 [CRUD开发流程](./function-dev-flow.md) 执行:**

1. 数据库表设计 [需确认]
2. 确认功能所属模块
3. 预览代码 [需确认]
4. 按CRUD模板生成代码(Entity、Mapper、Service、Controller等8个模板)

**如插件无需数据库操作:**
- 跳过此步骤
- 直接编写业务逻辑代码(Service、Controller等)
- 无需遵循CRUD模板

**详细流程参见:** [function-dev-flow.md](./function-dev-flow.md)

### 步骤四:开发移动端接口(可选)

**如插件需要支持移动端,推荐将移动端代码放在插件内,实现模块化管理:**

**包路径规范:**
- **管理端** - `com.iteaj.iboot.plugin.xxx.controller`
- **移动端** - `com.iteaj.iboot.plugin.xxx.app.controller`
- **移动端DTO** - `com.iteaj.iboot.plugin.xxx.app.dto`

**API路径规范:**
- **管理端** - `/xxx` (如 `/ai/vision`)
- **移动端** - `/m/xxx` (如 `/m/vision`)

**目录结构:**
```
iboot-plugin/iboot-xxx/
├── entity/                          # 实体类(共用)
├── mapper/                          # Mapper(共用)
├── service/                         # Service(共用)
├── controller/                      # 管理端Controller
├── app/                             # 移动端代码
│   ├── controller/                  # 移动端Controller
│   └── dto/                         # 移动端DTO
└── XxxAutoConfiguration.java
```

**依赖关系:**
- 移动端Controller和管理端Controller共用同一个Service层
- 通过构造器注入插件的Service
- 移动端可使用专门的DTO进行数据转换

**移动端Controller模板:** [AppControllerTemplate.java](./AppControllerTemplate.java)

**移动端开发详细流程参见:** [CRUD开发流程 - app后端模块代码生成策略](./function-dev-flow.md#app后端模块代码生成策略)

---

## 开发规范

### 包名规范

插件包名格式：`com.iteaj.iboot.plugin.{插件英文简写}.{子模块英文简写}(可选)`

| 插件目录 | 包名 |
|----------|------|
| `iboot-amqp/amqp-rabbitmq` | `com.iteaj.iboot.plugin.amqp.rabbit` |
| `iboot-datasource` | `com.iteaj.iboot.plugin.ds` |
| `iboot-protocol/protocol-mqtt` | `com.iteaj.iboot.plugin.protocol.mqtt` |
| `iboot-edge/edge-server` | `com.iteaj.iboot.plugin.edge.server` |

### 包结构规范

**使用 imports 方式（推荐）**：自动配置类直接放在插件包根目录

```
com.iteaj.iboot.plugin.{插件简写}/
├── XxxAutoConfiguration.java      # 自动配置类（直接在根包）
├── controller/                    # 管理端控制层（CRUD流程生成）
├── app/                           # 移动端代码（可选）
│   ├── controller/                # 移动端控制层
│   └── dto/                       # 移动端DTO
├── service/                       # 服务层（CRUD流程生成）
│   └── impl/
├── mapper/                        # 数据访问层（CRUD流程生成）
├── entity/                        # 实体类（CRUD流程生成）
└── dto/                           # 数据传输对象（CRUD流程生成）
```

**使用 autoconfig 包方式**：自动配置类放在 `autoconfig/` 子包

```
com.iteaj.iboot.plugin.{插件简写}/
├── autoconfig/                
│   └── XxxAutoConfiguration.java
├── controller/
├── app/
│   ├── controller/
│   └── dto/
├── service/
│   └── impl/
├── mapper/
├── entity/
└── dto/
```

### 自动配置类规范

- 使用 `@ComponentScan` 扫描插件包
- 使用 `@MapperScan` 扫描 Mapper（如有数据库操作）
- **imports 方式**：不需要加 `@AutoConfiguration` 注解
- **autoconfig 包方式**：必须加 `@AutoConfiguration` 注解

### CRUD开发规范

**适用场景:** 插件需要数据库操作和标准增删改查功能时

插件中的CRUD开发**必须严格遵循** [CRUD开发流程](./function-dev-flow.md)，包括:

- 数据库表设计确认
- 模块归属确认
- 代码预览确认
- 8个标准模板生成（Entity、Dto、Mapper、XML、Service、ServiceImpl、Controller、AppController）
- 接口路径规范（/view、/list、/saveOrUpdate、/edit、/detail、/del）
- 权限编码规范（模块:资源:操作）
- 构造器注入、Result返回等编码规范

**开发CRUD功能时，直接跳转执行:** [CRUD开发流程](./function-dev-flow.md)

