# 设备驱动模板文件使用说明

## 📚 模板文件清单

| 模板文件 | 用途 | 使用场景 | 必须使用 |
|---------|------|---------|---------|
| [StaticClientProtocolSupplierTemplate.java](StaticClientProtocolSupplierTemplate.java) | 静态客户端协议提供 | 网关主动连接设备(TCP Client、MQTT Client等) | ✅ 是 |
| [StaticServerProtocolSupplierTemplate.java](StaticServerProtocolSupplierTemplate.java) | 静态服务端协议提供 | 设备主动连接网关(TCP Server、UDP Server等) | ✅ 是 |
| [FuncProtocolModelApiTemplate.java](FuncProtocolModelApiTemplate.java) | 功能接口实现 | 平台主动采集/控制(读寄存器、下发指令等) | ✅ 是 |
| [EventProtocolModelApiTemplate.java](EventProtocolModelApiTemplate.java) | 事件接口实现 | 设备主动上报(告警、状态变化等) | ✅ 是 |
| [DataParserTemplate.java](DataParserTemplate.java) | 数据解析示例 | 解析设备报文数据(参考用) | ⚠️ 参考 |

## ⚠️ 重要声明

### 🚨 生成驱动时必须严格遵守

**所有驱动代码生成必须严格遵循本目录下的模板文件！**

具体要求：

1. **✅ 必须保留的结构**
   - 所有方法签名(名称、参数、返回值)
   - 所有属性定义
   - 构造函数
   - 类继承关系
   - 注解(如@Component)

2. **✅ 必须保留的注释**
   - 类级Javadoc注释
   - 方法级Javadoc注释
   - 重要代码块的说明注释
   - TODO标记的提示注释

3. **✅ 必须填充的内容**
   - 所有`{协议简称}`占位符
   - 所有`{协议名称}`占位符
   - 所有`TODO:`标记的代码
   - 所有示例中的实际实现

4. **❌ 不得删除的内容**
   - 任何模板中的方法(除非明确标注"可选覆盖")
   - 任何模板中的属性
   - 任何Javadoc注释
   - 任何方法签名中的参数

5. **❌ 不得修改的内容**
   - 方法的访问修饰符(public/protected/private)
   - 方法的参数类型和顺序
   - 方法的返回值类型
   - 类的继承关系

## 📖 各模板详细说明

### 1. StaticClientProtocolSupplierTemplate.java

**用途**: 静态客户端协议提供类模板

**适用场景**:
- 网关主动连接设备
- Modbus TCP Client
- OPC UA Client
- MQTT Client

**核心方法**:

| 方法 | 必须实现 | 说明 |
|------|---------|------|
| GatewayMetas | ✅ 必须 | 网关基本配置参数定义(主机、端口等) |
| 构造函数 | ✅ 必须 | 初始化协议码、传输协议、描述 |
| getDesc() | ✅ 必须 | 返回协议描述 |
| getVersion() | ✅ 必须 | 返回协议版本 |
| doCreateComponent() | ✅ 必须 | 创建网络组件 |
| doCreateClient() | ✅ 必须 | 创建设备客户端 |
| createFuncApi() | ✅ 必须 | 创建功能接口 |
| createEventApi() | ✅ 必须 | 创建事件接口 |
| getGatewayConfig() | ✅ 必须 | 返回网关配置(通常返回GatewayMetas) |
| getProductConfig() | ⚠️ 可选 | 产品额外配置(新增产品物模型时显示) |
| getDeviceConfig() | ⚠️ 可选 | 子设备额外配置(配置网关子设备时显示) |
| doRefresh() | ⚠️ 可选 | 刷新组件配置 |

**生成步骤**:
1. 复制模板文件
2. 替换所有`{协议简称}`为实际简称(如ModbusTcp)
3. 替换所有`{协议名称}`为实际名称(如Modbus Tcp)
4. 实现所有TODO标记的方法
5. 配置GatewayMetas参数(基本网络配置)
6. 实现doCreateClient()中的连接逻辑
7. 根据需要覆盖getProductConfig()、getDeviceConfig()

**参考示例**: `iboot-plugin/protocol-modbus/modbus-tcp/ModbusTcpProtocolSupplier.java`

---

### 2. StaticServerProtocolSupplierTemplate.java

**用途**: 静态服务端协议提供类模板

**适用场景**:
- 设备主动连接网关
- Modbus TCP Server
- DTU接入
- TCP Server

**核心方法**:

| 方法 | 必须实现 | 说明 |
|------|---------|------|
| GatewayMetas | ✅ 必须 | 网关基本配置参数定义 |
| 构造函数 | ✅ 必须 | 初始化协议码、传输协议、描述 |
| getDesc() | ✅ 必须 | 返回协议描述 |
| getVersion() | ✅ 必须 | 返回协议版本 |
| doCreateComponent() | ✅ 必须 | 创建服务端组件 |
| createFuncApi() | ✅ 必须 | 创建功能接口 |
| createEventApi() | ✅ 必须 | 创建事件接口 |
| getGatewayConfig() | ✅ 必须 | 返回网关配置(通常返回GatewayMetas) |
| close() | ⚠️ 可选 | 关闭设备连接 |
| getProductConfig() | ⚠️ 可选 | 产品额外配置 |
| getDeviceConfig() | ⚠️ 可选 | 子设备额外配置 |

**与Client模式的区别**:
- Client: 网关主动连接设备 → 需要doCreateClient()
- Server: 设备主动连接网关 → 不需要doCreateClient()
- Server: 多了close()方法，可主动关闭设备连接

**生成步骤**:
1. 复制模板文件
2. 替换所有占位符
3. 实现所有TODO标记的方法
4. 配置监听端口等参数

---

### 3. FuncProtocolModelApiTemplate.java

**用途**: 功能接口实现类模板

**适用场景**:
- 平台主动读取设备数据
- 平台主动下发控制指令
- 读/写寄存器
- 参数配置

**核心方法**:

| 方法 | 必须实现 | 说明 |
|------|---------|------|
| 构造函数 | ✅ 必须 | 初始化接口代码、名称、说明 |
| buildProtocol() | ✅ 必须 | 构建要发送的协议报文 |
| doInvoke() | ✅ 必须 | 执行实际的通信操作 |

**功能类型**:
- `FuncType.R` - 读操作(采集数据)
- `FuncType.W` - 写操作(控制设备)

**数据流向**:
```
平台发起请求 → buildProtocol()构建报文 → doInvoke()发送报文
→ 设备响应 → 解析响应 → 回调结果 → 更新设备状态
```

**生成步骤**:
1. 复制模板文件
2. 替换占位符
3. 实现buildProtocol()：根据协议格式构建报文
4. 实现doInvoke()：发送报文并接收响应
5. 添加辅助方法(如buildReadCommand、buildWriteCommand)

**关键点**:
- 读操作和写操作可能需要构建不同的报文
- 注意处理通信超时
- 正确处理异常并回调失败结果

---

### 4. EventProtocolModelApiTemplate.java

**用途**: 事件接口实现类模板

**适用场景**:
- 设备主动上报告警
- 设备状态变化通知
- 设备定期上报数据
- 设备故障通知

**核心方法**:

| 方法 | 必须实现 | 说明 |
|------|---------|------|
| 构造函数 | ✅ 必须 | 初始化接口代码、名称、说明 |
| buildProtocol() | ✅ 必须 | 通常返回null(不需要发送报文) |
| doInvoke() | ✅ 必须 | 处理事件(通常直接返回成功) |

**与功能接口的区别**:
- 功能接口: 平台主动请求 → 需要构建报文和通信
- 事件接口: 设备主动上报 → 通常不需要发送报文

**数据流向**:
```
设备上报报文 → ProtocolSupplier.getDataSupplier()解析
→ 发布采集数据 → 触发事件接口 → doInvoke()被调用
```

**生成步骤**:
1. 复制模板文件
2. 替换占位符
3. buildProtocol()通常返回null
4. doInvoke()直接返回成功或处理业务逻辑

**常见事件类型**:
- alarm - 告警事件
- status - 状态变化
- report - 定期上报
- fault - 故障事件

---

### 5. DataParserTemplate.java

**用途**: 数据解析示例模板(参考用)

**说明**:
- 此模板不是完整的类，而是展示如何实现数据解析
- 实际解析逻辑写在ProtocolSupplier.getDataSupplier()方法中
- 参考此模板实现报文解析

**支持的报文类型**:
- 二进制报文(byte[]) - TCP、UDP等
- 字符串报文(String) - HTTP、WebSocket等
- JSON报文(JSONObject) - MQTT等

**解析步骤**:
1. 识别报文类型
2. 根据协议格式解析
3. 提取各个属性值
4. 构建DataSupplier
5. 调用addData()添加数据
6. 返回DataSupplier

**关键点**:
- 注意字节序(大端/小端)
- 使用`& 0xFF`将byte转为无符号int
- 校验报文完整性
- 处理异常情况

---

## 📝 模板占位符说明

### 必须替换的占位符

| 占位符 | 说明 | 示例 |
|--------|------|------|
| `{协议简称}` | 协议简称(英文+大写驼峰) | ModbusTcp、OpcUa、Mqtt |
| `{协议名称}` | 协议全称(中文) | Modbus Tcp、OPC UA、MQTT |
| `{协议码}` | 协议唯一标识码 | MODBUS_TCP、OPC_UA、MQTT |
| `{协议用途描述}` | 协议用途简述 | Modbus Tcp协议通信、OPC UA数据采集 |
| `{网络配置类型}` | 网络配置类 | TcpNetworkConfig、MqttNetworkConfig |
| `{默认端口}` | 协议默认端口 | 502(Modbus)、4840(OPC UA)、1883(MQTT) |
| `{传输协议类型}` | 传输协议枚举 | TCP、MQTT、UDP、HTTP、WEBSOCKET、SERIAL |
| `{作者}` | 开发者名称 | 开发者姓名 |

### 占位符替换示例

```java
// 模板中的占位符:
public class {协议简称}ProtocolSupplier extends StaticClientProtocolSupplier<
        NetworkConfigImpl.{网络配置类型},
        {协议简称}ClientComponent,
        {协议简称}Client> {
    
    public {协议简称}ProtocolSupplier() {
        super("{协议码}", TransportProtocol.{传输协议类型}, "用于{协议用途描述}");
    }
    
    @Override
    public String getDesc() {
        return "{协议名称}协议提供";
    }
}

// Modbus TCP实际代码:
public class ModbusTcpProtocolSupplier extends StaticClientProtocolSupplier<
        NetworkConfigImpl.TcpNetworkConfig,
        ModbusTcpClientComponent,
        ModbusTcpClient> {
    
    public ModbusTcpProtocolSupplier() {
        super("MODBUS_TCP", TransportProtocol.TCP, "用于Modbus Tcp协议通信");
    }
    
    @Override
    public String getDesc() {
        return "Modbus Tcp协议提供";
    }
}
```

---

## ⚙️ 设备驱动配置方法说明

### 三个配置方法的区别

设备驱动提供三个配置方法，用于在管理端不同场景下显示额外配置项：

| 配置方法 | 使用场景 | 显示位置 | 默认值 | 示例 |
|---------|---------|---------|--------|------|
| **getProductConfig()** | 新增产品物模型 | 产品配置页 | EMPTY_CONFIG | 产品特定的通信参数 |
| **getDeviceConfig()** | 配置网关子设备 | 子设备配置页 | EMPTY_CONFIG | Modbus从机站号、数据格式 |
| **getGatewayConfig()** | 新增网关设备 | 网关设备配置页 | GatewayMetas | 心跳间隔、QoS等级 |

### 配置层次关系

```
平台管理端
├── 产品管理
│   └── 新增产品物模型
│       └── 显示 getProductConfig() 配置项
│
├── 设备管理
│   ├── 新增网关设备
│   │   ├── 显示 GatewayMetas (基本配置)
│   │   └── 显示 getGatewayConfig() (额外配置)
│   │
│   └── 配置网关子设备
│       └── 显示 getDeviceConfig() 配置项
```

### 1. getProductConfig() - 产品配置

**使用场景**:
- 在管理端新增产品物模型时显示
- 产品级别的通用配置
- 所有该产品的设备共享此配置

**常见配置项**:
- 产品特定的通信参数
- 产品特定的数据格式
- 产品特定的协议参数

### 2. getDeviceConfig() - 子设备配置

**使用场景**:
- 在管理端配置网关下的子设备时显示
- 子设备级别的特定配置
- 每个子设备可以有不同配置

**典型示例 - Modbus从机配置**:
```java
@Override
public ParamMeta[] getDeviceConfig() {
    return new ParamMeta[]{
        ParamMeta.buildRequiredNumber("slaveId", "从机站号", 1, "Modbus从机地址(1-247)"),
        ParamMeta.buildSelect("dataFormat", "数据格式", "ABCD",
            new String[]{"ABCD", "BADC", "CDAB", "DCBA"}, "寄存器字节序"),
        ParamMeta.buildNumber("readInterval", "读取间隔(ms)", 100, "连续读取间隔")
    };
}
```

**配置项说明**:
- `slaveId`: 从机站号，每个Modbus从设备唯一
- `dataFormat`: 数据格式(ABCD/DCBA)，决定寄存器字节序
- `readInterval`: 采集间隔，可针对不同子设备设置不同频率

### 3. getGatewayConfig() - 网关设备配置

**使用场景**:
- 在管理端新增网关设备时显示
- 与GatewayMetas配合使用
- 返回协议特定的额外配置

**与GatewayMetas的区别**:

| 配置 | 内容 | 必须实现 | 示例 |
|------|------|---------|------|
| **GatewayMetas** | 基本网络配置 | ✅ 必须 | 主机、端口、超时 |
| **getGatewayConfig()** | 额外协议配置 | ⚠️ 可选 | 心跳间隔、QoS、重试次数 |

### 何时需要覆盖这些方法

| 场景 | 需要覆盖 | 方法 |
|------|---------|------|
| 协议所有设备使用相同配置 | ❌ 不需要 | 都使用默认EMPTY_CONFIG |
| 产品级别有特定配置 | ✅ 需要 | getProductConfig() |
| 子设备有特定配置 | ✅ 需要 | getDeviceConfig() |
| 网关设备有额外配置 | ✅ 需要 | getGatewayConfig() |

**常见情况**:
- **简单协议**: 只需实现GatewayMetas，其他返回EMPTY_CONFIG
- **Modbus协议**: 需要实现getDeviceConfig()(从机站号、数据格式)
- **MQTT协议**: 可能需要实现getGatewayConfig()(clientId、QoS)
- **复杂协议**: 可能需要实现所有三个配置方法

---

## 🚀 使用流程

### 完整驱动开发流程

```
1. 选择协议类型
   ├─ 客户端模式(网关主动连接设备) → StaticClientProtocolSupplierTemplate
   └─ 服务端模式(设备主动连接网关) → StaticServerProtocolSupplierTemplate

2. 创建ProtocolSupplier
   ├─ 复制对应模板
   ├─ 替换所有占位符
   ├─ 实现所有TODO方法
   └─ 添加@DataParserTemplate中的解析逻辑

3. 创建FuncProtocolModelApi
   ├─ 复制FuncProtocolModelApiTemplate
   ├─ 替换占位符
   ├─ 实现buildProtocol()
   └─ 实现doInvoke()

4. 创建EventProtocolModelApi
   ├─ 复制EventProtocolModelApiTemplate
   ├─ 替换占位符
   ├─ buildProtocol()返回null(通常)
   └─ 实现doInvoke()

5. 集成测试
   ├─ 编译驱动模块
   ├─ 配置设备连接
   ├─ 测试数据采集
   └─ 测试设备控制
```

---

## ⚡ 快速检查清单

生成驱动后，必须检查以下项：

### ProtocolSupplier检查

- [ ] 添加了@Component注解
- [ ] 继承了正确的基类(StaticClientProtocolSupplier或StaticServerProtocolSupplier)
- [ ] 实现了所有必须的方法
- [ ] GatewayMetas配置正确
- [ ] 构造函数参数正确
- [ ] getDesc()返回描述
- [ ] getVersion()返回版本号
- [ ] doCreateComponent()创建组件
- [ ] doCreateClient()创建客户端(Client模式)
- [ ] createFuncApi()返回功能接口
- [ ] createEventApi()返回事件接口
- [ ] getGatewayConfig()返回配置
- [ ] getDataSupplier()实现数据解析

### FuncProtocolModelApi检查

- [ ] 继承了AbstractFuncProtocolModelApi
- [ ] 构造函数调用super(code, name, remark)
- [ ] buildProtocol()构建正确的报文
- [ ] doInvoke()实现通信逻辑
- [ ] 正确处理读操作(R)和写操作(W)
- [ ] 正确处理异常情况
- [ ] 回调结果使用result.accept()

### EventProtocolModelApi检查

- [ ] 继承了AbstractEventProtocolModelApi
- [ ] 构造函数调用super(code, name, remark)
- [ ] buildProtocol()返回null(通常)
- [ ] doInvoke()实现事件处理
- [ ] 正确处理业务逻辑

### 通用检查

- [ ] 所有占位符已替换
- [ ] 所有TODO已实现
- [ ] 所有Javadoc注释保留
- [ ] 没有删除模板中的方法
- [ ] 没有修改方法签名
- [ ] 编译无错误
- [ ] 符合协议规范

---

## 📊 模板文件结构对比

### 客户端 vs 服务端

| 特性 | Client模式 | Server模式 |
|------|-----------|-----------|
| **基类** | StaticClientProtocolSupplier | StaticServerProtocolSupplier |
| **连接方式** | 网关主动连接设备 | 设备主动连接网关 |
| **doCreateClient()** | ✅ 必须实现 | ❌ 不需要 |
| **close()** | ❌ 不需要 | ⚠️ 可选实现 |
| **泛型参数** | 3个(C, T, K) | 2个(C, T) |
| **适用协议** | Modbus TCP Client、OPC UA | Modbus TCP Server、DTU |

### 功能接口 vs 事件接口

| 特性 | Func接口 | Event接口 |
|------|---------|----------|
| **基类** | AbstractFuncProtocolModelApi | AbstractEventProtocolModelApi |
| **触发方式** | passive(平台主动) | active(设备主动) |
| **类型** | ProtocolApiType.func | ProtocolApiType.event |
| **buildProtocol()** | 必须构建报文 | 通常返回null |
| **doInvoke()** | 实现通信逻辑 | 处理事件 |
| **用途** | 读数据、写数据 | 告警、状态上报 |

---

## 🔗 相关资源

- **设备驱动开发流程**: [driver-dev-flow.md](../driver-dev-flow.md)
- **SPI接口文档**: `spi-iot-data-model.md`
- **数据服务文档**: `spi-iot-data-services.md`
- **事件监听文档**: `spi-iot-event-listener.md`
- **示例驱动**: 
  - Modbus TCP: `iboot-plugin/protocol-modbus/modbus-tcp/`
  - MQTT: `iboot-plugin/protocol-mqtt/`

---

## ⚠️ 注意事项

1. **模板是强制性的**
   - 生成驱动时必须严格遵循模板
   - 不得随意删除或修改模板结构
   - 只能在TODO位置填充实现

2. **注释是必须的**
   - 所有方法必须保留Javadoc注释
   - 重要代码块必须有说明注释
   - 注释有助于理解和维护

3. **命名要规范**
   - 协议简称使用英文+大写驼峰
   - 协议码使用大写+下划线
   - 类名要有明确的语义

4. **测试要充分**
   - 编译无错误
   - 连接测试通过
   - 数据采集正常
   - 设备控制正常
   - 异常处理完善

5. **文档要完整**
   - 保留所有模板注释
   - 添加必要的业务注释
   - 更新README(如需要)

---

## 📞 问题反馈

如果在使用模板过程中遇到问题：
1. 查看模板中的详细注释
2. 参考示例驱动代码
3. 查阅相关文档
4. 联系架构师或技术支持

---

**最后更新**: 2026-04-11  
**文档版本**: v1.0  
**维护者**: AIoTS架构团队
