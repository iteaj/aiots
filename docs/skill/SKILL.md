---
name: aiots
description: aiots物联网网关项目的开发辅助技能。当需要在此项目中进行代码开发、新增业务模块、新增插件、理解项目架构或遵循项目开发规范时使用。包含项目结构说明、实体/Service/Controller 开发规范、插件包名规范、权限编码规范等完整上下文。
---

# aiots 物联网网关项目开发技能

## 项目概述

aiots 是基于 Java 1.8、Spring Boot 2.7、Netty 开发的物联网网关平台。
- **南向**：采集和控制物联网设备（TCP、UDP、MQTT、HTTP、串口、Modbus、PLC 等）
- **北向**：通过 HTTP 同步接口或 MQTT 异步消息向第三方业务系统开放设备数据
- **实时推送**：采集数据、告警、设备上下线通过 WebSocket 实时推送前端，或通过 RabbitMQ 推送业务系统
- **多数据源**：支持 MySQL + TDengine 时序数据库双写
- **边缘部署**：支持部署在边缘网关服务器（Linux 环境）
- **插件化架构**：功能以插件形式按需引入，扩展灵活

---

## 模块架构

项目分为三个顶层模块：

```
iboot/
├── framework/        # 底层基础框架，所有模块的公共依赖
├── bootstrap/        # SpringBoot 启动入口，包含系统核心业务
└── iboot-plugin/     # 插件集合，按需引入 Maven 依赖来启用对应功能
```

### framework（基础框架层）

所有模块都依赖此模块，提供以下基础能力：

| 包路径 | 职责 |
|--------|------|
| `com.iteaj.framework` | 基类：Entity、BaseEntity、TreeEntity、IBaseService、BaseServiceImpl、AbstractController、BaseController |
| `com.iteaj.framework.result` | 统一响应封装：Result、HttpResult、BooleanResult、DetailResult、ListResult、PageResult |
| `com.iteaj.framework.autoconfigure` | 自动配置：缓存、验证码、Mybatis-Plus、Web、插件注册 |
| `com.iteaj.framework.cache` | Redis 缓存扩展 |
| `com.iteaj.framework.logger` | 操作日志、访问日志、实时日志推送 |
| `com.iteaj.framework.exception` | 统一异常：ServiceException、FrameworkException、全局异常处理 |
| `com.iteaj.framework.mybatis` | MyBatis 扩展 TypeHandler |
| `com.iteaj.framework.security` | 权限注解 CheckPermission、SecurityUtil |

### bootstrap（启动主模块）

包含两个核心业务子模块：

**`module/core`（系统管理）**：用户、角色、菜单、组织、岗位、字典、系统配置、通知、在线用户、操作日志等

**`module/iot`（物联网核心）**：
- 设备管理、产品管理、物模型（属性/功能/事件）
- 实时推送：`RealtimePushListener` → WebSocket
- 联动规则、告警机制
- 调试功能

> framework 通过 `spi/iot` 包向插件开放物联网扩展接口，插件开发时需重点了解，详见 [assets/SpiIotInterfaces.md]

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

AI 视觉模块提供统一的视觉 AI 能力接口，支持目标检测、人脸识别、OCR、图像分割等场景。详细架构设计见 [assets/ai-vision-architecture.md](assets/ai-vision-architecture.md)

**核心特性**：
- **多场景覆盖**：检测/分类/OCR/人脸/分割/姿态/背景去除/超分辨率/特征提取/内容审核/视频流检测
- **模型可插拔**：通过 ModelAdapter 机制支持任意 ONNX 模型，运行时自动路由
- **亚洲人脸优化**：内置 GlintR100 适配器，针对亚洲人员识别优化

---

## 插件创建流程与开发规范

创建新插件的完整流程及开发规范详见 [assets/plugin-create-flow.md]

---

## 技术栈

| 层次 | 技术 |
|------|------|
| 语言 | Java 1.8 |
| 框架 | Spring Boot 2.7 |
| 网络 | Netty |
| ORM | MyBatis-Plus 3.4 |
| 数据库 | MySQL 8.0+ |
| 时序库 | TDengine（可选） |
| 缓存 | Redis |
| 认证 | Shiro 或 SaToken（二选一，按引入插件决定） |
| 消息队列 | RabbitMQ（iboot-amqp 插件） |
| 实时通信 | WebSocket |
| 模板引擎 | FreeMarker（HTML 渲染） |
| 工具库 | Hutool 5.7、Lombok |
| 构建工具 | Maven |
| 开发工具 | IDEA |
