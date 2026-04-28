# CRUD 后端功能速查

> 开发后端 CRUD 接口时，优先查阅本文档。详细模板见各 Template 文件。

---

## 一、标准接口

### 管理端接口（路径前缀 `/模块/资源`）

| 操作 | 路径 | Method | 参数 | 返回类型 |
|------|------|--------|------|----------|
| 分页查询 | `/view` | GET | Page + EntityDto | `Result<IPage<Entity>>` |
| 列表查询 | `/list` | GET | Entity | `Result<List<Entity>>` |
| 获取编辑记录 | `/edit` | GET | `id` | `Result<Entity>` |
| 获取详情(含关联) | `/detail` | GET | `id` | `Result<EntityDto>` |
| 新增或更新 | `/saveOrUpdate` | POST | `@RequestBody Entity` | `Result<Boolean>` |
| 删除 | `/del` | POST | `@RequestBody List<Long>` | `Result<Boolean>` |

### 移动端接口（路径前缀 `/m/资源`）

| 操作 | 路径 | Method | 参数 | 返回类型 |
|------|------|--------|------|----------|
| 分页查询 | `/page` | GET | Page + EntityDto | `Result<IPage<Entity>>` |
| 列表查询 | `/list` | GET | Entity | `Result<List<Entity>>` |
| 编辑记录 | `/edit` | GET | `id` | `Result<Entity>` |
| 详情(含关联) | `/detail` | GET | `id` | `Result<EntityDto>` |

> **POST 接口统一使用 `@RequestBody` 接收 JSON**，禁止 `@RequestParam`。

---

## 二、代码分层

| 层 | 类名 | 继承/实现 | 关键注解 |
|----|------|----------|----------|
| Entity | `Xxx` | `extends BaseEntity` | `@TableName` `@Getter` `@Setter` `@Accessors(chain=true)` |
| 树形Entity | `Xxx` | `extends TreeEntity` | 同上 + 实现 `getPid()` |
| DTO | `XxxDto` | `extends Xxx` | `@Getter` `@Setter` `@Accessors(chain=true)` |
| Mapper接口 | `XxxMapper` | `extends BaseMapper<Xxx>` | `@Mapper` |
| Service | `IXxxService` | `extends IBaseService<Xxx>` | 仅定义自定义方法 |
| ServiceImpl | `XxxServiceImpl` | `extends BaseServiceImpl<XxxMapper, Xxx> implements IXxxService` | `@Service` |
| 管理端Controller | `XxxController` | `extends BaseController` | `@RestController` `@RequestMapping("/模块/资源")` |
| 移动端Controller | `AppXxxController` | `extends BaseController` | `@RestController` `@RequestMapping("/m/资源")` |

> 模板：[EntityTemplate](./EntityTemplate.java) [EntityDtoTemplate](./EntityDtoTemplate.java) [MapperTemplate](./MapperTemplate.java) [ServiceTemplate](./ServiceTemplate.java) [ServiceImplTemplate](./ServiceImplTemplate.java) [ControllerTemplate](./ControllerTemplate.java) [AppControllerTemplate](./AppControllerTemplate.java)

---

## 三、框架基类核心

### BaseEntity 内置字段
```java
@TableId(type = IdType.AUTO) private Long id;  // 自增主键
@TableField(exist = false) private Object[] section;  // 非数据库字段
```

### TreeEntity 额外字段
```java
private Long pid;  // 父节点ID（需实现 getPid()）
@TableField(exist = false) private Collection children;  // 子节点列表
```

### IBaseService 内置方法（Service层无需重复声明）

| 方法 | 返回类型 | 说明 |
|------|---------|------|
| `save(E)` | `BooleanResult` | 新增 |
| `saveBatch(Collection<E>)` | `BooleanResult` | 批量新增 |
| `saveOrUpdate(E)` | `BooleanResult` | 新增或更新(按id判断) |
| `updateById(E)` | `BooleanResult` | 按id更新 |
| `removeById(Serializable)` | `BooleanResult` | 按id删除 |
| `removeByIds(Collection)` | `BooleanResult` | 按id批量删除 |
| `remove(Wrapper)` | `BooleanResult` | 条件删除 |
| `getById(Serializable)` | `DetailResult<E>` | 按id查询 |
| `getOne(Wrapper)` | `DetailResult<E>` | 条件查单条 |
| `list(E)` | `ListResult<E>` | 条件查询列表 |
| `list()` | `ListResult<E>` | 查询全部 |
| `listByIds(Collection)` | `ListResult<E>` | 按id批量查询 |
| `page(IPage, E)` | `PageResult<IPage<E>>` | 条件分页 |
| `page(IPage)` | `PageResult<IPage<E>>` | 无条件分页 |
| `count()` | `NumberResult<Long>` | 计数 |
| `count(Wrapper)` | `NumberResult<Long>` | 条件计数 |

### Controller 返回方法
```java
success(data)         // 成功(200)
success()             // 成功无数据
success(data, msg)    // 成功+消息
fail(msg)             // 失败(500)
fail(data, msg)       // 失败+数据
statusCode(data, msg, code)  // 自定义状态码
```

### Result 静态方法
```java
Result.success(data)
Result.success(msg)
Result.success(data, msg)
Result.fail(msg)
Result.fail(data, msg)
```

---

## 四、Entity 注解

### 必需注解
```java
@Getter @Setter @Accessors(chain = true)
@TableName("table_name")
```

### @TableField 用法

| 用法 | 代码 | 说明 |
|------|------|------|
| 映射列名 | `@TableField("column_name")` | 默认驼峰转下划线，不一致时显式指定 |
| 模糊搜索 | `@TableField(value = "name", condition = SqlCondition.LIKE)` | 字符串字段模糊查询 |
| 非数据库字段 | `@TableField(exist = false)` | DTO关联字段、计算字段等 |
| JSON类型 | `@TableField(typeHandler = FastjsonTypeHandler.class)` | JSON数组/对象存储 |
| 更新策略-忽略空 | `@TableField(updateStrategy = FieldStrategy.IGNORED)` | 更新时字段为null也更新 |
| 更新策略-不更新 | `@TableField(updateStrategy = FieldStrategy.NEVER)` | 更新时忽略此字段 |
| 查询策略-忽略空 | `@TableField(whereStrategy = FieldStrategy.IGNORED)` | 查询时字段为null也作为条件 |

> **SqlCondition 必须从 `com.baomidou.mybatisplus.annotation.SqlCondition` 导入**，不要从其他包导入。

---

## 五、Controller 注解

```java
@Logger("操作描述")                         // 操作日志，每个接口必须加
@CheckPermission("模块:资源:操作")           // 权限校验
@CheckPermission(value={"a:add","a:edit"}, logical=Logical.OR)  // 多权限OR
@GetMapping / @PostMapping                   // 请求方法
@RequestBody                                 // JSON请求体（POST必须）
@RequestParam                                // URL参数（仅GET）
```

### 权限编码格式
`{模块}:{资源}:{操作}`，如 `iot:device:view`

| 操作 | 编码后缀 | saveOrUpdate合并编码 |
|------|---------|---------------------|
| 查看 | `:view` | — |
| 新增 | `:add` | `value={"x:add","x:edit"}, logical=Logical.OR` |
| 编辑 | `:edit` | 同上 |
| 删除 | `:del` | — |

---

## 六、Mapper XML

### 查询条件模板
```xml
<where>
    <if test="entity.name != null and entity.name != ''">
        AND name = #{entity.name}
    </if>
    <if test="entity.startTime != null">
        AND create_time &gt;= #{entity.startTime}
    </if>
    <if test="entity.endTime != null">
        AND create_time &lt;= #{entity.endTime}
    </if>
</where>
ORDER BY create_time DESC
```

### 表关联(仅 detail/pageOfDetail)

| 类型 | XML标签 | columnPrefix | SQL别名 |
|------|---------|-------------|--------|
| 一对一 | `<association property="user" columnPrefix="u_" javaType="User">` | `u_` | `u.id AS u_id` |
| 一对多 | `<collection property="devices" column="id" columnPrefix="d_" ofType="Device">` | `d_` | `d.id AS d_id` |

> view/list/edit **不填充关联**，仅 detail 填充。一对多在分页时数据重复需特殊处理。

### 非表关联字段（平铺方式，项目实际采用）
```xml
<!-- 在DtoResultMap中直接映射关联表的字段 -->
<result property="productName" column="p_name"/>
```

---

## 七、DTO 设计

DTO 继承 Entity，扩展两类字段：
1. **搜索条件**：`startTime`/`endTime` 等非数据库字段
2. **关联数据**：一对一(实体)、一对多(列表)

---

## 八、业务模块

项目: `bootstrap`，基础包: `com.iteaj.iboot.module`

| 模块 | 子包 | API前缀 | 说明 |
|------|------|---------|------|
| 核心 | core | /core | 系统管理 |
| 物联网 | iot | /iot | 设备管理 |
| 移动端 | app | /m | 移动端接口 |

移动端代码生成位置：独立插件→插件内`app/controller/`，主项目→`module/app/controller/`

---

## 九、关键规范

- **构造器注入**：禁止 `@Autowired` 字段注入，使用构造器注入
- **POST请求体**：所有 POST 接口统一 `@RequestBody`
- **Service去重**：仅声明自定义方法，不重复 IBaseService 已有方法
- **XML防全表**：`<where><if>` 必须判空
- **模糊搜索**：Entity 用 `SqlCondition.LIKE`，XML 用 `like '%${entity.name}%'`
- **Javadoc**：所有类和接口方法必须加注释
- **saveOrUpdate权限**：使用 `Logical.OR` 合并 add+edit 权限
