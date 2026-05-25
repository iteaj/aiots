// 用户登录相关类型定义

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
  /** 用户类别：1-管理端 2-会员端 */
  userCategory: number;
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
