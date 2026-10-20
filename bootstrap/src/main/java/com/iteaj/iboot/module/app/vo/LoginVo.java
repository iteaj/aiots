package com.iteaj.iboot.module.app.vo;

import lombok.Data;

/**
 * 移动端登录VO
 * @author iteaj
 */
@Data
public class LoginVo {
    /** Token值 */
    private String token;
    /** Token过期时间 */
    private long expire;
    /** 刷新Token */
    private String refreshToken;
    /** 刷新Token过期时间 */
    private long refreshExpire;
    /** Token名称 */
    private String tokenName;
}
