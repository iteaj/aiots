package com.iteaj.plguin.protocol.hj212.network;

import cn.hutool.core.date.DateUtil;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.iot.message.DefaultMessageBody;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class HJ212MessageBody extends DefaultMessageBody {

    private final Map<String, String> data;

    public HJ212MessageBody(Map<String, String> data) {
        this.data = data;
    }

    public String getItem(String key) {
        return data != null ? data.get(key) : null;
    }

    /**
     * 1 准备执行请求
     * 2 请求被拒绝
     * 3 PW 错误
     * 4 MN 错误
     * 5 ST 错误
     * 6 Flag 错误
     * 7 QN 错误
     * 8 CN 错误
     * 9 CRC 校验错误
     * 100 未知错误
     * @return QnRtn
     */
    public String getQnRtn() {
        return getItem("QnRtn");
    }

    public boolean isQnRtnSuccess() {
        String qnRtn = getQnRtn();
        return qnRtn != null && Objects.equals(qnRtn, "1");
    }

    public String getQnRtnMsg() {
        String qnRtn = getQnRtn();
        if(qnRtn == null) return "";
        switch (qnRtn) {
            case "1": return "准备执行请求";
            case "2": return "请求被拒绝";
            case "3": return "密码错误";
            case "4": return "设备编号错误";
            case "5": return "ST 错误";
            case "6": return "Flag 错误";
            case "7": return "QN 错误";
            case "8": return "CN 错误";
            case "9": return "CRC 校验错误";
            case "100": return "未知错误";

            default: return "未知的执行结果码: " + qnRtn;
        }
    }

    /**
     * 1 执行成功
     * 2 执行失败，但不知道原因
     * 3 命令请求条件错误
     * 4 通讯超时
     * 5 系统繁忙不能执行
     * 6 系统故障
     * 100 没有数据
     * @return
     */
    public String getExeRtn() {
        return getItem("ExeRtn");
    }

    public String getExeRtnMsg() {
        String exeRtn = getExeRtn();
        if(exeRtn == null) return "";

        switch (exeRtn) {
            case "1": return "执行成功";
            case "2": return "执行失败，但不知道原因";
            case "3": return "命令请求条件错误";
            case "4": return "通讯超时";
            case "5": return "系统繁忙不能执行";
            case "6": return "系统故障";
            case "100": return "没有数据";
            default: return "未知的执行结果码: " + exeRtn;
        }
    }

    public boolean isExeRtnSuccess() {
        String exeRtn = getExeRtn();
        return exeRtn != null && Objects.equals(exeRtn, "1");
    }

    public Date getCollectTime() {
        return data != null ? DateUtil.parse(data.get("DataTime"), "yyyyMMddHHmmss") : null;
    }

    public Map<String, String> getData() {
        return data;
    }
}
