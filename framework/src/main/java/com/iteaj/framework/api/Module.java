package com.iteaj.framework.api;

import java.io.Serializable;

public interface Module {

    /**
     * 获取模块编码, 必须唯一
     * @return
     */
    Serializable getMsn();

    /**
     * 模块名称
     * @return
     */
    String getName();

    /**
     * 模块版本
     * @return
     */
    String getVersion();

    /**
     * 模块简介
     * @return
     */
    String getIntroduce();
}
