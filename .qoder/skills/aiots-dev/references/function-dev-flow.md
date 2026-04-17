# CRUD后端接口开发流程

## CRUD接口开发流程
1. **数据库设计**
   - 使用表格列出功能需要的表结构（字段、类型、约束条件、索引等）[需确认]

2. **模块确认**
   - 确认功能所属模块，参考: 业务模块和对应的接口列表
   - 列出功能所属模块信息（模块名称、模块编码、模块描述、子包路径、rest api前缀、移动端子包路径)[需确认]

3. **预览代码(生成代码前)**
   - 在代码文件生成前咨询用户是否需要预览代码[需确认]

4. **根据模板生成代码**
   - 生成代码: 严格按模板[CRUD模板](#crud代码模板)

## 业务模块和对应的接口列表
- 项目: bootstrap
- 模块包路径: com.iteaj.iboot.module
- **app模块** 是开发端模块不属于业务模块
| 模块名称 | 子包路径/标识 | 模块类型 | rest api前缀 | 模块描述 | 模块功能明细文档 |
|---|---|---|---|---|---|
| 核心功能模块       | core | 业务 | /core | 系统管理功能接口   | [接口列表明细](./core-module-reference.md) |
| 物联网功能模块     | iot  | 业务 | /iot  | 物联网相关功能接口 | [接口列表明细](./iot-module-reference.md) |
| 移动端后端功能模块 | app  | 终端 | /m    | 移动端功能接口     | [接口列表明细](./app-module-reference.md) |

## app后端模块代码生成策略

**当功能需要移动端时，根据开发类型自动选择生成位置:**

### 自动判断规则

| 开发类型 | 生成位置 | 包路径 | 说明 |
|---------|---------|--------|------|
| **独立插件模块** | 插件项目内 | `com.iteaj.iboot.plugin.{插件简写}.app.controller` | 插件独立管理 |
| **主项目功能模块** | 启动项目module/app | `com.iteaj.iboot.module.app.controller` | 非插件模块 |

### 判断依据

**属于独立插件模块:**
- 在 `iboot-plugin/` 目录下开发
- 有独立的 `pom.xml` 和自动配置类
- 遵循插件开发规范
- 例如：`iboot-plugin/iboot-ai/ai-vision/`

**属于主项目功能模块:**
- 在 `bootstrap/` 目录下开发
- 直接使用主项目的模块结构
- 不创建独立的插件模块
- 例如：`bootstrap/src/main/java/com/iteaj/iboot/module/core/`

---

### 策略一：生成到插件项目内（独立插件开发模式）

**适用场景:** 开发独立插件，需要模块化管理

**包路径:** `com.iteaj.iboot.plugin.{插件简写}.app.controller`

**目录结构:**
```
iboot-plugin/iboot-xxx/
├── controller/                      # 管理端Controller
│   └── XxxController.java          # /xxx/view, /xxx/list
└── app/
    └── controller/                  # 移动端Controller
        └── AppXxxController.java   # /m/xxx/*
```

**API前缀:** `/m/xxx` (如 `/m/vision`)

**优势:**
- 模块独立，插件包含完整功能
- 便于插件单独开发、测试、维护
- 移动端和管理端共用Service层

### 策略二：生成到启动项目的module/app下（主项目方式）

**适用场景:** 在主项目中直接开发功能，不创建独立插件

**包路径:** `com.iteaj.iboot.module.app.controller`

**目录结构:**
```
bootstrap/src/main/java/
└── com/iteaj/iboot/module/app/
    └── controller/
        └── AppXxxController.java   # /m/xxx/*
```

**API前缀:** `/m/xxx` (如 `/m/vision`)

**优势:**
- 所有移动端接口集中管理
- 便于跨模块功能整合
- 移动端统一版本控制

### 生成时确认项

开发移动端前需确认:
1. **开发类型** - 独立插件模块 或 主项目功能模块 [需确认]
2. **生成位置** - 根据开发类型自动确定 [自动]
3. **包路径** - 根据生成类型确定完整包路径 [自动]

## CRUD代码模板
- 实体类模板: [EntityTemplate.java](./EntityTemplate.java)
- 实体类Dto模板: [EntityDtoTemplate.java](./EntityDtoTemplate.java)
- Mapper接口类模板: [MapperTemplate.java](./MapperTemplate.java)
- xml模板: [MapperTemplate.xml](./MapperTemplate.xml)
- Service接口类模板: [ServiceTemplate.java](./ServiceTemplate.java)
- ServiceImpl实现类模板: [ServiceImplTemplate.java](./ServiceImplTemplate.java)
- 管理端Controller类模板: [ControllerTemplate.java](./ControllerTemplate.java)
- 移动端前端Controller类模板(开发移动端时生成): [AppControllerTemplate.java](AppControllerTemplate.java)

## 接口规范
1. CRUD模板文件强制遵循 [重要]
- 生成的 CRUD 代码必须严格按照 CRUD 模板文件来生成
- 模板文件位置：`references\`
  - EntityTemplate.java
  - EntityDtoTemplate.java
  - MapperTemplate.java
  - MapperTemplate.xml
  - ServiceTemplate.java
  - ServiceImplTemplate.java
  - ControllerTemplate.java
  - AppControllerTemplate.java

2. **接口路径规范**
   - 分页查询：`/view`（GET）
   - 列表查询：`/list`（GET）
   - 新增和修改：`/saveOrUpdate`（POST）
   - 获取记录详情：`/edit`（GET）
   - 获取记录详细(包括关联的数据)：`/detail`（GET）
   - 删除：`/del`（POST，请求体 `List<Long> ids`）

3. **权限编码规范**
   - 格式：`模块:资源:操作`
   - 示例：`iot:device:view`、`core:user:add`

## 注意事项

1. 所有类和方法必须添加 Javadoc 注释
2. 使用构造器注入，避免字段注入
3. 返回类型统一使用 `Result` 封装
4. 遵循统一的命名规范和代码风格
5. 添加适当的日志记录和权限控制
6. **表关联处理规范** [重要]
   - **DTO定义**：详见 [EntityDtoTemplate.java](./EntityDtoTemplate.java)
     - 一对一关联：添加实体类字段（如：`private User user;`）
     - 一对多关联：添加实体列表字段（如：`private List<Device> devices;`）
   
   - **XML实现**：详见 [MapperTemplate.xml](./MapperTemplate.xml)
     - 统一使用columnPrefix方式简化映射
     - 一对一：`<association>` + `columnPrefix`
     - 一对多：`<collection>` + `column` + `columnPrefix`
   
   - **填充时机**：
     - 列表查询（view/list）：不填充关联数据
     - 详情查询（detail）：填充一对一和一对多关联数据
     - 编辑查询（edit）：仅填充基本信息，不填充关联数据
   
   - **性能注意**：
     - 避免N+1查询问题（列表查询不使用关联）
     - 一对多关联在分页时会导致数据重复，需特殊处理
     - 使用批量查询或JOIN优化性能
   
   - **命名规范**：
     - 一对一：使用单数形式（如：`user`、`product`）
     - 一对多：使用复数形式（如：`users`、`devices`）

