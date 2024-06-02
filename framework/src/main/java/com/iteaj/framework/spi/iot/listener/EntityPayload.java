package com.iteaj.framework.spi.iot.listener;

import com.iteaj.framework.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * 实体负载
 */
@Getter
@Setter
public class EntityPayload<T extends Entity> implements IotEventPayload{

    private T payload;

    public EntityPayload(T payload) {
        this.payload = payload;
    }
}
