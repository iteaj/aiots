// =====接口声明模板文件 =====
// - 请根据实际情况修改模板中的xxx为实际功能名称
// - api接口要和后端接口保持一致包括: 接口路径、请求方法、请求参数、响应参数等
/**
 * xxx模块接口声明文件
 */
import type { Xxx, XxxReqParams, XxxDetail } from './xxx.type';
import { Page, PageParams } from './common.type';
import { request } from "../service";

/**
 * xxx模块接口
 */
export const xxxApi = {
  /**
   * 获取xxx列表
   * @param params 查询参数
   * @returns xxx列表响应
   */
  getXxxList(params: XxxReqParams): Promise<Xxx[]> {
    return request({
      url: '/m/xxx/list',
      method: 'GET',
      data: params
    });
  },

  /**
   * 获取xxx列表
   * @param params 查询参数
   * @returns xxx列表响应
   */
  getXxxPage(params: XxxReqParams & PageParams): Promise<Page<Xxx>> {
    return request({
      url: '/m/xxx/page',
      method: 'GET',
      data: params
    });
  },

  /**
   * 获取xxx详情
   * @param id xxxID
   * @returns xxx详情响应
   */
  getXxxDetail(id: string): Promise<XxxDetail> {
    return request({
      url: '/m/xxx/detail',
      method: 'GET',  
      data: {
        id
      }
    });
  },
}