---
name: driver
description: 设备驱动开发技能。当需要开发物联网设备驱动（PLC/Modbus/MQTT/DTU/OPCUA/自定义协议接入）、实现DeviceProtocolSupplier SPI接口、编写驱动模板代码、配置网络参数时使用。
role: 设备驱动开发工程师
---

# 设备驱动开发技能

## 1. 适用场景

开发物联网设备驱动时使用，包括但不限于：
- **协议接入**：PLC（西门子/欧姆龙/三菱/汇川/松下/AB）、Modbus（RTU/TCP）、MQTT、DTU网关、OPC UA
- **驱动实现**：实现 `DeviceProtocolSupplier` SPI 接口，完成协议解析与数据采集
- **模板使用**：基于 `driver/references/` 下的模板生成驱动代码

## 2. 参考文档

| 文档 | 内容 |
|------|------|
| **[driver-dev-flow.md](./references/driver-dev-flow.md)** | 架构设计 / 顶级接口 / 抽象实现类 / 实现步骤 / 核心数据对象 / 网络配置 / 检查清单 |
| [TEMPLATE_USAGE.md](./references/TEMPLATE_USAGE.md) | 驱动模板使用说明 |
| [StaticClientProtocolSupplierTemplate.java](./references/StaticClientProtocolSupplierTemplate.java) | 静态客户端协议提供模板 |
| [StaticServerProtocolSupplierTemplate.java](./references/StaticServerProtocolSupplierTemplate.java) | 静态服务端协议提供模板 |
| [DirectStaticServerProtocolSupplierTemplate.java](./references/DirectStaticServerProtocolSupplierTemplate.java) | 直连服务端抽象类模板 |
| [FuncProtocolModelApiTemplate.java](./references/FuncProtocolModelApiTemplate.java) | 功能接口实现模板（平台采集/控制） |
| [EventProtocolModelApiTemplate.java](./references/EventProtocolModelApiTemplate.java) | 事件接口实现模板（设备主动上报） |

## 3. 架构概览

```
DeviceProtocolSupplier (顶层接口)
├── ServerProtocolSupplier (服务端协议接口)
│   └── StaticServerProtocolSupplier (静态服务端抽象类)
│       └── DirectStaticServerProtocolSupplier (直连服务端抽象类)
├── ClientProtocolSupplier (客户端协议接口)
│   └── StaticClientProtocolSupplier (静态客户端抽象类)
├── StaticProtocolSupplier (静态协议抽象基类)
└── DynamicProtocolSupplier (动态协议抽象类)
```

## 4. 开发规范

- **模板严格遵循**：不得删除模板中的方法和属性，不得修改方法签名和类继承关系
- **TODO 标记**：在模板的 TODO 处填充实现，保留所有 Javadoc 注释
- **网络配置**：使用 `NetworkConfigImpl` SPI 进行网络参数配置
- **批量支持**：实现 `BatchProtocolSupplier` 标记接口开启批量读取
- **驱动包路径**：`iboot-plugin/iboot-protocol/protocol-plc/{driver-name}/`

## 5. 检查清单

- [ ] 选择正确的模板（Client/Server/Direct）
- [ ] 模板方法和属性完整保留
- [ ] 类继承关系正确
- [ ] TODO 处已填充实现
- [ ] 网络配置已注册 SPI
- [ ] 驱动包目录结构符合规范
