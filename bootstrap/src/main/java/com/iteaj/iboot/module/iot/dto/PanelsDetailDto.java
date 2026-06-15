package com.iteaj.iboot.module.iot.dto;

import com.alibaba.fastjson2.annotation.JSONField;
import com.iteaj.framework.IVOption;
import com.iteaj.framework.spi.iot.consts.AttrType;
import com.iteaj.framework.spi.iot.consts.CollectStatus;
import com.iteaj.iboot.module.iot.consts.WarnGrade;
import com.iteaj.iboot.module.iot.entity.Device;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class PanelsDetailDto {

    /**
     * 设备信息
     */
    private Device device;

    /**
     * 产品信息
     */
    private ProductDto product;

    /**
     * 模型属性列表
     */
    private List<ModelAttrContext.ModelAttrValue> attrs = new ArrayList<>();

    /**
     * 值到标签映射
     */
    private Map<String, String> valueLabels = new HashMap<>();

    /**
     * 告警记录
     */
    private List<Warn> warns = new ArrayList<>();

    /**
     * 接口调试参数
     */
    private Map<String, List<IVOption>> apiCodeDebugMap;

    public PanelsDetailDto addAttr(ModelAttrContext.ModelAttrValue attr) {
        attrs.add(attr);
        return this;
    }

    public PanelsDetailDto addWarn(String field, String name, WarnGrade grade, String content, Date warnTime) {
        this.warns.add(new Warn(field, name, grade.getDesc(), content, warnTime)); return this;
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

        /**
         *告警字段
         */
        private String field;

        @JSONField(format = "yy-MM-dd HH:mm:ss")
        private Date warnTime;

        public Warn(String field, String name, String grade, String content, Date warnTime) {
            this.name = name;
            this.field = field;
            this.grade = grade;
            this.content = content;
            this.warnTime = warnTime;
        }
    }
}
