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

5. **生成sql文件**
   - 生成sql文件: 包括创建表sql和需要的菜单sql
   - 将sql写到docs/sql/patch.sql文件中, 标上日期，方便后续回滚

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

生成CRUD代码时必须严格按照以下模板：

| 层 | 模板文件 |
|----|----------|
| Entity | [EntityTemplate.java](./EntityTemplate.java) |
| DTO | [EntityDtoTemplate.java](./EntityDtoTemplate.java) |
| Mapper | [MapperTemplate.java](./MapperTemplate.java) + [MapperTemplate.xml](./MapperTemplate.xml) |
| Service | [ServiceTemplate.java](./ServiceTemplate.java) |
| ServiceImpl | [ServiceImplTemplate.java](./ServiceImplTemplate.java) |
| 管理端Controller | [ControllerTemplate.java](./ControllerTemplate.java) |
| 移动端Controller | [AppControllerTemplate.java](./AppControllerTemplate.java)（开发移动端时生成） |

## 接口规范

> 接口路径、代码分层、注解、权限编码等速查内容见 [crud-quick-ref.md](./crud-quick-ref.md)

## 表关联处理规范

> 关联类型速查见 [crud-quick-ref.md](./crud-quick-ref.md)，以下补充详细规范：

- **DTO定义**：详见 [EntityDtoTemplate.java](./EntityDtoTemplate.java)
  - 一对一关联：添加实体类字段（如：`private User user;`）
  - 一对多关联：添加实体列表字段（如：`private List<Device> devices;`）

- **XML实现**：详见 [MapperTemplate.xml](./MapperTemplate.xml)
  - 统一使用columnPrefix方式简化映射

- **性能注意**：
  - 避免N+1查询问题（列表查询不使用关联）
  - 一对多关联在分页时会导致数据重复，需特殊处理
  - 使用批量查询或JOIN优化性能

- **命名规范**：
  - 一对一：使用单数形式（如：`user`、`product`）
  - 一对多：使用复数形式（如：`users`、`devices`）

---

## 代码生成检查清单

生成代码前必须逐项检查，以下规则与模板同等约束力，违反即需修正：

- [ ] 已读取并理解对应的模板文件（Entity/Mapper/Service/Controller等8个）
- [ ] **继承关系**与模板一致（Entity → BaseEntity，ServiceImpl → ServiceImpl<Mapper, Entity>）
- [ ] **注解完整**（@TableName/@Getter/@Setter/@Service/@RestController等模板注解不可遗漏）
- [ ] **标准方法无遗漏**（view/list/edit/detail/saveOrUpdate/del）
- [ ] **返回值类型正确**（ListResult/PageResult/DetailResult，与模板一致）
- [ ] **接口路径规范**（/view /list /saveOrUpdate /edit /detail /del）
- [ ] **权限编码正确**（格式：模块:资源:操作）
- [ ] **构造器注入**（禁止 @Autowired 字段注入）
- [ ] **POST接口请求体**（所有 POST 接口统一使用 `@RequestBody` 接收 JSON 格式参数）
- [ ] **Service接口去重**（仅定义自定义方法，禁止重复IBaseService已有方法）
- [ ] **Mapper.xml空条件判断**（`<where><if>` 防止全表查询）
- [ ] **Javadoc注释**（所有类和方法）

