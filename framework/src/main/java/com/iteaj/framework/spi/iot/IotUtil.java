package com.iteaj.framework.spi.iot;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.iteaj.framework.exception.FrameworkException;

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
        if(cmdItem.length < 2) {
            throw new FrameworkException("指令码格式错误");
        }

        String modelApiCode = cmdItem[0].split("->")[1];
        uid = isUidBlank ? uid : cmdItem[1].split("->")[1];

        JSONObject params = resolveParams(cmdItem, isUidBlank);
        return new ProtocolModelApiInvokeParam(uid, params).addApiCode(modelApiCode);
    }
}
