package com.iteaj.framework.spi.iot;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.exception.FrameworkException;
import com.iteaj.framework.spi.iot.consts.DataType;

public class IotUtil {

    private static JSONObject resolveParams(String[] cmdItem, boolean isUidBlank) {
        int length = isUidBlank ? 1 : 2;
        JSONObject result = new JSONObject();
        if(cmdItem.length > length) {
            for (int i = length; i < cmdItem.length; i++) {
                String[] item = cmdItem[i].split("->");
                result.put(item[0], item[1]);
            }
        }

        return result;
    }

    public static ProtocolModelApiInvokeParam createParamByCmdCode(String cmdCode, String uid) {
        boolean isUidBlank = !cmdCode.contains("uid->");
        if(!cmdCode.startsWith("api") || (isUidBlank && StrUtil.isBlank(uid))) {
            throw new FrameworkException("指令码格式错误");
        }

        String[] cmdItem = cmdCode.split("&");
        if(cmdItem.length < 1) {
            throw new FrameworkException("指令码格式错误");
        }

        String modelApiCode = cmdItem[0].split("->")[1];
        uid = isUidBlank ? uid : cmdItem[1].split("->")[1];

        JSONObject params = resolveParams(cmdItem, isUidBlank);
        return new ProtocolModelApiInvokeParam(uid, params).addApiCode(modelApiCode);
    }

    public static String getModelCodePrefix(String productCode) {
        if(productCode.length() <= 5) {
            return productCode + "_";
        }

        StringBuilder sb = new StringBuilder();
        final char[] chars = productCode.toCharArray();
        sb.append(chars[0]);
        int limit = productCode.length() <= 20 ? 4 : 5;
        for (int i = 0; i < chars.length; i++) {
            if(Character.isLetter(chars[i])) {
                if(i % limit == 0) {
                    sb.append(chars[i]);
                }
            }
        }

        return sb.append('_').toString();
    }

    public static Object resolverBit(UpModelAttr attr, Object value) {
        if(attr.isBit()) {
            if(StrUtil.isBlank(attr.getPath())) {
                throw new IllegalArgumentException("位属性必须指定位地址");
            }

            if(value instanceof Number) {
                final long longValue = ((Number) value).longValue();
                final String[] split = attr.getPath().split("-");
                final int addBit = Integer.parseInt(split[0]);
                if(split.length == 1) {
                    final long l = longValue << 64 - addBit;
                    value = l >>> 63;
                } else { // 大于1
                    final int maxBit = Integer.parseInt(split[1]); // 最大位
                    final long l = longValue << 64 - maxBit;
                    value = l >>> 63 - (maxBit - addBit);
                }

                if(attr.getDataType() == DataType.t_boolean) {
                    return (long) value > 0;
                }
            }
        }

        return value;
    }
}
