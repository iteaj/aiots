// 用户模块类型声明与接口地址

import type { UserCategory } from "@/.cool";

// ====== 接口地址 ======

export const UserApi = {
  /** 获取验证码 */
  CAPTCHA: "/app/user/login/captcha",
  /** 发送短信验证码 */
  SMS_CODE: "/app/user/login/smsCode",
  /** 手机号验证码登录 */
  PHONE_LOGIN: "/app/user/login/phone",
  /** 账号密码登录 */
  ACCOUNT_LOGIN: "/m/login",
  /** 微信小程序授权登录 */
  WX_MA_AUTHORIZE: "/wx/ma/authorize",
  /** 微信小程序绑定管理端账号 */
  WX_MA_BIND: "/m/login/bind",
};

// ====== 类型定义 ======

/**
 * 登录表单（手机号+验证码）
 */
export type LoginForm = {
	phone: string;
	smsCode: string;
};

/**
 * 账号密码登录请求
 */
export interface AccountLoginForm {
  /** 管理端账号 */
  account: string;
  /** 密码 */
  password: string;
}

/**
 * 账号密码登录响应
 */
export interface AccountLoginResult {
  /** 访问token */
  token: string;
  /** token过期时间（秒） */
  expire: number;
  /** token名称 */
  tokenName: string;
}

/**
 * 小程序登录请求参数
 */
export interface WechatMaLoginParam {
  /** 临时登录凭证 */
  code: string;
  /** 用户类别：ADMIN-管理端 MEMBER-会员端 */
  userCategory: UserCategory;
}

/**
 * 公众号绑定请求
 */
export interface MpBindParam {
  /** 微信openId */
  openId: string;
  /** 管理端账号 */
  account: string;
  /** 密码 */
  password: string;
}

/**
 * 第三方社交用户信息
 */
export interface SocialUserInfo {
  /** 主键 */
  id: number;
  /** 平台标识 */
  platform: string;
  /** 平台用户唯一标识 */
  openId: string;
  /** 平台统一标识 */
  unionId?: string;
  /** 关联系统用户ID */
  systemUserId?: number;
  /** 昵称 */
  nickname?: string;
  /** 头像 */
  avatar?: string;
  /** 手机号 */
  phone?: string;
  /** 性别：0-未知 1-男 2-女 */
  gender?: number;
}
