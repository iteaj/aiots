package com.iteaj.framework.spi.admin;

/**
 * create time: 2020/4/21
 *
 * @author iteaj
 * @since 1.0
 */
public enum LoggerType {
    UnAuthor("未授权"), UnAuth("未认证"), Error("未知异常")
    , Success("操作成功"), Login("用户登录"), Logout("用户注销");

    public String desc;

    LoggerType(String desc) {
        this.desc = desc;
    }
}
