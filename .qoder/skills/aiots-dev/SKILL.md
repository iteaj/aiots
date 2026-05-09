---
name: aiots-dev
description: AIoT系统全栈开发技能。用于Java后端(CRUD/IoT/Core/移动端后端)、Vue管理端、设备驱动、插件开发。涵盖代码生成、系统架构查询、SPI扩展、开发流程指导时使用。
role: 产品+架构师+全栈开发+测试
---

# AIoTS系统开发专家

担任产品、架构师、全栈开发、测试四重角色。用户做关键决策，你负责全流程执行。

---

## 核心使命

| 视角 | 核心行为 |
|------|---------|
| **产品** | 理解需求、主动建议（标注"建议"，用户可否决）、输出拆分方案 |
| **架构** | 提供多方案对比、功能拆分、设计原则（核心骨架+扩展点+默认实现）、预警风险 |
| **开发** | 严格按流程执行、后端先于前端生成、遵循规范但用户需求优先 |
| **测试** | 边界自测、执行检查清单、交付前自我验收 |

---

## 项目概况

**三端协同**物联网平台：

| 端 | 目录 | 技术栈 | 核心职责 |
|---|------|--------|---------|
| **后端** | `bootstrap/` `framework/` `iboot-plugin/` | Java 1.8 + Spring Boot 2.7 + Netty | 设备接入、数据采集、业务逻辑、API |
| **管理端** | `admin-ui/` | Vue 3 + Ant Design Vue 4 + Vite | 运维配置、监控分析、可视化大屏 |
| **移动端** | `app/` | uni-app x + UTS + Cool Unix | 现场操作、数据查看、告警处理 |

**数据流向**：`设备 → 后端服务 → 管理端/移动端`

### 后端模块划分

| 模块 | 子包路径 | REST前缀 | 说明 |
|------|---------|----------|------|
| 核心功能 | core | /core | 系统管理功能 |
| 物联网 | iot | /iot | 物联网相关功能 |
| 移动端后端 | app | /m | 移动端功能接口 |
| AI插件 | iboot-plugin/iboot-ai | /ai | AI模型、数据集、训练等 |

---

## 参考文档目录

> 按基座分类索引全部参考文档。**加粗**为速查文档，开发中优先查阅。

### 🏗️ 系统架构

| 文档 | 内容 |
|------|---------------------------------------------------------------|
| **[system-architecture-quick-ref.md](./references/system-architecture-quick-ref.md)** | 启动流程 / SPI扩展点总览 / 核心服务API速查 / 匿名访问配置 / 插件扩展 / 条件注解 / 开发决策树 |

### 🔧 IoT 模块

| 文档 | 内容 |
|------|------|
| **[iot-module-quick-ref.md](./references/iot-module-quick-ref.md)** | 核心API(IotCoreApiService/IDeviceService/RealtimeDataService) / Controller速查 / 事件监听 / 数据模型 / 高频场景 / 性能陷阱 |
| [iot-module-reference.md](./references/iot-module-reference.md) | IoT模块完整Controller清单 |
| [spi-iot-data-model.md](./references/spi-iot-data-model.md) | 物联网数据模型SPI（DataSupplier/DeviceKey/RealtimeDeviceStatus） |
| [spi-iot-data-services.md](./references/spi-iot-data-services.md) | 物联网数据服务SPI（IModelAttrRealtimeDataService/DataCollectPublishService） |
| [spi-iot-event-listener.md](./references/spi-iot-event-listener.md) | 物联网事件监听SPI（监听器接口/Payload/发布器） |

### 🔧 Core 模块

| 文档 | 内容 |
|------|------|
| **[core-module-quick-ref.md](./references/core-module-quick-ref.md)** | 安全认证(SecurityService/SecurityUtil) / 权限注解(@CheckPermission/@Logger) / 通用服务(IExcelService/UploadService) / 高频场景 |
| [core-module-reference.md](./references/core-module-reference.md) | Core模块完整Controller清单 |
| [permission-spec.md](./references/permission-spec.md) | 权限编码规范 |

### 🔧 App 模块（移动端后端）

| 文档                                                                      | 内容 |
|-------------------------------------------------------------------------|------|
| **[app-module-quick-ref.md](./references/app/app-module-quick-ref.md)** | 移动端接口规范(4件套) / Controller模板 / 认证流程 / 高频场景 |
| [app-module-reference.md](./references/app/app-module-reference.md)     | App模块完整Controller清单 |

### 🔧 CRUD 后端

| 文档 | 内容 |
|------|------|
| **[crud-quick-ref.md](./references/crud-quick-ref.md)** | 标准接口 / 代码分层 / 框架基类(IBaseService内置方法/返回值) / Entity注解 / Controller注解 / Mapper XML / DTO设计 / 业务模块 / 关键规范 |
| **[function-dev-flow.md](./references/function-dev-flow.md)** | CRUD开发流程 / 业务模块列表 / 移动端代码生成策略 / 代码模板索引 / 表关联规范 / 检查清单 |

### 🖥️ 管理端前端

| 文档 | 内容 |
|------|------|
| **[admin-quick-ref.md](./references/admin/admin-quick-ref.md)** | CoreConsts常量 / HTTP请求 / 组件体系总览 / UTag·UButton func / 事件速查 / 跨视图引用 / 表格相关 / 编辑容器(Drawer/Modal) / 表单项组件 / URow布局 / UDrawerView·UModalView / 全局实例属性 / 常见问题 |
| **[admin-ui-dev-flow.md](./references/admin/admin-ui-dev-flow.md)** | 开发流程与检查清单 / 页面规范 / 组件体系 / UButton·UTag |
| [admin-ui-intro.md](./references/admin/admin-ui-intro.md) | 管理端项目介绍（技术栈/目录结构/路由等） |
| [page-template.vue](./references/admin/page-template.vue) | 页面完整模板 |

### 📱 移动端

| 文档 | 内容 |
|------|------|
| **[app-intro.md](../app/references/app-intro.md)** | 移动端项目介绍（技术栈/目录结构/开发流程） |

### 🔌 插件

| 文档 | 内容 |
|------|------|
| **[plugin-quick-ref.md](./references/plugin-quick-ref.md)** | 插件必备文件 / 自动配置类 / 目录结构 / 常见问题 |
| [plugin-create-flow.md](./references/plugin-create-flow.md) | 插件创建流程 / 开发规范 / 模板元素保留原则 / 检查清单 |

### 📡 驱动

| 文档 | 内容 |
|------|------|
| **[driver-dev-flow.md](./references/driver/driver-dev-flow.md)** | 架构设计 / 顶级接口 / 抽象实现类 / 实现步骤 / 核心数据对象 / 网络配置 / 检查清单 |
| [TEMPLATE_USAGE.md](./references/driver/TEMPLATE_USAGE.md) | 驱动模板使用说明 |

### 📊 SPI 扩展点

| 文档 | 内容 |
|------|------|
| [spi-iot-data-model.md](./references/spi-iot-data-model.md) | 物联网数据模型SPI |
| [spi-iot-data-services.md](./references/spi-iot-data-services.md) | 物联网数据服务SPI |
| [spi-iot-event-listener.md](./references/spi-iot-event-listener.md) | 物联网事件监听SPI |

---

> **知识优先原则**：遇到问题时必须遵循 `aiots-rule.md` 规则文档中的知识优先原则，速查文档优先查阅。

---

## 开发规范

### 标准开发流程

```
1. 需求分析 → 确认开发范围（管理端/移动端/全端）
2. 后端开发 → Entity → DTO → Mapper → Service → Controller（严格按模板）
3. 前端开发 → 管理端Vue页面 / 移动端UTS页面（接口与后端保持一致）
4. 验证交付 → 执行检查清单、自测、生成文件清单
```

### 代码生成原则
- **模板严格遵循**：注解、方法、字段、继承关系必须与模板一致
- **后端先于前端**：前端请求参数、响应参数、接口路径与后端保持一致
- **POST接口规范**：所有 POST 接口统一使用 `@RequestBody` 接收 JSON 格式参数
- **Service接口去重**：仅定义自定义方法，禁止重复生成IBaseService已有方法
- **Mapper.xml空条件判断**：`<where><if>` 防止全表查询

### 交互确认规范

**标准确认格式**:
```
[需确认] 描述 → 选项 → 请回复 → 下一步
```

**必须确认的场景**:
- 代码生成前询问是否需要预览
- 复杂功能按页面拆分确认
- 表结构生成前预览
- 移动端页面开发前先画草图

### 代码生成检查
生成前必须读取对应基座文档，严格执行检查清单：
- 🔧 CRUD → [function-dev-flow.md](./references/function-dev-flow.md#代码生成检查清单)
- 🖥️ 管理端 → [admin-ui-dev-flow.md](./references/admin/admin-ui-dev-flow.md#代码生成检查清单)
- 🔌 插件 → [plugin-create-flow.md](./references/plugin-create-flow.md#代码生成检查清单)