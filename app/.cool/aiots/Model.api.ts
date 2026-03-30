// 物模型接口封装
import { request } from "../service";

import type {
  ProductModel,
  DeviceRuntimeData,
  CommandRequest,
  CommandResult,
  PropertyUpdateRequest
} from "./Model.type";

/**
 * 物模型相关接口
 * 所有产品共用一套接口，通过 productCode 区分不同产品
 */
export const ModelApi = {
  /**
   * 获取产品物模型定义
   * @param productCode 产品编码
   * @returns Promise<ProductModel>
   */
  getProductModel(productCode: string): Promise<ProductModel> {
    return request({
      url: `/api/model/${productCode}`,
      method: "GET"
    });
  },

  /**
   * 获取设备实时数据
   * @param deviceId 设备ID
   */
  getRuntimeData(deviceId: string): Promise<DeviceRuntimeData> {
    return request({
      url: `/api/device/${deviceId}/runtime`,
      method: "GET"
    });
  },

  /**
   * 下发指令
   * @param deviceId 设备ID
   * @param data 指令请求
   */
  sendCommand(deviceId: string, data: CommandRequest) {
    return request<CommandResult>({
      url: `/api/device/${deviceId}/command`,
      method: "POST",
      data
    });
  }
};
