package com.iteaj.framework.spi.iot.resolver;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.iteaj.framework.spi.iot.DataValueResolver;
import com.iteaj.framework.spi.iot.UpModelAttr;
import com.iteaj.framework.spi.iot.consts.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

public class DefaultResolver implements DataValueResolver {

    private static final String DEFAULT_NAME = "默认";
    private final Logger logger = LoggerFactory.getLogger(DefaultResolver.class);

    @Override
    public String name() {
        return DEFAULT_NAME;
    }

    @Override
    public Object resolver(UpModelAttr attr, Object value) {
        DataType dataType = attr.getDataType();
        try {
            if(value instanceof Number) {
                if(value instanceof Long) {
                    if(dataType == DataType.t_datetime) {
                        return new Date((Long) value);
                    }
                }

                return doResolverNumberValue(attr, value);
            } else if(value instanceof String) {
                if(dataType == DataType.t_date) {
                    return DateUtil.parse(value.toString(), DatePattern.NORM_DATETIME_PATTERN);
                } else if(dataType == DataType.t_double || dataType == DataType.t_float) {
                    return doResolverNumberValue(attr, Double.valueOf(value.toString()));
                } else if(isInteger(dataType)) {
                    return doResolverNumberValue(attr, Long.valueOf(value.toString()));
                }
            } else if(value != null){
                if(value instanceof Boolean || value instanceof Enum<?>) {
                    return value;
                } else if(dataType == DataType.t_json || dataType == DataType.t_group) {
                    return JSON.toJSON(value);
                } else if(dataType == DataType.t_string) {
                    return JSON.toJSONString(value);
                }
            }
        } catch (Exception e) {
            logger.error("解析数据格式异常({}) 解析器: {} - 属性: {} - 值: {}", dataType
                    , attr.getResolver(), attr.getField(), value, e);
        }
        return value;
    }

    private Object doResolverNumberValue(UpModelAttr attr, Object value) {
        if(attr.getGain() != null && attr.getGain() != 0) {
            value = Double.parseDouble(value.toString()) / attr.getGain();
        }

        if(attr.getAccuracy() != null && attr.getAccuracy() > 0) {
            if(attr.getDataType() == DataType.t_double || attr.getDataType() == DataType.t_float) {
                BigDecimal bigDecimal = BigDecimal.valueOf(Double.parseDouble(value.toString()));
                value = bigDecimal.setScale(attr.getAccuracy(), RoundingMode.DOWN).doubleValue();
            }
        }

        return value;
    }
}
