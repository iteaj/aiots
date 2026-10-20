package com.iteaj.iboot.module.iot.collect;

import org.springframework.core.Ordered;

public interface CollectDataListener extends Ordered {

    @Override
    default int getOrder() {
        return 15168;
    }
}
