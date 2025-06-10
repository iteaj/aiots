package com.iteaj.framework.spi.iot.view;

import java.util.List;

public interface ViewOptionsItem {

    /**
     * key
     * @return
     */
    String getKey();

    /**
     * 名称
     * @return
     */
    String getName();


    List resolver();

    /**
     * 动态加载option
     * @return
     */
    List<DataItemOption> loadOption();

    /**
     * 数据列表
     * @return
     */
    List<DataItemOption> getOptions();
}
