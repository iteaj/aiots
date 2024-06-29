package com.iteaj.framework.spi.iot.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseTimeCount {

    private String time;

    private Object value;

    public BaseTimeCount() { }

    public BaseTimeCount(String time, Object value) {
        this.time = time;
        this.value = value;
    }

    public static BaseTimeCount buildZero(String time) {
        return new BaseTimeCount(time, 0);
    }
}
