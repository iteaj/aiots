# 网络组件(Component)实现流程

## 📖 概述

### 核心概念

**网络组件(Component)**是真正实现设备协议交互的网络层，负责：
- 管理网络连接(Client/Server)
- 接收和发送报文
- 报文解码/编码
- 设备连接管理
- 心跳维护
- 协议路由

**设备驱动层(DeviceProtocolSupplier)**是用于将网络组件接入到平台的物模型，负责：
- 定义协议模型(属性、功能、事件)
- 构建协议报文
- 解析协议数据
- 配置管理

### 两者关系

```
设备驱动层(ProtocolSupplier)
    ↓ 使用
网络组件层(Component)
    ↓ 管理
网络连接(Client/Server)
    ↓ 通信
物理设备
```

---

## 🏗️ 组件体系架构

### 组件分类

| 类型 | 基类 | 用途 | 通信方向 |
|------|------|------|---------|
| **客户端组件** | ClientComponent | 平台主动连接设备 | 平台→设备 |
| **服务端组件** | ServerComponent | 设备主动连接平台 | 设备→平台 |

### 客户端组件继承链

```
FrameworkComponent
    ↓
ClientComponent
    ↓
LengthFieldBasedFrameClientComponent<T> (基于长度字段的解码器)
    ↓
{协议}ClientComponent (自定义实现)
```

### 服务端组件继承链

```
FrameworkComponent
    ↓
ServerComponent
    ↓
LengthFieldBasedFrameDecoderServerComponent<T> (基于长度字段的解码器)
    ↓
{协议}ServerComponent (自定义实现)
```

---

## 📝 客户端组件实现流程(ClientComponent)

### 步骤1：创建客户端组件类

```java
package com.iteaj.iboot.plugin.protocol.{协议简称}.component;

import com.iteaj.iot.AbstractProtocol;
import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.client.ClientConnectProperties;
import com.iteaj.iot.client.TcpSocketClient;
import com.iteaj.iot.client.component.LengthFieldBasedFrameClientComponent;
import com.iteaj.iot.client.protocol.ServerInitiativeProtocol;
import com.iteaj.iot.codec.filter.DecoderInterceptor;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.timeout.IdleState;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * {协议名称}客户端网络组件
 * 
 * 【说明】
 * 1. 管理到设备的TCP/UDP/MQTT等网络连接
 * 2. 负责报文解码、心跳维护、连接管理
 * 3. 继承LengthFieldBasedFrameClientComponent实现基于长度字段的报文解析
 * 
 * 【泛型参数】
 * T - 消息类型，通常是自定义的{协议}Message类
 * 
 * @author {作者}
 * @since 1.0
 */
@Component
public class {协议简称}ClientComponent extends LengthFieldBasedFrameClientComponent<{协议}Message> {

    private static final String NAME = "{协议}_CLIENT";
    private static final String DESC = "{协议}客户端网络组件";

    /**
     * 构造函数
     * 
     * 【说明】
     * 1. 调用父类构造函数，配置报文解码参数
     * 2. 设置网络连接的初始配置
     * 3. 可注册解码器拦截器
     * 
     * @param config 客户端连接配置
     */
    public {协议简称}ClientComponent(ClientConnectProperties config) {
        // 父类构造函数参数:
        // - config: 连接配置(主机、端口、超时等)
        // - maxFrameLength: 最大报文长度
        // - lengthFieldOffset: 长度字段偏移量
        // - lengthFieldLength: 长度字段长度
        super(config, Short.MAX_VALUE, 1, 2);
        
        // 可选：注册解码器拦截器
        this.setInterceptor(new DecoderInterceptor<LengthFieldBasedFrameClientComponent<{协议}Message>>() {
            @Override
            public boolean isActivation(Channel channel, 
                                       LengthFieldBasedFrameClientComponent<{协议}Message> component) {
                // 连接激活时的处理(如发送注册报文)
                // ByteBuf registerMsg = buildRegisterMessage();
                // channel.writeAndFlush(registerMsg);
                return super.isActivation(channel, component);
            }
        });
    }

    /**
     * 获取组件名称
     * 
     * 【说明】
     * 1. 返回组件的唯一标识
     * 2. 用于FrameworkManager中注册和管理
     * 3. 建议格式：协议_客户端/服务端
     * 
     * @return 组件名称
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * 获取组件描述
     * 
     * 【说明】
     * 1. 返回组件的功能描述
     * 2. 用于平台展示和调试
     * 
     * @return 组件描述
     */
    @Override
    public String getDesc() {
        return DESC;
    }

    /**
     * 根据消息获取协议处理器
     * 
     * 【说明】
     * 1. 根据消息内容(如功能码、报文类型)路由到对应的协议处理器
     * 2. 返回null表示不处理该消息
     * 3. 用于处理设备主动上报的报文
     * 
     * @param message 接收到的消息
     * @return 协议处理器，不处理返回null
     */
    @Override
    public AbstractProtocol<{协议}Message> getProtocol({协议}Message message) {
        // 示例：根据消息类型路由
        // {协议}MessageType type = message.getType();
        // if(type == {协议}MessageType.HEARTBEAT) {
        //     return {协议}HeartbeatProtocol.getInstance();
        // }
        return null;
    }

    /**
     * 获取设备主动上报的协议处理器
     * 
     * 【说明】
     * 1. 处理设备主动上报的报文
     * 2. 根据报文类型返回对应的协议处理器
     * 3. 返回null表示不处理
     * 
     * @param message 上报消息
     * @param type 协议类型
     * @return 设备主动上报协议处理器
     */
    @Override
    protected ServerInitiativeProtocol<{协议}Message> doGetProtocol({协议}Message message, 
                                                                        ProtocolType type) {
        // 示例：处理心跳报文
        // if(type == {协议}ProtocolType.HEARTBEAT) {
        //     return new {协议}ServerInitiativeProtocol(message);
        // }
        return null;
    }

    /**
     * 空闲事件处理
     * 
     * 【说明】
     * 1. 当连接空闲时触发(读空闲/写空闲)
     * 2. 通常用于发送心跳报文
     * 3. 返回的对象会被发送到设备
     * 
     * @param deviceSn 设备编号
     * @param state 空闲状态(WRITER_IDLE/READER_IDLE)
     * @return 要发送的对象(心跳报文等)，不发送返回null
     */
    @Override
    public Object idle(String deviceSn, IdleState state) {
        if(state == IdleState.WRITER_IDLE) {
            // 写空闲：平台长时间未发送数据，发送心跳
            // return {协议}Message.buildHeartbeat();
        }
        return null;
    }

    /**
     * 创建新的客户端连接
     * 
     * 【说明】
     * 1. 为每个设备创建独立的客户端连接
     * 2. 可覆盖此方法实现自定义的客户端创建逻辑
     * 3. 默认使用框架提供的TcpSocketClient
     * 
     * @param config 连接配置
     * @return IoT客户端实例
     */
    @Override
    public TcpSocketClient createNewClient(ClientConnectProperties config) {
        return super.createNewClient(config);
    }
}
```

---

## 📝 服务端组件实现流程(ServerComponent)

### 步骤1：创建服务端组件类

```java
package com.iteaj.iboot.plugin.protocol.{协议简称}.component;

import com.iteaj.iot.ProtocolType;
import com.iteaj.iot.config.ConnectProperties;
import com.iteaj.iot.server.component.LengthFieldBasedFrameDecoderServerComponent;
import com.iteaj.iot.server.protocol.ClientInitiativeProtocol;
import org.springframework.stereotype.Component;

/**
 * {协议名称}服务端网络组件
 * 
 * 【说明】
 * 1. 监听端口，接受设备连接
 * 2. 负责报文解码、连接管理、设备识别
 * 3. 继承LengthFieldBasedFrameDecoderServerComponent实现报文解析
 * 
 * 【泛型参数】
 * T - 消息类型，通常是自定义的{协议}Message类
 * 
 * @author {作者}
 * @since 1.0
 */
@Component
public class {协议简称}ServerComponent extends LengthFieldBasedFrameDecoderServerComponent<{协议}Message> {

    private static final String NAME = "{协议}_SERVER";
    private static final String DESC = "{协议}服务端网络组件";

    /**
     * 构造函数
     * 
     * 【说明】
     * 1. 调用父类构造函数，配置报文解码参数
     * 2. 设置监听端口和超时配置
     * 
     * @param connectProperties 连接配置
     */
    public {协议简称}ServerComponent(ConnectProperties connectProperties) {
        // 父类构造函数参数:
        // - connectProperties: 连接配置
        // - maxFrameLength: 最大报文长度
        // - lengthFieldOffset: 长度字段偏移量
        // - lengthFieldLength: 长度字段长度
        super(connectProperties, Short.MAX_VALUE, 1, 2);
        
        // 配置监听参数
        // config().setPort(502);  // 监听端口
        // config().setHost("0.0.0.0");  // 绑定地址
        // config().setReaderIdleTime(60);  // 读空闲超时(秒)
    }

    /**
     * 获取组件名称
     * 
     * @return 组件名称
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * 获取组件描述
     * 
     * @return 组件描述
     */
    @Override
    public String getDesc() {
        return DESC;
    }

    /**
     * 创建消息对象
     * 
     * 【说明】
     * 1. 将原始字节数组转换为自定义消息对象
     * 2. 在消息构造函数中进行报文解析
     * 3. 每接收到一个完整报文都会调用此方法
     * 
     * @param message 原始字节数组
     * @return 自定义消息对象
     */
    @Override
    public {协议}Message createMessage(byte[] message) {
        return new {协议}Message(message);
    }

    /**
     * 获取消息类
     * 
     * 【说明】
     * 1. 返回消息类的Class对象
     * 2. 用于框架反射创建消息实例
     * 
     * @return 消息类
     */
    @Override
    public Class<{协议}Message> getMessageClass() {
        return {协议}Message.class;
    }

    /**
     * 获取设备主动上报的协议处理器
     * 
     * 【说明】
     * 1. 处理设备主动上报的报文
     * 2. 根据报文类型返回对应的协议处理器
     * 3. 返回null表示不处理
     * 
     * @param message 上报消息
     * @param type 协议类型
     * @return 设备主动上报协议处理器
     */
    @Override
    protected ClientInitiativeProtocol<{协议}Message> doGetProtocol({协议}Message message, 
                                                                          ProtocolType type) {
        // 示例：根据报文类型路由
        // if(type == {协议}ProtocolType.HEARTBEAT) {
        //     return new {协议}HeartbeatProtocol(message);
        // }
        // if(type == {协议}ProtocolType.DATA_REPORT) {
        //     return new {协议}DataReportProtocol(message);
        // }
        return null;
    }
}
```

---

## 🔧 核心方法详解

### 客户端组件核心方法

| 方法 | 作用 | 调用时机 | 返回值 |
|------|------|---------|--------|
| `getName()` | 组件名称 | 注册时 | String |
| `getDesc()` | 组件描述 | 展示时 | String |
| `getProtocol(message)` | 获取协议处理器 | 接收到消息时 | AbstractProtocol<T> |
| `doGetProtocol(message, type)` | 获取设备主动上报协议 | 设备主动上报时 | ServerInitiativeProtocol<T> |
| `idle(deviceSn, state)` | 空闲事件处理 | 连接空闲时 | Object(发送的内容) |
| `createNewClient(config)` | 创建客户端连接 | 连接设备时 | TcpSocketClient |

### 服务端组件核心方法

| 方法 | 作用 | 调用时机 | 返回值 |
|------|------|---------|--------|
| `getName()` | 组件名称 | 注册时 | String |
| `getDesc()` | 组件描述 | 展示时 | String |
| `createMessage(message)` | 创建消息对象 | 接收到报文时 | T |
| `getMessageClass()` | 获取消息类 | 反射时 | Class<T> |
| `doGetProtocol(message, type)` | 获取设备主动上报协议 | 设备主动上报时 | ClientInitiativeProtocol<T> |

---

## 📊 组件生命周期

### 客户端组件生命周期

```
1. Spring初始化
   ↓
2. 构造函数执行
   - 配置解码参数
   - 注册拦截器
   ↓
3. FrameworkManager注册
   - 调用getName()获取组件名
   - 注册到组件管理器
   ↓
4. 设备连接时
   - createNewClient(config)创建连接
   - 建立TCP/UDP/MQTT连接
   ↓
5. 运行阶段
   - 接收报文 → getProtocol() → 协议处理
   - 空闲事件 → idle() → 发送心跳
   - 设备上报 → doGetProtocol() → 上报处理
   ↓
6. 设备断开时
   - 清理连接资源
   - 触发重连(如配置)
```

### 服务端组件生命周期

```
1. Spring初始化
   ↓
2. 构造函数执行
   - 配置监听参数
   - 配置解码参数
   ↓
3. FrameworkManager注册
   - 调用getName()获取组件名
   - 注册到组件管理器
   ↓
4. 启动监听
   - 绑定端口
   - 开始接受连接
   ↓
5. 设备连接时
   - 接受新连接
   - 识别设备(根据报文)
   ↓
6. 运行阶段
   - 接收报文 → createMessage() → 创建消息对象
   - 报文路由 → doGetProtocol() → 协议处理
   - 空闲事件 → 断开超时连接
   ↓
7. 设备断开时
   - 清理连接资源
   - 更新设备状态
```

---

## 🎯 报文解码配置

### 基于长度字段的解码器

大多数协议使用"长度字段+报文内容"的格式：

```
| 长度字段(2字节) | 报文内容(N字节) |
|----------------|----------------|
|     0x0010     |   报文数据...   |
```

### 配置参数说明

```java
super(config, maxFrameLength, lengthFieldOffset, lengthFieldLength);
```

| 参数 | 说明 | 示例 |
|------|------|------|
| config | 连接配置 | 主机、端口、超时 |
| maxFrameLength | 最大报文长度 | Short.MAX_VALUE (32767) |
| lengthFieldOffset | 长度字段偏移量 | 0(从第0字节开始) |
| lengthFieldLength | 长度字段长度 | 2(2字节表示长度) |

### 常见协议配置

| 协议 | 格式 | maxFrameLength | offset | length |
|------|------|----------------|--------|--------|
| Modbus TCP | [事务ID][协议ID][长度][单元ID][功能码][数据] | 256 | 4 | 2 |
| 自定义协议1 | [长度][报文] | 1024 | 0 | 2 |
| 自定义协议2 | [帧头][长度][报文][校验] | 2048 | 2 | 2 |

---

## 🔗 组件与驱动的关联

### ProtocolSupplier中使用Component

```java
@Component
public class {协议}ProtocolSupplier extends StaticClientProtocolSupplier<
        NetworkConfigImpl.TcpNetworkConfig,
        {协议}ClientComponent,  // 关联客户端组件
        {协议}Client> {

    /**
     * 创建客户端网络组件
     * 
     * 【说明】
     * 1. 此方法返回组件实例，由FrameworkManager管理
     * 2. FrameworkManager根据组件配置创建网络连接
     * 3. 不要在此方法中启动连接，由框架统一管理
     */
    @Override
    protected {协议}ClientComponent doCreateComponent(NetworkConfigImpl.TcpNetworkConfig config) {
        // 创建连接配置
        ClientConnectProperties connectConfig = new ClientConnectProperties(
            config.getHost(),
            config.getPort(),
            null,
            null
        );
        connectConfig.setReaderIdleTime(config.getTimeout());
        
        // 创建并返回组件实例
        return new {协议}ClientComponent(connectConfig);
    }
}
```

---

## 📝 完整实现清单

### 客户端组件实现清单

- [ ] 继承`LengthFieldBasedFrameClientComponent<T>`
- [ ] 添加`@Component`注解
- [ ] 实现构造函数(配置解码参数)
- [ ] 实现`getName()`方法
- [ ] 实现`getDesc()`方法
- [ ] 实现`getProtocol(message)`方法(可选)
- [ ] 实现`doGetProtocol(message, type)`方法(可选)
- [ ] 实现`idle(deviceSn, state)`方法(可选)
- [ ] 实现`createNewClient(config)`方法(可选)
- [ ] 创建消息类`{协议}Message`
- [ ] 在ProtocolSupplier中关联组件

### 服务端组件实现清单

- [ ] 继承`LengthFieldBasedFrameDecoderServerComponent<T>`
- [ ] 添加`@Component`注解
- [ ] 实现构造函数(配置监听和解码参数)
- [ ] 实现`getName()`方法
- [ ] 实现`getDesc()`方法
- [ ] 实现`createMessage(message)`方法
- [ ] 实现`getMessageClass()`方法
- [ ] 实现`doGetProtocol(message, type)`方法(可选)
- [ ] 创建消息类`{协议}Message`
- [ ] 在ProtocolSupplier中关联组件

---

## 🚀 快速开始

### 1. 创建消息类

```java
public class {协议}Message {
    private byte[] rawData;
    private int type;  // 报文类型
    
    public {协议}Message(byte[] data) {
        this.rawData = data;
        // 解析报文头部
        this.type = data[0] & 0xFF;
    }
    
    public int getType() {
        return type;
    }
    
    public byte[] getRawData() {
        return rawData;
    }
}
```

### 2. 创建组件类

参考上面的客户端/服务端组件模板。

### 3. 在ProtocolSupplier中关联

```java
@Override
protected {协议}ClientComponent doCreateComponent(NetworkConfigImpl.TcpNetworkConfig config) {
    ClientConnectProperties connectConfig = new ClientConnectProperties(
        config.getHost(), config.getPort(), null, null
    );
    return new {协议}ClientComponent(connectConfig);
}
```

### 4. 注册到Spring

确保组件类添加了`@Component`注解，Spring会自动注册到FrameworkManager。

---

## 💡 最佳实践

1. **组件命名规范**：`{协议}ClientComponent` / `{协议}ServerComponent`
2. **组件名称格式**：`{协议}_CLIENT` / `{协议}_SERVER`
3. **消息类位置**：放在`component`或`network`包下
4. **解码器选择**：
   - 有长度字段：使用`LengthFieldBasedFrameDecoder`
   - 固定帧头：使用自定义解码器
   - 分隔符：使用`DelimiterBasedFrameDecoder`
5. **心跳处理**：在`idle()`方法中实现，不要阻塞
6. **异常处理**：在消息类中处理解析异常，组件层只负责路由
7. **线程安全**：组件是单例，避免使用可变状态

---

## 📚 参考示例

### 示例项目

| 协议 | 组件类 | 类型 | 路径 |
|------|--------|------|------|
| Modbus TCP | ModbusTcpClientComponent | Client | protocol-modbus/modbus-tcp |
| Modbus RTU | ModbusRtuClientComponent | Client | protocol-modbus/modbus-rtu |
| MQTT | MqttClientComponent | Client | protocol-mqtt |
| 云边协同 | EdgeClientComponent | Client | iboot-edge/edge-client |
| 云边协同 | EdgeServerComponent | Server | iboot-edge/edge-server |

---

## ⚠️ 注意事项

1. **组件是单例**：Spring管理的Component是单例，不要在组件中保存设备状态
2. **连接管理**：每个设备对应一个Client实例，由Component统一管理
3. **报文解码**：解码失败时不要抛出异常，返回null或空消息
4. **内存泄漏**：及时释放ByteBuf，使用ReferenceCountUtil.release()
5. **线程模型**：Netty的IO线程不要执行耗时操作，异步处理业务逻辑
6. **配置更新**：配置变更时通过`doRefresh()`方法更新组件配置

---

**文档版本**: 1.0.0  
**最后更新**: 2026-04-09  
**维护者**: AIoTS开发团队
