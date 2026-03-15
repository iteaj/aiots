package com.iteaj.iboot.module.iot.entity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.iteaj.framework.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.*;
import java.util.stream.Collectors;

import com.iteaj.framework.IVOption;
import com.iteaj.framework.exception.ServiceException;
import com.iteaj.framework.mybatis.handler.FastjsonTypeHandler;
import com.iteaj.framework.spi.iot.consts.TriggerMode;
import com.iteaj.framework.spi.iot.consts.FuncType;
import com.iteaj.framework.spi.iot.data.ModelApiMeta;
import com.iteaj.framework.spi.iot.data.ModelAttrMeta;
import com.iteaj.framework.spi.iot.protocol.ProtocolApiType;
import com.iteaj.iboot.module.iot.dto.DeviceCommandDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.iteaj.iboot.module.iot.dto.DeviceCommandViewDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.util.CollectionUtils;


/**
 * <p>
 * 物模型接口
 * </p>
 *
 * @author iteaj
 * @since 2023-09-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("iot_model_api")
public class ModelApi extends BaseEntity implements ModelApiMeta {

    private static final long serialVersionUID = 1L;

    /**
     * 接口类型
     */
    @NotNull(message = "接口类型必填")
    private ProtocolApiType type;

    /**
     * 协议指令
     */
    private String direct;

    /**
     * 功能代码
     */
    @NotBlank(message = "接口代码必填")
    @TableField(condition = SqlCondition.LIKE)
    private String code;

    /**
     * 显示到调试
     */
    private Boolean debug;

    /**
     * 是否为协议接口
     */
    private Boolean protocol;

    /**
     * 所属产品
     */
    @NotNull(message = "所属产品必填")
    private Long productId;

    /**
     * 接口名称
     */
    @TableField(condition = SqlCondition.LIKE)
    private String name;

    /**
     * 接口状态
     */
    private String status;

    /**
     * 接口说明
     */
    private String remark;

    /**
     * 作为状态位(用于点位控制协议)
     */
    private Boolean asStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 功能类型
     */
    private FuncType funcType;

    @TableField(exist = false)
    private String productCode;

    /**
     * 事件触发方式
     */
    private TriggerMode triggerMode;

    /**
     * 指令参数
     */
    @TableField(typeHandler = FastjsonTypeHandler.class)
    private JSONObject directParams;

    /**
     * 上行接口配置
     */
    @TableField(exist = false)
    private List<ModelApiConfig> upConfig;

    /**
     * 下行接口配置
     */
    @TableField(exist = false)
    private List<ModelApiConfig> downConfig;

    public JSONObject resolveStrValue(String value) {
        Map<String, String> attrValueMap = new HashMap<>();
        if(StrUtil.isNotBlank(value) && value.contains("::")) {
            String[] split = value.split(",");
            for (int i = 0; i < split.length; i++) {
                String[] fieldValueMap = split[i].split("::");
                if(fieldValueMap.length == 2) {
                    attrValueMap.put(fieldValueMap[0], fieldValueMap[1]);
                }
            }
        }

        JSONObject param = new JSONObject();
        if(CollectionUtil.isNotEmpty(downConfig)) {
            downConfig.forEach(config -> {
                String configValue = config.getValue();
                if(config.getModelAttrId() != null && configValue.startsWith("@")) { // 对提及的物模型属性赋值
                    String attrField = configValue.substring(1);
                    configValue = attrValueMap.get(attrField);
                }

                param.put(config.getProtocolAttrField(), configValue);
            });
        }
        return param;
    }

    /**
     * 解析接口字典配置
     * @return
     */
    public List<IVOption> resolveDownApiConfig(List<ModelAttr> attrs) {
        if(!CollectionUtils.isEmpty(downConfig)) {
            List<ModelApiConfig> configs = downConfig.stream()
                    .filter(item -> !CollectionUtils.isEmpty(item.getDicts()))
                    .collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(configs)) {
                if(CollectionUtils.isEmpty(attrs)) {
                    throw new ServiceException("参数错误[物模型属性为空]");
                }

                Map<Long, ModelAttr> attrMap = attrs.stream().collect(Collectors.toMap(ModelAttr::getId, item -> item));
                List<List<ModelAttrDict>> collect = configs.stream()
                        .map(item -> item.getDicts())
                        .collect(Collectors.toList());

                // 计算笛卡尔积
                List<IVOption> options = cartesianProduct(collect, attrMap).stream().map(item -> {
                    StringBuilder sb = new StringBuilder();
                    StringBuilder valueSb = new StringBuilder();
                    item.forEach(dict -> {
                        ModelAttr modelAttr = attrMap.get(dict.getModelAttrId());
                        if(modelAttr != null) {
                            sb.append(modelAttr.getName()).append("->").append(dict.getDictName()).append(", ");
                            valueSb.append(modelAttr.getField()).append("::").append(dict.getDictValue()).append(",");
                        }
                    });

                    sb.deleteCharAt(sb.length() - 2);
                    valueSb.deleteCharAt(valueSb.length() - 1);

                    return new IVOption(sb.toString(), valueSb.toString());
                }).collect(Collectors.toList());

                return options;
            }
        }

        return Collections.EMPTY_LIST;
    }

    public List<DeviceCommandDto> resolverCommand(String uid, List<? extends ModelAttrMeta> attrs) {
        if(!CollectionUtils.isEmpty(downConfig)) {
            List<ModelApiConfig> configs = downConfig.stream()
                    .filter(item -> !CollectionUtils.isEmpty(item.getDicts()))
                    .collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(configs)) {
                if(CollectionUtils.isEmpty(attrs)) {
                    throw new ServiceException("参数错误[物模型属性为空]");
                }

                Map<Long, ? extends ModelAttrMeta> attrMap = attrs.stream().collect(Collectors.toMap(ModelAttrMeta::getId, item -> item));
                List<List<ModelAttrDict>> collect = configs.stream()
                        .map(ModelApiConfig::getDicts)
                        .collect(Collectors.toList());

                StringBuilder configValue = new StringBuilder(), configName = new StringBuilder();
                downConfig.stream().filter(item -> item.getModelAttrId() == null).forEach(item -> {
                    configName.append(item.getProtocolAttrName()).append("->").append(item.getValue()).append(';');
                    configValue.append(item.getProtocolAttrField()).append("->").append(item.getValue()).append('&');
                });

                // 计算笛卡尔积
                return cartesianProduct(collect, attrMap).stream().map(item -> {
                    StringBuilder sb = new StringBuilder(configName.toString());
                    StringBuilder valueSb = new StringBuilder(configValue.toString());
                    item.forEach(dict -> {
                        ModelAttrMeta modelAttr = attrMap.get(dict.getModelAttrId());
                        if(modelAttr != null) {
                            sb.append(modelAttr.getName()).append("->").append(dict.getDictName()).append(";");
                            downConfig.forEach(config -> {
                                if(config.getModelAttrId() != null && config.getModelAttrId().equals(modelAttr.getId())) {
                                    valueSb.append(config.getProtocolAttrField()).append("->").append(dict.getDictValue()).append("&");
                                }
                            });
                        }
                    });

                    sb.deleteCharAt(sb.length() - 1);
                    valueSb.deleteCharAt(valueSb.length() - 1);

                    String dict = item.isEmpty() ? null : item.get(0).getDictName();
                    String apiName = dict == null ? getName() : getName() + "->" + dict;
                    return new DeviceCommandDto().setApiName(apiName).setApiType(getType()).setUid(uid)
                            .setCommandValue(valueSb.toString()).setRemark(getRemark()).setCommandName(sb.toString()).setCode(getCode());
                }).collect(Collectors.toList());
            } else {
                StringBuilder sb = new StringBuilder();
                StringBuilder valueSb = new StringBuilder();
                downConfig.forEach(config -> {
                    sb.append(config.getProtocolAttrName()).append("->").append(config.getValue()).append(';');
                    valueSb.append(config.getProtocolAttrField()).append("->").append(config.getValue()).append('&');
                });

                sb.deleteCharAt(sb.length() - 1);
                valueSb.deleteCharAt(valueSb.length() - 1);

                return Arrays.asList(new DeviceCommandDto().setApiName(getName()).setApiType(getType()).setUid(uid)
                        .setCommandValue(valueSb.toString()).setCommandName(sb.toString()).setRemark(getRemark()).setCode(getCode()));
            }
        } else {
            return Arrays.asList(new DeviceCommandDto().setApiName(getName()).setApiType(getType()).setUid(uid)
                    .setCommandValue(null).setCommandName("-").setRemark(getRemark()).setCode(getCode()));
        }
    }

    public DeviceCommandViewDto resolverCommandOfView(List<ModelAttr> attrs) {
        if(!CollectionUtils.isEmpty(downConfig)) {
            List<ModelApiConfig> configs = downConfig.stream()
                    .filter(item -> !CollectionUtils.isEmpty(item.getDicts()))
                    .collect(Collectors.toList());

            if(!CollectionUtils.isEmpty(configs)) {
                if(CollectionUtils.isEmpty(attrs)) {
                    throw new ServiceException("参数错误[物模型属性为空]");
                }

                final ModelApiConfig apiConfig = configs.get(0);
                final List<ModelAttrDict> dicts = apiConfig.getDicts();
                final List<DeviceCommandViewDto.ViewConfig> dictList = dicts.stream()
                        .map(item -> new DeviceCommandViewDto.ViewConfig(item.getDictName(), item.getDictValue()))
                        .collect(Collectors.toList());
                StringBuilder configValue = new StringBuilder();

                configValue.append(apiConfig.getProtocolAttrField()).append("->#{}");
                return new DeviceCommandViewDto(getName(), configValue.toString(), getCode()).setLeaf(true).setRemark(getRemark()).setConfigs(dictList);
            }
        }

        return new DeviceCommandViewDto(getName(), null, getCode()).setLeaf(true).setRemark(getRemark());
    }

    private List<List<ModelAttrDict>> cartesianProduct(List<List<ModelAttrDict>> lists, Map<Long, ? extends ModelAttrMeta> attrMap) {
        List<List<ModelAttrDict>> result = new ArrayList<>();
        cartesianProductHelper(lists, 0, new ArrayList<>(), result, attrMap);
        return result;
    }

    private void cartesianProductHelper(List<List<ModelAttrDict>> lists, int index, List<ModelAttrDict> current
            , List<List<ModelAttrDict>> result, Map<Long, ? extends ModelAttrMeta> attrMap) {
        if (index == lists.size()) {
            result.add(new ArrayList<>(current));
            return;
        }

        List<ModelAttrDict> list = lists.get(index);
        for (ModelAttrDict value : list) {
            ModelAttrMeta modelAttr = attrMap.get(value.getModelAttrId());
            if(modelAttr != null) {
                current.add(value);
            }

            cartesianProductHelper(lists, index + 1, current, result, attrMap);
            current.remove(current.size() - 1);
        }
    }
}
