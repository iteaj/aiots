package com.iteaj.framework.spi.iot.listener.filter;

/**
 * 脚本内容
 */
public interface EvalScript {

    /**
     * 脚本语言
     * @return
     */
    String getLanguage();

    /**
     * 脚本
     * @return
     */
    String getScript();

}
