package com.iteaj.framework.spi.open;

import javax.servlet.ServletRequest;

public class OpenApiUtil {

    public static final String OPEN_API_TAG = "OpenApiRequest";

    public static boolean isOpenRequest(ServletRequest request) {
        return request.getAttribute(OPEN_API_TAG) != null;
    }

    public static void buildOpenRequest(ServletRequest request) {
        request.setAttribute(OPEN_API_TAG, true);
    }
}
