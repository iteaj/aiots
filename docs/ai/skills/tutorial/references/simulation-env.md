# 模拟设备环境搭建

> **AI 开发提示**：联动脚本的 `methods.*` 方法在 `LinkageScriptMethodsProvider.java` 中定义（`iboot-plugin/iboot-script/.../model/`），`Controller` 是 `LinkageScriptController.java`。联动脚本模板在 `LinkageScriptController.java` 中。

没有真实设备也可以创建一个完整的模拟工厂环境，用于平台评估、方案验证、客户演示。

## 整体思路

```
模拟器（IoTClient）
  → 平台采集器读寄存器值
    → 触发联动脚本
      → 脚本计算下一周期的值
        → 写指令写回寄存器
          → 平台下次读到新值
```

采集器读到值 → 触发联动脚本 → 脚本把模拟值写回寄存器 → 下次采集读到新值，形成循环。

## 第一步：启动设备模拟器

**Windows 平台** — 下载 IoTClient 模拟器（支持 Modbus、PLC 等协议模拟）：

> 下载地址：[https://www.iteaj.com/downloads/IoTClient.exe](https://www.iteaj.com/downloads/IoTClient.exe)

**Linux 平台** — 使用 diagslave 工具：

```bash
# 启动 3 个模拟器实例，每个响应多个从站地址
diagslave -m tcp -p 1502 -a 1    # 从站地址：1
diagslave -m tcp -p 1503 -a 1    # 从站地址：1
diagslave -m tcp -p 1504 -a 1    # 从站地址：1
```

`-a 1` 指定从站地址为 1。Modbus 从站地址范围为 1~247，地址 0 为广播地址（从机不响应）。

不同产品的设备建议分散到不同模拟器实例，因为它们的寄存器地址可能相同。同一产品的设备使用相同的寄存器地址，必须分配到不同实例（不同端口），所以启动几台实例就意味着同一产品最多只能有几台设备。

## 第二步：创建设备分组

按工厂区域划分，先建顶级再建子级：

| 分组名称 | 父分组 |
|---------|--------|
| 智能工厂园区 | 无 |
| ├─ 生产车间 | 智能工厂园区 |
| │  ├─ 一楼原料区 | 生产车间 |
| │  ├─ 二楼生产区 | 生产车间 |
| │  └─ 三楼包装区 | 生产车间 |
| ├─ 动力站房 | 智能工厂园区 |
│ │  ├─ 空压机房 | 动力站房 |
│ │  └─ 干燥间 | 动力站房 |
| ├─ 污水处理站 | 智能工厂园区 |
│ │  ├─ 提升泵房 | 污水处理站 |
│ │  └─ 曝气池 | 污水处理站 |
| └─ 冷库群 | 智能工厂园区 |
│    ├─ 1号冷库区 | 冷库群 |
│    └─ 2号冷库区 | 冷库群 |

> 对应的 SQL 示例（展示顶级→子级的结构）：
> ```sql
> INSERT INTO `iot_device_group` (`id`, `pid`, `name`, `path`, `type`, `create_time`) VALUES
> (20, 0, '智能工厂园区', '20', 'archive', now()),
> (21, 20, '生产车间', '20,21', 'archive', now()),
> (22, 21, '一楼原料区', '20,21,22', 'archive', now());
> ```
> `path` 字段格式 `父path,自身ID`，顶级分组 `path` 等于自身 ID。树形结构靠 `pid`（父ID）和 `path`（完整路径）共同维护。

## 第三步：创建子产品与物模型

每个产品作为"子产品"，父产品选"默认Modbus Tcp产品"，协议驱动选"Modbus Tcp协议驱动"。

### 产品创建要点

| 字段 | 值 |
|------|-----|
| 产品名称 | 如：温湿度传感器 |
| 产品代码 | 如：th_sensor |
| 所属类型 | 根据场景选择（如：温湿度、环境监测、电器设备） |
| 设备类型 | 网关子设备 |
| 父产品 | 默认Modbus Tcp产品 |

> 对应的 SQL 示例：
> ```sql
> INSERT INTO `iot_product` (`code`, `name`, `status`, `remark`, `parent_id`, `device_type`, `protocol_code`, `product_type_id`, `create_time`) VALUES
> ('th_sensor', '温湿度传感器', 'enabled', '采集环境温度与湿度数据', 58, 'Child', 'MODBUS_TCP', 48, now());
> ```

### 属性配置要点

物模型属性配置要包含的字段：

- **属性名称** — 中文名称（如：温度）
- **属性字段/标识符** — 英文字段名（如：temperature）
- **数据类型** — 按实际值选择（float/short/int/boolean）
- **单位** — 按实际填写（℃、MPa、%RH 等）
- **功能类型** — 读属性选 R，读写属性选 R/W
- **属性来源** — 选 Model
- **精度** — float 类型推荐 1~2 位

> 开关状态、运行状态等属性数据类型用 `short`（0/1 表示），而不是 `boolean`。字典中配置 0=停止、1=运行。

> 对应的 SQL 示例：
> ```sql
> INSERT INTO `iot_model_attr` (`product_id`, `field`, `name`, `data_type`, `unit`, `accuracy`, `attr_type`, `origin`, `resolver`, `create_time`) VALUES
> (100, 'temperature', '温度', 'float', '℃', 1, 'R', 'Model', 'DEFAULT', now());
> ```

### 事件模型配置要点

每个属性对应一个采集事件：

| 字段 | 值 |
|------|-----|
| 接口类型 | 事件 |
| 接口代码 | 如：read_temp |
| 协议指令 | 由协议驱动定义，见下方说明 |
| 触发方式 | passive |
| 功能类型 | R（读） |
| 频率标签 | 默认 DEFAULT |

**协议指令来源** — 每个驱动在 `DeviceProtocolSupplier` 实现类中通过 `ProtocolModel.addModelApi()` 注册自己的指令集。`direct` 字段必须填写驱动已注册的指令编码之一。常用协议驱动注册的指令如下：

| 协议 | 类型 | 指令编码 | 说明 | 下行配置 |
|------|------|---------|------|---------|
| **Modbus TCP/RTU** | 事件(读) | `Read01` | 读线圈 | address |
| | | `Read02` | 读离散量输入 | address |
| | | `Read03` | 读保持寄存器 | dataType, address, num |
| | | `Read04` | 读输入寄存器 | dataType, address, num |
| | 功能(写) | `Write05` | 写单个线圈 | address, value |
| | | `Write06` | 写单个保持寄存器 | address, value |
| | | `Write0F` | 写多个线圈 | address, value |
| | | `Write10` | 写多个保持寄存器 | dataType, address, value |
| **PLC（基类通用）** | 事件(读) | `readBool` | 读取布尔值 | address |
| | | `readInt16` | 读取short值 | address |
| | | `readUInt16` | 读取无符号short值 | address |
| | | `readInt32` | 读取int值 | address |
| | | `readUInt32` | 读取无符号int值 | address |
| | | `readInt64` | 读取long值 | address |
| | | `readFloat` | 读取float值 | address |
| | | `readDouble` | 读取double值 | address |
| | | `readString` | 读取string值 | address, num |
| | 功能(写) | `writeBool` | 写布尔值 | address, value |
| | | `writeInt16` | 写short值 | address, value |
| | | `writeInt32` | 写int值 | address, value |
| | | `writeInt64` | 写long值 | address, value |
| | | `writeFloat` | 写float值 | address, value |
| | | `writeDouble` | 写double值 | address, value |
| | | `writeHex` | 写Hex值 | address, value |
| | | `writeString` | 写string值 | address, value |
| **西门子S7（PLC扩展）** | 事件(读) | `readByte` | 读取字节值 | address |
| | | `readDate` | 读取日期值 | address |
| | | `readDtlDatetime` | 读取DTL时间值 | address |
| | 功能(写) | `writeByte` | 写byte值 | address, value |
| | | `writeDate` | 写日期值 | address, value |
| | | `writeDatetime` | 写DTL时间值 | address, value |
| **OPC UA** | 事件(读) | `READ` | 读取变量节点值 | address |
| | 功能(写) | `WRITE` | 写入变量节点值 | address, value, dataType |
| | 功能 | `EXEC_METHOD` | 执行方法节点 | — |
| **DLT645** | 事件(读) | `readReq` | 读电表数据 | data |
| | | `readMAReq` | 读电表编号 | — |
| | 功能(写) | `rts` | 广播校时 | time |
| | | `mcr` | 修改波特率 | rate |
| | | `fCmd` | 冻结命令 | time |
| | | `ffCmd` | 广播冻结命令 | time |
| | | `writeMAReq` | 修改电表地址 | deviceSn |
| | | `ctrl` | 控制设备 | perm, cmd, pwd, code, expire |

> 各驱动实际注册的指令以源码中 `addModelApi()` 调用为准。下行配置的 `protocol_attr_field` 对应上表中的配置项名。

**上行配置**（UP 方向）— 把读取的值映射到属性。具体有哪些上行配置项，由协议指令对照表中对应指令的"下行配置"列决定（协议驱动框架将这列为 UP，但在配置表中统称为协议参数，系统根据 direction 字段区分方向）：

| 字段 | 说明 |
|------|------|
| 协议属性字段 | 驱动定义的字段名，如 `value`（读取到的值） |
| 配置值 | `@属性字段`（如 @temperature），将读取值映射到物模型属性 |
| 数据类型 | 属性的数据类型（如 float、short、string） |
| 协议数据类型 | 固定填 `any` |

**下行配置**（DOWN 方向）— 告诉驱动读取的参数。具体配置项取决于选择的协议指令，见上方**协议指令对照表**的"下行配置"列。例如：

- 选 `Read03`（Modbus 读保持寄存器）→ 需配置 dataType（数据类型）、address（寄存器地址）、num（寄存器数量）
- 选 `readFloat`（PLC 读浮点数）→ 只需配置 address（PLC 地址）
- 选 `readReq`（DLT645 读电表数据）→ 只需配置 data（数据标识）

> 对应的 SQL 示例（以 Modbus 的 Read03 为例）：
> ```sql
> -- 事件模型（direct 来自驱动注册的指令，如 Modbus 的 Read03）
> INSERT INTO `iot_model_api` (`product_id`, `type`, `code`, `name`, `direct`, `func_type`, `trigger_mode`, `status`, `debug`, `collect_tag`) VALUES
> (100, 'event', 'read_temp', '读取温度', 'Read03', 'R', 'passive', 'enabled', 1, 'DEFAULT');
> -- 下行配置：根据 Read03 的指令定义，需要 dataType/address/num
> INSERT INTO `iot_model_api_config` (`api_code`, `value`, `direction`, `protocol_attr_field`, `data_type`, `protocol_data_type`, `product_id`, `create_time`) VALUES
> ('read_temp', 'float', 'DOWN', 'dataType', 'string', 'string', 100, now()),
> ('read_temp', '0', 'DOWN', 'address', 'string', 'string', 100, now()),
> ('read_temp', '2', 'DOWN', 'num', 'int', 'int', 100, now());
> -- 上行配置：值映射到属性字段，绑定属性ID可选
> -- model_attr_id 仅在属性有字典（如开关状态 0/1 映射）时必须指定，
> -- 纯数值映射（如 @temperature）系统可自动解析，填写可让绑定更明确
> INSERT INTO `iot_model_api_config` (`api_code`, `value`, `direction`, `attr_field`, `protocol_attr_field`, `data_type`, `protocol_data_type`, `product_id`, `model_attr_id`, `create_time`) VALUES
> ('read_temp', '@temperature', 'UP', 'temperature', 'value', 'float', 'any', 100, 300, now());
> ```

### 功能模型配置要点

功能模型有两种用途：

1. **控制设备** — 如启停、开关、调参
2. **联动脚本写寄存器** — 联动脚本通过 `methods.invoke(cmd)` 调用驱动指令写值到寄存器

功能模型配置：

| 字段 | 说明 | 示例 |
|------|------|------|
| 接口代码 | 全局唯一，加产品名前缀 | write_th_temp |
| 协议指令 | 由协议驱动定义，见上方对照表 | Write10 |
| 功能类型 | W | W |

> 功能模型代码必须全局唯一，建议格式：`write_{产品简称}_{属性}`。
>
> 功能模型的 UP/DOWN 配置项同样取决于选择的协议指令（见**协议指令对照表**的"下行配置"列）。例如 Write10 需要配置 dataType、address、value；Write05 只需配置 address、value。

> 对应的 SQL 示例：
>
> **示例 1：写固定值（无字典，value 直接写死）**
> ```sql
> -- 功能模型（direct 来自驱动注册的指令，如 Modbus 的 Write10）
> INSERT INTO `iot_model_api` (`product_id`, `type`, `code`, `name`, `direct`, `func_type`, `trigger_mode`, `status`, `create_time`, `collect_tag`) VALUES
> (100, 'func', 'write_th_temp', '写温度', 'Write10', 'W', 'passive', 'enabled', now(), 'DEFAULT');
> -- 下行配置：协议参数和值都直接写死，不绑定模型属性
> INSERT INTO `iot_model_api_config` (`api_code`, `value`, `direction`, `protocol_attr_field`, `data_type`, `protocol_data_type`, `product_id`, `create_time`) VALUES
> ('write_th_temp', 'float', 'DOWN', 'dataType', 'string', 'string', 100, now()),
> ('write_th_temp', '0', 'DOWN', 'address', 'string', 'string', 100, now()),
> ('write_th_temp', '25.0', 'DOWN', 'value', 'float', 'any', 100, now());
> ```
>
> **示例 2：写开关状态（有多种选项，需绑定属性字典）**
> ```sql
> -- 功能模型
> INSERT INTO `iot_model_api` (`product_id`, `type`, `code`, `name`, `direct`, `func_type`, `trigger_mode`, `status`, `create_time`, `collect_tag`) VALUES
> (103, 'func', 'set_switch', '开关控制', 'Write10', 'W', 'passive', 'enabled', now(), 'DEFAULT');
> -- 下行配置：协议参数固定值（数据类型、寄存器地址）
> INSERT INTO `iot_model_api_config` (`api_code`, `value`, `direction`, `protocol_attr_field`, `data_type`, `protocol_data_type`, `product_id`, `create_time`) VALUES
> ('set_switch', 'short', 'DOWN', 'dataType', 'string', 'string', 103, now()),
> ('set_switch', '48', 'DOWN', 'address', 'string', 'string', 103, now());
> -- 值映射绑定属性字段 + 属性ID（因为开关状态有字典：0=关、1=开）
> INSERT INTO `iot_model_api_config` (`api_code`, `value`, `direction`, `attr_field`, `protocol_attr_field`, `data_type`, `protocol_data_type`, `product_id`, `model_attr_id`, `create_time`) VALUES
> ('set_switch', '@switch_status', 'DOWN', 'switch_status', 'value', 'boolean', 'any', 103, 304, now());
> ```

### 寄存器地址分配

不同产品的寄存器地址不能重叠，否则采集时会读到错误的值。建议每个产品分配独立的地址区间，各属性按偏移量排列。如 Modbus 驱动中通过下行配置的 `address` 字段指定起始地址、`num` 指定寄存器数量。

## 第四步：创建设备

每台子设备都需要一个独立的网关设备承载。因为模拟器实例通常只能模拟一个从站地址，所有子设备的 device_sn 只能填 `1`（从站地址），而平台要求同一网关下的子设备 device_sn 必须唯一——所以只能一台网关挂一台子设备来绕开这个限制。

每个网关设备的 `config` 中配置对应的模拟器地址：

```json
{"host": "127.0.0.1", "port": 1502, "deviceSn": "th_gw_101", "timeout": 5, "readIdle": 0}
```

| 设备名称 | 模拟器端口 | 从站地址 | 所属分组 |
|---------|-----------|---------|---------|
| 一楼温湿度网关 | 1502 | 通过子设备 device_sn 指定 | 一楼原料区 |
| 车间CO₂网关 | 1502 | 通过子设备 device_sn 指定 | 二楼生产区 |
| 车间PM2.5网关 | 1502 | 通过子设备 device_sn 指定 | 二楼生产区 |
| 三楼照明网关 | 1502 | 通过子设备 device_sn 指定 | 三楼包装区 |
| 1号空压机网关 | 1502 | 通过子设备 device_sn 指定 | 空压机房 |
| ... | ... | ... | ... |

> 对应的 SQL 示例：
> ```sql
> INSERT INTO `iot_device` (`id`, `uid`, `name`, `product_id`, `status`, `device_sn`, `config`, `device_group_id`, `create_time`) VALUES
> (200, 2201010000000003001, '一楼温湿度网关', 58, 'offline', 'th_gw_101', '{"host":"127.0.0.1","port":1502,"deviceSn":"th_gw_101","timeout":5,"readIdle":0}', 22, now());
> ```
> 注意：每个网关设备都需要独立的 uid，用雪花算法或时间戳生成。

> 同产品的设备使用相同的寄存器地址，必须分配到不同模拟器实例（不同端口），同一产品最多只能有模拟器实例数台设备。

### 子设备

1. 产品选对应的子产品
2. 父设备选对应的网关设备
3. **设备编号（device_sn）** — 含义取决于驱动：
   - Modbus 驱动：固定填从机地址（模拟场景下一个实例只有一个从站，填 `1`）
   - PLC 等驱动：不表示从机地址，只要同一网关下唯一即可，可填任意标识
4. 子设备的 config 需要配置批量采集参数（仅 Modbus 驱动需要）：

```json
{"uid": "设备UID", "isBatch": "true", "dataFormat": "ABCD"}
```

> `isBatch` 和 `dataFormat` 只对 Modbus 驱动等支持批量采集的驱动需要。

- 每个网关下只能挂一台子设备，因为每个模拟器实例只模拟一个从站地址
- 同产品的设备使用相同的寄存器地址，必须分配到不同模拟器实例（不同端口），同一产品最多只能有模拟器实例数台设备

> 对应的 SQL 示例（子设备挂载到网关下）：
> ```sql
> INSERT INTO `iot_device` (`id`, `uid`, `pid`, `name`, `product_id`, `status`, `device_sn`, `config`, `device_group_id`, `create_time`) VALUES
> (220, 2201010000000004001, 200, '一楼温湿度采集器', 100, 'offline', '1', '{"uid":"2201010000000004001","isBatch":"true","dataFormat":"ABCD"}', 22, now());
> ```

## 第五步：配置属性字典

对于布尔/状态类属性，配置字典显示中文映射：

| 属性 | 字典 |
|------|------|
| 开关状态 | 0=关、1=开 |
| 运行状态 | 0=停止、1=运行 |
| 加载状态 | 0=卸载、1=加载 |
| 压缩机状态 | 0=停止、1=运行 |

> 对应的 SQL 示例：
> ```sql
> INSERT INTO `iot_model_attr_dict` (`dict_name`, `dict_value`, `model_attr_id`) VALUES
> ('关', '0', 304), ('开', '1', 304);
> ```

## 第六步：配置告警与指标

告警配置三件套：**触发条件 + 等级 + 指标名**。

### 告警等级

| 等级 | 含义 | 行为 |
|------|------|------|
| fatal | 致命 | 产生告警记录、推送通知 |
| urgent | 紧急 | 产生告警记录、推送通知 |
| warn | 告警 | 产生告警记录、推送通知 |
| notice | 通知 | 产生告警记录 |
| **indicator** | **指标** | **仅显示状态标签，不占告警存储、不推送** |
| **normal** | **解除** | **值恢复正常时自动清除告警** |

### 配置示例

```sql
-- 触发告警：温度 >30℃ 产生告警（warn 级别）
-- 解除条件：温度 ≤30℃ 自动清除（normal 级别）
-- 指标：温度 ≤30℃ 显示"正常"标签（indicator 级别）
INSERT INTO `iot_warn_config` (`name`, `grade`, `model_attr_id`, `model_attr_field`, 
  `operator`, `threshold`, `product_id`, `indicator`) VALUES
('温度过高[告警]', 'warn', 300, 'temperature', 'gt', '30', 100, '偏高'),
('温度恢复[解除]', 'normal', 300, 'temperature', 'lte', '30', 100, '正常');
```

> 有触发告警就必须有对应的解除配置，否则告警不会自动恢复。

## 第七步：创建联动脚本实现数据动态变化

模拟器启动后寄存器值是不变的，要让数据"动起来"需要靠联动脚本：**采集器读到值 → 触发脚本 → 脚本算一个新值 → 写指令写回寄存器 → 下次采集再读到新值**，形成循环。

### 理解三个核心参数

模拟一个数值属性需要三个参数，理解它们各自的作用才能设计出合理的数据：

**基值（base）** — 设备在正常工况下的典型值。真实设备的数据总是围绕某个中心值小范围波动，不会满量程乱跳。基值就是那个中心值。比如温湿度传感器的基值是 25（正常室温），空压机排气温度的基值是 85（正常工况）。**先确定基值，再配置告警阈值**，让基值落在告警触发线以下。

**振幅（amplitude）** — 数据围绕基值上下波动的最大范围。振幅越小数据越平稳，越大变化越明显。设计振幅的关键约束是告警阈值：如温度告警 >30℃，基值 25℃ 配振幅 3（波动范围 22~28℃）就安全；配振幅 8（17~33℃）就会频繁触发告警，不符合"大部分时间正常运行"的模拟目标。

**周期（period）** — 数据完成一次完整波动的时间（毫秒）。周期越短曲线越陡、变化越机械；周期越长变化越平缓，看起来更像真实设备受环境影响缓慢变化。推荐范围 20000~60000ms。

正弦波公式：**`基值 + 振幅 × sin(当前毫秒时间戳 / 周期)`**，结果始终在 `基值 ± 振幅` 之间。

### 时间控制为什么必要

采集器每 5~10 秒采集一次，如果不加限制，每次采集都触发脚本写新值，数据一直在跳，有经验的人一眼就能看出是假的。而且频繁、大幅度的数值变化会触发告警。

正确的做法是：**限制更新频率，不是每次采集都写**。

用 `methods.isGeInterval(data, field, seconds)` 判断距上次对该设备该属性的操作是否已超过指定秒数，超时了才写。时间记录在实时数据缓存中，不受脚本生命周期影响。

为了让更新时间更自然，每次触发时随机取一个间隔（如 300~600 秒），不同设备之间的更新时间自然错开，更接近真实设备的随机上报。

### 不同属性类型的处理方式

```javascript
// 连续数值型（温度、压力、液位等）→ 正弦波模拟自然波动
var temp = (25 + 3 * Math.sin(t / 30000)).toFixed(1);

// 开关状态型（启停、开关、运行状态等）→ 写固定值
methods.invoke("...&value->1");  // 运行

// 累计递增值（运行时长、总产量等）→ 取上次值加随机增量
var prev = methods.getValue(uid, "run_hours");
var hours = ((parseFloat(prev) || 0) + Math.random() * 0.5).toFixed(1);
```

### 脚本模板

```javascript
function matcher() {
    // 改成你要模拟的产品编码和属性字段
    return {product_code: ["field1", "field2"]};
}

function trigger(data, methods) {
    if(!methods.isRealtimeData(data)) return;
    var uid = methods.getUid(data);
    // 限制每 300~600 秒（随机）才写一次
    var interval = 300 + Math.floor(Math.random() * 301);
    if(!methods.isGeInterval(data, "field1", interval)) return;
    var t = new Date().getTime();
    // 基值、振幅根据告警阈值设定，周期推荐 20000~60000ms
    var val = (base + amplitude * Math.sin(t / period)).toFixed(1);
    methods.invoke("api->write_func_code&uid->" + uid + "&value->" + val);
}
```

### 数据模拟公式

一般数据：`基值 + 振幅 × sin(时间 / 周期)`
状态数据：直接写固定值（如 0 或 1）

### 脚本启用

1. 管理端 → 脚本引擎 → 联动脚本
2. 新增脚本，选择 JavaScript 语言
3. 填入 matcher 和 trigger 函数
4. 保存并**启用**

### 采集器配置

1. 管理端 → 事件规则 → 事件源
2. 新增采集器，选择对应的产品和设备分组
3. 配置采集频率（建议 5~10 秒）

> 联动脚本只有在采集器读取到数据时才会被触发。

## 常见问题

### 同一产品为什么需要分散到不同模拟器？

同产品的设备使用相同的寄存器地址（如温湿度传感器的温度都在地址 0），如果放在同一台模拟器上会冲突。分散到不同模拟器后，通过模拟器的 IP:Port 区分，寄存器地址不变也没问题。所以启动几台模拟器实例，同一产品最多就只能创建几台设备。

### 为什么每个子设备要一个独立的网关？

模拟器每个实例只能模拟一个从站地址，所以子设备的 device_sn 只能填 `1`。平台要求同一网关下的子设备 device_sn 必须唯一，因此一台网关只能挂一台子设备。

### 协议指令从哪里获取？

每个协议驱动的 `DeviceProtocolSupplier` 实现类中注册了可用的指令集，在事件/功能模型配表中的**协议指令对照表**中列出了常用驱动的指令编码。`direct` 字段的值必须与驱动注册的指令编码一致，可在驱动的源码中查看。

### 联动脚本为什么需要写功能模型？

联动脚本通过 `methods.invoke(cmd)` 执行指令。cmd 中的 `api->{功能编码}` 指定功能模型，程序根据功能模型编码自动识别协议指令。所以每个需要动态写值的属性都要有一个对应的写功能模型。
