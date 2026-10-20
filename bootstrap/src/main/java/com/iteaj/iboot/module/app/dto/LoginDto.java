package com.iteaj.iboot.module.app.dto;

import lombok.Data;

/**
 * 移动端登录DTO
 * @author iteaj
 */
@Data
public class LoginDto {
    /** 账号 */
    private String account;
    /** 密码 */
    private String password;
}
