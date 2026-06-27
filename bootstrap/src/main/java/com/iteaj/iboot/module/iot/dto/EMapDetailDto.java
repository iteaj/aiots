package com.iteaj.iboot.module.iot.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.iboot.module.iot.consts.WarnGrade;
import com.iteaj.iboot.module.iot.entity.ModelApi;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class EMapDetailDto {

    private DeviceDto device;

    /**
     * 模型属性列表
     */
    private List<ModelAttr> attrs = new ArrayList<>();

    /**
     * 告警记录
     */
    private List<Warn> warns = new ArrayList<>();

    /**
     * 接口调试参数
     */
    private Map<String, List<IVOption>> apiCodeDebugMap;

    public EMapDetailDto addAttr(String field, String name, String unit) {
        return addAttr(field, name, unit, null, null, CollectStatus.Success);
    }

    public EMapDetailDto addAttr(String field, String name, String unit, Object value, Date collectTime, CollectStatus status) {
        attrs.add(new ModelAttr(field, name, unit, value, collectTime, status));
        return this;
    }

    public EMapDetailDto addWarn(String field, WarnGrade grade, String content, Date warnTime) {
        this.warns.add(new Warn(field, grade.getDesc(), content, warnTime)); return this;
    }

    @Getter
    @Setter
    public class ModelAttr {

        /**
         * 属性代码
         */
        private String field;

        /**
         * 属性名称
         */
        private String name;

        /**
         * 单位
         */
        private String unit;

        /**
         * 实时值
         */
        private Object value;

        /**
         * 采集时间
         */
        @JSONField(format = "yyyy-MM-dd HH:mm:ss")
        private Date collectTime;

        private CollectStatus status;

        public ModelAttr(String field, String name, String unit, Object value
                , Date collectTime, CollectStatus status) {
            this.field = field;
            this.name = name;
            this.unit = unit;
            this.value = value;
            this.status = status;
            this.collectTime = collectTime;
        }
    }

    public void addApiDebugParam(String apiCode, List<IVOption> params) {
        if(apiCodeDebugMap == null) {
            apiCodeDebugMap = new HashMap<>();
        }

        apiCodeDebugMap.put(apiCode, params);
    }

    @Getter
    @Setter
    public class Warn {

        private String field;

        /**
         * 告警等级
         */
        private String grade;

        /**
         * 告警内容
         */
        private String content;

        @JSONField(format = "yy-MM-dd HH:mm:ss")
        private Date warnTime;

        public Warn(String field, String grade, String content, Date warnTime) {
            this.grade = grade;
            this.field = field;
            this.content = content;
            this.warnTime = warnTime;
        }
    }
}
