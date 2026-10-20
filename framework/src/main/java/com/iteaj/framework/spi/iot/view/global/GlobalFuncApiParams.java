package com.iteaj.framework.spi.iot.view.global;

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
public class GlobalFuncApiParams {

    private String apiKey;

    private String apiValue;

    /**
     * api参数
     */
    private Map<String, String> params;

    @Transient
    private List<String> keys;

    public String getKey() {
        return resolveKeyItem(0);
    }

    public String getRequestParams(String key) {
        return params.get(key);
    }

    public String resolveKeyItem(int index) {
        if(keys == null) {
            keys = new ArrayList<>();
            final String key = getApiKey();
            if(key != null) {
                Collections.addAll(keys, key.split(DataItemOption.SPLIT));
            }
        }

        return index < this.keys.size() ? this.keys.get(index) : null;
    }
}
