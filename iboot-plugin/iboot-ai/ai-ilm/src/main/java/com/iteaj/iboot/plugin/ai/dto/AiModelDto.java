package com.iteaj.iboot.plugin.ai.dto;

import com.iteaj.iboot.plugin.ai.entity.AiModel;
import com.iteaj.iboot.plugin.ai.entity.AiModelParam;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * AI模型 DTO数据模型
 *
 * 【表关联】
 * - 一对多：ai_model → ai_model_param (model_id)
 *
 * @author aiots-dev
 */
@Getter
@Setter
@Accessors(chain = true)
public class AiModelDto extends AiModel {

    /** 模型参数列表（一对多） */
    private List<AiModelParam> params;
}
