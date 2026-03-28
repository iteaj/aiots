# 插件创建流程与开发规范

创建新插件前，**必须先向用户逐一确认以下事项，再开始写任何代码**。

> 没有具体的功能描述时，不生成任何代码。

---

## 步骤一：确认基本信息

- 插件名称（对应 Maven artifactId，如 `ai-vision`）
- 插件功能描述（一句话说明用途）
- 插件所属父模块（如 `iboot-ai`、`iboot-plugin` 直属等）

## 步骤二：创建必需文件

无论是否有 CRUD，以下文件必须创建：

| 文件 | 说明 |
|------|------|
| `pom.xml` | 子模块 Maven 配置，parent 指向父模块 |
| `XxxAutoConfiguration.java` | 自动配置类，包含 `@ComponentScan` / `@MapperScan`（有数据库时） |

自动配置类的加载方式二选一：

- **方式一（推荐）**：在 `src/main/resources/META-INF/spring/` 下创建文件 `org.springframework.boot.autoconfigure.AutoConfiguration.imports`，内容为自动配置类全限定名。此方式自动配置类直接放在插件包根目录下，**不需要** `autoconfig/` 子包
- **方式二**：将自动配置类放在 `com.iteaj.iboot.plugin.*.autoconfig` 包下，由框架 `PluginAutoConfiguration` 自动扫描加载（可能有 Bean 加载顺序问题）

并在父模块 `pom.xml` 的 `<modules>` 中注册新子模块。

## 步骤三：确认是否需要生成 CRUD

询问用户：**是否需要生成 CRUD 相关代码？**

- **需要** → 进一步询问实体名、表字段等信息，再按以下规范创建对应包目录：
  - `entity/` — `XxxEntity`（继承 `BaseEntity` 或 `TreeEntity`）
  - `mapper/` — `XxxMapper`（继承 `BaseMapper<T>`，加 `@Mapper`）
  - `service/` — `IXxxService`（继承 `IBaseService<T>`）
  - `service/impl/` — `XxxServiceImpl`（继承 `BaseServiceImpl<M, T>`，加 `@Service`）
  - `controller/` — `XxxController`（继承 `BaseController`，加 `@RestController`）
- **不需要** → 跳过以上所有代码，仅创建必需文件

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
├── XxxAutoConfiguration.java  # 自动配置类（直接在根包）
├── controller/     # 控制器层
├── service/        # 服务接口
│   └── impl/       # 服务实现
├── entity/         # 实体类
├── mapper/         # MyBatis Mapper
├── dto/            # 数据传输对象
├── enums/          # 枚举类
└── consts/         # 常量类
```

**使用 autoconfig 包方式**：自动配置类放在 `autoconfig/` 子包

```
com.iteaj.iboot.plugin.{插件简写}/
├── autoconfig/     # 自动配置类
├── controller/     # 控制器层
├── service/        # 服务接口
│   └── impl/       # 服务实现
├── entity/         # 实体类
├── mapper/         # MyBatis Mapper
├── dto/            # 数据传输对象
├── enums/          # 枚举类
└── consts/         # 常量类
```

### 自动配置类规范

- 使用 `@ComponentScan` 扫描插件包
- 使用 `@MapperScan` 扫描 Mapper（如有数据库操作）
- 使用 `@EnableConfigurationProperties` 启用配置属性类（可选）
- 使用 `@PropertySource` 加载插件自有配置文件（可选）
- **imports 方式**：不需要加 `@AutoConfiguration` 注解
- **autoconfig 包方式**：必须加 `@AutoConfiguration` 注解

完整示例见 [AutoConfigurationExample.java](AutoConfigurationExample.java)

### 实体类规范

- **普通实体** 继承 `BaseEntity`
- **树形实体** 继承 `TreeEntity` 并实现 `getPid()` 抽象方法
- 必须加 `@Getter`、`@Setter`、`@Accessors(chain = true)`、`@TableName("表名")`
- 类必须添加 Javadoc 注释（`/**  */`）说明实体用途
- 每个属性必须添加注释说明字段用途
- 非数据库字段标注 `@TableField(exist = false)`

完整示例见 [EntityExample.java](EntityExample.java)

### Service 层规范

- **接口**：继承 `IBaseService<T>`，示例见 [ServiceExample.java](ServiceExample.java)
- **实现类**：继承 `BaseServiceImpl<M, T>`，加 `@Service`，示例见 [ServiceImplExample.java](ServiceImplExample.java)
- 接口、实现类必须添加类级 Javadoc 注释（`/**  */`）
- 自定义方法必须添加 Javadoc 注释及 `@param` / `@return` 说明

`IBaseService` 已内置常用 CRUD，返回值均为 `Result` 子类：

| 操作类型 | 返回类型 |
|----------|----------|
| 增删改 | `BooleanResult` |
| 单条查询 | `DetailResult<T>` |
| 列表查询 | `ListResult<T>` |
| 分页查询 | `PageResult<IPage<T>>` |

Mapper 示例见 [MapperExample.java](MapperExample.java)

### Controller 层规范

- 继承 `BaseController`，返回类型统一为 `Result<T>`
- 直接返回 Service 的 `Result` 对象，禁止再次包装
- 使用 `@Logger("操作描述")` 记录操作日志
- 使用 `@CheckPermission("模块:资源:操作")` 进行权限控制
- 依赖注入使用构造器注入
- 类必须添加 Javadoc 注释说明模块功能
- 每个接口方法必须添加 Javadoc 注释（含 `@param` 说明）
- 接口路径：列表 `/view`，新增 `/add`，修改 `/edit`，删除 `/del`（`@PostMapping`，请求体 `List<Long> ids`）

完整示例见 [ControllerExample.java](ControllerExample.java)

### 权限编码规范

格式：`模块:资源:操作`，例如：
- `core:admin:view` — 用户查看
- `core:admin:add` — 用户新增
- `core:admin:edit` — 用户编辑
- `core:admin:del` — 用户删除
