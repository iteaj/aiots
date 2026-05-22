---
name: aiots-dev
description: 全功能开发流程技能。当AI需要开发一个涉及多端(后端+管理端+移动端)的完整功能、梳理跨团队协作流程、确定API契约、或需要判断使用哪个专业开发技能时使用。
role: 项目流程协调者
---

# AIoTS全功能开发流程技能

作为完整功能开发流程的协调者，负责串联后端(aiots-dev)、管理端(admin)、移动端(app)、驱动(driver)等专业技能，确保一个功能从需求到交付的完整闭环。

---

## 核心定位

| 职责 | 说明 |
|------|------|
| **流程编排** | 当一个功能涉及多端时，安排开发顺序、协调API契约、管理依赖关系 |
| **技能路由** | 判断当前任务应使用哪个专业技能（后端/管理端/移动端/驱动），指引到对应技能 |
| **架构决策** | 涉及跨端交互、数据流向、接口设计时的方案选择和风险预警 |
| **交付验收** | 多端功能联调、检查清单执行、确保完整交付 |

---

## 项目概览

**三端协同**物联网平台：

| 端 | 目录 | 技能(Skill) | 核心职责 |
|---|------|------------|---------|
| **后端** | `bootstrap/` `framework/` `iboot-plugin/` | aiots-dev（后端） | 设备接入、数据采集、业务逻辑、API |
| **管理端** | `admin-ui/` | `admin` | 管理端配置、运维配置、监控分析 |
| **移动端** | `app/` | `app` + `cool ui` | 现场操作、数据查看、告警处理 |

---

## 技能路由指引

遇到开发任务时，先判断属于哪个专业领域，委派给对应技能：

| 任务类型 | 使用技能 | 说明 |
|---------|---------|------|
| Java后端开发（CRUD/IoT/Core/App模块） | `aiots-dev`（查阅后端参考文档） | Entity/DTO/Mapper/Service/Controller |
| 管理端页面开发 | `admin` | Vue 3 + Ant Design Vue + UView组件体系 |
| 移动端页面开发 | `app` + `cool ui` | uni-app x + UTS + Cool UI组件 |
| 设备驱动开发（PLC/Modbus/MQTT等） | `driver` | 实现DeviceProtocolSupplier SPI |
| 创建新插件模块 | `aiots-dev`（插件参考文档） | iboot-plugin下的完整插件流程 |
| 项目打包与安装包发布 | `deploy` | Maven构建 + jpackage安装包 |
| 跨多端的完整功能 | **当前技能(aiots-dev)** | 先协调流程，再拆分委派 |

---

## 多端功能开发流程

当一个功能涉及多端时，按以下顺序执行：

```
1. 需求分析 → 确定涉及哪些端（后端/管理端/移动端）
       ↓
2. 设计API契约 → 定义接口路径、请求/响应参数（后端先定义）
       ↓
3. 后端开发 → 委派给后端技能 → Entity → DTO → Mapper → Service → Controller
       ↓
4. 管理端开发 → 委派给 admin 技能 → 页面/组件/接口对接
       ↓
5. 移动端开发 → 委派给 app + cool ui 技能 → 页面/类型/接口对接
       ↓
6. 联调验收 → 执行检查清单、全端自测
```

### 关键原则

- **后端先于前端**：后端完成API后再进行前端开发，确保接口可用
- **API契约一致**：前端URL、参数、方法与后端Controller保持完全一致
- **单端任务直接委派**：如果只涉及一个端，直接使用对应专业技能，无需走完整流程
- **不确定必确认**：功能背景不清晰时先问用户，禁止猜测开发
- **知识优先**：遇到问题先查速查文档，其次流程文档，最后搜索源码

---

## 后端开发流程

后端开发分为几种典型类型，各类型有独立的流程文档和规范：

| 开发类型 | 流程文档 | 核心步骤 | 适用场景 |
|---------|---------|---------|---------|
| **CRUD开发** | [function-dev-flow.md](./references/function-dev-flow.md) | Entity → DTO → Mapper → Service → Controller | 标准业务增删改查功能 |
| **插件开发** | [plugin-create-flow.md](./references/plugin-create-flow.md) | 创建模块 → 自动配置类 → SPI注册 → Maven装配 | 新功能模块、第三方集成 |
| **驱动开发** | 参见 `driver` 技能 | 选择模板 → 协议实现 → 网络配置SPI | 设备接入（委派给driver技能） |
| **移动端后端** | [app-module-quick-ref.md](./references/app-module-quick-ref.md) | 4件套接口规范 → Controller模板 → 认证流程 | 移动端专用接口（/m前缀） |

### 代码生成规范（通用）

- **模板严格遵循**：注解、方法、字段、继承关系必须与模板一致
- **Service接口去重**：仅定义自定义方法，禁止重复生成IBaseService已有方法
- **Mapper.xml空条件判断**：`<where><if>` 防止全表查询
- **POST接口规范**：所有POST接口统一使用 `@RequestBody` 接收JSON格式参数

---

## 后端参考文档

### 📖 速查文档（开发中优先查阅）

| 文档 | 内容 |
|------|------|
| **[crud-quick-ref.md](./references/crud-quick-ref.md)** | CRUD基类方法、注解、DTO设计、返回值规范 |
| **[system-architecture-quick-ref.md](./references/system-architecture-quick-ref.md)** | 启动流程 / SPI扩展点 / 核心服务API / 匿名访问 |
| **[core-module-quick-ref.md](./references/core-module-quick-ref.md)** | 安全认证 / 权限注解 / 通用服务 |
| **[iot-module-quick-ref.md](./references/iot-module-quick-ref.md)** | IoT核心API / Controller速查 / 事件监听 / 数据模型 |
| **[plugin-quick-ref.md](./references/plugin-quick-ref.md)** | 插件必备文件 / 自动配置类 / 目录结构 |

### 📖 详细参考

| 文档 | 内容 |
|------|------|
| [core-module-reference.md](./references/core-module-reference.md) | Core模块完整Controller清单 |
| [iot-module-reference.md](./references/iot-module-reference.md) | IoT模块完整Controller清单 |
| [permission-spec.md](./references/permission-spec.md) | 权限编码规范 |

### 📖 SPI扩展点

| 文档 | 内容 |
|------|------|
| [spi-iot-data-model.md](./references/spi-iot-data-model.md) | 物联网数据模型SPI |
| [spi-iot-data-services.md](./references/spi-iot-data-services.md) | 物联网数据服务SPI |
| [spi-iot-event-listener.md](./references/spi-iot-event-listener.md) | 物联网事件监听SPI |

---

## 检查清单

### 流程检查
- [ ] 功能涉及哪些端已明确
- [ ] API契约已定义（路径/请求/响应）
- [ ] 单端任务直接委派给对应技能
- [ ] 多端任务已规划开发顺序

### 跨端协调检查
- [ ] 后端接口路径和权限标识已确定
- [ ] 管理端和移动端引用同一套API
- [ ] 响应数据结构在前端正确处理
- [ ] 异常/错误码已统一约定

### 交付检查
- [ ] 后端自测通过
- [ ] 前端页面功能正常
- [ ] 多端联调通过
- [ ] 文件清单已生成
