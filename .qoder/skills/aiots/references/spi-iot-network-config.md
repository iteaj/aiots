# spi/iot — 网络配置

包路径：`com.iteaj.framework.spi.iot`

定义协议插件的网络连接参数，作为 `DeviceProtocolSupplier` 的泛型参数 `C`。

---

## NetworkConfig（接口）

| 方法 | 说明 |
|------|------|
| `getUid()` | 设备唯一 id |
| `getHost()` | 主机地址 |
| `getPort()` | 端口 |
| `isUseSsl()` | 是否启用 SSL |
| `getDeviceSn()` | 设备编号 |

---

## 内置实现（NetworkConfigImpl 子类）

`NetworkConfig.resolve(protocol, config)` 可根据 `TransportProtocol` 枚举自动反序列化为对应子类。

| 子类 | 适用协议 | 说明 |
|------|----------|------|
| `TcpNetworkConfig` | TCP | 主机 + 端口 + SSL |
| `MqttNetworkConfig` | MQTT | Broker 地址 + 端口 + 认证信息 |
| `UdpNetworkConfig` | UDP | 主机 + 端口 |
| `HttpNetworkConfig` | HTTP | 请求地址 + 端口 |
| `WebSocketConfig` | WEBSOCKET | WS 地址 + 端口 |
| `SerialConfig` | SERIAL | 串口号 + 波特率等参数 |

---

## TransportProtocol（传输协议枚举）

| 枚举值 | 说明 |
|--------|------|
| `TCP` | TCP 长连接 |
| `MQTT` | MQTT 消息协议 |
| `UDP` | UDP 无连接 |
| `HTTP` | HTTP 请求 |
| `WEBSOCKET` | WebSocket 长连接 |
| `SERIAL` | 串口通信 |

---

## 使用示例

协议插件泛型参数声明：

```java
// 服务端 TCP 协议
public class MyProtocolSupplier extends StaticServerProtocolSupplier<NetworkConfigImpl.TcpNetworkConfig, ServerComponent> {

    @Override
    protected ServerComponent doCreateComponent(NetworkConfigImpl.TcpNetworkConfig config) {
        // 使用 config.getHost()、config.getPort() 创建 TCP Server 组件
    }
}

// 客户端 MQTT 协议
public class MyMqttSupplier extends StaticClientProtocolSupplier<NetworkConfigImpl.MqttNetworkConfig, ClientComponent, IotClient> {

    @Override
    protected ClientComponent doCreateComponent(NetworkConfigImpl.MqttNetworkConfig config) {
        // 使用 config.getHost()、config.getPort() 建立 MQTT 连接
    }
}
```
