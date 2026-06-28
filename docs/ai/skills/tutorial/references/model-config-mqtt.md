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

```json
{"host": "127.0.0.1", "port": 1883, "username": "", "password": "", "clientId": "", "keepalive": 3600, "qos": 0, "topics": ""}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `host` | 是 | MQTT Broker 地址 |
| `port` | 是 | 端口，默认 1883 |
| `username` | 否 | 用户名 |
| `password` | 否 | 密码 |
| `clientId` | 是 | 客户端 ID |
| `keepalive` | 否 | 保活时间(秒)，默认 3600 |
| `qos` | 是 | 消息等级：0/1/2 |
| `topics` | 是 | 订阅的主题列表，多个用逗号隔开，需以产品编码开头 |

## 子设备配置

MQTT 驱动没有额外子设备 config（直接使用网关的发布订阅即可）。

## device_sn 说明

子设备的 `device_sn` 只是唯一标识，同一网关下必须唯一。
