# OPC UA 物模型配置参考

> **驱动源码：** `OpcuaProtocolSupplier.java`

## 驱动信息

| 字段 | 值 |
|------|-----|
| 协议编码 | `OPCUA_DEFAULT_IMPL` |
| 通信方式 | TCP |
| 采集模式 | 点位采集 (POINT) |

## 可选指令

### 事件模型（读指令）

| 指令编码 | 说明 | 操作类型 |
|---------|------|---------|
| `READ` | 读取变量节点值 | R |

### 功能模型（写指令）

| 指令编码 | 说明 | 操作类型 |
|---------|------|---------|
| `WRITE` | 写入变量节点值 | W |
| `EXEC_METHOD` | 执行方法节点 | W |

## 指令与上下行配置项

### READ（读变量节点值）

| 方向 | protocol_attr_field | 配置说明 |
|------|-------------------|---------|
| DOWN | `address` | 节点地址，如 `ns=2;s=通道.设备.标记` |
| UP | `value` | 配置值填 `@属性字段`，绑定到物模型属性 |

### WRITE（写变量节点值）

| 方向 | protocol_attr_field | 配置说明 |
|------|-------------------|---------|
| DOWN | `address` | 节点地址 |
| DOWN | `value` | 要写入的值 |
| DOWN | `dataType` | 数据类型，可选值见下表 |
| UP | `status` | 操作状态 |

**dataType 可选值：**

| 值 | 说明 |
|----|------|
| `string` | 字符串 |
| `byte` | 字节 |
| `short` | int16 |
| `int` | int32 |
| `long` | int64 |
| `boolean` | 布尔 |
| `float` | 单精度浮点数 |
| `double` | 双精度浮点数 |
| `date` | 日期 |

### EXEC_METHOD（执行方法节点）

无上下行配置项，直接调用方法。

## 网关设备配置

```json
{"host": "127.0.0.1", "port": 49326, "username": "", "password": ""}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `host` | 是 | OPC UA 服务器地址 |
| `port` | 是 | 端口，默认 49326 |
| `username` | 否 | 用户名，不填则匿名连接 |
| `password` | 否 | 密码 |

## 子设备配置

```json
{"isBatch": "true", "limit": 128}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `isBatch` | 是 | 是否批量读取：true/false |
| `limit` | 是 | 单次读取的最大标记数 |

## device_sn 说明

- 子设备的 `device_sn` 只是唯一标识，OPC UA 通过节点地址寻址
- 同一网关下的子设备 `device_sn` 必须唯一
