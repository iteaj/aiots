# IoT模块技能书明细

## 模块概述
- **模块名称**：IoT模块
- **功能范围**：物联网设备管理和数据处理
- **主要业务**：设备管理、数据采集、协议通信、物模型管理、联动控制等
- **表前缀**：iot_
- **模块rest api前缀**：/iot
- **模块接口所在包**：com.iteaj.iboot.module.iot.controller

## 功能列表

| 控制器 | 功能简介 | 实体类 | 数据库表名 |
|--------|----------|--------|------------|
| DeviceController | 设备基础管理 | Device | iot_device |
| DeviceChildController | 子设备管理 | DeviceChild | iot_device_child |
| DeviceGroupController | 设备分组管理 | DeviceGroup | iot_device_group |
| DeviceCtrlController | 设备控制管理 | Device | iot_device |
| DeviceDebugController | 设备调试管理 | - | - |
| DeviceDlt645Controller | DLT645协议设备管理 | Device | iot_device |
| DeviceDtuController | DTU设备管理 | Device | iot_device |
| DeviceModbusController | Modbus协议设备管理 | Device | iot_device |
| DeviceMqttController | MQTT协议设备管理 | Device | iot_device |
| DeviceOpcUaController | OPC UA协议设备管理 | Device | iot_device |
| DevicePlcController | PLC设备管理 | Device | iot_device |
| SerialController | 串口管理 | Serial | iot_serial |
| CollectDataController | 采集数据管理 | CollectData | iot_collect_data |
| CollectDetailController | 采集明细管理 | CollectDetail | iot_collect_detail |
| CollectTaskController | 采集任务管理 | CollectTask | iot_collect_task |
| RealtimeDataController | 实时数据管理 | - | - |
| ProductController | 产品管理 | Product | iot_product |
| ProductTypeController | 产品类型管理 | ProductType | iot_product_type |
| ProtocolController | 协议管理 | Protocol | iot_protocol |
| ProtocolApiController | 协议接口管理 | ProtocolApi | iot_protocol_api |
| ProtocolApiConfigController | 协议接口配置管理 | ProtocolApiConfig | iot_protocol_api_config |
| ProtocolAttrController | 协议属性管理 | ProtocolAttr | iot_protocol_attr |
| GatewayController | 网关管理 | Gateway | iot_gateway |
| ModelApiController | 物模型接口管理 | ModelApi | iot_model_api |
| ModelApiConfigController | 物模型接口配置管理 | ModelApiConfig | iot_model_api_config |
| ModelAttrController | 物模型属性管理 | ModelAttr | iot_model_attr |
| LinkageController | 联动规则管理 | Linkage | iot_linkage |
| LinkageActionController | 联动动作管理 | LinkageAction | iot_linkage_action |
| LinkageConditionController | 联动条件管理 | LinkageCondition | iot_linkage_condition |
| LinkageConditionItemController | 联动条件项管理 | LinkageConditionItem | iot_linkage_condition_item |
| EventSourceController | 事件源管理 | EventSource | iot_event_source |
| EventSourceDetailController | 事件源详情管理 | EventSourceDetail | iot_event_source_detail |
| WarnConfigController | 告警配置管理 | WarnConfig | iot_warn_config |
| WarnRecordController | 告警记录管理 | WarnRecord | iot_warn_record |
| CommonCountController | 通用统计管理 | 多表 | 多表统计 |
| CommonValueController | 通用值管理 | - | - |
| EMapController | 电子地图管理 | - | - |
| PanelsController | 面板管理 | - | - |
| PointGroupController | 点位组管理 | PointGroup | iot_point_group |
| SignalController | 信号管理 | Signal | iot_signal |
| VLScreenController | 可视化大屏管理 | - | - |
| IotIndexController | 物联网首页管理 | 多表 | 多表统计 |

## 使用说明

此文件用于AI快速定位IoT模块信息，包含：
1. 模块名称和功能范围
2. 控制器及其功能简介
3. 对应的实体类
4. 数据库表名