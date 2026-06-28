---
name: tutorial
description: AIots物联网平台操作教程，包含设计理念和操作指引——驱动配置、网络组件、产品与物模型、设备管理、数据中心、告警中心、事件规则、数据可视化、脚本引擎、设备调试、北向接入、快速入门。当用户问"平台怎么用"、"怎么配驱动/产品/物模型"、"设备怎么接入"、"驱动和网络组件区别"、"物模型容器类型是什么"、"计算属性和数据解析区别"、"控制状态是什么"、"模拟场景搭建"、"脚本引擎过滤/解析/联动怎么写"、"场景联动怎么配"、"告警怎么配置"、"北向接入是什么"时使用。
role: 平台使用指导
---

# 设计理念：模型驱动，而非代码驱动

## 传统做法：每步都要写代码

一台设备从接进来、到能在屏幕上看到数据，通常要走这几步：

```
设备数据 → 写代码解析协议 → 写代码转换数据 → 写控制逻辑 → 写界面展示
```

每一步都离不开程序员。换一个设备、加一个采集点、改一个控制逻辑，又得找程序员改代码。一个项目做完，下一个项目还得从头来一遍。

## 平台的做法：配配置就行

平台把"写代码"变成了"填表单"：

```
设备数据 → 驱动（选一个）→ 物模型（配一下）→ 采集控制（设个频率）→ 界面自动出来
```

整个过程在页面上点一点、选一选就完成了，不需要写一行代码。

打个比方：传统做法就像**自己造手机**——从焊电路板开始，每个零件都要自己弄。平台的做法就像**买一部智能手机**——系统已经装好了，你要做的只是装个 App、设置一下就能用。

## 核心：物模型就是"翻译官"

平台分三层，各干各的活：

```
┌─────────────────────────────────────┐
│  界面层 — 你看到的样子，自动生成      │
├─────────────────────────────────────┤
│  物模型层 — 你说的话，设备听得懂      │
│  属性（有什么数据）+ 字典（值啥意思）  │
│  功能模型（能做什么操作）              │
│  事件模型（采什么、多久采一次）         │
├─────────────────────────────────────┤
│  驱动层 — 跟设备打交道               │
│  Modbus / PLC / OPC UA ...          │
└─────────────────────────────────────┘
```

- **驱动层**：负责和设备"说话"。它知道怎么用 Modbus 读数据、怎么用 PLC 写指令，但它不关心这些数据是什么意思。
- **物模型层**：负责"翻译"。它告诉驱动：去读哪个地址、读回来的数据叫什么名字（温度还是压力）、值代表什么意思（1 是开、0 是关）。反过来，操作员在界面上点"打开阀门"，物模型把它翻译成驱动能懂的指令，发给设备执行。
- **界面层**：物模型配好之后，界面自动就生成了——设备列表显示什么、详情页有哪些数据、控制面板有哪些按钮，全由物模型决定。

再打个比方：驱动就像**快递员**——他负责把包裹从 A 送到 B，但他不知道里面装的是什么。物模型就是包裹上的**快递单**——写着"这是什么、送到哪、怎么处理"。界面就是**收件人看到的物流信息**——自动显示包裹到哪了、预计啥时候到。

## 这样做的几个好处

**第一个项目**：配驱动、建产品、定义物模型、创建设备、配置采集器——全部在页面上点一点就完成，零代码。

**第二个项目**：同类型的设备直接用之前配好的模板，新设备只需要建个新产品、改改物模型就行。驱动不用重配，界面自动就有了。

**日常运维**：想加一个采集点 → 物模型加个属性；想改控制逻辑 → 物模型改个绑定。不需要提需求、等排期、改代码、重新发布。

## 配一次，手机和电脑都能用

物模型配置完成后，管理端（PC 后台）和移动端（手机 App）的界面会自动适配——哪些数据要展示、控制面板有哪些按钮，都由物模型决定。不用给 PC 和手机分别开发两套界面。

# 操作教程（点击标题查看详情）

详细内容已按模块拆分到 `references/` 目录：

| 模块          | 参考文档 | 关键内容 |
|-------------|---------|---------|
| 整体流程 & 快速入门 | [quick-start.md](./references/quick-start.md) | 接入设备的完整步骤；温度传感器从创建到查看数据的 7 步示例 |
| 协议驱动配置      | [protocol-driver.md](./references/protocol-driver.md) | Modbus/PLC/OPC UA/DTU/MQTT/IEC104 等驱动的配置方法 |
| 网络组件配置      | [network-component.md](./references/network-component.md) | 通信链路配置（TCP/UDP/MQTT），网关挂载 |
| 产品配置与物模型    | [product-config.md](./references/product-config.md) | 产品创建、物模型属性/容器类型/字典、功能模型、事件模型、计算属性、控制状态 |
| Modbus 物模型配置  | [model-config-modbus.md](./references/model-config-modbus.md) | Modbus TCP/RTU 驱动支持的指令、上下行配置项、网关/子设备配置参数 |
| PLC 物模型配置     | [model-config-plc.md](./references/model-config-plc.md) | 西门子/欧姆龙/三菱/松下/汇川/罗克韦尔 PLC 共用的指令集，各品牌网关配置差异 |
| OPC UA 物模型配置  | [model-config-opcua.md](./references/model-config-opcua.md) | OPC UA 驱动支持的指令、上下行配置、节点地址配置 |
| MQTT 物模型配置    | [model-config-mqtt.md](./references/model-config-mqtt.md) | MQTT 发布/订阅的指令与上下行配置、主题占位符说明 |
| DTU Modbus 透传    | [model-config-dtu-modbus.md](./references/model-config-dtu-modbus.md) | DTU+Modbus TCP/RTU/MQTT 透传的指令集与网关配置 |
| DTU 私有协议       | [model-config-dtu-private.md](./references/model-config-dtu-private.md) | DTU 私有协议的读/写指令、数据格式配置 |
| 设备管理        | [device-management.md](./references/device-management.md) | 直连/网关/子设备、Modbus 批量采集、设备分组 |
| 运行管控        | [running-monitor.md](./references/running-monitor.md) | 运行展板、电子地图 |
| 数据中心        | [data-center.md](./references/data-center.md) | 模型数据、产品数据、通用报表 |
| 告警中心        | [alert-center.md](./references/alert-center.md) | 告警配置、告警模板、告警记录 |
| 事件规则        | [event-rules.md](./references/event-rules.md) | 采集器（按频率采集）、执行器（定时触发配合场景联动）、场景联动规则 |
| 数据可视化       | [data-visualization.md](./references/data-visualization.md) | 组态大屏、官方大屏、大屏素材 |
| 脚本引擎        | [script-engine.md](./references/script-engine.md) | 数据过滤、数据解析、联动脚本（含代码示例和工业场景） |
| 设备维护        | [device-debug.md](./references/device-debug.md) | 设备调试步骤 |
| 北向接入        | [northbound-access.md](./references/northbound-access.md) | 开放平台 API、数据转发（Kafka/MQTT/HTTP） |
| 模拟场景搭建      | [simulation-env.md](./references/simulation-env.md) | 无设备时创建完整模拟工厂，用于评估和演示 |
