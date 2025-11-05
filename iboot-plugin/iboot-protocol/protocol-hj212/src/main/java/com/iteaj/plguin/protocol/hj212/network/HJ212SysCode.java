package com.iteaj.plguin.protocol.hj212.network;

import com.iteaj.framework.spi.iot.protocol.ProtocolModelApiConfigOption;
import com.iteaj.iot.ProtocolException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public enum HJ212SysCode {

    地表水质量监测("21"),
    空气质量监测("22"),
    声环境质量监测("23"),
    地下水质量监测 ("24"),
    土壤质量监测 ("25"),
    海水质量监测 ("26"),
    挥发性有机物监测 ("27"),
    大气环境污染源 ("31"),
    地表水体环境污染源 ("32"),
    地下水体环境污染源 ("33"),
    海洋环境污染源 ("34"),
    土壤环境污染源 ("35"),
    声环境污染源 ("36"),
    振动环境污染源 ("37"),
    放射性环境污染源 ("38"),
    工地扬尘污染源 ("39"),
    电磁环境污染源 ("41"),
    烟气排放过程监控 ("51"),
    污水排放过程监控 ("52"),
    系统交互 ("91")
    ;

    private String code;
    HJ212SysCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static HJ212SysCode getByCode(String code) {
        for (HJ212SysCode item : HJ212SysCode.values()) {
            if (Objects.equals(code, item.getCode())) {
                return item;
            }
        }

        throw new ProtocolException("不支持的系统编码["+code+"]");
    }

    public static List<ProtocolModelApiConfigOption> options() {
        return Arrays.stream(values())
                .map(item -> new ProtocolModelApiConfigOption(item.name() + "("+item.getCode()+")", item.getCode()))
                .collect(Collectors.toList());
    }
}
