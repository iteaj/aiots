package com.iteaj.framework.spi.admin;

import org.springframework.core.Ordered;

/**
 * create time: 2020/7/29
 *  通过此对象可以获取对应的资源信息
 * @author iteaj
 * @since 1.0
 */
public interface Module extends Ordered {

    /**
     * 模块编号
     * @return
     */
    String getMsn();

    /**
     * 模块编号对应的说明
     * @return
     */
    String getName();
}
