package com.iteaj.framework.spi.app;

import cn.dev33.satoken.stp.StpUtil;
import com.iteaj.framework.Entity;

import java.io.Serializable;

/**
 * App用户登录安全工具类
 * <p>统一管理社交平台的SaToken登录登出，登录ID自动加前缀以防与系统用户ID冲突</p>
 * @author iteaj
 */
public class AppSecurityUtil {

    private static final String PREFIX = "APP:";

    /**
     * 社交用户登录
     * @param user 社交用户
     */
    public static void login(Entity<?> user) {
        StpUtil.login(PREFIX + user.getId(), "APP");
    }

    /**
     * 获取当前登录的社交用户ID
     * @return 社交用户ID（已去掉前缀）
     */
    public static Serializable getLoginId() {
        Object loginId = StpUtil.getLoginId();
        if (loginId instanceof String str) {
            return str.substring(PREFIX.length());
        }

        return null;
    }

    /**
     * 当前是否已社交用户身份登录
     */
    public static boolean isLogin() {
        if (!StpUtil.isLogin()) return false;
        Object loginId = StpUtil.getLoginId();
        return loginId instanceof String str && str.startsWith(PREFIX);
    }

    /**
     * 社交用户登出
     */
    public static void logout() {
        StpUtil.logout();
    }

    /**
     * 获取当前Token值
     */
    public static String getTokenValue() {
        return StpUtil.getTokenValue();
    }

    /**
     * 获取Token过期时间
     */
    public static long getTokenTimeout() {
        return StpUtil.getTokenTimeout();
    }

    /**
     * 获取Token名称
     */
    public static String getTokenName() {
        return StpUtil.getTokenName();
    }
}
