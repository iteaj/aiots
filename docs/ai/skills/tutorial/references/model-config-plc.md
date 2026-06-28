# PLC 物模型配置参考

> **驱动源码：** `AbstractPlcProtocolSupplier.java`（基类）
> **具体实现：** `SiemensProtocolSupplier.java`、`OmronProtocolSupplier.java`、`MelsecProtocolSupplier.java`、`PanasonicProtocolSupplier.java`、`InovanceProtocolSupplier.java`、`ABProtocolSupplier.java`

PLC 各品牌驱动均继承自同一个基类 `AbstractPlcProtocolSupplier`，因此**指令集和上下行配置完全一致**，仅协议编码和网关配置不同。

## 驱动信息

| 品牌 | 协议编码 | 通信方式 |
|------|---------|---------|
| 西门子 (Siemens) | `PLC_SIEMENS_S7` | TCP |
| 欧姆龙 (Omron) | `PLC_OMRON_TCP` | TCP |
| 三菱 (Melsec) | `PLC_MELSEC` | TCP |
| 松下 (Panasonic) | `PLC_PANASONIC` | TCP |
| 汇川 (Inovance) | `PLC_INOVANCE` | TCP |
| 罗克韦尔 (AB) | `PLC_AB` | TCP |

> 所有 PLC 驱动采用点位采集模式 (POINT)，网关类型为 Gateway。

## 可选指令

基类通过 `addModelApi()` 注册了以下指令，所有 PLC 品牌共用：

### 事件模型（读指令）

| 指令编码 | 说明 | 操作类型 |
|---------|------|---------|
| `readBool` | 读取布尔值 | R |
| `readInt16` | 读取 short 值 | R |
| `readUInt16` | 读取无符号 short 值 | R |
| `readInt32` | 读取 int 值 | R |
| `readUInt32` | 读取无符号 int 值 | R |
| `readInt64` | 读取 long 值 | R |
| `readFloat` | 读取 float 值 | R |
| `readDouble` | 读取 double 值 | R |
| `readString` | 读取 string 值（UTF8编码） | R |

### 功能模型（写指令）

| 指令编码 | 说明 | 操作类型 |
|---------|------|---------|
| `writeBool` | 写布尔值 | W |
| `writeInt16` | 写 short 值 | W |
| `writeInt32` | 写 int 值 | W |
| `writeInt64` | 写 long 值 | W |
| `writeFloat` | 写 float 值 | W |
| `writeDouble` | 写 double 值 | W |
| `writeHex` | 写 Hex 值 | W |
| `writeString` | 写 string 值（UTF8编码） | W |

### 西门子 S7 扩展指令

西门子在基类基础上额外注册了以下指令（`SiemensProtocolSupplier` 构造方法中注册）：

| 指令编码 | 说明 | 操作类型 |
|---------|------|---------|
| `readByte` | 读取字节值 | R |
| `readDate` | 读取日期值 | R |
| `readDtlDatetime` | 读取 DTL 时间值 | R |
| `writeByte` | 写 byte 值 | W |
| `writeDate` | 写日期值 | W |
| `writeDatetime` | 写 DTL 时间值 | W |

## 指令与上下行配置项

所有读指令和写指令，除了 `readString` 外，都只需要配置 `address`（PLC 地址）。`readString` 需要额外配置 `num`（寄存器数量）。

### 读指令配置（readBool / readInt16 / readUInt16 / readInt32 / readUInt32 / readInt64 / readFloat / readDouble）

| 方向 | protocol_attr_field | 配置说明 |
|------|-------------------|---------|
| DOWN | `address` | PLC 地址，如 `DB100.0`、`M0.0`、`I0.0` 等 |
| UP | `value` | 配置值填 `@属性字段`，绑定到物模型属性 |

### readString（读字符串）

| 方向 | protocol_attr_field | 配置说明 |
|------|-------------------|---------|
| DOWN | `address` | PLC 地址 |
| DOWN | `num` | 读取的寄存器数量 |
| UP | `value` | 配置值填 `@属性字段`，绑定到物模型属性 |

### 写指令配置（writeBool / writeInt16 / writeInt32 / writeInt64 / writeFloat / writeDouble / writeHex / writeString）

| 方向 | protocol_attr_field | 配置说明 |
|------|-------------------|---------|
| DOWN | `address` | PLC 地址 |
| DOWN | `value` | 写固定值，或 `@属性字段` 绑定字典属性 |
| UP | `status` | 操作状态（自动返回） |

### 西门子扩展指令配置

**readByte / readDate / readDtlDatetime**

| 方向 | protocol_attr_field | 配置说明 |
|------|-------------------|---------|
| DOWN | `address` | PLC 地址 |
| UP | `value` | 配置值填 `@属性字段`，绑定到物模型属性 |

**writeByte / writeDate / writeDatetime**

| 方向 | protocol_attr_field | 配置说明 |
|------|-------------------|---------|
| DOWN | `address` | PLC 地址 |
| DOWN | `value` | 写固定值，或 `@属性字段` |
| UP | `status` | 操作状态 |

## 网关设备配置

各品牌 PLC 驱动的网关配置（`getGatewayConfig()`）不同：

### 西门子 S7

```json
{"host": "127.0.0.1", "port": 102, "model": "S1200", "readIdle": 600}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `host` | 是 | 设备主机地址 |
| `port` | 是 | 端口，默认 102 |
| `model` | 是 | PLC 型号：S200/S200Smart/S300/S400/S1200/S1500 |
| `readIdle` | 是 | 读空闲(秒)，超过无数据自动断开 |

### 欧姆龙

```json
{"host": "127.0.0.1", "port": 9600, "readIdle": 600}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `host` | 是 | 设备主机地址 |
| `port` | 是 | 端口，默认 9600 |
| `readIdle` | 是 | 读空闲(秒) |

### 三菱

```json
{"host": "127.0.0.1", "port": 6000, "protocolType": "Qna3E"}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `host` | 是 | 设备主机地址 |
| `port` | 是 | 端口，默认 6000 |
| `protocolType` | 是 | 协议类型：Qna3C/Qna3E(MC)/Qna3E(MC)-R序列/FxLinks/FxSerial/A1E |

### 松下

```json
{"host": "127.0.0.1", "port": 44818, "station": 238}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `host` | 是 | 设备主机地址 |
| `port` | 是 | 端口，默认 44818 |
| `station` | 是 | 站点号 |

### 汇川

```json
{"host": "127.0.0.1", "port": 502, "station": 238, "series": "AM"}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `host` | 是 | 设备主机地址 |
| `port` | 是 | 端口，默认 502 |
| `station` | 是 | 站点号 |
| `series` | 是 | 型号序列：AM(AM400/AM800)、H3U、H5U |

### 罗克韦尔 (AB)

```json
{"host": "127.0.0.1", "port": 44818, "readIdle": 600}
```

| 字段 | 必填 | 说明 |
|------|------|------|
| `host` | 是 | 设备主机地址 |
| `port` | 是 | 端口，默认 44818 |
| `readIdle` | 是 | 读空闲(秒) |

## 子设备配置

PLC 驱动没有额外的子设备 `config` 配置项（`getDeviceConfig()` 返回空），创建子设备时 config 留空或传 `{}` 即可。

## device_sn 说明

- 子设备的 `device_sn` 只是唯一标识，**不表示 PLC 的站地址**（站地址在点位地址中配置）
- 同一网关下的子设备 `device_sn` 必须唯一
