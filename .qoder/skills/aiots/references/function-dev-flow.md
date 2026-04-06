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
- 一个功能既需要管理端也需要移动端 要求如下：
  - 在对应的业务模块下面生成CRUD代码
  - 在移动端模块下生成移动端的控制层代码(AppControllerTemplate.java)
  - 移动端后端接口的包路径: com.iteaj.iboot.module.app

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