package com.iteaj.iboot.msn.lcd.utils;

import com.iteaj.util.CommonUtils;

public class LowCodeUtil {

    /**
     * 移除括号内的内容
     * @param val
     * @return
     */
    public static String removeBracket(String val) {
        if(CommonUtils.isNotBlank(val)) {
            int indexOf = val.indexOf("(");
            if(indexOf == -1) {
                indexOf = val.indexOf("（");
            }
            if(indexOf == -1) {
                indexOf = val.indexOf(",");
            }
            if(indexOf == -1) {
                indexOf = val.indexOf("，");
            }
            if(indexOf == -1) {
                indexOf = val.indexOf(":");
            }
            if(indexOf == -1) {
                indexOf = val.indexOf("：");
            }
            if(indexOf == -1) {
                indexOf = val.indexOf(" ");
            }

            if(indexOf > -1) {
                val = val.substring(0, indexOf);
            }
        } else {
            return "";
        }

        return val;
    }

    /**
     * 获取模块名
     * @param comment
     * @return
     */
    public static String getModuleName(String comment) {
        if(CommonUtils.isBlank(comment)) {
            return "";
        }

        String moduleName = removeBracket(comment);
        if(moduleName.endsWith("表")) {
            return moduleName.substring(0, moduleName.length() - 1);
        }
        return moduleName;
    }

    /**
     * 业务层接口首字母小写名称
     * @param serviceName
     * @return
     */
    public static String firstLowerServiceName(String serviceName) {
        if(CommonUtils.isBlank(serviceName)) {
            return "";
        }

        if(serviceName.startsWith("I")) {
            return serviceName.substring(1, 2).toLowerCase() + serviceName.substring(2);
        } else {
            return serviceName.substring(0, 1).toLowerCase() + serviceName.substring(1);
        }
    }
}
