package com.iteaj.framework.spi.open;

import jakarta.servlet.ServletRequest;

public class OpenApiUtil {

    public static final String OPEN_API_TAG = "OpenApiRequest";
    public static final String PASS_API_TAG = "PassApiRequest";

    public static boolean isOpenRequest(ServletRequest request) {
        return request.getAttribute(OPEN_API_TAG) != null;
    }

    public static void buildOpenRequest(ServletRequest request) {
        request.setAttribute(OPEN_API_TAG, Boolean.TRUE);
    }

    public static boolean isPassRequest(ServletRequest request) {
        return request.getAttribute(PASS_API_TAG) != null;
    }

    public static void buildPassRequest(ServletRequest request) {
        request.setAttribute(PASS_API_TAG, Boolean.TRUE);
    }
}
