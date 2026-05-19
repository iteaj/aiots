
# IoT模块速查

> 开发IoT功能时优先查阅本文档。完整Controller清单见 [iot-module-reference.md](./iot-module-reference.md)。

---

## 一、核心API速查

### IotCoreApiService（静态方法，无需注入）

```java
import com.iteaj.framework.spi.iot.IotCoreApiService;
```

| 方法 | 返回类型 | 说明 |
|------|---------|------|
| `getDeviceStatus(uid)` | `RealtimeDeviceStatus` | 按uid获取设备实时状态（**内存缓存，高性能**） |
| `getDeviceStatus(protocolCode, deviceSn)` | `RealtimeDeviceStatus` | 按协议+设备编号获取状态 |
| `getDeviceStatus(protocolCode, deviceSn, parentDeviceSn)` | `RealtimeDeviceStatus` | 获取网关子设备状态 |
| `listByProductCode(productCode)` | `Collection<RealtimeDeviceStatus>` | 产品下所有设备状态 |
| `listByGroupId(groupId)` | `Collection<RealtimeDeviceStatus>` | 设备组下所有设备状态 |
| `getRealtimeData(uid, signalOrField)` | `ModelAttrRealtimeData` | 获取设备某属性最新采集值 |
| `getProductByCache(productId)` | `ProductMeta` | 获取产品信息（缓存） |
| `getProductModel(productCode)` | `ProductModel` | 获取产品完整模型（含属性/指令/接口） |
| `getAttrMeta(productCode, modelAttrId)` | `ModelAttrMeta` | 获取单个属性元数据 |
| `getAttrMetas(productCode)` | `Map<Serializable, ModelAttrMeta>` | 获取产品所有属性元数据 |
| `publish(dataSupplier, device)` | void | 发布采集数据（触发存储/告警/推送） |

### IDeviceService（注入使用）

```java
// 构造器注入
private final IDeviceService deviceService;
```

| 方法 | 返回类型 | 说明 |
|------|---------|------|
| `getDeviceStatusFromCache(protocolCode, deviceSn)` | `RealtimeStatus` | 从缓存获取设备状态 |
| `getDeviceStatusFromCache(protocolCode, deviceSn, parentDeviceSn)` | `RealtimeStatus` | 从缓存获取子设备状态 |
| `getDeviceStatusFromCache(deviceId)` | `RealtimeStatus` | 按ID从缓存获取 |
| `listDeviceStatusFromCache(protocolCode, deviceSn...)` | `Map<String, RealtimeStatus>` | 批量缓存查询 |
| `listChildDeviceStatusFromCache(protocolCode, parentDeviceSn, deviceSn...)` | `Map<String, RealtimeStatus>` | 子设备批量缓存查询 |
| `detailById(id)` | `DetailResult<DeviceDto>` | 设备详情（含关联） |
| `getByDeviceSn(deviceSn)` | `DetailResult<Device>` | 按设备编号查询 |
| `getByUid(uid)` | `DetailResult<Device>` | 按uid查询 |
| `pageOfDetail(page, entity)` | `PageResult<IPage<DeviceDto>>` | 设备详情分页 |
| `listByProductId(productId)` | `ListResult<DeviceDto>` | 产品下设备列表 |
| `listOfGateway(gatewayId)` | `ListResult<Device>` | 网关下设备列表 |
| `countCurrentDevice()` | `CurrentDeviceDto` | 当前设备统计 |
| `update(deviceSn, status)` | void | 更新设备状态 |
| `listDeviceStatus()` | `ListResult<RealtimeStatus>` | 所有设备缓存状态 |

### RealtimeDataService（注入使用，实时数据缓存）

```java
private final RealtimeDataService realtimeDataService;
```

| 方法 | 返回类型 | 说明 |
|------|---------|------|
| `listOfDevice(protocolCode, key)` | `Map<String, RealtimeData>` | 设备全部实时数据 |
| `listOfDevice(protocolCode, key, signalOrFields...)` | `Map<String, RealtimeData>` | 指定属性实时数据 |
| `getOfDeviceAndKey(protocolCode, key, signalOrField)` | `RealtimeData` | 单个属性实时数据 |
| `put(dataSupplier)` | void | 缓存采集数据 |
| `put(protocolCode, key, value)` | void | 缓存单个点位值 |
| `remove(protocolCode)` | void | 移除协议下所有缓存 |
| `remove(protocolCode, key)` | `Map<String, RealtimeData>` | 移除设备所有缓存 |
| `remove(protocolCode, key, signalOrField)` | `RealtimeData` | 移除指定属性缓存 |

---

## 二、常用Controller速查

| 功能 | Controller | REST路径 | 权限前缀 |
|------|-----------|---------|---------|
| 设备管理 | DeviceController | `/iot/device` | `iot:device` |
| 设备控制/调试 | DeviceCtrlController | `/iot/instruct` | `iot:model` |
| 子设备管理 | DeviceChildController | `/iot/deviceChild` | `iot:deviceChild` |
| 设备分组 | DeviceGroupController | `/iot/deviceGroup` | `iot:deviceGroup` |
| 实时数据 | RealtimeDataController | `/iot/realtime` | - |
| 产品管理 | ProductController | `/iot/product` | `iot:product` |
| 产品类型 | ProductTypeController | `/iot/productType` | `iot:productType` |
| 协议管理 | ProtocolController | `/iot/protocol` | `iot:protocol` |
| 网关管理 | GatewayController | `/iot/gateway` | `iot:gateway` |
| 物模型属性 | ModelAttrController | `/iot/modelAttr` | `iot:modelAttr` |
| 物模型接口 | ModelApiController | `/iot/modelApi` | `iot:modelApi` |
| 联动规则 | LinkageController | `/iot/linkage` | `iot:linkage` |
| 告警配置 | WarnConfigController | `/iot/warnConfig` | `iot:warnConfig` |
| 告警记录 | WarnRecordController | `/iot/warnRecord` | `iot:warnRecord` |
| 事件源 | EventSourceController | `/iot/eventSource` | `iot:eventSource` |
| 采集数据 | CollectDataController | `/iot/collectData` | `iot:collectData` |
| 采集任务 | CollectTaskController | `/iot/collectTask` | `iot:collectTask` |
| 电子地图 | EMapController | `/iot/eMap` | `iot:eMap` |
| 可视化大屏 | VLScreenController | `/iot/vlScreen` | `iot:vlScreen` |
| 首页统计 | IotIndexController | `/iot/index` | - |
| 通用统计 | CommonCountController | `/iot/commonCount` | - |

### 实时数据接口详情

```
GET  /iot/realtime/listByDeviceId?deviceId=     设备全部实时数据（按ID）
GET  /iot/realtime/listByDevice?productCode=&deviceSn=  设备全部实时数据（按编号）
GET  /iot/realtime/get?productCode=&deviceSn=&eventOrSignalField=  单个属性实时数据
GET  /iot/realtime/getByDeviceId?deviceId=&signalOrField=  单个属性实时数据（按ID）
```

---

## 三、IoT事件监听速查

### 监听器接口

| 接口 | 事件类型 | Payload | 说明 |
|------|----------|---------|------|
| `RealtimeInfoListener` | `Realtime` | `RealtimePayload` | 监听实时采集数据（**最常用**） |
| `CmdInvokeListener` | `CmdInvoke` | `CmdInvokePayload` | 监听指令调用结果 |
| `ScriptInvokeListener` | `ScriptInvoke` | `ScriptInvokePayload` | 监听脚本触发 |
| `StatusSwitchListener` | `DeviceStatus/GatewaySwitch/ProductSwitch` | `EntityPayload` | 监听状态切换 |

### RealtimePayload 数据类型判断

```java
payload.isData()       // 定时采集数据
payload.isDataCmd()    // 指令采集数据
payload.isDataActive() // 设备主动上报
payload.isWarn()       // 告警数据
payload.isStatus()     // 设备状态变更
```

### 使用方式

```java
@Component
public class MyRealtimeListener implements RealtimeInfoListener {
    @Override
    public void onIotEvent(IotEventType eventType, RealtimePayload payload) {
        if (payload.isData()) { /* 处理采集数据 */ }
        if (payload.isWarn()) { /* 处理告警 */ }
    }
}
```

### 事件发布

```java
// 同步发布（异步监听器自动转异步）
IotEvenPublisher.publish(IotEventType.Realtime, realtimePayload);

// 仅触发异步监听器 + 回调
IotEvenPublisher.publish(IotEventType.CmdInvoke, payload, callback);
```

---

## 四、IoT数据模型速查

### DeviceKey（设备标识）

```java
DeviceKey.build(deviceSn, null)              // 直连设备/网关设备
DeviceKey.build(deviceSn, parentDeviceSn)    // 网关子设备
```

### DataSupplier（采集数据载体）

```java
DataSupplier supplier = new DataSupplier(protocolCode, deviceKey);
supplier.build(new Date())                    // 设置采集时间
        .addValue(attrId, signalOrField, value)           // 添加点位值
        .addValue(attrId, address, signalOrField, value); // 带地址的点位值

// 发布采集数据
IotCoreApiService.publish(supplier, device);
```

### RealtimeDeviceStatus（设备实时状态）

| 方法 | 说明 |
|------|------|
| `getUid()` | 设备唯一uid |
| `getDeviceSn()` | 设备编号 |
| `getDeviceName()` | 设备名称 |
| `getDeviceStatus()` | 状态值（`online` / `offline`） |
| `getStatus()` | 状态枚举（`DeviceStatusEnum`） |
| `getProtocolCode()` | 协议码 |
| `getProductCode()` | 产品码 |
| `getParentDeviceSn()` | 父设备编号（网关子设备时有值） |
| `getConfig()` | 设备配置（`JSONObject`） |
| `buildKey()` | 构建 `DeviceKey` |

---

## 五、物模型属性解析器速查(DataValueResolver)

> 用于解析设备采集的原始数据,将原始值转换为实际物理值(如:电压转换、温度校准、线性变换等)。

### DataValueResolver接口

```java
public interface DataValueResolver {
    /**
     * @param modelAttr 物模型属性元数据
     * @param rawValue 原始采集值
     * @return 解析后的实际值
     */
    Object resolve(ModelAttrMeta modelAttr, Object rawValue);
}
```

### 内置解析器类型

| 解析器       | 说明       | 适用场景    |
|-----------|----------|---------|
| `default` | 系统默认的解析器 | 简单场景       |
| `script`  | 脚本解析(JS) | 复杂自定义逻辑 |

### 使用示例:线性变换

```java
public class LinearResolver implements DataValueResolver {
    @Override
    public Object resolve(ModelAttrMeta modelAttr, Object rawValue) {
        if (rawValue == null) return null;
        
        double raw = Double.parseDouble(rawValue.toString());
        
        // 从属性元数据获取系数
        JSONObject config = modelAttr.getConfig();
        double k = config.getDoubleValue("k", 1.0);  // 斜率
        double b = config.getDoubleValue("b", 0.0);  // 截距
        
        // 线性变换: y = kx + b
        double result = k * raw + b;
        return result;
    }
}
```

### 注册解析器
只需要在合适的地方调用`DataValueResolverFactory.register()`方法即可注册解析器。
```java
// 注册解析器
DataValueResolverFactory.register("linear", new LinearResolver());
```

---

## 六、高频开发场景

### 1. 获取设备状态（高性能）

```java
// ✅ 推荐：内存缓存，毫秒级
RealtimeDeviceStatus status = IotCoreApiService.getDeviceStatus(uid);
// 或
RealtimeStatus status = deviceService.getDeviceStatusFromCache(protocolCode, deviceSn);

// ❌ 避免：数据库查询，性能差
// Device device = deviceService.getById(id);
```

### 2. 获取设备实时采集数据

```java
// 方式1：IotCoreApiService（静态方法，获取单个属性）
ModelAttrRealtimeData data = IotCoreApiService.getRealtimeData(uid, "temperature");

// 方式2：RealtimeDataService（注入，获取全部属性）
Map<String, RealtimeData> allData = realtimeDataService.listOfDevice(productCode, DeviceKey.build(deviceSn, null));

// 方式3：REST API
// GET /iot/realtime/listByDeviceId?deviceId=xxx
```

### 3. 发布采集数据

```java
DataSupplier supplier = new DataSupplier(protocolCode, deviceKey);
supplier.build(new Date()).addValue(attrId, "temperature", 25.6);
IotCoreApiService.publish(supplier, realtimeDeviceStatus);
```

### 4. 监听设备状态变更

```java
@Component
public class MyStatusListener implements StatusSwitchListener {
    @Override
    public void onIotEvent(IotEventType eventType, EntityPayload payload) {
        if (eventType == IotEventType.DeviceStatus) {
            // 设备上线/离线
        }
    }
}
```

### 5. 批量获取设备状态

```java
// 按产品
Collection<? extends RealtimeDeviceStatus> devices = IotCoreApiService.listByProductCode(productCode);

// 按设备组
Collection<? extends RealtimeDeviceStatus> devices = IotCoreApiService.listByGroupId(groupId);

// 按设备ID列表
List<RealtimeStatus> list = deviceService.listDeviceStatusById(idList);
```

---

## 七、性能陷阱

| 场景 | ❌ 低性能 | ✅ 高性能 |
|------|---------|---------|
| 获取设备状态 | `deviceService.getById()` 数据库查 | `IotCoreApiService.getDeviceStatus()` 内存缓存 |
| 获取实时数据 | SQL查询采集表 | `realtimeDataService.listOfDevice()` 内存缓存 |
| 批量设备状态 | 循环查询数据库 | `IotCoreApiService.listByProductCode()` 一次获取 |
| 获取产品信息 | 每次查数据库 | `IotCoreApiService.getProductByCache()` 缓存 |

> 详细SPI接口说明见 [spi-iot-data-model.md](./spi-iot-data-model.md)、[spi-iot-data-services.md](./spi-iot-data-services.md)、[spi-iot-event-listener.md](./spi-iot-event-listener.md)
