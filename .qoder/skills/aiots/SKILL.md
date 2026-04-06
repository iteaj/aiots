---
name: 后端接口(管理后端、app后端)开发技能
description: aiots物联网项目的后端开发辅助技能。用于给移动端和管理端提供数据接口，提供对现有模块、新增业务模块、新增插件、项目架构、技术栈等说明。
---

# aiots 物联网项目开发技能

## 项目概述
> 用于给移动端和web管理端提供后端接口

---

## 系统架构

项目分为四个顶层模块：

```
iboot/

├── framework/        # 底层基础框架，所有模块的公共依赖
├── bootstrap/        # SpringBoot 启动入口，包含系统核心业务
├── app/              # 移动端UI项目，跨端（安卓、iOS、web、小程序）开发框架
└── iboot-plugin/     # 插件集合，按需引入 Maven 依赖来启用对应功能
```

### framework（基础框架层）
- framework 通过 `spi/iot` 包向插件开放物联网扩展接口[物联网扩展接口说明](references/SpiIotInterfaces.md)
- 权限体系详细规范见 [references/permission-spec.md](references/permission-spec.md)

### bootstrap（启动入口）
提供springboot启动入口，包含系统核心业务模块. 启动类 `boot/IBootApplication.java`。

### 业务模块和对应的接口列表
参考：[详见](references/function-dev-flow.md#业务模块和对应的接口列表)

### CRUD代码模板
参考：[详见](references/function-dev-flow.md#crud代码模板)

### IBaseService 已提供的接口

**IBaseService 提供了完整的 CRUD 默认方法，生成 Service 层代码时不得重复生成这些方法。**

#### 基础 CRUD 方法

| 方法名 | 功能说明 |
|--------|----------|
| save | 保存单条记录 |
| saveBatch | 批量保存 |
| saveOrUpdateBatch | 批量保存或更新 |
| removeById | 根据 ID 删除 |
| removeByMap | 根据条件删除 |
| remove | 根据条件删除 |
| removeByIds | 批量删除 |
| updateById | 根据 ID 更新 |
| update | 根据条件更新 |
| updateBatchById | 批量更新 |
| saveOrUpdate | 保存或更新 |

#### 查询方法

| 方法名 | 功能说明 |
|--------|----------|
| getById | 根据 ID 查询 |
| listByIds | 批量查询 |
| listByMap | 根据条件查询 |
| getOne | 查询一条记录 |
| getMap | 查询返回 Map |
| getObj | 查询并转换 |
| count | 统计总数 |
| list | 查询列表 |
| page | 分页查询 |
| listMaps | 查询列表返回 Map |
| listObjs | 查询全部记录 |
| pageMaps | 分页查询返回 Map |

#### 链式查询方法

| 方法名 | 功能说明 |
|--------|----------|
| query | 链式查询普通 |
| lambdaQuery | 链式查询 lambda |
| update | 链式更改普通 |
| lambdaUpdate | 链式更改 lambda |

### iboot-plugin（插件模块）

在 `bootstrap/pom.xml` 中引入对应 Maven 依赖即可启用：

| 插件模块 | 功能 |
|----------|------|
| `protocol-mqtt` | MQTT 协议设备接入 |
| `protocol-modbus`（tcp/rtu） | Modbus 协议（TCP/RTU） |
| `protocol-dtu` | DTU 设备接入 |
| `protocol-plc` | PLC 设备（欧姆龙/西门子） |
| `protocol-dahua` | 大华摄像头 |
| `protocol-hj212` | 环保 HJ212 协议 |
| `protocol-iec104` | IEC104 电力协议 |
| `protocol-dlt645` | DL/T645 电表协议 |
| `protocol-opcua` | OPC UA（开发中） |
| `iboot-edge` | 边缘计算（server/client/core） |
| `iboot-datasource` | 多数据源（含 TDengine） |
| `iboot-amqp` | RabbitMQ 消息推送 |
| `iboot-shiro` | Shiro 认证授权 |
| `iboot-SaToken` | SaToken 认证授权（与 shiro 二选一） |
| `iboot-oauth2` | OAuth2 授权 |
| `iboot-script` | 脚本插件 |
| `iboot-quartz` | 定时任务 |
| `iboot-message` | 短信/邮件消息通知 |
| `iboot-file` | 文件管理 |
| `iboot-gb28181` | 国标 GB28181 视频协议 |
| `iboot-le5le` | 乐吾乐组态/3D 大屏 |
| `iboot-iotview` | 大屏/组态插件 |
| `iboot-open` | 北向 API 开放接口 |
| `iboot-knife4j` | API 文档 |
| `iboot-code` | 代码生成器 |
| `iboot-ai/ai-mcp` | AI MCP 接入 |
| `iboot-ai/ai-vision` | AI 视觉检测（目标检测/人脸识别/OCR等） |

---

## AI 视觉模块架构

AI 视觉模块提供统一的视觉 AI 能力接口，支持目标检测、人脸识别、OCR、图像分割等场景。详细架构设计见 [references/ai-vision-architecture.md](references/ai-vision-architecture.md)

**核心特性**：
- **多场景覆盖**：检测/分类/OCR/人脸/分割/姿态/背景去除/超分辨率/特征提取/内容审核/视频流检测
- **模型可插拔**：通过 ModelAdapter 机制支持任意 ONNX 模型，运行时自动路由
- **亚洲人脸优化**：内置 GlintR100 适配器，针对亚洲人员识别优化

---

## maven插件创建流程与开发规范
创建新插件的完整流程及开发规范详见 [references/plugin-create-flow.md]

## 后端功能接口开发流程
后端功能接口开发流程规范[详见](references/function-dev-flow.md)

---

## 技术栈

| 层次 | 技术 |
|------|------|
| 语言 | Java 17+ |
| 框架 | Spring Boot 3.5+ |
| 网络 | Netty |
| ORM | MyBatis-Plus 3.4 |
| 数据库 | MySQL 8.0+ |
| 时序库 | TDengine（可选） |
| 缓存 | Redis |
| 认证 | SaToken |
| 消息队列 | RabbitMQ（iboot-amqp 插件） |
| 实时通信 | WebSocket |
| 模板引擎 | FreeMarker（HTML 渲染） |
| 工具库 | Hutool 5.7、Lombok |
| 构建工具 | Maven |
| 开发工具 | IDEA |

---