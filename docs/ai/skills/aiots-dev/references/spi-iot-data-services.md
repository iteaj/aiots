# spi/iot — 业务数据服务接口

包路径：`com.iteaj.framework.spi.iot.data`

这些接口由 framework 定义，在 bootstrap `module/iot` 中完成实现，以 Spring Bean 形式注入。
其他插件通过**构造器注入**即可直接调用这些服务获取物联网业务数据。

---

## IModelAttrRealtimeDataService

获取设备实时状态与最新采集数据。

| 方法 | 说明 |
|------|------|
| `getRealtimeData(uid, signalOrField)` | 获取设备某属性的最新采集值（`ModelAttrRealtimeData`） |
| `getDeviceStatus(uid)` | 按设备 uid 获取实时状态（`RealtimeDeviceStatus`） |
| `getDeviceStatus(protocolCode, deviceSn)` | 按协议码+设备编号获取实时状态 |
| `getDeviceStatus(protocolCode, deviceSn, parentDeviceSn)` | 获取网关子设备实时状态 |
| `listByProductCode(productCode)` | 获取指定产品下所有设备状态列表 |

---

## DataCollectPublishService

发布采集数据，触发实时推送、存储、告警等下游流程。

| 方法 | 说明 |
|------|------|
| `publish(supplier, device)` | 发布采集数据 |
| `publish(supplier, device, type)` | 发布采集数据并指定类型（`data_cmd` / `data_active`） |

---

## ProductModelService

查询产品物模型相关数据（产品、属性、指令、接口）。

| 方法 | 说明 |
|------|------|
| `listProducts(product)` | 按条件查询产品列表，返回 `List<ProductMeta>` |
| `getProductModel(productCode)` | 获取完整产品模型（含属性列表、指令列表、接口列表） |
| `listDirectMeta(productCode)` | 获取产品的指令码列表（`ModelDirectMeta`） |
| `listModelApiMeta(productCode)` | 获取产品的模型接口列表（`ModelApiMeta`） |

---

## ModelAttrMetaService

获取并缓存产品属性元数据，主要用于数据解析时的属性查询。

| 方法 | 说明 |
|------|------|
| `getAttrMetas(productCode)` | 获取产品所有属性元数据 Map（key = 属性 id，value = `ModelAttrMeta`） |
| `getAttrMeta(productCode, modelAttrId)` | 获取单个属性元数据 |
| `removeAttrMetas(productCode)` | 清除产品属性元数据缓存（产品变更时调用） |

---

## RealtimeCollectDataService

查询历史采集数据，主要用于前端图表展示。

| 方法 | 说明 |
|------|------|
| `detailOfPage(page, entity)` | 分页查询采集记录 |
| `productOfPage(page, entity)` | 按产品模型分页查询采集记录 |
| `listOfCommonByTime(condition)` | 按时间条件查询采集值列表（`CollectValueItem`） |
| `countOfCommonByTime(condition)` | 按时间统计采集数量，返回图表数据（`EchartsCountValue`） |
| `countLastMonth()` | 统计最近一个月每日数据量 |
| `countLastWeek(deviceGroupId)` | 统计最近一周每日数据量 |

---

## CollectWarnDataService

查询和管理告警数据。

| 方法 | 说明 |
|------|------|
| `detailPage(page, entity)` | 分页查询告警记录 |
| `batchRemove(ids)` | 批量删除告警 |
| `listOfCommonByTime(condition)` | 按时间条件查询告警列表（`CollectValueItem`） |
| `countOfCommonByTime(condition)` | 按时间统计告警数量，返回图表数据（`EchartsCountValue`） |
