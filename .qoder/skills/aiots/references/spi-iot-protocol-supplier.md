# spi/iot — 协议提供接口

包路径：`com.iteaj.framework.spi.iot`

插件通过实现这些接口向平台注册一种设备通信协议。

---

## DeviceProtocolSupplier（顶层接口）

所有协议插件必须实现，通常通过继承 `StaticProtocolSupplier` 实现。

| 方法 | 说明 |
|------|------|
| `getDesc()` | 协议描述 |
| `getVersion()` | 协议版本 |
| `getProtocol()` | 返回协议模型（`ProtocolModel`，含协议码、属性、指令） |
| `getGatewayType()` | 网关类型（`CloudGateway` / `EdgeGateway`） |
| `getConnectionType()` | 连接类型（`Server` / `Client`） |
| `getImplMode()` | 实现方式（`Internal` 内置 / `Jar` 外部包） |
| `getDataSupplier(protocol, param, value)` | **核心方法**：从协议报文中解析出设备采集数据，返回 `DataSupplier` |
| `getComponent()` | 获取已创建的网络组件 |
| `createComponent(config)` | 创建网络组件（启动端口监听或建立连接） |
| `getProductConfig()` | 产品额外配置项，可选覆盖 |
| `getDeviceConfig()` | 子设备配置项，可选覆盖 |
| `getGatewayConfig()` | 网关设备配置项，可选覆盖 |
| `getNetworkConfig()` | 网络组件额外配置项，可选覆盖 |

---

## ServerProtocolSupplier

服务端协议，设备主动连接网关（如 DTU、Modbus TCP Server）。继承 `DeviceProtocolSupplier`。

额外方法：`close(String deviceSn)` — 主动关闭指定设备连接

---

## ClientProtocolSupplier

客户端协议，网关主动连接设备（如 Modbus TCP Client、OPC UA）。继承 `DeviceProtocolSupplier`。

额外方法：
- `createClient(C config)` — 创建并缓存 Iot 客户端连接
- `removeClient(Object clientKey)` — 移除客户端连接

---

## StaticProtocolSupplier（推荐继承基类）

静态协议的抽象基类，封装了组件创建、注册逻辑。**新增协议插件通常继承此类**，只需实现：

```java
// 必须实现：创建网络组件
protected abstract T doCreateComponent(C config);

// 必须实现：解析报文 → 返回 DataSupplier
public DataSupplier getDataSupplier(Protocol protocol, ProtocolModelApiInvokeParam param, Object value);

// 可选覆盖：配置变更时刷新已有组件
protected void doRefresh(T component, C config) {}

// 工具方法：对原始值执行精度/增益/类型转换
protected Object dataResolver(UpModelAttr attr, Object value);
```

子接口：

| 接口 | 说明 |
|------|------|
| `StaticServerProtocolSupplier` | 静态服务端协议基类（组合 `StaticProtocolSupplier` + `ServerProtocolSupplier`） |
| `StaticClientProtocolSupplier` | 静态客户端协议基类（组合 `StaticProtocolSupplier` + `ClientProtocolSupplier`） |
| `DirectStaticServerProtocolSupplier` | 直连+静态+服务端 组合基类 |
| `DynamicProtocolSupplier` | 动态协议标记接口（协议可动态创建多个组件实例） |
| `BatchProtocolSupplier` | 批量采集协议标记接口 |

---

## DirectProtocolHandle（直连上报处理）

设备直连上报数据时的协议处理接口（如 MQTT 设备主动上报），继承自 `ProtocolHandle`。
框架已在 `handle()` 中实现数据解析→发布完整流程，插件只需实现：

```java
// 处理报文，返回响应
Object doHandle(T protocol);
```

---

## BatchEventModelApi（批量点位读取）

支持批量读取点位的协议实现（如 Modbus 批量读寄存器）：

| 方法 | 说明 |
|------|------|
| `resolver(points)` | 将点位列表按地址分组合并优化 |
| `invoke(arg, points, consumer)` | 执行批量读取，结果通过 consumer 回调 |

---

## DataValueResolver（数据解析器）

自定义原始值解析逻辑，注册为 Spring Bean 后可通过 `UpModelAttr.resolver` 字段引用。

| 方法 | 说明 |
|------|------|
| `name()` | 解析器唯一名称 |
| `resolver(attr, value)` | 执行解析，返回处理后的值 |

---

## ProtocolModel（协议模型）

包路径：`com.iteaj.framework.spi.iot.protocol`

`DeviceProtocolSupplier.getProtocol()` 的返回值，定义协议的完整静态结构（接口类型）。
内置实现 `DefaultProtocolModel`，插件中通常直接使用此类构建协议模型。

### ProtocolModel 接口方法

| 方法 | 说明 |
|------|------|
| `getCode()` | 协议代码（全局唯一，如 `modbus_tcp`） |
| `getRemark()` | 协议说明 |
| `getType()` | 使用的传输协议（`TransportProtocol` 枚举） |
| `getCtrlMode()` | 控制方式（`CtrlMode` 枚举：`COMMON` 常用控制 / `POINT` 点位控制） |
| `getAttrs()` | 协议属性 Map（key = `field`，value = `ProtocolModelAttr`，`LinkedHashMap` 有序） |
| `getApis()` | 协议接口 Map（key = `code`，value = `AbstractProtocolModelApi`，`LinkedHashMap` 有序） |
| `getAttr(field)` | 获取指定属性 |
| `getApi(code)` | 获取指定接口 |

### DefaultProtocolModel 构建方式

```java
DefaultProtocolModel model = new DefaultProtocolModel(
    "protocol_code",       // 协议码
    CtrlMode.COMMON,       // 控制方式
    TransportProtocol.TCP  // 传输协议
);
model.addModelAttr(new ProtocolModelAttr("field", "属性名", DataType.t_int, AttrType.R));
model.addModelApi(new MyFuncApi("api_code", "接口名"));
```

### ProtocolModelAttr（协议属性）

| 字段 | 说明 |
|------|------|
| `field` | 属性代码（唯一标识，与 `SignalOrFieldValue.signalOrField` 对应） |
| `name` | 属性名称 |
| `dataType` | 数据类型（`DataType` 枚举） |
| `attrType` | 读写方式（`AttrType` 枚举：`R` 只读 / `W` 只写 / `RW` 读写） |
| `remark` | 属性说明 |

---

## ProtocolModelApi（协议接口）

包路径：`com.iteaj.framework.spi.iot.protocol`

协议接口代表协议支持的一种操作（采集/控制），对应物模型中的功能或事件。
平台通过调用 `invoke()` 触发具体的设备通信请求。

### ProtocolModelApi 接口方法

| 方法 | 说明 |
|------|------|
| `getCode()` | 接口代码（在协议内唯一） |
| `getName()` | 接口名称 |
| `getRemark()` | 接口说明 |
| `getType()` | 接口类型（`ProtocolApiType` 枚举：`func` 功能 / `event` 事件） |
| `getTriggerMode()` | 触发方式（`TriggerMode` 枚举：`passive` 被动/平台主动请求 / `active` 主动/设备自主上报） |
| `getProtocolModel()` | 所属协议模型 |
| `invoke(arg, result)` | 执行接口调用，结果通过 `Consumer<InvokeResult>` 回调 |

### 抽象实现类

插件需继承以下抽象类之一，实现具体的报文构建和调用逻辑：

| 抽象类 | 类型 | 默认触发方式 | 说明 |
|--------|------|------------|------|
| `AbstractFuncProtocolModelApi` | `func` 功能 | `passive`（平台主动请求） | 用于主动采集/控制，如读寄存器、下发指令 |
| `AbstractEventProtocolModelApi` | `event` 事件 | `active`（设备主动上报） | 用于设备自主上报，如告警、状态变化 |

需要实现的抽象方法：

```java
// 根据调用参数构建待发送的协议报文
protected abstract Protocol buildProtocol(ProtocolModelApiInvokeParam arg);

// 执行协议通信，调用完成后通过 result 回调通知结果
protected abstract void doInvoke(Protocol protocol, ProtocolModelApiInvokeParam arg, Consumer<InvokeResult> result);
```

`AbstractFuncProtocolModelApi` 额外字段：
- `funcType`（`FuncType` 枚举：`R` 读 / `W` 写）— 调用 `buildRead()` / `buildWrite()` 设置

### upConfig / downConfig（接口配置）

`AbstractProtocolModelApi` 持有上下行配置 Map（`ProtocolModelApiConfig`），描述接口涉及的属性字段及方向：
- `upConfig`：上行字段配置（设备→平台，采集方向）
- `downConfig`：下行字段配置（平台→设备，控制方向）
- 通过 `addUpConfig(config)` / `addDownConfig(config)` 添加

---

## ProtocolSupplierManager（协议注册管理器）

单例，`StaticProtocolSupplier.createComponent()` 会自动注册，插件一般无需直接操作。

| 方法 | 说明 |
|------|------|
| `register(supplier)` | 按协议码注册协议 |
| `get(code)` | 按协议码获取协议提供 |
| `suppliers()` | 获取所有已注册协议列表 |
| `getDataSupplier(protocol)` | 从报文中获取采集数据（含缓存） |
| `load(jarFileOrDir)` | 从 Jar 动态加载协议（SPI 机制） |
