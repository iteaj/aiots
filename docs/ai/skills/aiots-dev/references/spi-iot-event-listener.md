# spi/iot — 事件监听接口

包路径：`com.iteaj.framework.spi.iot.listener`

插件将监听器实现类注册为 Spring Bean，框架会自动完成注册，无需手动调用。

---

## IotEventType（事件类型枚举）

| 枚举值 | 说明 |
|--------|------|
| `Realtime` | 实时数据（设备上报、指令采集结果） |
| `CmdInvoke` | 平台主动下发指令 |
| `ScriptInvoke` | 脚本触发调用 |
| `DeviceStatus` | 设备在线/离线状态切换 |
| `GatewaySwitch` | 网关状态切换 |
| `ProductSwitch` | 产品状态切换 |

---

## IotListener（监听器顶层接口）

实现此接口并注册为 Spring Bean 即可监听 IoT 事件。

```java
// 过滤感兴趣的事件类型
boolean isEventMatcher(IotEventType eventType);

// 处理事件
void onIotEvent(IotEventType eventType, V value);

// 可覆盖：控制执行顺序，默认 168000，越小越先执行
default int getOrder() { return 168000; }
```

---

## IotAsyncListener（异步监听器）

继承 `IotListener`，标记此接口后事件在线程池中异步执行，不阻塞采集主流程。

```java
// 异步处理，callback 用于通知调用方结果
default void onIotEvent(IotEventType eventType, V value, Consumer callback) {}
```

---

## 内置具体监听器接口（推荐实现）

优先实现这些具体接口，已内置 `isEventMatcher` 过滤逻辑，只需实现 `onIotEvent`：

| 接口 | 事件类型 | Payload | 说明 |
|------|----------|---------|------|
| `RealtimeInfoListener` | `Realtime` | `RealtimePayload` | 监听实时采集数据（最常用） |
| `CmdInvokeListener` | `CmdInvoke` | `CmdInvokePayload` | 监听平台指令调用结果 |
| `ScriptInvokeListener` | `ScriptInvoke` | `ScriptInvokePayload` | 监听脚本触发调用 |
| `StatusSwitchListener` | `DeviceStatus` / `GatewaySwitch` / `ProductSwitch` | `EntityPayload` | 监听状态切换 |

---

## RealtimePayload（实时数据负载）

`RealtimeInfoListener` 接收的事件载体。

| 字段/方法 | 说明 |
|-----------|------|
| `uid` | 设备 uid |
| `type` | 数据类型字符串（见下表） |
| `isData()` | 是否为定时采集数据 |
| `isDataCmd()` | 是否为指令采集数据（`data_cmd`） |
| `isDataActive()` | 是否为设备主动上报（`data_active`） |
| `isWarn()` | 是否为告警数据 |
| `isStatus()` | 是否为设备状态变更 |

type 取值：

| type 值 | 说明 |
|---------|------|
| `data` | 定时/轮询采集的数据 |
| `data_cmd` | 平台指令触发的采集数据 |
| `data_active` | 设备主动上报的数据 |
| `warn` | 告警触发 |
| `status` | 设备在线状态变更 |

---

## IotEvenPublisher（事件发布工具）

框架内部使用，插件通常不需要主动调用。如需在插件内发布自定义 IoT 事件可使用：

| 方法 | 说明 |
|------|------|
| `register(listener)` | 注册监听器（框架自动调用，插件无需手动调用） |
| `publish(type, payload)` | 同步发布事件（`IotAsyncListener` 实现会自动转为异步执行） |
| `publish(type, payload, callback)` | 仅触发异步监听器，执行完成后调用 callback |
