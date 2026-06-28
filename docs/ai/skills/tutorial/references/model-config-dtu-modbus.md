# DTU + Modbus 透传物模型配置参考

> **驱动源码：** `AbstractDtuForModbusProtocolSupplier.java`（基类）、`ModbusTcpForDtuProtocolSupplier.java`（TCP透传）、`ModbusRtuForDtuProtocolSupplier.java`（RTU透传）、`ModbusOverMqttForDtuProtocolSupplier.java`（MQTT透传）

DTU 透传场景下，设备通过 DTU 模块与平台通信，DTU 将 Modbus 协议数据透传给平台，平台相当于直接与 Modbus 设备交互。因此**物模型指令和 Modbus 驱动完全一致**。

## 驱动信息

| 驱动 | 协议编码 | 通信方式 |
|------|---------|---------|
| DTU + Modbus TCP 透传 | `DTU_MODBUS_TCP` | TCP |
| DTU + Modbus RTU 透传 | `DTU_MODBUS_RTU` | TCP |
| DTU + Modbus Over MQTT 透传 | `DTU_MODBUS_OVER_MQTT` | MQTT |

## 可选指令

与 Modbus 驱动相同：

### 事件模型（读指令）

| 指令编码 | 说明 |
|---------|------|
| `Read01` | 读线圈 |
| `Read02` | 读离散量输入 |
| `Read03` | 读保持寄存器 |
| `Read04` | 读输入寄存器 |

### 功能模型（写指令）

| 指令编码 | 说明 |
|---------|------|
| `Write05` | 写单个线圈 |
| `Write06` | 写单个保持寄存器 |
| `Write0F` | 写多个线圈 |
| `Write10` | 写多个保持寄存器 |

## 指令与上下行配置项

与 Modbus 驱动完全相同，详见 [Modbus 物模型配置参考](./model-config-modbus.md)。

## 网关设备配置

### DTU + Modbus TCP/RTU 透传

```json
{"host": "0.0.0.0", "port": 5020, "readIdle": 0, "messageType": "ASCII"}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `host` | 是 | 绑定的网卡 IP，`0.0.0.0` 表示全部 |
| `port` | 是 | 监听端口，默认 5020 |
| `readIdle` | 是 | 读空闲(秒)，≤0 不启用 |
| `messageType` | 是 | 注册码类型：ASCII/HEX |

### DTU + Modbus Over MQTT 透传

```json
{"host": "127.0.0.1", "port": 1883, "username": "", "password": "", "clientId": "", "keepalive": 3600, "qos": 0, "topics": "", "productCode": "", "publish": "", "subscription": ""}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `host` | 是 | MQTT Broker 地址 |
| `port` | 是 | 端口，默认 1883 |
| `username` | 否 | 用户名 |
| `password` | 否 | 密码 |
| `clientId` | 是 | 客户端 ID |
| `keepalive` | 否 | 保活时间(秒) |
| `qos` | 是 | 消息等级：0/1/2 |
| `topics` | 是 | 订阅主题（MQTT标准配置） |
| `productCode` | 是 | 子产品编号 |
| `publish` | 是 | 平台下发给 DTU 的主题，如 `demo/{deviceSn}/write` |
| `subscription` | 是 | DTU 上报给平台的主题，如 `demo/{deviceSn}/read` |

## 子设备配置

```json
{"dataFormat": "ABCD"}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `dataFormat` | 是 | 字节序：ABCD/BADC/CDAB/DCBA |

DTU Modbus 透传**不支持批量采集**（无 isBatch 配置项）。

## device_sn 说明

- 子设备的 `device_sn` 为**从机地址（slave address）**，范围 1~247
- 同一网关下的子设备 `device_sn` 必须唯一
