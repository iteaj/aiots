package com.iteaj.iboot.module.iot.dto;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.spi.iot.consts.AttrType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class BatchModelDto {

    /**
     * 模型属性id
     */
    private Long id;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 属性代码
     */
    private String field;

    /**
     * 属性名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 实际类型
     */
    private String realType;

    /**
     * 读写方式
     */
    private AttrType attrType;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 单位
     */
    private String unit;

    /**
     * 精度
     */
    private Integer accuracy;

    /**
     * 数据增益
     */
    private Integer gain;

    /**
     * 数据解析器
     */
    private String resolver;

    private String remark;

    /**
     * 协议指令
     */
    private String direct;

    /**
     * 接口名
     */
    private String apiName;

    /**
     * 接口代码
     */
    private String apiCode;

    /**
     * 产品码
     */
    private String productCode;

    /**
     * 频率标签（如：默认、低频、中频、高频）
     */
    private String collectTag;

    @Transient
    private String apiDownConfig;

    private Map<String, Object> config;

    public void setApiDownConfig(String apiDownConfig) {
        if(StrUtil.isNotBlank(apiDownConfig)) {
            config = new HashMap<>();
            if(apiDownConfig.contains("->")) {
                final String[] split = apiDownConfig.split(":::");
                for (String s : split) {
                    final String[] item = s.split("->");
                    if(item.length == 2) {
                        config.put(item[0], item[1]);
                    }
                }
            }
        }
    }
}
