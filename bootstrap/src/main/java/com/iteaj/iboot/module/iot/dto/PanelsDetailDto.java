package com.iteaj.iboot.module.iot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.spi.iot.consts.AttrType;
import com.iteaj.iboot.module.iot.consts.WarnGrade;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class PanelsDetailDto {

    /**
     * 设备信息
     */
    private DeviceDto device;

    /**
     * 产品信息
     */
    private ProductDto product;

    /**
     * 模型属性列表
     */
    private List<ModelAttr> attrs = new ArrayList<>();

    /**
     * 点位信息
     */
    private List<Signal> signals = new ArrayList<>();

    /**
     * 告警记录
     */
    private List<Warn> warns = new ArrayList<>();

    /**
     * 接口调试参数
     */
    private Map<String, List<IVOption>> apiCodeDebugMap;

    public PanelsDetailDto addAttr(String field, String name, String unit, AttrType type) {
        return addAttr(field, name, unit, type, null, null);
    }

    public PanelsDetailDto addAttr(String field, String name, String unit, AttrType type, Object value, Date collectTime) {
        attrs.add(new ModelAttr(field, name, unit, value, type, collectTime));
        return this;
    }

    public PanelsDetailDto addSignal(String name, String address) {
        this.signals.add(new Signal(name, "-", address, null)); return this;
    }

    public PanelsDetailDto addSignal(String name, String address, Object value, Date collectTime) {
        this.signals.add(new Signal(name, value, address, collectTime)); return this;
    }

    public PanelsDetailDto addWarn(String name, WarnGrade grade, String content, Date warnTime) {
        this.warns.add(new Warn(name, grade.getDesc(), content, warnTime)); return this;
    }

    @Getter
    @Setter
    public class Signal {
        /**
         * 点位名称
         */
        private String name;
        /**
         * 实时值
         */
        private Object value;
        /**
         * 地位地址
         */
        private String address;
        /**
         * 采集时间
         */
        @JsonFormat(pattern = "MM-dd HH:mm:ss")
        private Date collectTime;

        public Signal(String name, Object value, String address, Date collectTime) {
            this.name = name;
            this.value = value;
            this.address = address;
            this.collectTime = collectTime;
        }
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
         * 读写方式
         */
        private AttrType attrType;

        /**
         * 采集时间
         */
        @JsonFormat(pattern = "MM-dd HH:mm:ss")
        private Date collectTime;

        public ModelAttr(String field, String name, String unit, Object value, AttrType attrType, Date collectTime) {
            this.field = field;
            this.name = name;
            this.unit = unit;
            this.value = value;
            this.attrType = attrType;
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

        /**
         * 告警名称
         */
        private String name;

        /**
         * 告警等级
         */
        private String grade;

        /**
         * 告警内容
         */
        private String content;

        @JsonFormat(pattern = "yy-MM-dd HH:mm:ss")
        private Date warnTime;

        public Warn(String name, String grade, String content, Date warnTime) {
            this.name = name;
            this.grade = grade;
            this.content = content;
            this.warnTime = warnTime;
        }
    }
}
