# spi/iot — 核心数据对象

包路径：`com.iteaj.framework.spi.iot` / `com.iteaj.framework.spi.iot.data`

各接口方法的参数和返回值类型，开发协议插件时需重点掌握。

---

## DataSupplier（采集数据载体）

`DeviceProtocolSupplier.getDataSupplier()` 的返回值，封装一次采集所有点位的数据。

| 字段 | 说明 |
|------|------|
| `key` | 设备标识（`DeviceKey`） |
| `protocolCode` | 协议码 |
| `collectTime` | 采集时间 |
| `values` | 点位/字段值列表（`List<SignalOrFieldValue>`） |

构建方式：

```java
DataSupplier supplier = new DataSupplier(protocolCode, deviceKey);
supplier.build(new Date())   // 设置采集时间，同时填充未设置时间的点位
        .addValue(attrId, address, signalOrField, value);
```

`addValue` 重载说明：

| 方法签名 | 说明 |
|----------|------|
| `addValue(id, signalOrField, value)` | 添加点位值（无地址） |
| `addValue(id, address, signalOrField, value)` | 添加带地址的点位值 |
| `addValue(id, address, signalOrField, CollectStatus)` | 添加采集失败的点位 |
| `addValue(id, address, signalOrField, value, collectTime)` | 添加点位值并指定采集时间 |

---

## SignalOrFieldValue（单个点位值）

| 字段 | 说明 |
|------|------|
| `id` | 物模型属性或信号 id |
| `signalOrField` | 点位标识/字段名（在同一 `DeviceKey` 下唯一） |
| `address` | 点位地址（可选） |
| `value` | 采集值 |
| `collectTime` | 采集时间 |
| `status` | 采集状态（`Success` / 其他失败状态） |

构建方式：`SignalOrFieldValue.build(id, signalOrField, value)`

---

## DeviceKey（设备标识）

| 字段 | 说明 |
|------|------|
| `deviceSn` | 设备编号 |
| `parentDeviceSn` | 父设备编号（子设备时填写，如 DTU 下的从设备） |
| `key` | 唯一标识 = `parentDeviceSn->deviceSn`（有父设备时）或 `deviceSn` |

构建：`DeviceKey.build(deviceSn, parentDeviceSn)`

---

## UpModelAttr（上行模型属性元数据）

物模型属性的完整元数据，用于 `StaticProtocolSupplier.dataResolver()` 对原始值进行解析。

| 字段 | 说明 |
|------|------|
| `id` | 属性 id |
| `field` | 属性代码（与 `signalOrField` 对应） |
| `name` | 属性名称 |
| `dataType` | 数据类型（`DataType` 枚举） |
| `accuracy` | 精度（小数位数） |
| `gain` | 数据增益（原始值乘以增益） |
| `resolver` | 数据解析器名称（对应 `DataValueResolver.name()`） |
| `script` | 自定义解析脚本 |
| `path` | JSON 解析路径 |
| `bit` | 是否为位属性 |

---

## RealtimeDeviceStatus（设备实时状态）

`IModelAttrRealtimeDataService` 返回的设备状态对象（接口类型）。

| 方法 | 说明 |
|------|------|
| `getUid()` | 设备唯一 uid |
| `getDeviceSn()` | 设备编号 |
| `getDeviceName()` | 设备名称 |
| `getDeviceStatus()` | 设备状态（`online` / `offline`） |
| `getProtocolCode()` | 协议码 |
| `getProductCode()` | 产品码 |
| `getDeviceType()` | 设备类型 |
| `getParentDeviceSn()` | 父设备编号（网关子设备时有值） |
| `getConfig()` | 设备配置（`JSONObject`） |
| `getDeviceGroupId()` | 设备组 id |
| `buildKey()` | 构建 `DeviceKey` |

---

## ProductMeta（产品元数据）

`ProductModelService.listProducts()` 返回的产品信息（接口类型）。

| 方法 | 说明 |
|------|------|
| `getCode()` | 产品代码 |
| `getName()` | 产品名称 |
| `getProtocolCode()` | 绑定协议码 |
| `getGatewayType()` | 网关类型 |
| `getDeviceType()` | 设备类型 |
| `getConnectType()` | 网关连接类型 |
| `getConfig()` | 产品配置（`JSONObject`） |
| `getStatus()` | 产品状态（`FuncStatusEnum`） |

---

## ProtocolModel（协议模型）

`DeviceProtocolSupplier.getProtocol()` 的返回值，定义协议的完整结构（接口类型）。

| 方法 | 说明 |
|------|------|
| `getCode()` | 协议代码（全局唯一） |
| `getRemark()` | 协议说明 |
| `getType()` | 传输协议（`TransportProtocol` 枚举：TCP/MQTT/UDP/HTTP/WEBSOCKET/SERIAL） |
| `getCtrlMode()` | 控制方式（`CtrlMode` 枚举） |
| `getAttrs()` | 协议模型属性 Map（key = 属性 field） |
| `getApis()` | 协议模型指令/接口 Map（key = 接口 code） |
| `getAttr(field)` | 获取指定属性（`ProtocolModelAttr`） |
| `getApi(code)` | 获取指定接口（`AbstractProtocolModelApi`） |

内置实现：`DefaultProtocolModel`（推荐直接使用）
