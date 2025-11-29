package com.iteaj.framework.spi.iot.view.table;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.spi.iot.view.DataItemOption;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class TableApiParams {

    /**
     * 接口key
     */
    private String key;

    /**
     * 请求参数
     */
    private Map<String, Object> params;

    /**
     * @see #key key的拆分项列表
     * @ignore
     */
    @Transient
    private List<String> keys;

    public String getKey(int index) {
        return keys.get(index);
    }

    public void setKey(String key) {
        this.key = key;
        if(StrUtil.isNotBlank(key)) {
            Collections.addAll(keys = new ArrayList<>(), key.split(DataItemOption.SPLIT));
        }
    }

    public <T> T getParam(String key) {
        return (T) getParams().get(key);
    }

    public Integer getSize() {
        Object size = getParam("size");
        return size != null ? Integer.parseInt(size.toString()) : 10;
    }

    public Integer getCurrent() {
        Object current = getParam("current");
        return current != null ? Integer.parseInt(current.toString()) : 1;
    }
}
