package com.iteaj.iboot.plugin.ai.dto;

import com.iteaj.iboot.plugin.ai.entity.AiTrainMetric;
import com.iteaj.iboot.plugin.ai.entity.AiTrainTask;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * AI训练任务 DTO数据模型
 *
 * 【表关联】
 * - 多对一：ai_train_task → ai_model (model_id)，携带模型名称
 * - 一对多：ai_train_task → ai_train_metric (task_id)，携带指标列表（仅详情）
 *
 * @author aiots-dev
 */
@Getter
@Setter
@Accessors(chain = true)
public class AiTrainTaskDto extends AiTrainTask {

    /** 预训练模型名称（多对一关联 ai_model） */
    private String modelName;

    /** 训练指标列表（一对多，仅详情接口返回） */
    private List<AiTrainMetric> metrics;
}
