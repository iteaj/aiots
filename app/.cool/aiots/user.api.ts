// 用户登录接口封装
import { request } from "../service";

import type { AccountLoginForm, AccountLoginResult, MpBindParam, WechatMaLoginParam } from "./user.type";

/**
 * 用户登录相关接口
 */
export const UserApi = {
  /**
   * 账号密码登录（管理端用户）
   * @param data 账号密码
   * @returns Promise<AccountLoginResult>
   */
  accountLogin(data: AccountLoginForm): Promise<AccountLoginResult> {
    return request({
      url: "/m/login",
      method: "POST",
      data
    });
  },

  /**
   * 小程序登录
   * @param params 登录参数
   * @returns Promise<any>
   */
  maLogin(params: WechatMaLoginParam): Promise<any> {
    return request({
      url: "/wechat/ma/login",
      method: "POST",
      data: params
    });
  },

  /**
   * 公众号绑定（账号密码+openId）
   * @param data 绑定参数
   * @returns Promise<any>
   */
  bindMp(data: MpBindParam): Promise<any> {
    return request({
      url: "/wechat/mp/bind",
      method: "POST",
      data
    });
  }
};
