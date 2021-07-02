package com.iteaj.framework.consts;

/**
 * create time: 2021/3/24
 *
 * @author iteaj
 * @since 1.0
 */
public interface CoreConst {

    String FRAMEWORK_FILTER_NAME = "framework";

    String WEB_USER_AGENT = "IZONE_WEB_USER_AGENT";

    String SESSION_AUTH_CONTEXT = "IZONE_SESSION_AUTH_CONTEXT";

    String WEB_SESSION_CACHE = "IZONE_WEB_SESSION_CACHE";

    String APP_SESSION_CACHE = "IZONE_APP_SESSION_CACHE";

    /**
     * 未认证状态
     */
    String AUTH_STATUS = "AUTH_STATUS";

    /**
     * 请求
     */
    String HTTP_SERVLET_REQUEST = "IZONE_HTTP_SERVLET_REQUEST";

    /**
     * 处理登录的动作
     */
    String HANDLE_LOGIN_ACTION = "IZONE_HANDLE_LOGIN_ACTION";

    /**
     * 处理授权动作
     */
    String HANDLE_AUTH_ACTION = "IZONE_HANDLE_AUTH_ACTION";

    /**
     * 在线用户标识
     */
    String ONLINE_ACCOUNT_ID = "IZONE_ONLINE_ACCOUNT_ID";
}
