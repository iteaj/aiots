# 设备驱动开发流程

> ⚠️ **重要声明**: 所有驱动代码生成必须严格遵循 `references/driver/` 目录下的模板文件！
> - 不得删除模板中的任何方法和属性
> - 不得修改方法签名和类继承关系
> - 必须在TODO标记处填充实现
> - 必须保留所有Javadoc注释
> - 详见 [模板使用说明](TEMPLATE_USAGE.md)

## 📋 概述

设备驱动是物联网平台与物理设备通信的桥梁，通过实现Framework定义的SPI接口，完成设备数据采集、指令下发、协议解析等功能。

## 🏗️ 架构设计

### 接口层次结构

```
DeviceProtocolSupplier (顶层接口)
├── ServerProtocolSupplier (服务端协议接口)
│   └── StaticServerProtocolSupplier (静态服务端抽象类)
│       └── DirectStaticServerProtocolSupplier (直连服务端抽象类)
│
├── ClientProtocolSupplier (客户端协议接口)
│   └── StaticClientProtocolSupplier (静态客户端抽象类)
│
├── StaticProtocolSupplier (静态协议抽象基类)
│   └── [各种具体协议实现]
│
└── DynamicProtocolSupplier (动态协议抽象类)

标记接口:
├── BatchProtocolSupplier (支持批量读取)
└── DirectProtocolHandle (直连上报处理)
```

## 🔌 顶级接口设计

### 1. DeviceProtocolSupplier (核心接口)

**位置**: `com.iteaj.framework.spi.iot.DeviceProtocolSupplier`

**说明**: 所有设备驱动必须实现的顶层接口

**核心方法**:

| 方法 | 返回值 | 说明 | 必须实现 |
|------|--------|------|---------|
| `getDesc()` | String | 协议描述 | ✅ |
| `getVersion()` | String | 协议版本 | ✅ |
| `getProtocol()` | ProtocolModel | 返回协议模型(属性、指令) | ✅ |
| `getGatewayType()` | GatewayType | 网关类型(云网关/边缘网关) | ✅ |
| `getConnectionType()` | ConnectionType | 连接类型(Server/Client) | ✅ |
| `getImplMode()` | ProtocolImplMode | 实现方式(Internal/Jar) | ✅ |
| `getDataSupplier()` | DataSupplier | **核心**: 解析报文返回采集数据 | ✅ |
| `getComponent()` | T | 获取已创建的网络组件 | ✅ |
| `createComponent()` | T | 创建网络组件 | ✅ |

**可选配置方法**:

| 方法 | 返回值 | 说明 |
|------|--------|------|
| `getProductConfig()` | ParamMeta[] | 产品额外配置 |
| `getDeviceConfig()` | ParamMeta[] | 子设备配置 |
| `getGatewayConfig()` | ParamMeta[] | 网关设备配置 |
| `getNetworkConfig()` | ParamMeta[] | 网络组件额外配置 |

### 2. ServerProtocolSupplier (服务端接口)

**位置**: `com.iteaj.framework.spi.iot.ServerProtocolSupplier`

**说明**: 设备主动连接网关的场景(如DTU、Modbus TCP Server)

**额外方法**:
- `close(String deviceSn)` - 主动关闭指定设备连接

### 3. ClientProtocolSupplier (客户端接口)

**位置**: `com.iteaj.framework.spi.iot.ClientProtocolSupplier`

**说明**: 网关主动连接设备的场景(如Modbus TCP Client、OPC UA)

**额外方法**:
- `createClient(C config)` - 创建并缓存IoT客户端连接
- `removeClient(Object clientKey)` - 移除客户端连接

## 📦 抽象实现类

### 1. StaticProtocolSupplier (静态协议基类)

**位置**: `com.iteaj.framework.spi.iot.StaticProtocolSupplier`

**静态 vs 动态说明**:

| 类型 | 含义 | 实现方式 | 使用场景 |
|------|------|---------|----------|
| **静态协议** | 驱动通过编码实现，协议逻辑写在代码中 | 继承StaticProtocolSupplier，编写Java代码 | 复杂协议、需要精细控制、主流方式 |
| **动态协议** | 驱动通过管理端配置实现，无需编码 | 在管理端配置协议规则、解析脚本 | 简单协议、快速配置、未来规划 |

**当前状态**: ✅ **项目统一使用静态协议方式**，动态协议为未来扩展预留

**特点**:
- 封装组件创建、注册逻辑
- 管理单一组件实例
- 提供数据解析工具方法
- 协议逻辑通过代码实现，可控性强

**必须实现**:
```java
// 创建网络组件
protected abstract T doCreateComponent(C config);

// 解析报文 → 返回采集数据
public abstract DataSupplier getDataSupplier(Protocol protocol, 
                                             ProtocolModelApiInvokeParam param, 
                                             Object value);
```

**可选覆盖**:
```java
// 配置变更时刷新组件
protected void doRefresh(T component, C config) {}

// 自定义数据解析(精度/增益/类型转换)
protected Object dataResolver(UpModelAttr attr, Object value)
```

### 2. StaticServerProtocolSupplier (静态服务端)

**位置**: `com.iteaj.framework.spi.iot.StaticServerProtocolSupplier`

**继承关系**: `StaticProtocolSupplier` + `ServerProtocolSupplier`

**适用场景**: 设备主动连接平台的TCP Server模式

### 3. StaticClientProtocolSupplier (静态客户端)

**位置**: `com.iteaj.framework.spi.iot.StaticClientProtocolSupplier`

**继承关系**: `StaticProtocolSupplier` + `ClientProtocolSupplier`

**适用场景**: 平台主动连接设备的TCP Client模式

**必须实现**:
```java
// 创建IoT客户端
protected abstract K doCreateClient(C config, T component);
```

### 4. AbstractProtocolModelApi (协议接口抽象类)

**位置**: `com.iteaj.framework.spi.iot.protocol.AbstractProtocolModelApi`

**说明**: 定义协议支持的操作(采集/控制)

**必须实现**:
```java
// 根据参数构建协议报文
protected abstract Protocol buildProtocol(ProtocolModelApiInvokeParam arg);

// 执行协议通信
protected abstract void doInvoke(Protocol protocol, 
                                 ProtocolModelApiInvokeParam arg, 
                                 Consumer<InvokeResult> result);
```

### 5. AbstractFuncProtocolModelApi (功能接口)

**位置**: `com.iteaj.framework.spi.iot.protocol.AbstractFuncProtocolModelApi`

**类型**: `func` (功能)
**触发方式**: `passive` (平台主动请求)
**用途**: 主动采集/控制，如读寄存器、下发指令

**额外字段**:
- `funcType` - `R` 读 / `W` 写

### 6. AbstractEventProtocolModelApi (事件接口)

**位置**: `com.iteaj.framework.spi.iot.protocol.AbstractEventProtocolModelApi`

**类型**: `event` (事件)
**触发方式**: `active` (设备主动上报)
**用途**: 设备自主上报，如告警、状态变化

## 🛠️ 实现步骤

### 步骤0: 理解静态协议开发模式

**静态协议** = 通过编写Java代码实现驱动逻辑

**开发流程**:
```
1. 创建驱动插件模块 (protocol-xxx)
2. 编写ProtocolSupplier (协议提供类)
3. 编写ProtocolModelApi (功能接口/事件接口)
4. 注册为Spring Bean (@Component)
5. 系统启动时自动加载
```

**优势**:
- ✅ 完整的类型检查和编译时验证
- ✅ 支持复杂协议逻辑和算法
- ✅ 便于调试和单元测试
- ✅ 性能最优

**对比动态协议(未来)**:
- 动态协议 = 管理端配置 + 脚本引擎
- 适合简单协议快速配置
- 当前项目统一使用静态方式

### 步骤1: 选择正确的基类

根据通信模式选择:

| 场景 | 继承基类 | 示例 |
|------|---------|------|
| 设备连平台(TCP Server) | `StaticServerProtocolSupplier` | DTU、Modbus TCP Server |
| 平台连设备(TCP Client) | `StaticClientProtocolSupplier` | Modbus TCP Client、OPC UA |
| 设备直连上报(MQTT) | `DirectProtocolHandle` | MQTT设备 |
| 需要批量读取 | 实现 `BatchProtocolSupplier` | Modbus批量读寄存器 |

#### 网关类型选择

在实现ProtocolSupplier时，需要通过`getGatewayType()`方法指定网关类型：

**设备直连(Direct)**:
- **定义**: 真正产生数据的设备直接连接平台
- **示例**: 传感器、智能仪表等设备直接连接平台
- **特点**: 设备本身就是数据源，直接与平台通信
- **数据流向**: 设备 → 平台
- **返回值**: `GatewayType.Direct`

**边缘网关(Edge)**:
- **定义**: 产生数据的设备通过边缘网关间接连接平台
- **示例**: PLC下挂载多个子设备，PLC本身不生产数据
- **特点**: 平台通过连接网关(如PLC)读取子设备数据
- **数据流向**: 子设备 → 边缘网关(PLC) → 平台
- **返回值**: `GatewayType.Edge`

**选择依据**:
```
如果设备直接产生数据并上报 → Direct(设备直连)
如果设备作为网关采集下级设备数据 → Edge(边缘网关)
```

**示例**:
```java
@Override
public GatewayType getGatewayType() {
    // 传感器直接连接平台 → 设备直连
    return GatewayType.Direct;
    
    // PLC采集下级设备数据 → 边缘网关
    // return GatewayType.Edge;
}
```

### 步骤2: 创建驱动模块

**位置**: `iboot-plugin/iboot-protocol/protocol-xxx/`

**目录结构**:
```
iboot-plugin/iboot-protocol/
├── pom.xml                                    # 协议插件父POM
└── protocol-xxx/                              # 具体协议模块
    ├── pom.xml                                # 模块Maven配置
    └── src/main/java/com/iteaj/iboot/plugin/protocol/xxx/
        ├── XxxProtocolSupplier.java           # 协议提供(核心)
        ├── XxxFuncProtocolModelApi.java       # 功能接口(读/写)
        ├── XxxEventProtocolModelApi.java      # 事件接口(上报)
        └── component/                         # 网络组件(可选)
            └── XxxComponent.java
```

**示例**: Modbus TCP驱动
```
iboot-plugin/iboot-protocol/protocol-modbus/
├── modbus-common/                             # Modbus公共代码
├── modbus-tcp/                                # Modbus TCP驱动
│   └── src/main/java/.../modbus/tcp/
│       ├── ModbusTcpProtocolSupplier.java
│       ├── ModbusTcpFuncProtocolModeApi.java
│       └── ModbusTcpEventProtocolModeApi.java
└── modbus-rtu/                                # Modbus RTU驱动
```

### 步骤3: 实现ProtocolSupplier

> ⚠️ **必须严格遵循模板文件**: [StaticClientProtocolSupplierTemplate.java](StaticClientProtocolSupplierTemplate.java) 或 [StaticServerProtocolSupplierTemplate.java](StaticServerProtocolSupplierTemplate.java)

**关键实现点**:

1. **网关配置参数** - 定义设备连接所需的配置项
2. **构造函数** - 初始化协议码、传输协议、描述
3. **getGatewayType()** - 指定网关类型(设备直连/边缘网关)
4. **getImplMode()** - 指定实现方式(Internal/Jar)
5. **getProtocol()** - 定义协议模型(属性、功能接口、事件接口)
6. **doCreateComponent()** - 创建网络组件
7. **doCreateClient()** - 创建设备客户端(Client模式)
8. **createFuncApi()** - 创建功能接口
9. **createEventApi()** - 创建事件接口
10. **getDataSupplier()** - **核心**: 解析报文数据

**核心方法说明**:

#### 1. getGatewayType() - 网关类型
```java
@Override
public GatewayType getGatewayType() {
    // 设备直连: 传感器、智能仪表等直接连平台
    return GatewayType.Direct;
    
    // 边缘网关: PLC采集下级设备数据
    // return GatewayType.Edge;
}
```

#### 2. getImplMode() - 实现方式
```java
@Override
public ProtocolImplMode getImplMode() {
    // 内置实现(代码在当前项目中)
    return ProtocolImplMode.Internal;
    
    // 外部Jar包实现(动态加载)
    // return ProtocolImplMode.Jar;
}
```

#### 3. getProtocol() - 协议模型
```java
@Override
public ProtocolModel getProtocol() {
    DefaultProtocolModel model = new DefaultProtocolModel(
        "MODBUS_TCP",           // 协议码
        CtrlMode.COMMON,        // 控制方式
        TransportProtocol.TCP   // 传输协议
    );
    
    // 添加属性(点位)
    model.addModelAttr(new ProtocolModelAttr(
        "temperature",          // 属性代码
        "温度",                 // 属性名称
        DataType.t_float,       // 数据类型
        AttrType.R              // 只读
    ));
    
    // 添加功能接口
    model.addModelApi(createFuncApi("read_temp", "读取温度", "读取温度寄存器"));
    
    // 添加事件接口
    model.addModelApi(createEventApi("temp_alarm", "温度告警", "温度超限告警"));
    
    return model;
}
```

#### 4. getDataSupplier() - 数据解析(最核心)
```java
@Override
public DataSupplier getDataSupplier(Protocol protocol, 
                                    ProtocolModelApiInvokeParam param, 
                                    Object value) {
    // 1. 获取设备标识
    DeviceKey deviceKey = param != null ? param.getDeviceKey() : null;
    
    // 2. 创建数据容器
    DataSupplier supplier = new DataSupplier(getProtocol().getCode(), deviceKey);
    supplier.build(new Date());
    
    // 3. 解析报文
    byte[] data = (byte[]) value;
    // TODO: 根据协议格式解析报文
    
    // 4. 添加属性值
    supplier.addValue("temp", "温度", 25.5);
    
    // 5. 返回解析结果
    return supplier;
}
```

**完整实现参考**: 请查看模板文件，所有方法都有详细的注释和示例代码。

### 步骤4: 定义协议模型

> 📖 **参考模板**: 在ProtocolSupplier模板中已包含完整的协议模型定义示例

**关键概念**:

1. **ProtocolModel** - 协议模型，包含属性、指令、事件
2. **ProtocolModelAttr** - 协议属性(点位)定义
3. **ProtocolModelApi** - 协议接口(功能/事件)定义

**常见配置**:
- 添加属性(温度、湿度、开关等)
- 添加功能接口(读数据、写数据)
- 添加事件接口(告警上报、状态变化)

**详细说明**: 请查看模板文件中的注释和示例。

### 步骤5: 实现功能接口

> ⚠️ **必须严格遵循模板文件**: [FuncProtocolModelApiTemplate.java](FuncProtocolModelApiTemplate.java)

**核心方法**:

1. **buildProtocol()** - 根据调用参数构建协议报文
2. **doInvoke()** - 执行实际的设备通信操作

**功能类型**:
- `FuncType.R` - 读操作(采集数据)
- `FuncType.W` - 写操作(控制设备)

**数据流向**:
```
平台发起请求 → buildProtocol()构建报文 → doInvoke()发送报文
→ 设备响应 → 解析响应 → 回调结果 → 更新设备状态
```

**完整实现**: 请查看模板文件，包含详细的报文构建和通信示例。

### 步骤6: 实现事件接口

> ⚠️ **必须严格遵循模板文件**: [EventProtocolModelApiTemplate.java](EventProtocolModelApiTemplate.java)

**核心概念**:

1. **事件的定义**: 事件 = 平台定义的所有采集模型属性的设备接口
2. **不仅仅是设备主动上报**: 包括寄存器采集、数据上报、告警通知等
3. **触发方式**: `active` (相对于功能接口的passive)

**事件的多样性**:
- **寄存器采集事件**: 如Modbus寄存器读取(需要构建报文和通信)
- **数据上报事件**: 如设备定时上报传感器数据(通常返回null)
- **告警事件**: 如温度超限、设备离线(可能需要确认报文)
- **状态事件**: 如开关状态、运行状态变化
- **响应事件**: 如设备对平台指令的响应数据

**与功能接口的区别**:
- 功能接口(func): 平台主动发起的采集/控制请求，触发方式passive
- 事件接口(event): 平台定义的采集模型属性接口，触发方式active
- **两者都是事件，只是触发方式不同**

**实现方式**:
- 场景1(寄存器采集): 构建读取命令 → 发送通信 → 接收响应 → 解析数据
- 场景2(数据上报): 直接返回成功(数据在getDataSupplier中解析)
- 场景3(告警确认): 构建确认报文 → 发送到设备

**完整实现**: 请查看模板文件，包含3种场景的详细示例。

## 📊 核心数据对象

### DataSupplier (采集数据容器)

**用途**: 存储解析后的设备数据

**关键方法**:
- `addValue(code, name, value)` - 添加属性值
- `build(date)` - 设置采集时间

### DeviceKey (设备标识)

**用途**: 标识设备身份

**构建方式**:
- 直连设备: `DeviceKey.build(deviceSn, null)`
- 网关子设备: `DeviceKey.build(slaveSn, gatewaySn)`

### ProtocolModel (协议模型)

**用途**: 定义协议的属性、指令、事件

**组成**:
- `ProtocolModelAttr` - 属性定义
- `ProtocolModelApi` - 接口定义(功能/事件)

## 🔧 网络配置类

> 📖 **详细说明**: 请参考ProtocolSupplier模板中的网络配置使用示例

### 内置配置类

| 配置类 | 适用协议 | 说明 |
|--------|---------|------|
| `TcpNetworkConfig` | TCP | 主机+端口+SSL |
| `MqttNetworkConfig` | MQTT | Broker地址+端口+认证 |
| `UdpNetworkConfig` | UDP | 主机+端口 |
| `HttpNetworkConfig` | HTTP | 请求地址+端口 |
| `WebSocketConfig` | WEBSOCKET | WS地址+端口 |
| `SerialConfig` | SERIAL | 串口号+波特率 |

**使用示例**: 请查看模板文件中的doCreateClient()方法实现。

### 关键枚举速查

| 枚举 | 值 | 说明 |
|------|---|------|
| `GatewayType` | `Direct` / `Gateway` | 设备直连 / 边缘网关 |
| `ConnectionType` | `Server` / `Client` | 服务端 / 客户端 |
| `ProtocolImplMode` | `Internal` / `Jar` / `Custom` | 内置 / Jar / 自定义 |
| `CtrlMode` | `COMMON` / `POINT` | 常用控制 / 点位控制 |
| `TransportProtocol` | `TCP/MQTT/UDP/HTTP/WEBSOCKET/SERIAL` | 传输协议类型 |
| `TriggerMode` | `active` / `passive` | 设备主动 / 平台主动 |
| `FuncType` | `R` / `W` | 读操作 / 写操作 |
| `ProtocolApiType` | `func` / `event` | 功能接口 / 事件接口 |

## ✅ 开发检查清单

- [ ] 选择正确的基类(Server/Client/Direct)
- [ ] 实现`doCreateComponent()`创建网络组件
- [ ] 实现`doCreateClient()`创建设备连接(Client模式)
- [ ] 实现`getDataSupplier()`解析报文数据
- [ ] 定义协议模型(属性、指令、事件)
- [ ] 实现功能接口(AbstractFuncProtocolModelApi)
- [ ] 实现事件接口(AbstractEventProtocolModelApi)
- [ ] 配置网关参数(getGatewayConfig)
- [ ] 注册为Spring Bean(@Component)
- [ ] 测试数据采集、指令下发、状态监控

## 📚 参考资源

- **框架接口**: `com.iteaj.framework.spi.iot.*`
- **模板文件**: `references/driver/` ⭐ **必须严格遵循**
  - StaticClientProtocolSupplier模板
  - StaticServerProtocolSupplier模板
  - FuncProtocolModelApi模板
  - EventProtocolModelApi模板
  - [模板使用说明](TEMPLATE_USAGE.md)
- **示例驱动**: 
  - Modbus TCP: `iboot-plugin/protocol-modbus/modbus-tcp`
  - MQTT: `iboot-plugin/protocol-mqtt`
  - OPC UA: `iboot-plugin/protocol-opcua`
- **数据模型**: `spi-iot-data-model.md`
- **数据服务**: `spi-iot-data-services.md`
- **事件监听**: `spi-iot-event-listener.md`

---

## 🔄 静态协议 vs 动态协议(未来)

### 核心概念

**静态协议** = 通过编写Java代码实现驱动逻辑  
**动态协议** = 通过管理端配置实现，无需编码(未来功能)

### 当前策略

**项目统一使用静态协议方式**，原因:
1. IoT协议通常较复杂，需要精细控制
2. 静态方式性能更优，适合高频数据采集
3. 类型安全，编译时发现问题
4. 便于版本管理和代码审查

**动态协议为未来预留**，适用场景:
1. 用户自定义简单协议
2. 快速原型验证
3. 临时性协议适配
