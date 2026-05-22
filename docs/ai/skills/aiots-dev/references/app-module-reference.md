# App模块(移动端后端)接口明细

## 模块概述
- **模块名称**:App模块
- **功能范围**:移动端后端接口,为APP提供精简数据接口
- **主要业务**:设备查询、实时数据、告警处理、系统管理等移动端业务
- **模块rest api前缀**:/m
- **模块接口所在包**:各业务模块的 `app/controller/` 或插件内 `app/controller/`

## 接口规范

### 移动端接口4件套

| 操作 | 路径 | Method | 参数 | 返回类型 |
|------|------|--------|------|----------|
| 分页查询 | `/page` | GET | Page + EntityDto | `Result<IPage<Entity>>` |
| 列表查询 | `/list` | GET | Entity | `Result<List<Entity>>` |
| 编辑记录 | `/edit` | GET | `id` | `Result<Entity>` |
| 详情(含关联) | `/detail` | GET | `id` | `Result<EntityDto>` |

## 功能列表

> 当前App模块后端接口较少,随着移动端功能迭代会持续补充。

| 控制器 | 功能简介 | 实体类 | 所属业务模块 |
|--------|----------|--------|------------|
| AppDeviceController | 设备查询 | Device | IoT |
| AppRealtimeDataController | 实时数据查询 | - | IoT |
| AppWarnRecordController | 告警记录查询 | WarnRecord | IoT |

## 使用说明

1. 移动端Controller命名规范:`AppXxxController`
2. REST路径前缀:`/m/xxx`
3. 详细开发规范见 [app-module-quick-ref.md](./app-module-quick-ref.md)
