package com.iteaj.framework.spi.iot.view;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Getter
@Setter
public class GlobalApiParams extends HashMap<String, Object> {

    @Transient
    private List<String> keys;

    public GlobalApiParams set(String key, Object value) {
        this.put(key, value);
        return this;
    }

    public String getKey() {
        return (String) this.get("key");
    }

    public String resolveKeyItem(int index) {
        if(keys == null) {
            keys = new ArrayList<>();
            final String key = getKey();
            if(key != null) {
                Collections.addAll(keys, key.split(DataItemOption.SPLIT));
            }
        }

        return index < this.keys.size() ? this.keys.get(index) : null;
    }
}
