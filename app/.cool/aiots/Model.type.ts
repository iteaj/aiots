// 物模型通用类型定义
// 本文件由 AI 直接生成，无需通过 MCP 获取

/**
 * 物模型属性定义
 * 所有产品共用此结构，通过 productCode 区分不同产品的属性列表
 */
export interface ModelProperty {
  /** 属性标识，如 temperature */
  id: string;
  /** 属性名称，如 温度 */
  name: string;
  /** 数据类型 */
  dataType: "int" | "float" | "bool" | "string" | "enum" | "object";
  /** 单位，如 °C、% */
  unit?: string;
  /** 访问模式：r=只读, rw=读写 */
  accessMode: "r" | "rw";
  /** 取值范围 */
  range?: any;
  /** 步进值 */
  step?: number;
  /** 属性说明 */
  description?: string;
  /** 标签，用于分组 */
  tags?: string[];
}

/**
 * 物模型指令定义
 * 所有产品共用此结构
 */
export interface ModelCommand {
  /** 指令码，如 SET_REPORT_INTERVAL */
  cmdCode: string;
  /** 指令名称，如 设置上报周期 */
  cmdName: string;
  /** 指令说明 */
  cmdRemark?: string;
}

/**
 * 产品物模型
 * 通过 productCode 获取指定产品的完整物模型定义
 */
export interface ProductModel {
  /** 产品编码 */
  productCode: string;
  /** 产品名称 */
  productName?: string;
  /** 属性列表 */
  properties: ModelProperty[];
  /** 指令列表 */
  commands: ModelCommand[];
}

/**
 * 设备实时数据
 * values 的 key 为 propertyId，value 为属性值
 */
export interface DeviceRuntimeData {
  /** 设备ID */
  deviceId: string;
  /** 产品编码 */
  productCode: string;
  /** 属性值映射 */
  values: Record<string, any>;
  /** 更新时间 */
  updateTime: string;
}

/**
 * 指令下发请求
 */
export interface CommandRequest {
  /** 指令码 */
  cmdCode: string;
  /** 指令参数，根据指令定义动态填充 */
  params?: Record<string, any>;
}

/**
 * 指令下发结果
 */
export interface CommandResult {
  /** 是否成功 */
  success: boolean;
  /** 任务ID，用于查询执行状态 */
  taskId?: string;
  /** 提示信息 */
  message?: string;
}

/**
 * 属性更新请求
 */
export interface PropertyUpdateRequest {
  /** 属性ID */
  propertyId: string;
  /** 属性值 */
  value: any;
}
