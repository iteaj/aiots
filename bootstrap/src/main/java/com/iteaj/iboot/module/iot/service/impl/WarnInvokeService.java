package com.iteaj.iboot.module.iot.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.iteaj.iboot.module.iot.cache.entity.RealtimeStatus;
import com.iteaj.iboot.module.iot.collect.CollectListenerManager;
import com.iteaj.iboot.module.iot.consts.WarnGrade;
import com.iteaj.iboot.module.iot.entity.WarnConfig;
import com.iteaj.iboot.module.iot.entity.WarnRecord;
import com.iteaj.iboot.module.iot.linkage.LinkageActionListener;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class WarnInvokeService {

    private final Map<String, WarnInfo> warnStatus = new ConcurrentHashMap<>(512);

    public void sendWarn(WarnConfig warn, RealtimeStatus device, Object value) {
        WarnInvokeService.WarnInfo warnInfo = null;
        if(warn.getModelAttrId() != null) {
            warnInfo = warnStatus.get(device.getUid() + ":" + warn.getModelAttrId());
        }

        WarnGrade grade = warn.getGrade();
        // 属于解除告警并且已经执行一次
        if(grade == WarnGrade.normal) {
            // 等于null说明是第一次触发解除
            if(warnInfo == null) {
                return;
            }

            warnInfo.setStatus(true); // 已经解除告警
            warnStatus.remove(device.getUid() + ":" + warn.getModelAttrId()); // 移除告警信息
        } else if(grade != WarnGrade.status){ // 状态告警不涉及模型属性
            if(warnInfo == null) {
                warnStatus.put(device.getUid() + ":" + warn.getModelAttrId(), warnInfo = new WarnInvokeService.WarnInfo()); // 持续告警中
            }
        }

        // 如果是第一次告警或者超出了频率范围就发送告警
        if(warnInfo == null || warnInfo.isFirst() || warnInfo.isExpire(warn.getFrequency())) {
            if(warnInfo != null) {
                warnInfo.trigger();
            }

            Date date = new Date();
            String warnValue = value == null ? StrUtil.EMPTY : value.toString();
            String content = resolverWarnTemplate(warn, device.getDeviceSn(), warnValue, date, device.getDeviceName());
            WarnRecord warnRecord = new WarnRecord().setDeviceSn(device.getDeviceSn()).
                    setUid(device.getUid()).setCreateTime(date).setWarnTime(date).setGrade(grade)
                    .setWarnConfigId(warn.getId()).setContent(content).setField(warn.getModelAttrField())
                    .setProductCode(device.getProductCode()).setValue(warnValue);

            CollectListenerManager.getInstance().publish(listener -> {
                if(listener instanceof LinkageActionListener) {
                    ((LinkageActionListener) listener).warnTrigger(warn, warnRecord, device);
                }
            });
        }
    }


    /**
     * 解析告警配置模板
     * @param warn
     * @param warnTime
     * @param deviceName
     * @return
     */
    private String resolverWarnTemplate(WarnConfig warn, String deviceSn, String value, Date warnTime, String deviceName) {
        Map<String, String> param = new HashMap<>();
        param.put("value", value);
        param.put("deviceSn", deviceSn);
        param.put("deviceName", deviceName);
        param.put("grade", warn.getGrade().toString());
        param.put("warnTime", DateUtil.format(warnTime, "yy-MM-dd HH:mm:ss"));

        return StrUtil.format(warn.getTemplate(), param, true);
    }

    public static class WarnInfo {

        /**
         * 第一次告警的毫秒数
         */
        private long firstOfMs;

        /**
         * 解除状态(true 解除, false 未解除)
         */
        private boolean status;

        /**
         * 发送次数
         */
        private int times;

        public WarnInfo() {
            this.times = 0;
            this.status = false;
            this.firstOfMs = System.currentTimeMillis();
        }

        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.times = 0;
            this.status = status;
        }

        public boolean isFirst() {
            return this.times == 0;
        }

        /**
         * 是否超出频率范围
         * @param frequency
         * @return
         */
        public boolean isExpire(Integer frequency) {
            if(frequency == null || frequency <= 0) {
                return true; // 小于等于0的值代表实时告警
            }

            return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - firstOfMs) > frequency;
        }

        /**
         * 触发告警信息
         * @return
         */
        public void trigger() {
            this.firstOfMs = System.currentTimeMillis();
            this.times ++;
        }
    }
}
