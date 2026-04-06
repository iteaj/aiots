# framework spi/iot 接口总览

包路径：`com.iteaj.framework.spi.iot`

framework 通过 `spi/iot` 向其他插件开放物联网扩展能力。这些接口分为两类：
- **插件实现类**：注册协议、监听事件
- **Bootstrap 实现、插件注入使用**：查询设备数据、产品模型、发布采集数据

详细内容按分类查阅以下文件：

| 文件 | 说明 |
|------|------|
| [spi-iot-protocol-supplier.md] | 协议提供接口（`DeviceProtocolSupplier` 及子类、`DirectProtocolHandle`、`BatchEventModelApi`、`ProtocolSupplierManager`） |
| [spi-iot-data-services.md] | 业务数据服务接口（实时数据、采集发布、产品模型、历史数据、告警数据） |
| [spi-iot-event-listener.md] | 事件监听接口（`IotListener`、`RealtimeInfoListener`、`CmdInvokeListener` 等） |
| [spi-iot-data-model.md] | 核心数据对象（`DataSupplier`、`SignalOrFieldValue`、`DeviceKey`、`UpModelAttr`、`RealtimeDeviceStatus`、`ProtocolModel`） |
| [spi-iot-network-config.md] | 网络配置（`NetworkConfig` 及6种内置实现，`TransportProtocol` 枚举） |
