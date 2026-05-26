// =====模块模板文件 =====
// - 根据实际情况修改 xxx 为实际功能名称
// - 字段、URL 需与后端保持一致

import type { Page } from "@/.cool";

// ====== 接口地址 ======

/** 获取xxx列表 */
export const URL_XXX_LIST = "/m/xxx/list";
/** 获取xxx详情 */
export const URL_XXX_DETAIL = "/m/xxx/detail";

// ====== 类型定义 ======

/**
 * xxx实体
 */
export interface Xxx {
  /** 实体类字段列表 */

}

/**
 * xxx列表请求参数
 */
export interface XxxReqParams {
  /** 请求参数字段列表 */
}

/**
 * xxx详情响应数据
 */
export interface XxxDetail extends Xxx {
  /** 响应数据字段列表 */

}

/**
 * xxx分页响应
 */
export interface XxxPage {
  total: number;
  records: Xxx[];
}
