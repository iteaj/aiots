# 脚本引擎

**菜单：脚本引擎**

| 页面 | 说明 |
|------|------|
| 数据过滤 | 数据入库前按条件过滤，控制哪些数据写入数据库 |
| 数据解析 | 通过脚本对采集的原始数据进行复杂计算和类型转换 |
| 功能解析 | 下发指令时动态组装参数值（如 MQTT 下发需要收集多个配置项拼装成完整的下发数据） |
| 联动脚本 | 编写自定义联动逻辑 |

## 数据过滤（属性有变化才入库）

> **AI 开发提示**：`methods.*` 方法在 `RealtimeMethodProvide.java` 中定义，`Controller` 是 `ListenerScriptController.java`。

数据入库前，可通过脚本判断是否满足条件，满足才写入数据库。脚本中用 `methods.isChange(data)` 判断属性值相比上次是否有变化，无变化则返回 `false` 不写入，避免大量重复数据占用存储。

同时支持按时间间隔过滤：`methods.isGeInterval(data, 秒数)` 表示距离上次入库超过指定秒数才返回 `true`，适合变化频繁的属性降频存储。

### 可用方法

| 方法 | 说明 |
|------|------|
| `methods.isRealtimeData(data)` | 是否为实时数据 |
| `methods.isRealtimeStatus(data)` | 是否为状态数据 |
| `methods.isUid(data, uid)` | 是否为指定设备 |
| `methods.isProduct(data, productCode)` | 是否为指定产品 |
| `methods.isProtocol(data, protocolCode)` | 是否为指定协议 |
| `methods.isChange(data)` | 值是否改变（与上次入库值对比） |
| `methods.isGeInterval(data, seconds)` | 入库间隔是否≥指定秒数 |
| `data.getUid()` | 获取设备 UID |
| `data.getValue()` | 获取点位值 |
| `data.getField()` | 获取点位属性 |

### 写法示例

```javascript
function filter(data, methods) {
    // 仅温度变化时才入库
    if (methods.isChange(data)) {
        return true;
    }
    return false;
}
```

## 数据解析（属性值复杂计算）

> **AI 开发提示**：`methods.*` 方法在 `ModelAttrDataProvider.java` 中定义，`Controller` 是 `ModelAttrScriptController.java`。

当设备上报的原始值需要经过复杂计算才能转为业务值时，可在物模型属性上配置"值解析器"，编写 JavaScript 脚本对原始值做转换。

脚本支持：
- **类型转换**：字节数组 ↔ 整数/浮点数/十六进制
- **进制转换**：`methods.toHex(data)`、`methods.toBytes(data)` 等
- **复合计算**：多个字节组合后计算最终值
- **分组属性**：通过 `methods.addRealtimeData(模型标识, 属性标识, 值)` 将一条数据拆分为多条子属性

### 可用方法

| 方法 | 说明 |
|------|------|
| `methods.addRealtimeData(model, field, value)` | 增加实时属性数据 |
| `methods.isAttrField(model, field)` | 是否为指定字段的模型属性 |
| `methods.toHex(value)` | 将基础类型值转成16进制 |
| `methods.toBytes(value)` | 将基础类型值转成字节数组 |
| `methods.toString(value)` | 值转成字符串 |
| `methods.isBytes(value)` | 是否为字节数组类型 |
| `methods.isNull(value)` | 是否是空值 |
| `methods.isNumber(value)` | 是否是数值类型 |
| `methods.getLength(value)` | 获取字节数组长度 |
| `methods.getInt(value, offset)` | 从字节数组 offset 位置读取一个 int |
| `methods.getByte(value, offset)` | 从字节数组 offset 位置读取一个 byte |
| `methods.getShort(value, offset)` | 从字节数组 offset 位置读取一个 short |
| `methods.getLong(value, offset)` | 从字节数组 offset 位置读取一个 long |
| `methods.getFloat(value, offset)` | 从字节数组 offset 位置读取一个 float |
| `methods.getDouble(value, offset)` | 从字节数组 offset 位置读取一个 double |
| `methods.getString(value, offset, length)` | 从字节数组 offset 位置读取指定长度字符串 |

### 写法示例

```javascript
function resolver(value, model, methods) {
    // 字节数组转数值
    if(methods.isBytes(value)) {
        var temp = methods.getFloat(value, 0);
        return temp.toFixed(1);
    }
    
    // 十六进制转数值
    methods.info('解析', methods.toHex(value));
    return value;
}
```

适用于 Modbus 寄存器解析、PLC 数据字拼接、原始字节解码等场景。

## 功能解析（下发指令时动态组装参数）

> **AI 开发提示**：`methods.*` 方法在 `ScriptCmdMethodsProvider.java` 中定义，`Controller` 是 `ModelApiScriptController.java`。

当设备下发指令的参数需要动态组装时（如 MQTT 下发需要将多个配置项拼装成完整的 JSON 下发数据），可通过功能解析脚本处理。

### 可用方法

| 方法 | 说明 |
|------|------|
| `methods.writeValue(data, value)` | 写值（只支持基础数据类型） |
| `methods.getUid(data)` | 获取执行设备 UID |
| `methods.isUid(data, uid)` | 执行设备是否为指定设备 |
| `methods.isProduct(data, productCode)` | 执行设备是否为指定产品 |
| `methods.isProtocol(data, protocolCode)` | 执行设备是否为指定协议 |
| `methods.getApiCode(data)` | 获取调用的接口码 |
| `methods.getProductCode(data)` | 获取执行设备的产品码 |
| `methods.getProtocolCode(data)` | 获取执行设备的协议码 |
| `methods.getProtocolAttr(data)` | 获取此脚本绑定的协议属性 |
| `methods.getDeviceStatus(data)` | 获取执行设备状态（online/offline） |
| `methods.getDeviceGroupId(data)` | 获取执行设备所属组 |
| `methods.getDeviceSn(data)` | 获取执行设备编号 |
| `methods.getParentDeviceSn(data)` | 获取执行设备的父设备编号 |
| `methods.getDeviceConfig(data, key)` | 获取设备的配置项 |
| `methods.getInt(data, key)` | 获取执行参数 int 值 |
| `methods.getLong(data, key)` | 获取执行参数 long 值 |
| `methods.getDouble(data, key)` | 获取执行参数 double 值 |
| `methods.getBoolean(data, key)` | 获取执行参数 boolean 值 |
| `methods.getString(data, key)` | 获取执行参数 string 值 |

父类 `ValueConvertProvide` 提供的方法（`toHex`、`toBytes`、`toString`、`isNull`、`getFloat` 等）同样可用。

### 写法示例

```javascript
function write(data, methods) {
    // 判断产品类型，不同产品组装不同格式
    if(methods.isProduct(data, 'mqtt_device')) {
        // 组装 JSON 格式下发数据
        var payload = '{"cmd":"' + methods.getApiCode(data)
            + '","value":"' + methods.getString(data, 'value') + '"}';
        methods.writeValue(data, payload);
    } else {
        methods.writeValue(data, 35.8);
    }
}
```

## 联动脚本（数据驱动的自动化逻辑）

> **AI 开发提示**：`methods.*` 方法在 `LinkageScriptMethodsProvider.java` 中定义（`iboot-plugin/iboot-script/.../model/`），`Controller` 是 `LinkageScriptController.java`。

联动脚本由采集或上报的数据触发，通过脚本调用设置指令，可实现比场景联动更灵活的自动化逻辑。一个脚本实现一种模式，工厂操作员无需写代码，在页面切换脚本即可切换工艺模式。

### 脚本结构

每个联动脚本包含两个函数：

**`matcher()`** — 定义该脚本关注哪些产品的哪些属性。只有匹配的数据才会触发脚本：

```javascript
function matcher() {
    // 监控 th_sensor 产品的 temperature 和 humidity 属性
    return {th_sensor: ["temperature", "humidity"]};
}
```

支持的通配符：
- `*` — 匹配所有属性
- `#status` — 匹配设备在线状态变化

**`trigger(data, methods)`** — 数据匹配时执行的核心逻辑：

```javascript
function trigger(data, methods) {
    if(!methods.isRealtimeData(data)) return;  // 只处理实时数据
    var uid = methods.getUid(data);            // 获取触发设备UID
    
    // 读取当前值
    var temp = methods.getValue(uid, "temperature");
    
    // 调用设备指令
    methods.invoke("api->write_th_temp&uid->" + uid + "&value->" + temp);
}
```

### 可用方法

| 方法 | 说明 |
|------|------|
| `methods.invoke(cmd)` | 执行指令（设备指令集指令） |
| `methods.invoke(cmd, uid)` | 执行指令（产品指令集指令） |
| `methods.asyncInvoke(cmd, uid)` | 异步执行指令 |
| `methods.getValue(uid, field)` | 获取指定字段最新值 |
| `methods.getPrevValue(uid, field)` | 获取上一个值 |
| `methods.getUid(data)` | 当前触发设备UID |
| `methods.isRealtimeData(data)` | 是否为实时数据触发 |
| `methods.isRealtimeStatus(data)` | 是否为状态数据触发 |
| `methods.isProduct(data, productCode)` | 是否为指定产品 |
| `methods.isChange(uid, field)` | 属性值是否改变 |
| `methods.isGeInterval(data, seconds)` | 距离上次入库是否≥指定秒数 |
| `methods.warn(msgId, uid, field, value, content)` | 发送告警 |
| `methods.urgentWarn(msgId, uid, field, value, content)` | 发送紧急告警 |
| `methods.normalWarn(msgId, uid, field, value, content)` | 发送告警解除 |

### 指令消息格式

`methods.invoke()` 的 cmd 参数通过 `api->` 开头指定功能模型编码，程序自动识别协议指令：

```
api->{功能编码}&uid->{设备UID}&value->{值}&address->{寄存器地址}&dataType->{数据类型}&num->{寄存器数量}
```

示例（写Modbus寄存器）：
```javascript
// 写温度寄存器（地址0，float类型，2个寄存器）
methods.invoke("api->write_th_temp&uid->" + uid + "&value->25.5");

// 写开关控制（地址48，short类型，1个寄存器）
methods.invoke("api->set_switch&uid->" + uid + "&value->1");
```

### 数据模拟场景

联动脚本一个典型用途是：采集器从 Modbus 从机读到值 → 触发联动脚本计算下一个周期的模拟值 → 通过 `methods.invoke()` 用 Write10 指令写回从机寄存器 → 下次采集读到新值。

```javascript
function trigger(data, methods) {
    if(!methods.isRealtimeData(data)) return;
    var uid = methods.getUid(data);
    var t = new Date().getTime();
    
    // 用正弦波模拟温度变化
    var temp = (25 + 3 * Math.sin(t / 30000)).toFixed(1);
    methods.invoke("api->write_th_temp&uid->" + uid + "&value->" + temp);
}
```

支持的场景：

**温度超限分级保护** — 连续多次采样超温才告警，防误报。分级处理：超限小幅度先降速，幅度大再停机。

**产线计数满自动换箱** — 传感器触发计数，达到设定数量后调用指令驱动推杆换箱，并重置计数器。

**PID 闭环调节** — 读取当前值、目标值，通过脚本计算 PID 输出，调用指令调节执行器，实现精准控制。

**多条件复合预警** — 多传感器联合判断（如电流和振动同时异常才告警），减少单传感器误报。

**烟叶烘烤工艺模式切换** — 不同批次需要不同温湿度曲线（如不同等级烟叶的烘烤工艺不同）。将每条工艺曲线逻辑写成一个联动脚本（脚本A：清香型工艺、脚本B：浓香型工艺、脚本C：晾晒工艺），操作员只需要在页面上选择对应的脚本即可切换整套温湿度控制逻辑。
