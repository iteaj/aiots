# 后端接口开发规范

**命名规则**：
- 实体类型：`{标识}.type.ts`（如 `device.type.ts`）
- 实体接口：`{标识}.api.ts`（如 `device.api.ts`）

## 类型定义生成策略

| 文件 | 生成方式 | 说明 |
|------|----------|------|
| `{实体}.type.ts` | 动态获取表实体字段 | 每个实体字段不同 |
| `{实体}.api.ts` | 基于类型文件生成 CRUD 接口 | 依赖类型定义 |

## 实体类型生成流程

```
1. 检查 {标识}.type.ts 是否存在
   ├─ 存在 → 读取并询问是否更新
   └─ 不存在 → 继续
2. 获取系统所有表实体列表
   ├─ 发现该实体 → 继续
   └─ 未发现 → 提示："系统中未定义 {标识} 实体？"
3. 获取表实体包含哪些字段('{标识}')
   → 获取 baseFields + extraFields
4. 生成 {标识}.type.ts
   → 根据返回的字段生成接口定义
5. 生成 {标识}.api.ts
   → 生成 CRUD 接口封装
```

## 实体类型模板以device为例

**device.type.ts**（字段从 MCP 获取）：
```typescript
// 表实体字段需要动态获取
export interface Device {
  // 表实体字段动态获取，不要自行假设
}

export interface DeviceListParams {
  page?: number;
  size?: number;
  // 搜索条件字段(从表实体字段列表里面筛选)
}
```
## 实体接口模板
**device.api.ts**：
```typescript
import { Page } from "./common.type";
import { request } from "../service";
import type { Device, DeviceListParams } from "./device.type";

export const deviceApi = {
  getList(params: DeviceListParams): Promise<Page<Device>> {
    return request<{ list: Device[]; total: number }>({
      url: "/api/device/list",
      method: "GET",
      data: params
    });
  },

  getDetail(id: string): Promise<Device> {
    return request({
      url: `/api/device/${id}`,
      method: "GET"
    });
  }
};
```

## 物模型类型（直接复用）

物模型属性通用结构已预定义在 `.cool/aiots/Model.type.ts`：

```typescript
// 从 .cool/aiots/Model.type.ts 导入
import type { 
  ModelProperty, 
  ModelCommand, 
  ProductModel,
  DeviceRuntimeData,
  CommandRequest 
} from "@/.cool/aiots/Model.type";
```

**核心类型说明**：
- `ModelProperty` - 物模型属性定义（所有产品共用）
- `ModelCommand` - 物模型指令定义（所有产品共用）
- `ProductModel` - 产品物模型（通过 `productCode` 区分不同产品）
- `DeviceRuntimeData` - 设备实时数据（`values` 字段动态填充）
- `CommandRequest` - 指令下发请求

**使用示例**：
```typescript
import { Model } from "@/.cool/aiots/Model";

// 获取产品物模型属性列表
const model = await Model.getProductModel("HT-SENSOR-01");

// 获取设备实时数据
const runtime = await Model.getRuntimeData("device-001");

// 下发指令
await Model.sendCommand("device-001", {
  cmdCode: "SET_REPORT_INTERVAL",
  params: { interval: 60 }
});
```

## 后端响应格式

```typescript
// 分页数据
interface Page<T> {
  records: T[];
  total: number;
  page: number;
  size: number;
}
```

## AI 开发功能时的约束

1. **实体类型必须先动态查询**
   - 字段必须动态获取
   - 若不存在该实体，提示用户先创建

2. **文件存在检查**
   - 生成前检查文件是否已存在
   - 不存在新建存在则继续往下处理

3. **命名严格遵循规范**
   - 实体：`{标识}.type.ts`
   - 接口：`{标识}.api.ts`
