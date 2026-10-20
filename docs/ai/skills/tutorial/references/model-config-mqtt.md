# MQTT 物模型配置参考

> **驱动源码：** `DefaultMqttProtocolSupplier.java`

## 驱动信息

| 字段 | 值 |
|------|-----|
| 协议编码 | `MQTT_DEFAULT_IMPL` |
| 通信方式 | MQTT |
| 采集模式 | 公共模式 (COMMON) |

> MQTT 为公共模式，事件模型用于配置**数据上报的解析规则**（订阅），功能模型用于配置**下发指令**（发布）。

## 可选指令

### 功能模型

| 指令编码 | 说明 | 操作类型 |
|---------|------|---------|
| `publish` | 发布 | W |

### 事件模型

| 指令编码 | 说明 | 操作类型 |
|---------|------|---------|
| `subscribe` | 订阅 | R |

## 指令与上下行配置项

### publish（发布）

| 方向 | protocol_attr_field | 配置说明 |
|------|-------------------|---------|
| DOWN | `topic` | 发布的主题，支持占位符 `{clientId}`、`{deviceSn}`、`{gatewaySn}` |
| DOWN | `PublicPayload` | 发布的数据 |

### subscribe（订阅）

| 方向 | protocol_attr_field | 配置说明 |
|------|-------------------|---------|
| UP | `topic` | 订阅的主题，需以设备产品编码开头，支持占位符 |
| UP | `will` | 是否遗嘱消息：true/false |
| UP | `collectTime` | 采集时间，支持 JSON 路径如 `$.collectTime` 或 `{now}` |
| UP | `payload` | 订阅的数据（JSON 格式），脚本解析后绑定到物模型属性 |

## 网关设备配置

创建网关设备时，config 按以下字段配置：

```json
{"host": "127.0.0.1", "port": 1883, "username": "", "password": "", "clientId": "", "keepalive": 3600, "qos": 0, "topics": ""}
```

| 字段 | 必填 | 类型 | 说明 |
|------|------|------|------|
| `host` | 是 | string | MQTT Broker 地址 |
| `port` | 是 | number | 端口，默认 1883 |
| `username` | 否 | string | 用户名 |
| `password` | 否 | string | 密码 |
| `clientId` | 是 | string | 客户端 ID，MQTT 连接的唯一标识 |
| `keepalive` | 否 | number | 保活时间(秒)，默认 3600，必须大于 0 |
| `qos` | 是 | number | 消息等级：0（最多一次）/1（至少一次）/2（仅一次） |
| `topics` | 是 | string | 订阅的主题列表，多个用逗号隔开 |

### topics 主题规则

- **必须以子设备的产品编码开头**，例如产品编码为 `temp_sensor`，则主题应为 `temp_sensor/xxx`
- 支持多个主题，用逗号分隔
- 支持 MQTT 标准通配符：`+`（单层通配）、`#`（多层通配）
- 示例：`temp_sensor/+/data` 表示订阅 temp_sensor 产品下所有设备的 data 主题

## 占位符说明

在事件模型和事件模型的上下行配置中，主题字段支持以下占位符，运行时自动替换为实际值：

| 占位符 | 说明 | 示例值 |
|---------|------|--------|
| `{deviceSn}` | 当前设备的设备编号 | `TEMP-001` |
| `{gatewaySn}` | 父网关设备的设备编号 | `GW-001` |
| `{clientId}` | 网关设备配置中的客户端 ID | `mqtt-client-01` |

**使用场景示例：**

- 订阅主题配置：`temp_sensor/{deviceSn}/data` → 运行时替换为 `temp_sensor/TEMP-001/data`
- 发布主题配置：`{gatewaySn}/{deviceSn}/control` → 运行时替换为 `GW-001/TEMP-001/control`

> **注意：** 订阅主题（subscribe 指令的 topic）必须以子设备的产品编码开头，且需包含 `{deviceSn}` 占位符，否则平台无法将收到的消息路由到正确的子设备。

## device_sn 说明

子设备的 `device_sn` 只是唯一标识，同一网关下必须唯一。
