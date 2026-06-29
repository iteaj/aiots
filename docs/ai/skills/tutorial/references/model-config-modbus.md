# Modbus 物模型配置参考

> **驱动源码：** `AbstractModbusProtocolSupplier.java`（基类）、`ModbusTcpProtocolSupplier.java`（TCP）、`ModbusRtuProtocolSupplier.java`（RTU）
>
> 此文档供创建 Modbus 产品的物模型（事件模型、功能模型）及创建设备时查阅配置项。

## 驱动信息

| 字段 | Modbus TCP | Modbus RTU |
|------|-----------|-----------|
| 协议编码 | `MODBUS_TCP` | `MODBUS_RTU` |
| 通信方式 | TCP | 串口 (SERIAL) |

## 可选指令

创建事件模型或功能模型时，`direct` 字段从以下指令中选择：

### 事件模型（读）

| 指令编码 | 说明 | 适用场景 |
|---------|------|---------|
| `Read01` | 读线圈 | 读取开关量（线圈状态） |
| `Read02` | 读离散量输入 | 读取离散输入信号 |
| `Read03` | 读保持寄存器 | 读取模拟量/数值数据（温度、压力等） |
| `Read04` | 读输入寄存器 | 读取输入寄存器值 |

### 功能模型（写）

| 指令编码 | 说明 | 适用场景 |
|---------|------|---------|
| `Write05` | 写单个线圈 | 控制单个开关 |
| `Write06` | 写单个保持寄存器 | 写单个数值 |
| `Write0F` | 写多个线圈 | 批量控制开关 |
| `Write10` | 写多个保持寄存器 | 写数值（联动脚本写寄存器用此指令） |

## 指令与上下行配置项

选择指令后，需要为其配置上下行参数。以下列出各指令所需的 `protocol_attr_field`：

### Read01 / Read02（线圈/离散量）

| 方向 | protocol_attr_field | 配置说明 |
|------|-------------------|---------|
| DOWN | `address` | 线圈起始地址 |
| UP | `value` | 配置值填 `@属性字段`，将读取值绑定到物模型属性（如 `@switch_status`） |

数据类型自动为 boolean（读取到的值自动转为 true/false），无需配置 dataType 和 num。

### Read03 / Read04（保持寄存器/输入寄存器）

| 方向 | protocol_attr_field | 配置说明 |
|------|-------------------|---------|
| DOWN | `dataType` | 选择数据类型：见下方可选值 |
| DOWN | `address` | 寄存器起始地址 |
| DOWN | `num` | 寄存器数量（dataType 为 string/hex 时需手动指定） |
| UP | `value` | 配置值填 `@属性字段`，绑定到物模型属性（如 `@temperature`） |

**dataType 可选值：**

| 值 | 说明 | 寄存器数 |
|----|------|---------|
| `short` / `ushort` | 有符号/无符号16位整数 | 1 |
| `int` / `uint` | 有符号/无符号32位整数 | 2 |
| `long` | 64位整数 | 4 |
| `float` | 单精度浮点数 | 2 |
| `double` | 双精度浮点数 | 4 |
| `hex` | 十六进制 | 自定义 |
| `string` | 字符串 | 自定义 |
| `timestamp` | 时间戳 | 4 |

### Write05 / Write06 / Write0F（写线圈/单寄存器/多线圈）

| 方向 | protocol_attr_field | 配置说明 |
|------|-------------------|---------|
| DOWN | `address` | 要写的寄存器/线圈地址 |
| DOWN | `value` | 写固定值（如 `1`），或 `@属性字段` 绑定字典属性实现多选项控制 |

### Write10（写多个保持寄存器）

| 方向 | protocol_attr_field | 配置说明 |
|------|-------------------|---------|
| DOWN | `dataType` | 数据类型，可选值与读指令相同 |
| DOWN | `address` | 寄存器起始地址 |
| DOWN | `value` | 写固定值，或 `@属性字段` 绑定字典属性 |

**Write10 的 dataType 可选值：**

`short` / `int` / `long` / `float` / `double` / `hex` / `string` / `timestamp`

## 网关设备配置

创建网关设备时，config 按以下字段配置：

### Modbus TCP

```json
{"host": "127.0.0.1", "port": 502, "timeout": 5, "readIdle": 0}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `host` | 是 | 设备主机地址 |
| `port` | 是 | 端口，默认 502 |
| `timeout` | 是 | 请求超时(秒) |
| `readIdle` | 否 | 读空闲(秒)，≤0 不启用 |

### Modbus RTU

```json
{"baudRate": "9600", "dataBits": "8", "parity": "0", "stopBits": "1", "readIdle": 5}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `baudRate` | 是 | 波特率：1200/2400/4800/9600/14400/19200/38400/56000/57600/115200 |
| `dataBits` | 是 | 数据位：5/6/7/8 |
| `parity` | 是 | 校验位：0-无/1-奇/2-偶/3-标记/4-空格 |
| `stopBits` | 是 | 停止位：1/1.5/2 |
| `readIdle` | 是 | 读超时(秒) |

## 子设备配置

创建子设备时，config 按以下字段配置：

```json
{"isBatch": "true", "dataFormat": "ABCD"}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `isBatch` | 是 | 是否批量采集：true/false |
| `dataFormat` | 是 | 字节序，见下表 |

**dataFormat 字节序（多字节数据在寄存器中的排列顺序）：**

| 格式 | 说明 | 示例 (0x12345678) |
|------|------|-------------------|
| `ABCD` | 大端（高字节在前） | `12 34 56 78` |
| `BADC` | 交换字节 | `34 12 78 56` |
| `CDAB` | 交换字 | `56 78 12 34` |
| `DCBA` | 小端（低字节在前） | `78 56 34 12` |

> 一般 Modbus 设备默认 ABCD（大端），具体以设备手册为准。

## device_sn 说明

- 子设备的 `device_sn` 为**从机地址（slave address）**，范围 1~247
- 同一网关下 `device_sn` 必须唯一

## 附：协议参数与物模型属性的区别

驱动中定义的 `dataType`、`address`、`num`、`value` 是**协议通信参数**，出现事件/功能模型的上下行配置中，用于告诉驱动如何通信（读哪个地址、什么数据类型、读几个寄存器）。这些不是管理端配置的物模型属性。

管理端配置的物模型属性（temperature、humidity、switch_status）是**业务数据属性**，定义设备会上报哪些数据点。
